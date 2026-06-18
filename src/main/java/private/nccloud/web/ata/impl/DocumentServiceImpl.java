package nccloud.web.ata.impl;

import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.jdbc.framework.*;
import nc.jdbc.framework.processor.*;
import nc.bs.dao.BaseDAO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pub.VOStatus;
import java.util.*;
import nc.bs.framework.common.InvocationInfoProxy;

public class DocumentServiceImpl implements IDocumentService {

    private BaseDAO dao;

    private BaseDAO getDao() {
        if (dao == null) {
            dao = new BaseDAO();
        }
        return dao;
    }

    private String getPkGroup() {
        return InvocationInfoProxy.getInstance().getGroupId();
    }

    private String getPkOrg() {
        return InvocationInfoProxy.getInstance().getPkOrg();
    }

    private String getUserId() {
        return InvocationInfoProxy.getInstance().getUserId();
    }

    private UFDateTime getNow() {
        return new UFDateTime();
    }

    private void fillSysFields(SuperVO vo, boolean isInsert) {
        if (vo.getAttributeValue(DocumentVO.PK_GROUP) == null) {
            vo.setAttributeValue(DocumentVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(DocumentVO.PK_ORG) == null) {
            vo.setAttributeValue(DocumentVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(DocumentVO.CREATOR, getUserId());
            vo.setAttributeValue(DocumentVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(DocumentVO.MODIFIER, getUserId());
        vo.setAttributeValue(DocumentVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(DocumentVO.DR) == null) {
            vo.setAttributeValue(DocumentVO.DR, 0);
        }
    }

    private void fillSysFieldsForChild(SuperVO vo, boolean isInsert, String parentPkField, String parentPk) {
        if (parentPk != null) {
            vo.setAttributeValue(parentPkField, parentPk);
        }
        if (vo.getAttributeValue(ExhibitVO.PK_GROUP) == null) {
            vo.setAttributeValue(ExhibitVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(ExhibitVO.PK_ORG) == null) {
            vo.setAttributeValue(ExhibitVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(ExhibitVO.CREATOR, getUserId());
            vo.setAttributeValue(ExhibitVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(ExhibitVO.MODIFIER, getUserId());
        vo.setAttributeValue(ExhibitVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(ExhibitVO.DR) == null) {
            vo.setAttributeValue(ExhibitVO.DR, 0);
        }
    }

    @Override
    public AggDocumentVO save(AggDocumentVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        DocumentVO parent = vo.getParentVO();
        if (parent == null) {
            throw new BusinessException("表头不能为空");
        }
        boolean isInsert = (parent.getPk_document() == null || parent.getPk_document().trim().length() == 0);
        if (isInsert) {
            parent.setPk_document(getDao().getOID());
            if (parent.getDoc_status() == null) {
                parent.setDoc_status(0);
            }
        } else {
            DocumentVO old = queryHeadByPk(parent.getPk_document());
            if (old == null) {
                throw new BusinessException("数据不存在，无法更新");
            }
            if (parent.getTs() == null || !parent.getTs().equals(old.getTs())) {
                throw new BusinessException("数据已被他人修改，请刷新后重试");
            }
        }
        fillSysFields(parent, isInsert);
        try {
            if (isInsert) {
                getDao().insertVO(parent);
            } else {
                getDao().updateVO(parent);
            }
        } catch (Exception e) {
            throw new BusinessException("保存失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_document());
    }

    @Override
    public AggDocumentVO audit(AggDocumentVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        DocumentVO parent = vo.getParentVO();
        if (parent.getPk_document() == null) {
            throw new BusinessException("主键不能为空");
        }
        DocumentVO dbHead = queryHeadByPk(parent.getPk_document());
        if (dbHead == null) {
            throw new BusinessException("单证不存在");
        }
        if (dbHead.getDoc_status() != null && dbHead.getDoc_status() != 0 && dbHead.getDoc_status() != 2) {
            throw new BusinessException("只有草稿或需延期状态的单证才能审核");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        String pkList = dbHead.getPk_exhibit_list();
        if (pkList != null && pkList.trim().length() > 0) {
            ExhibitVO[] exhibits = queryExhibitsByListPk(pkList);
            if (exhibits != null && exhibits.length > 0) {
                for (ExhibitVO ex : exhibits) {
                    if (ex.getSerial_no() == null || ex.getSerial_no().trim().length() == 0) {
                        throw new BusinessException("展品[" + ex.getExhibit_name() + "]序列号缺失，不能审核");
                    }
                }
            }
        }
        UFDate validTo = dbHead.getValid_to();
        UFDate today = new UFDate();
        if (validTo != null) {
            long diffDays = (validTo.getTime() - today.getTime()) / (24 * 60 * 60 * 1000);
            if (diffDays < 0) {
                dbHead.setDoc_status(3);
                fillSysFields(dbHead, false);
                try {
                    getDao().updateVO(dbHead);
                } catch (Exception e) {
                    throw new BusinessException("更新状态失败：" + e.getMessage());
                }
                throw new BusinessException("单证已过期，不能审核");
            } else if (diffDays < 30) {
                dbHead.setDoc_status(2);
                fillSysFields(dbHead, false);
                try {
                    getDao().updateVO(dbHead);
                } catch (Exception e) {
                    throw new BusinessException("更新状态失败：" + e.getMessage());
                }
                throw new BusinessException("单证有效期不足30天，请先办理延期");
            }
        }
        dbHead.setDoc_status(1);
        dbHead.setReviewer(getUserId());
        dbHead.setReview_time(getNow());
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
        } catch (Exception e) {
            throw new BusinessException("审核失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_document());
    }

    @Override
    public AggDocumentVO unAudit(AggDocumentVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        DocumentVO parent = vo.getParentVO();
        if (parent.getPk_document() == null) {
            throw new BusinessException("主键不能为空");
        }
        DocumentVO dbHead = queryHeadByPk(parent.getPk_document());
        if (dbHead == null) {
            throw new BusinessException("单证不存在");
        }
        if (dbHead.getDoc_status() == null || dbHead.getDoc_status() != 1) {
            throw new BusinessException("只有已审核状态的单证才能弃审");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        dbHead.setDoc_status(0);
        dbHead.setReviewer(null);
        dbHead.setReview_time(null);
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
        } catch (Exception e) {
            throw new BusinessException("弃审失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_document());
    }

    @Override
    public AggDocumentVO remindExtend(String pkExhibitList) throws BusinessException {
        if (pkExhibitList == null || pkExhibitList.trim().length() == 0) {
            throw new BusinessException("清单主键不能为空");
        }
        DocumentVO doc = queryByExhibitListInternal(pkExhibitList);
        if (doc == null) {
            throw new BusinessException("清单对应的单证不存在");
        }
        UFDate validTo = doc.getValid_to();
        UFDate today = new UFDate();
        if (validTo == null) {
            throw new BusinessException("单证有效期未设置");
        }
        long diffDays = (validTo.getTime() - today.getTime()) / (24 * 60 * 60 * 1000);
        if (diffDays < 0) {
            doc.setDoc_status(3);
        } else if (diffDays < 30) {
            doc.setDoc_status(2);
        } else {
            throw new BusinessException("单证有效期超过30天，无需提醒");
        }
        fillSysFields(doc, false);
        try {
            getDao().updateVO(doc);
        } catch (Exception e) {
            throw new BusinessException("更新状态失败：" + e.getMessage());
        }
        return queryByPk(doc.getPk_document());
    }

    @Override
    public List<String> getExpiringList() throws BusinessException {
        UFDate today = new UFDate();
        UFDate limitDate = UFDate.valueOf(today.getYear(), today.getMonth(), today.getDay());
        limitDate = limitDate.addDay(30);
        String sql = "SELECT pk_document FROM ata_document WHERE dr = 0 AND doc_status != 3 AND valid_to IS NOT NULL AND valid_to <= ?";
        SQLParameter param = new SQLParameter();
        param.addParam(limitDate.toDate());
        try {
            List list = getDao().executeQuery(sql, param, new ColumnListProcessor());
            List<String> result = new ArrayList<String>();
            if (list != null) {
                for (Object obj : list) {
                    if (obj != null) {
                        result.add(obj.toString());
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new BusinessException("查询即将到期单证失败：" + e.getMessage());
        }
    }

    private DocumentVO queryHeadByPk(String pk) throws BusinessException {
        try {
            return (DocumentVO) getDao().retrieveByPK(DocumentVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    private DocumentVO queryByExhibitListInternal(String pkList) throws BusinessException {
        String sql = "SELECT * FROM ata_document WHERE pk_exhibit_list = ? AND dr = 0";
        SQLParameter param = new SQLParameter();
        param.addParam(pkList);
        try {
            List list = getDao().executeQuery(sql, param, new BeanListProcessor(DocumentVO.class));
            if (list == null || list.isEmpty()) {
                return null;
            }
            return (DocumentVO) list.get(0);
        } catch (Exception e) {
            throw new BusinessException("查询单证失败：" + e.getMessage());
        }
    }

    private ExhibitVO[] queryExhibitsByListPk(String pkList) throws BusinessException {
        String sql = "SELECT * FROM ata_exhibit WHERE pk_exhibit_list = ? AND dr = 0";
        SQLParameter param = new SQLParameter();
        param.addParam(pkList);
        try {
            List list = getDao().executeQuery(sql, param, new BeanListProcessor(ExhibitVO.class));
            if (list == null || list.isEmpty()) {
                return null;
            }
            return (ExhibitVO[]) list.toArray(new ExhibitVO[0]);
        } catch (Exception e) {
            throw new BusinessException("查询展品失败：" + e.getMessage());
        }
    }

    @Override
    public AggDocumentVO queryByPk(String pk) throws BusinessException {
        if (pk == null || pk.trim().length() == 0) {
            throw new BusinessException("主键不能为空");
        }
        DocumentVO head = queryHeadByPk(pk);
        if (head == null) {
            return null;
        }
        AggDocumentVO agg = new AggDocumentVO();
        agg.setParentVO(head);
        return agg;
    }

    @Override
    public AggDocumentVO queryByExhibitList(String pkExhibitList) throws BusinessException {
        if (pkExhibitList == null || pkExhibitList.trim().length() == 0) {
            throw new BusinessException("清单主键不能为空");
        }
        DocumentVO head = queryByExhibitListInternal(pkExhibitList);
        if (head == null) {
            return null;
        }
        AggDocumentVO agg = new AggDocumentVO();
        agg.setParentVO(head);
        return agg;
    }
}
