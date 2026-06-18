import { createPage, ajax, base, toast, cardCache, print, output, excelImport } from 'nc-lightapp-front';
import { listPageCode, cardPageCode, tableId, searchId, pageTitle } from './const';
import React, { Component } from 'react';
import './index.less';

const { NCAffix, NCDiv, NCTabs, NCButton, NCMessage, NCTooltip, NCModal, NCIcon } = base;
const { NCCheckbox, NCTable, NCCol, NCRow, NCScrollElement, NCAlert } = base;
const { setDefData, getDefData, getCurrentLastId } = cardCache;
const dataSource = 'ata.document.list.cache';

class DocumentList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            showExpiringModal: false,
            expiringList: [],
            expiringCount: 0,
            json: {}
        };
        this.selectedPks = [];
    }

    componentDidMount() {
        this.props.createUIDom({
            pagecode: listPageCode,
            appcode: this.getAppcode()
        }, (data) => {
            if (data) {
                if (data.button) {
                    let button = data.button;
                    this.props.button.setButtons(button);
                }
                if (data.template) {
                    let meta = data.template;
                    meta = this.modifierMeta(meta);
                    this.props.meta.setMeta(meta);
                }
                if (data.json) {
                    this.setState({ json: data.json });
                }
                this.queryExpiring();
                this.getData();
            }
        });
    }

    getAppcode = () => {
        return window['_AppCode_' || 'ATA'];
    }

    modifierMeta = (meta) => {
        let tableItem = meta[tableId];
        let columns = tableItem.items;
        let self = this;

        columns.find((col) => col.attrcode === 'remain_days') && (columns.find((col) => col.attrcode === 'remain_days').render = (text, record, index) => {
            let remain = parseInt(record.valid_to ? this.calRemainDays(record.valid_to) : 0);
            if (remain < 0) {
                return <span style={{ color: '#ff4d4f', fontWeight: 'bold' }}>已过期</span>;
            } else if (remain < 30) {
                return <span style={{ color: '#faad14', fontWeight: 'bold' }}>即将到期{remain}天</span>;
            } else {
                return <span style={{ color: '#52c41a' }}>{remain}天</span>;
            }
        });

        columns.find((col) => col.attrcode === 'doc_status') && (columns.find((col) => col.attrcode === 'doc_status').render = (text, record, index) => {
            let status = parseInt(record.doc_status);
            let statusMap = {
                0: { color: '#bfbfbf', text: '待审核' },
                1: { color: '#52c41a', text: '已通过' },
                2: { color: '#fa8c16', text: '需延期' },
                3: { color: '#ff4d4f', text: '已过期' }
            };
            let obj = statusMap[status] || { color: '#bfbfbf', text: '未知' };
            return <span style={{ color: obj.color, fontWeight: 'bold' }}>{obj.text}</span>;
        });

        columns.find((col) => col.attrcode === 'opr') && (columns.find((col) => col.attrcode === 'opr').render = (text, record, index) => {
            let status = parseInt(record.doc_status);
            let buttonAry = ['Edit'];
            if (status === 0) {
                buttonAry.push('Audit');
            }
            if (status === 1 || status === 2) {
                buttonAry.push('UnAudit');
            }
            buttonAry.push('RemindExtend');
            return this.props.button.createOprationButton(buttonAry, {
                area: 'list_inner',
                buttonLimit: 3,
                onButtonClick: (props, key) => this.buttonClick(props, key, record, index)
            });
        });

        let searchItem = meta[searchId];
        searchItem.items.find((item) => item.attrcode === 'doc_status').options = [
            { display: '全部', value: 'all' },
            { display: '待审核', value: '0' },
            { display: '已通过', value: '1' },
            { display: '需延期', value: '2' },
            { display: '已过期', value: '3' }
        ];
        searchItem.items.find((item) => item.attrcode === 'pk_exhibit_list').isShowUnit = () => false;
        searchItem.items.find((item) => item.attrcode === 'pk_exhibit_list').isTreelazyLoad = false;
        searchItem.items.find((item) => item.attrcode === 'pk_exhibit_list').isMultiSelectedEnabled = false;

        return meta;
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

    getData = () => {
        let searchVal = this.props.search.getAllSearchData(searchId);
        if (searchVal === false) {
            return;
        }
        let data = {
            querycondition: searchVal,
            pageInfo: this.props.table.getTablePageInfo(tableId),
            pagecode: listPageCode,
            queryAreaCode: searchId,
            oid: 'ata_document_query',
            querytype: 'tree'
        };
        ajax({
            url: '/nccloud/ata/document/query.do',
            data: data,
            success: (res) => {
                let { success, data } = res;
                if (success && data && data[tableId]) {
                    this.props.table.setAllTableData(tableId, data[tableId]);
                } else {
                    this.props.table.setAllTableData(tableId, { rows: [] });
                }
            }
        });
    }

    queryExpiring = () => {
        ajax({
            url: '/nccloud/ata/document/queryexpiring.do',
            data: { pagecode: listPageCode },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    this.setState({
                        expiringList: data.list || [],
                        expiringCount: data.count || 0
                    });
                }
            }
        });
    }

    buttonClick = (props, key, record, index) => {
        switch (key) {
            case 'Add':
                this.add();
                break;
            case 'Delete':
                this.delete();
                break;
            case 'Refresh':
                this.getData();
                this.queryExpiring();
                toast({ color: 'success', content: '刷新成功' });
                break;
            case 'BatchRemindExtend':
                this.batchRemindExtend();
                break;
            case 'Edit':
                this.edit(record);
                break;
            case 'Audit':
                this.audit(record);
                break;
            case 'UnAudit':
                this.unAudit(record);
                break;
            case 'RemindExtend':
                this.remindExtend(record);
                break;
            case 'ViewExpiring':
                this.setState({ showExpiringModal: true });
                break;
        }
    }

    onSearchButtonClick = (props, searchVal) => {
        this.getData();
    }

    onResetButtonClick = () => {
        this.props.search.resetAllFieldsValue(searchId);
    }

    onRowDoubleClick = (record, index, props, e) => {
        this.edit(record);
    }

    onSelectedChange = (props, moduleId, record, index, isSelected) => {
        if (isSelected) {
            this.selectedPks.push(record);
        } else {
            this.selectedPks = this.selectedPks.filter(item => item.pk_document.value !== record.pk_document.value);
        }
    }

    onSelectedAll = (props, moduleId, isChecked, checkedRowLength) => {
        if (isChecked) {
            let checkedData = this.props.table.getCheckedRows(tableId);
            this.selectedPks = checkedData.map(item => item.data);
        } else {
            this.selectedPks = [];
        }
    }

    getTableData = () => {
        let checkedData = this.props.table.getCheckedRows(tableId);
        if (checkedData.length === 0) {
            toast({ color: 'warning', content: '请选择数据' });
            return;
        }
        return checkedData.map(item => item.data.values.pk_document.value);
    }

    add = () => {
        this.props.pushTo('/card', {
            status: 'add',
            pagecode: cardPageCode
        });
    }

    edit = (record) => {
        let id = record.pk_document.value;
        this.props.pushTo('/card', {
            status: 'edit',
            id: id,
            pagecode: cardPageCode
        });
    }

    delete = () => {
        let pks = this.getTableData();
        if (!pks) return;
        this.props.modal.show('delete-modal', {
            beSureBtnClick: () => {
                ajax({
                    url: '/nccloud/ata/document/save.do',
                    data: { pks: pks, pagecode: listPageCode, action: 'delete' },
                    success: (res) => {
                        let { success, data } = res;
                        if (success) {
                            toast({ color: 'success', content: '删除成功' });
                            this.getData();
                            this.queryExpiring();
                        }
                    }
                });
            }
        });
    }

    audit = (record) => {
        let pk = record.pk_document.value;
        let remainDays = this.calRemainDays(record.valid_to.value);
        if (remainDays < 0) {
            this.props.modal.show('audit-expired-modal', {
                beSureBtnClick: () => this.doAudit(pk)
            });
        } else if (remainDays < 30) {
            this.props.modal.show('audit-warning-modal', {
                content: `单证剩余${remainDays}天到期，审核后将自动标记为需延期状态，是否继续？`,
                beSureBtnClick: () => this.doAudit(pk)
            });
        } else {
            this.doAudit(pk);
        }
    }

    doAudit = (pk) => {
        ajax({
            url: '/nccloud/ata/document/audit.do',
            data: { pks: [pk], pagecode: listPageCode },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '审核成功' });
                    this.getData();
                    this.queryExpiring();
                }
            }
        });
    }

    unAudit = (record) => {
        let pk = record.pk_document.value;
        ajax({
            url: '/nccloud/ata/document/unaudit.do',
            data: { pks: [pk], pagecode: listPageCode },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '弃审成功' });
                    this.getData();
                }
            }
        });
    }

    remindExtend = (record) => {
        let pk = record.pk_document.value;
        this.props.modal.show('remind-modal', {
            beSureBtnClick: () => this.doRemindExtend([pk])
        });
    }

    batchRemindExtend = () => {
        let pks = this.getTableData();
        if (!pks) return;
        this.props.modal.show('remind-modal', {
            beSureBtnClick: () => this.doRemindExtend(pks)
        });
    }

    doRemindExtend = (pks) => {
        ajax({
            url: '/nccloud/ata/document/remindextend.do',
            data: { pks: pks, pagecode: listPageCode },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    toast({ color: 'success', content: '延期提醒已发送' });
                    this.getData();
                    this.queryExpiring();
                    this.setState({ showExpiringModal: false });
                }
            }
        });
    }

    modalRowRemind = (record, index) => {
        this.doRemindExtend([record.pk_document]);
    }

    closeExpiringModal = () => {
        this.setState({ showExpiringModal: false });
    }

    render() {
        let { table, search, button, modal, ncmodal } = this.props;
        let { createSimpleTable } = table;
        let { NCCreateSearch } = search;
        let { createButton, createButtonApp } = button;
        let { createModal } = modal;
        let { showExpiringModal, expiringList, expiringCount } = this.state;

        return (
            <div className="nc-bill-list">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-bill-header-area">
                        <div className="header-title-search-area">
                            <h2 className="title-search-detail">{pageTitle}</h2>
                        </div>
                        <div className="header-button-area">
                            {createButtonApp({
                                area: 'list_head',
                                buttonLimit: 5,
                                onButtonClick: this.buttonClick,
                                popContainer: document.querySelector('.header-button-area')
                            })}
                        </div>
                    </NCDiv>
                </NCAffix>
                {expiringCount > 0 && (
                    <div className="nc-bill-alert-area" style={{ margin: '10px 16px' }}>
                        <NCAlert
                            type="warning"
                            showIcon
                            message={`有${expiringCount}份单证即将到期，点击查看并提醒延期`}
                            description="请及时提醒展商办理延期手续"
                            closeAfterClick={true}
                            onClick={() => this.buttonClick(this.props, 'ViewExpiring')}
                            action={
                                <NCButton size="small" type="warning" onClick={() => this.buttonClick(this.props, 'ViewExpiring')}>
                                    查看并提醒
                                </NCButton>
                            }
                        />
                    </div>
                )}
                <div className="nc-bill-search-area">
                    {NCCreateSearch(searchId, {
                        clickSearchBtn: this.onSearchButtonClick,
                        clickResetBtn: this.onResetButtonClick,
                        showAdvBtn: true
                    })}
                </div>
                <div className="nc-bill-table-area">
                    {createSimpleTable(tableId, {
                        handleTabChange: () => { },
                        handleSelectedRows: this.onSelectedChange,
                        handleSingleChecked: this.onSelectedChange,
                        handleCheckAll: this.onSelectedAll,
                        showCheck: true,
                        showIndex: true,
                        onRowDoubleClick: this.onRowDoubleClick,
                        dataSource: dataSource,
                        pkname: 'pk_document'
                    })}
                </div>
                <NCModal
                    fieldid="expiring_modal"
                    show={showExpiringModal}
                    onHide={this.closeExpiringModal}
                    title="即将到期单证列表"
                    backdrop="static"
                    size="xlg"
                >
                    <NCModal.Body>
                        <div className="expiring-table">
                            <table className="table nc-theme-area-bgc nc-theme-common-border">
                                <thead>
                                    <tr>
                                        <th style={{ width: '120px' }}>单证号</th>
                                        <th style={{ width: '150px' }}>关联清单号</th>
                                        <th style={{ width: '120px' }}>签发机关</th>
                                        <th style={{ width: '100px' }}>有效期起</th>
                                        <th style={{ width: '100px' }}>有效期止</th>
                                        <th style={{ width: '100px' }}>剩余天数</th>
                                        <th style={{ width: '100px' }}>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {expiringList.map((item, index) => {
                                        let remain = this.calRemainDays(item.valid_to);
                                        return (
                                            <tr key={index}>
                                                <td>{item.document_no}</td>
                                                <td>{item.pk_exhibit_list_name || '-'}</td>
                                                <td>{item.issuing_authority || '-'}</td>
                                                <td>{item.valid_from}</td>
                                                <td>{item.valid_to}</td>
                                                <td>
                                                    {remain < 30 ? (
                                                        <span style={{ color: '#faad14', fontWeight: 'bold' }}>即将到期{remain}天</span>
                                                    ) : (
                                                        <span style={{ color: '#52c41a' }}>{remain}天</span>
                                                    )}
                                                </td>
                                                <td>
                                                    <NCButton
                                                        size="small"
                                                        type="warning"
                                                        onClick={() => this.modalRowRemind(item, index)}
                                                    >
                                                        提醒延期
                                                    </NCButton>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                    {expiringList.length === 0 && (
                                        <tr>
                                            <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#999' }}>
                                                暂无即将到期单证
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </NCModal.Body>
                    <NCModal.Footer>
                        <NCButton
                            fieldid="expiring_batch_remind"
                            colors="warning"
                            onClick={() => {
                                let pks = expiringList.map(item => item.pk_document);
                                if (pks.length > 0) {
                                    this.doRemindExtend(pks);
                                }
                            }}
                        >
                            批量提醒延期
                        </NCButton>
                        <NCButton fieldid="expiring_close" onClick={this.closeExpiringModal}>
                            关闭
                        </NCButton>
                    </NCModal.Footer>
                </NCModal>
                {createModal('delete-modal', {
                    title: '删除确认',
                    content: '确定要删除所选数据吗？',
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
                    content: this.state.expiringWarning || '单证即将到期，审核后将自动标记为需延期状态，是否继续？',
                    beSureBtnName: '确定',
                    cancelBtnName: '取消'
                })}
                {createModal('remind-modal', {
                    title: '提醒确认',
                    content: '确认向展商发送延期提醒？',
                    beSureBtnName: '确定',
                    cancelBtnName: '取消'
                })}
            </div>
        );
    }
}

export default createPage({
    billinfo: {
        billtype: 'list',
        pagecode: listPageCode
    }
})(DocumentList);
