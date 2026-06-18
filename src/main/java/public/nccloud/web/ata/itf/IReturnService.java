package nccloud.web.ata.itf;

import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import java.util.List;
import nc.jdbc.framework.SQLParameter;

public interface IReturnService {

    AggReturnVO save(AggReturnVO vo) throws BusinessException;

    AggReturnVO register(AggReturnVO vo) throws BusinessException;

    AggReturnVO cancelRegister(AggReturnVO vo) throws BusinessException;

    AggReturnVO queryByPk(String pk) throws BusinessException;

    List<AggReturnVO> queryByExhibitList(String pkExhibitList) throws BusinessException;

    List<AggReturnVO> queryByCondition(String pkExhibitList, Integer returnStatus, String pkOrg) throws BusinessException;
}
