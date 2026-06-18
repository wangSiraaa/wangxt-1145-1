import { createPage, ajax, base, high, toast, cardCache, print, output } from 'nc-lightapp-front';
import { createPageIcon } from 'nc-lightapp-front';
import '../index.less';

const { NCAffix, NCDiv, NCTable, NCTooltip, NCButton, NCPopconfirm, NCIcon, NCMsg, NCModal, NCStep, NCMessage } = base;
const { NCCreateSearch, NCTableControl } = high;
const { addCache, getCacheById, updateCache, deleteCacheById, getNextId, getCurrentLastId } = cardCache;

let searchId = '202606ATASHIPLIST_search';
let tableId = '202606ATASHIPLIST_table';
let pagecode = '202606ATASHIPLIST';
let appcode = '202606ATASHIP';
let cardPagecode = '202606ATASHIPCARD';
let dataSource = 'ata.shipment.list.cache';

class ShipmentList extends React.Component {
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
            moduleId: '202606ATASHIPLIST',
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
                oid: '1001Z310000000000000',
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
            if (item.attrcode === 'shipment_status') {
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
        let status = record.values.shipment_status && record.values.shipment_status.value;
        let statusText = '';
        let color = '';
        if (status == 0) {
            statusText = this.state.json['202606ATASHIPLIST-000000'];
            color = 'default';
        } else if (status == 1) {
            statusText = this.state.json['202606ATASHIPLIST-000001'];
            color = 'success';
        }
        return (
            <span>
                <NCTooltip placement="top" overlay={statusText}>
                    <NCButton size="sm" colors={color}>{statusText}</NCButton>
                </NCTooltip>
            </span>
        );
    };

    renderOpr = (text, record, index) => {
        let status = record.values.shipment_status && record.values.shipment_status.value;
        return (
            <span>
                <a
                    style={{ cursor: 'pointer', padding: '0 5px' }}
                    onClick={() => this.btnOprEdit.call(this, record, index)}
                >
                    {this.state.json['202606ATASHIPLIST-000002']}
                </a>
                {status == 0 && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px' }}
                        onClick={() => this.btnOprRegister.call(this, record, index)}
                    >
                        {this.state.json['202606ATASHIPLIST-000003']}
                    </a>
                )}
                {status == 1 && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px' }}
                        onClick={() => this.btnOprCancelRegister.call(this, record, index)}
                    >
                        {this.state.json['202606ATASHIPLIST-000004']}
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
        queryInfo.oid = queryInfo.oid || '1001Z310000000000000';
        if (searchVal) {
            queryInfo.querycondition = searchVal;
        }
        let data = {
            queryInfo: queryInfo,
            pageCode: pagecode
        };
        ajax({
            url: '/nccloud/ata/shipment/query.do',
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
        let pk_shipment = record.values.pk_shipment.value;
        this.props.pushTo('/card', {
            status: 'edit',
            id: pk_shipment,
            pagecode: cardPagecode
        });
    };

    btnOprRegister = (record, index) => {
        let pk_shipment = record.values.pk_shipment.value;
        ajax({
            url: '/nccloud/ata/shipment/register.do',
            data: {
                pk: pk_shipment,
                pageCode: pagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.getData();
                    this.showToast('success', this.state.json['202606ATASHIPLIST-000005']);
                }
            }
        });
    };

    btnOprCancelRegister = (record, index) => {
        let pk_shipment = record.values.pk_shipment.value;
        ajax({
            url: '/nccloud/ata/shipment/cancelregister.do',
            data: {
                pk: pk_shipment,
                pageCode: pagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.getData();
                    this.showToast('success', this.state.json['202606ATASHIPLIST-000006']);
                }
            }
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
            this.showToast('warning', this.state.json['202606ATASHIPLIST-000007']);
            return;
        }
        let delData = [];
        checkedRows.forEach((row) => {
            delData.push({
                pk: row.data.values.pk_shipment.value,
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
            url: '/nccloud/ata/shipment/delete.do',
            data: {
                pageCode: pagecode,
                temps: delData
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.getData();
                    this.showToast('success', this.state.json['202606ATASHIPLIST-000008']);
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
        this.showToast('success', this.state.json['202606ATASHIPLIST-000009']);
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
                            <h2 className="title-search-detail">{this.state.json['202606ATASHIPLIST-000010']}</h2>
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
                        <NCModal.Title>{this.state.json['202606ATASHIPLIST-000011']}</NCModal.Title>
                    </NCModal.Header>
                    <NCModal.Body>
                        <p>{this.state.json['202606ATASHIPLIST-000012']}</p>
                    </NCModal.Body>
                    <NCModal.Footer>
                        <NCButton fieldid="confirmdel" onClick={this.onConfirmDel.bind(this)} colors="primary">
                            {this.state.json['202606ATASHIPLIST-000013']}
                        </NCButton>
                        <NCButton fieldid="canceldel" onClick={this.onCancelDel.bind(this)}>
                            {this.state.json['202606ATASHIPLIST-000014']}
                        </NCButton>
                    </NCModal.Footer>
                </NCModal>
            </div>
        );
    }
}

export default ShipmentList = createPage({
    billinfo: {
        billtype: 'grid',
        pagecode: pagecode,
        headcode: tableId,
        searchcode: searchId
    },
    mutiLangCode: '202606ATASHIPLIST'
})(ShipmentList);
