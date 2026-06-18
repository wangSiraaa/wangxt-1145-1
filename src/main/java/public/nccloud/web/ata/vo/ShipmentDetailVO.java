package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class ShipmentDetailVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_shipment_detail";
	public static final String PK_SHIPMENT_DETAIL = "pk_shipment_detail";
	public static final String PK_SHIPMENT = "pk_shipment";
	public static final String PK_EXHIBIT = "pk_exhibit";
	public static final String EXHIBIT_CODE = "exhibit_code";
	public static final String EXHIBIT_NAME = "exhibit_name";
	public static final String SHIPMENT_QTY = "shipment_qty";
	public static final String SERIAL_VERIFIED = "serial_verified";
	public static final String REMARK = "remark";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_shipment_detail;
	private String pk_shipment;
	private String pk_exhibit;
	private String exhibit_code;
	private String exhibit_name;
	private UFDouble shipment_qty;
	private Integer serial_verified;
	private String remark;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_shipment_detail() {
		return pk_shipment_detail;
	}

	public void setPk_shipment_detail(String pk_shipment_detail) {
		this.pk_shipment_detail = pk_shipment_detail;
	}

	public String getPk_shipment() {
		return pk_shipment;
	}

	public void setPk_shipment(String pk_shipment) {
		this.pk_shipment = pk_shipment;
	}

	public String getPk_exhibit() {
		return pk_exhibit;
	}

	public void setPk_exhibit(String pk_exhibit) {
		this.pk_exhibit = pk_exhibit;
	}

	public String getExhibit_code() {
		return exhibit_code;
	}

	public void setExhibit_code(String exhibit_code) {
		this.exhibit_code = exhibit_code;
	}

	public String getExhibit_name() {
		return exhibit_name;
	}

	public void setExhibit_name(String exhibit_name) {
		this.exhibit_name = exhibit_name;
	}

	public UFDouble getShipment_qty() {
		return shipment_qty;
	}

	public void setShipment_qty(UFDouble shipment_qty) {
		this.shipment_qty = shipment_qty;
	}

	public Integer getSerial_verified() {
		return serial_verified;
	}

	public void setSerial_verified(Integer serial_verified) {
		this.serial_verified = serial_verified;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getPKFieldName() {
		return PK_SHIPMENT_DETAIL;
	}

	@Override
	public String getParentPKFieldName() {
		return PK_SHIPMENT;
	}

	@Override
	public VOMetaData getMetaData() throws MetaDataException {
		VOMetaData meta = new VOMetaData();
		EntityMetaData entity = new EntityMetaData();
		entity.setTableName(TABLE_NAME);
		entity.setPackageName(this.getClass().getPackage().getName());
		entity.setParentPKFieldName(PK_SHIPMENT);
		entity.setPKFieldName(PK_SHIPMENT_DETAIL);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[16];
		props[0] = PropertyMetaDataFactory.createDefault(PK_SHIPMENT_DETAIL, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(PK_SHIPMENT, "出运主键", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT, "展品主键", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(EXHIBIT_CODE, "展品编码", String.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(EXHIBIT_NAME, "展品名称", String.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(SHIPMENT_QTY, "出运数量", UFDouble.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(SERIAL_VERIFIED, "序列号验证", Integer.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(REMARK, "备注", String.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(PK_GROUP, "集团主键", String.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(PK_ORG, "组织主键", String.class.getName(), false);
		props[10] = PropertyMetaDataFactory.createDefault(CREATOR, "创建人", String.class.getName(), false);
		props[11] = PropertyMetaDataFactory.createDefault(CREATIONTIME, "创建时间", UFDateTime.class.getName(), false);
		props[12] = PropertyMetaDataFactory.createDefault(MODIFIER, "修改人", String.class.getName(), false);
		props[13] = PropertyMetaDataFactory.createDefault(MODIFIEDTIME, "修改时间", UFDateTime.class.getName(), false);
		props[14] = PropertyMetaDataFactory.createDefault(DR, "删除标记", Integer.class.getName(), false);
		props[15] = PropertyMetaDataFactory.createDefault(TS, "时间戳", UFDateTime.class.getName(), false);

		entity.setProperties(props);
		meta.addEntityMetaData(entity);
		return meta;
	}
}
