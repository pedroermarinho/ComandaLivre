-- liquibase formatted sql

-- changeset pedroermarinho:v001

-- comment: Criação inicial das tabelas principais no schema prumodigital


CREATE TABLE project_status (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE project_status IS 'Define os possíveis status para um projeto (ex: Em Planejamento, Em Andamento, Concluído, Cancelado).';
COMMENT ON COLUMN project_status.key IS 'Chave textual única para referência programática do status (ex: "planning", "in_progress").';
COMMENT ON COLUMN project_status.name IS 'Nome legível do status do projeto.';
COMMENT ON COLUMN project_status.description IS 'Descrição opcional do status do projeto.';

--rollback DROP TABLE IF EXISTS project_status;



CREATE TABLE weather_status (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NULL,
    icon VARCHAR(100) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE weather_status IS 'Define os possíveis status climáticos para relatórios diários (ex: Ensolarado, Nublado, Chuvoso).';
COMMENT ON COLUMN weather_status.key IS 'Chave textual única para referência programática do status (ex: "sunny", "cloudy").';
COMMENT ON COLUMN weather_status.name IS 'Nome legível do status climático.';
COMMENT ON COLUMN weather_status.description IS 'Descrição opcional do status climático.';
COMMENT ON COLUMN weather_status.icon IS 'ícone para o status climático.';

--rollback DROP TABLE IF EXISTS weather_status;

CREATE TABLE daily_activity_status (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE daily_activity_status IS 'Define os possíveis status para atividades diárias de um projeto (ex: Planejada, Em Andamento, Concluída, Bloqueada).';
COMMENT ON COLUMN daily_activity_status.key IS 'Chave textual única para referência programática do status (ex: "planned", "in_progress").';
COMMENT ON COLUMN daily_activity_status.name IS 'Nome legível do status da atividade.';
COMMENT ON COLUMN daily_activity_status.description IS 'Descrição opcional do status da atividade.';

--rollback DROP TABLE IF EXISTS daily_activity_status;


CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    company_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    address_id INT NULL,
    planned_start_date DATE NULL,
    planned_end_date DATE NULL,
    actual_start_date DATE NULL,
    actual_end_date DATE NULL,
    client_name VARCHAR(255) NULL,
    project_status_id INT NOT NULL,
    budget NUMERIC(15,2) NULL,
    description TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_projects_status FOREIGN KEY (project_status_id) REFERENCES project_status(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_projects_company_code UNIQUE (company_id, code)
);

COMMENT ON TABLE projects IS 'Armazena informações sobre os projetos gerenciados.';
COMMENT ON COLUMN projects.company_id IS 'FK para company.companies(id), identificando a empresa principal responsável ou proprietária do projeto.';
COMMENT ON COLUMN projects.name IS 'Nome do projeto.';
COMMENT ON COLUMN projects.code IS 'Código único do projeto dentro da empresa.';
COMMENT ON COLUMN projects.address_id IS 'FK opcional para public.addresses(id), indicando o local físico principal do projeto.';
COMMENT ON COLUMN projects.planned_start_date IS 'Data de início planejada para o projeto.';
COMMENT ON COLUMN projects.planned_end_date IS 'Data de término planejada para o projeto.';
COMMENT ON COLUMN projects.actual_start_date IS 'Data de início real do projeto.';
COMMENT ON COLUMN projects.actual_end_date IS 'Data de término real do projeto.';
COMMENT ON COLUMN projects.client_name IS 'Nome do cliente para o qual o projeto está sendo executado, se aplicável.';
COMMENT ON COLUMN projects.project_status_id IS 'FK para project_status(id), indicando o status atual do projeto.';
COMMENT ON COLUMN projects.budget IS 'Orçamento estimado ou alocado para o projeto.';
COMMENT ON COLUMN projects.description IS 'Descrição detalhada do escopo e objetivos do projeto.';

CREATE INDEX IF NOT EXISTS idx_projects_company_id ON projects(company_id);
CREATE INDEX IF NOT EXISTS idx_projects_project_status_id ON projects(project_status_id);

--rollback DROP INDEX IF EXISTS idx_projects_project_status_id;
--rollback DROP INDEX IF EXISTS idx_projects_company_id;
--rollback DROP TABLE IF EXISTS projects;

CREATE TABLE employee_project_assignments (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    employee_id INT NOT NULL,
    project_id INT NOT NULL,
    role_in_project_id INT NULL,
    assignment_start_date DATE NOT NULL,
    assignment_end_date DATE NULL,
    is_active_assignment BOOLEAN NOT NULL DEFAULT TRUE,
    is_project_admin BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_epa_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT uq_epa_employee_project_start UNIQUE (employee_id, project_id, assignment_start_date)
);


CREATE TABLE daily_reports (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    project_id INT NOT NULL,
    report_date DATE NOT NULL,
    general_observations TEXT NULL,
    morning_weather_id INT NULL,
    afternoon_weather_id INT NULL,
    work_start_time TIME NULL,
    lunch_start_time TIME NULL,
    lunch_end_time TIME NULL,
    work_end_time TIME NULL,
    reported_by_assignment_id INT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_dr_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE, -- FK corrigida para projects.id
    CONSTRAINT fk_dr_reported_by FOREIGN KEY (reported_by_assignment_id) REFERENCES employee_project_assignments(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_dr_morning_weather FOREIGN KEY (morning_weather_id) REFERENCES weather_status(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_dr_afternoon_weather FOREIGN KEY (afternoon_weather_id) REFERENCES weather_status(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT uq_dr_project_date UNIQUE (project_id, report_date)
);

COMMENT ON TABLE daily_reports IS 'Relatórios diários de progresso e condições de um projeto.';
COMMENT ON COLUMN daily_reports.project_id IS 'FK para projects(id), o projeto ao qual este relatório se refere.';
COMMENT ON COLUMN daily_reports.report_date IS 'Data a que se refere o relatório.';
COMMENT ON COLUMN daily_reports.general_observations IS 'Observações gerais sobre o dia no projeto.';
COMMENT ON COLUMN daily_reports.morning_weather_id IS 'FK para weather_status(id), indicando o clima no período da manhã.';
COMMENT ON COLUMN daily_reports.afternoon_weather_id IS 'FK para weather_status(id), indicando o clima no período da tarde.';
COMMENT ON COLUMN daily_reports.work_start_time IS 'Horário de início das atividades no dia.';
COMMENT ON COLUMN daily_reports.lunch_start_time IS 'Horário de início do intervalo de almoço.';
COMMENT ON COLUMN daily_reports.lunch_end_time IS 'Horário de término do intervalo de almoço.';
COMMENT ON COLUMN daily_reports.work_end_time IS 'Horário de término das atividades no dia.';
COMMENT ON COLUMN daily_reports.reported_by_assignment_id IS 'FK para employee_project_assignments(id), indicando qual alocação de funcionário reportou.';


CREATE INDEX IF NOT EXISTS idx_dr_project_id ON daily_reports(project_id);
CREATE INDEX IF NOT EXISTS idx_dr_reported_by ON daily_reports(reported_by_assignment_id);

--rollback DROP INDEX IF EXISTS idx_dr_project_id;
--rollback DROP INDEX IF EXISTS idx_dr_project_id_date;
--rollback DROP TABLE IF EXISTS daily_reports;


CREATE TABLE daily_attendances (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    daily_report_id INT NOT NULL,
    employee_assignment_id INT NOT NULL,
    present BOOLEAN NOT NULL DEFAULT TRUE,
    arrival_time TIME,
    departure_time TIME,
    attendance_note TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_da_daily_report FOREIGN KEY (daily_report_id) REFERENCES daily_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_da_employee_assignment FOREIGN KEY (employee_assignment_id) REFERENCES employee_project_assignments(id) ON DELETE CASCADE,
    CONSTRAINT uq_da_report_employee_assignment UNIQUE (daily_report_id, employee_assignment_id)
);

COMMENT ON TABLE daily_attendances IS 'Registra a presença e horários de funcionários alocados a projetos em um relatório diário.';
COMMENT ON COLUMN daily_attendances.daily_report_id IS 'FK para daily_reports(id), o relatório diário associado.';
COMMENT ON COLUMN daily_attendances.employee_assignment_id IS 'FK para employee_project_assignments(id), identificando o funcionário e sua alocação.';
COMMENT ON COLUMN daily_attendances.present IS 'Indica se o funcionário esteve presente (TRUE) ou ausente (FALSE).';
COMMENT ON COLUMN daily_attendances.arrival_time IS 'Horário de chegada do funcionário.';
COMMENT ON COLUMN daily_attendances.departure_time IS 'Horário de saída do funcionário.';
COMMENT ON COLUMN daily_attendances.attendance_note IS 'Observações sobre a presença/ausência do funcionário.';

CREATE INDEX IF NOT EXISTS idx_da_daily_report_id ON daily_attendances(daily_report_id);
CREATE INDEX IF NOT EXISTS idx_da_employee_assignment_id ON daily_attendances(employee_assignment_id);

--rollback DROP INDEX IF EXISTS idx_da_employee_assignment_id;
--rollback DROP INDEX IF EXISTS idx_da_daily_report_id;
--rollback DROP TABLE IF EXISTS daily_attendances;

CREATE TABLE daily_activities (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    daily_report_id INT NOT NULL,
    activity_description TEXT NOT NULL,
    status_id INT NOT NULL,
    location_description TEXT NULL,
    responsible_employee_assignment_id INT NULL,


    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT fk_dact_daily_report FOREIGN KEY (daily_report_id) REFERENCES daily_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_dact_status FOREIGN KEY (status_id) REFERENCES daily_activity_status(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_dact_responsible_employee FOREIGN KEY (responsible_employee_assignment_id) REFERENCES employee_project_assignments(id) ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE daily_activities IS 'Descreve as atividades realizadas ou planejadas em um relatório diário de projeto.';
COMMENT ON COLUMN daily_activities.daily_report_id IS 'FK para daily_reports(id), o relatório ao qual esta atividade pertence.';
COMMENT ON COLUMN daily_activities.activity_description IS 'Descrição detalhada da atividade.';
COMMENT ON COLUMN daily_activities.status_id IS 'FK para daily_activity_status(id), o status atual da atividade.';
COMMENT ON COLUMN daily_activities.location_description IS 'Descrição do local específico da atividade, se aplicável (ex: "Bloco A, 3º Andar").';
COMMENT ON COLUMN daily_activities.responsible_employee_assignment_id IS 'FK opcional para employee_project_assignments(id), indicando o funcionário responsável pela atividade.';

CREATE INDEX IF NOT EXISTS idx_dact_daily_report_id ON daily_activities(daily_report_id);
CREATE INDEX IF NOT EXISTS idx_dact_status_id ON daily_activities(status_id);

--rollback DROP INDEX IF EXISTS idx_dact_status_id;
--rollback DROP INDEX IF EXISTS idx_dact_daily_report_id;
--rollback DROP TABLE IF EXISTS daily_activities;

CREATE TABLE activity_attachments (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    daily_activity_id INT NOT NULL,
    asset_id INT NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    FOREIGN KEY (daily_activity_id) REFERENCES daily_activities (id) ON DELETE CASCADE
);

COMMENT ON TABLE activity_attachments IS 'Armazena anexos (fotos, documentos) relacionados a atividades diárias de projetos.';
COMMENT ON COLUMN activity_attachments.daily_activity_id IS 'FK para daily_activities(id), a atividade à qual este anexo pertence.';
COMMENT ON COLUMN activity_attachments.asset_id IS 'FK para public.assets(id), o arquivo anexado.';
COMMENT ON COLUMN activity_attachments.description IS 'Descrição opcional do anexo.';

CREATE INDEX IF NOT EXISTS idx_aa_daily_activity_id ON activity_attachments(daily_activity_id);

--rollback DROP INDEX IF EXISTS idx_aa_daily_activity_id;
--rollback DROP TABLE IF EXISTS activity_attachments;
