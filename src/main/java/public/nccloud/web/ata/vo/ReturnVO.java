package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class ReturnVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_return";
	public static final String PK_RETURN = "pk_return";
	public static final String PK_EXHIBIT_LIST = "pk_exhibit_list";
	public static final String PK_SHIPMENT = "pk_shipment";
	public static final String RETURN_NO = "return_no";
	public static final String RETURN_DATE = "return_date";
	public static final String ARRIVAL_PORT = "arrival_port";
	public static final String CARRIER = "carrier";
	public static final String WAYBILL_NO = "waybill_no";
	public static final String REGISTRANT = "registrant";
	public static final String RETURN_STATUS = "return_status";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_return;
	private String pk_exhibit_list;
	private String pk_shipment;
	private String return_no;
	private UFDate return_date;
	private String arrival_port;
	private String carrier;
	private String waybill_no;
	private String registrant;
	private Integer return_status;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_return() {
		return pk_return;
	}

	public void setPk_return(String pk_return) {
		this.pk_return = pk_return;
	}

	public String getPk_exhibit_list() {
		return pk_exhibit_list;
	}

	public void setPk_exhibit_list(String pk_exhibit_list) {
		this.pk_exhibit_list = pk_exhibit_list;
	}

	public String getPk_shipment() {
		return pk_shipment;
	}

	public void setPk_shipment(String pk_shipment) {
		this.pk_shipment = pk_shipment;
	}

	public String getReturn_no() {
		return return_no;
	}

	public void setReturn_no(String return_no) {
		this.return_no = return_no;
	}

	public UFDate getReturn_date() {
		return return_date;
	}

	public void setReturn_date(UFDate return_date) {
		this.return_date = return_date;
	}

	public String getArrival_port() {
		return arrival_port;
	}

	public void setArrival_port(String arrival_port) {
		this.arrival_port = arrival_port;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getWaybill_no() {
		return waybill_no;
	}

	public void setWaybill_no(String waybill_no) {
		this.waybill_no = waybill_no;
	}

	public String getRegistrant() {
		return registrant;
	}

	public void setRegistrant(String registrant) {
		this.registrant = registrant;
	}

	public Integer getReturn_status() {
		return return_status;
	}

	public void setReturn_status(Integer return_status) {
		this.return_status = return_status;
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
		return PK_RETURN;
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
		entity.setPKFieldName(PK_RETURN);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[19];
		props[0] = PropertyMetaDataFactory.createDefault(PK_RETURN, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT_LIST, "清单主键", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(PK_SHIPMENT, "出运主键", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(RETURN_NO, "回运单号", String.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(RETURN_DATE, "回运日期", UFDate.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(ARRIVAL_PORT, "入境口岸", String.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(CARRIER, "承运人", String.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(WAYBILL_NO, "运单号", String.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(REGISTRANT, "登记人", String.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(RETURN_STATUS, "回运状态", Integer.class.getName(), false);
		props[10] = PropertyMetaDataFactory.createDefault(PK_GROUP, "集团主键", String.class.getName(), false);
		props[11] = PropertyMetaDataFactory.createDefault(PK_ORG, "组织主键", String.class.getName(), false);
		props[12] = PropertyMetaDataFactory.createDefault(CREATOR, "创建人", String.class.getName(), false);
		props[13] = PropertyMetaDataFactory.createDefault(CREATIONTIME, "创建时间", UFDateTime.class.getName(), false);
		props[14] = PropertyMetaDataFactory.createDefault(MODIFIER, "修改人", String.class.getName(), false);
		props[15] = PropertyMetaDataFactory.createDefault(MODIFIEDTIME, "修改时间", UFDateTime.class.getName(), false);
		props[16] = PropertyMetaDataFactory.createDefault(DR, "删除标记", Integer.class.getName(), false);
		props[17] = PropertyMetaDataFactory.createDefault(TS, "时间戳", UFDateTime.class.getName(), false);

		entity.setProperties(props);
		meta.addEntityMetaData(entity);
		return meta;
	}
}
