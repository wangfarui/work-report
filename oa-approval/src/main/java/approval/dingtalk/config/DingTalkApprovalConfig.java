package approval.dingtalk.config;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalEventType;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkApprovalCallback;
import com.wyyt.scp.biz.approval.dingtalk.listener.DingTalkCallbackListener;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 钉钉审批属性配置
 *
 * @author wangfarui
 * @since 2023/8/17
 */
@Slf4j
public abstract class DingTalkApprovalConfig {

    private static final ThreadLocal<Long> TENANT_ID_CONTEXT = new ThreadLocal<>();

    /**
     * 业务审批表单的版本
     */
    private static final Map<ApprovalBusinessTypeEnum, String> FORM_VERSION_MAP;

    /**
     * 定时缓存
     */
    private static final Cache<String, String> TIMER_CACHE;

    /**
     * scm租户的钉钉属性
     */
    private static final Map<Long, DingTalkTenantProperties> TENANT_PROPERTIES_MAP;

    private static final Map<ApprovalBusinessTypeEnum, Map<ApprovalEventType, List<DingTalkCallbackListener>>> CALLBACK_LISTENER_MAP;

    static {
        FORM_VERSION_MAP = new HashMap<>();
        TIMER_CACHE = CacheBuilder.newBuilder()
                .maximumSize(100L)
                .expireAfterWrite(Duration.ofSeconds(600L))
                .removalListener(listener -> log.info(listener.getKey() + " : " + listener.getValue() + " 被移除,原因:" + listener.getCause()))
                .build();
        TENANT_PROPERTIES_MAP = new HashMap<>();
        CALLBACK_LISTENER_MAP = new HashMap<>();
    }

    public static void setTenantIdContext(Long tenantId) {
        TENANT_ID_CONTEXT.set(tenantId);
    }

    public static Long getTenantId() {
        Long tenantId = TENANT_ID_CONTEXT.get();
        if (tenantId == null) {
            throw new IllegalStateException("无效的租户id");
        }
        return tenantId;
    }

    public static void removeTenantIdContext() {
        TENANT_ID_CONTEXT.remove();
    }

    public static String getFormVersion(ApprovalBusinessTypeEnum approvalBusinessTypeEnum, Supplier<String> computeMethod) {
        String version = FORM_VERSION_MAP.get(approvalBusinessTypeEnum);
        if (version == null) {
            FORM_VERSION_MAP.put(approvalBusinessTypeEnum, (version = computeMethod.get()));
        }
        return version;
    }

    public static String getValueByTimerCache(String key, Supplier<String> supplier) {
        String value = TIMER_CACHE.getIfPresent(key);
        if (value == null) {
            value = supplier.get();
            TIMER_CACHE.put(key, value);
        }
        return value;
    }

    public static void cleanTimerCache() {
        TIMER_CACHE.invalidateAll();
    }

    public static void putTenantProperties(Map<Long, DingTalkTenantProperties> propertiesMap) {
        log.info("[DingTalkApprovalConfig]propertiesMap: " + JSON.toJSONString(propertiesMap));
        TENANT_PROPERTIES_MAP.clear();
        TENANT_PROPERTIES_MAP.putAll(propertiesMap);
    }

    public static Set<Long> getAllTenantId() {
        return TENANT_PROPERTIES_MAP.keySet();
    }

    public static Collection<DingTalkTenantProperties> getUniqueAppKeyTenantProperties() {
        Map<String, DingTalkTenantProperties> map = new HashMap<>();
        for (DingTalkTenantProperties properties : TENANT_PROPERTIES_MAP.values()) {
            map.putIfAbsent(properties.getAppKey(), properties);
        }
        return map.values();
    }

    public static <R> R getTenantPropertiesValue(Function<DingTalkTenantProperties, R> function) {
        return getTenantPropertiesValue(getTenantId(), function);
    }

    public static <R> R getTenantPropertiesValue(Long tenantId, Function<DingTalkTenantProperties, R> function) {
        DingTalkTenantProperties tenantProperties = TENANT_PROPERTIES_MAP.get(tenantId);
        if (tenantProperties == null) {
            log.info("[DingTalkApprovalConfig]TENANT_PROPERTIES_MAP: " + JSON.toJSONString(TENANT_PROPERTIES_MAP));
            throw new IllegalStateException("租户未开启钉钉配置");
        }
        return function.apply(tenantProperties);
    }

    public static void addCallbackListener(DingTalkApprovalCallback annotation, DingTalkCallbackListener listener) {
        Map<ApprovalEventType, List<DingTalkCallbackListener>> eventTypeMap = CALLBACK_LISTENER_MAP.computeIfAbsent(
                annotation.value(), t -> new HashMap<>(1 << 2)
        );
        ApprovalEventType approvalEventType = annotation.eventType();
        if (ApprovalEventType.ALL.equals(approvalEventType)) {
            addCallbackListener(eventTypeMap, ApprovalEventType.AGREE, listener);
            addCallbackListener(eventTypeMap, ApprovalEventType.REJECT, listener);
            addCallbackListener(eventTypeMap, ApprovalEventType.IN_PROGRESS, listener);
        } else {
            addCallbackListener(eventTypeMap, approvalEventType, listener);
        }

    }

    private static void addCallbackListener(Map<ApprovalEventType, List<DingTalkCallbackListener>> eventTypeMap,
                                            ApprovalEventType approvalEventType,
                                            DingTalkCallbackListener listener) {
        List<DingTalkCallbackListener> listeners = eventTypeMap.get(approvalEventType);
        if (listeners == null) {
            listeners = new ArrayList<>(4);
        }
        listeners.add(listener);
        eventTypeMap.put(approvalEventType, listeners);
    }

    public static List<DingTalkCallbackListener> getCallbackListener(ApprovalBusinessTypeEnum approvalTypeEnum,
                                                                     ApprovalEventType approvalEventType) {
        Map<ApprovalEventType, List<DingTalkCallbackListener>> eventTypeMap = CALLBACK_LISTENER_MAP.get(approvalTypeEnum);
        if (eventTypeMap == null) {
            return null;
        }
        return eventTypeMap.get(approvalEventType);
    }
}
