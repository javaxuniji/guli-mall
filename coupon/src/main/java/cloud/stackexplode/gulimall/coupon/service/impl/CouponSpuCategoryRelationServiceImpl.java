package cloud.stackexplode.gulimall.coupon.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.coupon.dao.CouponSpuCategoryRelationDao;
import cloud.stackexplode.gulimall.common.entities.coupon.entity.CouponSpuCategoryRelationEntity;
import cloud.stackexplode.gulimall.coupon.service.CouponSpuCategoryRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("couponSpuCategoryRelationService")
public class CouponSpuCategoryRelationServiceImpl
        extends ServiceImpl<CouponSpuCategoryRelationDao, CouponSpuCategoryRelationEntity>
        implements CouponSpuCategoryRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponSpuCategoryRelationEntity> page =
                this.page(
                        new Query<CouponSpuCategoryRelationEntity>().getPage(params),
                        new QueryWrapper<CouponSpuCategoryRelationEntity>());

        return new PageUtils(page);
    }
}
