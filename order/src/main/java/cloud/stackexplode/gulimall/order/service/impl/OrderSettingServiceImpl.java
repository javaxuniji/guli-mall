package cloud.stackexplode.gulimall.order.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.order.dao.OrderSettingDao;
import cloud.stackexplode.gulimall.common.entities.order.entity.OrderSettingEntity;
import cloud.stackexplode.gulimall.order.service.OrderSettingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("orderSettingService")
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingDao, OrderSettingEntity>
        implements OrderSettingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderSettingEntity> page =
                this.page(
                        new Query<OrderSettingEntity>().getPage(params),
                        new QueryWrapper<OrderSettingEntity>());

        return new PageUtils(page);
    }
}
