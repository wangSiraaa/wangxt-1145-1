package nccloud.web.ata.itf;

import nccloud.web.ata.vo.*;
import nc.vo.pub.BusinessException;
import java.util.List;
import nc.jdbc.framework.SQLParameter;

public interface IExhibitListService {

    AggExhibitListVO save(AggExhibitListVO vo) throws BusinessException;

    AggExhibitListVO delete(AggExhibitListVO vo) throws BusinessException;

    AggExhibitListVO commit(AggExhibitListVO vo) throws BusinessException;

    AggExhibitListVO unCommit(AggExhibitListVO vo) throws BusinessException;

    AggExhibitListVO queryByPk(String pk) throws BusinessException;

    List<AggExhibitListVO> queryByCondition(String condition, SQLParameter param) throws BusinessException;
}
