package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggDocumentVOMeta extends AbstractBillMeta {

    @Override
    public void init() {
        this.setParent(DocumentVO.class);
    }
}
