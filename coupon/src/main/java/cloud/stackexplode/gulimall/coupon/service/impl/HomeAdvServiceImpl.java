package cloud.stackexplode.gulimall.coupon.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.coupon.dao.HomeAdvDao;
import cloud.stackexplode.gulimall.common.entities.coupon.entity.HomeAdvEntity;
import cloud.stackexplode.gulimall.coupon.service.HomeAdvService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("homeAdvService")
public class HomeAdvServiceImpl extends ServiceImpl<HomeAdvDao, HomeAdvEntity>
        implements HomeAdvService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HomeAdvEntity> page =
                this.page(new Query<HomeAdvEntity>().getPage(params), new QueryWrapper<HomeAdvEntity>());

        return new PageUtils(page);
    }
}
