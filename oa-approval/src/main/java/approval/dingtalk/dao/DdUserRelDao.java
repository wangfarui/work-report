package approval.dingtalk.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyyt.scp.biz.approval.dingtalk.mapper.DdUserRelMapper;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdUserRel;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 钉钉用户关联表 服务实现类
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Component
public class DdUserRelDao extends ServiceImpl<DdUserRelMapper, DdUserRel> {

}
