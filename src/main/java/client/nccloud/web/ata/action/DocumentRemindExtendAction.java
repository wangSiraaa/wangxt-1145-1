package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nccloud.web.ata.vo.DocumentVO;
import java.util.*;

public class DocumentRemindExtendAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Object obj = JsonFactory.create().fromJson(json, Object.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            List<String> pks = extractPks(obj);
            if (pks != null && !pks.isEmpty()) {
                for (String pk : pks) {
                    try {
                        service.remindExtend(pk);
                    } catch (Exception e) {
                    }
                }
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", true);
                return result;
            }
            if (obj instanceof Map) {
                Map params = (Map) obj;
                String pkDocument = (String) params.get("pk_document");
                if (pkDocument == null || pkDocument.isEmpty()) {
                    pkDocument = (String) params.get("id");
                }
                if (pkDocument != null && !pkDocument.isEmpty()) {
                    service.remindExtend(pkDocument);
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("success", true);
                    return result;
                }
                String pkExhibitList = (String) params.get("pk_exhibit_list");
                if (pkExhibitList != null && !pkExhibitList.isEmpty()) {
                    service.remindExtend(pkExhibitList);
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("success", true);
                    return result;
                }
            }
            throw new BusinessException("请传入单证主键");
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
