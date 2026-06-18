package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class ExhibitListVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_exhibit_list";
	public static final String PK_EXHIBIT_LIST = "pk_exhibit_list";
	public static final String LIST_CODE = "list_code";
	public static final String LIST_NAME = "list_name";
	public static final String PK_EXHIBITOR = "pk_exhibitor";
	public static final String EXHIBITOR_NAME = "exhibitor_name";
	public static final String EXHIBITION_NAME = "exhibition_name";
	public static final String APPLY_DATE = "apply_date";
	public static final String LIST_STATUS = "list_status";
	public static final String REMARK = "remark";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_exhibit_list;
	private String list_code;
	private String list_name;
	private String pk_exhibitor;
	private String exhibitor_name;
	private String exhibition_name;
	private UFDate apply_date;
	private Integer list_status;
	private String remark;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_exhibit_list() {
		return pk_exhibit_list;
	}

	public void setPk_exhibit_list(String pk_exhibit_list) {
		this.pk_exhibit_list = pk_exhibit_list;
	}

	public String getList_code() {
		return list_code;
	}

	public void setList_code(String list_code) {
		this.list_code = list_code;
	}

	public String getList_name() {
		return list_name;
	}

	public void setList_name(String list_name) {
		this.list_name = list_name;
	}

	public String getPk_exhibitor() {
		return pk_exhibitor;
	}

	public void setPk_exhibitor(String pk_exhibitor) {
		this.pk_exhibitor = pk_exhibitor;
	}

	public String getExhibitor_name() {
		return exhibitor_name;
	}

	public void setExhibitor_name(String exhibitor_name) {
		this.exhibitor_name = exhibitor_name;
	}

	public String getExhibition_name() {
		return exhibition_name;
	}

	public void setExhibition_name(String exhibition_name) {
		this.exhibition_name = exhibition_name;
	}

	public UFDate getApply_date() {
		return apply_date;
	}

	public void setApply_date(UFDate apply_date) {
		this.apply_date = apply_date;
	}

	public Integer getList_status() {
		return list_status;
	}

	public void setList_status(Integer list_status) {
		this.list_status = list_status;
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
		return PK_EXHIBIT_LIST;
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
		entity.setPKFieldName(PK_EXHIBIT_LIST);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[17];
		props[0] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT_LIST, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(LIST_CODE, "清单编号", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(LIST_NAME, "清单名称", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(PK_EXHIBITOR, "展商主键", String.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(EXHIBITOR_NAME, "展商名称", String.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(EXHIBITION_NAME, "展会", String.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(APPLY_DATE, "申请日期", UFDate.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(LIST_STATUS, "清单状态", Integer.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(REMARK, "备注", String.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(PK_GROUP, "集团主键", String.class.getName(), false);
		props[10] = PropertyMetaDataFactory.createDefault(PK_ORG, "组织主键", String.class.getName(), false);
		props[11] = PropertyMetaDataFactory.createDefault(CREATOR, "创建人", String.class.getName(), false);
		props[12] = PropertyMetaDataFactory.createDefault(CREATIONTIME, "创建时间", UFDateTime.class.getName(), false);
		props[13] = PropertyMetaDataFactory.createDefault(MODIFIER, "修改人", String.class.getName(), false);
		props[14] = PropertyMetaDataFactory.createDefault(MODIFIEDTIME, "修改时间", UFDateTime.class.getName(), false);
		props[15] = PropertyMetaDataFactory.createDefault(DR, "删除标记", Integer.class.getName(), false);
		props[16] = PropertyMetaDataFactory.createDefault(TS, "时间戳", UFDateTime.class.getName(), false);

		entity.setProperties(props);
		meta.addEntityMetaData(entity);
		return meta;
	}
}
