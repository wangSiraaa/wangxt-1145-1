package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggShipmentVOMeta extends AbstractBillMeta {

    @Override
    public void init() {
        this.setParent(ShipmentVO.class);
        this.addChildren(ShipmentDetailVO.class);
    }
}
