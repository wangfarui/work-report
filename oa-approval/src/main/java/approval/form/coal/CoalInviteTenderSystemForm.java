package approval.form.coal;

import com.wyyt.scp.biz.approval.form.SystemApprovalFormEngine;
import com.wyyt.scp.biz.approval.system.SystemFormComponent;
import lombok.Data;

/**
 * 煤炭招标审批系统审批表单
 *
 * @author: chenyu
 * @Date: 2023/12/13 17:21
 */
@Data
public class CoalInviteTenderSystemForm implements SystemApprovalFormEngine {

    @SystemFormComponent("呈报内容")
    public String content;
}
