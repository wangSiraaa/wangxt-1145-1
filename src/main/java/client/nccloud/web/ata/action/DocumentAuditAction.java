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
                String pk = getParamValue(params, "pk_document");
                if (pk == null || pk.isEmpty()) {
                    pk = getParamValue(params, "id");
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

    private String getParamValue(Map<String, Object> params, String key) {
        if (params == null || key == null) {
            return null;
        }
        Object val = params.get(key);
        if (val == null) {
            return null;
        }
        if (val instanceof String) {
            return (String) val;
        }
        if (val instanceof Map) {
            Object v = ((Map) val).get("value");
            return v == null ? null : String.valueOf(v);
        }
        return String.valueOf(val);
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
        if (pksObj instanceof String) {
            String s = (String) pksObj;
            if (s.trim().length() > 0) {
                if (s.contains(",")) {
                    String[] arr = s.split(",");
                    List<String> result = new ArrayList<String>();
                    for (String a : arr) {
                        if (a != null && a.trim().length() > 0) {
                            result.add(a.trim());
                        }
                    }
                    return result;
                } else {
                    return Collections.singletonList(s);
                }
            }
        }
        return null;
    }
}
