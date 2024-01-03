package approval.dingtalk.service;

import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkWorkflowClient;
import com.wyyt.scp.biz.approval.dingtalk.dao.DdApprovalFormRelDao;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalFormInstance;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalFormRel;
import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 钉钉审批表单关联表 服务实现类
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Service
@Slf4j
public class DdApprovalFormRelService {

    @Resource
    private DdApprovalFormRelDao ddApprovalFormRelDao;

    /**
     * 决定一个钉钉审批表单关联对象
     */
    @Transactional(rollbackFor = Exception.class)
    public DdApprovalFormRel resolveDdApprovalFormRel(DingTalkApprovalFormInstance instance) {
        DingTalkApprovalFormEngine approvalForm = instance.getApprovalForm();

        // 从关联表中查询钉钉表单模板Code
        DdApprovalFormRel ddApprovalFormRel = ddApprovalFormRelDao.lambdaQuery()
                .eq(DdApprovalFormRel::getTenantId, instance.getTenantId())
                .eq(DdApprovalFormRel::getTypeCode, approvalForm.getBusinessApprovalTypeEnum().getCode())
                .last("limit 1")
                .one();
        if (ddApprovalFormRel != null) {
            String version = approvalForm.version();
            if (version.equals(ddApprovalFormRel.getFormVersion())) {
                return ddApprovalFormRel;
            }
            return updateDdApprovalFormRel(approvalForm, ddApprovalFormRel);
        }
        // 关联表无映射时，生成关联数据并返回
        return createDdApprovalFormRel(instance);
    }

    private DdApprovalFormRel updateDdApprovalFormRel(DingTalkApprovalFormEngine approvalForm, DdApprovalFormRel ddApprovalFormRel) {
        log.info("更新钉钉审批表单模板, approvalForm: {}, ProcessCode: {}", approvalForm.getBusinessApprovalTypeEnum(), ddApprovalFormRel.getProcessCode());
        DingTalkWorkflowClient.saveOrUpdateApprovalForm(approvalForm, ddApprovalFormRel.getProcessCode());
        ApprovalBusinessTypeEnum typeEnum = approvalForm.getBusinessApprovalTypeEnum();
        ddApprovalFormRelDao.lambdaUpdate()
                .eq(DdApprovalFormRel::getId, ddApprovalFormRel.getId())
                .set(DdApprovalFormRel::getTypeCode, typeEnum.getCode())
                .set(DdApprovalFormRel::getTypeName, typeEnum.getName())
                .set(DdApprovalFormRel::getFormVersion, approvalForm.version())
                .update();
        ddApprovalFormRel.setTypeCode(typeEnum.getCode());
        ddApprovalFormRel.setTypeName(typeEnum.getName());
        ddApprovalFormRel.setFormVersion(approvalForm.version());
        return ddApprovalFormRel;
    }

    private DdApprovalFormRel createDdApprovalFormRel(DingTalkApprovalFormInstance instance) {
        DingTalkApprovalFormEngine approvalForm = instance.getApprovalForm();
        ApprovalBusinessTypeEnum approvalBusinessTypeEnum = approvalForm.getBusinessApprovalTypeEnum();

        // 根据审批表单模板名称获取历史钉钉表单模板code
        String processCode = DingTalkWorkflowClient.getProcessCodeByName(approvalBusinessTypeEnum.getName());
//        // 若模板code不为空则删除钉钉对应的审批表单模板
//        if (StringUtils.isNotBlank(oldProcessCode)) {
//            DingTalkWorkflowClient.deleteApprovalForm(oldProcessCode);
//        }

        if (StringUtils.isBlank(processCode)) {
            // 新生成钉钉审批表单模板
            processCode = DingTalkWorkflowClient.saveOrUpdateApprovalForm(approvalForm, null);
        }

        // 初始化关联对象并保存
        DdApprovalFormRel ddApprovalFormRel = new DdApprovalFormRel();
        ddApprovalFormRel.setTenantId(instance.getTenantId());
        ddApprovalFormRel.setTypeCode(approvalBusinessTypeEnum.getCode());
        ddApprovalFormRel.setTypeName(approvalBusinessTypeEnum.getName());
        ddApprovalFormRel.setCreateById(instance.getUserId(false));
        ddApprovalFormRel.setCreateTime(new Date());
        ddApprovalFormRel.setProcessCode(processCode);
        ddApprovalFormRelDao.save(ddApprovalFormRel);

        return ddApprovalFormRel;
    }

}
