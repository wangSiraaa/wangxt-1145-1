export const PAGECODE = {
    list_pagecode: '202606ATAEXHLIST',
    card_pagecode: '202606ATAEXHCARD'
};

export const AREA = {
    list_table: 'list_head',
    list_search: 'search_area',
    card_form: 'card_head',
    card_table: 'exhibit_table'
};

export const URL = {
    query: '/nccloud/ata/exhibitlist/query.do',
    querybypks: '/nccloud/ata/exhibitlist/query.do',
    querycard: '/nccloud/ata/exhibitlist/querycard.do',
    save: '/nccloud/ata/exhibitlist/save.do',
    delete: '/nccloud/ata/exhibitlist/delete.do',
    commit: '/nccloud/ata/exhibitlist/commit.do',
    uncommit: '/nccloud/ata/exhibitlist/uncommit.do'
};

export const LIST_BUTTON = {
    add: 'Add',
    delete: 'Delete',
    commit: 'CommitBatch',
    refresh: 'Refresh',
    edit: 'Edit',
    commit_inner: 'CommitInner',
    uncommit_inner: 'UnCommitInner'
};

export const CARD_BUTTON = {
    save: 'Save',
    saveadd: 'SaveAdd',
    delete: 'Delete',
    commit: 'Commit',
    uncommit: 'UnCommit',
    back: 'Back',
    addline: 'AddLine',
    delline: 'DeleteLine'
};

export const LIST_STATUS = {
    draft: '0',
    submitted: '1',
    auditing: '2',
    pass: '3',
    nopass: '4'
};

export const CARD_STATUS = {
    add: 'add',
    edit: 'edit',
    browse: 'browse',
    copy: 'copy'
};

export const STATUS_MAP = {
    '0': { display: '草稿', color: 'default' },
    '1': { display: '已提交', color: 'primary' },
    '2': { display: '审核中', color: 'warning' },
    '3': { display: '审核通过', color: 'success' },
    '4': { display: '审核不通过', color: 'danger' }
};
