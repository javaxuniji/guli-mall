package cloud.stackexplode.gulimall.coupon.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.coupon.dao.SeckillSkuRelationDao;
import cloud.stackexplode.gulimall.common.entities.coupon.entity.SeckillSkuRelationEntity;
import cloud.stackexplode.gulimall.coupon.service.SeckillSkuRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl
        extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity>
        implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSkuRelationEntity> page =
                this.page(
                        new Query<SeckillSkuRelationEntity>().getPage(params),
                        new QueryWrapper<SeckillSkuRelationEntity>());

        return new PageUtils(page);
    }
}
