package approval.dingtalk.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyyt.scp.biz.api.vo.sys.FileRecordVo;
import com.wyyt.scp.biz.approval.dingtalk.mapper.DdApprovalProcessLogMapper;
import com.wyyt.scp.biz.approval.dingtalk.model.ApprovalProcessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalLogVo;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalProcessLog;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalProcessLogAttachment;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DdApprovalProcessLogDao
 *
 * @author wangfarui
 * @since 2023/8/28
 */
@Component
public class DdApprovalProcessLogDao extends ServiceImpl<DdApprovalProcessLogMapper, DdApprovalProcessLog> {

    @Resource
    private DdApprovalProcessLogAttachmentDao ddApprovalProcessLogAttachmentDao;

    public List<DingTalkApprovalLogVo> getApprovalLogList(Long businessRelId) {
        List<DdApprovalProcessLog> logList = this.lambdaQuery()
                .eq(DdApprovalProcessLog::getBusinessRelId, businessRelId)
                .orderByAsc(DdApprovalProcessLog::getCreateTime)
                .orderByAsc(DdApprovalProcessLog::getId)
                .list();
        if (logList.isEmpty()) {
            return Collections.emptyList();
        }
        List<DingTalkApprovalLogVo> result = new ArrayList<>(logList.size());
        for (DdApprovalProcessLog log : logList) {
            DingTalkApprovalLogVo vo = BeanUtil.copyProperties(log, DingTalkApprovalLogVo.class);
            vo.setProcessTypeName(ApprovalProcessTypeEnum.getProcessTypeRemark(log.getProcessType()));
            List<DdApprovalProcessLogAttachment> attachmentList = ddApprovalProcessLogAttachmentDao.lambdaQuery()
                    .eq(DdApprovalProcessLogAttachment::getLogId, log.getId())
                    .list();
            if (CollectionUtils.isNotEmpty(attachmentList)) {
                vo.setFileList(BeanUtil.copyToList(attachmentList, FileRecordVo.class, new CopyOptions()));
            }
            result.add(vo);
        }
        return result;
    }

    public void deleteApprovalLog(List<Long> businessRelIdList) {
        // 删除日志记录
        List<Long> logIdList = this.lambdaQuery()
                .in(DdApprovalProcessLog::getBusinessRelId, businessRelIdList)
                .select(DdApprovalProcessLog::getId)
                .list()
                .stream()
                .map(DdApprovalProcessLog::getId)
                .collect(Collectors.toList());
        this.removeByIds(logIdList);
        // 删除日志附件
        ddApprovalProcessLogAttachmentDao.lambdaUpdate()
                .in(DdApprovalProcessLogAttachment::getLogId, logIdList)
                .remove();
    }
}
