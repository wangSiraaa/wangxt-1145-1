package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IShipmentService;
import nccloud.web.ata.vo.AggShipmentVO;

public class ShipmentSaveAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        AggShipmentVO vo = JsonFactory.create().fromJson(json, AggShipmentVO.class);
        IShipmentService service = ServiceLocator.find(IShipmentService.class);
        try {
            return service.save(vo);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
