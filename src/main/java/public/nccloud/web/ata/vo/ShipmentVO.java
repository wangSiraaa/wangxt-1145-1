package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class ShipmentVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_shipment";
	public static final String PK_SHIPMENT = "pk_shipment";
	public static final String PK_EXHIBIT_LIST = "pk_exhibit_list";
	public static final String SHIPMENT_NO = "shipment_no";
	public static final String SHIPMENT_DATE = "shipment_date";
	public static final String DEPARTURE_PORT = "departure_port";
	public static final String DESTINATION_PORT = "destination_port";
	public static final String CARRIER = "carrier";
	public static final String WAYBILL_NO = "waybill_no";
	public static final String REGISTRANT = "registrant";
	public static final String SHIPMENT_STATUS = "shipment_status";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_shipment;
	private String pk_exhibit_list;
	private String shipment_no;
	private UFDate shipment_date;
	private String departure_port;
	private String destination_port;
	private String carrier;
	private String waybill_no;
	private String registrant;
	private Integer shipment_status;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_shipment() {
		return pk_shipment;
	}

	public void setPk_shipment(String pk_shipment) {
		this.pk_shipment = pk_shipment;
	}

	public String getPk_exhibit_list() {
		return pk_exhibit_list;
	}

	public void setPk_exhibit_list(String pk_exhibit_list) {
		this.pk_exhibit_list = pk_exhibit_list;
	}

	public String getShipment_no() {
		return shipment_no;
	}

	public void setShipment_no(String shipment_no) {
		this.shipment_no = shipment_no;
	}

	public UFDate getShipment_date() {
		return shipment_date;
	}

	public void setShipment_date(UFDate shipment_date) {
		this.shipment_date = shipment_date;
	}

	public String getDeparture_port() {
		return departure_port;
	}

	public void setDeparture_port(String departure_port) {
		this.departure_port = departure_port;
	}

	public String getDestination_port() {
		return destination_port;
	}

	public void setDestination_port(String destination_port) {
		this.destination_port = destination_port;
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

	public Integer getShipment_status() {
		return shipment_status;
	}

	public void setShipment_status(Integer shipment_status) {
		this.shipment_status = shipment_status;
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
		return PK_SHIPMENT;
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
		entity.setPKFieldName(PK_SHIPMENT);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[18];
		props[0] = PropertyMetaDataFactory.createDefault(PK_SHIPMENT, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT_LIST, "清单主键", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(SHIPMENT_NO, "出运单号", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(SHIPMENT_DATE, "出运日期", UFDate.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(DEPARTURE_PORT, "出境口岸", String.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(DESTINATION_PORT, "目的口岸", String.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(CARRIER, "承运人", String.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(WAYBILL_NO, "运单号", String.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(REGISTRANT, "登记人", String.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(SHIPMENT_STATUS, "出运状态", Integer.class.getName(), false);
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
