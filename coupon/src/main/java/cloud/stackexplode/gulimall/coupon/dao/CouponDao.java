package cloud.stackexplode.gulimall.coupon.dao;

import cloud.stackexplode.gulimall.common.entities.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-10 21:38:58
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
}
