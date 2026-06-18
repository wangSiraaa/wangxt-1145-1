package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IDiffService;
import java.util.Map;

public class DiffQueryByStatusAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IDiffService service = ServiceLocator.find(IDiffService.class);
        try {
            Integer diffStatus = null;
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                Object status = map.get("diff_status");
                if (status != null) {
                    diffStatus = Integer.parseInt(status.toString());
                }
            } else if (obj instanceof Integer) {
                diffStatus = (Integer) obj;
            } else if (obj instanceof String) {
                diffStatus = Integer.parseInt(obj.toString());
            }
            return service.queryByStatus(diffStatus);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
