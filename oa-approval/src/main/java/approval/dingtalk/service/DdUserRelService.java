package approval.dingtalk.service;

import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.wyyt.open.platform.client.module.wyys.user.UserOpenApi;
import com.wyyt.open.platform.client.module.wyys.user.request.UserDetailRequest;
import com.wyyt.open.platform.client.module.wyys.user.response.UserResponse;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkUserInfoClient;
import com.wyyt.scp.biz.approval.dingtalk.dao.DdUserRelDao;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdUserRel;
import com.wyyt.scp.biz.manager.WyysClientApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 钉钉用户关联表 服务实现类
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Service
public class DdUserRelService {

    @Resource
    private DdUserRelDao ddUserRelDao;

    @Transactional(rollbackFor = Exception.class)
    public DdUserRel resolveDdUserRel(Long tenantId, Long scmUserId) {
        // 查询已关联的用户信息
        DdUserRel ddUserRel = ddUserRelDao.lambdaQuery()
                .eq(DdUserRel::getTenantId, tenantId)
                .eq(DdUserRel::getScmUserId, scmUserId)
                .last("limit 1")
                .one();
        if (ddUserRel != null) {
            return ddUserRel;
        }

        return createDdUserRelByScmUser(tenantId, scmUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public DdUserRel resolveDdUserRel(Long tenantId, String ddUserId) {
        DdUserRel ddUserRel = ddUserRelDao.lambdaQuery()
                .eq(DdUserRel::getTenantId, tenantId)
                .eq(DdUserRel::getDdUserId, ddUserId)
                .last("limit 1")
                .one();
        if (ddUserRel != null) {
            return ddUserRel;
        }

        return createDdUserRelByDdUser(tenantId, ddUserId);
    }

    private DdUserRel createDdUserRelByScmUser(Long tenantId, Long scmUserId) {
        // 根据scm用户id查询用户详情
        UserResponse userResponse = WyysClientApi.getUserById(scmUserId);
        // 通过手机号查询钉钉用户id
        String ddUserId = DingTalkUserInfoClient.getUserIdByMobile(userResponse.getMobile());
        // 通过钉钉用户id查询钉钉用户详情
        OapiV2UserGetResponse.UserGetResponse ddUserRsp = DingTalkUserInfoClient.getUserById(ddUserId);

        // 新增关联对象
        DdUserRel newEntity = new DdUserRel();
        newEntity.setTenantId(tenantId);
        newEntity.setScmUserId(scmUserId);
        newEntity.setDdUserId(ddUserId);
        newEntity.setDdUserName(userResponse.getUserName());
        newEntity.setUserMobile(userResponse.getMobile());
        newEntity.setDdDeptId(ddUserRsp.getDeptIdList().get(0));
        ddUserRelDao.save(newEntity);

        return newEntity;
    }

    /**
     * 通过钉钉用户创建关联信息
     */
    private DdUserRel createDdUserRelByDdUser(Long tenantId, String ddUserId) {
        OapiV2UserGetResponse.UserGetResponse ddUserRsp = DingTalkUserInfoClient.getUserById(ddUserId);
        if (StringUtils.isBlank(ddUserRsp.getMobile())) {
            return null;
        }
        UserDetailRequest request = new UserDetailRequest();
        request.setTenantId(tenantId);
        request.setMobile(ddUserRsp.getMobile());
        UserResponse userResponse = UserOpenApi.detailUser(request);
        if (userResponse == null) {
            return null;
        }

        // 新增关联对象
        DdUserRel newEntity = new DdUserRel();
        newEntity.setTenantId(tenantId);
        newEntity.setScmUserId(userResponse.getId());
        newEntity.setDdUserId(ddUserId);
        newEntity.setDdUserName(userResponse.getUserName());
        newEntity.setUserMobile(ddUserRsp.getMobile());
        newEntity.setDdDeptId(ddUserRsp.getDeptIdList().get(0));
        ddUserRelDao.save(newEntity);

        return newEntity;
    }

}
