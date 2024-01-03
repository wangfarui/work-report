package approval.form.coal;

import com.wyyt.scp.biz.approval.form.SystemApprovalFormEngine;
import com.wyyt.scp.biz.approval.system.SystemFormComponent;
import lombok.Data;

/**
 * @author: chenyu
 * @Date: 2023/12/13 15:51
 */
@Data
public class CoalResultReportSystemForm implements SystemApprovalFormEngine {

    @SystemFormComponent("呈报内容")
    public String content;
}
