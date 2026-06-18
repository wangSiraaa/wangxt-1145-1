package nccloud.web.ata.vo;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.*;
import nc.md.model.MetaDataException;
import nc.vo.pubapp.pattern.model.meta.entity.vo.*;

public class DocumentVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ata_document";
	public static final String PK_DOCUMENT = "pk_document";
	public static final String PK_EXHIBIT_LIST = "pk_exhibit_list";
	public static final String DOCUMENT_NO = "document_no";
	public static final String VALID_FROM = "valid_from";
	public static final String VALID_TO = "valid_to";
	public static final String ISSUING_AUTHORITY = "issuing_authority";
	public static final String ISSUE_DATE = "issue_date";
	public static final String GUARANTEE_AMOUNT = "guarantee_amount";
	public static final String DOC_STATUS = "doc_status";
	public static final String REVIEWER = "reviewer";
	public static final String REVIEW_TIME = "review_time";
	public static final String REVIEW_REMARK = "review_remark";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DR = "dr";
	public static final String TS = "ts";

	private String pk_document;
	private String pk_exhibit_list;
	private String document_no;
	private UFDate valid_from;
	private UFDate valid_to;
	private String issuing_authority;
	private UFDate issue_date;
	private UFDouble guarantee_amount;
	private Integer doc_status;
	private String reviewer;
	private UFDateTime review_time;
	private String review_remark;
	private String pk_group;
	private String pk_org;
	private String creator;
	private UFDateTime creationtime;
	private String modifier;
	private UFDateTime modifiedtime;
	private Integer dr;
	private UFDateTime ts;

	public String getPk_document() {
		return pk_document;
	}

	public void setPk_document(String pk_document) {
		this.pk_document = pk_document;
	}

	public String getPk_exhibit_list() {
		return pk_exhibit_list;
	}

	public void setPk_exhibit_list(String pk_exhibit_list) {
		this.pk_exhibit_list = pk_exhibit_list;
	}

	public String getDocument_no() {
		return document_no;
	}

	public void setDocument_no(String document_no) {
		this.document_no = document_no;
	}

	public UFDate getValid_from() {
		return valid_from;
	}

	public void setValid_from(UFDate valid_from) {
		this.valid_from = valid_from;
	}

	public UFDate getValid_to() {
		return valid_to;
	}

	public void setValid_to(UFDate valid_to) {
		this.valid_to = valid_to;
	}

	public String getIssuing_authority() {
		return issuing_authority;
	}

	public void setIssuing_authority(String issuing_authority) {
		this.issuing_authority = issuing_authority;
	}

	public UFDate getIssue_date() {
		return issue_date;
	}

	public void setIssue_date(UFDate issue_date) {
		this.issue_date = issue_date;
	}

	public UFDouble getGuarantee_amount() {
		return guarantee_amount;
	}

	public void setGuarantee_amount(UFDouble guarantee_amount) {
		this.guarantee_amount = guarantee_amount;
	}

	public Integer getDoc_status() {
		return doc_status;
	}

	public void setDoc_status(Integer doc_status) {
		this.doc_status = doc_status;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public UFDateTime getReview_time() {
		return review_time;
	}

	public void setReview_time(UFDateTime review_time) {
		this.review_time = review_time;
	}

	public String getReview_remark() {
		return review_remark;
	}

	public void setReview_remark(String review_remark) {
		this.review_remark = review_remark;
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
		return PK_DOCUMENT;
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
		entity.setPKFieldName(PK_DOCUMENT);
		entity.setName(this.getClass().getName());

		PropertyMetaData[] props = new PropertyMetaData[20];
		props[0] = PropertyMetaDataFactory.createDefault(PK_DOCUMENT, "主键", String.class.getName(), true);
		props[1] = PropertyMetaDataFactory.createDefault(PK_EXHIBIT_LIST, "清单主键", String.class.getName(), false);
		props[2] = PropertyMetaDataFactory.createDefault(DOCUMENT_NO, "ATA单证号", String.class.getName(), false);
		props[3] = PropertyMetaDataFactory.createDefault(VALID_FROM, "有效期起", UFDate.class.getName(), false);
		props[4] = PropertyMetaDataFactory.createDefault(VALID_TO, "有效期止", UFDate.class.getName(), false);
		props[5] = PropertyMetaDataFactory.createDefault(ISSUING_AUTHORITY, "签发机关", String.class.getName(), false);
		props[6] = PropertyMetaDataFactory.createDefault(ISSUE_DATE, "签发日期", UFDate.class.getName(), false);
		props[7] = PropertyMetaDataFactory.createDefault(GUARANTEE_AMOUNT, "担保金额", UFDouble.class.getName(), false);
		props[8] = PropertyMetaDataFactory.createDefault(DOC_STATUS, "单证状态", Integer.class.getName(), false);
		props[9] = PropertyMetaDataFactory.createDefault(REVIEWER, "审核人", String.class.getName(), false);
		props[10] = PropertyMetaDataFactory.createDefault(REVIEW_TIME, "审核时间", UFDateTime.class.getName(), false);
		props[11] = PropertyMetaDataFactory.createDefault(REVIEW_REMARK, "审核意见", String.class.getName(), false);
		props[12] = PropertyMetaDataFactory.createDefault(PK_GROUP, "集团主键", String.class.getName(), false);
		props[13] = PropertyMetaDataFactory.createDefault(PK_ORG, "组织主键", String.class.getName(), false);
		props[14] = PropertyMetaDataFactory.createDefault(CREATOR, "创建人", String.class.getName(), false);
		props[15] = PropertyMetaDataFactory.createDefault(CREATIONTIME, "创建时间", UFDateTime.class.getName(), false);
		props[16] = PropertyMetaDataFactory.createDefault(MODIFIER, "修改人", String.class.getName(), false);
		props[17] = PropertyMetaDataFactory.createDefault(MODIFIEDTIME, "修改时间", UFDateTime.class.getName(), false);
		props[18] = PropertyMetaDataFactory.createDefault(DR, "删除标记", Integer.class.getName(), false);
		props[19] = PropertyMetaDataFactory.createDefault(TS, "时间戳", UFDateTime.class.getName(), false);

		entity.setProperties(props);
		meta.addEntityMetaData(entity);
		return meta;
	}
}
