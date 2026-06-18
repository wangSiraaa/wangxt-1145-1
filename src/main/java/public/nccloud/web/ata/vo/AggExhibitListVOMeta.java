package nccloud.web.ata.vo;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggExhibitListVOMeta extends AbstractBillMeta {

    @Override
    public void init() {
        this.setParent(ExhibitListVO.class);
        this.addChildren(ExhibitVO.class);
    }
}
