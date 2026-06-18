package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IReturnService;
import nccloud.web.ata.vo.AggReturnVO;
import java.util.Map;

public class ReturnCancelRegisterAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IReturnService service = ServiceLocator.find(IReturnService.class);
        try {
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            AggReturnVO vo = null;
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                Object pk = map.get("pk_return");
                if (pk != null) {
                    vo = service.queryByPk(pk.toString());
                } else {
                    vo = JsonFactory.create().fromJson(json, AggReturnVO.class);
                }
            } else if (obj instanceof AggReturnVO) {
                vo = (AggReturnVO) obj;
            } else {
                vo = JsonFactory.create().fromJson(json, AggReturnVO.class);
            }
            return service.cancelRegister(vo);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
