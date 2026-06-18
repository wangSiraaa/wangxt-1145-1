<template>
    <div class="nc-bill-list">
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
        <div class="nc-bill-search-area">
            <NCForm
                :model="searchForm"
                :label-width="90"
                label-position="right"
            >
                <NCRow :gutter="16">
                    <NCCol :span="6">
                        <NCFormItem label="清单编号" prop="list_code">
                            <NCInput
                                v-model="searchForm.list_code"
                                placeholder="请输入清单编号"
                                clearable
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="6">
                        <NCFormItem label="清单名称" prop="list_name">
                            <NCInput
                                v-model="searchForm.list_name"
                                placeholder="请输入清单名称"
                                clearable
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="6">
                        <NCFormItem label="展商" prop="exhibitor_name">
                            <NCInput
                                v-model="searchForm.exhibitor_name"
                                placeholder="请输入展商名称"
                                clearable
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="6">
                        <NCFormItem label="展会" prop="exhibition_name">
                            <NCInput
                                v-model="searchForm.exhibition_name"
                                placeholder="请输入展会名称"
                                clearable
                            />
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="6">
                        <NCFormItem label="状态" prop="list_status">
                            <NCSelect
                                v-model="searchForm.list_status"
                                placeholder="请选择状态"
                                clearable
                            >
                                <NCOption
                                    v-for="item in statusOptions"
                                    :key="item.value"
                                    :label="item.display"
                                    :value="item.value"
                                />
                            </NCSelect>
                        </NCFormItem>
                    </NCCol>
                    <NCCol :span="6">
                        <NCFormItem label-width="0">
                            <Button color="primary" @click="onSearch">
                                <i class="iconfont icon-search"></i>查询
                            </Button>
                            <Button color="default" @click="onResetSearch">
                                <i class="iconfont icon-reset"></i>重置
                            </Button>
                        </NCFormItem>
                    </NCCol>
                </NCRow>
            </NCForm>
        </div>
        <div class="table-area">
            <NCTable
                ref="tableRef"
                :columns="columns"
                :data="tableData"
                :pagination="pagination"
                :row-key="'pk_exhibit_list'"
                :column-width="100"
                :selected-change="onSelectedChange"
                @row-dblclick="onRowDoubleClick"
                @page-change="onPageChange"
                @size-change="onSizeChange"
                border
            >
                <template #list_status="scope">
                    <NCTag
                        v-if="scope.row.list_status"
                        :color="getStatusColor(scope.row.list_status)"
                    >
                        {{ getStatusDisplay(scope.row.list_status) }}
                    </NCTag>
                </template>
                <template #opr="scope">
                    <Button
                        v-if="canEdit(scope.row)"
                        size="xsmall"
                        color="link"
                        @click="handleEdit(scope.row)"
                    >
                        编辑
                    </Button>
                    <Button
                        v-if="canCommit(scope.row)"
                        size="xsmall"
                        color="link"
                        @click="handleCommit(scope.row)"
                    >
                        提交
                    </Button>
                    <Button
                        v-if="canUncommit(scope.row)"
                        size="xsmall"
                        color="link"
                        @click="handleUncommit(scope.row)"
                    >
                        收回
                    </Button>
                </template>
            </NCTable>
        </div>
    </div>
</template>

<script>
import { ajax, toast } from 'nc-lightapp-front';
import { PAGECODE, AREA, URL, LIST_STATUS, STATUS_MAP } from '../const';

export default {
    name: 'ExhibitListList',
    data() {
        return {
            searchForm: {
                list_code: '',
                list_name: '',
                exhibitor_name: '',
                exhibition_name: '',
                list_status: ''
            },
            statusOptions: [
                { value: '0', display: '草稿' },
                { value: '1', display: '已提交' },
                { value: '2', display: '审核中' },
                { value: '3', display: '审核通过' },
                { value: '4', display: '审核不通过' }
            ],
            headerButtons: [
                {
                    key: 'Add',
                    name: '新增',
                    color: 'primary'
                },
                {
                    key: 'Delete',
                    name: '删除',
                    color: 'default',
                    disabled: true
                },
                {
                    key: 'CommitBatch',
                    name: '批量提交',
                    color: 'default',
                    disabled: true
                },
                {
                    key: 'Refresh',
                    name: '刷新',
                    color: 'default'
                }
            ],
            columns: [
                {
                    title: '清单编号',
                    key: 'list_code',
                    dataIndex: 'list_code',
                    width: 140,
                    ellipsis: true
                },
                {
                    title: '清单名称',
                    key: 'list_name',
                    dataIndex: 'list_name',
                    width: 160,
                    ellipsis: true
                },
                {
                    title: '展商',
                    key: 'exhibitor_name',
                    dataIndex: 'exhibitor_name',
                    width: 140,
                    ellipsis: true
                },
                {
                    title: '展会',
                    key: 'exhibition_name',
                    dataIndex: 'exhibition_name',
                    width: 140,
                    ellipsis: true
                },
                {
                    title: '申请日期',
                    key: 'apply_date',
                    dataIndex: 'apply_date',
                    width: 120
                },
                {
                    title: '状态',
                    key: 'list_status',
                    dataIndex: 'list_status',
                    width: 100,
                    slot: 'list_status'
                },
                {
                    title: '创建人',
                    key: 'creator',
                    dataIndex: 'creator',
                    width: 100,
                    ellipsis: true
                },
                {
                    title: '创建时间',
                    key: 'creationtime',
                    dataIndex: 'creationtime',
                    width: 160
                },
                {
                    title: '操作',
                    key: 'opr',
                    dataIndex: 'opr',
                    width: 160,
                    fixed: 'right',
                    slot: 'opr'
                }
            ],
            tableData: [],
            selectedRows: [],
            pagination: {
                current: 1,
                pageSize: 10,
                total: 0,
                showSizeChanger: true,
                showTotal: true,
                pageSizeOptions: ['10', '20', '50', '100']
            }
        };
    },
    created() {
        this.queryData();
    },
    methods: {
        getStatusDisplay(status) {
            let item = STATUS_MAP[status];
            return item ? item.display : status;
        },
        getStatusColor(status) {
            let item = STATUS_MAP[status];
            return item ? item.color : 'default';
        },
        canEdit(row) {
            return row.list_status === LIST_STATUS.draft;
        },
        canCommit(row) {
            return row.list_status === LIST_STATUS.draft;
        },
        canUncommit(row) {
            return row.list_status === LIST_STATUS.submitted;
        },
        buildSearchCondition() {
            let conditions = {};
            Object.keys(this.searchForm).forEach((key) => {
                if (this.searchForm[key] !== '' && this.searchForm[key] != null) {
                    conditions[key] = this.searchForm[key];
                }
            });
            return conditions;
        },
        queryData() {
            let queryCondition = this.buildSearchCondition();
            let data = {
                pageid: PAGECODE.list_pagecode,
                querycondition: queryCondition,
                queryInfo: {
                    pageIndex: this.pagination.current - 1,
                    pageSize: this.pagination.pageSize
                },
                oid: PAGECODE.list_pagecode
            };
            ajax({
                url: URL.query,
                data: data,
                success: (res) => {
                    if (res.success && res.data) {
                        let tableData = res.data[AREA.list_table] || {};
                        this.tableData = tableData.rows || [];
                        this.pagination.total = tableData.total || 0;
                    } else {
                        this.tableData = [];
                        this.pagination.total = 0;
                    }
                }
            });
        },
        onSearch() {
            this.pagination.current = 1;
            this.queryData();
        },
        onResetSearch() {
            this.searchForm = {
                list_code: '',
                list_name: '',
                exhibitor_name: '',
                exhibition_name: '',
                list_status: ''
            };
            this.pagination.current = 1;
            this.queryData();
        },
        onPageChange(pageIndex) {
            this.pagination.current = pageIndex;
            this.queryData();
        },
        onSizeChange(size) {
            this.pagination.pageSize = size;
            this.pagination.current = 1;
            this.queryData();
        },
        onSelectedChange(rows, record) {
            this.selectedRows = rows;
            let hasSelected = rows.length > 0;
            this.headerButtons.forEach((btn) => {
                if (btn.key === 'Delete' || btn.key === 'CommitBatch') {
                    if (!hasSelected) {
                        btn.disabled = true;
                    } else {
                        let flag = rows.some((item) => {
                            return item.list_status !== LIST_STATUS.draft;
                        });
                        btn.disabled = flag;
                    }
                }
            });
        },
        onRowDoubleClick(record, index) {
            let id = record.pk_exhibit_list;
            if (id) {
                this.$router.push({
                    path: '/ata/exhibitlist/card',
                    query: {
                        status: 'browse',
                        id: id,
                        pagecode: PAGECODE.card_pagecode
                    }
                });
            }
        },
        onButtonClick(key) {
            switch (key) {
                case 'Add':
                    this.handleAdd();
                    break;
                case 'Delete':
                    this.handleBatchDelete();
                    break;
                case 'CommitBatch':
                    this.handleBatchCommit();
                    break;
                case 'Refresh':
                    this.handleRefresh();
                    break;
                default:
                    break;
            }
        },
        handleAdd() {
            this.$router.push({
                path: '/ata/exhibitlist/card',
                query: {
                    status: 'add',
                    pagecode: PAGECODE.card_pagecode
                }
            });
        },
        handleEdit(row) {
            let id = row.pk_exhibit_list;
            this.$router.push({
                path: '/ata/exhibitlist/card',
                query: {
                    status: 'edit',
                    id: id,
                    pagecode: PAGECODE.card_pagecode
                }
            });
        },
        handleBatchDelete() {
            if (this.selectedRows.length === 0) {
                toast({ color: 'warning', content: '请先选择要删除的数据！' });
                return;
            }
            let pks = this.selectedRows.map((item) => item.pk_exhibit_list);
            ajax({
                url: URL.delete,
                data: { pks: pks },
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '删除成功！' });
                        this.queryData();
                    }
                }
            });
        },
        handleBatchCommit() {
            if (this.selectedRows.length === 0) {
                toast({ color: 'warning', content: '请先选择要提交的数据！' });
                return;
            }
            let pks = this.selectedRows.map((item) => item.pk_exhibit_list);
            ajax({
                url: URL.commit,
                data: { pks: pks },
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '提交成功！' });
                        this.queryData();
                    }
                }
            });
        },
        handleCommit(row) {
            let id = row.pk_exhibit_list;
            ajax({
                url: URL.commit,
                data: { pks: [id] },
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '提交成功！' });
                        this.queryData();
                    }
                }
            });
        },
        handleUncommit(row) {
            let id = row.pk_exhibit_list;
            ajax({
                url: URL.uncommit,
                data: { pks: [id] },
                success: (res) => {
                    if (res.success) {
                        toast({ color: 'success', content: '收回成功！' });
                        this.queryData();
                    }
                }
            });
        },
        handleRefresh() {
            this.queryData();
            toast({ color: 'success', content: '刷新成功！' });
        }
    }
};
</script>

<style scoped lang="scss">
.nc-bill-list {
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
.nc-bill-search-area {
    background: #fff;
    padding: 16px 10px;
    border-radius: 4px;
    margin-bottom: 10px;
}
.table-area {
    background: #fff;
    padding: 10px;
    border-radius: 4px;
}
</style>
