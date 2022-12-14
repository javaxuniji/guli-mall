package cloud.stackexplode.gulimall.common.entities.ware.entity;

import cloud.stackexplode.gulimall.common.constant.WareConstant;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-12 20:57:36
 */
@Data
@Accessors(fluent = false, chain = true)
@TableName("wms_purchase_detail")
public class PurchaseDetailEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /** */
  @TableId(type = IdType.AUTO)
  private Long id;
  /** 采购单id */
  private Long purchaseId;
  /** 采购商品id */
  private Long skuId;
  /** 采购数量 */
  private Integer skuNum;
  /** 采购金额 */
  private BigDecimal skuPrice;
  /** 仓库id */
  private Long wareId;
  /** 状态[0新建，1已分配，2正在采购，3已完成，4采购失败] */
  private WareConstant.PurchaseDetailStatusEnum status;
}
