import { createPage, ajax, base, toast, cardCache, high } from 'nc-lightapp-front';
import { cardPageCode, formId, cardTableId, listPageCode, pageTitle } from './const';
import React, { Component } from 'react';
import './index.less';

const { NCAffix, NCDiv, NCButton, NCMessage, NCTooltip, NCModal } = base;
const { BillTrack, ApproveDetail, ApprovalTrans } = high;
const { getDefData, setDefData, getCacheById } = cardCache;
const dataSource = 'ata.document.card.cache';

class DocumentCard extends Component {
    constructor(props) {
        super(props);
        this.state = {
            billtype: 'ata_document',
            billId: '',
            showTrack: false,
            showApproveDetail: false,
            showAssign: false,
            assignData: null,
            exhibitList: [],
            remainDays: 0,
            json: {}
        };
        this.status = props.getUrlParam('status') || 'browse';
        this.pk_document = props.getUrlParam('pk_document') || props.getUrlParam('id') || '';
    }

    componentDidMount() {
        this.props.createUIDom({
            pagecode: cardPageCode,
            appcode: this.getAppcode()
        }, (data) => {
            if (data) {
                if (data.button) {
                    let button = data.button;
                    this.props.button.setButtons(button);
                    this.initButtonStatus(this.status);
                }
                if (data.template) {
                    let meta = data.template;
                    meta = this.modifierMeta(meta);
                    this.props.meta.setMeta(meta, () => {
                        if (this.status === 'add') {
                            this.props.form.EmptyAllFormValue(formId);
                            this.props.cardTable.setTableData(cardTableId, { rows: [] });
                        } else if (this.status === 'edit' || this.status === 'browse') {
                            this.getData();
                        }
                    });
                }
                if (data.json) {
                    this.setState({ json: data.json });
                }
            }
        });
    }

    getAppcode = () => {
        return window['_AppCode_' || 'ATA'];
    }

    modifierMeta = (meta) => {
        let formItem = meta[formId];
        let self = this;

        formItem.items.find((item) => item.attrcode === 'pk_exhibit_list') && (formItem.items.find((item) => item.attrcode === 'pk_exhibit_list').isShowUnit = () => false);
        formItem.items.find((item) => item.attrcode === 'pk_exhibit_list') && (formItem.items.find((item) => item.attrcode === 'pk_exhibit_list').isTreelazyLoad = false);
        formItem.items.find((item) => item.attrcode === 'pk_exhibit_list') && (formItem.items.find((item) => item.attrcode === 'pk_exhibit_list').isMultiSelectedEnabled = false);
        formItem.items.find((item) => item.attrcode === 'pk_exhibit_list') && (formItem.items.find((item) => item.attrcode === 'pk_exhibit_list').onAfterEvent = this.onExhibitListAfterEvent.bind(this));

        formItem.items.find((item) => item.attrcode === 'valid_to') && (formItem.items.find((item) => item.attrcode === 'valid_to').onAfterEvent = this.onValidToAfterEvent.bind(this));

        formItem.items.find((item) => item.attrcode === 'doc_status') && (formItem.items.find((item) => item.attrcode === 'doc_status').render = (text, record, index) => {
            let value = this.props.form.getFormItemsValue(formId, 'doc_status');
            let status = value ? parseInt(value.value) : 0;
            let statusMap = {
                0: { color: '#bfbfbf', text: '待审核' },
                1: { color: '#52c41a', text: '已通过' },
                2: { color: '#fa8c16', text: '需延期' },
                3: { color: '#ff4d4f', text: '已过期' }
            };
            let obj = statusMap[status] || { color: '#bfbfbf', text: '未知' };
            return <span style={{ color: obj.color, fontWeight: 'bold', fontSize: '14px' }}>{obj.text}</span>;
        });

        let tableItem = meta[cardTableId];
        tableItem && (tableItem.items.find((item) => item.attrcode === 'serial_no') && (tableItem.items.find((item) => item.attrcode === 'serial_no').render = (text, record, index) => {
            let value = record && record.values && record.values.serial_no && record.values.serial_no.value;
            if (!value) {
                return <span style={{ color: '#ff4d4f', border: '1px solid #ff4d4f', padding: '2px 8px', borderRadius: '4px', backgroundColor: '#fff2f0' }}>缺失</span>;
            }
            return <span>{value}</span>;
        }));

        return meta;
    }

    initButtonStatus = (status) => {
        let { setButtonDisabled, setButtonVisible } = this.props.button;
        this.status = status;
        let buttons = {
            add: { Save: true, SaveAdd: true, Cancel: true, Back: true },
            edit: { Save: true, SaveAdd: true, Cancel: true, Back: true },
            browse: { Add: true, Edit: true, Delete: true, Audit: true, UnAudit: true, RemindExtend: true, Back: true }
        };
        let currentBtns = buttons[status] || buttons.browse;

        let allBtns = ['Add', 'Edit', 'Delete', 'Save', 'SaveAdd', 'Cancel', 'Audit', 'UnAudit', 'RemindExtend', 'Back'];
        allBtns.forEach((btn) => {
            setButtonVisible(btn, !!currentBtns[btn]);
        });

        if (status === 'browse') {
            let docStatus = this.getDocStatus();
            if (docStatus === 0) {
                setButtonDisabled('Audit', false);
                setButtonDisabled('UnAudit', true);
                setButtonDisabled('Edit', false);
                setButtonDisabled('Delete', false);
            } else if (docStatus === 1 || docStatus === 2) {
                setButtonDisabled('Audit', true);
                setButtonDisabled('UnAudit', false);
                setButtonDisabled('Edit', true);
                setButtonDisabled('Delete', true);
            } else if (docStatus === 3) {
                setButtonDisabled('Audit', true);
                setButtonDisabled('UnAudit', true);
                setButtonDisabled('Edit', true);
 setButtonDisabled('Delete', true);
            }
            setButtonDisabled('RemindExtend', false);
        }
    }

    getDocStatus = () => {
        let value = this.props.form.getFormItemsValue(formId, 'doc_status');
        return value ? parseInt(value.value) : 0;
    }

    calRemainDays = (validTo) => {
        if (!validTo) return 0;
        let end = new Date(validTo);
        let now = new Date();
        now.setHours(0, 0, 0, 0);
        end.setHours(0, 0, 0, 0);
        let diff = end.getTime() - now.getTime();
        return Math.floor(diff / (24 * 3600 * 1000));
    }

    updateRemainDays = () => {
        let validTo = this.props.form.getFormItemsValue(formId, 'valid_to');
        if (validTo && validTo.value) {
            let remain = this.calRemainDays(validTo.value);
            this.setState({ remainDays: remain });
        }
    }

    onValidToAfterEvent = (props, moduleId, key, value, oldValue) => {
        this.updateRemainDays();
    }

    onExhibitListAfterEvent = (props, moduleId, key, value, oldValue) => {
        if (value && value.refpk) {
            this.queryExhibitList(value.refpk);
        } else {
            this.props.cardTable.setTableData(cardTableId, { rows: [] });
            this.setState({ exhibitList: [] });
        }
    }

    queryExhibitList = (pkExhibitList) => {
        ajax({
            url: '/nccloud/ata/exhibitlist/querycard.do',
            data: {
                id: pkExhibitList,
                pk_exhibit_list: pkExhibitList,
                pagecode: '202606ATADOCCARD'
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    let exhibitRows = [];
                    if (data && data['ata_exhibit']) {
                        exhibitRows = data['ata_exhibit'].rows || [];
                    }
                    this.props.cardTable.setTableData(cardTableId, { rows: exhibitRows });
                    this.setState({ exhibitList: exhibitRows });
                }
            }
        });
    }

    getData = () => {
        ajax({
            url: '/nccloud/ata/document/query.do',
            data: {
                pk_document: this.pk_document,
                id: this.pk_document,
                pagecode: cardPageCode
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data && data.head) {
                    this.props.form.setAllFormValue({ [formId]: data.head[formId] });
                    let pkExhibitList = this.props.form.getFormItemsValue(formId, 'pk_exhibit_list');
                    if (pkExhibitList && pkExhibitList.value) {
                        this.queryExhibitList(pkExhibitList.value);
                    }
                    this.updateRemainDays();
                    this.setState({ billId: this.pk_document });
                    this.initButtonStatus(this.status);
                }
            }
        });
    }

    buttonClick = (props, key) => {
        switch (key) {
            case 'Add':
                this.add();
                break;
            case 'Edit':
                this.edit();
                break;
            case 'Delete':
                this.delete();
                break;
            case 'Save':
                this.save(false);
                break;
            case 'SaveAdd':
                this.save(true);
                break;
            case 'Cancel':
                this.cancel();
                break;
            case 'Audit':
                this.audit();
                break;
            case 'UnAudit':
                this.unAudit();
                break;
            case 'RemindExtend':
                this.remindExtend();
                break;
            case 'Back':
                this.back();
                break;
        }
    }

    add = () => {
        this.props.form.EmptyAllFormValue(formId);
        this.props.cardTable.setTableData(cardTableId, { rows: [] });
        this.setState({ exhibitList: [], remainDays: 0 });
        this.pk_document = '';
        this.initButtonStatus('add');
        this.props.setUrlParam({ status: 'add', id: '', pk_document: '' });
    }

    edit = () => {
        this.initButtonStatus('edit');
        this.props.setUrlParam({ status: 'edit', id: this.pk_document, pk_document: this.pk_document });
    }

    delete = () => {
        this.props.modal.show('delete-modal', {
            beSureBtnClick: () => {
                ajax({
                    url: '/nccloud/ata/document/save.do',
                    data: {
                        pks: [this.pk_document],
                        pagecode: cardPageCode,
                        action: 'delete'
                    },
                    success: (res) => {
                        let { success, data } = res;
                        if (success) {
                            toast({ color: 'success', content: '删除成功' });
                            this.back();
                        }
                    }
                });
            }
        });
    }

    save = (isSaveAdd) => {
        let flag = this.props.form.isCheckNow(formId);
        if (!flag) return;

        let data = {
            pageid: cardPageCode,
            temps: {
                [formId]: {
                    rows: this.props.form.getAllFormValue(formId).rows
                }
            },
            pagecode: cardPageCode,
            action: this.status === 'add' ? 'save' : 'update'
        };

        ajax({
            url: '/nccloud/ata/document/save.do',
            data: data,
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '保存成功' });
                    let id = data.head && data.head[formId] && data.head[formId].rows && data.head[formId].rows[0]
                        && data.head[formId].rows[0].values.pk_document.value;
                    if (id) {
                        this.pk_document = id;
                        this.setState({ billId: id });
                        this.props.setUrlParam({ status: 'browse', id: id, pk_document: id });
                        this.initButtonStatus('browse');
                        if (isSaveAdd) {
                            this.add();
                        } else {
                            this.getData();
                        }
                    }
                }
            }
        });
    }

    cancel = () => {
        if (this.status === 'add') {
            this.back();
        } else {
            this.props.setUrlParam({ status: 'browse', id: this.pk_document, pk_document: this.pk_document });
            this.getData();
        }
    }

    audit = () => {
        let validTo = this.props.form.getFormItemsValue(formId, 'valid_to');
        let remainDays = this.calRemainDays(validTo && validTo.value);
        if (remainDays < 0) {
            this.props.modal.show('audit-expired-modal', {
                beSureBtnClick: () => this.doAudit()
            });
        } else if (remainDays < 30) {
            this.props.modal.show('audit-warning-modal', {
                content: `单证剩余${remainDays}天到期，审核后将自动标记为需延期状态，是否继续？`,
                beSureBtnClick: () => this.doAudit()
            });
        } else {
            this.doAudit();
        }
    }

    doAudit = () => {
        ajax({
            url: '/nccloud/ata/document/audit.do',
            data: {
                pks: [this.pk_document],
                pagecode: cardPageCode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '审核成功' });
                    this.getData();
                }
            }
        });
    }

    unAudit = () => {
        ajax({
            url: '/nccloud/ata/document/unaudit.do',
            data: {
                pks: [this.pk_document],
                pagecode: cardPageCode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '弃审成功' });
                    this.getData();
                }
            }
        });
    }

    remindExtend = () => {
        this.props.modal.show('remind-modal', {
            beSureBtnClick: () => this.doRemindExtend()
        });
    }

    doRemindExtend = () => {
        ajax({
            url: '/nccloud/ata/document/remindextend.do',
            data: {
                pks: [this.pk_document],
                pagecode: cardPageCode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '延期提醒已发送' });
                    this.getData();
                }
            }
        });
    }

    back = () => {
        this.props.pushTo('/list', {
            pagecode: listPageCode
        });
    }

    render() {
        let { form, button, cardTable, modal, billHead } = this.props;
        let { createForm } = form;
        let { createCardTable } = cardTable;
        let { createButton, createButtonApp } = button;
        let { createModal } = modal;
        let { createBillHeadInfo } = billHead;
        let { remainDays } = this.state;

        let remainHtml = '';
        if (this.status !== 'add') {
            if (remainDays < 0) {
                remainHtml = <span style={{ color: '#ff4d4f', fontWeight: 'bold', marginLeft: '10px' }}>（已过期）</span>;
            } else if (remainDays < 30) {
                remainHtml = <span style={{ color: '#faad14', fontWeight: 'bold', marginLeft: '10px' }}>（剩余{remainDays}天到期）</span>;
            } else {
                remainHtml = <span style={{ color: '#52c41a', marginLeft: '10px' }}>（剩余{remainDays}天）</span>;
            }
        }

        return (
            <div className="nc-bill-card">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-bill-header-area">
                        <div className="header-title-search-area">
                            {createBillHeadInfo({
                                title: pageTitle,
                                backBtnClick: this.back,
                                billCode: this.props.form.getFormItemsValue(formId, 'document_no') && this.props.form.getFormItemsValue(formId, 'document_no').value || ''
                            })}
                        </div>
                        <div className="header-button-area">
                            {createButtonApp({
                                area: 'card_head',
                                buttonLimit: 8,
                                onButtonClick: this.buttonClick,
                                popContainer: document.querySelector('.header-button-area')
                            })}
                        </div>
                    </NCDiv>
                </NCAffix>
                <div className="nc-bill-form-area">
                    {createForm(formId, {
                        onAfterEvent: this.onAfterEvent,
                        expandArr: [{
                            key: 'form_expand_1',
                            title: '基本信息',
                            expanded: true
                        }]
                    })}
                </div>
                <div className="nc-bill-bottom-area">
                    <div className="title-style">
                        <span className="text-left-title">关联展品信息</span>
                        {remainHtml}
                    </div>
                    <div className="nc-bill-table-area">
                        {createCardTable(cardTableId, {
                            showIndex: true,
                            showCheck: false,
                            tableHead: <span>关联展品信息</span>,
                            hideDefaultButtons: true
                        })}
                    </div>
                </div>
                {createModal('delete-modal', {
                    title: '删除确认',
                    content: '确定要删除该单证吗？',
                    beSureBtnName: '确定',
                    cancelBtnName: '取消'
                })}
                {createModal('audit-expired-modal', {
                    title: '有效期已过',
                    content: '该单证有效期已过，审核将标记为已过期状态，是否继续？',
                    beSureBtnName: '确定',
                    cancelBtnName: '取消'
                })}
                {createModal('audit-warning-modal', {
                    title: '有效期即将到期',
                    content: this.state.auditWarningContent || '单证即将到期，审核后将自动标记为需延期状态，是否继续？',
                    beSureBtnName: '确定',
                    cancelBtnName: '取消'
                })}
                {createModal('remind-modal', {
                    title: '提醒确认',
                    content: '确认向展商发送延期提醒？',
                    beSureBtnName: '确定',
                    cancelBtnName: '取消'
                })}
                <ApproveDetail
                    show={this.state.showApproveDetail}
                    close={() => this.setState({ showApproveDetail: false })}
                    billid={this.state.billId}
                    billtype={this.state.billtype}
                />
                <BillTrack
                    show={this.state.showTrack}
                    close={() => this.setState({ showTrack: false })}
                    id={this.state.billId}
                    billtype={this.state.billtype}
                />
                <ApprovalTrans
                    title="指派"
                    show={this.state.showAssign}
                    assignData={this.state.assignData}
                    onClose={() => this.setState({ showAssign: false })}
                    onOk={(value) => {
                        this.setState({ showAssign: false });
                        if (value && value.userObj) {
                            let checkQueue = value.checkQueue;
                        }
                    }}
                />
            </div>
        );
    }
}

export default createPage({
    billinfo: {
        billtype: 'card',
        pagecode: cardPageCode,
        headcode: formId,
        bodycode: [cardTableId]
    }
})(DocumentCard);
