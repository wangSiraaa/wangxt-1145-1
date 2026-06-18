package nccloud.web.ata.action;

import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.web.json.JsonFactory;
import nccloud.web.ata.itf.IExhibitListService;
import nccloud.web.ata.vo.AggExhibitListVO;
import nc.vo.pub.BusinessException;
import nc.jdbc.framework.SQLParameter;
import java.util.List;
import java.util.Map;

public class ExhibitListQueryAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        String json = request.read();
        Map<String, Object> params = JsonFactory.create().fromJson(json, Map.class);
        IExhibitListService service = ServiceLocator.find(IExhibitListService.class);
        try {
            String pk = (String) params.get("pk_exhibit_list");
            if (pk != null && !pk.isEmpty()) {
                AggExhibitListVO result = service.queryByPk(pk);
                return result;
            }
            StringBuilder condition = new StringBuilder(" dr = 0 ");
            SQLParameter sqlParam = new SQLParameter();
            int paramIndex = 0;
            if (params.get("pk_org") != null && !((String) params.get("pk_org")).isEmpty()) {
                condition.append(" and pk_org = ? ");
                sqlParam.addParam(paramIndex++, params.get("pk_org"));
            }
            if (params.get("list_status") != null) {
                condition.append(" and list_status = ? ");
                sqlParam.addParam(paramIndex++, params.get("list_status"));
            }
            if (params.get("list_code") != null && !((String) params.get("list_code")).isEmpty()) {
                condition.append(" and list_code like ? ");
                sqlParam.addParam(paramIndex++, "%" + params.get("list_code") + "%");
            }
            List<AggExhibitListVO> result = service.queryByCondition(condition.toString(), sqlParam);
            return result;
        } catch (BusinessException e) {
            ExceptionUtils.wrapException(e);
            return null;
        }
    }
}
