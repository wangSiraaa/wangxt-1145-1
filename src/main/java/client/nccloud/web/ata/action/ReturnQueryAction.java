package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IReturnService;
import java.util.Map;

public class ReturnQueryAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IReturnService service = ServiceLocator.find(IReturnService.class);
        try {
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                Object pk = map.get("pk_return");
                if (pk != null && pk.toString().trim().length() > 0) {
                    return service.queryByPk(pk.toString());
                }
                String pkExhibitList = null;
                Integer returnStatus = null;
                String pkOrg = null;
                Object pkList = map.get("pk_exhibit_list");
                if (pkList != null && pkList.toString().trim().length() > 0) {
                    pkExhibitList = pkList.toString();
                }
                Object status = map.get("return_status");
                if (status != null) {
                    returnStatus = Integer.parseInt(status.toString());
                }
                Object pkOrgObj = map.get("pk_org");
                if (pkOrgObj != null && pkOrgObj.toString().trim().length() > 0) {
                    pkOrg = pkOrgObj.toString();
                }
                return service.queryByCondition(pkExhibitList, returnStatus, pkOrg);
            }
            return null;
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
