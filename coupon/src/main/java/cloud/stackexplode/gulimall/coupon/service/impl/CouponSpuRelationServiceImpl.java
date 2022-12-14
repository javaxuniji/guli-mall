package cloud.stackexplode.gulimall.coupon.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.coupon.dao.CouponSpuRelationDao;
import cloud.stackexplode.gulimall.common.entities.coupon.entity.CouponSpuRelationEntity;
import cloud.stackexplode.gulimall.coupon.service.CouponSpuRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("couponSpuRelationService")
public class CouponSpuRelationServiceImpl
        extends ServiceImpl<CouponSpuRelationDao, CouponSpuRelationEntity>
        implements CouponSpuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponSpuRelationEntity> page =
                this.page(
                        new Query<CouponSpuRelationEntity>().getPage(params),
                        new QueryWrapper<CouponSpuRelationEntity>());

        return new PageUtils(page);
    }
}
