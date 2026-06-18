package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.web.ata.itf.IDiffService;
import nccloud.web.ata.vo.DiffVO;
import java.util.Map;

public class DiffProcessAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        IDiffService service = ServiceLocator.find(IDiffService.class);
        try {
            Object obj = JsonFactory.create().fromJson(json, Object.class);
            DiffVO vo = null;
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                Object pk = map.get("pk_diff");
                if (pk != null) {
                    vo = service.queryByPk(pk.toString());
                    Object handler = map.get("handler");
                    if (handler != null && vo != null) {
                        vo.setHandler(handler.toString());
                    }
                    Object handleRemark = map.get("handle_remark");
                    if (handleRemark != null && vo != null) {
                        vo.setHandle_remark(handleRemark.toString());
                    }
                } else {
                    vo = JsonFactory.create().fromJson(json, DiffVO.class);
                }
            } else if (obj instanceof DiffVO) {
                vo = (DiffVO) obj;
            } else {
                vo = JsonFactory.create().fromJson(json, DiffVO.class);
            }
            return service.process(vo);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
