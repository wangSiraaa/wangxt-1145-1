package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.annotation.AggVoInfo;

@AggVoInfo(parentVO = "nccloud.web.ata.vo.DocumentVO")
public class AggDocumentVO extends AbstractBill {

    @Override
    public IBillMeta getMetaData() {
        return new AggDocumentVOMeta();
    }

    @Override
    public DocumentVO getParentVO() {
        return (DocumentVO) super.getParentVO();
    }
}
