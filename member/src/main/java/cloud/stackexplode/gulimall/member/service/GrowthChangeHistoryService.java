package cloud.stackexplode.gulimall.member.service;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.entities.member.entity.GrowthChangeHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author wangudiercai
 * @email 2653084650@qq.com
 * @date 2022-07-12 20:32:53
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
