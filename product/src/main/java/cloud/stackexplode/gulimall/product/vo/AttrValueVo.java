package cloud.stackexplode.gulimall.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AttrValueVo {
  private Long attrId;
  private String attrName;
  private String attrValue;
}
