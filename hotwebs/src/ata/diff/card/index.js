import { createPage, ajax, base, high, toast, cardCache, print, output } from 'nc-lightapp-front';
import { createPageIcon } from 'nc-lightapp-front';

const { NCAffix, NCDiv, NCTable, NCTooltip, NCButton, NCPopconfirm, NCIcon, NCMsg, NCModal, NCStep, NCMessage, NCRadio, NCScrollElement, NCAnchor, NCScrollLink, NCAlert, NCTag, NCInput, NCTextarea, NCSelect } = base;
const { NCCreateSearch, NCEditTable, NCTableControl } = high;
const { addCache, getCacheById, updateCache, deleteCacheById, getNextId, getCurrentLastId } = cardCache;

let formId = '202606ATADIFFCARD_form';
let exhibitFormId = '202606ATADIFFCARD_exhibit_form';
let handleFormId = '202606ATADIFFCARD_handle_form';
let pagecode = '202606ATADIFFCARD';
let appcode = '202606ATADIFF';
let listPagecode = '202606ATADIFFLIST';
let dataSource = 'ata.diff.card.cache';

const DIFF_STATUS_MAP = {
    '0': { display: '待处理', color: 'warning', bgColor: '#fffbe6', textColor: '#d46b08', alertType: 'warning' },
    '1': { display: '处理中', color: 'primary', bgColor: '#e6f7ff', textColor: '#1890ff', alertType: 'info' },
    '2': { display: '已处理', color: 'success', bgColor: '#f6ffed', textColor: '#52c41a', alertType: 'success' },
    '3': { display: '已关闭', color: 'default', bgColor: '#f5f5f5', textColor: '#8c8c8c', alertType: 'info' }
};

const DIFF_TYPE_OPTIONS = [
    { value: '1', display: '丢失' },
    { value: '2', display: '损坏' },
    { value: '3', display: '变卖' },
    { value: '4', display: '赠送' },
    { value: '5', display: '其他' }
];

const DIFF_TYPE_MAP = {
    '1': { display: '丢失', color: 'danger' },
    '2': { display: '损坏', color: 'warning' },
    '3': { display: '变卖', color: 'info' },
    '4': { display: '赠送', color: 'primary' },
    '5': { display: '其他', color: 'default' }
};

class DiffCard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            json: {},
            inlt: null,
            showToast: false,
            toastColor: '',
            toastContent: '',
            status: 'browse',
            pk_diff: null,
            diffStatus: null,
            cardData: null,
            showAlert: true,
            alertType: 'warning',
            alertContent: '',
            alertCloseable: false,
            diffTypeValue: '',
            handleRemarkValue: ''
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
            moduleId: '202606ATADIFFCARD',
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
        if (meta[formId]) {
            meta[formId].items.map((item) => {
                item.disabled = true;
                return item;
            });
        }
        if (meta[exhibitFormId]) {
            meta[exhibitFormId].items.map((item) => {
                item.disabled = true;
                return item;
            });
        }
        if (meta[handleFormId]) {
            meta[handleFormId].items.map((item) => {
                if (item.attrcode === 'diff_type') {
                    item.required = true;
                    item.options = DIFF_TYPE_OPTIONS;
                }
                if (item.attrcode === 'handle_remark') {
                    item.required = true;
                }
                if (item.attrcode === 'handler' || item.attrcode === 'handle_time') {
                    item.disabled = true;
                    item.visible = false;
                }
                return item;
            });
        }
        return meta;
    };

    setDefaultValue = () => {
        let action = this.props.getUrlParam('action');
        if (action) {
            setTimeout(() => {
                if (action === 'start') {
                    this.btnStartProcess();
                } else if (action === 'finish') {
                    this.btnFinishProcess();
                }
            }, 500);
        }
    };

    queryCardData = () => {
        let status = this.props.getUrlParam('status');
        let id = this.props.getUrlParam('id');
        this.setState({ status: status, pk_diff: id });
        if (status == 'add') {
            this.props.form.setFormStatus(formId, 'edit');
            this.props.form.setFormStatus(exhibitFormId, 'edit');
            this.props.form.setFormStatus(handleFormId, 'edit');
        } else {
            if (!id) return;
            ajax({
                url: '/nccloud/ata/diff/querycard.do',
                data: {
                    pk: id,
                    pageCode: pagecode
                },
                success: (res) => {
                    let { success, data } = res;
                    if (success && data) {
                        this.setState({ cardData: data });
                        if (data[formId]) {
                            this.props.form.setAllFormValue({ [formId]: data[formId] });
                            let diffStatus = data[formId].rows && data[formId].rows[0] && data[formId].rows[0].values.diff_status && data[formId].rows[0].values.diff_status.value;
                            this.setState({ diffStatus: diffStatus });
                            this.updateAlertInfo(diffStatus);
                        }
                        if (data[exhibitFormId]) {
                            this.props.form.setAllFormValue({ [exhibitFormId]: data[exhibitFormId] });
                        }
                        if (data[handleFormId]) {
                            this.props.form.setAllFormValue({ [handleFormId]: data[handleFormId] });
                            let diffType = data[handleFormId].rows && data[handleFormId].rows[0] && data[handleFormId].rows[0].values.diff_type && data[handleFormId].rows[0].values.diff_type.value;
                            let handleRemark = data[handleFormId].rows && data[handleFormId].rows[0] && data[handleFormId].rows[0].values.handle_remark && data[handleFormId].rows[0].values.handle_remark.value;
                            this.setState({
                                diffTypeValue: diffType || '',
                                handleRemarkValue: handleRemark || ''
                            });
                            this.updateHandleFormVisibility(diffStatus);
                        }
                        if (status == 'browse') {
                            this.props.form.setFormStatus(formId, 'browse');
                            this.props.form.setFormStatus(exhibitFormId, 'browse');
                            this.props.form.setFormStatus(handleFormId, 'browse');
                            this.updateButtonStatus();
                        } else if (status == 'edit') {
                            this.props.form.setFormStatus(formId, 'browse');
                            this.props.form.setFormStatus(exhibitFormId, 'browse');
                            this.props.form.setFormStatus(handleFormId, 'edit');
                            this.updateButtonStatus();
                        }
                    }
                }
            });
        }
    };

    updateAlertInfo = (diffStatus) => {
        let statusInfo = DIFF_STATUS_MAP[diffStatus] || {};
        let alertContent = '';
        if (diffStatus === '0') {
            alertContent = '该差异待处理，请选择差异类型并填写处理意见后点击开始处理';
        } else if (diffStatus === '1') {
            alertContent = '处理中，完成调查后请填写处理意见并点击完成处理';
        } else if (diffStatus === '2') {
            alertContent = '已处理完毕，确认无误后可点击关闭';
        } else if (diffStatus === '3') {
            alertContent = '差异已关闭';
        }
        this.setState({
            alertType: statusInfo.alertType || 'info',
            alertContent: alertContent
        });
    };

    updateHandleFormVisibility = (diffStatus) => {
        let showHandleInfo = diffStatus && diffStatus !== '0';
        let meta = this.props.meta.getMeta();
        if (meta[handleFormId]) {
            meta[handleFormId].items.map((item) => {
                if (item.attrcode === 'handler' || item.attrcode === 'handle_time') {
                    item.visible = showHandleInfo;
                    item.disabled = true;
                }
                return item;
            });
            this.props.meta.setMeta(meta);
        }
    };

    afterEvent = (props, moduleId, key, value, oldValue, data) => {
        if (moduleId === handleFormId) {
            if (key === 'diff_type') {
                this.setState({ diffTypeValue: value && value.value ? value.value : '' });
            }
            if (key === 'handle_remark') {
                this.setState({ handleRemarkValue: value || '' });
            }
        }
    };

    validateHandleForm = () => {
        let { diffTypeValue, handleRemarkValue } = this.state;
        if (!diffTypeValue) {
            this.showToast('warning', '请选择差异类型');
            return false;
        }
        if (!handleRemarkValue || !handleRemarkValue.trim()) {
            this.showToast('warning', '请填写处理意见');
            return false;
        }
        return true;
    };

    getCardData = () => {
        let formData = this.props.form.getAllFormValue(formId);
        let handleFormData = this.props.form.getAllFormValue(handleFormId);
        let exhibitFormData = this.props.form.getAllFormValue(exhibitFormId);
        let data = {
            [formId]: formData,
            [exhibitFormId]: exhibitFormData,
            [handleFormId]: handleFormData,
            pageCode: pagecode,
            templetid: '1001Z310000000000003'
        };
        return data;
    };

    buttonClick = (props, id) => {
        switch (id) {
            case 'StartProcess':
                this.btnStartProcess();
                break;
            case 'FinishProcess':
                this.btnFinishProcess();
                break;
            case 'CloseDiff':
                this.btnCloseDiff();
                break;
            case 'Save':
                this.btnSave();
                break;
            case 'Back':
                this.btnBack();
                break;
            default:
                break;
        }
    };

    btnStartProcess = () => {
        if (!this.validateHandleForm()) {
            return;
        }
        let data = this.getCardData();
        ajax({
            url: '/nccloud/ata/diff/process.do',
            data: {
                ...data,
                action: 'start'
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    this.refreshCardData(data);
                    this.showToast('success', '开始处理成功');
                }
            }
        });
    };

    btnFinishProcess = () => {
        if (!this.validateHandleForm()) {
            return;
        }
        let data = this.getCardData();
        ajax({
            url: '/nccloud/ata/diff/process.do',
            data: {
                ...data,
                action: 'finish'
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    this.refreshCardData(data);
                    this.showToast('success', '完成处理成功');
                }
            }
        });
    };

    btnCloseDiff = () => {
        NCMsg.show({
            content: '确定要关闭该差异单吗？关闭后不可再修改。',
            beSureBtnClick: () => {
                this.doClose();
            }
        });
    };

    doClose = () => {
        let pk = this.state.pk_diff;
        if (!pk) {
            let formData = this.props.form.getAllFormValue(formId);
            if (formData && formData.rows && formData.rows[0]) {
                pk = formData.rows[0].values.pk_diff.value;
            }
        }
        let ts = null;
        let formData = this.props.form.getAllFormValue(formId);
        if (formData && formData.rows && formData.rows[0]) {
            ts = formData.rows[0].values.ts && formData.rows[0].values.ts.value;
        }
        ajax({
            url: '/nccloud/ata/diff/close.do',
            data: {
                pk: pk,
                ts: ts,
                pageCode: pagecode
            },
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    this.refreshCardData(data);
                    this.showToast('success', '关闭成功');
                }
            }
        });
    };

    btnSave = () => {
        let formFlag = this.props.form.isCheckNow(handleFormId);
        if (!formFlag) {
            return;
        }
        let data = this.getCardData();
        ajax({
            url: '/nccloud/ata/diff/save.do',
            data: data,
            success: (res) => {
                let { success, data } = res;
                if (success && data) {
                    this.refreshCardData(data);
                    this.showToast('success', '保存成功');
                }
            }
        });
    };

    refreshCardData = (data) => {
        if (data[formId]) {
            this.props.form.setAllFormValue({ [formId]: data[formId] });
            let diffStatus = data[formId].rows && data[formId].rows[0] && data[formId].rows[0].values.diff_status && data[formId].rows[0].values.diff_status.value;
            this.setState({ diffStatus: diffStatus });
            this.updateAlertInfo(diffStatus);
            this.updateHandleFormVisibility(diffStatus);
        }
        if (data[handleFormId]) {
            this.props.form.setAllFormValue({ [handleFormId]: data[handleFormId] });
            let diffType = data[handleFormId].rows && data[handleFormId].rows[0] && data[handleFormId].rows[0].values.diff_type && data[handleFormId].rows[0].values.diff_type.value;
            let handleRemark = data[handleFormId].rows && data[handleFormId].rows[0] && data[handleFormId].rows[0].values.handle_remark && data[handleFormId].rows[0].values.handle_remark.value;
            this.setState({
                diffTypeValue: diffType || '',
                handleRemarkValue: handleRemark || ''
            });
        }
        this.props.form.setFormStatus(formId, 'browse');
        this.props.form.setFormStatus(exhibitFormId, 'browse');
        let newDiffStatus = this.state.diffStatus;
        if (newDiffStatus === '3') {
            this.props.form.setFormStatus(handleFormId, 'browse');
        } else {
            this.props.form.setFormStatus(handleFormId, 'edit');
        }
        this.updateButtonStatus();
    };

    btnBack = () => {
        this.props.pushTo('/list', {
            pagecode: listPagecode
        });
    };

    updateButtonStatus = () => {
        let diffStatus = this.state.diffStatus;
        if (diffStatus === '0') {
            this.props.button.setButtonVisible(['StartProcess', 'Save', 'FinishProcess', 'CloseDiff', 'Back'], [true, true, false, false, true]);
        } else if (diffStatus === '1') {
            this.props.button.setButtonVisible(['StartProcess', 'Save', 'FinishProcess', 'CloseDiff', 'Back'], [false, true, true, false, true]);
        } else if (diffStatus === '2') {
            this.props.button.setButtonVisible(['StartProcess', 'Save', 'FinishProcess', 'CloseDiff', 'Back'], [false, false, false, true, true]);
        } else if (diffStatus === '3') {
            this.props.button.setButtonVisible(['StartProcess', 'Save', 'FinishProcess', 'CloseDiff', 'Back'], [false, false, false, false, true]);
        }
    };

    getDiffStatusInfo = () => {
        let diffStatus = this.state.diffStatus;
        return DIFF_STATUS_MAP[diffStatus] || { display: '-', color: 'default' };
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

    onAlertClose = () => {
        this.setState({ showAlert: false });
    };

    renderStatusTag = () => {
        let statusInfo = this.getDiffStatusInfo();
        return (
            <span style={{
                display: 'inline-block',
                padding: '4px 14px',
                borderRadius: '14px',
                color: statusInfo.textColor || '#fff',
                backgroundColor: statusInfo.bgColor || '#1890ff',
                border: `1px solid ${statusInfo.textColor || '#1890ff'}`,
                fontSize: '13px',
                fontWeight: 'bold',
                marginLeft: '12px'
            }}>
                {statusInfo.display}
            </span>
        );
    };

    renderQtyCompare = () => {
        let shipmentQty = 0;
        let returnQty = 0;
        let exhibitFormData = this.props.form.getAllFormValue(exhibitFormId);
        let formData = this.props.form.getAllFormValue(formId);
        if (exhibitFormData && exhibitFormData.rows && exhibitFormData.rows[0]) {
            shipmentQty = exhibitFormData.rows[0].values.shipment_qty && Number(exhibitFormData.rows[0].values.shipment_qty.value) || 0;
            returnQty = exhibitFormData.rows[0].values.return_qty && Number(exhibitFormData.rows[0].values.return_qty.value) || 0;
        }
        if (formData && formData.rows && formData.rows[0]) {
            shipmentQty = formData.rows[0].values.shipment_qty && Number(formData.rows[0].values.shipment_qty.value) || shipmentQty;
            returnQty = formData.rows[0].values.return_qty && Number(formData.rows[0].values.return_qty.value) || returnQty;
        }
        let diffQty = shipmentQty - returnQty;
        let diffColor = '#8c8c8c';
        let diffFontWeight = 'normal';
        let diffDisplay = diffQty;
        if (diffQty > 0) {
            diffColor = '#ff4d4f';
            diffFontWeight = 'bold';
            diffDisplay = `+${diffQty}`;
        } else if (diffQty < 0) {
            diffColor = '#1890ff';
            diffFontWeight = 'bold';
            diffDisplay = `${diffQty}`;
        }
        return (
            <div className="qty-compare-wrapper" style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'stretch',
                gap: '0',
                padding: '20px',
                margin: '10px 0',
                backgroundColor: '#fafafa',
                borderRadius: '6px'
            }}>
                <div style={{
                    flex: 1,
                    textAlign: 'center',
                    padding: '20px',
                    backgroundColor: '#fff',
                    border: '1px solid #e8e8e8',
                    borderRadius: '6px 0 0 6px'
                }}>
                    <div style={{ color: '#8c8c8c', fontSize: '13px', marginBottom: '8px' }}>原出运数量</div>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#262626' }}>{shipmentQty}</div>
                </div>
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    width: '50px',
                    fontSize: '24px',
                    color: '#bfbfbf',
                    backgroundColor: '#f5f5f5'
                }}>
                    →
                </div>
                <div style={{
                    flex: 1,
                    textAlign: 'center',
                    padding: '20px',
                    backgroundColor: '#fff',
                    border: '1px solid #e8e8e8',
                    borderLeft: 'none',
                    borderRight: 'none'
                }}>
                    <div style={{ color: '#8c8c8c', fontSize: '13px', marginBottom: '8px' }}>实际回运数量</div>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#1890ff' }}>{returnQty}</div>
                </div>
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    width: '50px',
                    fontSize: '24px',
                    color: '#bfbfbf',
                    backgroundColor: '#f5f5f5'
                }}>
                    =
                </div>
                <div style={{
                    flex: 1,
                    textAlign: 'center',
                    padding: '20px',
                    backgroundColor: diffQty !== 0 ? '#fff1f0' : '#fff',
                    border: `1px solid ${diffQty !== 0 ? '#ffa39e' : '#e8e8e8'}`,
                    borderRadius: '0 6px 6px 0'
                }}>
                    <div style={{ color: '#8c8c8c', fontSize: '13px', marginBottom: '8px' }}>差异数量</div>
                    <div style={{ fontSize: '32px', fontWeight: diffFontWeight, color: diffColor }}>{diffDisplay}</div>
                </div>
            </div>
        );
    };

    renderLinkSection = () => {
        let pk_shipment = null;
        let pk_return = null;
        let pk_exhibit_list = null;
        let exhibit_list_status = null;
        let exhibit_status = null;
        let formData = this.props.form.getAllFormValue(formId);
        if (formData && formData.rows && formData.rows[0]) {
            pk_shipment = formData.rows[0].values.pk_shipment;
            pk_return = formData.rows[0].values.pk_return;
            pk_exhibit_list = formData.rows[0].values.pk_exhibit_list;
            exhibit_list_status = formData.rows[0].values.exhibit_list_status;
            exhibit_status = formData.rows[0].values.exhibit_status;
        }
        return (
            <div style={{
                padding: '16px 20px',
                backgroundColor: '#fafafa',
                borderRadius: '6px',
                marginTop: '16px'
            }}>
                <div style={{ fontWeight: 'bold', marginBottom: '12px', fontSize: '14px', color: '#262626' }}>
                    <NCIcon type="uf-link" style={{ color: '#1890ff', marginRight: '6px' }} />
                    关联信息
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px' }}>
                    <div style={{ flex: 1, minWidth: '200px' }}>
                        <div style={{ color: '#8c8c8c', fontSize: '12px', marginBottom: '4px' }}>关联出运单</div>
                        <a style={{ color: '#1890ff', cursor: 'pointer' }} onClick={() => this.goToShipment(pk_shipment)}>
                            {pk_shipment && pk_shipment.display || '-'}
                        </a>
                    </div>
                    <div style={{ flex: 1, minWidth: '200px' }}>
                        <div style={{ color: '#8c8c8c', fontSize: '12px', marginBottom: '4px' }}>关联回运单</div>
                        <a style={{ color: '#1890ff', cursor: 'pointer' }} onClick={() => this.goToReturn(pk_return)}>
                            {pk_return && pk_return.display || '-'}
                        </a>
                    </div>
                    <div style={{ flex: 1, minWidth: '200px' }}>
                        <div style={{ color: '#8c8c8c', fontSize: '12px', marginBottom: '4px' }}>关联清单</div>
                        <a style={{ color: '#1890ff', cursor: 'pointer' }} onClick={() => this.goToExhibitList(pk_exhibit_list)}>
                            {pk_exhibit_list && pk_exhibit_list.display || '-'}
                        </a>
                    </div>
                    <div style={{ flex: 1, minWidth: '200px' }}>
                        <div style={{ color: '#8c8c8c', fontSize: '12px', marginBottom: '4px' }}>清单状态</div>
                        <NCTag color="primary">
                            {exhibit_list_status && exhibit_list_status.display || '-'}
                        </NCTag>
                    </div>
                    <div style={{ flex: 1, minWidth: '200px' }}>
                        <div style={{ color: '#8c8c8c', fontSize: '12px', marginBottom: '4px' }}>展品状态</div>
                        <NCTag color="info">
                            {exhibit_status && exhibit_status.display || '-'}
                        </NCTag>
                    </div>
                </div>
            </div>
        );
    };

    goToShipment = (pk) => {
        if (pk && pk.value) {
            this.props.pushTo('/shipment/card', {
                status: 'browse',
                id: pk.value,
                pagecode: '202606ATASHIPCARD'
            });
        }
    };

    goToReturn = (pk) => {
        if (pk && pk.value) {
            this.props.pushTo('/return/card', {
                status: 'browse',
                id: pk.value,
                pagecode: '202606ATARETUCARD'
            });
        }
    };

    goToExhibitList = (pk) => {
        if (pk && pk.value) {
            this.props.pushTo('/exhibitlist/card', {
                status: 'browse',
                id: pk.value,
                pagecode: '202606ATAEXHCARD'
            });
        }
    };

    render() {
        let { form, button } = this.props;
        let { createForm } = form;
        let { createButtonApp } = button;
        let diffStatus = this.state.diffStatus;
        return (
            <div className="nc-bill-card">
                <NCAffix>
                    <NCDiv areaCode={NCDiv.config.HEADER} className="nc-bill-header-area">
                        <div className="header-title-search-area">
                            {createPageIcon()}
                            <h2 className="title-search-detail">差异处理卡片</h2>
                            {(diffStatus != null && diffStatus != undefined) && this.renderStatusTag()}
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
                    {this.state.showAlert && this.state.alertContent && (
                        <div style={{ padding: '10px 20px' }}>
                            <NCAlert
                                type={this.state.alertType}
                                message={this.state.alertContent}
                                showIcon
                                closeAfterClick={this.state.alertCloseable}
                                onClose={this.onAlertClose.bind(this)}
                            />
                        </div>
                    )}
                    <NCDiv areaCode={NCDiv.config.FORM} className="nc-bill-form-top">
                        <div className="nc-bill-form-area" style={{ padding: '10px 20px' }}>
                            <div style={{
                                fontWeight: 'bold',
                                padding: '10px 0',
                                fontSize: '14px',
                                color: '#262626',
                                borderBottom: '1px solid #e8e8e8',
                                marginBottom: '12px'
                            }}>
                                <NCIcon type="uf-file" style={{ color: '#1890ff', marginRight: '6px' }} />
                                基本信息
                            </div>
                            {createForm(formId, {
                                onAfterEvent: this.afterEvent.bind(this)
                            })}
                        </div>
                    </NCDiv>
                    <NCDiv areaCode={NCDiv.config.TABLE} className="nc-bill-form-bottom">
                        <div style={{ padding: '10px 20px' }}>
                            <div style={{
                                fontWeight: 'bold',
                                padding: '10px 0',
                                fontSize: '14px',
                                color: '#262626',
                                borderBottom: '1px solid #e8e8e8',
                                marginBottom: '12px'
                            }}>
                                <NCIcon type="uf-box" style={{ color: '#52c41a', marginRight: '6px' }} />
                                展品信息
                            </div>
                            {createForm(exhibitFormId, {
                                onAfterEvent: this.afterEvent.bind(this)
                            })}
                            {this.renderQtyCompare()}
                        </div>
                        <div style={{ padding: '10px 20px' }}>
                            <div style={{
                                fontWeight: 'bold',
                                padding: '10px 0',
                                fontSize: '14px',
                                color: '#262626',
                                borderBottom: '1px solid #e8e8e8',
                                marginBottom: '12px'
                            }}>
                                <NCIcon type="uf-correct" style={{ color: '#faad14', marginRight: '6px' }} />
                                处理信息
                            </div>
                            {createForm(handleFormId, {
                                onAfterEvent: this.afterEvent.bind(this)
                            })}
                            {this.renderLinkSection()}
                        </div>
                    </NCDiv>
                </div>
                {this.state.showToast && <NCMsg.Success content={this.state.toastContent} duration={3} />}
            </div>
        );
    }
}

export default DiffCard = createPage({
    billinfo: {
        billtype: 'card',
        pagecode: pagecode,
        headcode: formId
    },
    mutiLangCode: '202606ATADIFFCARD'
})(DiffCard);
