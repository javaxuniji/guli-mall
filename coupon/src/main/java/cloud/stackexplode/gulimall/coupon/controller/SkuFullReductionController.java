package cloud.stackexplode.gulimall.coupon.controller;

import cloud.stackexplode.gulimall.common.to.product.SkuReductionTo;
import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.R;
import cloud.stackexplode.gulimall.common.entities.coupon.entity.SkuFullReductionEntity;
import cloud.stackexplode.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 商品满减信息
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-10 21:38:58
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController {
  @Autowired private SkuFullReductionService skuFullReductionService;

  /** 列表 */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = skuFullReductionService.queryPage(params);

    return R.ok(page);
  }

  /** 信息 */
  @RequestMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

    return R.ok( skuFullReduction);
  }

  /** 保存 */
  @RequestMapping("/save")
  public R save(@RequestBody SkuReductionTo skuReductionTo) {
    Boolean res = skuFullReductionService.saveSkuReductionTo(skuReductionTo);

    return res ? R.ok("保存成功") : R.error("保存失败");
  }
  //    @RequestMapping("/save")
  //    public R save(@RequestBody SkuFullReductionEntity skuFullReduction) {
  //        skuFullReductionService.save(skuFullReduction);
  //
  //        return R.ok();
  //    }
  /** 修改 */
  @RequestMapping("/update")
  public R update(@RequestBody SkuFullReductionEntity skuFullReduction) {
    skuFullReductionService.updateById(skuFullReduction);

    return R.ok();
  }

  /** 删除 */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    skuFullReductionService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }
}
