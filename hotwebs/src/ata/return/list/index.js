import { createPage, ajax, base, high, toast, cardCache, print, output } from 'nc-lightapp-front';
import { createPageIcon } from 'nc-lightapp-front';
import '../index.less';

const { NCAffix, NCDiv, NCTable, NCTooltip, NCButton, NCPopconfirm, NCIcon, NCMsg, NCModal, NCStep, NCMessage, NCTag } = base;
const { NCCreateSearch, NCTableControl } = high;
const { addCache, getCacheById, updateCache, deleteCacheById, getNextId, getCurrentLastId } = cardCache;

let searchId = '202606ATARETULIST_search';
let tableId = '202606ATARETULIST_table';
let pagecode = '202606ATARETULIST';
let appcode = '202606ATARETU';
let cardPagecode = '202606ATARETUCARD';
let diffPagecode = '202606ATADIFFLIST';
let dataSource = 'ata.return.list.cache';

class ReturnList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            json: {},
            inlt: null,
            showToast: false,
            toastColor: '',
            toastContent: '',
            checkedRows: [],
            record: {},
            index: null,
            showDeleteModal: false,
            delData: null
        };
    }

    componentDidMount() {
        let callback = (json, status, inlt) => {
            if (status) {
                this.setState({ json, inlt }, () => {
                    this.initTemplate();
                });
            } else {
                console.log('未加载到多语资源');
            }
        };
        this.props.MultiInit.getMultiLang({
            moduleId: '202606ATARETULIST',
            domainName: 'ata',
            callback
        });
    }

    initTemplate = () => {
        let callback = (json) => {
            this.props.createSearchTemplate.call(this, {
                pagecode: pagecode,
                searchcode: searchId,
                tablecode: tableId,
                oid: '1001Z310000000000001',
                dataSource: dataSource,
                initQueryCondition: this.initQueryCondition,
                clickSearchBtn: this.clickSearchBtn.bind(this),
                clickQueryBtn: this.clickQueryBtn.bind(this),
                clickAssginSure: this.clickAssginSure.bind(this)
            });
            this.props.createUIDom(
                {
                    pagecode: pagecode,
                    appcode: appcode
                },
                (data) => {
                    if (data) {
                        if (data.template) {
                            let meta = data.template;
                            meta = this.modifierMeta(meta);
                            this.props.meta.setMeta(meta);
                        }
                        if (data.button) {
                            let button = data.button;
                            this.props.button.setButtons(button);
                        }
                        this.getData();
                    }
                }
            );
        };
    };

    initQueryCondition = () => {
        return [
            {
                field: 'dr',
                value: { first: 0, second: null },
                opr: '='
            }
        ];
    };

    clickSearchBtn = (props, val) => {
        let pageInfo = props.table.getTablePageInfo(tableId);
        let searchVal = props.search.getAllSearchData(searchId);
        if (searchVal) {
            this.getData({ pageInfo, searchVal });
        }
    };

    clickQueryBtn = (props, val) => {
        let pageInfo = props.table.getTablePageInfo(tableId);
        let searchVal = props.search.getAllSearchData(searchId);
        if (searchVal) {
            this.getData({ pageInfo, searchVal });
        }
    };

    clickAssginSure = () => {
        return;
    };

    modifierMeta = (meta) => {
        meta[tableId].items = meta[tableId].items.map((item) => {
            if (item.attrcode === 'return_status') {
                item.render = (text, record, index) => {
                    return this.renderStatus(text, record, index);
                };
            }
            if (item.attrcode === 'opr') {
                item.render = (text, record, index) => {
                    return this.renderOpr(text, record, index);
                };
            }
            return item;
        });
        return meta;
    };

    renderStatus = (text, record, index) => {
        let status = record.values.return_status && record.values.return_status.value;
        let diffCount = record.values.diff_count && record.values.diff_count.value;
        let statusText = '';
        let color = '';
        if (status == 0) {
            statusText = this.state.json['202606ATARETULIST-000000'] || '待登记';
            color = 'default';
        } else if (status == 1) {
            statusText = this.state.json['202606ATARETULIST-000001'] || '已回运';
            color = 'success';
        } else if (status == 2) {
            statusText = this.state.json['202606ATARETULIST-000002'] || '有差异';
            color = 'warning';
        }
        return (
            <span>
                <NCTooltip placement="top" overlay={statusText}>
                    <span>
                        <NCButton size="sm" colors={color}>{statusText}</NCButton>
                        {status == 2 && diffCount && Number(diffCount) > 0 && (
                            <NCTag
                                color="orange"
                                style={{ marginLeft: '4px', fontSize: '11px' }}
                            >
                                {`${this.state.json['202606ATARETULIST-000003'] || '差异'} ${diffCount}`}
                            </NCTag>
                        )}
                    </span>
                </NCTooltip>
            </span>
        );
    };

    renderOpr = (text, record, index) => {
        let status = record.values.return_status && record.values.return_status.value;
        return (
            <span>
                <a
                    style={{ cursor: 'pointer', padding: '0 5px' }}
                    onClick={() => this.btnOprEdit.call(this, record, index)}
                >
                    {this.state.json['202606ATARETULIST-000004'] || '编辑'}
                </a>
                {status == 0 && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px' }}
                        onClick={() => this.btnOprRegister.call(this, record, index)}
                    >
                        {this.state.json['202606ATARETULIST-000005'] || '登记回运'}
                    </a>
                )}
                {(status == 1 || status == 2) && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px' }}
                        onClick={() => this.btnOprCancelRegister.call(this, record, index)}
                    >
                        {this.state.json['202606ATARETULIST-000006'] || '取消登记'}
                    </a>
                )}
                {status == 2 && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px', color: '#d46b08' }}
                        onClick={() => this.btnOprViewDiff.call(this, record, index)}
                    >
                        {this.state.json['202606ATARETULIST-000007'] || '查看差异'}
                    </a>
                )}
            </span>
        );
    };

    getData = (p = {}) => {
        let { pageInfo, searchVal } = p;
        if (!pageInfo) {
            pageInfo = {
                pageIndex: 0,
                pageSize: 10
            };
        }
        let queryInfo = this.props.search.getQueryInfo(searchId, true);
        queryInfo.pageInfo = pageInfo;
        queryInfo.pageCode = pagecode;
        queryInfo.oid = queryInfo.oid || '1001Z310000000000001';
        if (searchVal) {
            queryInfo.querycondition = searchVal;
        }
        let data = {
            queryInfo: queryInfo,
            pageCode: pagecode
        };
        ajax({
            url: '/nccloud/ata/return/query.do',
            data: data,
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    if (data && data[tableId]) {
                        this.props.table.setAllTableData(tableId, data[tableId]);
                    } else {
                        this.props.table.setAllTableData(tableId, { rows: [] });
                    }
                }
            }
        });
    };

    btnOprEdit = (record, index) => {
        let pk_return = record.values.pk_return.value;
        this.props.pushTo('/card', {
            status: 'edit',
            id: pk_return,
            pagecode: cardPagecode
        });
    };

    btnOprRegister = (record, index) => {
        let pk_return = record.values.pk_return.value;
        this.props.pushTo('/card', {
            status: 'edit',
            id: pk_return,
            action: 'register',
            pagecode: cardPagecode
        });
    };

    btnOprCancelRegister = (record, index) => {
        let pk_return = record.values.pk_return.value;
        ajax({
            url: '/nccloud/ata/return/cancelregister.do',
            data: {
                pk: pk_return,
                pageCode: pagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.getData();
                    this.showToast('success', this.state.json['202606ATARETULIST-000008'] || '取消登记成功');
                }
            }
        });
    };

    btnOprViewDiff = (record, index) => {
        let pk_exhibit_list = record.values.pk_exhibit_list && record.values.pk_exhibit_list.value;
        let pk_return = record.values.pk_return.value;
        this.props.pushTo('/diff', {
            status: 'browse',
            id: pk_return,
            pk_exhibit_list: pk_exhibit_list,
            pagecode: diffPagecode
        });
    };

    showToast = (color, content) => {
        this.setState({
            showToast: true,
            toastColor: color,
            toastContent: content
        }, () => {
            setTimeout(() => {
                this.setState({ showToast: false });
            }, 3000);
        });
    };

    buttonClick = (props, id) => {
        switch (id) {
            case 'Add':
                this.btnAdd();
                break;
            case 'Delete':
                this.btnDelete();
                break;
            case 'Refresh':
                this.btnRefresh();
                break;
            default:
                break;
        }
    };

    btnAdd = () => {
        this.props.pushTo('/card', {
            status: 'add',
            pagecode: cardPagecode
        });
    };

    btnDelete = () => {
        let { checkedRows } = this.props.table.getCheckedRows(tableId);
        if (checkedRows.length == 0) {
            this.showToast('warning', this.state.json['202606ATARETULIST-000009'] || '请选择要删除的数据');
            return;
        }
        let delData = [];
        checkedRows.forEach((row) => {
            delData.push({
                pk: row.data.values.pk_return.value,
                ts: row.data.values.ts.value
            });
        });
        this.setState({
            showDeleteModal: true,
            delData: delData
        });
    };

    onConfirmDel = () => {
        let { delData } = this.state;
        ajax({
            url: '/nccloud/ata/return/delete.do',
            data: {
                pageCode: pagecode,
                temps: delData
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.getData();
                    this.showToast('success', this.state.json['202606ATARETULIST-000010'] || '删除成功');
                }
            }
        });
        this.setState({
            showDeleteModal: false,
            delData: null
        });
    };

    onCancelDel = () => {
        this.setState({
            showDeleteModal: false,
            delData: null
        });
    };

    btnRefresh = () => {
        this.getData();
        this.showToast('success', this.state.json['202606ATARETULIST-000011'] || '刷新成功');
    };

    render() {
        let { table, search, button } = this.props;
        let { createSimpleTable } = table;
        let { NCCreateSearch } = search;
        let { createButtonApp } = button;
        const { createMessage } = NCModal;
        return (
            <div className="nc-single-table">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-singleTable-header-area">
                        <div className="header-title-search-area">
                            {createPageIcon()}
                            <h2 className="title-search-detail">{this.state.json['202606ATARETULIST-000012'] || '回运登记'}</h2>
                        </div>
                        <div className="header-button-area">
                            {createButtonApp({
                                area: 'list_head',
                                buttonLimit: 3,
                                onButtonClick: this.buttonClick.bind(this),
                                popContainer: document.querySelector('.header-button-area')
                            })}
                        </div>
                    </NCDiv>
                </NCAffix>
                <div className="nc-singleTable-search-area">
                    <NCDiv areaCode={NCDiv.config.SEARCH} className="search-area">
                        {NCCreateSearch(searchId, {
                            clickSearchBtn: this.clickSearchBtn.bind(this),
                            showAdvBtn: true
                        })}
                    </NCDiv>
                </div>
                <NCDiv areaCode={NCDiv.config.TABLE} className="nc-singleTable-table-area">
                    {createSimpleTable(tableId, {
                        handlePageInfoChange: this.getData.bind(this),
                        showCheck: true,
                        showIndex: true,
                        dataSource: dataSource
                    })}
                </NCDiv>
                {this.state.showToast && <NCMsg.Success content={this.state.toastContent} duration={3} />}
                <NCModal
                    show={this.state.showDeleteModal}
                    onHide={this.onCancelDel.bind(this)}
                    backdrop={true}
                    fieldid="deleteConfirm"
                >
                    <NCModal.Header closeButton={true}>
                        <NCModal.Title>{this.state.json['202606ATARETULIST-000013'] || '删除确认'}</NCModal.Title>
                    </NCModal.Header>
                    <NCModal.Body>
                        <p>{this.state.json['202606ATARETULIST-000014'] || '确定要删除选中的回运登记单吗？'}</p>
                    </NCModal.Body>
                    <NCModal.Footer>
                        <NCButton fieldid="confirmdel" onClick={this.onConfirmDel.bind(this)} colors="primary">
                            {this.state.json['202606ATARETULIST-000015'] || '确定'}
                        </NCButton>
                        <NCButton fieldid="canceldel" onClick={this.onCancelDel.bind(this)}>
                            {this.state.json['202606ATARETULIST-000016'] || '取消'}
                        </NCButton>
                    </NCModal.Footer>
                </NCModal>
            </div>
        );
    }
}

export default ReturnList = createPage({
    billinfo: {
        billtype: 'grid',
        pagecode: pagecode,
        headcode: tableId,
        searchcode: searchId
    },
    mutiLangCode: '202606ATARETULIST'
})(ReturnList);
