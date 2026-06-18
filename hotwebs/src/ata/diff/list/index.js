import { createPage, ajax, base, high, toast, cardCache, print, output } from 'nc-lightapp-front';
import { createPageIcon } from 'nc-lightapp-front';

const { NCAffix, NCDiv, NCTable, NCTooltip, NCButton, NCPopconfirm, NCIcon, NCMsg, NCModal, NCStep, NCMessage, NCTag, NCDropdown, NCMenu } = base;
const { NCCreateSearch, NCTableControl } = high;
const { addCache, getCacheById, updateCache, deleteCacheById, getNextId, getCurrentLastId } = cardCache;

let searchId = '202606ATADIFFLIST_search';
let tableId = '202606ATADIFFLIST_table';
let pagecode = '202606ATADIFFLIST';
let appcode = '202606ATADIFF';
let cardPagecode = '202606ATADIFFCARD';
let dataSource = 'ata.diff.list.cache';

const DIFF_STATUS_MAP = {
    '0': { display: '待处理', color: 'warning', bgColor: '#fffbe6', textColor: '#d46b08' },
    '1': { display: '处理中', color: 'primary', bgColor: '#e6f7ff', textColor: '#1890ff' },
    '2': { display: '已处理', color: 'success', bgColor: '#f6ffed', textColor: '#52c41a' },
    '3': { display: '已关闭', color: 'default', bgColor: '#f5f5f5', textColor: '#8c8c8c' }
};

const DIFF_TYPE_MAP = {
    '1': { display: '丢失', color: 'danger' },
    '2': { display: '损坏', color: 'warning' },
    '3': { display: '变卖', color: 'info' },
    '4': { display: '赠送', color: 'primary' },
    '5': { display: '其他', color: 'default' }
};

class DiffList extends React.Component {
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
            showBatchModal: false,
            batchType: '',
            batchData: null,
            exportLoading: false
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
            moduleId: '202606ATADIFFLIST',
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
                        let pk_return = this.props.getUrlParam('id');
                        let pk_exhibit_list = this.props.getUrlParam('pk_exhibit_list');
                        if (pk_return || pk_exhibit_list) {
                            this.getDataWithParam(pk_return, pk_exhibit_list);
                        } else {
                            this.getData();
                        }
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
            if (item.attrcode === 'diff_qty') {
                item.render = (text, record, index) => {
                    return this.renderDiffQty(text, record, index);
                };
            }
            if (item.attrcode === 'diff_type') {
                item.render = (text, record, index) => {
                    return this.renderDiffType(text, record, index);
                };
            }
            if (item.attrcode === 'diff_status') {
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

    renderDiffQty = (text, record, index) => {
        let diffQty = record.values.diff_qty && record.values.diff_qty.value;
        diffQty = diffQty != null ? Number(diffQty) : 0;
        let color = '#8c8c8c';
        let fontWeight = 'normal';
        let display = diffQty;
        if (diffQty > 0) {
            color = '#ff4d4f';
            fontWeight = 'bold';
            display = `+${diffQty}`;
        } else if (diffQty < 0) {
            color = '#1890ff';
            fontWeight = 'bold';
            display = `${diffQty}`;
        }
        return (
            <span style={{ color: color, fontWeight: fontWeight, fontSize: '13px' }}>
                {display}
            </span>
        );
    };

    renderDiffType = (text, record, index) => {
        let diffType = record.values.diff_type && record.values.diff_type.value;
        let typeInfo = DIFF_TYPE_MAP[diffType] || { display: '-', color: 'default' };
        return (
            <NCTag color={typeInfo.color}>
                {typeInfo.display}
            </NCTag>
        );
    };

    renderStatus = (text, record, index) => {
        let status = record.values.diff_status && record.values.diff_status.value;
        let statusInfo = DIFF_STATUS_MAP[status] || { display: '-', color: 'default' };
        return (
            <NCButton size="sm" colors={statusInfo.color}>
                {statusInfo.display}
            </NCButton>
        );
    };

    renderOpr = (text, record, index) => {
        let status = record.values.diff_status && record.values.diff_status.value;
        return (
            <span>
                <a
                    style={{ cursor: 'pointer', padding: '0 5px' }}
                    onClick={() => this.btnOprView.call(this, record, index)}
                >
                    {status === '0' || status === '1' ? '编辑' : '查看'}
                </a>
                {status === '0' && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px', color: '#faad14' }}
                        onClick={() => this.btnOprStartProcess.call(this, record, index)}
                    >
                        开始处理
                    </a>
                )}
                {status === '1' && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px', color: '#1890ff' }}
                        onClick={() => this.btnOprFinishProcess.call(this, record, index)}
                    >
                        完成处理
                    </a>
                )}
                {status === '2' && (
                    <a
                        style={{ cursor: 'pointer', padding: '0 5px', color: '#8c8c8c' }}
                        onClick={() => this.btnOprClose.call(this, record, index)}
                    >
                        关闭
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
            url: '/nccloud/ata/diff/query.do',
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

    getDataWithParam = (pk_return, pk_exhibit_list) => {
        let queryCondition = [];
        if (pk_return) {
            queryCondition.push({
                field: 'pk_return',
                value: { first: pk_return, second: null },
                opr: '='
            });
        }
        if (pk_exhibit_list) {
            queryCondition.push({
                field: 'pk_exhibit_list',
                value: { first: pk_exhibit_list, second: null },
                opr: '='
            });
        }
        queryCondition.push({
            field: 'dr',
            value: { first: 0, second: null },
            opr: '='
        });
        let pageInfo = {
            pageIndex: 0,
            pageSize: 10
        };
        let data = {
            queryInfo: {
                pageInfo: pageInfo,
                pageCode: pagecode,
                oid: '1001Z310000000000001',
                querycondition: {
                    logic: 'and',
                    conditions: queryCondition
                }
            },
            pageCode: pagecode
        };
        ajax({
            url: '/nccloud/ata/diff/query.do',
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

    btnOprView = (record, index) => {
        let pk_diff = record.values.pk_diff.value;
        let status = record.values.diff_status && record.values.diff_status.value;
        let pageStatus = (status === '0' || status === '1') ? 'edit' : 'browse';
        this.props.pushTo('/card', {
            status: pageStatus,
            id: pk_diff,
            pagecode: cardPagecode
        });
    };

    btnOprStartProcess = (record, index) => {
        let pk_diff = record.values.pk_diff.value;
        this.props.pushTo('/card', {
            status: 'edit',
            id: pk_diff,
            action: 'start',
            pagecode: cardPagecode
        });
    };

    btnOprFinishProcess = (record, index) => {
        let pk_diff = record.values.pk_diff.value;
        this.props.pushTo('/card', {
            status: 'edit',
            id: pk_diff,
            action: 'finish',
            pagecode: cardPagecode
        });
    };

    btnOprClose = (record, index) => {
        let pk_diff = record.values.pk_diff.value;
        let ts = record.values.ts && record.values.ts.value;
        NCMsg.show({
            content: '确定要关闭该差异单吗？',
            beSureBtnClick: () => {
                this.doClose(pk_diff, ts);
            }
        });
    };

    doClose = (pk, ts) => {
        ajax({
            url: '/nccloud/ata/diff/close.do',
            data: {
                pk: pk,
                ts: ts,
                pageCode: cardPagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.getData();
                    this.showToast('success', '关闭成功');
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
            case 'Refresh':
                this.btnRefresh();
                break;
            case 'BatchProcess':
                this.btnBatchProcess();
                break;
            case 'Export':
                this.btnExport();
                break;
            default:
                break;
        }
    };

    btnRefresh = () => {
        this.getData();
        this.showToast('success', '刷新成功');
    };

    btnBatchProcess = () => {
        let { checkedRows } = this.props.table.getCheckedRows(tableId);
        if (checkedRows.length == 0) {
            this.showToast('warning', '请选择要批量处理的数据');
            return;
        }
        this.setState({
            showBatchModal: true,
            batchData: checkedRows
        });
    };

    onBatchTypeSelect = (type) => {
        this.setState({ batchType: type });
    };

    onConfirmBatch = () => {
        let { batchType, batchData } = this.state;
        if (!batchType) {
            this.showToast('warning', '请选择批量处理操作');
            return;
        }
        let temps = batchData.map(row => ({
            pk: row.data.values.pk_diff.value,
            ts: row.data.values.ts && row.data.values.ts.value
        }));
        let url = '';
        if (batchType === 'start') {
            url = '/nccloud/ata/diff/batchstartprocess.do';
        } else if (batchType === 'finish') {
            url = '/nccloud/ata/diff/batchfinishprocess.do';
        } else if (batchType === 'close') {
            url = '/nccloud/ata/diff/batchclose.do';
        }
        ajax({
            url: url,
            data: {
                pageCode: pagecode,
                temps: temps
            },
            success: (res) => {
                let { success, data } = res;
                if (success) {
                    this.setState({
                        showBatchModal: false,
                        batchType: '',
                        batchData: null
                    });
                    this.getData();
                    this.showToast('success', '批量操作成功');
                }
            }
        });
    };

    onCancelBatch = () => {
        this.setState({
            showBatchModal: false,
            batchType: '',
            batchData: null
        });
    };

    btnExport = () => {
        this.setState({ exportLoading: true });
        let queryInfo = this.props.search.getQueryInfo(searchId, true);
        queryInfo.pageCode = pagecode;
        queryInfo.oid = queryInfo.oid || '1001Z310000000000001';
        let data = {
            queryInfo: queryInfo,
            pageCode: pagecode
        };
        ajax({
            url: '/nccloud/ata/diff/export.do',
            data: data,
            success: (res) => {
                this.setState({ exportLoading: false });
                let { success, data } = res;
                if (success && data) {
                    output({
                        url: data.url,
                        fileName: data.fileName || '差异处理列表'
                    });
                }
            },
            error: () => {
                this.setState({ exportLoading: false });
            }
        });
    };

    renderBatchModalContent = () => {
        let { batchData, batchType } = this.state;
        let statusCounts = { '0': 0, '1': 0, '2': 0, '3': 0 };
        batchData && batchData.forEach(row => {
            let status = row.data.values.diff_status && row.data.values.diff_status.value;
            if (statusCounts[status] != undefined) {
                statusCounts[status]++;
            }
        });
        return (
            <div>
                <p style={{ marginBottom: '15px', color: '#666' }}>
                    共选择 <b style={{ color: '#1890ff' }}>{batchData && batchData.length}</b> 条数据：
                </p>
                <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', flexWrap: 'wrap' }}>
                    {Object.keys(DIFF_STATUS_MAP).map(key => (
                        <NCTag key={key} color={DIFF_STATUS_MAP[key].color} style={{ fontSize: '12px' }}>
                            {DIFF_STATUS_MAP[key].display}：{statusCounts[key] || 0}
                        </NCTag>
                    ))}
                </div>
                <p style={{ marginBottom: '10px', fontWeight: 'bold' }}>请选择批量操作：</p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <label style={{
                        cursor: 'pointer',
                        padding: '10px 12px',
                        border: batchType === 'start' ? '1px solid #faad14' : '1px solid #d9d9d9',
                        borderRadius: '4px',
                        backgroundColor: batchType === 'start' ? '#fffbe6' : '#fff'
                    }} onClick={() => this.onBatchTypeSelect('start')}>
                        <input type="radio" checked={batchType === 'start'} style={{ marginRight: '8px' }} readOnly />
                        <span style={{ color: '#d46b08', fontWeight: 'bold' }}>批量开始处理</span>
                        <span style={{ color: '#8c8c8c', marginLeft: '10px', fontSize: '12px' }}>（状态：待处理 → 处理中）</span>
                    </label>
                    <label style={{
                        cursor: 'pointer',
                        padding: '10px 12px',
                        border: batchType === 'finish' ? '1px solid #1890ff' : '1px solid #d9d9d9',
                        borderRadius: '4px',
                        backgroundColor: batchType === 'finish' ? '#e6f7ff' : '#fff'
                    }} onClick={() => this.onBatchTypeSelect('finish')}>
                        <input type="radio" checked={batchType === 'finish'} style={{ marginRight: '8px' }} readOnly />
                        <span style={{ color: '#1890ff', fontWeight: 'bold' }}>批量完成处理</span>
                        <span style={{ color: '#8c8c8c', marginLeft: '10px', fontSize: '12px' }}>（状态：处理中 → 已处理）</span>
                    </label>
                    <label style={{
                        cursor: 'pointer',
                        padding: '10px 12px',
                        border: batchType === 'close' ? '1px solid #8c8c8c' : '1px solid #d9d9d9',
                        borderRadius: '4px',
                        backgroundColor: batchType === 'close' ? '#f5f5f5' : '#fff'
                    }} onClick={() => this.onBatchTypeSelect('close')}>
                        <input type="radio" checked={batchType === 'close'} style={{ marginRight: '8px' }} readOnly />
                        <span style={{ color: '#595959', fontWeight: 'bold' }}>批量关闭</span>
                        <span style={{ color: '#8c8c8c', marginLeft: '10px', fontSize: '12px' }}>（状态：已处理 → 已关闭）</span>
                    </label>
                </div>
            </div>
        );
    };

    render() {
        let { table, search, button } = this.props;
        let { createSimpleTable } = table;
        let { NCCreateSearch } = search;
        let { createButtonApp } = button;
        return (
            <div className="nc-single-table">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-singleTable-header-area">
                        <div className="header-title-search-area">
                            {createPageIcon()}
                            <h2 className="title-search-detail">差异处理</h2>
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
                    show={this.state.showBatchModal}
                    onHide={this.onCancelBatch.bind(this)}
                    backdrop={true}
                    fieldid="batchProcessConfirm"
                    size="md"
                >
                    <NCModal.Header closeButton={true}>
                        <NCModal.Title>
                            <NCIcon type="uf-setting" style={{ color: '#1890ff', marginRight: '6px' }} />
                            批量处理
                        </NCModal.Title>
                    </NCModal.Header>
                    <NCModal.Body>
                        {this.renderBatchModalContent()}
                    </NCModal.Body>
                    <NCModal.Footer>
                        <NCButton
                            fieldid="confirmBatch"
                            onClick={this.onConfirmBatch.bind(this)}
                            colors="primary"
                        >
                            确定
                        </NCButton>
                        <NCButton fieldid="cancelBatch" onClick={this.onCancelBatch.bind(this)}>
                            取消
                        </NCButton>
                    </NCModal.Footer>
                </NCModal>
            </div>
        );
    }
}

export default DiffList = createPage({
    billinfo: {
        billtype: 'grid',
        pagecode: pagecode,
        headcode: tableId,
        searchcode: searchId
    },
    mutiLangCode: '202606ATADIFFLIST'
})(DiffList);
