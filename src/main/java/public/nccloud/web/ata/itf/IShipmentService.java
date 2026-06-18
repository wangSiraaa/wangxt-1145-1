package nccloud.web.ata.itf;

import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import java.util.List;
import nc.jdbc.framework.SQLParameter;

public interface IShipmentService {

    AggShipmentVO save(AggShipmentVO vo) throws BusinessException;

    AggShipmentVO register(AggShipmentVO vo) throws BusinessException;

    AggShipmentVO cancelRegister(AggShipmentVO vo) throws BusinessException;

    AggShipmentVO queryByPk(String pk) throws BusinessException;

    List<AggShipmentVO> queryByExhibitList(String pkExhibitList) throws BusinessException;

    List<AggShipmentVO> queryByCondition(String pkExhibitList, Integer shipmentStatus, String pkOrg) throws BusinessException;
}
