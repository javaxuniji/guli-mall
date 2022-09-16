package cloud.stackexplode.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.stackexplode.gulimall.coupon.entity.SkuLadderEntity;
import cloud.stackexplode.gulimall.coupon.service.SkuLadderService;
import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.R;

/**
 * 商品阶梯价格
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-10 21:38:58
 */
@RestController
@RequestMapping("coupon/skuladder")
public class SkuLadderController {
  @Autowired private SkuLadderService skuLadderService;

  /** 列表 */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = skuLadderService.queryPage(params);

    return R.ok().put("page", page);
  }

  /** 信息 */
  @RequestMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    SkuLadderEntity skuLadder = skuLadderService.getById(id);

    return R.ok().put("skuLadder", skuLadder);
  }

  /** 保存 */
  @RequestMapping("/save")
  public R save(@RequestBody SkuLadderEntity skuLadder) {
    skuLadderService.save(skuLadder);

    return R.ok();
  }

  /** 修改 */
  @RequestMapping("/update")
  public R update(@RequestBody SkuLadderEntity skuLadder) {
    skuLadderService.updateById(skuLadder);

    return R.ok();
  }

  /** 删除 */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    skuLadderService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }
}