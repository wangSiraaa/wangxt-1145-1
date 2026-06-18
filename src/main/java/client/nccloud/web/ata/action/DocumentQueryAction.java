package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.AggDocumentVO;
import nc.vo.pub.BusinessException;
import nc.jdbc.framework.SQLParameter;
import java.util.List;
import java.util.Map;

public class DocumentQueryAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Map<String, Object> params = JsonFactory.create().fromJson(json, Map.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            String pkDocument = (String) params.get("pk_document");
            if (pkDocument != null && !pkDocument.isEmpty()) {
                AggDocumentVO result = service.queryByPk(pkDocument);
                return result;
            }
            String pkExhibitList = (String) params.get("pk_exhibit_list");
            if (pkExhibitList != null && !pkExhibitList.isEmpty()) {
                AggDocumentVO result = service.queryByExhibitList(pkExhibitList);
                return result;
            }
            StringBuilder condition = new StringBuilder(" dr = 0 ");
            SQLParameter sqlParam = new SQLParameter();
            int paramIndex = 0;
            if (params.get("pk_org") != null && !((String) params.get("pk_org")).isEmpty()) {
                condition.append(" and pk_org = ? ");
                sqlParam.addParam(paramIndex++, params.get("pk_org"));
            }
            if (params.get("doc_status") != null) {
                condition.append(" and doc_status = ? ");
                sqlParam.addParam(paramIndex++, params.get("doc_status"));
            }
            if (params.get("list_status") != null) {
                condition.append(" and pk_exhibit_list in (select pk_exhibit_list from ata_exhibit_list where dr = 0 and list_status = ?) ");
                sqlParam.addParam(paramIndex++, params.get("list_status"));
            }
            List<AggDocumentVO> result = new java.util.ArrayList<>();
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
