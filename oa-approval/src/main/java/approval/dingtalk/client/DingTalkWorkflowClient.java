package approval.dingtalk.client;

import com.alibaba.fastjson.JSON;
import com.aliyun.dingtalkworkflow_1_0.Client;
import com.aliyun.dingtalkworkflow_1_0.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackGetCallBackFailedResultRequest;
import com.dingtalk.api.response.OapiCallBackGetCallBackFailedResultResponse;
import com.taobao.api.ApiException;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkAuthManager;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkTenantProperties;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkURLConstant;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalFormInstance;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalFormRel;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdUserRel;
import com.wyyt.scp.biz.approval.dingtalk.util.DingTalkApiClientUtil;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValues;

/**
 * 钉钉工作流Client
 *
 * @author wangfarui
 * @since 2023/8/2
 */
@Slf4j
public class DingTalkWorkflowClient {

    /**
     * 获取存储空间信息
     *
     * @return spaceId
     */
    public static GetAttachmentSpaceResponseBody.GetAttachmentSpaceResponseBodyResult getAttachmentSpace() {
        GetAttachmentSpaceHeaders getAttachmentSpaceHeaders = new GetAttachmentSpaceHeaders();
        getAttachmentSpaceHeaders.xAcsDingtalkAccessToken = DingTalkAuthManager.getToken();
        GetAttachmentSpaceRequest getAttachmentSpaceRequest = new GetAttachmentSpaceRequest()
                .setUserId(DingTalkApprovalConfig.getTenantPropertiesValue(DingTalkTenantProperties::getUserId))
                .setAgentId(DingTalkApprovalConfig.getTenantPropertiesValue(DingTalkTenantProperties::getAgentId));
        Client workflowClient = DingTalkApiClientUtil.createWorkflowClient();
        try {
            GetAttachmentSpaceResponse response = workflowClient.getAttachmentSpaceWithOptions(getAttachmentSpaceRequest, getAttachmentSpaceHeaders, new RuntimeOptions());
            log.info("[DingTalkWorkflowClient]getAttachmentSpace: {}", JSON.toJSONString(response));
            GetAttachmentSpaceResponseBody body = response.getBody();
            if (body.getSuccess() == null || !body.getSuccess()) {
                return null;
            }
            return body.getResult();
        } catch (TeaException err) {
            throw err;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }

    /**
     * 根据模板名称获取模板processCode
     * <p>若不存在返回null</p>
     */
    public static String getProcessCodeByName(String processName) {
        GetProcessCodeByNameHeaders getProcessCodeByNameHeaders = new GetProcessCodeByNameHeaders();
        getProcessCodeByNameHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        GetProcessCodeByNameRequest getProcessCodeByNameRequest = new GetProcessCodeByNameRequest()
                .setName(processName);
        Client workflowClient = DingTalkApiClientUtil.createWorkflowClient();
        try {
            GetProcessCodeByNameResponse response = workflowClient.getProcessCodeByNameWithOptions(getProcessCodeByNameRequest, getProcessCodeByNameHeaders, new RuntimeOptions());
            log.info("[DingTalkWorkflowClient]getProcessCodeByName: {}", JSON.toJSONString(response));
            GetProcessCodeByNameResponseBody.GetProcessCodeByNameResponseBodyResult result = response.getBody().getResult();
            return result.getProcessCode();
        } catch (TeaException err) {
            if (!err.getMessage().contains("模板不存在")) {
                throw err;
            }
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
        return null;
    }

    /**
     * 删除审批表单模板
     */
    public static void deleteApprovalForm(String processCode) {
        DeleteProcessHeaders deleteProcessHeaders = new DeleteProcessHeaders();
        deleteProcessHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        DeleteProcessRequest deleteProcessRequest = new DeleteProcessRequest()
                .setProcessCode(processCode)
                .setCleanRunningTask(false);
        Client workflowClient = DingTalkApiClientUtil.createWorkflowClient();
        try {
            DeleteProcessResponse response = workflowClient.deleteProcessWithOptions(deleteProcessRequest, deleteProcessHeaders, new RuntimeOptions());
            log.info("[DingTalkWorkflowClient]deleteApprovalForm: {}", JSON.toJSONString(response));
        } catch (TeaException err) {
            throw err;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }

    /**
     * 创建审批表单模板
     *
     * @param approvalForm 业务审批表单模板
     * @param processCode  表单code, 新增时传null
     * @return processCode
     */
    public static String saveOrUpdateApprovalForm(DingTalkApprovalFormEngine approvalForm, String processCode) {
        // 请求头
        FormCreateHeaders formCreateHeaders = new FormCreateHeaders();
        formCreateHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        // 表单全局配置
        FormCreateRequest.FormCreateRequestTemplateConfig templateConfig =
                new FormCreateRequest.FormCreateRequestTemplateConfig()
                        // 目录id
                        .setDirId(null)
                        // 是否在首页隐藏模板
                        .setDisableHomepage(true)
                        // 全局隐藏流程模板入口
                        .setHidden(null)
                        // 禁止表单编辑功能
                        .setDisableFormEdit(true)
                        // 禁用模板删除按钮
                        .setDisableDeleteProcess(false)
                        // 禁用停用按钮
                        .setDisableStopProcessButton(true)
                        // 禁用详情页再次发起按钮
                        .setDisableResubmit(true);
        // 表单模板请求对象
        FormCreateRequest formCreateRequest = new FormCreateRequest()
                .setProcessCode(processCode)
                .setName(approvalForm.getBusinessApprovalTypeEnum().getName())
                .setDescription(approvalForm.getBusinessApprovalTypeEnum().getDesc())
                .setTemplateConfig(templateConfig)
                .setFormComponents(approvalForm.buildFormComponent());
        // 创建Client并发送请求
        Client workflowClient = DingTalkApiClientUtil.createWorkflowClient();
        try {
            FormCreateResponse response = workflowClient.formCreateWithOptions(formCreateRequest, formCreateHeaders, new RuntimeOptions());
            log.info("[DingTalkWorkflowClient]createApprovalForm: {}", JSON.toJSONString(response));
            return response.getBody().getResult().getProcessCode();
        } catch (TeaException err) {
            throw err;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }

    /**
     * 发起审批实例
     *
     * @param instance          审批表单流程实例对象
     * @param ddUserRel         钉钉用户关联数据
     * @param ddApprovalFormRel 钉钉审批表单关联数据
     * @return instanceId
     */
    @SafeVarargs
    public static String processInstances(DingTalkApprovalFormInstance instance, DdUserRel ddUserRel, DdApprovalFormRel ddApprovalFormRel,
                                          Supplier<StartProcessInstanceRequestFormComponentValues>... expandValues) {
        DingTalkApprovalFormEngine approvalForm = instance.getApprovalForm();
        // 请求头
        StartProcessInstanceHeaders startProcessInstanceHeaders = new StartProcessInstanceHeaders();
        startProcessInstanceHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        // 构建表单控件元素
        List<StartProcessInstanceRequestFormComponentValues> formComponentValues = approvalForm.buildFormComponentValues();
        // 组装扩展控件值
        for (Supplier<StartProcessInstanceRequestFormComponentValues> supplier : expandValues) {
            StartProcessInstanceRequestFormComponentValues value = supplier.get();
            if (value != null) {
                formComponentValues.add(value);
            }
        }
        // 表单实例请求对象
        StartProcessInstanceRequest startProcessInstanceRequest = new StartProcessInstanceRequest()
                .setOriginatorUserId(ddUserRel.getDdUserId())
                .setProcessCode(ddApprovalFormRel.getProcessCode())
                .setDeptId(ddUserRel.getDdDeptId())
                .setMicroappAgentId(DingTalkApprovalConfig.getTenantPropertiesValue(DingTalkTenantProperties::getAgentId))
                .setFormComponentValues(formComponentValues);
        // 创建Client并发送请求
        Client workflowClient = DingTalkApiClientUtil.createWorkflowClient();
        try {
            log.info("[DingTalkWorkflowClient]processInstances req: {}", JSON.toJSONString(startProcessInstanceRequest));
            StartProcessInstanceResponse response = workflowClient.startProcessInstanceWithOptions(
                    startProcessInstanceRequest, startProcessInstanceHeaders, new RuntimeOptions()
            );
            log.info("[DingTalkWorkflowClient]processInstances res: {}", JSON.toJSONString(response));
            return response.getBody().getInstanceId();
        } catch (TeaException err) {
            throw err;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }

    /**
     * 获取审批流程实例详情
     *
     * @param processInstanceId 实例实例id
     */
    public static GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult getProcessInstanceById(String processInstanceId) {
        Client client = DingTalkApiClientUtil.createWorkflowClient();
        GetProcessInstanceHeaders getProcessInstanceHeaders = new GetProcessInstanceHeaders();
        getProcessInstanceHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        GetProcessInstanceRequest getProcessInstanceRequest = new GetProcessInstanceRequest()
                .setProcessInstanceId(processInstanceId);
        try {
            return client.getProcessInstanceWithOptions(
                    getProcessInstanceRequest, getProcessInstanceHeaders, new RuntimeOptions()
            ).getBody().getResult();
        } catch (TeaException err) {
            throw err;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }

    public static OapiCallBackGetCallBackFailedResultResponse getCallbackFailedResult() throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(DingTalkURLConstant.URL_CALLBACK_FAILED);
        OapiCallBackGetCallBackFailedResultRequest req = new OapiCallBackGetCallBackFailedResultRequest();
        req.setHttpMethod("GET");
        return client.execute(req, DingTalkAuthManager.getToken());
    }

    public static boolean authorizeApprovalDentry(List<String> fileIdList, String userId) {
        Client client = DingTalkApiClientUtil.createWorkflowClient();
        AddApproveDentryAuthHeaders addApproveDentryAuthHeaders = new AddApproveDentryAuthHeaders();
        addApproveDentryAuthHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        Long spaceId = Long.valueOf(DingTalkAuthManager.getSpaceId());
        List<AddApproveDentryAuthRequest.AddApproveDentryAuthRequestFileInfos> collect = fileIdList.stream().map(fileId ->
                new AddApproveDentryAuthRequest.AddApproveDentryAuthRequestFileInfos()
                        .setSpaceId(spaceId)
                        .setFileId(fileId)
        ).collect(Collectors.toList());

        AddApproveDentryAuthRequest addApproveDentryAuthRequest = new AddApproveDentryAuthRequest()
                .setUserId(userId)
                .setFileInfos(collect);
        try {
            AddApproveDentryAuthResponse response = client.addApproveDentryAuthWithOptions(addApproveDentryAuthRequest, addApproveDentryAuthHeaders, new RuntimeOptions());
            return response.getBody().getSuccess();
        } catch (TeaException err) {
            log.error("文件授权失败", err);
            return false;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }

    public static boolean terminateProcessInstance(String processInstanceId, String operatingUserId) {
        Client client = DingTalkApiClientUtil.createWorkflowClient();
        TerminateProcessInstanceHeaders terminateProcessInstanceHeaders = new TerminateProcessInstanceHeaders();
        terminateProcessInstanceHeaders.setXAcsDingtalkAccessToken(DingTalkAuthManager.getToken());
        TerminateProcessInstanceRequest terminateProcessInstanceRequest = new TerminateProcessInstanceRequest()
                .setProcessInstanceId(processInstanceId)
                .setIsSystem(false)
                .setOperatingUserId(operatingUserId);
        try {
            TerminateProcessInstanceResponse response = client.terminateProcessInstanceWithOptions(terminateProcessInstanceRequest, terminateProcessInstanceHeaders, new RuntimeOptions());
            return response.getBody().getResult();
        } catch (TeaException err) {
            throw err;
        } catch (Exception _err) {
            throw new TeaException(_err.getMessage(), _err);
        }
    }
}
