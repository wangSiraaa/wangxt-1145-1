package nccloud.web.ata.itf;

import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import java.util.List;
import nc.jdbc.framework.SQLParameter;

public interface IDiffService {

    DiffVO save(DiffVO vo) throws BusinessException;

    DiffVO process(DiffVO vo) throws BusinessException;

    DiffVO close(DiffVO vo) throws BusinessException;

    DiffVO queryByPk(String pk) throws BusinessException;

    List<DiffVO> queryByExhibitList(String pkExhibitList) throws BusinessException;

    List<DiffVO> queryByStatus(Integer status) throws BusinessException;

    List<DiffVO> queryByCondition(String pkExhibitList, Integer diffStatus, Integer diffType, String pkOrg) throws BusinessException;
}
