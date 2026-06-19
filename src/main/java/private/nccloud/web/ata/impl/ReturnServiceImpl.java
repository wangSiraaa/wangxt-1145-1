package nccloud.web.ata.impl;

import nccloud.web.ata.itf.IReturnService;
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

public class ReturnServiceImpl implements IReturnService {

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
        if (vo.getAttributeValue(ReturnVO.PK_GROUP) == null) {
            vo.setAttributeValue(ReturnVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(ReturnVO.PK_ORG) == null) {
            vo.setAttributeValue(ReturnVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(ReturnVO.CREATOR, getUserId());
            vo.setAttributeValue(ReturnVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(ReturnVO.MODIFIER, getUserId());
        vo.setAttributeValue(ReturnVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(ReturnVO.DR) == null) {
            vo.setAttributeValue(ReturnVO.DR, 0);
        }
    }

    private void fillSysFieldsForDetail(SuperVO vo, boolean isInsert, String parentPkField, String parentPk) {
        if (parentPk != null) {
            vo.setAttributeValue(parentPkField, parentPk);
        }
        if (vo.getAttributeValue(ReturnDetailVO.PK_GROUP) == null) {
            vo.setAttributeValue(ReturnDetailVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(ReturnDetailVO.PK_ORG) == null) {
            vo.setAttributeValue(ReturnDetailVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(ReturnDetailVO.CREATOR, getUserId());
            vo.setAttributeValue(ReturnDetailVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(ReturnDetailVO.MODIFIER, getUserId());
        vo.setAttributeValue(ReturnDetailVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(ReturnDetailVO.DR) == null) {
            vo.setAttributeValue(ReturnDetailVO.DR, 0);
        }
    }

    private void fillSysFieldsForDiff(DiffVO vo, boolean isInsert) {
        if (vo.getPk_group() == null || vo.getPk_group().trim().length() == 0) {
            vo.setPk_group(getPkGroup());
        }
        if (vo.getPk_org() == null || vo.getPk_org().trim().length() == 0) {
            vo.setPk_org(getPkOrg());
        }
        if (isInsert) {
            vo.setCreator(getUserId());
            vo.setCreationtime(getNow());
        }
        vo.setModifier(getUserId());
        vo.setModifiedtime(getNow());
        if (vo.getDr() == null) {
            vo.setDr(0);
        }
    }

    private void fillSysFieldsForExhibit(ExhibitVO vo) {
        vo.setModifier(getUserId());
        vo.setModifiedtime(getNow());
    }

    private String generateReturnNo() throws BusinessException {
        String prefix = "RT" + new UFDate().toString().replaceAll("-", "");
        String sql = "SELECT MAX(return_no) FROM ata_return WHERE return_no LIKE ? AND dr = 0";
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

    private String generateDiffNo() throws BusinessException {
        String prefix = "DF" + new UFDate().toString().replaceAll("-", "");
        String sql = "SELECT MAX(diff_no) FROM ata_diff WHERE diff_no LIKE ? AND dr = 0";
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
    public AggReturnVO save(AggReturnVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        ReturnVO parent = vo.getParentVO();
        if (parent == null) {
            throw new BusinessException("表头不能为空");
        }
        boolean isInsert = (parent.getPk_return() == null || parent.getPk_return().trim().length() == 0);
        if (isInsert) {
            parent.setPk_return(getDao().getOID());
            if (parent.getReturn_no() == null || parent.getReturn_no().trim().length() == 0) {
                parent.setReturn_no(generateReturnNo());
            }
            if (parent.getReturn_status() == null) {
                parent.setReturn_status(0);
            }
        } else {
            ReturnVO old = queryHeadByPk(parent.getPk_return());
            if (old == null) {
                throw new BusinessException("数据不存在，无法更新");
            }
            if (parent.getTs() == null || !parent.getTs().equals(old.getTs())) {
                throw new BusinessException("数据已被他人修改，请刷新后重试");
            }
        }
        fillSysFields(parent, isInsert);
        SuperVO[] children = vo.getChildrenVO();
        List<ReturnDetailVO> insertList = new ArrayList<ReturnDetailVO>();
        List<ReturnDetailVO> updateList = new ArrayList<ReturnDetailVO>();
        List<String> deletePks = new ArrayList<String>();
        if (children != null) {
            for (SuperVO child : children) {
                ReturnDetailVO rd = (ReturnDetailVO) child;
                fillSysFieldsForDetail(rd, (rd.getPk_return_detail() == null || rd.getPk_return_detail().trim().length() == 0), ReturnDetailVO.PK_RETURN, parent.getPk_return());
                VOStatus status = rd.getStatus();
                if (status == VOStatus.NEW) {
                    if (rd.getPk_return_detail() == null || rd.getPk_return_detail().trim().length() == 0) {
                        rd.setPk_return_detail(getDao().getOID());
                    }
                    insertList.add(rd);
                } else if (status == VOStatus.UPDATED) {
                    updateList.add(rd);
                } else if (status == VOStatus.DELETED) {
                    deletePks.add(rd.getPk_return_detail());
                }
            }
        }
        try {
            if (isInsert) {
                getDao().insertVO(parent);
            } else {
                getDao().updateVO(parent);
            }
            for (ReturnDetailVO rd : insertList) {
                getDao().insertVO(rd);
            }
            for (ReturnDetailVO rd : updateList) {
                getDao().updateVO(rd);
            }
            for (String pk : deletePks) {
                getDao().deleteByPK(ReturnDetailVO.class, pk);
            }
        } catch (Exception e) {
            throw new BusinessException("保存失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_return());
    }

    @Override
    public AggReturnVO register(AggReturnVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ReturnVO parent = vo.getParentVO();
        if (parent.getPk_return() == null) {
            throw new BusinessException("主键不能为空");
        }
        ReturnVO dbHead = queryHeadByPk(parent.getPk_return());
        if (dbHead == null) {
            throw new BusinessException("回运单不存在");
        }
        if (dbHead.getReturn_status() != null && dbHead.getReturn_status() != 0) {
            throw new BusinessException("只有草稿状态的回运单才能登记");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        String pkShipment = dbHead.getPk_shipment();
        if (pkShipment == null || pkShipment.trim().length() == 0) {
            throw new BusinessException("回运单未关联出运单");
        }
        ShipmentVO shipment = queryShipmentByPk(pkShipment);
        if (shipment == null) {
            throw new BusinessException("关联出运单不存在");
        }
        if (shipment.getShipment_status() == null || shipment.getShipment_status() != 1) {
            throw new BusinessException("关联出运单状态必须为已登记");
        }
        ShipmentDetailVO[] shipmentDetails = queryShipmentDetailsByShipmentPk(pkShipment);
        Map<String, ShipmentDetailVO> shipmentDetailMap = new HashMap<String, ShipmentDetailVO>();
        if (shipmentDetails != null) {
            for (ShipmentDetailVO sd : shipmentDetails) {
                shipmentDetailMap.put(sd.getPk_exhibit(), sd);
            }
        }
        ReturnDetailVO[] details = queryDetailsByReturnPk(parent.getPk_return());
        if (details == null || details.length == 0) {
            throw new BusinessException("回运单无明细，不能登记");
        }
        List<DiffVO> diffList = new ArrayList<DiffVO>();
        List<ExhibitVO> exhibitsToUpdate = new ArrayList<ExhibitVO>();
        boolean hasDiff = false;
        String pkList = dbHead.getPk_exhibit_list();
        int returnDeadlineBase = 180;
        DocumentVO doc = queryDocumentByListPk(pkList);
        if (doc != null && doc.getReturn_deadline_base() != null) {
            returnDeadlineBase = doc.getReturn_deadline_base();
        }
        UFDate shipmentDate = shipment.getShipment_date();
        for (ReturnDetailVO rd : details) {
            String pkExhibit = rd.getPk_exhibit();
            if (pkExhibit == null || pkExhibit.trim().length() == 0) {
                throw new BusinessException("回运明细[" + rd.getExhibit_name() + "]未关联展品");
            }
            ExhibitVO exhibit = queryExhibitByPk(pkExhibit);
            if (exhibit == null) {
                throw new BusinessException("展品[" + rd.getExhibit_name() + "]不存在");
            }
            UFDouble shipmentQty = rd.getShipment_qty();
            UFDouble returnQty = rd.getReturn_qty();
            if (shipmentQty == null) {
                ShipmentDetailVO sd = shipmentDetailMap.get(pkExhibit);
                if (sd != null && sd.getShipment_qty() != null) {
                    shipmentQty = sd.getShipment_qty();
                    rd.setShipment_qty(shipmentQty);
                }
            }
            if (shipmentQty == null) {
                throw new BusinessException("展品[" + rd.getExhibit_name() + "]原出运数量缺失");
            }
            if (returnQty == null) {
                returnQty = UFDouble.ZERO_DBL;
            }
            UFDouble diffQty = shipmentQty.sub(returnQty);
            rd.setDiff_qty(diffQty);
            fillSysFieldsForDetail(rd, false, ReturnDetailVO.PK_RETURN, parent.getPk_return());
            if (diffQty.compareTo(UFDouble.ZERO_DBL) != 0) {
                hasDiff = true;
                DiffVO diff = new DiffVO();
                diff.setPk_diff(getDao().getOID());
                diff.setDiff_no(generateDiffNo());
                diff.setPk_exhibit_list(pkList);
                diff.setPk_return(dbHead.getPk_return());
                diff.setPk_exhibit(pkExhibit);
                diff.setExhibit_code(rd.getExhibit_code());
                diff.setExhibit_name(rd.getExhibit_name());
                diff.setShipment_qty(shipmentQty);
                diff.setReturn_qty(returnQty);
                diff.setDiff_qty(diffQty);
                UFDate returnDeadline = shipmentDate;
                if (returnDeadline == null) {
                    returnDeadline = new UFDate();
                }
                returnDeadline = returnDeadline.addDay(returnDeadlineBase);
                diff.setReturn_deadline(returnDeadline);
                diff.setReturn_reminded(0);
                UFDate today = new UFDate();
                if (today.after(returnDeadline)) {
                    diff.setDiff_type(5);
                    diff.setDiff_status(0);
                } else {
                    diff.setDiff_type(5);
                    diff.setDiff_status(0);
                }
                fillSysFieldsForDiff(diff, true);
                diffList.add(diff);
                exhibit.setExhibit_status(3);
            } else {
                exhibit.setExhibit_status(2);
            }
            fillSysFieldsForExhibit(exhibit);
            exhibitsToUpdate.add(exhibit);
        }
        if (hasDiff) {
            dbHead.setReturn_status(2);
        } else {
            dbHead.setReturn_status(1);
        }
        dbHead.setRegistrant(getUserId());
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
            for (ReturnDetailVO rd : details) {
                getDao().updateVO(rd);
            }
            for (DiffVO diff : diffList) {
                getDao().insertVO(diff);
            }
            for (ExhibitVO ex : exhibitsToUpdate) {
                getDao().updateVO(ex);
            }
        } catch (Exception e) {
            throw new BusinessException("登记回运失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_return());
    }

    @Override
    public AggReturnVO cancelRegister(AggReturnVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ReturnVO parent = vo.getParentVO();
        if (parent.getPk_return() == null) {
            throw new BusinessException("主键不能为空");
        }
        ReturnVO dbHead = queryHeadByPk(parent.getPk_return());
        if (dbHead == null) {
            throw new BusinessException("回运单不存在");
        }
        if (dbHead.getReturn_status() == null || (dbHead.getReturn_status() != 1 && dbHead.getReturn_status() != 2)) {
            throw new BusinessException("只有已登记或有差异状态的回运单才能取消登记");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        ReturnDetailVO[] details = queryDetailsByReturnPk(parent.getPk_return());
        List<ExhibitVO> exhibitsToUpdate = new ArrayList<ExhibitVO>();
        if (details != null && details.length > 0) {
            for (ReturnDetailVO rd : details) {
                String pkExhibit = rd.getPk_exhibit();
                if (pkExhibit != null && pkExhibit.trim().length() > 0) {
                    ExhibitVO exhibit = queryExhibitByPk(pkExhibit);
                    if (exhibit != null) {
                        exhibit.setExhibit_status(1);
                        fillSysFieldsForExhibit(exhibit);
                        exhibitsToUpdate.add(exhibit);
                    }
                }
            }
        }
        String deleteDiffSql = "DELETE FROM ata_diff WHERE pk_return = ?";
        SQLParameter diffParam = new SQLParameter();
        diffParam.addParam(parent.getPk_return());
        dbHead.setReturn_status(0);
        dbHead.setRegistrant(null);
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
            getDao().executeUpdate(deleteDiffSql, diffParam);
            for (ExhibitVO ex : exhibitsToUpdate) {
                getDao().updateVO(ex);
            }
        } catch (Exception e) {
            throw new BusinessException("取消登记失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_return());
    }

    private ReturnVO queryHeadByPk(String pk) throws BusinessException {
        try {
            return (ReturnVO) getDao().retrieveByPK(ReturnVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    private ReturnDetailVO[] queryDetailsByReturnPk(String pkReturn) throws BusinessException {
        String sql = "SELECT * FROM ata_return_detail WHERE pk_return = ? AND dr = 0";
        SQLParameter param = new SQLParameter();
        param.addParam(pkReturn);
        try {
            List list = getDao().executeQuery(sql, param, new BeanListProcessor(ReturnDetailVO.class));
            if (list == null || list.isEmpty()) {
                return null;
            }
            return (ReturnDetailVO[]) list.toArray(new ReturnDetailVO[0]);
        } catch (Exception e) {
            throw new BusinessException("查询回运明细失败：" + e.getMessage());
        }
    }

    private ShipmentVO queryShipmentByPk(String pk) throws BusinessException {
        try {
            return (ShipmentVO) getDao().retrieveByPK(ShipmentVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询出运单失败：" + e.getMessage());
        }
    }

    private ShipmentDetailVO[] queryShipmentDetailsByShipmentPk(String pkShipment) throws BusinessException {
        String sql = "SELECT * FROM ata_shipment_detail WHERE pk_shipment = ? AND dr = 0";
        SQLParameter param = new SQLParameter();
        param.addParam(pkShipment);
        try {
            List list = getDao().executeQuery(sql, param, new BeanListProcessor(ShipmentDetailVO.class));
            if (list == null || list.isEmpty()) {
                return null;
            }
            return (ShipmentDetailVO[]) list.toArray(new ShipmentDetailVO[0]);
        } catch (Exception e) {
            throw new BusinessException("查询出运明细失败：" + e.getMessage());
        }
    }

    private ExhibitVO queryExhibitByPk(String pk) throws BusinessException {
        try {
            return (ExhibitVO) getDao().retrieveByPK(ExhibitVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询展品失败：" + e.getMessage());
        }
    }

    private DocumentVO queryDocumentByListPk(String pkList) throws BusinessException {
        if (pkList == null || pkList.trim().length() == 0) {
            return null;
        }
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

    @Override
    public AggReturnVO queryByPk(String pk) throws BusinessException {
        if (pk == null || pk.trim().length() == 0) {
            throw new BusinessException("主键不能为空");
        }
        ReturnVO head = queryHeadByPk(pk);
        if (head == null) {
            return null;
        }
        ReturnDetailVO[] children = queryDetailsByReturnPk(pk);
        AggReturnVO agg = new AggReturnVO();
        agg.setParentVO(head);
        if (children != null && children.length > 0) {
            agg.setChildrenVO(children);
        }
        return agg;
    }

    @Override
    public List<AggReturnVO> queryByExhibitList(String pkExhibitList) throws BusinessException {
        if (pkExhibitList == null || pkExhibitList.trim().length() == 0) {
            throw new BusinessException("清单主键不能为空");
        }
        String sql = "SELECT * FROM ata_return WHERE pk_exhibit_list = ? AND dr = 0 ORDER BY creationtime DESC";
        SQLParameter param = new SQLParameter();
        param.addParam(pkExhibitList);
        try {
            List<ReturnVO> heads = (List<ReturnVO>) getDao().executeQuery(sql, param, new BeanListProcessor(ReturnVO.class));
            List<AggReturnVO> result = new ArrayList<AggReturnVO>();
            if (heads != null) {
                for (ReturnVO head : heads) {
                    AggReturnVO agg = new AggReturnVO();
                    agg.setParentVO(head);
                    ReturnDetailVO[] children = queryDetailsByReturnPk(head.getPk_return());
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

    @Override
    public List<AggReturnVO> queryByCondition(String pkExhibitList, Integer returnStatus, String pkOrg) throws BusinessException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ata_return WHERE dr = 0");
        SQLParameter param = new SQLParameter();
        if (pkExhibitList != null && pkExhibitList.trim().length() > 0) {
            sql.append(" AND pk_exhibit_list = ?");
            param.addParam(pkExhibitList);
        }
        if (returnStatus != null) {
            sql.append(" AND return_status = ?");
            param.addParam(returnStatus);
        }
        if (pkOrg != null && pkOrg.trim().length() > 0) {
            sql.append(" AND pk_org = ?");
            param.addParam(pkOrg);
        }
        sql.append(" ORDER BY creationtime DESC");
        try {
            List<ReturnVO> heads = (List<ReturnVO>) getDao().executeQuery(sql.toString(), param, new BeanListProcessor(ReturnVO.class));
            List<AggReturnVO> result = new ArrayList<AggReturnVO>();
            if (heads != null) {
                for (ReturnVO head : heads) {
                    AggReturnVO agg = new AggReturnVO();
                    agg.setParentVO(head);
                    ReturnDetailVO[] children = queryDetailsByReturnPk(head.getPk_return());
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
}
