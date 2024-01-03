package approval;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.wyyt.scp.biz.api.vo.bas.oms.FileDto;
import com.wyyt.scp.biz.approval.dingtalk.DingTalkApprovalService;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkApprovalCallback;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalFormInstance;
import com.wyyt.scp.biz.approval.form.demo.DemoDingTalkApprovalForm;
import com.wyyt.scp.biz.approval.form.demo.DemoSystemApprovalForm;
import com.wyyt.scp.biz.approval.system.SystemApprovalFormInstance;
import com.wyyt.scp.biz.approval.system.SystemApprovalService;
import com.wyyt.scp.biz.approval.system.callback.SystemApprovalCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 示例审批服务<p>
 * 演示钉钉审批和系统审批的使用示例
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@Service
@Slf4j
public class DemoApprovalService {

    @Resource
    private SystemApprovalService systemApprovalService;

    @Resource
    private DingTalkApprovalService dingTalkApprovalService;

    @Transactional(rollbackFor = Exception.class)
    public void startSystemApproval(String name) {
        // 模拟审批表单数据
        DemoSystemApprovalForm approvalForm = new DemoSystemApprovalForm();
        approvalForm.setName(name);
        approvalForm.setStartDate(DateUtil.parse("2023-12-14", "yyyy-MM-dd"));
        FileDto fileDto = new FileDto();
        fileDto.setFileName("测试文件111.png");
        fileDto.setFileUrl("/wyyt-image/devcj/20231206/38e56498-5de1-4208-9efc-c8167baf6f82.png");
        approvalForm.setFileDto(fileDto);
        approvalForm.setRemark("备注fhjsahbfjahbsfjbasjbzxcn你倒是快就把房间卡上吧放假吧gsdnfkasfkasb");
        List<DemoSystemApprovalForm.DemoDetail> detailList = new ArrayList<>();
        DemoSystemApprovalForm.DemoDetail demoDetail1 = new DemoSystemApprovalForm.DemoDetail();
        demoDetail1.setCode("11111");
        demoDetail1.setCreateTime(new Date());
        detailList.add(demoDetail1);
        DemoSystemApprovalForm.DemoDetail demoDetail2 = new DemoSystemApprovalForm.DemoDetail();
        demoDetail2.setCode("22222");
        demoDetail2.setCreateTime(DateUtil.parse("2023-12-15 10:30:11", "yyyy-MM-dd HH:mm:ss"));
        detailList.add(demoDetail2);
        DemoSystemApprovalForm.DemoDetail demoDetail3 = new DemoSystemApprovalForm.DemoDetail();
        demoDetail3.setCode("33333");
        detailList.add(demoDetail3);
        approvalForm.setDetailList(detailList);

        // 构建发起审批表单的实例
        SystemApprovalFormInstance formInstance = SystemApprovalFormInstance.builder()
                .businessId(System.currentTimeMillis())
                .businessTypeEnum(ApprovalBusinessTypeEnum.DEMO)
                .approvalForm(approvalForm)
                .businessDate(new Date())
                .build();

        // 发起审批
        systemApprovalService.startApprovalFlowInstance(formInstance);
    }

    @Transactional(rollbackFor = Exception.class)
    public void startDingTalkApproval(Long businessId) {
        DemoDingTalkApprovalForm demoApprovalForm = new DemoDingTalkApprovalForm();
        demoApprovalForm.setProjectName("测试审批项目" + businessId);
        dingTalkApprovalService.startApprovalFlowInstance(
                DingTalkApprovalFormInstance.builder()
                        .businessId(businessId)
                        .approvalForm(demoApprovalForm)
                        .build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @SystemApprovalCallback(value = ApprovalBusinessTypeEnum.DEMO, eventType = ApprovalEventType.AGREE)
    @DingTalkApprovalCallback(value = ApprovalBusinessTypeEnum.DEMO, eventType = ApprovalEventType.AGREE)
    public void audit(BusinessApprovalCallbackEvent callbackEvent) {
        log.info("业务审批audit, callbackEvent: " + JSON.toJSONString(callbackEvent));
    }

    @Transactional(rollbackFor = Exception.class)
    @SystemApprovalCallback(value = ApprovalBusinessTypeEnum.DEMO, eventType = ApprovalEventType.REJECT)
    @DingTalkApprovalCallback(value = ApprovalBusinessTypeEnum.DEMO, eventType = ApprovalEventType.REJECT)
    public void reject(BusinessApprovalCallbackEvent callbackEvent) {
        log.info("业务审批reject, callbackEvent: " + JSON.toJSONString(callbackEvent));
    }
}
