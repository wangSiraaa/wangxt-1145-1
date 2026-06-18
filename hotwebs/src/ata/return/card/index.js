import { createPage, ajax, base, high, toast, cardCache, print, output } from 'nc-lightapp-front';
import { createPageIcon } from 'nc-lightapp-front';
import '../index.less';

const { NCAffix, NCDiv, NCTable, NCTooltip, NCButton, NCPopconfirm, NCIcon, NCMsg, NCModal, NCStep, NCMessage, NCRadio, NCScrollElement, NCAnchor, NCScrollLink, NCAlert, NCTag } = base;
const { NCCreateSearch, NCEditTable, NCTableControl } = high;
const { addCache, getCacheById, updateCache, deleteCacheById, getNextId, getCurrentLastId } = cardCache;

let formId = '202606ATARETUCARD_form';
let tableId = '202606ATARETUCARD_table';
let pagecode = '202606ATARETUCARD';
let appcode = '202606ATARETU';
let listPagecode = '202606ATARETULIST';
let shipmentCardPagecode = '202606ATASHIPCARD';
let diffPagecode = '202606ATADIFFLIST';
let dataSource = 'ata.return.card.cache';

class ReturnCard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            json: {},
            inlt: null,
            showToast: false,
            toastColor: '',
            toastContent: '',
            status: 'browse',
            pk_return: null,
            isPaste: false,
            showAlert: false,
            alertType: '',
            alertContent: '',
            showDiffModal: false,
            diffRows: [],
            diffCount: 0,
            alertBtnText: '',
            alertCloseable: true
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
            moduleId: '202606ATARETUCARD',
            domainName: 'ata',
            callback
        });
    }

    initTemplate = () => {
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
                        this.props.meta.setMeta(meta, this.setDefaultValue.bind(this));
                    }
                    if (data.button) {
                        let button = data.button;
                        this.props.button.setButtons(button);
                    }
                    this.queryCardData();
                }
            }
        );
    };

    modifierMeta = (meta) => {
        meta[formId].items.map((item) => {
            if (item.attrcode === 'pk_shipment') {
                item.queryCondition = () => {
                    return {
                        shipment_status: '1'
                    };
                };
                item.renderStatus = {
                    edit: true,
                    browse: false
                };
            }
            if (item.attrcode === 'pk_exhibit_list' || item.attrcode === 'return_no' || item.attrcode === 'return_status' || item.attrcode === 'registrant') {
                item.disabled = true;
            }
            if (item.attrcode === 'return_date') {
                item.required = true;
            }
            if (item.attrcode === 'arrival_port') {
                item.required = true;
            }
            return item;
        });

        meta[tableId].items.map((item) => {
            if (item.attrcode === 'pk_exhibit') {
                item.visible = false;
            }
            if (item.attrcode === 'exhibit_code' || item.attrcode === 'exhibit_name' || item.attrcode === 'serial_no' || item.attrcode === 'shipment_qty' || item.attrcode === 'specification' || item.attrcode === 'unit') {
                item.disabled = true;
            }
            if (item.attrcode === 'diff_qty') {
                item.disabled = true;
                item.render = (text, record, index) => {
                    return this.renderDiffQty(text, record, index);
                };
            }
            return item;
        });
        return meta;
    };

    renderDiffQty = (text, record, index) => {
        let diffQty = record && record.values && record.values.diff_qty && record.values.diff_qty.value;
        let hasDiff = diffQty != null && Number(diffQty) !== 0;
        return (
            <span style={{
                display: 'inline-block',
                width: '100%',
                padding: '2px 6px',
                backgroundColor: hasDiff ? '#fff2e8' : 'transparent',
                border: hasDiff ? '1px solid #ff4d4f' : 'none',
                borderRadius: '4px',
                color: hasDiff ? '#d4380d' : '#333',
                fontWeight: hasDiff ? 'bold' : 'normal',
                boxSizing: 'border-box'
            }}>
                {diffQty}
            </span>
        );
    };

    setDefaultValue = () => {
        let status = this.props.getUrlParam('status');
        if (status == 'add') {
            let today = new Date();
            let dateStr = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
            this.props.form.setFormItemsValue(formId, {
                return_date: { value: dateStr, display: dateStr },
                return_status: { value: '0', display: this.state.json['202606ATARETUCARD-000000'] || '待登记' }
            });
        }
    };

    queryCardData = () => {
        let status = this.props.getUrlParam('status');
        let id = this.props.getUrlParam('id');
        let action = this.props.getUrlParam('action');
        this.setState({ status: status, pk_return: id });
        if (status == 'add') {
            this.props.form.setFormStatus(formId, 'edit');
            this.props.editTable.setStatus(tableId, 'edit');
            this.setDefaultValue();
            if (action == 'register') {
                setTimeout(() => {
                    this.btnRegister();
                }, 500);
            }
        } else {
            if (!id) return;
            ajax({
                url: '/nccloud/ata/return/querycard.do',
                data: {
                    pk: id,
                    pageCode: pagecode
                },
                success: (res) => {
                    let { success, data } = res;
                    if (success && data) {
                        if (data[formId]) {
                            this.props.form.setAllFormValue({ [formId]: data[formId] });
                        }
                        if (data[tableId]) {
                            let rows = data[tableId].rows || [];
                            rows.forEach(row => {
                                this.calcDiffForRow(row);
                            });
                            this.props.editTable.setTableData(tableId, { rows });
                            this.updateDiffCount();
                        }
                        if (status == 'browse') {
                            this.props.form.setFormStatus(formId, 'browse');
                            this.props.editTable.setStatus(tableId, 'browse');
                            this.updateButtonStatus();
                        } else if (status == 'edit') {
                            this.props.form.setFormStatus(formId, 'edit');
                            this.props.editTable.setStatus(tableId, 'edit');
                        }
                        let returnStatus = this.getReturnStatus();
                        if (returnStatus == 2) {
                            this.showDiffAlert();
                        } else if (returnStatus == 1) {
                            this.showSuccessAlert();
                        }
                    }
                }
            });
        }
    };

    afterEvent = (props, moduleId, key, value, oldValue, data) => {
        if (moduleId === formId && key === 'pk_shipment' && value && value.value) {
            this.loadShipmentData(value.value);
        }
        if (moduleId === tableId && key === 'return_qty') {
            this.onReturnQtyChange(data.index);
        }
        if (moduleId === tableId && key === 'remark') {
            let rows = props.editTable.getAllRows(tableId);
            rows[data.index].values.remark = { value: value, display: value };
            props.editTable.setTableData(tableId, { rows });
        }
    };

    loadShipmentData = (pk_shipment) => {
        ajax({
            url: '/nccloud/ata/shipment/querybylist.do',
            data: {
                pk: pk_shipment,
                pageCode: shipmentCardPagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    let shipmentFormId = '202606ATASHIPCARD_form';
                    let shipmentTableId = '202606ATASHIPCARD_table';
                    if (data[shipmentFormId] && data[shipmentFormId].rows && data[shipmentFormId].rows[0]) {
                        let pk_exhibit_list = data[shipmentFormId].rows[0].values.pk_exhibit_list;
                        this.props.form.setFormItemsValue(formId, {
                            pk_exhibit_list: pk_exhibit_list
                        });
                    }
                    if (data[shipmentTableId]) {
                        let shipmentRows = data[shipmentTableId].rows || [];
                        let newRows = [];
                        shipmentRows.forEach((row) => {
                            let shipment = row.values;
                            let shipmentQty = shipment.shipment_qty && shipment.shipment_qty.value;
                            let newRow = {
                                values: {
                                    pk_exhibit: { value: shipment.pk_exhibit && shipment.pk_exhibit.value },
                                    exhibit_code: { value: shipment.exhibit_code && shipment.exhibit_code.value, display: shipment.exhibit_code && shipment.exhibit_code.display },
                                    exhibit_name: { value: shipment.exhibit_name && shipment.exhibit_name.value, display: shipment.exhibit_name && shipment.exhibit_name.display },
                                    serial_no: { value: shipment.serial_no && shipment.serial_no.value, display: shipment.serial_no && shipment.serial_no.display },
                                    shipment_qty: { value: shipmentQty, display: shipmentQty },
                                    return_qty: { value: shipmentQty, display: shipmentQty },
                                    diff_qty: { value: 0, display: 0 },
                                    specification: { value: shipment.specification && shipment.specification.value, display: shipment.specification && shipment.specification.display },
                                    unit: { value: shipment.unit && shipment.unit.value, display: shipment.unit && shipment.unit.display },
                                    remark: { value: '', display: '' }
                                },
                                status: '1'
                            };
                            newRows.push(newRow);
                        });
                        this.props.editTable.setTableData(tableId, { rows: newRows });
                        this.updateDiffCount();
                    }
                }
            }
        });
    };

    calcDiffForRow = (row) => {
        let shipmentQty = row.values.shipment_qty && row.values.shipment_qty.value;
        let returnQty = row.values.return_qty && row.values.return_qty.value;
        shipmentQty = shipmentQty ? Number(shipmentQty) : 0;
        returnQty = returnQty != null ? Number(returnQty) : shipmentQty;
        let diffQty = shipmentQty - returnQty;
        row.values.diff_qty = { value: diffQty, display: diffQty };
        if (!row.values.return_qty || row.values.return_qty.value == null) {
            row.values.return_qty = { value: shipmentQty, display: shipmentQty };
        }
    };

    onReturnQtyChange = (index) => {
        let rows = this.props.editTable.getAllRows(tableId);
        if (rows[index]) {
            this.calcDiffForRow(rows[index]);
            let returnQty = rows[index].values.return_qty && rows[index].values.return_qty.value;
            if (returnQty != null && Number(returnQty) < 0) {
                this.showToast('warning', this.state.json['202606ATARETUCARD-000001'] || '回运数量不能为负数');
            }
        }
        this.props.editTable.setTableData(tableId, { rows });
        this.updateDiffCount();
    };

    updateDiffCount = () => {
        let rows = this.props.editTable.getAllRows(tableId);
        let count = 0;
        rows.forEach(row => {
            let diffQty = row.values.diff_qty && row.values.diff_qty.value;
            if (diffQty != null && Number(diffQty) !== 0) {
                count++;
            }
        });
        this.setState({ diffCount: count });
    };

    calculateDiff = () => {
        let rows = this.props.editTable.getAllRows(tableId);
        let diffRows = [];
        rows.forEach(row => {
            let shipmentQty = row.values.shipment_qty && row.values.shipment_qty.value;
            let returnQty = row.values.return_qty && row.values.return_qty.value;
            shipmentQty = shipmentQty ? Number(shipmentQty) : 0;
            returnQty = returnQty != null ? Number(returnQty) : shipmentQty;
            let diffQty = shipmentQty - returnQty;
            if (diffQty !== 0) {
                diffRows.push({
                    exhibit_code: row.values.exhibit_code && row.values.exhibit_code.value,
                    exhibit_name: row.values.exhibit_name && row.values.exhibit_name.value,
                    shipment_qty: shipmentQty,
                    return_qty: returnQty,
                    diff_qty: diffQty,
                    remark: row.values.remark && row.values.remark.value
                });
            }
        });
        return diffRows;
    };

    validateForm = () => {
        let formFlag = this.props.form.isCheckNow(formId);
        if (!formFlag) {
            return false;
        }
        let pk_shipment = this.props.form.getFormItemsValue(formId, 'pk_shipment');
        if (!pk_shipment || !pk_shipment.value) {
            this.showToast('warning', this.state.json['202606ATARETUCARD-000002'] || '请选择出运单');
            return false;
        }
        let rows = this.props.editTable.getAllRows(tableId);
        if (rows.length == 0) {
            this.showToast('warning', this.state.json['202606ATARETUCARD-000003'] || '请先选择出运单以加载出运明细');
            return false;
        }
        for (let i = 0; i < rows.length; i++) {
            let returnQty = rows[i].values.return_qty && rows[i].values.return_qty.value;
            if (returnQty != null && Number(returnQty) < 0) {
                let exhibitName = rows[i].values.exhibit_name && rows[i].values.exhibit_name.value;
                this.showToast('warning', `${exhibitName}` + (this.state.json['202606ATARETUCARD-000004'] || '的回运数量不能为负数'));
                return false;
            }
        }
        return true;
    };

    getCardData = () => {
        let formData = this.props.form.getAllFormValue(formId);
        let tableData = this.props.editTable.getAllRows(tableId);
        tableData.forEach(row => {
            this.calcDiffForRow(row);
        });
        let detailRows = tableData.map(r => {
            return {
                values: {
                    pk_exhibit: r.values.pk_exhibit,
                    exhibit_code: r.values.exhibit_code,
                    exhibit_name: r.values.exhibit_name,
                    shipment_qty: r.values.shipment_qty,
                    return_qty: r.values.return_qty,
                    diff_qty: r.values.diff_qty,
                    remark: r.values.remark
                },
                status: r.status || '1'
            };
        });
        let data = {
            [formId]: formData,
            [tableId]: { rows: detailRows },
            pageCode: pagecode,
            templetid: '1001Z310000000000002'
        };
        return data;
    };

    buttonClick = (props, id) => {
        switch (id) {
            case 'Save':
                this.btnSave();
                break;
            case 'Register':
                this.btnRegister();
                break;
            case 'CancelRegister':
                this.btnCancelRegister();
                break;
            case 'ViewDiff':
                this.btnViewDiff();
                break;
            case 'Edit':
                this.btnEdit();
                break;
            case 'Back':
                this.btnBack();
                break;
            default:
                break;
        }
    };

    btnSave = () => {
        let formFlag = this.props.form.isCheckNow(formId);
        if (!formFlag) {
            return;
        }
        let data = this.getCardData();
        ajax({
            url: '/nccloud/ata/return/save.do',
            data: data,
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    if (data[formId]) {
                        this.props.form.setAllFormValue({ [formId]: data[formId] });
                    }
                    if (data[tableId]) {
                        let rows = data[tableId].rows || [];
                        rows.forEach(row => {
                            this.calcDiffForRow(row);
                        });
                        this.props.editTable.setTableData(tableId, { rows });
                        this.setState({ pk_return: data[formId].rows[0].values.pk_return.value });
                        this.updateDiffCount();
                    }
                    this.showToast('success', this.state.json['202606ATARETUCARD-000005'] || '保存成功');
                    this.props.form.setFormStatus(formId, 'browse');
                    this.props.editTable.setStatus(tableId, 'browse');
                    this.updateButtonStatus();
                }
            }
        });
    };

    btnRegister = () => {
        if (!this.validateForm()) {
            return;
        }
        let diffRows = this.calculateDiff();
        if (diffRows.length > 0) {
            this.setState({
                showDiffModal: true,
                diffRows: diffRows
            });
        } else {
            this.doRegister();
        }
    };

    doRegister = () => {
        let data = this.getCardData();
        ajax({
            url: '/nccloud/ata/return/register.do',
            data: data,
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    if (data[formId]) {
                        this.props.form.setAllFormValue({ [formId]: data[formId] });
                    }
                    if (data[tableId]) {
                        let rows = data[tableId].rows || [];
                        rows.forEach(row => {
                            this.calcDiffForRow(row);
                        });
                        this.props.editTable.setTableData(tableId, { rows });
                        this.updateDiffCount();
                    }
                    this.props.form.setFormStatus(formId, 'browse');
                    this.props.editTable.setStatus(tableId, 'browse');
                    this.updateButtonStatus();
                    let returnStatus = this.getReturnStatus();
                    if (returnStatus == 2) {
                        this.showDiffAlert();
                    } else if (returnStatus == 1) {
                        this.showSuccessAlert();
                    }
                }
            }
        });
    };

    onConfirmDiffModal = () => {
        this.setState({ showDiffModal: false });
        this.doRegister();
    };

    onCancelDiffModal = () => {
        this.setState({ showDiffModal: false });
    };

    btnCancelRegister = () => {
        let pk = this.state.pk_return;
        if (!pk) {
            let formData = this.props.form.getAllFormValue(formId);
            if (formData && formData.rows && formData.rows[0]) {
                pk = formData.rows[0].values.pk_return.value;
            }
        }
        if (!pk) {
            this.showToast('warning', this.state.json['202606ATARETUCARD-000006'] || '未获取到回运单主键');
            return;
        }
        ajax({
            url: '/nccloud/ata/return/cancelregister.do',
            data: {
                pk: pk,
                pageCode: pagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    if (data[formId]) {
                        this.props.form.setAllFormValue({ [formId]: data[formId] });
                    }
                    if (data[tableId]) {
                        let rows = data[tableId].rows || [];
                        rows.forEach(row => {
                            this.calcDiffForRow(row);
                        });
                        this.props.editTable.setTableData(tableId, { rows });
                        this.updateDiffCount();
                    }
                    this.hideAlert();
                    this.showToast('success', this.state.json['202606ATARETUCARD-000007'] || '取消登记成功');
                    this.props.form.setFormStatus(formId, 'edit');
                    this.props.editTable.setStatus(tableId, 'edit');
                    this.updateButtonStatus();
                }
            }
        });
    };

    btnViewDiff = () => {
        let pk_exhibit_list = this.props.form.getFormItemsValue(formId, 'pk_exhibit_list');
        let pk_return = this.state.pk_return;
        if (!pk_return) {
            let formData = this.props.form.getAllFormValue(formId);
            if (formData && formData.rows && formData.rows[0]) {
                pk_return = formData.rows[0].values.pk_return.value;
            }
        }
        this.props.pushTo('/diff', {
            status: 'browse',
            id: pk_return,
            pk_exhibit_list: pk_exhibit_list && pk_exhibit_list.value,
            pagecode: diffPagecode
        });
    };

    btnEdit = () => {
        this.hideAlert();
        this.props.form.setFormStatus(formId, 'edit');
        this.props.editTable.setStatus(tableId, 'edit');
        this.updateButtonStatus();
    };

    btnBack = () => {
        this.props.pushTo('/list', {
            pagecode: listPagecode
        });
    };

    getReturnStatus = () => {
        let status = this.props.form.getFormItemsValue(formId, 'return_status');
        return status && status.value;
    };

    updateButtonStatus = () => {
        let formStatus = this.props.form.getFormStatus(formId);
        let returnStatus = this.getReturnStatus();
        let hasDiff = this.state.diffCount > 0;
        if (formStatus == 'browse') {
            if (returnStatus == 0) {
                this.props.button.setButtonVisible(['Edit', 'Register', 'Save', 'CancelRegister', 'Back'], [true, true, false, false, true]);
                this.props.button.setButtonDisabled(['Save', 'Register'], [true, true]);
            } else if (returnStatus == 1) {
                this.props.button.setButtonVisible(['Edit', 'Register', 'Save', 'CancelRegister', 'Back', 'ViewDiff'], [false, false, false, true, true, false]);
            } else if (returnStatus == 2) {
                this.props.button.setButtonVisible(['Edit', 'Register', 'Save', 'CancelRegister', 'Back', 'ViewDiff'], [false, false, false, true, true, true]);
            }
        } else if (formStatus == 'edit') {
            this.props.button.setButtonVisible(['Save', 'Register', 'Back'], [true, true, true]);
            this.props.button.setButtonVisible(['Edit', 'CancelRegister', 'ViewDiff'], [false, false, false]);
        }
    };

    getReturnStatusText = () => {
        let status = this.getReturnStatus();
        let statusText = '';
        let color = '';
        let hasDiff = this.state.diffCount > 0;
        if (status == 0) {
            statusText = this.state.json['202606ATARETUCARD-000000'] || '待登记';
            color = '#999';
        } else if (status == 1) {
            statusText = this.state.json['202606ATARETUCARD-000008'] || '已回运';
            color = '#00a854';
        } else if (status == 2) {
            statusText = this.state.json['202606ATARETUCARD-000009'] || '有差异';
            color = '#d46b08';
        }
        return { statusText, color, hasDiff };
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

    showDiffAlert = () => {
        let count = this.state.diffCount;
        this.setState({
            showAlert: true,
            alertType: 'warning',
            alertContent: `(${count})` + (this.state.json['202606ATARETUCARD-000010'] || '本次回运存在 项差异，已自动生成差异处理单，请及时处理'),
            alertBtnText: this.state.json['202606ATARETUCARD-000011'] || '去处理差异',
            alertCloseable: true
        });
    };

    showSuccessAlert = () => {
        this.setState({
            showAlert: true,
            alertType: 'success',
            alertContent: this.state.json['202606ATARETUCARD-000012'] || '回运登记完成，所有展品已如数回运',
            alertBtnText: '',
            alertCloseable: true
        });
    };

    hideAlert = () => {
        this.setState({
            showAlert: false
        });
    };

    onAlertBtnClick = () => {
        this.btnViewDiff();
    };

    renderStatus = () => {
        let { statusText, color, hasDiff } = this.getReturnStatusText();
        let count = this.state.diffCount;
        return (
            <span style={{ marginLeft: '10px' }}>
                <span style={{
                    display: 'inline-block',
                    padding: '2px 10px',
                    borderRadius: '10px',
                    color: '#fff',
                    backgroundColor: color,
                    fontSize: '12px'
                }}>
                    {statusText}
                </span>
                {hasDiff && count > 0 && (
                    <NCTag
                        color="orange"
                        style={{ marginLeft: '6px', fontSize: '11px' }}
                    >
                        {`${this.state.json['202606ATARETUCARD-000013'] || '差异'} ${count}`}
                    </NCTag>
                )}
            </span>
        );
    };

    renderDiffModalContent = () => {
        let { diffRows } = this.state;
        return (
            <div>
                <p style={{ color: '#d46b08', fontWeight: 'bold', marginBottom: '10px' }}>
                    {`(${diffRows.length})` + (this.state.json['202606ATARETUCARD-000014'] || '检测到 项展品存在回运差异，详情如下：')}
                </p>
                <table className="diff-modal-table">
                    <thead>
                        <tr>
                            <th>{this.state.json['202606ATARETUCARD-000015'] || '展品编码'}</th>
                            <th>{this.state.json['202606ATARETUCARD-000016'] || '展品名称'}</th>
                            <th>{this.state.json['202606ATARETUCARD-000017'] || '原出运数量'}</th>
                            <th>{this.state.json['202606ATARETUCARD-000018'] || '实际回运数量'}</th>
                            <th>{this.state.json['202606ATARETUCARD-000019'] || '差异数量'}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {diffRows.map((row, idx) => (
                            <tr key={idx}>
                                <td>{row.exhibit_code}</td>
                                <td>{row.exhibit_name}</td>
                                <td>{row.shipment_qty}</td>
                                <td>{row.return_qty}</td>
                                <td className="diff-value">{row.diff_qty > 0 ? `+${row.diff_qty}` : row.diff_qty}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                <p style={{ marginTop: '10px', color: '#666', fontSize: '12px' }}>
                    {this.state.json['202606ATARETUCARD-000020'] || '确认后将自动生成差异处理单，请及时处理。'}
                </p>
            </div>
        );
    };

    render() {
        let { form, editTable, button } = this.props;
        let { createForm } = form;
        let { createEditTable } = editTable;
        let { createButtonApp } = button;
        let returnStatus = this.getReturnStatus();
        return (
            <div className="nc-bill-card">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-bill-header-area">
                        <div className="header-title-search-area">
                            {createPageIcon()}
                            <h2 className="title-search-detail">{this.state.json['202606ATARETUCARD-000021'] || '回运登记卡片'}</h2>
                            {(returnStatus != null && returnStatus != undefined) && this.renderStatus()}
                        </div>
                        <div className="header-button-area">
                            {createButtonApp({
                                area: 'card_head',
                                buttonLimit: 6,
                                onButtonClick: this.buttonClick.bind(this),
                                popContainer: document.querySelector('.header-button-area')
                            })}
                        </div>
                    </NCDiv>
                </NCAffix>
                <div className="nc-bill-form-area">
                    {this.state.showAlert && (
                        <div style={{ padding: '10px 20px' }}>
                            <NCAlert
                                type={this.state.alertType}
                                message={this.state.alertContent}
                                showIcon
                                closeAfterClick={this.state.alertCloseable}
                                onClose={this.hideAlert.bind(this)}
                            >
                                {this.state.alertBtnText && (
                                    <NCButton
                                        size="sm"
                                        colors={this.state.alertType === 'warning' ? 'warning' : 'primary'}
                                        onClick={this.onAlertBtnClick.bind(this)}
                                        style={{ marginTop: '8px' }}
                                    >
                                        {this.state.alertBtnText}
                                    </NCButton>
                                )}
                            </NCAlert>
                        </div>
                    )}
                    <NCDiv areaCode={NCDiv.config.FORM} className="nc-bill-form-top">
                        {createForm(formId, {
                            onAfterEvent: this.afterEvent.bind(this)
                        })}
                    </NCDiv>
                    <NCDiv areaCode={NCDiv.config.TABLE} className="nc-bill-form-bottom">
                        <div className="nc-bill-table-area" style={{ padding: '10px 20px' }}>
                            <div className="nc-bill-table-header" style={{ padding: '10px 0', fontWeight: 'bold' }}>
                                {this.state.json['202606ATARETUCARD-000022'] || '回运明细'}
                                {this.state.diffCount > 0 && (
                                    <NCTag color="orange" style={{ marginLeft: '10px', fontSize: '11px' }}>
                                        {`${this.state.json['202606ATARETUCARD-000013'] || '差异'} ${this.state.diffCount}`}
                                    </NCTag>
                                )}
                            </div>
                            {createEditTable(tableId, {
                                onAfterEvent: this.afterEvent.bind(this),
                                showIndex: true,
                                showCheck: false,
                                adaptionHeight: true,
                                onSelected: () => {}
                            })}
                        </div>
                    </NCDiv>
                </div>
                {this.state.showToast && <NCMsg.Success content={this.state.toastContent} duration={3} />}
                <NCModal
                    show={this.state.showDiffModal}
                    onHide={this.onCancelDiffModal.bind(this)}
                    backdrop={true}
                    fieldid="diffConfirm"
                    size="lg"
                >
                    <NCModal.Header closeButton={true}>
                        <NCModal.Title>
                            <NCIcon type="uf-exc-t-o" style={{ color: '#faad14', marginRight: '6px' }} />
                            {this.state.json['202606ATARETUCARD-000023'] || '差异确认'}
                        </NCModal.Title>
                    </NCModal.Header>
                    <NCModal.Body>
                        {this.renderDiffModalContent()}
                    </NCModal.Body>
                    <NCModal.Footer>
                        <NCButton
                            fieldid="confirmDiff"
                            onClick={this.onConfirmDiffModal.bind(this)}
                            colors="warning"
                        >
                            {this.state.json['202606ATARETUCARD-000024'] || '确认并生成差异处理单'}
                        </NCButton>
                        <NCButton fieldid="cancelDiff" onClick={this.onCancelDiffModal.bind(this)}>
                            {this.state.json['202606ATARETUCARD-000025'] || '返回修改'}
                        </NCButton>
                    </NCModal.Footer>
                </NCModal>
            </div>
        );
    }
}

export default ReturnCard = createPage({
    billinfo: {
        billtype: 'extcard',
        pagecode: pagecode,
        headcode: formId,
        bodycode: tableId
    },
    mutiLangCode: '202606ATARETUCARD'
})(ReturnCard);
