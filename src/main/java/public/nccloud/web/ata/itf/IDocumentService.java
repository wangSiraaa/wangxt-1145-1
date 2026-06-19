package nccloud.web.ata.itf;

import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import java.util.List;
import java.util.Map;
import nc.jdbc.framework.SQLParameter;

public interface IDocumentService {

    AggDocumentVO save(AggDocumentVO vo) throws BusinessException;

    AggDocumentVO audit(AggDocumentVO vo) throws BusinessException;

    AggDocumentVO unAudit(AggDocumentVO vo) throws BusinessException;

    AggDocumentVO remindExtend(String pkDocument) throws BusinessException;

    AggDocumentVO extend(String pkDocument, UFDate newValidTo, String extendRemark) throws BusinessException;

    AggDocumentVO closeDocument(String pkDocument) throws BusinessException;

    AggDocumentVO queryByPk(String pk) throws BusinessException;

    AggDocumentVO queryByExhibitList(String pkExhibitList) throws BusinessException;

    List<String> getExpiringList() throws BusinessException;

    List<DocumentVO> getExpiringDetails() throws BusinessException;

    List<AggDocumentVO> queryByCondition(Map<String, Object> params) throws BusinessException;

    List<AggDocumentVO> queryByPks(String[] pks) throws BusinessException;

    void deleteByPks(String[] pks) throws BusinessException;
}
