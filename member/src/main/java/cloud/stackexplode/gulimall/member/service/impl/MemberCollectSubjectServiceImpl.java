package cloud.stackexplode.gulimall.member.service.impl;

import cloud.stackexplode.gulimall.common.utils.PageUtils;
import cloud.stackexplode.gulimall.common.utils.Query;
import cloud.stackexplode.gulimall.member.dao.MemberCollectSubjectDao;
import cloud.stackexplode.gulimall.common.entities.member.entity.MemberCollectSubjectEntity;
import cloud.stackexplode.gulimall.member.service.MemberCollectSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("memberCollectSubjectService")
public class MemberCollectSubjectServiceImpl
        extends ServiceImpl<MemberCollectSubjectDao, MemberCollectSubjectEntity>
        implements MemberCollectSubjectService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberCollectSubjectEntity> page =
                this.page(
                        new Query<MemberCollectSubjectEntity>().getPage(params),
                        new QueryWrapper<MemberCollectSubjectEntity>());

        return new PageUtils(page);
    }
}
