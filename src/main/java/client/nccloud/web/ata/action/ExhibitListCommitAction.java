package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IExhibitListService;
import nccloud.web.ata.vo.AggExhibitListVO;
import nc.vo.pub.BusinessException;

public class ExhibitListCommitAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        AggExhibitListVO aggVO = JsonFactory.create().fromJson(json, AggExhibitListVO.class);
        IExhibitListService service = ServiceLocator.find(IExhibitListService.class);
        try {
            AggExhibitListVO result = service.commit(aggVO);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
