package approval.dingtalk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalBusinessRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 钉钉审批业务关联表 Mapper 接口
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Mapper
public interface DdApprovalBusinessRelMapper extends BaseMapper<DdApprovalBusinessRel> {

}
