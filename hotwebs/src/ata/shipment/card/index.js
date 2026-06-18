import { createPage, ajax, base, high, toast, cardCache, print, output } from 'nc-lightapp-front';
import { createPageIcon } from 'nc-lightapp-front';
import '../index.less';

const { NCAffix, NCDiv, NCTable, NCTooltip, NCButton, NCPopconfirm, NCIcon, NCMsg, NCModal, NCStep, NCMessage, NCRadio, NCScrollElement, NCAnchor, NCScrollLink, NCAlert } = base;
const { NCCreateSearch, NCEditTable, NCTableControl } = high;
const { addCache, getCacheById, updateCache, deleteCacheById, getNextId, getCurrentLastId } = cardCache;

let formId = '202606ATASHIPCARD_form';
let tableId = '202606ATASHIPCARD_table';
let pagecode = '202606ATASHIPCARD';
let appcode = '202606ATASHIP';
let listPagecode = '202606ATASHIPLIST';
let dataSource = 'ata.shipment.card.cache';

class ShipmentCard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            json: {},
            inlt: null,
            showToast: false,
            toastColor: '',
            toastContent: '',
            status: 'browse',
            pk_shipment: null,
            isPaste: false,
            showAlert: false,
            alertType: '',
            alertContent: ''
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
            moduleId: '202606ATASHIPCARD',
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
            if (item.attrcode === 'pk_exhibit_list') {
                item.queryCondition = () => {
                    return {
                        list_status: '3'
                    };
                };
                item.renderStatus = {
                    edit: true,
                    browse: false
                };
            }
            if (item.attrcode === 'shipment_no' || item.attrcode === 'shipment_status' || item.attrcode === 'registrant') {
                item.disabled = true;
            }
            if (item.attrcode === 'shipment_date') {
                item.required = true;
            }
            if (item.attrcode === 'departure_port' || item.attrcode === 'destination_port') {
                item.required = true;
            }
            return item;
        });

        meta[tableId].items.map((item) => {
            if (item.attrcode === 'select') {
                item.render = (text, record, index) => {
                    return this.renderCheckbox(text, record, index);
                };
            }
            if (item.attrcode === 'pk_exhibit') {
                item.visible = false;
            }
            if (item.attrcode === 'exhibit_code' || item.attrcode === 'exhibit_name' || item.attrcode === 'serial_no' || item.attrcode === 'specification' || item.attrcode === 'unit') {
                item.disabled = true;
            }
            if (item.attrcode === 'serial_verified') {
                item.render = (text, record, index) => {
                    return this.renderSerialVerified(text, record, index);
                };
                item.options = [
                    { display: this.state.json['202606ATASHIPCARD-000000'] || '未验证', value: '0' },
                    { display: this.state.json['202606ATASHIPCARD-000001'] || '已通过', value: '1' }
                ];
            }
            if (item.attrcode === 'shipment_qty') {
                item.render = (text, record, index) => {
                    return this.renderShipmentQty(text, record, index);
                };
            }
            return item;
        });
        return meta;
    };

    renderCheckbox = (text, record, index) => {
        let checked = record && record.values && record.values.select && record.values.select.value;
        return (
            <span>
                <input
                    type="checkbox"
                    checked={checked || false}
                    onChange={(e) => this.onCheckboxChange(e, record, index)}
                />
            </span>
        );
    };

    renderSerialVerified = (text, record, index) => {
        let value = record && record.values && record.values.serial_verified && record.values.serial_verified.value;
        let display = value == 1 ? (this.state.json['202606ATASHIPCARD-000001'] || '已通过') : (this.state.json['202606ATASHIPCARD-000000'] || '未验证');
        let isUnverified = value == 0;
        return (
            <span>
                <NCTooltip
                    placement="top"
                    overlay={isUnverified ? (this.state.json['202606ATASHIPCARD-000002'] || '序列号未验证') : ''}
                >
                    <span style={{
                        padding: '2px 6px',
                        backgroundColor: isUnverified ? '#fffbe6' : 'transparent',
                        borderRadius: '4px',
                        color: isUnverified ? '#d46b08' : '#333',
                        cursor: 'default'
                    }}>
                        {display}
                    </span>
                </NCTooltip>
            </span>
        );
    };

    renderShipmentQty = (text, record, index) => {
        let value = record && record.values && record.values.shipment_qty && record.values.shipment_qty.value;
        let selected = record && record.values && record.values.select && record.values.select.value;
        let hasError = selected && (value == null || Number(value) <= 0);
        return (
            <span style={{ color: hasError ? '#ff4d4f' : '#333' }}>
                {value}
                {hasError && (
                    <div style={{ fontSize: '12px', marginTop: '2px' }}>
                        {this.state.json['202606ATASHIPCARD-000003'] || '出运数量必须大于0'}
                    </div>
                )}
            </span>
        );
    };

    onCheckboxChange = (e, record, index) => {
        let checked = e.target.checked;
        let rows = this.props.editTable.getAllRows(tableId);
        if (rows[index]) {
            rows[index].values.select = { value: checked, display: checked };
        }
        this.props.editTable.setTableData(tableId, { rows });
    };

    setDefaultValue = () => {
        let status = this.props.getUrlParam('status');
        if (status == 'add') {
            let today = new Date();
            let dateStr = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();
            this.props.form.setFormItemsValue(formId, {
                shipment_date: { value: dateStr, display: dateStr },
                shipment_status: { value: '0', display: this.state.json['202606ATASHIPCARD-000004'] || '待登记' }
            });
        }
    };

    queryCardData = () => {
        let status = this.props.getUrlParam('status');
        let id = this.props.getUrlParam('id');
        this.setState({ status: status, pk_shipment: id });
        if (status == 'add') {
            this.props.form.setFormStatus(formId, 'edit');
            this.props.editTable.setStatus(tableId, 'edit');
            this.setDefaultValue();
        } else {
            if (!id) return;
            ajax({
                url: '/nccloud/ata/shipment/querycard.do',
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
                                if (!row.values.select) {
                                    row.values.select = { value: false, display: false };
                                }
                            });
                            this.props.editTable.setTableData(tableId, { rows });
                        }
                        if (status == 'browse') {
                            this.props.form.setFormStatus(formId, 'browse');
                            this.props.editTable.setStatus(tableId, 'browse');
                            this.props.button.setButtonVisible(['Save', 'Register', 'CancelRegister'], false);
                            this.props.button.setButtonVisible(['Edit', 'Back'], true);
                        } else if (status == 'edit') {
                            this.props.form.setFormStatus(formId, 'edit');
                            this.props.editTable.setStatus(tableId, 'edit');
                        }
                    }
                }
            });
        }
    };

    afterEvent = (props, moduleId, key, value, oldValue, data) => {
        if (moduleId === formId && key === 'pk_exhibit_list' && value && value.value) {
            this.loadExhibitList(value.value);
        }
        if (moduleId === tableId && key === 'shipment_qty') {
            this.validateShipmentQty(data.index);
        }
        if (moduleId === tableId && key === 'serial_verified') {
            let rows = props.editTable.getAllRows(tableId);
            rows[data.index].values.serial_verified = { value: value, display: value };
            props.editTable.setTableData(tableId, { rows });
        }
    };

    loadExhibitList = (pk_exhibit_list) => {
        ajax({
            url: '/nccloud/ata/exhibitlist/querycard.do',
            data: {
                pk: pk_exhibit_list,
                pageCode: listPagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    let exhibitTableId = '202606ATAEXHIBITLISTCARD_table';
                    let exhibitRows = (data[exhibitTableId] && data[exhibitTableId].rows) || [];
                    let newRows = [];
                    exhibitRows.forEach((row) => {
                        let exhibit = row.values;
                        newRows.push({
                            values: {
                                select: { value: false, display: false },
                                pk_exhibit: { value: exhibit.pk_exhibit && exhibit.pk_exhibit.value },
                                exhibit_code: { value: exhibit.exhibit_code && exhibit.exhibit_code.value, display: exhibit.exhibit_code && exhibit.exhibit_code.display },
                                exhibit_name: { value: exhibit.exhibit_name && exhibit.exhibit_name.value, display: exhibit.exhibit_name && exhibit.exhibit_name.display },
                                serial_no: { value: exhibit.serial_no && exhibit.serial_no.value, display: exhibit.serial_no && exhibit.serial_no.display },
                                serial_verified: { value: '1', display: this.state.json['202606ATASHIPCARD-000001'] || '已通过' },
                                shipment_qty: { value: exhibit.quantity && exhibit.quantity.value, display: exhibit.quantity && exhibit.quantity.display },
                                specification: { value: exhibit.specification && exhibit.specification.value, display: exhibit.specification && exhibit.specification.display },
                                unit: { value: exhibit.unit && exhibit.unit.value, display: exhibit.unit && exhibit.unit.display }
                            },
                            status: '1'
                        });
                    });
                    this.props.editTable.setTableData(tableId, { rows: newRows });
                }
            }
        });
    };

    validateShipmentQty = (index) => {
        let rows = this.props.editTable.getAllRows(tableId);
        let row = rows[index];
        if (row && row.values.select && row.values.select.value) {
            let qty = row.values.shipment_qty && row.values.shipment_qty.value;
            if (qty == null || Number(qty) <= 0) {
                this.showToast('warning', this.state.json['202606ATASHIPCARD-000003'] || '出运数量必须大于0');
            }
        }
    };

    validateForm = () => {
        let formFlag = this.props.form.isCheckNow(formId);
        if (!formFlag) {
            return false;
        }
        let rows = this.props.editTable.getAllRows(tableId);
        let selectedRows = rows.filter(r => r.values.select && r.values.select.value);
        if (selectedRows.length == 0) {
            this.showToast('warning', this.state.json['202606ATASHIPCARD-000005'] || '请至少勾选一行展品');
            return false;
        }
        for (let row of selectedRows) {
            let verified = row.values.serial_verified && row.values.serial_verified.value;
            if (verified != 1) {
                let exhibitName = row.values.exhibit_name && row.values.exhibit_name.value;
                this.showToast('warning', `${exhibitName}` + (this.state.json['202606ATASHIPCARD-000006'] || '序列号未验证，不能出运'));
                return false;
            }
            let qty = row.values.shipment_qty && row.values.shipment_qty.value;
            if (qty == null || Number(qty) <= 0) {
                let exhibitName = row.values.exhibit_name && row.values.exhibit_name.value;
                this.showToast('warning', `${exhibitName}` + (this.state.json['202606ATASHIPCARD-000003'] || '出运数量必须大于0'));
                return false;
            }
        }
        return true;
    };

    getCardData = () => {
        let formData = this.props.form.getAllFormValue(formId);
        let tableData = this.props.editTable.getAllRows(tableId);
        let detailRows = tableData.filter(r => r.values.select && r.values.select.value).map(r => {
            return {
                values: {
                    pk_exhibit: r.values.pk_exhibit,
                    exhibit_code: r.values.exhibit_code,
                    exhibit_name: r.values.exhibit_name,
                    serial_verified: r.values.serial_verified,
                    shipment_qty: r.values.shipment_qty
                },
                status: r.status || '1'
            };
        });
        let data = {
            [formId]: formData,
            [tableId]: { rows: detailRows },
            pageCode: pagecode,
            templetid: '1001Z310000000000000'
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
            url: '/nccloud/ata/shipment/save.do',
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
                            if (!row.values.select) {
                                row.values.select = { value: row.status == '1' || row.status == '3', display: row.status == '1' || row.status == '3' };
                            }
                        });
                        this.props.editTable.setTableData(tableId, { rows });
                        this.setState({ pk_shipment: data[formId].rows[0].values.pk_shipment.value });
                    }
                    this.showToast('success', this.state.json['202606ATASHIPCARD-000007'] || '保存成功');
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
        let data = this.getCardData();
        ajax({
            url: '/nccloud/ata/shipment/register.do',
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
                            if (!row.values.select) {
                                row.values.select = { value: true, display: true };
                            }
                        });
                        this.props.editTable.setTableData(tableId, { rows });
                    }
                    this.showAlert('success', this.state.json['202606ATASHIPCARD-000008'] || '出运登记成功');
                    this.props.form.setFormStatus(formId, 'browse');
                    this.props.editTable.setStatus(tableId, 'browse');
                    this.updateButtonStatus();
                }
            }
        });
    };

    btnCancelRegister = () => {
        let pk = this.state.pk_shipment;
        if (!pk) {
            let formData = this.props.form.getAllFormValue(formId);
            if (formData && formData.rows && formData.rows[0]) {
                pk = formData.rows[0].values.pk_shipment.value;
            }
        }
        if (!pk) {
            this.showToast('warning', this.state.json['202606ATASHIPCARD-000009'] || '未获取到出运单主键');
            return;
        }
        ajax({
            url: '/nccloud/ata/shipment/cancelregister.do',
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
                            if (!row.values.select) {
                                row.values.select = { value: row.status == '1' || row.status == '3', display: row.status == '1' || row.status == '3' };
                            }
                        });
                        this.props.editTable.setTableData(tableId, { rows });
                    }
                    this.showToast('success', this.state.json['202606ATASHIPCARD-000010'] || '取消登记成功');
                    this.props.form.setFormStatus(formId, 'edit');
                    this.props.editTable.setStatus(tableId, 'edit');
                    this.updateButtonStatus();
                }
            }
        });
    };

    btnEdit = () => {
        this.props.form.setFormStatus(formId, 'edit');
        this.props.editTable.setStatus(tableId, 'edit');
        this.updateButtonStatus();
    };

    btnBack = () => {
        this.props.pushTo('/list', {
            pagecode: listPagecode
        });
    };

    updateButtonStatus = () => {
        let formStatus = this.props.form.getFormStatus(formId);
        let formData = this.props.form.getAllFormValue(formId);
        let shipment_status = formData && formData.rows && formData.rows[0] && formData.rows[0].values.shipment_status && formData.rows[0].values.shipment_status.value;
        if (formStatus == 'browse') {
            if (shipment_status == 0) {
                this.props.button.setButtonVisible(['Edit', 'Register', 'CancelRegister', 'Back'], [true, true, false, true]);
                this.props.button.setButtonDisabled(['Save', 'Register'], [true, true]);
            } else if (shipment_status == 1) {
                this.props.button.setButtonVisible(['Edit', 'Register', 'CancelRegister', 'Save', 'Back'], [false, false, true, false, true]);
            }
        } else if (formStatus == 'edit') {
            this.props.button.setButtonVisible(['Save', 'Register', 'Back'], [true, true, true]);
            this.props.button.setButtonVisible(['Edit', 'CancelRegister'], [false, false]);
        }
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

    showAlert = (type, content) => {
        this.setState({
            showAlert: true,
            alertType: type,
            alertContent: content
        }, () => {
            setTimeout(() => {
                this.setState({ showAlert: false });
            }, 5000);
        });
    };

    renderStatus = (text, record, index) => {
        let status = this.props.form.getFormItemsValue(formId, 'shipment_status');
        let value = status && status.value;
        let statusText = '';
        let color = '';
        if (value == 0) {
            statusText = this.state.json['202606ATASHIPCARD-000004'] || '待登记';
            color = '#999';
        } else if (value == 1) {
            statusText = this.state.json['202606ATASHIPCARD-000011'] || '已出运';
            color = '#00a854';
        }
        return (
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
        );
    };

    render() {
        let { form, editTable, button } = this.props;
        let { createForm } = form;
        let { createEditTable } = editTable;
        let { createButtonApp } = button;
        let shipmentStatus = this.props.form.getFormItemsValue(formId, 'shipment_status');
        let statusValue = shipmentStatus && shipmentStatus.value;
        return (
            <div className="nc-bill-card">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-bill-header-area">
                        <div className="header-title-search-area">
                            {createPageIcon()}
                            <h2 className="title-search-detail">{this.state.json['202606ATASHIPCARD-000012'] || '出运登记卡片'}</h2>
                            {statusValue != null && this.renderStatus()}
                        </div>
                        <div className="header-button-area">
                            {createButtonApp({
                                area: 'card_head',
                                buttonLimit: 5,
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
                                closeAfterClick
                            />
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
                                {this.state.json['202606ATASHIPCARD-000013'] || '出运明细'}
                            </div>
                            {createEditTable(tableId, {
                                onAfterEvent: this.afterEvent.bind(this),
                                showIndex: true,
                                showCheck: false,
                                adaptionHeight: true
                            })}
                        </div>
                    </NCDiv>
                </div>
                {this.state.showToast && <NCMsg.Success content={this.state.toastContent} duration={3} />}
            </div>
        );
    }
}

export default ShipmentCard = createPage({
    billinfo: {
        billtype: 'extcard',
        pagecode: pagecode,
        headcode: formId,
        bodycode: tableId
    },
    mutiLangCode: '202606ATASHIPCARD'
})(ShipmentCard);
