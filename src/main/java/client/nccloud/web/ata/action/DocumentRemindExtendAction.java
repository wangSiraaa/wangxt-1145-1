package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IDocumentService;
import nccloud.web.ata.vo.AggDocumentVO;
import nc.vo.pub.BusinessException;
import java.util.Map;

public class DocumentRemindExtendAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Map<String, Object> params = JsonFactory.create().fromJson(json, Map.class);
        IDocumentService service = ServiceLocator.find(IDocumentService.class);
        try {
            String pkExhibitList = (String) params.get("pk_exhibit_list");
            if (pkExhibitList == null || pkExhibitList.isEmpty()) {
                String pkDocument = (String) params.get("pk_document");
                if (pkDocument != null && !pkDocument.isEmpty()) {
                    AggDocumentVO docVO = service.queryByPk(pkDocument);
                    if (docVO != null && docVO.getParentVO() != null) {
                        pkExhibitList = docVO.getParentVO().getPk_exhibit_list();
                    }
                }
            }
            if (pkExhibitList == null || pkExhibitList.isEmpty()) {
                throw new BusinessException("请传入展品清单主键或单证主键");
            }
            AggDocumentVO result = service.remindExtend(pkExhibitList);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
