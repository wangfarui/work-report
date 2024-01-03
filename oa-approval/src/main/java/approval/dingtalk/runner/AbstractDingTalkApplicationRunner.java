package approval.dingtalk.runner;

import com.wyyt.big.platform.client.context.WyysSessionSupport;
import com.wyyt.scp.biz.approval.ApprovalApplicationRunner;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalFormInstance;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalFormRelService;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import com.wyyt.scp.biz.core.utils.DSUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 钉钉应用Runner 抽象类
 *
 * <ol>
 *     <li>初始化业务审批模板</li>
 * </ol>
 *
 * @author wangfarui
 * @since 2023/8/30
 */
@Slf4j
public abstract class AbstractDingTalkApplicationRunner extends ApprovalApplicationRunner {

    private final DdApprovalFormRelService ddApprovalFormRelService;

    public AbstractDingTalkApplicationRunner(DdApprovalFormRelService ddApprovalFormRelService) {
        this.ddApprovalFormRelService = ddApprovalFormRelService;
    }

    /**
     * 刷新钉钉启动项
     */
    public void refresh() {
        this.outerRun();
    }

    @Override
    protected void outerRun() {
        this.initApprovalForm();
    }

    /**
     * 初始化审批表单
     */
    private void initApprovalForm() {
        ApprovalBusinessTypeEnum[] approvalBusinessTypeEnums = ApprovalBusinessTypeEnum.values();
        Set<Long> tenantIds = DingTalkApprovalConfig.getAllTenantId();
        for (Long tenantId : tenantIds) {
            DSUtils.ds(tenantId.toString());
            WyysSessionSupport.initTenantId(tenantId);
            for (ApprovalBusinessTypeEnum typeEnum : approvalBusinessTypeEnums) {
                if (typeEnum.getDingTalkFormClazz() == null) continue;
                DingTalkApprovalFormEngine approvalForm;
                try {
                    approvalForm = typeEnum.getDingTalkFormClazz().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("[" + typeEnum.getName() + "]审批表单实例化失败", e);
                    throw new RuntimeException(e);
                }
                DingTalkApprovalFormInstance dingTalkApprovalFormInstance = DingTalkApprovalFormInstance.builder()
                        .approvalForm(approvalForm)
                        .tenantId(tenantId)
                        .build();
                try {
                    DingTalkApprovalConfig.setTenantIdContext(tenantId);
                    ddApprovalFormRelService.resolveDdApprovalFormRel(dingTalkApprovalFormInstance);
                } finally {
                    DingTalkApprovalConfig.removeTenantIdContext();
                }
            }
        }
        log.info("初始化钉钉审批表单成功");
    }

}
