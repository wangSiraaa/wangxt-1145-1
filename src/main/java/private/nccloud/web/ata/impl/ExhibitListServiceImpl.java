package nccloud.web.ata.impl;

import nccloud.web.ata.itf.IExhibitListService;
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

public class ExhibitListServiceImpl implements IExhibitListService {

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
        if (vo.getAttributeValue(ExhibitListVO.PK_GROUP) == null) {
            vo.setAttributeValue(ExhibitListVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(ExhibitListVO.PK_ORG) == null) {
            vo.setAttributeValue(ExhibitListVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(ExhibitListVO.CREATOR, getUserId());
            vo.setAttributeValue(ExhibitListVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(ExhibitListVO.MODIFIER, getUserId());
        vo.setAttributeValue(ExhibitListVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(ExhibitListVO.DR) == null) {
            vo.setAttributeValue(ExhibitListVO.DR, 0);
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

    private String generateListCode() throws BusinessException {
        String prefix = "EX" + new UFDate().toString().replaceAll("-", "");
        String sql = "SELECT MAX(list_code) FROM ata_exhibit_list WHERE list_code LIKE ? AND dr = 0";
        SQLParameter param = new SQLParameter();
        param.addParam(prefix + "%");
        Object result = getDao().executeQuery(sql, param, new ColumnProcessor());
        int seq = 1;
        if (result != null) {
            String maxCode = result.toString();
            if (maxCode.length() > prefix.length()) {
                String seqStr = maxCode.substring(prefix.length());
                try {
                    seq = Integer.parseInt(seqStr) + 1;
                } catch (NumberFormatException e) {
                    seq = 1;
                }
            }
        }
        return prefix + String.format("%04d", seq);
    }

    @Override
    public AggExhibitListVO save(AggExhibitListVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        ExhibitListVO parent = vo.getParentVO();
        if (parent == null) {
            throw new BusinessException("表头不能为空");
        }
        boolean isInsert = (parent.getPk_exhibit_list() == null || parent.getPk_exhibit_list().trim().length() == 0);
        if (isInsert) {
            parent.setPk_exhibit_list(getDao().getOID());
            if (parent.getList_code() == null || parent.getList_code().trim().length() == 0) {
                parent.setList_code(generateListCode());
            }
            if (parent.getList_status() == null) {
                parent.setList_status(0);
            }
        } else {
            ExhibitListVO old = queryHeadByPk(parent.getPk_exhibit_list());
            if (old == null) {
                throw new BusinessException("数据不存在，无法更新");
            }
            if (parent.getTs() == null || !parent.getTs().equals(old.getTs())) {
                throw new BusinessException("数据已被他人修改，请刷新后重试");
            }
        }
        fillSysFields(parent, isInsert);
        SuperVO[] children = vo.getChildrenVO();
        List<ExhibitVO> insertList = new ArrayList<ExhibitVO>();
        List<ExhibitVO> updateList = new ArrayList<ExhibitVO>();
        List<String> deletePks = new ArrayList<String>();
        if (children != null) {
            for (SuperVO child : children) {
                ExhibitVO ex = (ExhibitVO) child;
                fillSysFieldsForChild(ex, (ex.getPk_exhibit() == null || ex.getPk_exhibit().trim().length() == 0), ExhibitVO.PK_EXHIBIT_LIST, parent.getPk_exhibit_list());
                VOStatus status = ex.getStatus();
                if (status == VOStatus.NEW) {
                    if (ex.getPk_exhibit() == null || ex.getPk_exhibit().trim().length() == 0) {
                        ex.setPk_exhibit(getDao().getOID());
                    }
                    insertList.add(ex);
                } else if (status == VOStatus.UPDATED) {
                    updateList.add(ex);
                } else if (status == VOStatus.DELETED) {
                    deletePks.add(ex.getPk_exhibit());
                }
            }
        }
        try {
            if (isInsert) {
                getDao().insertVO(parent);
            } else {
                getDao().updateVO(parent);
            }
            for (ExhibitVO ex : insertList) {
                getDao().insertVO(ex);
            }
            for (ExhibitVO ex : updateList) {
                getDao().updateVO(ex);
            }
            for (String pk : deletePks) {
                getDao().deleteByPK(ExhibitVO.class, pk);
            }
        } catch (Exception e) {
            throw new BusinessException("保存失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_exhibit_list());
    }

    @Override
    public AggExhibitListVO delete(AggExhibitListVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ExhibitListVO parent = vo.getParentVO();
        if (parent.getPk_exhibit_list() == null) {
            throw new BusinessException("主键不能为空");
        }
        if (parent.getList_status() != null && parent.getList_status() != 0) {
            throw new BusinessException("只有草稿状态的清单才能删除");
        }
        try {
            getDao().deleteByPK(ExhibitListVO.class, parent.getPk_exhibit_list());
            String sql = "DELETE FROM ata_exhibit WHERE pk_exhibit_list = ?";
            SQLParameter param = new SQLParameter();
            param.addParam(parent.getPk_exhibit_list());
            getDao().executeUpdate(sql, param);
        } catch (Exception e) {
            throw new BusinessException("删除失败：" + e.getMessage());
        }
        return vo;
    }

    @Override
    public AggExhibitListVO commit(AggExhibitListVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ExhibitListVO parent = vo.getParentVO();
        if (parent.getPk_exhibit_list() == null) {
            throw new BusinessException("主键不能为空");
        }
        ExhibitListVO dbHead = queryHeadByPk(parent.getPk_exhibit_list());
        if (dbHead == null) {
            throw new BusinessException("清单不存在");
        }
        if (dbHead.getList_status() != null && dbHead.getList_status() != 0) {
            throw new BusinessException("只有草稿状态的清单才能提交");
        }
        ExhibitVO[] exhibits = queryExhibitsByListPk(parent.getPk_exhibit_list());
        if (exhibits == null || exhibits.length == 0) {
            throw new BusinessException("清单项下无展品，不能申报");
        }
        Set<String> serialSet = new HashSet<String>();
        StringBuilder controlledMsg = new StringBuilder();
        int controlledCount = 0;
        for (ExhibitVO ex : exhibits) {
            if (ex.getSerial_no() == null || ex.getSerial_no().trim().length() == 0) {
                throw new BusinessException("展品[" + ex.getExhibit_name() + "]序列号缺失，不能申报");
            }
            String serial = ex.getSerial_no().trim();
            if (serialSet.contains(serial)) {
                throw new BusinessException("序列号[" + serial + "]在清单内重复，请检查");
            }
            serialSet.add(serial);
            if (ex.getValue() == null || ex.getValue().doubleValue() <= 0) {
                throw new BusinessException("展品[" + ex.getExhibit_name() + "]估值不能为空或小于等于0");
            }
            boolean isControlled = checkControlledByHsCode(ex.getHs_code());
            ex.setIs_controlled(isControlled ? 1 : 0);
            if (isControlled) {
                ex.setControl_level("一般管制");
                controlledCount++;
                controlledMsg.append("[").append(ex.getExhibit_name()).append("]");
            }
            if (ex.getShipped_qty() == null) {
                ex.setShipped_qty(new UFDouble(0));
            }
            if (ex.getValue_verified() == null) {
                ex.setValue_verified(0);
            }
            fillSysFieldsForChild(ex, false, ExhibitVO.PK_EXHIBIT_LIST, parent.getPk_exhibit_list());
            try {
                getDao().updateVO(ex);
            } catch (Exception e) {
                throw new BusinessException("更新展品信息失败：" + e.getMessage());
            }
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        dbHead.setList_status(1);
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
        } catch (Exception e) {
            throw new BusinessException("提交失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_exhibit_list());
    }

    @Override
    public AggExhibitListVO unCommit(AggExhibitListVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ExhibitListVO parent = vo.getParentVO();
        if (parent.getPk_exhibit_list() == null) {
            throw new BusinessException("主键不能为空");
        }
        ExhibitListVO dbHead = queryHeadByPk(parent.getPk_exhibit_list());
        if (dbHead == null) {
            throw new BusinessException("清单不存在");
        }
        if (dbHead.getList_status() == null || dbHead.getList_status() != 1) {
            throw new BusinessException("只有已提交状态的清单才能收回");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        dbHead.setList_status(0);
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
        } catch (Exception e) {
            throw new BusinessException("收回失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_exhibit_list());
    }

    private ExhibitListVO queryHeadByPk(String pk) throws BusinessException {
        try {
            return (ExhibitListVO) getDao().retrieveByPK(ExhibitListVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
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
    public AggExhibitListVO queryByPk(String pk) throws BusinessException {
        if (pk == null || pk.trim().length() == 0) {
            throw new BusinessException("主键不能为空");
        }
        ExhibitListVO head = queryHeadByPk(pk);
        if (head == null) {
            return null;
        }
        ExhibitVO[] children = queryExhibitsByListPk(pk);
        AggExhibitListVO agg = new AggExhibitListVO();
        agg.setParentVO(head);
        if (children != null && children.length > 0) {
            agg.setChildrenVO(children);
        }
        return agg;
    }

    @Override
    public List<AggExhibitListVO> queryByCondition(String condition, SQLParameter param) throws BusinessException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ata_exhibit_list WHERE dr = 0");
        if (condition != null && condition.trim().length() > 0) {
            sql.append(" AND ").append(condition);
        }
        sql.append(" ORDER BY creationtime DESC");
        try {
            List<ExhibitListVO> heads = (List<ExhibitListVO>) getDao().executeQuery(sql.toString(), param, new BeanListProcessor(ExhibitListVO.class));
            List<AggExhibitListVO> result = new ArrayList<AggExhibitListVO>();
            if (heads != null) {
                for (ExhibitListVO head : heads) {
                    AggExhibitListVO agg = new AggExhibitListVO();
                    agg.setParentVO(head);
                    ExhibitVO[] children = queryExhibitsByListPk(head.getPk_exhibit_list());
                    if (children != null && children.length > 0) {
                        agg.setChildrenVO(children);
                    }
                    result.add(agg);
                }
            }
            return result;
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    private boolean checkControlledByHsCode(String hsCode) {
        if (hsCode == null || hsCode.trim().length() == 0) {
            return false;
        }
        String code = hsCode.trim().replaceAll("\\.", "");
        if (code.length() < 4) {
            return false;
        }
        String[] controlledPrefixes = {
            "93", "36", "2933", "2934", "30", "0508",
            "0509", "0510", "4403", "4406", "4407",
            "7103", "7104", "7105", "71", "2716",
            "2844", "2845", "9018", "9019", "9020",
            "9021", "9022", "8541", "8542"
        };
        for (String prefix : controlledPrefixes) {
            if (code.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
