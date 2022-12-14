package cloud.stackexplode.gulimall.ware.service.impl;

import cloud.stackexplode.gulimall.common.entities.ware.entity.WareOrderTaskDetailEntity;
import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.ware.dao.WareOrderTaskDetailDao;
import cloud.stackexplode.gulimall.ware.service.WareOrderTaskDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl
        extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity>
        implements WareOrderTaskDetailService {
    @Override
    public List<WareOrderTaskDetailEntity> getWareOrderTaskDetailByTaskId(Long id) {
        return this.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq(id > 0, "task_id", id));
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page =
                this.page(
                        new Query<WareOrderTaskDetailEntity>().getPage(params),
                        new QueryWrapper<WareOrderTaskDetailEntity>());

        return new PageUtils(page);
    }
}
