package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class DiffVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_diff";
	public static final String PK_DIFF = "pk_diff";
	public static final String PK_EXHIBIT_LIST = "pk_exhibit_list";
	public static final String PK_RETURN = "pk_return";
	public static final String PK_EXHIBIT = "pk_exhibit";
	public static final String DIFF_NO = "diff_no";
	public static final String EXHIBIT_CODE = "exhibit_code";
	public static final String EXHIBIT_NAME = "exhibit_name";
	public static final String SHIPMENT_QTY = "shipment_qty";
	public static final String RETURN_QTY = "return_qty";
	public static final String DIFF_QTY = "diff_qty";
	public static final String DIFF_TYPE = "diff_type";
	public static final String DIFF_STATUS = "diff_status";
	public static final String RETURN_DEADLINE = "return_deadline";
	public static final String RETURN_REMINDED = "return_reminded";
	public static final String HANDLER = "handler";
	public static final String HANDLE_TIME = "handle_time";
	public static final String HANDLE_REMARK = "handle_remark";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_diff;
	private String pk_exhibit_list;
	private String pk_return;
	private String pk_exhibit;
	private String diff_no;
	private String exhibit_code;
	private String exhibit_name;
	private UFDouble shipment_qty;
	private UFDouble return_qty;
	private UFDouble diff_qty;
	private Integer diff_type;
	private Integer diff_status;
	private UFDate return_deadline;
	private Integer return_reminded;
	private String handler;
	private UFDateTime handle_time;
	private String handle_remark;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_diff() {
		return pk_diff;
	}

	public void setPk_diff(String pk_diff) {
		this.pk_diff = pk_diff;
	}

	public String getPk_exhibit_list() {
		return pk_exhibit_list;
	}

	public void setPk_exhibit_list(String pk_exhibit_list) {
		this.pk_exhibit_list = pk_exhibit_list;
	}

	public String getPk_return() {
		return pk_return;
	}

	public void setPk_return(String pk_return) {
		this.pk_return = pk_return;
	}

	public String getPk_exhibit() {
		return pk_exhibit;
	}

	public void setPk_exhibit(String pk_exhibit) {
		this.pk_exhibit = pk_exhibit;
	}

	public String getDiff_no() {
		return diff_no;
	}

	public void setDiff_no(String diff_no) {
		this.diff_no = diff_no;
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

	public UFDouble getReturn_qty() {
		return return_qty;
	}

	public void setReturn_qty(UFDouble return_qty) {
		this.return_qty = return_qty;
	}

	public UFDouble getDiff_qty() {
		return diff_qty;
	}

	public void setDiff_qty(UFDouble diff_qty) {
		this.diff_qty = diff_qty;
	}

	public Integer getDiff_type() {
		return diff_type;
	}

	public void setDiff_type(Integer diff_type) {
		this.diff_type = diff_type;
	}

	public Integer getDiff_status() {
		return diff_status;
	}

	public void setDiff_status(Integer diff_status) {
		this.diff_status = diff_status;
	}

	public UFDate getReturn_deadline() {
		return return_deadline;
	}

	public void setReturn_deadline(UFDate return_deadline) {
		this.return_deadline = return_deadline;
	}

	public Integer getReturn_reminded() {
		return return_reminded;
	}

	public void setReturn_reminded(Integer return_reminded) {
		this.return_reminded = return_reminded;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public UFDateTime getHandle_time() {
		return handle_time;
	}

	public void setHandle_time(UFDateTime handle_time) {
		this.handle_time = handle_time;
	}

	public String getHandle_remark() {
		return handle_remark;
	}

	public void setHandle_remark(String handle_remark) {
		this.handle_remark = handle_remark;
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
		return PK_DIFF;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public VOMetaData getMetaData() throws MetaDataException {
		VOMetaData meta = new VOMetaData();
		EntityMetaData entity = new EntityMetaData();
		entity.setTableName(TABLE_NAME);
		entity.setPackageName(this.getClass().getPackage().getName());
		entity.setParentPKFieldName(null);
		entity.setPKFieldName(PK_DIFF);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[25];
		props[0] = PropertyMetaDataFactory.createDefault(PK_DIFF, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT_LIST, "清单主键", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(PK_RETURN, "回运主键", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT, "展品主键", String.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(DIFF_NO, "差异单号", String.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(EXHIBIT_CODE, "展品编码", String.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(EXHIBIT_NAME, "展品名称", String.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(SHIPMENT_QTY, "原出运数量", UFDouble.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(RETURN_QTY, "实际回运数量", UFDouble.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(DIFF_QTY, "差异数量", UFDouble.class.getName(), false);
		props[10] = PropertyMetaDataFactory.createDefault(DIFF_TYPE, "差异类型", Integer.class.getName(), false);
		props[11] = PropertyMetaDataFactory.createDefault(DIFF_STATUS, "处理状态", Integer.class.getName(), false);
		props[12] = PropertyMetaDataFactory.createDefault(RETURN_DEADLINE, "回运期限", UFDate.class.getName(), false);
		props[13] = PropertyMetaDataFactory.createDefault(RETURN_REMINDED, "是否已提醒", Integer.class.getName(), false);
		props[14] = PropertyMetaDataFactory.createDefault(HANDLER, "处理人", String.class.getName(), false);
		props[15] = PropertyMetaDataFactory.createDefault(HANDLE_TIME, "处理时间", UFDateTime.class.getName(), false);
		props[16] = PropertyMetaDataFactory.createDefault(HANDLE_REMARK, "处理意见", String.class.getName(), false);
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
