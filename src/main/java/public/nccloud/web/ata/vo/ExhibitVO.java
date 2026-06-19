package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class ExhibitVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_exhibit";
	public static final String PK_EXHIBIT = "pk_exhibit";
	public static final String PK_EXHIBIT_LIST = "pk_exhibit_list";
	public static final String EXHIBIT_CODE = "exhibit_code";
	public static final String EXHIBIT_NAME = "exhibit_name";
	public static final String SERIAL_NO = "serial_no";
	public static final String SPECIFICATION = "specification";
	public static final String QUANTITY = "quantity";
	public static final String UNIT = "unit";
	public static final String VALUE = "value";
	public static final String CURRENCY = "currency";
	public static final String HS_CODE = "hs_code";
	public static final String EXHIBIT_STATUS = "exhibit_status";
	public static final String IS_CONTROLLED = "is_controlled";
	public static final String CONTROL_LEVEL = "control_level";
	public static final String SHIPPED_QTY = "shipped_qty";
	public static final String VALUE_VERIFIED = "value_verified";
	public static final String REMARK = "remark";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_exhibit;
	private String pk_exhibit_list;
	private String exhibit_code;
	private String exhibit_name;
	private String serial_no;
	private String specification;
	private UFDouble quantity;
	private String unit;
	private UFDouble value;
	private String currency;
	private String hs_code;
	private Integer exhibit_status;
	private Integer is_controlled;
	private String control_level;
	private UFDouble shipped_qty;
	private Integer value_verified;
	private String remark;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_exhibit() {
		return pk_exhibit;
	}

	public void setPk_exhibit(String pk_exhibit) {
		this.pk_exhibit = pk_exhibit;
	}

	public String getPk_exhibit_list() {
		return pk_exhibit_list;
	}

	public void setPk_exhibit_list(String pk_exhibit_list) {
		this.pk_exhibit_list = pk_exhibit_list;
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

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public UFDouble getQuantity() {
		return quantity;
	}

	public void setQuantity(UFDouble quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public UFDouble getValue() {
		return value;
	}

	public void setValue(UFDouble value) {
		this.value = value;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getHs_code() {
		return hs_code;
	}

	public void setHs_code(String hs_code) {
		this.hs_code = hs_code;
	}

	public Integer getExhibit_status() {
		return exhibit_status;
	}

	public void setExhibit_status(Integer exhibit_status) {
		this.exhibit_status = exhibit_status;
	}

	public Integer getIs_controlled() {
		return is_controlled;
	}

	public void setIs_controlled(Integer is_controlled) {
		this.is_controlled = is_controlled;
	}

	public String getControl_level() {
		return control_level;
	}

	public void setControl_level(String control_level) {
		this.control_level = control_level;
	}

	public UFDouble getShipped_qty() {
		return shipped_qty;
	}

	public void setShipped_qty(UFDouble shipped_qty) {
		this.shipped_qty = shipped_qty;
	}

	public Integer getValue_verified() {
		return value_verified;
	}

	public void setValue_verified(Integer value_verified) {
		this.value_verified = value_verified;
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
		return PK_EXHIBIT;
	}

	@Override
	public String getParentPKFieldName() {
		return PK_EXHIBIT_LIST;
	}

	@Override
	public VOMetaData getMetaData() throws MetaDataException {
		VOMetaData meta = new VOMetaData();
		EntityMetaData entity = new EntityMetaData();
		entity.setTableName(TABLE_NAME);
		entity.setPackageName(this.getClass().getPackage().getName());
		entity.setParentPKFieldName(PK_EXHIBIT_LIST);
		entity.setPKFieldName(PK_EXHIBIT);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[25];
		props[0] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT_LIST, "清单主键", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(EXHIBIT_CODE, "展品编码", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(EXHIBIT_NAME, "展品名称", String.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(SERIAL_NO, "序列号", String.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(SPECIFICATION, "规格", String.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(QUANTITY, "数量", UFDouble.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(UNIT, "单位", String.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(VALUE, "价值", UFDouble.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(CURRENCY, "币种", String.class.getName(), false);
		props[10] = PropertyMetaDataFactory.createDefault(HS_CODE, "HS编码", String.class.getName(), false);
		props[11] = PropertyMetaDataFactory.createDefault(EXHIBIT_STATUS, "状态", Integer.class.getName(), false);
		props[12] = PropertyMetaDataFactory.createDefault(IS_CONTROLLED, "是否管制品", Integer.class.getName(), false);
		props[13] = PropertyMetaDataFactory.createDefault(CONTROL_LEVEL, "管制级别", String.class.getName(), false);
		props[14] = PropertyMetaDataFactory.createDefault(SHIPPED_QTY, "累计已出运数量", UFDouble.class.getName(), false);
		props[15] = PropertyMetaDataFactory.createDefault(VALUE_VERIFIED, "估值审核标记", Integer.class.getName(), false);
		props[16] = PropertyMetaDataFactory.createDefault(REMARK, "备注", String.class.getName(), false);
		props[17] = PropertyMetaDataFactory.createDefault(PK_GROUP, "集团主键", String.class.getName(), false);
		props[18] = PropertyMetaDataFactory.createDefault(PK_ORG, "组织主键", String.class.getName(), false);
		props[19] = PropertyMetaDataFactory.createDefault(CREATOR, "创建人", String.class.getName(), false);
		props[20] = PropertyMetaDataFactory.createDefault(CREATIONTIME, "创建时间", UFDateTime.class.getName(), false);
		props[21] = PropertyMetaDataFactory.createDefault(MODIFIER, "修改人", String.class.getName(), false);
		props[22] = PropertyMetaDataFactory.createDefault(MODIFIEDTIME, "修改时间", UFDateTime.class.getName(), false);
		props[23] = PropertyMetaDataFactory.createDefault(DR, "删除标记", Integer.class.getName(), false);
		props[24] = PropertyMetaDataFactory.createDefault(TS, "时间戳", UFDateTime.class.getName(), false);

		entity.setProperties(props);
		meta.addEntityMetaData(entity);
		return meta;
	}
}
