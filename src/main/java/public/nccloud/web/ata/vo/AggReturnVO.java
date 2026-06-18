package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.annotation.AggVoInfo;

@AggVoInfo(parentVO = "nccloud.web.ata.vo.ReturnVO")
public class AggReturnVO extends AbstractBill {

    @Override
    public IBillMeta getMetaData() {
        return new AggReturnVOMeta();
    }

    @Override
    public ReturnVO getParentVO() {
        return (ReturnVO) super.getParentVO();
    }
}
