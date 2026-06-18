package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IReturnService;
import java.util.Map;

public class ReturnQueryByListAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IReturnService service = ServiceLocator.find(IReturnService.class);
        try {
            String pkExhibitList = null;
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                Object pk = map.get("pk_exhibit_list");
                if (pk != null) {
                    pkExhibitList = pk.toString();
                }
            } else if (obj instanceof String) {
                pkExhibitList = obj.toString();
            }
            return service.queryByExhibitList(pkExhibitList);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
