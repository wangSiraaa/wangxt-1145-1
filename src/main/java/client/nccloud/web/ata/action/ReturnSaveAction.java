package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IReturnService;
import nccloud.web.ata.vo.AggReturnVO;

public class ReturnSaveAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        AggReturnVO vo = JsonFactory.create().fromJson(json, AggReturnVO.class);
        IReturnService service = ServiceLocator.find(IReturnService.class);
        try {
            return service.save(vo);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
