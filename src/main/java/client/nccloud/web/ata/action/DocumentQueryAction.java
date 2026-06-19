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
import java.util.*;

public class DocumentQueryAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Map<String, Object> params = JsonFactory.create().fromJson(json, Map.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            String pkDocument = getParamValue(params, "pk_document");
            if (pkDocument == null || pkDocument.isEmpty()) {
                pkDocument = getParamValue(params, "id");
            }
            if (pkDocument != null && !pkDocument.isEmpty()) {
                AggDocumentVO result = service.queryByPk(pkDocument);
                return buildCardResult(result);
            }
            String pkExhibitList = getParamValue(params, "pk_exhibit_list");
            if (pkExhibitList != null && !pkExhibitList.isEmpty()) {
                AggDocumentVO result = service.queryByExhibitList(pkExhibitList);
                return buildCardResult(result);
            }
            Map<String, Object> condition = extractCondition(params);
            List<AggDocumentVO> list = service.queryByCondition(condition);
            return buildListResult(list);
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

    private Map<String, Object> extractCondition(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params == null) {
            return result;
        }
        Object qc = params.get("querycondition");
        if (qc instanceof Map) {
            Map qcMap = (Map) qc;
            Object conditions = qcMap.get("conditions");
            if (conditions instanceof List) {
                List condList = (List) conditions;
                for (Object obj : condList) {
                    if (obj instanceof Map) {
                        Map cond = (Map) obj;
                        String field = (String) cond.get("field");
                        Object value = cond.get("value");
                        if (field != null && value != null) {
                            if (value instanceof String) {
                                result.put(field, value);
                            } else if (value instanceof Map) {
                                Object firstValue = ((Map) value).get("firstvalue");
                                if (firstValue != null) {
                                    result.put(field, firstValue);
                                } else {
                                    Object v = ((Map) value).get("value");
                                    if (v != null) {
                                        result.put(field, v);
                                    }
                                }
                            } else {
                                result.put(field, value);
                            }
                        }
                    }
                }
            }
        }
        for (String key : new String[]{"pk_org", "doc_status", "pk_exhibit_list", "document_no", "list_status"}) {
            if (!result.containsKey(key)) {
                String v = getParamValue(params, key);
                if (v != null && !v.isEmpty()) {
                    result.put(key, v);
                }
            }
        }
        return result;
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

    private Map<String, Object> buildListResult(List<AggDocumentVO> list) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list != null && !list.isEmpty()) {
            String[] fields = new String[]{
                "pk_document", "document_no", "pk_exhibit_list", "pk_org",
                "doc_status", "issuing_authority", "ata_carnet_no", "valid_from", "valid_to",
                "purpose", "remark", "reviewer", "review_time", "creator", "creationtime", "ts"
            };
            for (AggDocumentVO agg : list) {
                DocumentVO head = agg.getParentVO();
                if (head == null) continue;
                Map<String, Object> row = new HashMap<String, Object>();
                Map<String, Object> values = new HashMap<String, Object>();
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
            }
        }
        Map<String, Object> table = new HashMap<String, Object>();
        table.put("rows", rows);
        table.put("allpks", new ArrayList<String>());
        result.put("document_list_table", table);
        return result;
    }
}
