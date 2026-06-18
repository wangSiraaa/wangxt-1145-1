package nccloud.web.ata.impl;

import nccloud.web.ata.itf.IDiffService;
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

public class DiffServiceImpl implements IDiffService {

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

    private void fillSysFields(DiffVO vo, boolean isInsert) {
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
    public DiffVO save(DiffVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        boolean isInsert = (vo.getPk_diff() == null || vo.getPk_diff().trim().length() == 0);
        if (isInsert) {
            vo.setPk_diff(getDao().getOID());
            if (vo.getDiff_no() == null || vo.getDiff_no().trim().length() == 0) {
                vo.setDiff_no(generateDiffNo());
            }
            if (vo.getDiff_status() == null) {
                vo.setDiff_status(0);
            }
        } else {
            DiffVO old = queryByPk(vo.getPk_diff());
            if (old == null) {
                throw new BusinessException("数据不存在，无法更新");
            }
            if (vo.getTs() == null || !vo.getTs().equals(old.getTs())) {
                throw new BusinessException("数据已被他人修改，请刷新后重试");
            }
        }
        fillSysFields(vo, isInsert);
        try {
            if (isInsert) {
                getDao().insertVO(vo);
            } else {
                getDao().updateVO(vo);
            }
        } catch (Exception e) {
            throw new BusinessException("保存失败：" + e.getMessage());
        }
        return queryByPk(vo.getPk_diff());
    }

    @Override
    public DiffVO process(DiffVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        if (vo.getPk_diff() == null) {
            throw new BusinessException("主键不能为空");
        }
        DiffVO dbVo = queryByPk(vo.getPk_diff());
        if (dbVo == null) {
            throw new BusinessException("差异单不存在");
        }
        if (vo.getTs() == null || !vo.getTs().equals(dbVo.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        Integer status = dbVo.getDiff_status();
        if (status == null) {
            status = 0;
        }
        if (status == 0) {
            dbVo.setDiff_status(1);
            dbVo.setHandler(getUserId());
            dbVo.setHandle_time(getNow());
        } else if (status == 1) {
            dbVo.setDiff_status(2);
            if (dbVo.getHandler() == null) {
                dbVo.setHandler(getUserId());
            }
            dbVo.setHandle_time(getNow());
        } else if (status == 2) {
            throw new BusinessException("差异单已处理完成，请执行关闭操作");
        } else if (status == 3) {
            throw new BusinessException("差异单已关闭，不能处理");
        }
        fillSysFields(dbVo, false);
        try {
            getDao().updateVO(dbVo);
        } catch (Exception e) {
            throw new BusinessException("处理失败：" + e.getMessage());
        }
        return queryByPk(vo.getPk_diff());
    }

    @Override
    public DiffVO close(DiffVO vo) throws BusinessException {
        if (vo == null) {
            throw new BusinessException("参数不能为空");
        }
        if (vo.getPk_diff() == null) {
            throw new BusinessException("主键不能为空");
        }
        DiffVO dbVo = queryByPk(vo.getPk_diff());
        if (dbVo == null) {
            throw new BusinessException("差异单不存在");
        }
        if (vo.getTs() == null || !vo.getTs().equals(dbVo.getTs())) {
            throw new BusinessException("数据已被他人修改，请刷新后重试");
        }
        Integer status = dbVo.getDiff_status();
        if (status == null || status != 2) {
            throw new BusinessException("只有处理完成状态的差异单才能关闭");
        }
        dbVo.setDiff_status(3);
        fillSysFields(dbVo, false);
        try {
            getDao().updateVO(dbVo);
        } catch (Exception e) {
            throw new BusinessException("关闭失败：" + e.getMessage());
        }
        return queryByPk(vo.getPk_diff());
    }

    @Override
    public DiffVO queryByPk(String pk) throws BusinessException {
        if (pk == null || pk.trim().length() == 0) {
            throw new BusinessException("主键不能为空");
        }
        try {
            return (DiffVO) getDao().retrieveByPK(DiffVO.class, pk);
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    @Override
    public List<DiffVO> queryByExhibitList(String pkExhibitList) throws BusinessException {
        if (pkExhibitList == null || pkExhibitList.trim().length() == 0) {
            throw new BusinessException("清单主键不能为空");
        }
        String sql = "SELECT * FROM ata_diff WHERE pk_exhibit_list = ? AND dr = 0 ORDER BY creationtime DESC";
        SQLParameter param = new SQLParameter();
        param.addParam(pkExhibitList);
        try {
            return (List<DiffVO>) getDao().executeQuery(sql, param, new BeanListProcessor(DiffVO.class));
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    @Override
    public List<DiffVO> queryByStatus(Integer status) throws BusinessException {
        if (status == null) {
            throw new BusinessException("状态不能为空");
        }
        String sql = "SELECT * FROM ata_diff WHERE diff_status = ? AND dr = 0 ORDER BY creationtime DESC";
        SQLParameter param = new SQLParameter();
        param.addParam(status);
        try {
            return (List<DiffVO>) getDao().executeQuery(sql, param, new BeanListProcessor(DiffVO.class));
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }

    @Override
    public List<DiffVO> queryByCondition(String pkExhibitList, Integer diffStatus, Integer diffType, String pkOrg) throws BusinessException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ata_diff WHERE dr = 0");
        SQLParameter param = new SQLParameter();
        if (pkExhibitList != null && pkExhibitList.trim().length() > 0) {
            sql.append(" AND pk_exhibit_list = ?");
            param.addParam(pkExhibitList);
        }
        if (diffStatus != null) {
            sql.append(" AND diff_status = ?");
            param.addParam(diffStatus);
        }
        if (diffType != null) {
            sql.append(" AND diff_type = ?");
            param.addParam(diffType);
        }
        if (pkOrg != null && pkOrg.trim().length() > 0) {
            sql.append(" AND pk_org = ?");
            param.addParam(pkOrg);
        }
        sql.append(" ORDER BY creationtime DESC");
        try {
            return (List<DiffVO>) getDao().executeQuery(sql.toString(), param, new BeanListProcessor(DiffVO.class));
        } catch (Exception e) {
            throw new BusinessException("查询失败：" + e.getMessage());
        }
    }
}
