package approval.form.demo;

import com.wyyt.scp.biz.api.vo.bas.oms.FileDto;
import com.wyyt.scp.biz.approval.form.SystemApprovalFormEngine;
import com.wyyt.scp.biz.approval.system.SystemFormComponent;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 示例的系统审批表单
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@Data
public class DemoSystemApprovalForm implements SystemApprovalFormEngine {

    @SystemFormComponent("名称")
    public String name;

    @SystemFormComponent(value = "开始日期", pattern = "yyyy-MM-dd")
    private Date startDate;

    @SystemFormComponent("资料")
    private FileDto fileDto;

    @SystemFormComponent("说明")
    private String remark;

    @SystemFormComponent("数据明细")
    private List<DemoDetail> detailList;

    @Data
    public static class DemoDetail {

        @SystemFormComponent("编码")
        private String code;

        @SystemFormComponent("创建时间")
        private Date createTime;
    }
}
