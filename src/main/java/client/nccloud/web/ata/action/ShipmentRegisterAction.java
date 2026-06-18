package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IShipmentService;
import nccloud.web.ata.vo.AggShipmentVO;
import java.util.Map;

public class ShipmentRegisterAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IShipmentService service = ServiceLocator.find(IShipmentService.class);
        try {
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            AggShipmentVO vo = null;
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                Object pk = map.get("pk_shipment");
                if (pk != null) {
                    vo = service.queryByPk(pk.toString());
                } else {
                    vo = JsonFactory.create().fromJson(json, AggShipmentVO.class);
                }
            } else if (obj instanceof AggShipmentVO) {
                vo = (AggShipmentVO) obj;
            } else {
                vo = JsonFactory.create().fromJson(json, AggShipmentVO.class);
            }
            return service.register(vo);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
