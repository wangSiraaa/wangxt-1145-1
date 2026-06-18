<template>
    <div class="nc-bill-card">
        <NCAffix>
            <div class="header-button-area">
                <Button
                    v-for="btn in headerButtons"
                    :key="btn.key"
                    :color="btn.color || 'default'"
                    :disabled="btn.disabled"
                    @click="onButtonClick(btn.key)"
                >
                    {{ btn.name }}
                </Button>
            </div>
        </NCAffix>
        <div class="nc-bill-form-area">
            <div class="card-form-title">展品清单信息</div>
            <NCForm
                ref="formRef"
                :model="formData"
                :label-width="110"
                label-position="right"
                :rules="formRules"
            >
                <NCRow :gutter="16">
                    <NCCol :span="8">
                        <NCFormItem label="清单编号" prop="list_code">
                            <NCInput
                                v-model="formData.list_code"
                                disabled
                                placeholder="保存后自动生成"
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="8">
                        <NCFormItem label="清单名称" prop="list_name">
                            <NCInput
                                v-model="formData.list_name"
                                placeholder="请输入清单名称"
                                :disabled="isReadOnly"
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="8">
                        <NCFormItem label="展商" prop="exhibitor_name">
                            <div style="display: flex; width: 100%;">
                                <NCInput
                                    v-model="formData.exhibitor_name"
                                    placeholder="请选择展商"
                                    style="flex: 1;"
                                    disabled
                                />
                                <Button
                                    type="primary"
                                    icon="iconfont icon-search"
                                    style="margin-left: 4px;"
                                    :disabled="isReadOnly"
                                    @click="openExhibitorRef"
                                >
                                    选择
                                </Button>
                            </div>
                        </NCFormItem>
                    </NCCol>
                </NCRow>
                <NCRow :gutter="16">
                    <NCCol :span="8">
                        <NCFormItem label="展会名称" prop="exhibition_name">
                            <NCInput
                                v-model="formData.exhibition_name"
                                placeholder="请输入展会名称"
                                :disabled="isReadOnly"
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="8">
                        <NCFormItem label="申请日期" prop="apply_date">
                            <NCDatePicker
                                v-model="formData.apply_date"
                                placeholder="请选择日期"
                                :disabled="isReadOnly"
                                style="width: 100%;"
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="8">
                        <NCFormItem label="状态" prop="list_status">
                            <NCTag
                                v-if="formData.list_status"
                                :color="getStatusColor(formData.list_status)"
                                style="line-height: 28px; padding: 0 10px;"
                            >
                                {{ getStatusDisplay(formData.list_status) }}
                            </NCTag>
                            <span v-else>-</span>
                        </NCFormItem>
                    </NCCol>
                </NCRow>
                <NCRow :gutter="16">
                    <NCCol :span="24">
                        <NCFormItem label="备注" prop="remark">
                            <NCInput
                                v-model="formData.remark"
                                type="textarea"
                                :rows="3"
                                placeholder="请输入备注"
                                :disabled="isReadOnly"
                            />
                        </NCFormItem>
                    </NCCol>
                </NCRow>
            </NCForm>
        </div>
        <div class="nc-bill-card-table-area">
            <div class="table-header">
                <span class="table-title">展品明细</span>
                <div class="table-buttons">
                    <Button
                        size="small"
                        color="primary"
                        icon="iconfont icon-add"
                        :disabled="isReadOnly"
                        @click="addLine"
                    >
                        增行
                    </Button>
                    <Button
                        size="small"
                        color="danger"
                        icon="iconfont icon-delete"
                        :disabled="isReadOnly || selectedRowKeys.length === 0"
                        @click="deleteLines"
                    >
                        删除
                    </Button>
                </div>
            </div>
            <NCEditTable
                ref="editTableRef"
                :columns="editTableColumns"
                :data="tableData"
                :row-key="'row_id'"
                :show-index="true"
                :selected-change="onTableSelectedChange"
                :cell-value-change="onCellValueChange"
                border
            >
                <template #serial_no="scope">
                    <div
                        :class="{ 'cell-error': isSerialNoEmpty(scope.row) }"
                        style="width: 100%;"
                    >
                        <NCTooltip
                            v-if="isSerialNoEmpty(scope.row)"
                            placement="top"
                        >
                            <template slot="content">
                                <span style="color: #f5222d;">序列号缺失，不能申报</span>
                            </template>
                            <NCInput
                                v-model="scope.row.serial_no"
                                placeholder="请输入序列号"
                                :disabled="isReadOnly"
                                class="input-error"
                                @blur="onCellBlur(scope)"
                            />
                        </NCTooltip>
                        <NCInput
                            v-else
                            v-model="scope.row.serial_no"
                            placeholder="请输入序列号"
                            :disabled="isReadOnly"
                            @blur="onCellBlur(scope)"
                        />
                    </div>
                </template>
                <template #exhibit_status="scope">
                    <NCTag
                        v-if="scope.row.exhibit_status"
                        color="default"
                    >
                        {{ scope.row.exhibit_status }}
                    </NCTag>
                    <span v-else>-</span>
                </template>
                <template #opr="scope">
                    <Button
                        type="danger"
                        size="xsmall"
                        icon="iconfont icon-delete"
                        :disabled="isReadOnly"
                        @click="deleteLine(scope.index)"
                    >
                    </Button>
                </template>
            </NCEditTable>
        </div>
    </div>
</template>

<script>
import { ajax, toast } from 'nc-lightapp-front';
import { PAGECODE, AREA, URL, LIST_STATUS, CARD_STATUS, STATUS_MAP } from '../const';

export default {
    name: 'ExhibitListCard',
    data() {
        return {
            pageStatus: CARD_STATUS.add,
            pk_exhibit_list: '',
            formData: {
                pk_exhibit_list: '',
                list_code: '',
                list_name: '',
                pk_exhibitor: '',
                exhibitor_name: '',
                exhibition_name: '',
                apply_date: '',
                list_status: LIST_STATUS.draft,
                remark: ''
            },
            formRules: {
                list_name: [
                    { required: true, message: '清单名称不能为空', trigger: 'blur' }
                ],
                exhibitor_name: [
                    { required: true, message: '展商不能为空', trigger: 'change' }
                ],
                exhibition_name: [
                    { required: true, message: '展会名称不能为空', trigger: 'blur' }
                ]
            },
            headerButtons: [
                {
                    key: 'Save',
                    name: '保存',
                    color: 'primary'
                },
                {
                    key: 'Delete',
                    name: '删除',
                    color: 'default',
                    disabled: true
                },
                {
                    key: 'Commit',
                    name: '提交申报',
                    color: 'warning',
                    disabled: true
                },
                {
                    key: 'UnCommit',
                    name: '收回',
                    color: 'default',
                    disabled: true
                },
                {
                    key: 'Back',
                    name: '返回列表',
                    color: 'default'
                }
            ],
            editTableColumns: [
                {
                    title: '展品编码',
                    key: 'exhibit_code',
                    dataIndex: 'exhibit_code',
                    width: 120,
                    editable: true,
                    editType: 'input'
                },
                {
                    title: '展品名称',
                    key: 'exhibit_name',
                    dataIndex: 'exhibit_name',
                    width: 160,
                    editable: true,
                    editType: 'input',
                    required: true
                },
                {
                    title: '序列号',
                    key: 'serial_no',
                    dataIndex: 'serial_no',
                    width: 160,
                    slot: 'serial_no',
                    editable: true,
                    editType: 'input',
                    required: true
                },
                {
                    title: '规格',
                    key: 'specification',
                    dataIndex: 'specification',
                    width: 140,
                    editable: true,
                    editType: 'input'
                },
                {
                    title: '数量',
                    key: 'quantity',
                    dataIndex: 'quantity',
                    width: 100,
                    editable: true,
                    editType: 'number'
                },
                {
                    title: '单位',
                    key: 'unit',
                    dataIndex: 'unit',
                    width: 80,
                    editable: true,
                    editType: 'input'
                },
                {
                    title: '价值',
                    key: 'value',
                    dataIndex: 'value',
                    width: 120,
                    editable: true,
                    editType: 'number'
                },
                {
                    title: '币种',
                    key: 'currency',
                    dataIndex: 'currency',
                    width: 100,
                    editable: true,
                    editType: 'input'
                },
                {
                    title: 'HS编码',
                    key: 'hs_code',
                    dataIndex: 'hs_code',
                    width: 120,
                    editable: true,
                    editType: 'input'
                },
                {
                    title: '展品状态',
                    key: 'exhibit_status',
                    dataIndex: 'exhibit_status',
                    width: 100,
                    slot: 'exhibit_status'
                },
                {
                    title: '操作',
                    key: 'opr',
                    dataIndex: 'opr',
                    width: 80,
                    fixed: 'right',
                    slot: 'opr'
                }
            ],
            tableData: [],
            selectedRowKeys: [],
            tableSelectedRows: []
        };
    },
    computed: {
        isReadOnly() {
            return this.pageStatus === CARD_STATUS.browse;
        },
        isEditMode() {
            return this.pageStatus === CARD_STATUS.add || this.pageStatus === CARD_STATUS.edit;
        }
    },
    created() {
        this.initPageByQuery();
    },
    mounted() {
        this.initDefaultData();
    },
    methods: {
        initPageByQuery() {
            let query = this.$route.query || {};
            this.pageStatus = query.status || CARD_STATUS.add;
            this.pk_exhibit_list = query.id || '';
            if (this.pk_exhibit_list) {
                this.queryCardData();
            }
        },
        initDefaultData() {
            if (this.pageStatus === CARD_STATUS.add) {
                let today = this.getToday();
                this.$set(this.formData, 'apply_date', today);
                this.$set(this.formData, 'list_status', LIST_STATUS.draft);
                this.updateButtonStatus();
            }
        },
        getToday() {
            let date = new Date();
            let year = date.getFullYear();
            let month = String(date.getMonth() + 1).padStart(2, '0');
            let day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        getStatusDisplay(status) {
            let item = STATUS_MAP[status];
            return item ? item.display : status;
        },
        getStatusColor(status) {
            let item = STATUS_MAP[status];
            return item ? item.color : 'default';
        },
        isSerialNoEmpty(row) {
            return !row.serial_no || row.serial_no.toString().trim() === '';
        },
        queryCardData() {
            let data = {
                pk_exhibit_list: this.pk_exhibit_list,
                pageid: PAGECODE.card_pagecode
            };
            ajax({
                url: URL.querycard,
                data: data,
                success: (res) => {
                    if (res.success && res.data) {
                        let parent = res.data.parent || {};
                        let children = res.data.children || [];
                        Object.keys(parent).forEach((key) => {
                            if (this.formData.hasOwnProperty(key)) {
                                this.$set(this.formData, key, parent[key]);
                            }
                        });
                        this.tableData = children.map((item, index) => {
                            return {
                                ...item,
                                row_id: item.row_id || this.getUUID()
                            };
                        });
                        this.updateButtonStatus();
                    }
                }
            });
        },
        updateButtonStatus() {
            let status = this.formData.list_status;
            this.headerButtons.forEach((btn) => {
                switch (btn.key) {
                    case 'Save':
                        btn.disabled = !this.isEditMode;
                        break;
                    case 'Delete':
                        btn.disabled = this.pageStatus === CARD_STATUS.add;
                        break;
                    case 'Commit':
                        btn.disabled = status !== LIST_STATUS.draft || !this.pk_exhibit_list;
                        break;
                    case 'UnCommit':
                        btn.disabled = status !== LIST_STATUS.submitted;
                        break;
                    default:
                        btn.disabled = false;
                        break;
                }
            });
        },
        onButtonClick(key) {
            switch (key) {
                case 'Save':
                    this.handleSave();
                    break;
                case 'Delete':
                    this.handleDelete();
                    break;
                case 'Commit':
                    this.handleCommit();
                    break;
                case 'UnCommit':
                    this.handleUnCommit();
                    break;
                case 'Back':
                    this.handleBack();
                    break;
                default:
                    break;
            }
        },
        handleBack() {
            this.$router.push({
                path: '/ata/exhibitlist/list',
                query: {
                    pagecode: PAGECODE.list_pagecode
                }
            });
        },
        validateForm() {
            let valid = true;
            if (!this.formData.list_name || this.formData.list_name.trim() === '') {
                toast({ color: 'warning', content: '清单名称不能为空' });
                return false;
            }
            if (!this.formData.exhibitor_name || this.formData.exhibitor_name.trim() === '') {
                toast({ color: 'warning', content: '展商不能为空' });
                return false;
            }
            if (!this.formData.exhibition_name || this.formData.exhibition_name.trim() === '') {
                toast({ color: 'warning', content: '展会名称不能为空' });
                return false;
            }
            return valid;
        },
        validateSerialNo() {
            let invalidItems = [];
            this.tableData.forEach((row) => {
                if (this.isSerialNoEmpty(row)) {
                    let exhibitName = row.exhibit_name || row.exhibit_code || '展品';
                    invalidItems.push(exhibitName);
                }
            });
            if (invalidItems.length > 0) {
                let msg = '展品"' + invalidItems[0] + '"序列号缺失，不能申报';
                if (invalidItems.length > 1) {
                    msg = '有' + invalidItems.length + '个展品序列号缺失，不能申报';
                }
                toast({ color: 'danger', content: msg });
                return false;
            }
            return true;
        },
        buildBillData() {
            let children = this.tableData.map((row, index) => {
                let newRow = { ...row };
                if (!newRow.pk_exhibit) {
                    delete newRow.pk_exhibit;
                }
                delete newRow.row_id;
                if (newRow.quantity !== undefined && newRow.quantity !== null && newRow.quantity !== '') {
                    newRow.quantity = Number(newRow.quantity);
                }
                if (newRow.value !== undefined && newRow.value !== null && newRow.value !== '') {
                    newRow.value = Number(newRow.value);
                }
                return newRow;
            });
            return {
                model: {
                    parent: { ...this.formData },
                    children: children
                },
                pageid: PAGECODE.card_pagecode
            };
        },
        handleSave() {
            if (!this.validateForm()) {
                return;
            }
            let data = this.buildBillData();
            ajax({
                url: URL.save,
                data: data,
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '保存成功' });
                        if (res.data && res.data.parent) {
                            let parent = res.data.parent;
                            Object.keys(parent).forEach((key) => {
                                if (this.formData.hasOwnProperty(key)) {
                                    this.$set(this.formData, key, parent[key]);
                                }
                            });
                            if (parent.pk_exhibit_list) {
                                this.pk_exhibit_list = parent.pk_exhibit_list;
                            }
                        }
                        if (res.data && res.data.children) {
                            this.tableData = res.data.children.map((item, index) => {
                                return {
                                    ...item,
                                    row_id: item.row_id || this.getUUID()
                                };
                            });
                        }
                        if (this.pageStatus === CARD_STATUS.add) {
                            this.pageStatus = CARD_STATUS.edit;
                        }
                        this.updateButtonStatus();
                    }
                }
            });
        },
        handleDelete() {
            if (!this.pk_exhibit_list) {
                toast({ color: 'warning', content: '请先保存再删除' });
                return;
            }
            this.$Modal.confirm({
                title: '删除确认',
                content: '确定要删除该展品清单吗？',
                onOk: () => {
                    ajax({
                        url: URL.delete,
                        data: { pks: [this.pk_exhibit_list] },
                        success: (res) => {
                            if (res.success) {
                                toast({ color: 'success', content: '删除成功' });
                                this.handleBack();
                            }
                        }
                    });
                }
            });
        },
        handleCommit() {
            if (!this.pk_exhibit_list) {
                toast({ color: 'warning', content: '请先保存再提交' });
                return;
            }
            if (!this.validateSerialNo()) {
                return;
            }
            ajax({
                url: URL.commit,
                data: { pks: [this.pk_exhibit_list] },
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '提交成功' });
                        this.$set(this.formData, 'list_status', LIST_STATUS.submitted);
                        this.updateButtonStatus();
                    }
                }
            });
        },
        handleUnCommit() {
            if (!this.pk_exhibit_list) {
                return;
            }
            ajax({
                url: URL.uncommit,
                data: { pks: [this.pk_exhibit_list] },
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '收回成功' });
                        this.$set(this.formData, 'list_status', LIST_STATUS.draft);
                        this.updateButtonStatus();
                    }
                }
            });
        },
        openExhibitorRef() {
            toast({ color: 'info', content: '请配置展商参照' });
        },
        getUUID() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                let r = Math.random() * 16 | 0;
                let v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },
        addLine() {
            let newRow = {
                row_id: this.getUUID(),
                exhibit_code: '',
                exhibit_name: '',
                serial_no: '',
                specification: '',
                quantity: 1,
                unit: '',
                value: 0,
                currency: '',
                hs_code: '',
                exhibit_status: '待申报'
            };
            this.tableData.push(newRow);
        },
        deleteLine(index) {
            this.tableData.splice(index, 1);
        },
        deleteLines() {
            if (this.selectedRowKeys.length === 0) {
                toast({ color: 'warning', content: '请先选择要删除的行' });
                return;
            }
            this.tableData = this.tableData.filter((row) => {
                return !this.selectedRowKeys.includes(row.row_id);
            });
            this.selectedRowKeys = [];
            this.tableSelectedRows = [];
        },
        onTableSelectedChange(keys, rows) {
            this.selectedRowKeys = keys;
            this.tableSelectedRows = rows;
        },
        onCellValueChange(value, row, rowIndex, column) {
        },
        onCellBlur(scope) {
            let row = scope.row;
            let index = scope.index;
        }
    }
};
</script>

<style scoped lang="scss">
.nc-bill-card {
    padding: 10px;
}
.header-button-area {
    background: #fff;
    padding: 10px;
    border-bottom: 1px solid #eee;
    margin-bottom: 10px;
    .el-button {
        margin-right: 8px;
    }
}
.nc-bill-form-area {
    background: #fff;
    padding: 16px 20px;
    border-radius: 4px;
    margin-bottom: 10px;
    .card-form-title {
        font-size: 16px;
        font-weight: bold;
        padding: 10px 0;
        border-bottom: 1px solid #eee;
        margin-bottom: 16px;
    }
}
.nc-bill-card-table-area {
    background: #fff;
    padding: 16px;
    border-radius: 4px;
    .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 0;
        margin-bottom: 10px;
        border-bottom: 1px solid #eee;
        .table-title {
            font-size: 16px;
            font-weight: bold;
        }
        .table-buttons {
            .el-button {
                margin-left: 8px;
            }
        }
    }
}
.cell-error {
    border: 1px solid #f5222d;
    border-radius: 4px;
    .el-input__inner {
        border: none !important;
    }
}
.input-error .el-input__inner {
    border: 1px solid #f5222d !important;
    background-color: #fff1f0;
}
</style>
