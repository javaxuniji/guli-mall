package cloud.stackexplode.gulimall.product.controller;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.R;
import cloud.stackexplode.gulimall.common.entities.product.entity.AttrGroupEntity;
import cloud.stackexplode.gulimall.product.service.AttrGroupService;
import cloud.stackexplode.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-10 18:27:08
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
  @Autowired private AttrGroupService attrGroupService;
  @Autowired private CategoryService categoryService;

  /** 列表 */
  @GetMapping("/list/{catelogId}")
  public R list(
      @RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogid) {
    PageUtils page = attrGroupService.queryPage(params, catelogid);

    return R.ok(page);
  }

  @GetMapping("/list/{catelogId}/withAttrs")
  public R listWithAttrs(
      @RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogid) {
    PageUtils page = attrGroupService.queryPageWithAllAttrs(params, catelogid);

    return R.ok(page);
  }
  /** 信息 */
  @GetMapping("/info/{attrGroupId}")
  public R info(@PathVariable("attrGroupId") Long attrGroupId) {
    AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
    Long[] path = categoryService.findCatelogPath(attrGroup.getCatelogId());
    attrGroup.setCatelogPath(path);
    return R.ok( attrGroup);
  }

  /** 保存 */
  @PostMapping("/save")
  public R save(@RequestBody AttrGroupEntity attrGroup) {
    attrGroupService.save(attrGroup);

    return R.ok();
  }

  /** 修改 */
  @PutMapping("/update")
  public R update(@RequestBody AttrGroupEntity attrGroup) {
    attrGroupService.updateById(attrGroup);

    return R.ok();
  }

  /** 删除 */
  @DeleteMapping("/delete")
  public R delete(@RequestBody Long[] attrGroupIds) {
    attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

    return R.ok();
  }
}
