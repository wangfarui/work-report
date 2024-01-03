package approval;

import com.wyyt.scp.biz.approval.form.DingTalkApprovalFormEngine;
import com.wyyt.scp.biz.approval.form.coal.*;
import com.wyyt.scp.biz.approval.form.demo.DemoDingTalkApprovalForm;
import com.wyyt.scp.biz.approval.form.logistics.*;
import com.wyyt.scp.biz.approval.form.settle.AdvancePaymentDingTalkApprovalForm;
import com.wyyt.scp.biz.approval.form.settle.DepositPaymentRequestDingTalkApprovalForm;
import com.wyyt.scp.biz.approval.form.settle.PaymentRequestDingTalkApprovalForm;
import com.wyyt.scp.biz.approval.form.settle.SettleRefundBillFormDingTalk;
import com.wyyt.scp.biz.approval.form.steel.*;
import lombok.Getter;

import java.util.HashSet;

/**
 * 业务审批类型枚举
 *
 * @author wangfarui
 * @since 2023/8/2
 */
@Getter
public enum ApprovalBusinessTypeEnum {
    DEMO(-1, "测试OA审批", DemoDingTalkApprovalForm.class),
    TENDER(2, "投标审批", CoalTenderFormDingTalk.class),
    COAL_LOGISTICS_CONTRACT(3, "物流合同", LogisticsContractFormDingTalk.class),
    INVITETENDER(4, "招标审批", CoalInviteTenderFormDingTalk.class),
    PURCHASE_CONTRACT(5, "采购合同", CoalPurchaseContractFormDingTalk.class),
    RESULT_REPORT(6, "结果报告", CoalResultReportInfoFormDingTalk.class),
    SALE_CONTRACT(7, "销售合同", CoalSaleContractFormDingTalk.class),
    COAL_PROJECT(8, "立项项目", CoalProjectDingTalkApprovalForm.class),
    COAL_AGREEMENT(9, "补充协议", CoalAgreementDingTalkApprovalForm.class),
    FRAME_PURCHASE_CONTRACT(10, "采购框架合同", CoalFramePurchaseContractFormDingTalk.class),
    FRAME_SALE_CONTRACT(11, "销售框架合同", CoalFrameSaleContractFormDingTalk.class),
    DEPOSIT_PAYMENT_REQUEST(12, "保证金付款申请", DepositPaymentRequestDingTalkApprovalForm.class),
    PAYMENT_REQUEST(13, "付款申请", PaymentRequestDingTalkApprovalForm.class),
    ADVANCE_PAYMENT(14, "预付款申请", AdvancePaymentDingTalkApprovalForm.class),
    //========================================================合同物流=====================================
    LOGISTICS_INIT_PROJECT(15,"合同物流立项管理", LogisticsInitProjectDingTalkApprovalForm.class),
    LOGISTICS_RESULT_REPORT_MANAGEMENT(16,"合同物流结果报告", LogisticsResultReportInfoFormDingTalk.class),
    LOGISTICS_TENDER(17,"合同物流投标审批", LogisticsTenderFormDingTalk.class),
    LOGISTICS_INVITE_TENDER(18,"合同物流招标审批", LogisticsInviteTenderFormDingTalk.class),
    LOGISTICS_AGREEMENT(19,"合同物流补充协议", LogisticsAgreementFormDingTalk.class),
    LOGISTICS_UP_CONTRACT(20, "承运合同", LogisticsUpContractFormDingTalk.class),
    LOGISTICS_DOWN_CONTRACT(21, "托运合同", LogisticsDownContractFormDingTalk.class),
    COAL_LOGISTICS_FRAME_CONTRACT(31, "物流框架合同", LogisticsFrameContractFormDingTalk.class),

    SETTLE_REFUND_BILL(32, "退款单", SettleRefundBillFormDingTalk.class),

    //========================================================钢材=====================================
    STEEL_PROJECT(22, "钢材立项管理", SteelProjectDingTalkApprovalForm.class),
    STEEL_STORAGE_CONTRACT(23, "钢材仓储合同", SteelStorageContractDingTalkApprovalForm.class),
    STEEL_SALE_CONTRACT(24, "钢材销售普通合同", SteelSaleContractFormDingTalk.class),
    STEEL_SALE_FRAME_CONTRACT(25, "钢材销售框架合同", SteelSaleFrameContractFormDingTalk.class),
    STEEL_AGREEMENT(26, "钢材补充协议", SteelAgreementDingTalkApprovalForm.class),
    STEEL_LOGISTICS_CONTRACT(27, "钢材物流合同", SteelLogisticsContractFormDingTalk.class),
    STEEL_PURCHASE_CONTRACT(28, "钢材采购普通合同", SteelPurchaseContractFormDingTalk.class),
    STEEL_PURCHASE_FRAME_CONTRACT(29, "钢材采购框架合同", SteelPurchaseFrameContractFormDingTalk.class),
    ;

    /**
     * 业务审批类型的编号
     * <p>枚举内唯一!!!</p>
     * <p>钉钉审批：在不改动code的情况下，修改表单控件，会同步更新钉钉表单模板</p>
     * <p>系统审批：code对应Apollo配置，便于拿取模板编码。Apollo的key为 system.oa.config </p>
     */
    private final Integer code;

    /**
     * 审批表单名称（即钉钉表单的名称）
     * <p>枚举内唯一!!!</p>
     * <p>钉钉审批：表单名称会被生成于钉钉表单模板上</p>
     * <p>系统审批：手动新建的系统表单模板名称建议与该字段值保持一致</p>
     */
    private final String name;

    /**
     * 钉钉审批表单Class类
     * <p>用于初始化钉钉审批表单，没有钉钉审批时，可为null</p>
     */
    private final Class<? extends DingTalkApprovalFormEngine> dingTalkFormClazz;

    /**
     * 审批表单描述
     */
    private final String desc;

    /**
     * 完成内部数据校验
     */
    private static boolean FINISH_VERIFY;

    ApprovalBusinessTypeEnum(Integer code, String name) {
        this(code, name, null, null);
    }

    ApprovalBusinessTypeEnum(Integer code, String name, Class<? extends DingTalkApprovalFormEngine> dingTalkFormClazz) {
        this(code, name, dingTalkFormClazz, null);
    }

    ApprovalBusinessTypeEnum(Integer code, String name, Class<? extends DingTalkApprovalFormEngine> dingTalkFormClazz, String desc) {
        this.code = code;
        this.name = name;
        this.dingTalkFormClazz = dingTalkFormClazz;
        this.desc = desc;
    }

    public static ApprovalBusinessTypeEnum getBusinessTypeEnum(Integer code) {
        ApprovalBusinessTypeEnum[] approvalBusinessTypeEnums = values();
        for (ApprovalBusinessTypeEnum approvalBusinessTypeEnum : approvalBusinessTypeEnums) {
            if (approvalBusinessTypeEnum.getCode().equals(code)) {
                return approvalBusinessTypeEnum;
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        ApprovalBusinessTypeEnum[] approvalBusinessTypeEnums = values();
        for (ApprovalBusinessTypeEnum approvalBusinessTypeEnum : approvalBusinessTypeEnums) {
            if (approvalBusinessTypeEnum.getCode().equals(code)) {
                return approvalBusinessTypeEnum.getName();
            }
        }
        return null;
    }

    public static void verifyEnumSelf() {
        if (ApprovalBusinessTypeEnum.FINISH_VERIFY) {
            return;
        }
        HashSet<Integer> codeSet = new HashSet<>();
        HashSet<String> nameSet = new HashSet<>();
        for (ApprovalBusinessTypeEnum approvalBusinessTypeEnum : values()) {
            Integer enumCode = approvalBusinessTypeEnum.getCode();
            String enumName = approvalBusinessTypeEnum.getName();
            if (codeSet.contains(enumCode)) {
                throw new IllegalArgumentException("审批业务类型枚举(ApprovalBusinessTypeEnum)的 code 不能重复! 重复code为: " + enumCode);
            }
            if (nameSet.contains(enumName)) {
                throw new IllegalArgumentException("审批业务类型枚举(ApprovalBusinessTypeEnum)的 name 不能重复! 重复name为: " + enumName);
            }
            codeSet.add(enumCode);
            nameSet.add(enumName);
        }
        ApprovalBusinessTypeEnum.FINISH_VERIFY = true;
    }
}
