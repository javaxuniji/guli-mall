package cloud.stackexplode.gulimall.order.service.impl;

import cloud.stackexplode.gulimall.common.components.IdWorker;
import cloud.stackexplode.gulimall.common.constant.order.constant.OrderConstant;
import cloud.stackexplode.gulimall.common.entities.order.entity.OrderEntity;
import cloud.stackexplode.gulimall.common.entities.order.entity.OrderItemEntity;
import cloud.stackexplode.gulimall.common.enums.order.ConfirmStatus;
import cloud.stackexplode.gulimall.common.enums.order.OrderStatus;
import cloud.stackexplode.gulimall.common.to.mq.SeckillOrderTo;
import cloud.stackexplode.gulimall.common.to.order.OrderCreateTo;
import cloud.stackexplode.gulimall.common.to.product.SkuHasStockVo;
import cloud.stackexplode.gulimall.common.to.product.SpuInfoTo;
import cloud.stackexplode.gulimall.common.to.session.MemberSessionTo;
import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.common.utils.R;
import cloud.stackexplode.gulimall.common.utils.StatusCode;
import cloud.stackexplode.gulimall.common.vo.order.vo.*;
import cloud.stackexplode.gulimall.order.dao.OrderDao;
import cloud.stackexplode.gulimall.order.event.OrderCreateEvent;
import cloud.stackexplode.gulimall.order.feign.CartFeignService;
import cloud.stackexplode.gulimall.order.feign.MemberFeignService;
import cloud.stackexplode.gulimall.order.feign.ProductFeignService;
import cloud.stackexplode.gulimall.order.feign.WareFeignService;
import cloud.stackexplode.gulimall.order.interceptor.LoginInterceptor;
import cloud.stackexplode.gulimall.order.service.OrderItemService;
import cloud.stackexplode.gulimall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public OrderEntity getOrderDetailsByOrderSn(Long orderSn) {
        CompletableFuture<OrderEntity> orderEntityFuture = CompletableFuture
                .supplyAsync(() -> this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn)), executor);
        CompletableFuture<List<OrderItemEntity>> orderItemsFuture = CompletableFuture
                .supplyAsync(() -> orderItemService.list(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, orderSn)), executor);
        CompletableFuture.allOf(orderEntityFuture, orderItemsFuture).join();
        OrderEntity orderEntity = orderEntityFuture.join();
        List<OrderItemEntity> orderItemEntities = orderItemsFuture.join();
        orderEntity.setItems(orderItemEntities);
        return orderEntity;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page =
                this.page(new Query<OrderEntity>().getPage(params), new QueryWrapper<OrderEntity>());

        return new PageUtils(page);
    }


    @Override
    public OrderConfirmVo confirmOrder() {
        MemberSessionTo memberSessionTo = LoginInterceptor.loginUser.get();
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> itemAndStockFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1. ???????????????????????????
            List<OrderItemVo> checkedItems = cartFeignService.getCheckedItems().getData();
            confirmVo.setItems(checkedItems);
            return checkedItems;
        }, executor).thenAcceptAsync((items) -> {
            //4. ??????
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            Map<Long, Boolean> hasStockMap = wareFeignService.getSkuHasStocks(skuIds).getData().stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
            confirmVo.setStocks(hasStockMap);
        }, executor);

        //2. ????????????????????????
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            R<List<MemberAddressVo>> r = memberFeignService.getAddressByUserId(memberSessionTo.getId());
            List<MemberAddressVo> address = r.getData();
            confirmVo.setMemberAddressVos(address);
        }, executor);

        //3. ??????
        confirmVo.setIntegration(memberSessionTo.getIntegration());

        //5. ??????????????????
        //6. ????????????
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberSessionTo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        try {
            CompletableFuture.allOf(itemAndStockFuture, addressFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return confirmVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);
        MemberSessionTo memberResponseVo = LoginInterceptor.loginUser.get();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId()), submitVo.getOrderToken());
        if (execute == 0L) {
            //1.1 ????????????????????????
            responseVo.setCode(1);
            return responseVo;
        } else {
            //2. ????????????????????????
            OrderCreateTo order = createOrderTo(memberResponseVo, submitVo);
            //3. ??????
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = submitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //4. ????????????
                saveOrder(order);
                //5. ????????????
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    return orderItemVo;
                }).collect(Collectors.toList());
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                lockVo.setLocks(orderItemVos);
                R r = wareFeignService.orderLockStock(lockVo);
                //5.1 ??????????????????
                if (r.getCode().equals(StatusCode.SUCCESS)) {
                    responseVo.setOrder(order.getOrder());
                    responseVo.setCode(0);
                    OrderCreateEvent orderCreateEvent = new OrderCreateEvent()
                            .setOrder(order)
                            .setMemberId(memberResponseVo.getId());
                    publisher.publishEvent(orderCreateEvent);
                    return responseVo;
                } else {
                    //5.1 ??????????????????
                    String msg = r.getMsg();
                    throw new RuntimeException(msg);
                }

            } else {
                //????????????
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(Long orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * ????????????????????????
     *
     * @param orderSn
     */
    @Override
    public void closeOrder(Long orderSn) {
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        OrderEntity order = getOrderByOrderSn(orderSn);
        if (order == null) {
            log.info("??????????????????????????????{}", orderSn);
            return;
        }
        //??????????????????????????????????????????????????????????????????????????????
        OrderEntity updateOrder = new OrderEntity();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderStatus.CANCLED);
        this.updateById(updateOrder);

    }

    @Override
    public PageUtils getMemberOrderPage(Map<String, Object> params) {
        MemberSessionTo memberResponseVo = LoginInterceptor.loginUser.get();
        LambdaQueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<OrderEntity>().lambda().eq(OrderEntity::getMemberId, memberResponseVo.getId()).orderByDesc(OrderEntity::getCreateTime);
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params), queryWrapper
        );
        List<OrderEntity> entities = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItems(orderItemEntities);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(entities);
        return new PageUtils(page);
    }

    @Override
    public PayVo getOrderPay(Long orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        if (ObjectUtils.isNotEmpty(orderEntity)) {
            payVo.setOutTradeNo(orderSn);
            BigDecimal payAmount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
            payVo.setTotalAmount(payAmount.toString());
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
            OrderItemEntity orderItemEntity = orderItemEntities.get(0);
            payVo.setSubject(orderItemEntity.getSkuName());
            payVo.setBody(orderItemEntity.getSkuAttrsVals());
        }

        return payVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        MemberSessionTo memberSessionTo = LoginInterceptor.loginUser.get();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        if (memberSessionTo != null) {
            orderEntity.setMemberUsername(memberSessionTo.getUserName());
        }
        orderEntity.setStatus(OrderStatus.CREATE_NEW);
        orderEntity.setCreateTime(new Date());
        orderEntity.setPayAmount(orderTo.getSeckillPrice().multiply(new BigDecimal(orderTo.getNum())));
        this.save(orderEntity);

        R r = productFeignService.info(orderTo.getSkuId());
        if (r.getCode().equals(StatusCode.SUCCESS)) {
            SeckillSkuInfoVo skuInfo = (SeckillSkuInfoVo) r.getData();
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderSn(orderTo.getOrderSn());
            orderItemEntity.setSpuId(skuInfo.getSpuId());
            orderItemEntity.setCategoryId(skuInfo.getCatalogId());
            orderItemEntity.setSkuId(skuInfo.getSkuId());
            orderItemEntity.setSkuName(skuInfo.getSkuName());
            orderItemEntity.setSkuPic(skuInfo.getSkuDefaultImg());
            orderItemEntity.setSkuPrice(skuInfo.getPrice());
            orderItemEntity.setSkuQuantity(orderTo.getNum());
            orderItemService.save(orderItemEntity);
        }
    }

    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity order = orderCreateTo.getOrder();
        order.setId(idWorker.nextId());
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        if (this.save(order)) {
            List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems().stream().map(orderItem -> {
                orderItem
                        .setOrderSn(order.getOrderSn())
                        .setOrderId(order.getId());
                return orderItem;
            }).collect(Collectors.toList());
            orderItemService.saveBatch(orderItems);
        } else {
            throw new RuntimeException("??????????????????");
        }
    }

    private OrderCreateTo createOrderTo(MemberSessionTo memberSessionTo, OrderSubmitVo submitVo) {
        //???IdWorker???????????????
        Long orderSn = idWorker.nextId();
        //????????????
        OrderEntity order = buildOrder(memberSessionTo, submitVo, orderSn);
        //???????????????
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        //????????????
        compute(order, orderItemEntities);
        OrderCreateTo createTo = new OrderCreateTo();
        createTo.setOrder(order);
        createTo.setOrderItems(orderItemEntities);
        return createTo;
    }

    private void compute(OrderEntity entity, List<OrderItemEntity> orderItemEntities) {
        //??????
        BigDecimal total = BigDecimal.ZERO;
        //????????????
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        //??????
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total = total.add(orderItemEntity.getRealAmount());
            promotion = promotion.add(orderItemEntity.getPromotionAmount());
            integration = integration.add(orderItemEntity.getIntegrationAmount());
            coupon = coupon.add(orderItemEntity.getCouponAmount());
            integrationTotal += orderItemEntity.getGiftIntegration();
            growthTotal += orderItemEntity.getGiftGrowth();
        }

        entity.setTotalAmount(total);
        entity.setPromotionAmount(promotion);
        entity.setIntegrationAmount(integration);
        entity.setCouponAmount(coupon);
        entity.setIntegration(integrationTotal);
        entity.setGrowth(growthTotal);

        //????????????=????????????+??????
        entity.setPayAmount(entity.getFreightAmount().add(total));

        //??????????????????(0-????????????1-?????????)
        entity.setDeleteStatus(0);
    }

    private List<OrderItemEntity> buildOrderItems(Long orderSn) {
        List<OrderItemVo> checkedItems = cartFeignService.getCheckedItems().getData();
        return checkedItems.stream().map((item) -> {
            OrderItemEntity orderItemEntity = buildOrderItem(item);
            //1) ???????????????
            orderItemEntity.setOrderSn(orderSn);
            return orderItemEntity;
        }).collect(Collectors.toList());
    }

    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        Long skuId = item.getSkuId();
        //2) ??????sku????????????
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttrValues(), ";"));
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());
        //3) ??????skuId??????spu?????????????????????
        R<SpuInfoTo> r = productFeignService.getSpuBySkuId(skuId);
        if (r.getCode().equals(StatusCode.SUCCESS)) {
            SpuInfoTo spuInfo = r.getData();
            orderItemEntity.setSpuId(spuInfo.getId());
            orderItemEntity.setSpuName(spuInfo.getSpuName());
            orderItemEntity.setSpuBrand(spuInfo.getBrandName());
            orderItemEntity.setCategoryId(spuInfo.getCatelogId());
        }
        //4) ?????????????????????(??????)

        //5) ?????????????????????????????????x??????
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());

        //6) ???????????????????????????
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //7) ????????????
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);

        return orderItemEntity;
    }

    private OrderEntity buildOrder(MemberSessionTo memberSessionTo, OrderSubmitVo submitVo, Long orderSn) {

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setOrderSn(orderSn);

        //2) ??????????????????
        orderEntity.setMemberId(memberSessionTo.getId());
        orderEntity.setMemberUsername(memberSessionTo.getUserName());
        //3) ???????????????????????????????????????
        FareVo fareVo = wareFeignService.getFare(submitVo.getAddrId(), submitVo.getWareId()).getData();
        try {
            log.info("???????????????{}", new ObjectMapper().writeValueAsString(fareVo));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        BigDecimal fare = fareVo.getFare();
        orderEntity.setFreightAmount(fare);
        MemberAddressVo address = fareVo.getAddress();
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        //4) ?????????????????????????????????
        orderEntity.setStatus(OrderStatus.CREATE_NEW);
        orderEntity.setConfirmStatus(ConfirmStatus.UNCONFIRMED);
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

}
