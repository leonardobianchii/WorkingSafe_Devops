----------------------------------------------------
-- INSERTS (VERSÃO SQL SERVER)
----------------------------------------------------

----------------------------------------------------
-- EMPRESAS (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_EMPRESA ON;
INSERT INTO T_GS_EMPRESA (id_empresa, nm_empresa, cnpj, email_contato, dt_cadastro) VALUES
(1, 'TechMind Ltda',      '11.111.111/0001-11', 'contato@techmind.com',      CURRENT_TIMESTAMP),
(2, 'HealthCorp SA',      '22.222.222/0002-22', 'contato@healthcorp.com',    CURRENT_TIMESTAMP),
(3, 'EduFocus Ltda',      '33.333.333/0003-33', 'contato@edufocus.com',      CURRENT_TIMESTAMP),
(4, 'WorkLife Inc',       '44.444.444/0004-44', 'contato@worklife.com',      CURRENT_TIMESTAMP),
(5, 'FutureWork SA',      '55.555.555/0005-55', 'contato@futurework.com',    CURRENT_TIMESTAMP);
SET IDENTITY_INSERT T_GS_EMPRESA OFF;
GO

----------------------------------------------------
-- TIMES (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_TIME ON;
INSERT INTO T_GS_TIME (id_time, id_empresa, nm_time, ds_time) VALUES
(1, 1, 'Time Backend',   'Time responsável por APIs e integrações.'),
(2, 1, 'Time Frontend',  'Time responsável por UI e mobile.'),
(3, 2, 'Time Pessoas',   'Time de RH e cultura.'),
(4, 3, 'Time Produto',   'Time de discovery e roadmap.'),
(5, 4, 'Time Dados',     'Time focado em analytics e BI.');
SET IDENTITY_INSERT T_GS_TIME OFF;
GO

----------------------------------------------------
-- PAPEIS (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_PAPEL ON;
INSERT INTO T_GS_PAPEL (id_papel, cd_papel, ds_papel) VALUES
(1, 'GESTOR',       'Gestor com acesso a dashboards e configurações.'),
(2, 'COLABORADOR',  'Colaborador que realiza check-ins e recebe recomendações.'),
(3, 'ADMIN',        'Admin do sistema.'),
(4, 'HR',           'RH com acesso a relatórios específicos.'),
(5, 'VIEWER',       'Acesso somente leitura a alguns relatórios.');
SET IDENTITY_INSERT T_GS_PAPEL OFF;
GO

----------------------------------------------------
-- USUARIOS (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_USUARIO ON;
INSERT INTO T_GS_USUARIO (
    id_usuario, id_empresa, id_time, nm_usuario, email,
    fl_ativo, fuso_horario, dt_cadastro
) VALUES
(1, 1, 1, 'Ana Gestora',       'ana.gestora@techmind.com',   'S', 'America/Sao_Paulo', CURRENT_TIMESTAMP),
(2, 1, 2, 'Bruno Colaborador', 'bruno.colab@techmind.com',   'S', 'America/Sao_Paulo', CURRENT_TIMESTAMP),
(3, 2, 3, 'Carla Colaboradora','carla.colab@healthcorp.com', 'S', 'America/Sao_Paulo', CURRENT_TIMESTAMP),
(4, 3, 4, 'Diego Gestor',      'diego.gestor@edufocus.com',  'S', 'America/Sao_Paulo', CURRENT_TIMESTAMP),
(5, 4, 5, 'Elisa Colaboradora','elisa.colab@worklife.com',   'S', 'America/Sao_Paulo', CURRENT_TIMESTAMP);
SET IDENTITY_INSERT T_GS_USUARIO OFF;
GO

----------------------------------------------------
-- USUARIO_PAPEL (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_USUARIO_PAPEL ON;
INSERT INTO T_GS_USUARIO_PAPEL (id_usuario_papel, id_usuario, id_papel) VALUES
(1, 1, 1), -- Ana - GESTOR
(2, 2, 2), -- Bruno - COLABORADOR
(3, 3, 2), -- Carla - COLABORADOR
(4, 4, 1), -- Diego - GESTOR
(5, 5, 2); -- Elisa - COLABORADOR
SET IDENTITY_INSERT T_GS_USUARIO_PAPEL OFF;
GO

----------------------------------------------------
-- CHECKINS (5)
----------------------------------------------------
INSERT INTO T_GS_CHECKIN (
    id_usuario, dt_checkin, vl_humor, vl_foco,
    minutos_pausas, horas_trabalhadas, ds_observacoes, tags, origem
) VALUES
(2, CONVERT(datetime2,'2025-11-01 09:00:00'), 4, 4, 30,  8.0, 'Dia produtivo, pequenas reuniões.', 'reunião,backend',  'MOBILE'),
(2, CONVERT(datetime2,'2025-11-02 09:10:00'), 3, 3, 15,  9.0, 'Um pouco cansado, muita call.',     'call,entrega',      'WEB'),
(3, CONVERT(datetime2,'2025-11-01 08:50:00'), 5, 4, 40,  7.5, 'Bem disposto, foco em tarefas.',    'tarefas,focus',     'MOBILE'),
(5, CONVERT(datetime2,'2025-11-01 10:00:00'), 2, 2, 10, 10.0, 'Pressão de prazo, pouco descanso.',  'deadline,stress',   'MOBILE'),
(5, CONVERT(datetime2,'2025-11-02 10:15:00'), 3, 2, 20,  9.5, 'Melhorou um pouco, mas ainda puxado.','ajuste,projeto',  'WEB');
GO

----------------------------------------------------
-- CONFIG_GESTOR (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_CONFIG_GESTOR ON;
INSERT INTO T_GS_CONFIG_GESTOR (
    id_config_gestor, id_empresa, id_time, limiar_alerta, janela_dias, fl_anonimizado
) VALUES
(1, 1, NULL, 0.60, 7, 'S'), -- padrão da empresa TechMind
(2, 1, 1,    0.55, 7, 'S'), -- backend
(3, 1, 2,    0.65, 7, 'S'), -- frontend
(4, 2, NULL, 0.50, 14, 'S'),
(5, 3, 4,    0.70, 7, 'S');
SET IDENTITY_INSERT T_GS_CONFIG_GESTOR OFF;
GO

----------------------------------------------------
-- AGG_INDICE_SEMANAL (5)
----------------------------------------------------
SET IDENTITY_INSERT T_GS_AGG_INDICE_SEMANAL ON;
INSERT INTO T_GS_AGG_INDICE_SEMANAL (
    id_agg, id_empresa, id_time, dt_inicio_semana,
    qtd_usuarios, media_indice, media_risco, dt_geracao
) VALUES
(1, 1, 1, CAST('2025-10-27' AS date), 5, 0.78, 0.22, CURRENT_TIMESTAMP),
(2, 1, 2, CAST('2025-10-27' AS date), 4, 0.70, 0.30, CURRENT_TIMESTAMP),
(3, 2, 3, CAST('2025-10-27' AS date), 6, 0.82, 0.18, CURRENT_TIMESTAMP),
(4, 3, 4, CAST('2025-10-27' AS date), 3, 0.65, 0.35, CURRENT_TIMESTAMP),
(5, 4, 5, CAST('2025-10-27' AS date), 4, 0.60, 0.40, CURRENT_TIMESTAMP);
SET IDENTITY_INSERT T_GS_AGG_INDICE_SEMANAL OFF;
GO

----------------------------------------------------
-- RECOMENDACAO_IA (5)
----------------------------------------------------
INSERT INTO T_GS_RECOMENDACAO_IA (
    id_usuario, dt_criacao, dt_validade, categoria, descricao, origem
) VALUES
(2, CONVERT(datetime2,'2025-11-01 09:00:00'), CAST('2025-11-08' AS date), 'PAUSAS',
 'Tente fazer uma pausa de 5 minutos a cada 60 minutos de foco.', 'CARGA_INICIAL'),
(2, CONVERT(datetime2,'2025-11-02 09:00:00'), CAST('2025-11-09' AS date), 'MINDFULNESS',
 'Faça uma respiração profunda por 2 minutos antes das próximas reuniões.', 'CARGA_INICIAL'),
(3, CONVERT(datetime2,'2025-11-01 09:00:00'), CAST('2025-11-08' AS date), 'ATIVIDADE',
 'Uma caminhada leve de 10 minutos pode ajudar a manter seu nível de energia.', 'CARGA_INICIAL'),
(5, CONVERT(datetime2,'2025-11-01 09:00:00'), CAST('2025-11-08' AS date), 'SONO',
 'Tente deitar 30 minutos mais cedo hoje para compensar o cansaço.', 'CARGA_INICIAL'),
(5, CONVERT(datetime2,'2025-11-02 09:00:00'), CAST('2025-11-09' AS date), 'PAUSAS',
 'Inclua uma pausa maior no meio da tarde para evitar queda de foco.', 'CARGA_INICIAL');
GO
