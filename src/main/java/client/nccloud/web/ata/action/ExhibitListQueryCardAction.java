package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IExhibitListService;
import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import java.util.*;

public class ExhibitListQueryCardAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Map<String, Object> params = JsonFactory.create().fromJson(json, Map.class);
        String pk = getParamValue(params, "pk_exhibit_list");
        if (pk == null || pk.isEmpty()) {
            pk = getParamValue(params, "id");
        }
        IExhibitListService service = ServiceLocator.find(IExhibitListService.class);
        try {
            AggExhibitListVO result = service.queryByPk(pk);
            return buildResult(result);
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

    private Map<String, Object> buildResult(AggExhibitListVO agg) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (agg == null) {
            return result;
        }
        ExhibitListVO head = agg.getParentVO();
        if (head != null) {
            Map<String, Object> headForm = new HashMap<String, Object>();
            List<Map<String, Object>> headRows = new ArrayList<Map<String, Object>>();
            Map<String, Object> headRow = new HashMap<String, Object>();
            Map<String, Object> headValues = new HashMap<String, Object>();
            String[] headFields = new String[]{
                "pk_exhibit_list", "list_code", "list_name", "list_status",
                "pk_org", "pk_group", "exhibition_name", "exhibition_country",
                "exhibition_start", "exhibition_end", "exhibitor_name",
                "remark", "creator", "creationtime", "modifier", "modifiedtime", "ts", "dr"
            };
            for (String f : headFields) {
                Map<String, Object> fmap = buildFieldMap(head.getAttributeValue(f));
                headValues.put(f, fmap);
            }
            headRow.put("values", headValues);
            headRow.put("status", "0");
            headRows.add(headRow);
            headForm.put("rows", headRows);
            Map<String, Object> headWrapper = new HashMap<String, Object>();
            headWrapper.put("ata_exhibit_list", headForm);
            result.put("head", headWrapper);
        }
        ExhibitVO[] children = agg.getChildrenVO();
        List<Map<String, Object>> bodyRows = new ArrayList<Map<String, Object>>();
        if (children != null && children.length > 0) {
            String[] bodyFields = new String[]{
                "pk_exhibit", "pk_exhibit_list", "exhibit_code", "exhibit_name",
                "exhibit_spec", "exhibit_model", "serial_no", "hs_code",
                "origin_country", "unit", "qty", "unit_price", "total_price",
                "currency", "exhibit_status", "serial_verified", "remark",
                "creator", "creationtime", "modifier", "modifiedtime", "ts", "dr"
            };
            for (ExhibitVO ex : children) {
                Map<String, Object> row = new HashMap<String, Object>();
                Map<String, Object> values = new HashMap<String, Object>();
                for (String f : bodyFields) {
                    Map<String, Object> fmap = buildFieldMap(ex.getAttributeValue(f));
                    values.put(f, fmap);
                }
                row.put("values", values);
                row.put("status", "0");
                bodyRows.add(row);
            }
        }
        Map<String, Object> bodyWrapper = new HashMap<String, Object>();
        bodyWrapper.put("rows", bodyRows);
        bodyWrapper.put("allpks", new ArrayList<String>());
        result.put("ata_exhibit", bodyWrapper);
        return result;
    }

    private Map<String, Object> buildFieldMap(Object v) {
        Map<String, Object> fmap = new HashMap<String, Object>();
        if (v != null) {
            if (v instanceof UFDate || v instanceof UFDateTime || v instanceof UFDouble) {
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
        return fmap;
    }
}
