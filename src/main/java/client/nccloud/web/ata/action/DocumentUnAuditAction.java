package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.AggDocumentVO;
import nc.vo.pub.BusinessException;
import java.util.Map;

public class DocumentUnAuditAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            AggDocumentVO aggVO;
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            if (obj instanceof Map) {
                Map params = (Map) obj;
                String pk = (String) params.get("pk_document");
                if (pk != null && !pk.isEmpty()) {
                    aggVO = service.queryByPk(pk);
                } else {
                    aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
                }
            } else {
                aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
            }
            AggDocumentVO result = service.unAudit(aggVO);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
