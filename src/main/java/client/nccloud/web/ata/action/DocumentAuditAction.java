package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.AggDocumentVO;
import nc.vo.pub.BusinessException;
import java.util.*;

public class DocumentAuditAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            List<String> pks = extractPks(obj);
            if (pks != null && !pks.isEmpty()) {
                for (String pk : pks) {
                    AggDocumentVO aggVO = service.queryByPk(pk);
                    if (aggVO != null) {
                        service.audit(aggVO);
                    }
                }
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", true);
                return result;
            }
            AggDocumentVO aggVO;
            if (obj instanceof Map) {
                Map params = (Map) obj;
                String pk = (String) params.get("pk_document");
                if (pk == null || pk.isEmpty()) {
                    pk = (String) params.get("id");
                }
                if (pk != null && !pk.isEmpty()) {
                    aggVO = service.queryByPk(pk);
                } else {
                    aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
                }
            } else {
                aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
            }
            AggDocumentVO result = service.audit(aggVO);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }

    private List<String> extractPks(Object obj) {
        if (!(obj instanceof Map)) {
            return null;
        }
        Map params = (Map) obj;
        Object pksObj = params.get("pks");
        if (pksObj instanceof List) {
            List list = (List) pksObj;
            List<String> result = new ArrayList<String>();
            for (Object o : list) {
                if (o != null) {
                    result.add(String.valueOf(o));
                }
            }
            return result;
        }
        if (pksObj instanceof String[]) {
            return Arrays.asList((String[]) pksObj);
        }
        return null;
    }
}
