package approval.dingtalk.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 钉钉审批记录响应对象
 *
 * @author wangfarui
 * @since 2023/9/12
 */
@ApiModel("钉钉审批记录响应对象")
@Data
public class DingTalkApprovalRecordVo {

    @ApiModelProperty("钉钉审批编号")
    private String instanceNumber;

    @ApiModelProperty("审批状态")
    private String auditStatus;

    @ApiModelProperty("审批记录的日志列表")
    private List<DingTalkApprovalLogVo> logList;
}
