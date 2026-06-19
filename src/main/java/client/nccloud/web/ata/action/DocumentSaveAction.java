package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.VOStatus;
import java.util.*;

public class DocumentSaveAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Object obj = JsonFactory.create().fromJson(json, Object.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            if (obj instanceof Map) {
                Map params = (Map) obj;
                String action = (String) params.get("action");
                if ("delete".equals(action)) {
                    List pkList = (List) params.get("pks");
                    if (pkList != null && !pkList.isEmpty()) {
                        String[] pks = (String[]) pkList.toArray(new String[0]);
                        service.deleteByPks(pks);
                        Map<String, Object> result = new HashMap<String, Object>();
                        result.put("success", true);
                        return result;
                    }
                }
                AggDocumentVO aggVO = parseAggFromTemps(params);
                if (aggVO == null) {
                    aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
                }
                AggDocumentVO result = service.save(aggVO);
                return buildCardResult(result);
            }
            AggDocumentVO aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
            AggDocumentVO result = service.save(aggVO);
            return buildCardResult(result);
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }

    private AggDocumentVO parseAggFromTemps(Map params) {
        Object temps = params.get("temps");
        if (!(temps instanceof Map)) {
            return null;
        }
        Map tempsMap = (Map) temps;
        Object formObj = tempsMap.get("document_head_form");
        if (!(formObj instanceof Map)) {
            return null;
        }
        Map formMap = (Map) formObj;
        Object rowsObj = formMap.get("rows");
        if (!(rowsObj instanceof List) || ((List) rowsObj).isEmpty()) {
            return null;
        }
        List rows = (List) rowsObj;
        Object rowObj = rows.get(0);
        if (!(rowObj instanceof Map)) {
            return null;
        }
        Map row = (Map) rowObj;
        Object valuesObj = row.get("values");
        if (!(valuesObj instanceof Map)) {
            return null;
        }
        Map values = (Map) valuesObj;
        DocumentVO head = new DocumentVO();
        String[] fields = new String[]{
            "pk_document", "document_no", "pk_exhibit_list", "pk_org", "pk_group",
            "doc_status", "issuing_authority", "ata_carnet_no", "valid_from", "valid_to",
            "purpose", "remark", "reviewer", "review_time", "ts", "dr"
        };
        for (String f : fields) {
            Object fv = values.get(f);
            if (fv instanceof Map) {
                Object v = ((Map) fv).get("value");
                if (v != null) {
                    if ("valid_from".equals(f) || "valid_to".equals(f)) {
                        try {
                            head.setAttributeValue(f, new UFDate(String.valueOf(v)));
                        } catch (Exception e) {
                            head.setAttributeValue(f, v);
                        }
                    } else if ("doc_status".equals(f) || "dr".equals(f)) {
                        try {
                            head.setAttributeValue(f, Integer.valueOf(String.valueOf(v)));
                        } catch (Exception e) {
                            head.setAttributeValue(f, v);
                        }
                    } else {
                        head.setAttributeValue(f, v);
                    }
                }
            }
        }
        if (head.getPk_document() == null || String.valueOf(head.getPk_document()).trim().length() == 0) {
            head.setStatus(VOStatus.NEW);
        } else {
            head.setStatus(VOStatus.UPDATED);
        }
        AggDocumentVO agg = new AggDocumentVO();
        agg.setParentVO(head);
        return agg;
    }

    private Map<String, Object> buildCardResult(AggDocumentVO agg) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (agg == null || agg.getParentVO() == null) {
            result.put("head", new HashMap<String, Object>());
            return result;
        }
        DocumentVO head = agg.getParentVO();
        Map<String, Object> headForm = new HashMap<String, Object>();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        Map<String, Object> values = new HashMap<String, Object>();
        String[] fields = new String[]{
            "pk_document", "document_no", "pk_exhibit_list", "pk_org", "pk_group",
            "doc_status", "issuing_authority", "ata_carnet_no", "valid_from", "valid_to",
            "purpose", "remark", "reviewer", "review_time", "creator", "creationtime",
            "modifier", "modifiedtime", "ts", "dr"
        };
        for (String f : fields) {
            Map<String, Object> fmap = new HashMap<String, Object>();
            Object v = head.getAttributeValue(f);
            if (v != null) {
                if (v instanceof UFDate) {
                    fmap.put("value", v.toString());
                    fmap.put("display", v.toString());
                } else {
                    fmap.put("value", v);
                    fmap.put("display", v.toString());
                }
            } else {
                fmap.put("value", null);
                fmap.put("display", null);
            }
            String refname = f + "_name";
            Object display = head.getAttributeValue(refname);
            if (display != null) {
                fmap.put("display", display.toString());
            }
            values.put(f, fmap);
        }
        row.put("values", values);
        row.put("status", "0");
        rows.add(row);
        headForm.put("rows", rows);
        Map<String, Object> headWrapper = new HashMap<String, Object>();
        headWrapper.put("document_head_form", headForm);
        result.put("head", headWrapper);
        return result;
    }
}
