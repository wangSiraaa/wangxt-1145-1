package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.annotation.AggVoInfo;

@AggVoInfo(parentVO = "nccloud.web.ata.vo.ExhibitListVO")
public class AggExhibitListVO extends AbstractBill {

    @Override
    public IBillMeta getMetaData() {
        return new AggExhibitListVOMeta();
    }

    @Override
    public ExhibitListVO getParentVO() {
        return (ExhibitListVO) super.getParentVO();
    }
}
