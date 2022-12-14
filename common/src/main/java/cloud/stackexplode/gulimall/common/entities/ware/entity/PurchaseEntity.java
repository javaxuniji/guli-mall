package cloud.stackexplode.gulimall.common.entities.ware.entity;

import cloud.stackexplode.gulimall.common.constant.WareConstant;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购信息
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-12 20:57:36
 */
@Data
@Accessors(fluent = false, chain = true)
@TableName("wms_purchase")
public class PurchaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /** */
  @TableId(type = IdType.AUTO)
  private Long id;
  /** */
  private Long assigneeId;
  /** */
  private String assigneeName;
  /** */
  private String phone;
  /** */
  private Integer priority;
  /** */
  private WareConstant.PurchaseStatusEnum status;
  /** */
  private Long wareId;
  /** */
  private BigDecimal amount;
  /** */
  private Date createTime;
  /** */
  private Date updateTime;
}
