package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.annotation.AggVoInfo;

@AggVoInfo(parentVO = "nccloud.web.ata.vo.ShipmentVO")
public class AggShipmentVO extends AbstractBill {

    @Override
    public IBillMeta getMetaData() {
        return new AggShipmentVOMeta();
    }

    @Override
    public ShipmentVO getParentVO() {
        return (ShipmentVO) super.getParentVO();
    }
}
