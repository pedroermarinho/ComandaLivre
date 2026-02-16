--liquibase formatted sql
--changeset pedroermarinho:V001_insert_tables
-- comment: Insere dados iniciais nas tabelas de status para o schema 'prumodigital'


INSERT INTO project_status (public_id, key, name, description)
VALUES
('01972e26-449d-79a6-a46f-15bb76c4d5f6', 'planning', 'Em Planejamento', 'Fase inicial de concepção, estudo de viabilidade, e elaboração de projetos básicos e executivos.'),
('01972e26-449d-7c59-9aaf-ae69b1b794b9', 'bidding', 'Em Licitação', 'Projeto em processo de licitação para contratação de fornecedores ou da construtora principal.'),
('01972e26-449d-704f-8898-82aad3b55b0d', 'pre_construction', 'Pré-Construção / Mobilização', 'Fase de preparação do canteiro de obras, obtenção de licenças finais, contratações e mobilização de equipe e recursos.'),
('01972e26-449d-76e0-a72f-2ab891aedec0', 'in_progress', 'Em Andamento', 'A construção física do projeto está em plena execução conforme cronograma.'),
('01972e26-449d-7027-9d87-70109c14e350', 'on_hold', 'Em Espera / Suspenso', 'O projeto foi temporariamente interrompido, aguardando resolução de pendências, aprovações ou decisões estratégicas.'),
('01972e26-449d-7a6d-ba0d-ed9fc776cf60', 'completed', 'Concluído', 'Todas as etapas construtivas, instalações e acabamentos foram finalizadas e inspecionadas.'),
('01972e26-449d-7b2e-810e-50bac6aed517', 'post_construction', 'Pós-Construção / Desmobilização', 'Fase de entrega formal da obra, obtenção de "habite-se", manuais do proprietário e desmobilização do canteiro.'),
('01972e26-449d-7b29-83a8-b6ae9f5d6455', 'warranty_period', 'Período de Garantia', 'Projeto entregue e em uso, mas ainda sob o período de garantia construtiva para eventuais reparos.'),
('01972e26-449d-7e5e-a403-967e9e4c7c13', 'canceled', 'Cancelado', 'O projeto foi formalmente cancelado e não será executado ou continuado.'),
('01972e26-449d-7c28-b082-1c71a8bfdc32', 'archived', 'Arquivado', 'Projeto concluído, período de garantia finalizado e toda documentação e processos encerrados. Mantido para histórico.');


INSERT INTO weather_status (public_id, key, name, description, icon)
VALUES
('01972e26-449d-79f8-9f23-036904e783f4', 'sunny_clear', 'Ensolarado / Céu Limpo', 'Condições ideais para a maioria das atividades, sem nuvens ou com poucas nuvens, sem previsão de chuva.', 'sun'),
('01972e26-449d-72d5-a4b4-954a4e609442', 'partly_cloudy', 'Parcialmente Nublado', 'Períodos de sol entremeados com nuvens, baixa probabilidade de chuva, geralmente bom para trabalho.', 'cloud-sun'),
('01972e26-449d-731b-a8ec-1101296945d2', 'cloudy_overcast', 'Nublado / Encoberto', 'Céu majoritariamente ou totalmente coberto por nuvens, pode haver garoa ou chuva fraca intermitente.', 'cloud'),
('01972e26-449d-7b4b-8d26-c29080ca7b00', 'light_rain_drizzle', 'Chuva Leve / Garoa', 'Precipitação leve e fina, pode não impedir todas as atividades externas, mas requer atenção.', 'cloud-rain'),
('01972e26-449d-7dc2-b26e-73c5a391bdd0', 'moderate_rain', 'Chuva Moderada', 'Precipitação constante e de intensidade moderada, provavelmente impactará atividades externas.', 'cloud-showers-heavy'),
('01972e26-449d-75a8-a57d-3a6dbcb45c56', 'heavy_rain_storm', 'Chuva Forte / Tempestade', 'Precipitação intensa, acompanhada ou não de trovoadas. Geralmente impede atividades externas e pode apresentar riscos.', 'poo-storm'),
('01972e26-449d-72ef-9b29-572cbfbeede1', 'strong_wind', 'Vento Forte', 'Condições de vento que podem ser perigosas para trabalhos em altura, operação de guindastes ou manuseio de materiais leves.', 'wind'),
('01972e26-449d-7e86-be54-395dbee59e5e', 'fog_mist', 'Nevoeiro / Neblina', 'Visibilidade significativamente reduzida, podendo afetar a segurança e a logística no canteiro.', 'smog'),
('01972e26-449d-723f-b70d-c6d52d333eb5', 'extreme_heat', 'Calor Extremo', 'Temperaturas muito elevadas que podem causar estresse térmico, exigindo pausas frequentes e hidratação.', 'temperature-high'),
('01972e26-449d-748f-a2c4-ebb5df687c3c', 'extreme_cold_frost', 'Frio Extremo / Geada', 'Temperaturas muito baixas que podem afetar o desempenho de equipamentos, a cura de materiais e o bem-estar dos trabalhadores.', 'snowflake');


INSERT INTO daily_activity_status (public_id, key, name, description)
VALUES
('01972e26-449d-795b-96ba-2d75337e399c', 'not_started', 'Não Iniciada', 'Atividade planejada para o dia, mas ainda não iniciada.'),
('01972e26-449d-7333-861a-a8288ea4fe05', 'in_progress', 'Em Andamento', 'Atividade atualmente em execução pela equipe designada.'),
('01972e26-449d-750f-9056-56213c629399', 'completed_today', 'Concluída Hoje', 'Atividade foi finalizada com sucesso durante o dia corrente.'),
('01972e26-449d-7b0e-9419-6227f118a848', 'partially_completed', 'Parcialmente Concluída', 'Parte da atividade foi executada, mas não foi totalmente finalizada no dia.'),
('01972e26-449d-78e7-9901-f2a7913ba008', 'blocked_impeded', 'Bloqueada / Impedida', 'Atividade não pôde ser iniciada ou continuada devido a um impedimento (ex: falta de material, equipamento quebrado, dependência de outra tarefa).'),
('01972e26-449d-7044-a1a6-3b22bead2e11', 'delayed_by_weather', 'Atrasada (Clima)', 'Atividade impactada ou adiada devido a condições climáticas desfavoráveis.'),
('01972e26-449d-749e-948b-8e394f4959e4', 'delayed_other_reasons', 'Atrasada (Outros Motivos)', 'Atividade atrasada por motivos não relacionados ao clima (ex: falta de mão de obra, problemas técnicos).'),
('01972e26-449d-7e49-82c1-748e96df4b33', 'rework_required', 'Necessita Retrabalho', 'Atividade executada, mas identificada com falhas ou necessidade de correções.'),
('01972e26-449d-7849-8db7-d3b65a780260', 'pending_inspection', 'Aguardando Inspeção', 'Atividade concluída pela equipe de execução, mas aguardando inspeção de qualidade ou aprovação.'),
('01972e26-449d-7849-947e-f967d704633d', 'approved_inspected', 'Aprovada / Inspecionada', 'Atividade inspecionada e aprovada, considerada finalizada do ponto de vista da qualidade.'),
('01972e26-449d-7efb-828b-9f2023f79116', 'canceled_activity', 'Atividade Cancelada', 'Atividade que estava planejada mas foi removida do escopo do dia ou do projeto.');

--rollback sqlFile:path=../rollbacks/datasets/V001_insert_tables_undo.sql
