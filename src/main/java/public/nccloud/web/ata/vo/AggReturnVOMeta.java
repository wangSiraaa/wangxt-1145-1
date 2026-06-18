package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggReturnVOMeta extends AbstractBillMeta {

    @Override
    public void init() {
        this.setParent(ReturnVO.class);
        this.addChildren(ReturnDetailVO.class);
    }
}
