package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IExhibitListService;
import nccloud.web.ata.vo.AggExhibitListVO;
import nc.vo.pub.BusinessException;
import java.util.Map;

public class ExhibitListUnCommitAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IExhibitListService service = ServiceLocator.find(IExhibitListService.class);
        try {
            AggExhibitListVO aggVO;
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            if (obj instanceof Map) {
                Map params = (Map) obj;
                String pk = (String) params.get("pk_exhibit_list");
                if (pk != null && !pk.isEmpty()) {
                    aggVO = service.queryByPk(pk);
                } else {
                    aggVO = JsonFactory.create().fromJson(json, AggExhibitListVO.class);
                }
            } else {
                aggVO = JsonFactory.create().fromJson(json, AggExhibitListVO.class);
            }
            AggExhibitListVO result = service.unCommit(aggVO);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
