-- ===========================================================================================
-- ATA 展品管理系统数据库建表脚本
-- 数据库类型: Oracle 12c+ / 达梦数据库
-- 创建日期: 2026-06-18
-- 说明: 所有标识符使用双引号，类型使用 NVARCHAR2 / DATE / NUMBER，兼容 Oracle 与达梦
-- ===========================================================================================

-- =================================== 序列定义 =============================================

"CREATE" SEQUENCE "SEQ_ATA_EXHIBIT_LIST"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_EXHIBIT"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_DOCUMENT"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_SHIPMENT"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_SHIPMENT_DETAIL"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_RETURN"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_RETURN_DETAIL"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;

"CREATE" SEQUENCE "SEQ_ATA_DIFF"
    START WITH 100000
    INCREMENT BY 1
    NOMAXVALUE
    NOMINVALUE
    NOCYCLE
    CACHE 20;


-- ================================ 1. ata_exhibit_list（展品清单主表） ========================

"CREATE" TABLE "ATA_EXHIBIT_LIST" (
    "PK_EXHIBIT_LIST"   NVARCHAR2(20)   NOT NULL,
    "LIST_CODE"         NVARCHAR2(50)   NOT NULL,
    "LIST_NAME"         NVARCHAR2(200)  NOT NULL,
    "PK_EXHIBITOR"      NVARCHAR2(20),
    "EXHIBITOR_NAME"    NVARCHAR2(200),
    "EXHIBITION_NAME"   NVARCHAR2(200)  NOT NULL,
    "APPLY_DATE"        DATE,
    "LIST_STATUS"       NUMBER(2)       DEFAULT 0,
    "REMARK"            NVARCHAR2(500),
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_EXHIBIT_LIST" PRIMARY KEY ("PK_EXHIBIT_LIST")
        USING INDEX TABLESPACE "USERS"
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_EXHIBIT_LIST"              IS '展品清单主表';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."PK_EXHIBIT_LIST" IS '主键';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."LIST_CODE"        IS '清单编号 EX+yyyyMMdd+序号';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."LIST_NAME"        IS '清单名称';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."PK_EXHIBITOR"     IS '展商ID';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."EXHIBITOR_NAME"   IS '展商名称';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."EXHIBITION_NAME"  IS '展会名称';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."APPLY_DATE"       IS '申请日期';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."LIST_STATUS"      IS '清单状态 0草稿/1提交/2审核中/3通过/4不通过';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."REMARK"           IS '备注';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."PK_GROUP"         IS '集团主键';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."PK_ORG"           IS '组织主键';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."CREATOR"          IS '创建人';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."CREATIONTIME"     IS '创建时间';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."MODIFIER"         IS '修改人';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."MODIFIEDTIME"     IS '修改时间';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."DR"               IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_EXHIBIT_LIST"."TS"               IS '时间戳';

"CREATE" UNIQUE INDEX "IDX_ATA_EXHIBIT_LIST_CODE" ON "ATA_EXHIBIT_LIST" ("LIST_CODE") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_EXHIBIT_LIST_STATUS" ON "ATA_EXHIBIT_LIST" ("LIST_STATUS") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_EXHIBIT_LIST_DATE"   ON "ATA_EXHIBIT_LIST" ("APPLY_DATE")  TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_EXHIBIT_LIST_EXHBT"  ON "ATA_EXHIBIT_LIST" ("PK_EXHIBITOR") TABLESPACE "USERS";


-- ================================ 2. ata_exhibit（展品明细表） =============================

"CREATE" TABLE "ATA_EXHIBIT" (
    "PK_EXHIBIT"        NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT_LIST"   NVARCHAR2(20)   NOT NULL,
    "EXHIBIT_CODE"      NVARCHAR2(50)   NOT NULL,
    "EXHIBIT_NAME"      NVARCHAR2(200)  NOT NULL,
    "SERIAL_NO"         NVARCHAR2(100),
    "SPECIFICATION"     NVARCHAR2(500),
    "QUANTITY"          NUMBER(28,8),
    "UNIT"              NVARCHAR2(20),
    "VALUE"             NUMBER(28,8),
    "CURRENCY"          NVARCHAR2(10),
    "HS_CODE"           NVARCHAR2(50),
    "EXHIBIT_STATUS"    NUMBER(2)       DEFAULT 0,
    "REMARK"            NVARCHAR2(500),
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_EXHIBIT" PRIMARY KEY ("PK_EXHIBIT")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_EXHIBIT_LIST" FOREIGN KEY ("PK_EXHIBIT_LIST")
        REFERENCES "ATA_EXHIBIT_LIST" ("PK_EXHIBIT_LIST")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_EXHIBIT"              IS '展品明细表';
COMMENT ON COLUMN "ATA_EXHIBIT"."PK_EXHIBIT"      IS '主键';
COMMENT ON COLUMN "ATA_EXHIBIT"."PK_EXHIBIT_LIST" IS '清单主表外键';
COMMENT ON COLUMN "ATA_EXHIBIT"."EXHIBIT_CODE"    IS '展品编码';
COMMENT ON COLUMN "ATA_EXHIBIT"."EXHIBIT_NAME"    IS '展品名称';
COMMENT ON COLUMN "ATA_EXHIBIT"."SERIAL_NO"       IS '序列号（申报必填）';
COMMENT ON COLUMN "ATA_EXHIBIT"."SPECIFICATION"   IS '规格型号';
COMMENT ON COLUMN "ATA_EXHIBIT"."QUANTITY"        IS '数量';
COMMENT ON COLUMN "ATA_EXHIBIT"."UNIT"            IS '单位';
COMMENT ON COLUMN "ATA_EXHIBIT"."VALUE"           IS '价值';
COMMENT ON COLUMN "ATA_EXHIBIT"."CURRENCY"        IS '币种';
COMMENT ON COLUMN "ATA_EXHIBIT"."HS_CODE"         IS 'HS编码';
COMMENT ON COLUMN "ATA_EXHIBIT"."EXHIBIT_STATUS"  IS '展品状态 0待出运/1已出运/2已回运/3差异';
COMMENT ON COLUMN "ATA_EXHIBIT"."REMARK"          IS '备注';
COMMENT ON COLUMN "ATA_EXHIBIT"."PK_GROUP"        IS '集团主键';
COMMENT ON COLUMN "ATA_EXHIBIT"."PK_ORG"          IS '组织主键';
COMMENT ON COLUMN "ATA_EXHIBIT"."CREATOR"         IS '创建人';
COMMENT ON COLUMN "ATA_EXHIBIT"."CREATIONTIME"    IS '创建时间';
COMMENT ON COLUMN "ATA_EXHIBIT"."MODIFIER"        IS '修改人';
COMMENT ON COLUMN "ATA_EXHIBIT"."MODIFIEDTIME"    IS '修改时间';
COMMENT ON COLUMN "ATA_EXHIBIT"."DR"              IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_EXHIBIT"."TS"              IS '时间戳';

"CREATE" UNIQUE INDEX "IDX_ATA_EXHIBIT_CODE"    ON "ATA_EXHIBIT" ("EXHIBIT_CODE")    TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_EXHIBIT_LIST_FK"       ON "ATA_EXHIBIT" ("PK_EXHIBIT_LIST") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_EXHIBIT_STATUS"        ON "ATA_EXHIBIT" ("EXHIBIT_STATUS")  TABLESPACE "USERS";


-- ================================ 3. ata_document（ATA单证表） =============================

"CREATE" TABLE "ATA_DOCUMENT" (
    "PK_DOCUMENT"       NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT_LIST"   NVARCHAR2(20)   NOT NULL,
    "DOCUMENT_NO"       NVARCHAR2(50)   NOT NULL,
    "VALID_FROM"        DATE,
    "VALID_TO"          DATE,
    "ISSUING_AUTHORITY" NVARCHAR2(200),
    "ISSUE_DATE"        DATE,
    "GUARANTEE_AMOUNT"  NUMBER(28,8),
    "DOC_STATUS"        NUMBER(2)       DEFAULT 0,
    "REVIEWER"          NVARCHAR2(20),
    "REVIEW_TIME"       DATE,
    "REVIEW_REMARK"     NVARCHAR2(500),
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_DOCUMENT" PRIMARY KEY ("PK_DOCUMENT")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_DOCUMENT_LIST" FOREIGN KEY ("PK_EXHIBIT_LIST")
        REFERENCES "ATA_EXHIBIT_LIST" ("PK_EXHIBIT_LIST")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_DOCUMENT"              IS 'ATA单证表';
COMMENT ON COLUMN "ATA_DOCUMENT"."PK_DOCUMENT"       IS '主键';
COMMENT ON COLUMN "ATA_DOCUMENT"."PK_EXHIBIT_LIST"   IS '清单主表外键';
COMMENT ON COLUMN "ATA_DOCUMENT"."DOCUMENT_NO"       IS 'ATA单证册号';
COMMENT ON COLUMN "ATA_DOCUMENT"."VALID_FROM"        IS '有效期起';
COMMENT ON COLUMN "ATA_DOCUMENT"."VALID_TO"          IS '有效期止（<30天提示延期）';
COMMENT ON COLUMN "ATA_DOCUMENT"."ISSUING_AUTHORITY" IS '签发机关';
COMMENT ON COLUMN "ATA_DOCUMENT"."ISSUE_DATE"        IS '签发日期';
COMMENT ON COLUMN "ATA_DOCUMENT"."GUARANTEE_AMOUNT"  IS '担保金额';
COMMENT ON COLUMN "ATA_DOCUMENT"."DOC_STATUS"        IS '单证状态 0待审核/1通过/2需延期/3过期';
COMMENT ON COLUMN "ATA_DOCUMENT"."REVIEWER"          IS '审核人';
COMMENT ON COLUMN "ATA_DOCUMENT"."REVIEW_TIME"       IS '审核时间';
COMMENT ON COLUMN "ATA_DOCUMENT"."REVIEW_REMARK"     IS '审核意见';
COMMENT ON COLUMN "ATA_DOCUMENT"."PK_GROUP"          IS '集团主键';
COMMENT ON COLUMN "ATA_DOCUMENT"."PK_ORG"            IS '组织主键';
COMMENT ON COLUMN "ATA_DOCUMENT"."CREATOR"           IS '创建人';
COMMENT ON COLUMN "ATA_DOCUMENT"."CREATIONTIME"      IS '创建时间';
COMMENT ON COLUMN "ATA_DOCUMENT"."MODIFIER"          IS '修改人';
COMMENT ON COLUMN "ATA_DOCUMENT"."MODIFIEDTIME"      IS '修改时间';
COMMENT ON COLUMN "ATA_DOCUMENT"."DR"                IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_DOCUMENT"."TS"                IS '时间戳';

"CREATE" UNIQUE INDEX "IDX_ATA_DOCUMENT_NO"      ON "ATA_DOCUMENT" ("DOCUMENT_NO")     TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DOCUMENT_LIST_FK"       ON "ATA_DOCUMENT" ("PK_EXHIBIT_LIST") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DOCUMENT_STATUS"        ON "ATA_DOCUMENT" ("DOC_STATUS")      TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DOCUMENT_VALID_TO"      ON "ATA_DOCUMENT" ("VALID_TO")        TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DOCUMENT_ISSUE_DATE"    ON "ATA_DOCUMENT" ("ISSUE_DATE")      TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DOCUMENT_REVIEW_TIME"   ON "ATA_DOCUMENT" ("REVIEW_TIME")     TABLESPACE "USERS";


-- ================================ 4. ata_shipment（出运登记表） ===========================

"CREATE" TABLE "ATA_SHIPMENT" (
    "PK_SHIPMENT"       NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT_LIST"   NVARCHAR2(20)   NOT NULL,
    "SHIPMENT_NO"       NVARCHAR2(50)   NOT NULL,
    "SHIPMENT_DATE"     DATE,
    "DEPARTURE_PORT"    NVARCHAR2(100),
    "DESTINATION_PORT"  NVARCHAR2(100),
    "CARRIER"           NVARCHAR2(200),
    "WAYBILL_NO"        NVARCHAR2(50),
    "REGISTRANT"        NVARCHAR2(20),
    "SHIPMENT_STATUS"   NUMBER(2)       DEFAULT 0,
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_SHIPMENT" PRIMARY KEY ("PK_SHIPMENT")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_SHIPMENT_LIST" FOREIGN KEY ("PK_EXHIBIT_LIST")
        REFERENCES "ATA_EXHIBIT_LIST" ("PK_EXHIBIT_LIST")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_SHIPMENT"              IS '出运登记表';
COMMENT ON COLUMN "ATA_SHIPMENT"."PK_SHIPMENT"       IS '主键';
COMMENT ON COLUMN "ATA_SHIPMENT"."PK_EXHIBIT_LIST"   IS '清单主表外键';
COMMENT ON COLUMN "ATA_SHIPMENT"."SHIPMENT_NO"       IS '出运单号 SH+yyyyMMdd+序号';
COMMENT ON COLUMN "ATA_SHIPMENT"."SHIPMENT_DATE"     IS '出运日期';
COMMENT ON COLUMN "ATA_SHIPMENT"."DEPARTURE_PORT"    IS '出境口岸';
COMMENT ON COLUMN "ATA_SHIPMENT"."DESTINATION_PORT"  IS '目的口岸';
COMMENT ON COLUMN "ATA_SHIPMENT"."CARRIER"           IS '承运人';
COMMENT ON COLUMN "ATA_SHIPMENT"."WAYBILL_NO"        IS '运单号';
COMMENT ON COLUMN "ATA_SHIPMENT"."REGISTRANT"        IS '登记人';
COMMENT ON COLUMN "ATA_SHIPMENT"."SHIPMENT_STATUS"   IS '出运状态 0待登记/1已出运';
COMMENT ON COLUMN "ATA_SHIPMENT"."PK_GROUP"          IS '集团主键';
COMMENT ON COLUMN "ATA_SHIPMENT"."PK_ORG"            IS '组织主键';
COMMENT ON COLUMN "ATA_SHIPMENT"."CREATOR"           IS '创建人';
COMMENT ON COLUMN "ATA_SHIPMENT"."CREATIONTIME"      IS '创建时间';
COMMENT ON COLUMN "ATA_SHIPMENT"."MODIFIER"          IS '修改人';
COMMENT ON COLUMN "ATA_SHIPMENT"."MODIFIEDTIME"      IS '修改时间';
COMMENT ON COLUMN "ATA_SHIPMENT"."DR"                IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_SHIPMENT"."TS"                IS '时间戳';

"CREATE" UNIQUE INDEX "IDX_ATA_SHIPMENT_NO"        ON "ATA_SHIPMENT" ("SHIPMENT_NO")     TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_SHIPMENT_LIST_FK"         ON "ATA_SHIPMENT" ("PK_EXHIBIT_LIST") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_SHIPMENT_STATUS"          ON "ATA_SHIPMENT" ("SHIPMENT_STATUS") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_SHIPMENT_DATE"            ON "ATA_SHIPMENT" ("SHIPMENT_DATE")   TABLESPACE "USERS";


-- ================================ 5. ata_shipment_detail（出运明细表） =====================

"CREATE" TABLE "ATA_SHIPMENT_DETAIL" (
    "PK_SHIPMENT_DETAIL" NVARCHAR2(20)  NOT NULL,
    "PK_SHIPMENT"        NVARCHAR2(20)  NOT NULL,
    "PK_EXHIBIT"         NVARCHAR2(20)  NOT NULL,
    "EXHIBIT_CODE"       NVARCHAR2(50),
    "EXHIBIT_NAME"       NVARCHAR2(200),
    "SHIPMENT_QTY"       NUMBER(28,8)   NOT NULL,
    "SERIAL_VERIFIED"    NUMBER(2)      DEFAULT 1,
    "REMARK"             NVARCHAR2(500),
    "PK_GROUP"           NVARCHAR2(20)   NOT NULL,
    "PK_ORG"             NVARCHAR2(20)   NOT NULL,
    "CREATOR"            NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"       DATE            NOT NULL,
    "MODIFIER"           NVARCHAR2(20),
    "MODIFIEDTIME"       DATE,
    "DR"                 NUMBER(2)       DEFAULT 0,
    "TS"                 NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_SHIPMENT_DETAIL" PRIMARY KEY ("PK_SHIPMENT_DETAIL")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_SHIPMENT_DETAIL_SHIP" FOREIGN KEY ("PK_SHIPMENT")
        REFERENCES "ATA_SHIPMENT" ("PK_SHIPMENT"),
    CONSTRAINT "FK_ATA_SHIPMENT_DETAIL_EXH" FOREIGN KEY ("PK_EXHIBIT")
        REFERENCES "ATA_EXHIBIT" ("PK_EXHIBIT")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_SHIPMENT_DETAIL"              IS '出运明细表';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."PK_SHIPMENT_DETAIL" IS '主键';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."PK_SHIPMENT"        IS '出运登记表外键';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."PK_EXHIBIT"         IS '展品明细表外键';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."EXHIBIT_CODE"       IS '展品编码';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."EXHIBIT_NAME"       IS '展品名称';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."SHIPMENT_QTY"       IS '出运数量';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."SERIAL_VERIFIED"    IS '序列号验证 0未/1通过';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."REMARK"             IS '备注';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."PK_GROUP"           IS '集团主键';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."PK_ORG"             IS '组织主键';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."CREATOR"            IS '创建人';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."CREATIONTIME"       IS '创建时间';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."MODIFIER"           IS '修改人';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."MODIFIEDTIME"       IS '修改时间';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."DR"                 IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_SHIPMENT_DETAIL"."TS"                 IS '时间戳';

"CREATE" INDEX "IDX_ATA_SHIPMENT_DTL_SHIP_FK"  ON "ATA_SHIPMENT_DETAIL" ("PK_SHIPMENT") TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_SHIPMENT_DTL_EXH_FK"   ON "ATA_SHIPMENT_DETAIL" ("PK_EXHIBIT")  TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_SHIPMENT_DTL_CODE"     ON "ATA_SHIPMENT_DETAIL" ("EXHIBIT_CODE") TABLESPACE "USERS";


-- ================================ 6. ata_return（回运登记表） ==============================

"CREATE" TABLE "ATA_RETURN" (
    "PK_RETURN"         NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT_LIST"   NVARCHAR2(20)   NOT NULL,
    "PK_SHIPMENT"       NVARCHAR2(20)   NOT NULL,
    "RETURN_NO"         NVARCHAR2(50)   NOT NULL,
    "RETURN_DATE"       DATE,
    "ARRIVAL_PORT"      NVARCHAR2(100),
    "CARRIER"           NVARCHAR2(200),
    "WAYBILL_NO"        NVARCHAR2(50),
    "REGISTRANT"        NVARCHAR2(20),
    "RETURN_STATUS"     NUMBER(2)       DEFAULT 0,
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_RETURN" PRIMARY KEY ("PK_RETURN")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_RETURN_LIST" FOREIGN KEY ("PK_EXHIBIT_LIST")
        REFERENCES "ATA_EXHIBIT_LIST" ("PK_EXHIBIT_LIST"),
    CONSTRAINT "FK_ATA_RETURN_SHIP" FOREIGN KEY ("PK_SHIPMENT")
        REFERENCES "ATA_SHIPMENT" ("PK_SHIPMENT")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_RETURN"              IS '回运登记表';
COMMENT ON COLUMN "ATA_RETURN"."PK_RETURN"         IS '主键';
COMMENT ON COLUMN "ATA_RETURN"."PK_EXHIBIT_LIST"   IS '清单主表外键';
COMMENT ON COLUMN "ATA_RETURN"."PK_SHIPMENT"       IS '出运登记表外键';
COMMENT ON COLUMN "ATA_RETURN"."RETURN_NO"         IS '回运单号 RT+yyyyMMdd+序号';
COMMENT ON COLUMN "ATA_RETURN"."RETURN_DATE"       IS '回运日期';
COMMENT ON COLUMN "ATA_RETURN"."ARRIVAL_PORT"      IS '入境口岸';
COMMENT ON COLUMN "ATA_RETURN"."CARRIER"           IS '承运人';
COMMENT ON COLUMN "ATA_RETURN"."WAYBILL_NO"        IS '运单号';
COMMENT ON COLUMN "ATA_RETURN"."REGISTRANT"        IS '登记人';
COMMENT ON COLUMN "ATA_RETURN"."RETURN_STATUS"     IS '回运状态 0待登记/1已回运/2有差异';
COMMENT ON COLUMN "ATA_RETURN"."PK_GROUP"          IS '集团主键';
COMMENT ON COLUMN "ATA_RETURN"."PK_ORG"            IS '组织主键';
COMMENT ON COLUMN "ATA_RETURN"."CREATOR"           IS '创建人';
COMMENT ON COLUMN "ATA_RETURN"."CREATIONTIME"      IS '创建时间';
COMMENT ON COLUMN "ATA_RETURN"."MODIFIER"          IS '修改人';
COMMENT ON COLUMN "ATA_RETURN"."MODIFIEDTIME"      IS '修改时间';
COMMENT ON COLUMN "ATA_RETURN"."DR"                IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_RETURN"."TS"                IS '时间戳';

"CREATE" UNIQUE INDEX "IDX_ATA_RETURN_NO"          ON "ATA_RETURN" ("RETURN_NO")         TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_RETURN_LIST_FK"           ON "ATA_RETURN" ("PK_EXHIBIT_LIST")   TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_RETURN_SHIP_FK"           ON "ATA_RETURN" ("PK_SHIPMENT")       TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_RETURN_STATUS"            ON "ATA_RETURN" ("RETURN_STATUS")     TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_RETURN_DATE"              ON "ATA_RETURN" ("RETURN_DATE")       TABLESPACE "USERS";


-- ================================ 7. ata_return_detail（回运明细表） =======================

"CREATE" TABLE "ATA_RETURN_DETAIL" (
    "PK_RETURN_DETAIL"  NVARCHAR2(20)   NOT NULL,
    "PK_RETURN"         NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT"        NVARCHAR2(20)   NOT NULL,
    "EXHIBIT_CODE"      NVARCHAR2(50),
    "EXHIBIT_NAME"      NVARCHAR2(200),
    "SHIPMENT_QTY"      NUMBER(28,8)    NOT NULL,
    "RETURN_QTY"        NUMBER(28,8)    NOT NULL,
    "DIFF_QTY"          NUMBER(28,8),
    "REMARK"            NVARCHAR2(500),
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_RETURN_DETAIL" PRIMARY KEY ("PK_RETURN_DETAIL")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_RETURN_DETAIL_RET" FOREIGN KEY ("PK_RETURN")
        REFERENCES "ATA_RETURN" ("PK_RETURN"),
    CONSTRAINT "FK_ATA_RETURN_DETAIL_EXH" FOREIGN KEY ("PK_EXHIBIT")
        REFERENCES "ATA_EXHIBIT" ("PK_EXHIBIT")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_RETURN_DETAIL"              IS '回运明细表';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."PK_RETURN_DETAIL" IS '主键';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."PK_RETURN"        IS '回运登记表外键';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."PK_EXHIBIT"       IS '展品明细表外键';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."EXHIBIT_CODE"     IS '展品编码';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."EXHIBIT_NAME"     IS '展品名称';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."SHIPMENT_QTY"     IS '原出运数量';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."RETURN_QTY"       IS '实际回运数量';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."DIFF_QTY"         IS '差异数量=原出运-实际回运';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."REMARK"           IS '备注';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."PK_GROUP"         IS '集团主键';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."PK_ORG"           IS '组织主键';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."CREATOR"          IS '创建人';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."CREATIONTIME"     IS '创建时间';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."MODIFIER"         IS '修改人';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."MODIFIEDTIME"     IS '修改时间';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."DR"               IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_RETURN_DETAIL"."TS"               IS '时间戳';

"CREATE" INDEX "IDX_ATA_RETURN_DTL_RET_FK"   ON "ATA_RETURN_DETAIL" ("PK_RETURN")   TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_RETURN_DTL_EXH_FK"   ON "ATA_RETURN_DETAIL" ("PK_EXHIBIT")  TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_RETURN_DTL_CODE"     ON "ATA_RETURN_DETAIL" ("EXHIBIT_CODE") TABLESPACE "USERS";


-- ================================ 8. ata_diff（差异处理表） ===============================

"CREATE" TABLE "ATA_DIFF" (
    "PK_DIFF"           NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT_LIST"   NVARCHAR2(20)   NOT NULL,
    "PK_RETURN"         NVARCHAR2(20)   NOT NULL,
    "PK_EXHIBIT"        NVARCHAR2(20)   NOT NULL,
    "DIFF_NO"           NVARCHAR2(50)   NOT NULL,
    "EXHIBIT_CODE"      NVARCHAR2(50),
    "EXHIBIT_NAME"      NVARCHAR2(200),
    "SHIPMENT_QTY"      NUMBER(28,8),
    "RETURN_QTY"        NUMBER(28,8),
    "DIFF_QTY"          NUMBER(28,8)    NOT NULL,
    "DIFF_TYPE"         NUMBER(2)       DEFAULT 5,
    "DIFF_STATUS"       NUMBER(2)       DEFAULT 0,
    "HANDLER"           NVARCHAR2(20),
    "HANDLE_TIME"       DATE,
    "HANDLE_REMARK"     NVARCHAR2(1000),
    "PK_GROUP"          NVARCHAR2(20)   NOT NULL,
    "PK_ORG"            NVARCHAR2(20)   NOT NULL,
    "CREATOR"           NVARCHAR2(20)   NOT NULL,
    "CREATIONTIME"      DATE            NOT NULL,
    "MODIFIER"          NVARCHAR2(20),
    "MODIFIEDTIME"      DATE,
    "DR"                NUMBER(2)       DEFAULT 0,
    "TS"                NVARCHAR2(50)   NOT NULL,
    CONSTRAINT "PK_ATA_DIFF" PRIMARY KEY ("PK_DIFF")
        USING INDEX TABLESPACE "USERS",
    CONSTRAINT "FK_ATA_DIFF_LIST" FOREIGN KEY ("PK_EXHIBIT_LIST")
        REFERENCES "ATA_EXHIBIT_LIST" ("PK_EXHIBIT_LIST"),
    CONSTRAINT "FK_ATA_DIFF_RETURN" FOREIGN KEY ("PK_RETURN")
        REFERENCES "ATA_RETURN" ("PK_RETURN"),
    CONSTRAINT "FK_ATA_DIFF_EXHIBIT" FOREIGN KEY ("PK_EXHIBIT")
        REFERENCES "ATA_EXHIBIT" ("PK_EXHIBIT")
) TABLESPACE "USERS";

COMMENT ON TABLE  "ATA_DIFF"              IS '差异处理表';
COMMENT ON COLUMN "ATA_DIFF"."PK_DIFF"           IS '主键';
COMMENT ON COLUMN "ATA_DIFF"."PK_EXHIBIT_LIST"   IS '清单主表外键';
COMMENT ON COLUMN "ATA_DIFF"."PK_RETURN"         IS '回运登记表外键';
COMMENT ON COLUMN "ATA_DIFF"."PK_EXHIBIT"        IS '展品明细表外键';
COMMENT ON COLUMN "ATA_DIFF"."DIFF_NO"           IS '差异单号 DF+yyyyMMdd+序号';
COMMENT ON COLUMN "ATA_DIFF"."EXHIBIT_CODE"      IS '展品编码';
COMMENT ON COLUMN "ATA_DIFF"."EXHIBIT_NAME"      IS '展品名称';
COMMENT ON COLUMN "ATA_DIFF"."SHIPMENT_QTY"      IS '原出运数量';
COMMENT ON COLUMN "ATA_DIFF"."RETURN_QTY"        IS '实际回运数量';
COMMENT ON COLUMN "ATA_DIFF"."DIFF_QTY"          IS '差异数量';
COMMENT ON COLUMN "ATA_DIFF"."DIFF_TYPE"         IS '差异类型 1丢失/2损坏/3变卖/4赠送/5其他';
COMMENT ON COLUMN "ATA_DIFF"."DIFF_STATUS"       IS '处理状态 0待处理/1处理中/2已处理/3已关闭';
COMMENT ON COLUMN "ATA_DIFF"."HANDLER"           IS '处理人';
COMMENT ON COLUMN "ATA_DIFF"."HANDLE_TIME"       IS '处理时间';
COMMENT ON COLUMN "ATA_DIFF"."HANDLE_REMARK"     IS '处理意见';
COMMENT ON COLUMN "ATA_DIFF"."PK_GROUP"          IS '集团主键';
COMMENT ON COLUMN "ATA_DIFF"."PK_ORG"            IS '组织主键';
COMMENT ON COLUMN "ATA_DIFF"."CREATOR"           IS '创建人';
COMMENT ON COLUMN "ATA_DIFF"."CREATIONTIME"      IS '创建时间';
COMMENT ON COLUMN "ATA_DIFF"."MODIFIER"          IS '修改人';
COMMENT ON COLUMN "ATA_DIFF"."MODIFIEDTIME"      IS '修改时间';
COMMENT ON COLUMN "ATA_DIFF"."DR"                IS '删除标记 0正常/1删除';
COMMENT ON COLUMN "ATA_DIFF"."TS"                IS '时间戳';

"CREATE" UNIQUE INDEX "IDX_ATA_DIFF_NO"          ON "ATA_DIFF" ("DIFF_NO")          TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_LIST_FK"           ON "ATA_DIFF" ("PK_EXHIBIT_LIST")  TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_RETURN_FK"         ON "ATA_DIFF" ("PK_RETURN")        TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_EXHIBIT_FK"        ON "ATA_DIFF" ("PK_EXHIBIT")       TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_TYPE"              ON "ATA_DIFF" ("DIFF_TYPE")        TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_STATUS"            ON "ATA_DIFF" ("DIFF_STATUS")      TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_HANDLE_TIME"       ON "ATA_DIFF" ("HANDLE_TIME")      TABLESPACE "USERS";
"CREATE" INDEX "IDX_ATA_DIFF_CODE"              ON "ATA_DIFF" ("EXHIBIT_CODE")     TABLESPACE "USERS";


-- ===========================================================================================
-- =================================== 脚本统计信息 ==========================================
-- ===========================================================================================
--
-- 表数量统计:
--   1. ATA_EXHIBIT_LIST      - 展品清单主表
--   2. ATA_EXHIBIT           - 展品明细表
--   3. ATA_DOCUMENT          - ATA单证表
--   4. ATA_SHIPMENT          - 出运登记表
--   5. ATA_SHIPMENT_DETAIL   - 出运明细表
--   6. ATA_RETURN            - 回运登记表
--   7. ATA_RETURN_DETAIL     - 回运明细表
--   8. ATA_DIFF              - 差异处理表
--   共计: 8 张表
--
-- 序列数量统计: 8 个
--   SEQ_ATA_EXHIBIT_LIST, SEQ_ATA_EXHIBIT, SEQ_ATA_DOCUMENT, SEQ_ATA_SHIPMENT,
--   SEQ_ATA_SHIPMENT_DETAIL, SEQ_ATA_RETURN, SEQ_ATA_RETURN_DETAIL, SEQ_ATA_DIFF
--   起始值: 100000, 步长: 1
--
-- 索引数量统计: 共计 42 个索引（新增 8 个）
--   UNIQUE索引:  6 个（list_code, exhibit_code, document_no, shipment_no, return_no, diff_no）
--   外键索引:   13 个（所有外键字段）
--   状态索引:    7 个（各表 *_status 字段，含 diff_type）
--   日期索引:    7 个（*_date, *_time 字段）
--   其他索引:    1 个（pk_exhibitor 查询字段）
--
-- 外键约束统计: 共计 13 个外键
--   ATA_EXHIBIT          -> ATA_EXHIBIT_LIST
--   ATA_DOCUMENT         -> ATA_EXHIBIT_LIST
--   ATA_SHIPMENT         -> ATA_EXHIBIT_LIST
--   ATA_SHIPMENT_DETAIL  -> ATA_SHIPMENT, ATA_EXHIBIT
--   ATA_RETURN           -> ATA_EXHIBIT_LIST, ATA_SHIPMENT
--   ATA_RETURN_DETAIL    -> ATA_RETURN, ATA_EXHIBIT
--   ATA_DIFF             -> ATA_EXHIBIT_LIST, ATA_RETURN, ATA_EXHIBIT
--
-- ===========================================================================================
-- ================================== 状态枚举值说明 =========================================
-- ===========================================================================================
--
-- 【清单状态 list_status】:
--   0 = 草稿
--   1 = 提交
--   2 = 审核中
--   3 = 通过
--   4 = 不通过
--
-- 【展品状态 exhibit_status】:
--   0 = 待出运
--   1 = 已出运
--   2 = 已回运
--   3 = 差异
--
-- 【单证状态 doc_status】:
--   0 = 待审核
--   1 = 通过
--   2 = 需延期
--   3 = 过期
--   4 = 已关闭
--
-- 【出运状态 shipment_status】:
--   0 = 待登记
--   1 = 已出运
--
-- 【回运状态 return_status】:
--   0 = 待登记
--   1 = 已回运
--   2 = 有差异
--
-- 【差异类型 diff_type】:
--   1 = 丢失
--   2 = 损坏
--   3 = 变卖
--   4 = 赠送
--   5 = 其他（默认）
--
-- 【处理状态 diff_status】:
--   0 = 待处理
--   1 = 处理中
--   2 = 已处理
--   3 = 已关闭
--
-- 【序列号验证 serial_verified】:
--   0 = 未验证
--   1 = 通过（默认）
--
-- 【删除标记 dr】:
--   0 = 正常（默认）
--   1 = 已删除
--
-- ===========================================================================================
-- ============================ 通用枚举初始化数据（可选）====================================
-- ===========================================================================================
-- 说明: 若项目中存在通用枚举字典表（如 bd_enum / pub_enumdict 等），
--       可参照以下模板插入状态枚举初始化数据。以下为示例模板，
--       请根据实际枚举表结构调整后执行。
--
-- 示例模板（伪代码，需根据实际表结构修改）:
--
-- -- INSERT INTO "BD_ENUM" ("ENUM_TYPE", "ENUM_CODE", "ENUM_NAME", "SORT")
-- -- VALUES ('ATA_LIST_STATUS', '0', '草稿', 1);
-- -- INSERT INTO "BD_ENUM" ("ENUM_TYPE", "ENUM_CODE", "ENUM_NAME", "SORT")
-- -- VALUES ('ATA_LIST_STATUS', '1', '提交', 2);
-- -- INSERT INTO "BD_ENUM" ("ENUM_TYPE", "ENUM_CODE", "ENUM_NAME", "SORT")
-- -- VALUES ('ATA_LIST_STATUS', '2', '审核中', 3);
-- -- INSERT INTO "BD_ENUM" ("ENUM_TYPE", "ENUM_CODE", "ENUM_NAME", "SORT")
-- -- VALUES ('ATA_LIST_STATUS', '3', '通过', 4);
-- -- INSERT INTO "BD_ENUM" ("ENUM_TYPE", "ENUM_CODE", "ENUM_NAME", "SORT")
-- -- VALUES ('ATA_LIST_STATUS', '4', '不通过', 5);
--

-- ===========================================================================================
-- ================================ 功能增强: 新增字段 ALTER ================================
-- ===========================================================================================

-- ====== 1. ata_exhibit（展品明细表）新增字段 ======
ALTER TABLE "ATA_EXHIBIT" ADD "IS_CONTROLLED"    NUMBER(2)      DEFAULT 0;
COMMENT ON COLUMN "ATA_EXHIBIT"."IS_CONTROLLED" IS '管制品标识 0非管制品/1管制品';

ALTER TABLE "ATA_EXHIBIT" ADD "CONTROL_LEVEL"    NVARCHAR2(50);
COMMENT ON COLUMN "ATA_EXHIBIT"."CONTROL_LEVEL" IS '管制级别（HS编码匹配，如武器/爆炸物/药品/木材/贵金属等）';

ALTER TABLE "ATA_EXHIBIT" ADD "SHIPPED_QTY"      NUMBER(28,8)   DEFAULT 0;
COMMENT ON COLUMN "ATA_EXHIBIT"."SHIPPED_QTY" IS '累计已出运数量（拆分出运用，与quantity比较判断是否出运完毕）';

ALTER TABLE "ATA_EXHIBIT" ADD "VALUE_VERIFIED"   NUMBER(2)      DEFAULT 0;
COMMENT ON COLUMN "ATA_EXHIBIT"."VALUE_VERIFIED" IS '估值审核标记 0未审核/1已审核';

-- ====== 2. ata_document（ATA单证表）新增字段 ======
ALTER TABLE "ATA_DOCUMENT" ADD "EXTEND_COUNT"         NUMBER(5)    DEFAULT 0;
COMMENT ON COLUMN "ATA_DOCUMENT"."EXTEND_COUNT" IS '延期次数';

ALTER TABLE "ATA_DOCUMENT" ADD "ORIGINAL_VALID_TO"    DATE;
COMMENT ON COLUMN "ATA_DOCUMENT"."ORIGINAL_VALID_TO" IS '原始有效期（首次签发时的valid_to，延期时保留原始值）';

ALTER TABLE "ATA_DOCUMENT" ADD "RETURN_DEADLINE_BASE" NUMBER(5)    DEFAULT 180;
COMMENT ON COLUMN "ATA_DOCUMENT"."RETURN_DEADLINE_BASE" IS '基础回运期限天数（默认180天，用于差异单return_deadline计算）';

-- ====== 3. ata_diff（差异处理表）新增字段 ======
ALTER TABLE "ATA_DIFF" ADD "RETURN_DEADLINE"   DATE;
COMMENT ON COLUMN "ATA_DIFF"."RETURN_DEADLINE" IS '回运期限（=出运日期 + return_deadline_base，到期前提醒）';

ALTER TABLE "ATA_DIFF" ADD "RETURN_REMINDED"   NUMBER(2)      DEFAULT 0;
COMMENT ON COLUMN "ATA_DIFF"."RETURN_REMINDED" IS '是否已提醒 0未提醒/1已提醒';


-- ===========================================================================================
-- ================================== 功能增强: 新增索引 ====================================
-- ===========================================================================================

-- 展品明细表: 管制品快速查询
CREATE INDEX "IDX_ATA_EXHIBIT_CONTROLLED"   ON "ATA_EXHIBIT" ("IS_CONTROLLED") TABLESPACE "USERS";

-- 展品明细表: HS编码用于管制品匹配查询
CREATE INDEX "IDX_ATA_EXHIBIT_HS_CODE"      ON "ATA_EXHIBIT" ("HS_CODE") TABLESPACE "USERS";

-- 展品明细表: 序列号唯一性校验索引（清单内唯一）
CREATE INDEX "IDX_ATA_EXHIBIT_SERIAL_LIST"  ON "ATA_EXHIBIT" ("PK_EXHIBIT_LIST", "SERIAL_NO") TABLESPACE "USERS";

-- 展品明细表: 拆分出运查询（判断哪些展品还可继续出运）
CREATE INDEX "IDX_ATA_EXHIBIT_SHIPPED"      ON "ATA_EXHIBIT" ("EXHIBIT_STATUS", "SHIPPED_QTY") TABLESPACE "USERS";

-- 单证表: 延期次数统计查询
CREATE INDEX "IDX_ATA_DOCUMENT_EXTEND_CNT"  ON "ATA_DOCUMENT" ("EXTEND_COUNT") TABLESPACE "USERS";

-- 单证表: 原始有效期查询（审计用）
CREATE INDEX "IDX_ATA_DOCUMENT_ORIG_VALID"  ON "ATA_DOCUMENT" ("ORIGINAL_VALID_TO") TABLESPACE "USERS";

-- 差异表: 回运期限查询（到期提醒扫描用）
CREATE INDEX "IDX_ATA_DIFF_RETURN_DDL"      ON "ATA_DIFF" ("RETURN_DEADLINE") TABLESPACE "USERS";

-- 差异表: 提醒状态过滤（未提醒+期限内查询）
CREATE INDEX "IDX_ATA_DIFF_REMINDED"        ON "ATA_DIFF" ("RETURN_REMINDED", "DIFF_STATUS") TABLESPACE "USERS";


-- ===========================================================================================
-- ============================== 新增枚举值说明（管制品管制级别）===========================
-- ===========================================================================================
--
-- 【管制品标识 is_controlled】:
--   0 = 非管制品（默认）
--   1 = 管制品
--
-- 【管制级别 control_level】（基于HS编码前缀自动识别，命中时由系统写入）:
--   武器          -> 93章
--   爆炸物/烟火   -> 36章
--   药品/兽药     -> 30章
--   木材/木制品   -> 44章
--   贵金属/包贵金属 -> 71章
--   宝石/半宝石   -> 71章后段
--   放射性物质     -> 2844, 2845品目
--   医疗设备/仪器 -> 9018~9022品目
--   半导体/电子元件 -> 8541, 8542品目
--   其他管制      -> 其他命中的海关监管编码前缀
--
-- 【估值审核标记 value_verified】:
--   0 = 未审核（默认）
--   1 = 已审核
--
-- 【回运提醒标记 return_reminded】:
--   0 = 未提醒（默认）
--   1 = 已提醒
--

-- ===========================================================================================
-- ================================== 脚本结束 ===============================================
-- ===========================================================================================
