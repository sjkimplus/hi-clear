-- BATCH_JOB_INSTANCE 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_JOB_INSTANCE (
                                                  JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
                                                  VERSION BIGINT,
                                                  JOB_NAME VARCHAR(100) NOT NULL,
                                                  JOB_KEY VARCHAR(32) NOT NULL,
                                                  CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
) ENGINE=InnoDB;

-- BATCH_JOB_EXECUTION 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION (
                                                   JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                   VERSION BIGINT,
                                                   JOB_INSTANCE_ID BIGINT NOT NULL,
                                                   CREATE_TIME DATETIME(6) NOT NULL,
                                                   START_TIME DATETIME(6) DEFAULT NULL,
                                                   END_TIME DATETIME(6) DEFAULT NULL,
                                                   STATUS VARCHAR(10),
                                                   EXIT_CODE VARCHAR(2500),
                                                   EXIT_MESSAGE VARCHAR(2500),
                                                   LAST_UPDATED DATETIME(6),
                                                   CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
                                                       REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ENGINE=InnoDB;

-- BATCH_JOB_EXECUTION_PARAMS 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_PARAMS (
                                                          JOB_EXECUTION_ID BIGINT NOT NULL,
                                                          PARAMETER_NAME VARCHAR(100) NOT NULL,
                                                          PARAMETER_TYPE VARCHAR(100) NOT NULL,
                                                          PARAMETER_VALUE VARCHAR(2500),
                                                          IDENTIFYING CHAR(1) NOT NULL,
                                                          CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                                              REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- BATCH_STEP_EXECUTION 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION (
                                                    STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                    VERSION BIGINT NOT NULL,
                                                    STEP_NAME VARCHAR(100) NOT NULL,
                                                    JOB_EXECUTION_ID BIGINT NOT NULL,
                                                    CREATE_TIME DATETIME(6) NOT NULL,
                                                    START_TIME DATETIME(6) DEFAULT NULL,
                                                    END_TIME DATETIME(6) DEFAULT NULL,
                                                    STATUS VARCHAR(10),
                                                    COMMIT_COUNT BIGINT,
                                                    READ_COUNT BIGINT,
                                                    FILTER_COUNT BIGINT,
                                                    WRITE_COUNT BIGINT,
                                                    READ_SKIP_COUNT BIGINT,
                                                    WRITE_SKIP_COUNT BIGINT,
                                                    PROCESS_SKIP_COUNT BIGINT,
                                                    ROLLBACK_COUNT BIGINT,
                                                    EXIT_CODE VARCHAR(2500),
                                                    EXIT_MESSAGE VARCHAR(2500),
                                                    LAST_UPDATED DATETIME(6),
                                                    CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                                        REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- BATCH_STEP_EXECUTION_CONTEXT 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT (
                                                            STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                            SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                                            SERIALIZED_CONTEXT TEXT,
                                                            CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
                                                                REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ENGINE=InnoDB;

-- BATCH_JOB_EXECUTION_CONTEXT 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT (
                                                           JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                           SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                                           SERIALIZED_CONTEXT TEXT,
                                                           CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                                               REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- BATCH_STEP_EXECUTION_SEQ 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ (
                                                        ID BIGINT NOT NULL,
                                                        UNIQUE_KEY CHAR(1) NOT NULL,
                                                        CONSTRAINT UNIQUE_KEY_UN UNIQUE (UNIQUE_KEY)
) ENGINE=InnoDB;

-- BATCH_JOB_EXECUTION_SEQ 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ (
                                                       ID BIGINT NOT NULL,
                                                       UNIQUE_KEY CHAR(1) NOT NULL,
                                                       CONSTRAINT UNIQUE_KEY_UN UNIQUE (UNIQUE_KEY)
) ENGINE=InnoDB;

-- BATCH_JOB_SEQ 테이블 생성
CREATE TABLE IF NOT EXISTS BATCH_JOB_SEQ (
                                             ID BIGINT NOT NULL,
                                             UNIQUE_KEY CHAR(1) NOT NULL,
                                             CONSTRAINT UNIQUE_KEY_UN UNIQUE (UNIQUE_KEY)
) ENGINE=InnoDB;

-- 초기값 삽입
INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY)
SELECT 0, '0' FROM dual WHERE NOT EXISTS (SELECT * FROM BATCH_STEP_EXECUTION_SEQ);

INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY)
SELECT 0, '0' FROM dual WHERE NOT EXISTS (SELECT * FROM BATCH_JOB_EXECUTION_SEQ);

INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY)
SELECT 0, '0' FROM dual WHERE NOT EXISTS (SELECT * FROM BATCH_JOB_SEQ);