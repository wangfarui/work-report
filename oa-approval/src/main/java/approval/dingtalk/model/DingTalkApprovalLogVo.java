package approval.dingtalk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 钉钉审批日志响应对象
 *
 * @author wangfarui
 * @since 2023/9/12
 */
@Data
@ApiModel("钉钉审批日志响应对象")
public class DingTalkApprovalLogVo {

    private String id;

    @ApiModelProperty("操作人名称")
    private String staffName;

    @ApiModelProperty("流程类型 ApprovalProcessType")
    private Integer processType;

    @ApiModelProperty("操作详情")
    private String processTypeName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty("评论内容")
    private String processContent;

    @ApiModelProperty("附件")
    private List<FileRecordVo> fileList;
}
