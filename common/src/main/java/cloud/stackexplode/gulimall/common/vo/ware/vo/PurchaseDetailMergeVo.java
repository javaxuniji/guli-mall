package cloud.stackexplode.gulimall.common.vo.ware.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PurchaseDetailMergeVo {

  private List<Long> purchaseDetailIds;

  private Long purchaseId;
}
