package approval.dingtalk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wyyt.scp.biz.approval.ApprovalInstanceStatusEnum;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkWorkflowClient;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkCommonConstant;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalFormInstance;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalBusinessRel;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalFormRel;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdUserRel;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalBusinessRelService;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalFormRelService;
import com.wyyt.scp.biz.approval.dingtalk.service.DdUserRelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValues;

/**
 * 钉钉审批 服务层
 *
 * @author wangfarui
 * @since 2023/8/2
 */
@Service
@Slf4j
public class DingTalkApprovalService {

    @Resource
    private DdUserRelService ddUserRelService;
    @Resource
    private DdApprovalFormRelService ddApprovalFormRelService;
    @Resource
    private DdApprovalBusinessRelService ddApprovalBusinessRelService;

    /**
     * 发起审批流程实例
     */
    public void startApprovalFlowInstance(DingTalkApprovalFormInstance instance) {
        log.info("发起审批流程实例: {}", instance);
        instance.checkParamValid();
        DingTalkApprovalConfig.setTenantIdContext(instance.getTenantId());

        try {
            // 1. 创建并获取审批模板
            DdApprovalFormRel ddApprovalFormRel = ddApprovalFormRelService.resolveDdApprovalFormRel(instance);
            log.info("发起审批流程实例-审批模板: {}", ddApprovalFormRel);

            // 2. 获取审批发起人对应的钉钉用户信息
            DdUserRel ddUserRel = ddUserRelService.resolveDdUserRel(instance.getTenantId(), instance.getUserId());
            // 2.1 若手动指定钉钉部门id，则覆盖用户关联的钉钉部门
            if (instance.getDepartmentId() != null) {
                ddUserRel.setDdDeptId(instance.getDepartmentId());
            }
            log.info("发起审批流程实例-钉钉用户: {}", ddUserRel);

            // 3. 发起审批实例，拿取实例id
            String instanceId = DingTalkWorkflowClient.processInstances(
                    instance, ddUserRel, ddApprovalFormRel
//                    () -> getSelfRelatedForm(instance.getBusinessId())
            );
            log.info("发起审批流程实例-审批实例: {}", instanceId);

            // 4. 保存审批实例与业务单据的关联信息
            ddApprovalBusinessRelService.saveApprovalBusinessRel(instance, ddApprovalFormRel, instanceId);
        } finally {
            DingTalkApprovalConfig.removeTenantIdContext();
        }

    }

    /**
     * 获取自身的关联审批单
     *
     * @param businessId 自身的业务单id
     */
    public StartProcessInstanceRequestFormComponentValues getSelfRelatedForm(Long businessId) {
        List<DdApprovalBusinessRel> approvalRecords = ddApprovalBusinessRelService.getApprovalRecords(Collections.singletonList(businessId));
        StartProcessInstanceRequestFormComponentValues componentValues = new StartProcessInstanceRequestFormComponentValues()
                .setName(DingTalkCommonConstant.SELF_RELATE_FIELD_NAME);
        return this.buildRelateFieldValues(componentValues, approvalRecords);
    }

    /**
     * 获取审批关联的业务表单
     *
     * @param businessIdList 业务单id
     */
    public StartProcessInstanceRequestFormComponentValues getApprovalRelatedForm(StartProcessInstanceRequestFormComponentValues componentValues,
                                                                                 List<Long> businessIdList) {
        List<DdApprovalBusinessRel> approvalRecords = ddApprovalBusinessRelService.getApprovalRecords(businessIdList, ApprovalInstanceStatusEnum.AGREE.getCode());
        return this.buildRelateFieldValues(componentValues, approvalRecords);
    }

    private StartProcessInstanceRequestFormComponentValues buildRelateFieldValues(StartProcessInstanceRequestFormComponentValues componentValues,
                                                                                  List<DdApprovalBusinessRel> approvalRecords) {
        if (approvalRecords.isEmpty()) {
            return null;
        }
        // 审批实例id
        List<JSONObject> list = new ArrayList<>(approvalRecords.size());
        for (DdApprovalBusinessRel ddApprovalBusinessRel : approvalRecords) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("procInstId", ddApprovalBusinessRel.getInstanceId());
            jsonObject.put("businessId", ddApprovalBusinessRel.getInstanceBusinessId());
            list.add(jsonObject);
        }
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("list", list);

        // 审批实例标题
        List<String> instanceTitleList = approvalRecords.stream()
                .map(DdApprovalBusinessRel::getInstanceTitle)
                .collect(Collectors.toList());

        return componentValues.setValue(JSON.toJSONString(instanceTitleList)).setExtValue(JSON.toJSONString(jsonObject2));
    }

}
