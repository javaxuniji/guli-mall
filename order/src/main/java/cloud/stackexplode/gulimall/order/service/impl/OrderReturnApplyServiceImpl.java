package cloud.stackexplode.gulimall.order.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.order.dao.OrderReturnApplyDao;
import cloud.stackexplode.gulimall.common.entities.order.entity.OrderReturnApplyEntity;
import cloud.stackexplode.gulimall.order.service.OrderReturnApplyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("orderReturnApplyService")
public class OrderReturnApplyServiceImpl
        extends ServiceImpl<OrderReturnApplyDao, OrderReturnApplyEntity>
        implements OrderReturnApplyService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderReturnApplyEntity> page =
                this.page(
                        new Query<OrderReturnApplyEntity>().getPage(params),
                        new QueryWrapper<OrderReturnApplyEntity>());

        return new PageUtils(page);
    }
}
