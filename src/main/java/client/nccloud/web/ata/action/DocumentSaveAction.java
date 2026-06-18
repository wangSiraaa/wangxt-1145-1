package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.AggDocumentVO;
import nc.vo.pub.BusinessException;

public class DocumentSaveAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        AggDocumentVO aggVO = JsonFactory.create().fromJson(json, AggDocumentVO.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            AggDocumentVO result = service.save(aggVO);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
