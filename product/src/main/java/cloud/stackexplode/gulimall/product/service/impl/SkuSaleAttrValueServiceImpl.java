package cloud.stackexplode.gulimall.product.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.product.dao.SkuSaleAttrValueDao;
import cloud.stackexplode.gulimall.common.entities.product.entity.SkuSaleAttrValueEntity;
import cloud.stackexplode.gulimall.product.service.SkuSaleAttrValueService;
import cloud.stackexplode.gulimall.common.vo.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl
        extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity>
        implements SkuSaleAttrValueService {
    @Override
    public List<String> getSkuSaleAttrValuesAsString(Long skuId) {
        return baseMapper.getSkuSaleAttrValuesAsString(skuId);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page =
                this.page(
                        new Query<SkuSaleAttrValueEntity>().getPage(params),
                        new QueryWrapper<>());

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> listSaleAttrs(Long spuId) {
        return baseMapper.listSaleAttrs(spuId);
    }
}
