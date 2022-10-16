package cloud.stackexplode.gulimall.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParamVo {

  /** 页面传递过来的全文匹配关键字 */
  private String keyword;

  /** 品牌id,可以多选 */
  private List<Long> brandId;

  /** 三级分类id */
  private Long catelog3Id;

  /** 排序条件：sort=price/salecount/hotscore_desc/asc */
  private String sort;

  /** 是否显示有货 */
  private Boolean hasStock;

  /** 价格区间查询 */
  private String skuPrice;

  /** 按照属性进行筛选 */
  private List<String> attrsString;

  /** 页码 */
  private Integer pageNum = 1;

  /** 原生的所有查询条件 */
  private String _queryString;
}
