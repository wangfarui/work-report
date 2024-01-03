package approval.system;

import com.alibaba.fastjson.JSON;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalEventType;
import com.wyyt.scp.biz.approval.system.callback.SystemApprovalCallback;
import com.wyyt.scp.biz.approval.system.callback.SystemCallbackListener;
import com.wyyt.scp.biz.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统审批配置类
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@Slf4j
public class SystemApprovalConfig {

    /**
     * 系统审批-模板编码映射表<p>
     * key -> {@link ApprovalBusinessTypeEnum#getCode()} <p>
     * value -> 系统业务表单模板的模板编号
     */
    private static Map<String, String> TEMPLATE_CODE_MAP = new HashMap<>();

    private static final Map<ApprovalBusinessTypeEnum, Map<ApprovalEventType, List<SystemCallbackListener>>> CALLBACK_LISTENER_MAP = new HashMap<>();

    public static void setTemplateCodeMap(Map<String, String> templateCodeMap) {
        log.info("[SystemApprovalConfig]templateCodeMap: " + JSON.toJSONString(templateCodeMap));
        TEMPLATE_CODE_MAP = templateCodeMap;
    }

    @NonNull
    public static String getTemplateCode(Integer code) {
        String templateCode = TEMPLATE_CODE_MAP.get(code.toString());
        if (templateCode == null) {
            log.info("[SystemApprovalConfig]TEMPLATE_CODE_MAP: " + JSON.toJSONString(TEMPLATE_CODE_MAP));
            throw new BizException("未知的业务审批类型！请检查模板编码映射关系！");
        }
        return templateCode;
    }

    public static void addCallbackListener(SystemApprovalCallback annotation, SystemCallbackListener listener) {
        Map<ApprovalEventType, List<SystemCallbackListener>> eventTypeMap = CALLBACK_LISTENER_MAP.computeIfAbsent(
                annotation.value(), t -> new HashMap<>(1 << 2)
        );
        ApprovalEventType approvalEventType = annotation.eventType();

        if (ApprovalEventType.ALL.equals(approvalEventType)) {
            throw new IllegalArgumentException("系统审批的方法不支持同时监听所有审批事件. 请将 SystemApprovalCallback(value=SystemApprovalBusinessTypeEnum.ALL) 改为其他值.");
        }
        SystemApprovalConfig.addCallbackListener(eventTypeMap, approvalEventType, listener);
    }

    public static List<SystemCallbackListener> getCallbackListener(ApprovalBusinessTypeEnum approvalTypeEnum,
                                                                   ApprovalEventType approvalEventType) {
        Map<ApprovalEventType, List<SystemCallbackListener>> eventTypeMap = CALLBACK_LISTENER_MAP.get(approvalTypeEnum);
        if (eventTypeMap == null) {
            return null;
        }
        return eventTypeMap.get(approvalEventType);
    }

    private static void addCallbackListener(Map<ApprovalEventType, List<SystemCallbackListener>> eventTypeMap,
                                            ApprovalEventType approvalEventType,
                                            SystemCallbackListener listener) {
        List<SystemCallbackListener> listeners = eventTypeMap.get(approvalEventType);
        if (listeners == null) {
            listeners = new ArrayList<>(4);
        }
        listeners.add(listener);
        eventTypeMap.put(approvalEventType, listeners);
    }
}
