package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IDiffService;
import nccloud.web.ata.vo.DiffVO;

public class DiffSaveAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        DiffVO vo = JsonFactory.create().fromJson(json, DiffVO.class);
        IDiffService service = ServiceLocator.find(IDiffService.class);
        try {
            return service.save(vo);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
