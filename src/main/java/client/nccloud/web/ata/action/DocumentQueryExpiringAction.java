package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.DocumentVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import java.util.*;

public class DocumentQueryExpiringAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            List<DocumentVO> list = service.getExpiringDetails();
            Map<String, Object> result = new HashMap<String, Object>();
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            if (list != null && !list.isEmpty()) {
                for (DocumentVO vo : list) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("pk_document", nvl(vo.getPk_document()));
                    item.put("document_no", nvl(vo.getDocument_no()));
                    item.put("pk_exhibit_list", nvl(vo.getPk_exhibit_list()));
                    item.put("pk_exhibit_list_name", nvl(vo.getAttributeValue("pk_exhibit_list_name")));
                    item.put("issuing_authority", nvl(vo.getIssuing_authority()));
                    item.put("valid_from", formatDate(vo.getValid_from()));
                    item.put("valid_to", formatDate(vo.getValid_to()));
                    item.put("doc_status", vo.getDoc_status() == null ? 0 : vo.getDoc_status());
                    dataList.add(item);
                }
            }
            result.put("list", dataList);
            result.put("count", dataList.size());
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }

    private String nvl(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    private String formatDate(Object obj) {
        if (obj == null) return "";
        if (obj instanceof UFDate) {
            return obj.toString();
        }
        return String.valueOf(obj);
    }
}
