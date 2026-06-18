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

public class ExhibitListDeleteAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Map<String, Object> params = JsonFactory.create().fromJson(json, Map.class);
        String pk = (String) params.get("pk_exhibit_list");
        IExhibitListService service = ServiceLocator.find(IExhibitListService.class);
        try {
            AggExhibitListVO aggVO = service.queryByPk(pk);
            if (aggVO == null) {
                throw new BusinessException("未找到要删除的展品清单数据");
            }
            AggExhibitListVO result = service.delete(aggVO);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
