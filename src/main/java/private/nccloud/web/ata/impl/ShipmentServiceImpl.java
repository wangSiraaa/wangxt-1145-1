package nccloud.web.ata.impl;

import nccloud.web.ata.itf.IShipmentService;
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

public class ShipmentServiceImpl implements IShipmentService {

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
        if (vo.getAttributeValue(ShipmentVO.PK_GROUP) == null) {
            vo.setAttributeValue(ShipmentVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(ShipmentVO.PK_ORG) == null) {
            vo.setAttributeValue(ShipmentVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(ShipmentVO.CREATOR, getUserId());
            vo.setAttributeValue(ShipmentVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(ShipmentVO.MODIFIER, getUserId());
        vo.setAttributeValue(ShipmentVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(ShipmentVO.DR) == null) {
            vo.setAttributeValue(ShipmentVO.DR, 0);
        }
    }

    private void fillSysFieldsForDetail(SuperVO vo, boolean isInsert, String parentPkField, String parentPk) {
        if (parentPk != null) {
            vo.setAttributeValue(parentPkField, parentPk);
        }
        if (vo.getAttributeValue(ShipmentDetailVO.PK_GROUP) == null) {
            vo.setAttributeValue(ShipmentDetailVO.PK_GROUP, getPkGroup());
        }
        if (vo.getAttributeValue(ShipmentDetailVO.PK_ORG) == null) {
            vo.setAttributeValue(ShipmentDetailVO.PK_ORG, getPkOrg());
        }
        if (isInsert) {
            vo.setAttributeValue(ShipmentDetailVO.CREATOR, getUserId());
            vo.setAttributeValue(ShipmentDetailVO.CREATIONTIME, getNow());
        }
        vo.setAttributeValue(ShipmentDetailVO.MODIFIER, getUserId());
        vo.setAttributeValue(ShipmentDetailVO.MODIFIEDTIME, getNow());
        if (vo.getAttributeValue(ShipmentDetailVO.DR) == null) {
            vo.setAttributeValue(ShipmentDetailVO.DR, 0);
        }
    }

    private String generateShipmentNo() throws BusinessException {
        String prefix = "SH" + new UFDate().toString().replaceAll("-", "");
        String sql = "SELECT MAX(shipment_no) FROM ata_shipment WHERE shipment_no LIKE ? AND dr = 0";
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
    public AggShipmentVO save(AggShipmentVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        ShipmentVO parent = vo.getParentVO();
        if (parent == null) {
            throw new BusinessException("表头不能为空");
        }
        boolean isInsert = (parent.getPk_shipment() == null || parent.getPk_shipment().trim().length() == 0);
        if (isInsert) {
            parent.setPk_shipment(getDao().getOID());
            if (parent.getShipment_no() == null || parent.getShipment_no().trim().length() == 0) {
                parent.setShipment_no(generateShipmentNo());
            }
            if (parent.getShipment_status() == null) {
                parent.setShipment_status(0);
            }
        } else {
            ShipmentVO old = queryHeadByPk(parent.getPk_shipment());
            if (old == null) {
                throw new BusinessException("数据不存在，无法更新");
            }
            if (parent.getTs() == null || !parent.getTs().equals(old.getTs())) {
                throw new BusinessException("数据已被他人修改，请刷新后重试");
            }
        }
        fillSysFields(parent, isInsert);
        SuperVO[] children = vo.getChildrenVO();
        List<ShipmentDetailVO> insertList = new ArrayList<ShipmentDetailVO>();
        List<ShipmentDetailVO> updateList = new ArrayList<ShipmentDetailVO>();
        List<String> deletePks = new ArrayList<String>();
        if (children != null) {
            for (SuperVO child : children) {
                ShipmentDetailVO sd = (ShipmentDetailVO) child;
                fillSysFieldsForDetail(sd, (sd.getPk_shipment_detail() == null || sd.getPk_shipment_detail().trim().length() == 0), ShipmentDetailVO.PK_SHIPMENT, parent.getPk_shipment());
                VOStatus status = sd.getStatus();
                if (status == VOStatus.NEW) {
                    if (sd.getPk_shipment_detail() == null || sd.getPk_shipment_detail().trim().length() == 0) {
                        sd.setPk_shipment_detail(getDao().getOID());
                    }
                    insertList.add(sd);
                } else if (status == VOStatus.UPDATED) {
                    updateList.add(sd);
                } else if (status == VOStatus.DELETED) {
                    deletePks.add(sd.getPk_shipment_detail());
                }
            }
        }
        try {
            if (isInsert) {
                getDao().insertVO(parent);
            } else {
                getDao().updateVO(parent);
            }
            for (ShipmentDetailVO sd : insertList) {
                getDao().insertVO(sd);
            }
            for (ShipmentDetailVO sd : updateList) {
                getDao().updateVO(sd);
            }
            for (String pk : deletePks) {
                getDao().deleteByPK(ShipmentDetailVO.class, pk);
            }
        } catch (Exception e) {
            throw new BusinessException("保存失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_shipment());
    }

    @Override
    public AggShipmentVO register(AggShipmentVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ShipmentVO parent = vo.getParentVO();
        if (parent.getPk_shipment() == null) {
            throw new BusinessException("主键不能为空");
        }
        ShipmentVO dbHead = queryHeadByPk(parent.getPk_shipment());
        if (dbHead == null) {
            throw new BusinessException("出运单不存在");
        }
        if (dbHead.getShipment_status() != null && dbHead.getShipment_status() != 0) {
            throw new BusinessException("只有草稿状态的出运单才能登记");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        String pkList = dbHead.getPk_exhibit_list();
        if (pkList == null || pkList.trim().length() == 0) {
            throw new BusinessException("出运单未关联清单");
        }
        ExhibitListVO listVO = queryExhibitListByPk(pkList);
        if (listVO == null) {
            throw new BusinessException("关联清单不存在");
        }
        if (listVO.getList_status() == null || listVO.getList_status() != 3) {
            throw new BusinessException("清单状态必须为审核通过才能出运");
        }
        ShipmentDetailVO[] details = queryDetailsByShipmentPk(parent.getPk_shipment());
        if (details == null || details.length == 0) {
            throw new BusinessException("出运单无明细，不能登记");
        }
        List<ExhibitVO> exhibitsToUpdate = new ArrayList<ExhibitVO>();
        for (ShipmentDetailVO sd : details) {
            String pkExhibit = sd.getPk_exhibit();
            if (pkExhibit == null || pkExhibit.trim().length() == 0) {
                throw new BusinessException("出运明细[" + sd.getExhibit_name() + "]未关联展品");
            }
            ExhibitVO exhibit = queryExhibitByPk(pkExhibit);
            if (exhibit == null) {
                throw new BusinessException("展品[" + sd.getExhibit_name() + "]不存在");
            }
            if (sd.getSerial_verified() == null || sd.getSerial_verified() != 1) {
                throw new BusinessException("展品[" + sd.getExhibit_name() + "]序列号未验证通过，不能出运");
            }
            UFDouble shipmentQty = sd.getShipment_qty();
            if (shipmentQty == null || shipmentQty.doubleValue() <= 0) {
                throw new BusinessException("展品[" + sd.getExhibit_name() + "]出运数量必须大于0");
            }
            UFDouble totalQty = exhibit.getQuantity();
            UFDouble shippedQty = exhibit.getShipped_qty() == null ? new UFDouble(0) : exhibit.getShipped_qty();
            UFDouble remainQty = totalQty.subtract(shippedQty);
            if (shipmentQty.doubleValue() > remainQty.doubleValue()) {
                throw new BusinessException("展品[" + sd.getExhibit_name() + "]本次出运数量[" + shipmentQty + "]超过剩余可出运数量[" + remainQty + "]，总数量[" + totalQty + "]，已出运[" + shippedQty + "]");
            }
            UFDouble newShippedQty = shippedQty.add(shipmentQty);
            exhibit.setShipped_qty(newShippedQty);
            if (newShippedQty.doubleValue() >= totalQty.doubleValue()) {
                exhibit.setExhibit_status(1);
            } else {
                exhibit.setExhibit_status(0);
            }
            fillSysFieldsForExhibit(exhibit);
            exhibitsToUpdate.add(exhibit);
        }
        dbHead.setShipment_status(1);
        dbHead.setRegistrant(getUserId());
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
            for (ExhibitVO ex : exhibitsToUpdate) {
                getDao().updateVO(ex);
            }
        } catch (Exception e) {
            throw new BusinessException("登记出运失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_shipment());
    }

    @Override
    public AggShipmentVO cancelRegister(AggShipmentVO vo) throws BusinessException {
        if (vo == null || vo.getParentVO() == null) {
            throw new BusinessException("参数不能为空");
        }
        ShipmentVO parent = vo.getParentVO();
        if (parent.getPk_shipment() == null) {
            throw new BusinessException("主键不能为空");
        }
        ShipmentVO dbHead = queryHeadByPk(parent.getPk_shipment());
        if (dbHead == null) {
            throw new BusinessException("出运单不存在");
        }
        if (dbHead.getShipment_status() == null || dbHead.getShipment_status() != 1) {
            throw new BusinessException("只有已登记状态的出运单才能取消登记");
        }
        if (parent.getTs() == null || !parent.getTs().equals(dbHead.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        ShipmentDetailVO[] details = queryDetailsByShipmentPk(parent.getPk_shipment());
        List<ExhibitVO> exhibitsToUpdate = new ArrayList<ExhibitVO>();
        if (details != null && details.length > 0) {
            for (ShipmentDetailVO sd : details) {
                String pkExhibit = sd.getPk_exhibit();
                if (pkExhibit != null && pkExhibit.trim().length() > 0) {
                    ExhibitVO exhibit = queryExhibitByPk(pkExhibit);
                    if (exhibit != null) {
                        UFDouble shipmentQty = sd.getShipment_qty() == null ? new UFDouble(0) : sd.getShipment_qty();
                        UFDouble shippedQty = exhibit.getShipped_qty() == null ? new UFDouble(0) : exhibit.getShipped_qty();
                        UFDouble newShippedQty = shippedQty.subtract(shipmentQty);
                        if (newShippedQty.doubleValue() < 0) {
                            newShippedQty = new UFDouble(0);
                        }
                        exhibit.setShipped_qty(newShippedQty);
                        if (newShippedQty.doubleValue() <= 0) {
                            exhibit.setExhibit_status(0);
                        } else {
                            UFDouble totalQty = exhibit.getQuantity();
                            if (newShippedQty.doubleValue() >= totalQty.doubleValue()) {
                                exhibit.setExhibit_status(1);
                            } else {
                                exhibit.setExhibit_status(0);
                            }
                        }
                        fillSysFieldsForExhibit(exhibit);
                        exhibitsToUpdate.add(exhibit);
                    }
                }
            }
        }
        dbHead.setShipment_status(0);
        dbHead.setRegistrant(null);
        fillSysFields(dbHead, false);
        try {
            getDao().updateVO(dbHead);
            for (ExhibitVO ex : exhibitsToUpdate) {
                getDao().updateVO(ex);
            }
        } catch (Exception e) {
            throw new BusinessException("取消登记失败：" + e.getMessage());
        }
        return queryByPk(parent.getPk_shipment());
    }

    private void fillSysFieldsForExhibit(ExhibitVO vo) {
        vo.setModifier(getUserId());
        vo.setModifiedtime(getNow());
    }

    private ShipmentVO queryHeadByPk(String pk) throws BusinessException {
        try {
            return (ShipmentVO) getDao().retrieveByPK(ShipmentVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    private ShipmentDetailVO[] queryDetailsByShipmentPk(String pkShipment) throws BusinessException {
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

    private ExhibitListVO queryExhibitListByPk(String pk) throws BusinessException {
        try {
            return (ExhibitListVO) getDao().retrieveByPK(ExhibitListVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询清单失败：" + e.getMessage());
        }
    }

    private ExhibitVO queryExhibitByPk(String pk) throws BusinessException {
        try {
            return (ExhibitVO) getDao().retrieveByPK(ExhibitVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询展品失败：" + e.getMessage());
        }
    }

    @Override
    public AggShipmentVO queryByPk(String pk) throws BusinessException {
        if (pk == null || pk.trim().length() == 0) {
            throw new BusinessException("主键不能为空");
        }
        ShipmentVO head = queryHeadByPk(pk);
        if (head == null) {
            return null;
        }
        ShipmentDetailVO[] children = queryDetailsByShipmentPk(pk);
        AggShipmentVO agg = new AggShipmentVO();
        agg.setParentVO(head);
        if (children != null && children.length > 0) {
            agg.setChildrenVO(children);
        }
        return agg;
    }

    @Override
    public List<AggShipmentVO> queryByExhibitList(String pkExhibitList) throws BusinessException {
        if (pkExhibitList == null || pkExhibitList.trim().length() == 0) {
            throw new BusinessException("清单主键不能为空");
        }
        String sql = "SELECT * FROM ata_shipment WHERE pk_exhibit_list = ? AND dr = 0 ORDER BY creationtime DESC";
        SQLParameter param = new SQLParameter();
        param.addParam(pkExhibitList);
        try {
            List<ShipmentVO> heads = (List<ShipmentVO>) getDao().executeQuery(sql, param, new BeanListProcessor(ShipmentVO.class));
            List<AggShipmentVO> result = new ArrayList<AggShipmentVO>();
            if (heads != null) {
                for (ShipmentVO head : heads) {
                    AggShipmentVO agg = new AggShipmentVO();
                    agg.setParentVO(head);
                    ShipmentDetailVO[] children = queryDetailsByShipmentPk(head.getPk_shipment());
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
    public List<AggShipmentVO> queryByCondition(String pkExhibitList, Integer shipmentStatus, String pkOrg) throws BusinessException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ata_shipment WHERE dr = 0");
        SQLParameter param = new SQLParameter();
        if (pkExhibitList != null && pkExhibitList.trim().length() > 0) {
            sql.append(" AND pk_exhibit_list = ?");
            param.addParam(pkExhibitList);
        }
        if (shipmentStatus != null) {
            sql.append(" AND shipment_status = ?");
            param.addParam(shipmentStatus);
        }
        if (pkOrg != null && pkOrg.trim().length() > 0) {
            sql.append(" AND pk_org = ?");
            param.addParam(pkOrg);
        }
        sql.append(" ORDER BY creationtime DESC");
        try {
            List<ShipmentVO> heads = (List<ShipmentVO>) getDao().executeQuery(sql.toString(), param, new BeanListProcessor(ShipmentVO.class));
            List<AggShipmentVO> result = new ArrayList<AggShipmentVO>();
            if (heads != null) {
                for (ShipmentVO head : heads) {
                    AggShipmentVO agg = new AggShipmentVO();
                    agg.setParentVO(head);
                    ShipmentDetailVO[] children = queryDetailsByShipmentPk(head.getPk_shipment());
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
