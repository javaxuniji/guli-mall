package cloud.stackexplode.gulimall.ware.controller;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.R;
import cloud.stackexplode.gulimall.common.entities.ware.entity.PurchaseEntity;
import cloud.stackexplode.gulimall.ware.service.PurchaseService;
import cloud.stackexplode.gulimall.common.vo.ware.vo.PurchaseDetailMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-12 20:57:36
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
  @Autowired private PurchaseService purchaseService;

  /** 列表 */
  @GetMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = purchaseService.queryPage(params);
    return R.ok(page);
  }

  @GetMapping("/unReceiveList")
  public R listUnReceive(@RequestParam Map<String, Object> params) {
    PageUtils page = purchaseService.queryPage(params);

    return R.ok(page);
  }

  /** 信息 */
  @GetMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    PurchaseEntity purchase = purchaseService.getById(id);

    return R.ok(purchase);
  }

  /** 保存 */
  @PostMapping("/save")
  public R save(@RequestBody PurchaseEntity purchase) {
    purchaseService.save(purchase);

    return R.ok();
  }

  /** 修改 */
  @PutMapping("/update")
  public R update(@RequestBody PurchaseEntity purchase) {
    purchaseService.updateById(purchase);

    return R.ok();
  }

  @PutMapping("/merge")
  public R mergePurchase(@RequestBody PurchaseDetailMergeVo purchase) {
    purchaseService.mergeDetails(purchase);

    return R.ok();
  }
  /** 删除 */
  @DeleteMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    purchaseService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }
}
