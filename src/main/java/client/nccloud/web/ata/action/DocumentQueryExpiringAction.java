package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IDocumentService;
import nc.vo.pub.BusinessException;
import java.util.List;

public class DocumentQueryExpiringAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            List<String> result = service.getExpiringList();
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
