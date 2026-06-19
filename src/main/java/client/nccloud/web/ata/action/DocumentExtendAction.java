package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.AggDocumentVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import java.util.*;

public class DocumentExtendAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Object obj = JsonFactory.create().fromJson(json, Object.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            if (!(obj instanceof Map)) {
                throw new BusinessException("参数格式错误");
            }
            Map params = (Map) obj;
            String pkDocument = getParamValue(params, "pk_document");
            if (pkDocument == null || pkDocument.isEmpty()) {
                pkDocument = getParamValue(params, "id");
            }
            if (pkDocument == null || pkDocument.isEmpty()) {
                throw new BusinessException("单证主键不能为空");
            }
            String newValidToStr = getParamValue(params, "new_valid_to");
            if (newValidToStr == null || newValidToStr.isEmpty()) {
                throw new BusinessException("新的有效期不能为空");
            }
            UFDate newValidTo;
            try {
                newValidToStr = newValidToStr.replaceAll("/", "-");
                newValidTo = UFDate.valueOf(newValidToStr);
            } catch (Exception e) {
                throw new BusinessException("有效期格式错误，请使用yyyy-MM-dd格式");
            }
            String extendRemark = getParamValue(params, "extend_remark");
            AggDocumentVO result = service.extend(pkDocument, newValidTo, extendRemark);
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put("success", true);
            ret.put("data", result);
            return ret;
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
}
