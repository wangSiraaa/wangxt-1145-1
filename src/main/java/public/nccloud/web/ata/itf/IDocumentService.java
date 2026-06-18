package nccloud.web.ata.itf;

import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import java.util.List;
import nc.jdbc.framework.SQLParameter;

public interface IDocumentService {

    AggDocumentVO save(AggDocumentVO vo) throws BusinessException;

    AggDocumentVO audit(AggDocumentVO vo) throws BusinessException;

    AggDocumentVO unAudit(AggDocumentVO vo) throws BusinessException;

    AggDocumentVO remindExtend(String pkExhibitList) throws BusinessException;

    AggDocumentVO queryByPk(String pk) throws BusinessException;

    AggDocumentVO queryByExhibitList(String pkExhibitList) throws BusinessException;

    List<String> getExpiringList() throws BusinessException;
}
