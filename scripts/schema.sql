----------------------------------------------------
-- USANDO DATABASE
----------------------------------------------------
USE db_workingsafe;
GO

----------------------------------------------------
-- CRIAÇÃO DAS TABELAS (VERSÃO SQL SERVER)
----------------------------------------------------

----------------------------------------------------
-- EMPRESA
----------------------------------------------------
CREATE TABLE T_GS_EMPRESA (
    id_empresa     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    nm_empresa     VARCHAR(150) NOT NULL,
    cnpj           VARCHAR(18),
    email_contato  VARCHAR(150),
    dt_cadastro    DATETIME2,
    CONSTRAINT uk_empresa_nome UNIQUE (nm_empresa)
);
GO

----------------------------------------------------
-- TIME / EQUIPE
----------------------------------------------------
CREATE TABLE T_GS_TIME (
    id_time    BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_empresa BIGINT NOT NULL,
    nm_time    VARCHAR(120) NOT NULL,
    ds_time    VARCHAR(300),
    CONSTRAINT fk_time_empresa
        FOREIGN KEY (id_empresa) REFERENCES T_GS_EMPRESA(id_empresa),
    CONSTRAINT uk_time_empresa_nome UNIQUE (id_empresa, nm_time)
);
GO

----------------------------------------------------
-- PAPEL
----------------------------------------------------
CREATE TABLE T_GS_PAPEL (
    id_papel BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    cd_papel VARCHAR(30) NOT NULL,
    ds_papel VARCHAR(150),
    CONSTRAINT uk_papel_codigo UNIQUE (cd_papel)
);
GO

----------------------------------------------------
-- USUARIO
----------------------------------------------------
CREATE TABLE T_GS_USUARIO (
    id_usuario   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_empresa   BIGINT NOT NULL,
    id_time      BIGINT NULL,
    nm_usuario   VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NOT NULL,
    fl_ativo     CHAR(1),
    fuso_horario VARCHAR(60),
    dt_cadastro  DATETIME2,
    CONSTRAINT fk_usuario_empresa
        FOREIGN KEY (id_empresa) REFERENCES T_GS_EMPRESA(id_empresa),
    CONSTRAINT fk_usuario_time
        FOREIGN KEY (id_time) REFERENCES T_GS_TIME(id_time),
    CONSTRAINT uk_usuario_email UNIQUE (email)
);
GO

----------------------------------------------------
-- USUARIO_PAPEL
----------------------------------------------------
CREATE TABLE T_GS_USUARIO_PAPEL (
    id_usuario_papel BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_usuario       BIGINT NOT NULL,
    id_papel         BIGINT NOT NULL,
    CONSTRAINT fk_up_usuario
        FOREIGN KEY (id_usuario) REFERENCES T_GS_USUARIO(id_usuario),
    CONSTRAINT fk_up_papel
        FOREIGN KEY (id_papel) REFERENCES T_GS_PAPEL(id_papel),
    CONSTRAINT uk_usuario_papel UNIQUE (id_usuario, id_papel)
);
GO

----------------------------------------------------
-- CHECKIN
----------------------------------------------------
CREATE TABLE T_GS_CHECKIN (
    id_checkin        BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_usuario        BIGINT       NOT NULL,
    dt_checkin        DATETIME2    NOT NULL,
    vl_humor          INT          NOT NULL,
    vl_foco           INT          NOT NULL,
    minutos_pausas    INT,
    horas_trabalhadas DECIMAL(10,2),
    ds_observacoes    NVARCHAR(MAX),
    tags              VARCHAR(200),
    origem            VARCHAR(20),
    CONSTRAINT fk_checkin_usuario
        FOREIGN KEY (id_usuario) REFERENCES T_GS_USUARIO(id_usuario)
);
GO

----------------------------------------------------
-- CONFIG_GESTOR
----------------------------------------------------
CREATE TABLE T_GS_CONFIG_GESTOR (
    id_config_gestor BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_empresa       BIGINT NOT NULL,
    id_time          BIGINT NULL,
    limiar_alerta    DECIMAL(10,2) NOT NULL,
    janela_dias      INT NOT NULL,
    fl_anonimizado   CHAR(1) NOT NULL,
    CONSTRAINT fk_cfg_empresa
        FOREIGN KEY (id_empresa) REFERENCES T_GS_EMPRESA(id_empresa),
    CONSTRAINT fk_cfg_time
        FOREIGN KEY (id_time) REFERENCES T_GS_TIME(id_time)
);
GO

----------------------------------------------------
-- AGG_INDICE_SEMANAL
----------------------------------------------------
CREATE TABLE T_GS_AGG_INDICE_SEMANAL (
    id_agg           BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_empresa       BIGINT NOT NULL,
    id_time          BIGINT NULL,
    dt_inicio_semana DATE NOT NULL,
    qtd_usuarios     INT NOT NULL,
    media_indice     DECIMAL(10,2) NOT NULL,
    media_risco      DECIMAL(10,2) NOT NULL,
    dt_geracao       DATETIME2,
    CONSTRAINT fk_agg_empresa
        FOREIGN KEY (id_empresa) REFERENCES T_GS_EMPRESA(id_empresa),
    CONSTRAINT fk_agg_time
        FOREIGN KEY (id_time) REFERENCES T_GS_TIME(id_time)
);
GO

----------------------------------------------------
-- RECOMENDACAO_IA
----------------------------------------------------
CREATE TABLE T_GS_RECOMENDACAO_IA (
    id_recomendacao BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    id_usuario      BIGINT       NOT NULL,
    dt_criacao      DATETIME2    NOT NULL,
    dt_validade     DATE         NOT NULL,
    categoria       VARCHAR(50)  NOT NULL,
    descricao       VARCHAR(1000) NOT NULL,
    origem          VARCHAR(30),
    CONSTRAINT fk_recomendacao_usuario
        FOREIGN KEY (id_usuario) REFERENCES T_GS_USUARIO(id_usuario)
);
GO
