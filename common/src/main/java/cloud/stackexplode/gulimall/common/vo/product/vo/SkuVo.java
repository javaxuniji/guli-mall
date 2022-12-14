package cloud.stackexplode.gulimall.common.vo.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * The type Sku vo.
 */
@Data
@Accessors(chain = true)
public class SkuVo {

  private List<SkuAttrVo> attr;

  private Long countStatus;

  private List<String> descar;

  private BigDecimal discount;

  private BigDecimal fullCount;

  private BigDecimal fullPrice;

  private List<ImageVo> images;

  private List<MemberPriceVo> memberPrice;

  private BigDecimal price;

  private Long priceStatus;

  private BigDecimal reducePrice;

  private String skuName;

  private String skuSubtitle;

  private String skuTitle;
}
