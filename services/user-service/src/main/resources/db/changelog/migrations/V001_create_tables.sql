--liquibase formatted sql
--changeset pedroermarinho:V001_create_tables

--comment: Criação inicial das tabelas principais no schema public

CREATE TABLE public.feature_flags (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    name VARCHAR(255) NOT NULL,
    description TEXT,
    key_flag VARCHAR(255) UNIQUE NOT NULL ,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE public.feature_flags IS 'Tabela para gerenciamento de feature flags globais do sistema.';
COMMENT ON COLUMN public.feature_flags.name IS 'Nome legível da feature flag.';
COMMENT ON COLUMN public.feature_flags.description IS 'Descrição detalhada do propósito da feature flag.';
COMMENT ON COLUMN public.feature_flags.key_flag IS 'Chave textual única usada programaticamente para verificar o estado da feature.';
COMMENT ON COLUMN public.feature_flags.enabled IS 'Estado da feature flag (TRUE se ativa, FALSE se inativa).';

CREATE INDEX IF NOT EXISTS idx_feature_flags_enabled ON public.feature_flags(enabled);


CREATE TABLE public.event_log (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    event_key VARCHAR(100) NOT NULL,
    event_title VARCHAR(255) NOT NULL,
    event_description TEXT NULL,
    actor_user_sub VARCHAR(255) NULL,
    target_entity_type VARCHAR(100) NULL,
    target_entity_key VARCHAR(255) NULL,
    event_data JSONB NULL,
    tags TEXT[] NULL,
    ip_address VARCHAR(45) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL,
    version INT NOT NULL DEFAULT 1
);

COMMENT ON COLUMN public.event_log.event_key IS 'Chave textual única e padronizada que identifica o tipo do evento (ex: USER_CREATED, PAYMENT_FAILED).';
COMMENT ON COLUMN public.event_log.event_title IS 'Título conciso e legível para humanos descrevendo o evento.';
COMMENT ON COLUMN public.event_log.event_description IS 'Descrição textual mais detalhada do evento, se necessário.';
COMMENT ON COLUMN public.event_log.actor_user_sub IS 'Identificador (sub) do usuário ou sistema que iniciou ou executou a ação que gerou o evento.';
COMMENT ON COLUMN public.event_log.target_entity_type IS 'O tipo da entidade principal que foi o alvo ou objeto da ação do evento (ex: USER, PRODUCT, ORDER).';
COMMENT ON COLUMN public.event_log.target_entity_key IS 'A chave identificadora (ex: ID público, código) da entidade alvo mencionada em target_entity_type.';
COMMENT ON COLUMN public.event_log.event_data IS 'Payload JSON contendo dados estruturados específicos e relevantes para o tipo de evento ocorrido.';
COMMENT ON COLUMN public.event_log.tags IS 'Array de tags ou palavras-chave para categorizar ou agrupar o evento.';
COMMENT ON COLUMN public.event_log.ip_address IS 'Endereço IP associado à origem da ação que gerou o evento, se aplicável.';

CREATE INDEX IF NOT EXISTS idx_event_log_event_key ON public.event_log (event_key);
CREATE INDEX IF NOT EXISTS idx_event_log_actor_user_sub ON public.event_log (actor_user_sub);
CREATE INDEX IF NOT EXISTS idx_event_log_created_at ON public.event_log (created_at DESC);

CREATE TABLE public.assets (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    storage_provider VARCHAR(50) NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    file_extension VARCHAR(20) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    storage_path TEXT NOT NULL,
    tags TEXT[] NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL,
    version INT NOT NULL DEFAULT 1
);

COMMENT ON TABLE public.assets IS 'Armazena metadados de ativos digitais, como imagens, documentos, etc., gerenciados pelo sistema.';
COMMENT ON COLUMN public.assets.public_id IS 'Identificador público único (UUID) do asset, usado para referência externa e como nome base do arquivo no storage.';
COMMENT ON COLUMN public.assets.storage_provider IS 'Identifica o provedor de armazenamento onde o arquivo físico reside (ex: S3_AWS, MINIO_LOCAL).';
COMMENT ON COLUMN public.assets.bucket_name IS 'Nome do bucket no provedor de armazenamento onde o arquivo está localizado.';
COMMENT ON COLUMN public.assets.file_extension IS 'Extensão do arquivo (ex: png, jpg, pdf).';
COMMENT ON COLUMN public.assets.file_size_bytes IS 'Tamanho do arquivo em bytes.';
COMMENT ON COLUMN public.assets.storage_path IS 'Caminho do diretório (ou prefixo da chave) dentro do bucket onde o arquivo está localizado.';
COMMENT ON COLUMN public.assets.tags IS 'Array de tags (palavras-chave) associadas ao asset para facilitar buscas e categorização.';

CREATE TABLE public.addresses (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    street VARCHAR (255) NOT NULL,
    number VARCHAR(20)  NOT NULL,
    complement VARCHAR(100) NULL,
    zip_code VARCHAR(10)  NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    neighborhood VARCHAR(255) NOT NULL,
    latitude NUMERIC(10, 8) NULL,
    longitude NUMERIC(11, 8) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE public.addresses IS 'Armazena informações de endereçamento físico.';
COMMENT ON COLUMN public.addresses.street IS 'Nome da rua/logradouro.';
COMMENT ON COLUMN public.addresses.number IS 'Número do imóvel no logradouro.';
COMMENT ON COLUMN public.addresses.complement IS 'Complemento do endereço (ex: Apt 101, Bloco B).';
COMMENT ON COLUMN public.addresses.neighborhood IS 'Bairro.';
COMMENT ON COLUMN public.addresses.city IS 'Cidade.';
COMMENT ON COLUMN public.addresses.state IS 'Estado ou Unidade Federativa (UF).';
COMMENT ON COLUMN public.addresses.zip_code IS 'Código de Endereçamento Postal (CEP).';
COMMENT ON COLUMN public.addresses.latitude IS 'Coordenada de latitude do endereço.';
COMMENT ON COLUMN public.addresses.longitude IS 'Coordenada de longitude do endereço.';


CREATE TABLE public.users (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    sub VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    avatar_asset_id INT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    FOREIGN KEY (avatar_asset_id) REFERENCES public.assets(id) ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE public.users IS 'Tabela de usuários principais do sistema.';
COMMENT ON COLUMN public.users.sub IS 'Subject (identificador) único do provedor de autenticação (ex: Firebase UID).';
COMMENT ON COLUMN public.users.name IS 'Nome completo do usuário.';
COMMENT ON COLUMN public.users.cpf IS 'Cadastro de Pessoa Física (CPF) do usuário, único se informado.';
COMMENT ON COLUMN public.users.email IS 'Endereço de e-mail único do usuário.';
COMMENT ON COLUMN public.users.avatar_asset_id IS 'FK para a tabela "assets", referenciando o avatar do usuário.';

CREATE TABLE public.user_addresses (
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    nickname VARCHAR(100) NULL,
    tag VARCHAR(255),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    CONSTRAINT pk_user_addresses PRIMARY KEY (user_id, address_id),
    CONSTRAINT fk_useraddresses_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_useraddresses_address FOREIGN KEY (address_id) REFERENCES public.addresses(id) ON DELETE CASCADE
);

COMMENT ON TABLE public.user_addresses IS 'Tabela de junção que permite a um usuário associar múltiplos endereços, cada um com um apelido e tags personalizadas.';
COMMENT ON COLUMN public.user_addresses.user_id IS 'Referência ao ID do usuário na tabela "users".';
COMMENT ON COLUMN public.user_addresses.address_id IS 'Referência ao ID do endereço na tabela "addresses".';
COMMENT ON COLUMN public.user_addresses.nickname IS 'Apelido dado pelo usuário para este endereço específico (ex: "Casa", "Escritório").';
COMMENT ON COLUMN public.user_addresses.tag IS 'Tag textual para categorizar ou identificar o endereço (ex: "home").';
COMMENT ON COLUMN public.user_addresses.is_default IS 'Indica se este é o endereço principal ou padrão para o usuário (TRUE para padrão, FALSE caso contrário).';


CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON public.user_addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_address_id ON public.user_addresses(address_id);

CREATE TABLE public.notifications (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    user_id INT,
    event_key VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE NULL,
    action JSONB,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

COMMENT ON TABLE public.notifications IS 'Armazena notificações para usuários sobre eventos no sistema.';
COMMENT ON COLUMN public.notifications.user_id IS 'ID do usuário destinatário da notificação. NULL se for uma notificação de sistema geral.';
COMMENT ON COLUMN public.notifications.event_key IS 'Chave do evento que originou esta notificação (para rastreabilidade e agrupamento).';
COMMENT ON COLUMN public.notifications.title IS 'Título breve da notificação.';
COMMENT ON COLUMN public.notifications.message IS 'Conteúdo/mensagem principal da notificação.';
COMMENT ON COLUMN public.notifications.read_at IS 'Timestamp de quando a notificação foi marcada como lida pelo usuário. Nulo se não lida.';
COMMENT ON COLUMN public.notifications.action IS 'Ação ao clicar na notificação.';

CREATE INDEX IF NOT EXISTS idx_notifications_user_id_read_at ON public.notifications (user_id, read_at);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON public.notifications (user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_event_key ON public.notifications (event_key);

-- Tabela para catalogar as features/recursos que podem ser atribuídos
CREATE TABLE public.features_catalog (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    feature_key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE public.features_catalog IS 'Catálogo de features/recursos granulares que podem ser atribuídos a grupos de usuários.';
COMMENT ON COLUMN public.features_catalog.feature_key IS 'Chave textual única para identificar programaticamente a feature.';
COMMENT ON COLUMN public.features_catalog.name IS 'Nome legível para humanos da feature.';
COMMENT ON COLUMN public.features_catalog.description IS 'Descrição detalhada da feature e seus benefícios.';

CREATE TABLE public.feature_groups (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,

    group_key VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1
);

COMMENT ON TABLE public.feature_groups IS 'Grupos de acesso que agregam um conjunto de permissões de features .';
COMMENT ON COLUMN public.feature_groups.group_key IS 'Chave textual única para identificar programaticamente o grupo.';
COMMENT ON COLUMN public.feature_groups.name IS 'Nome legível para humanos do grupo de features.';


-- Tabela de junção para associar usuários a um ou mais grupos de features
CREATE TABLE public.user_feature_groups (
    user_id INT NOT NULL,
    feature_group_id INT NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    notes TEXT NULL,


    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    PRIMARY KEY (user_id, feature_group_id),
    CONSTRAINT fk_ufg_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ufg_feature_group FOREIGN KEY (feature_group_id) REFERENCES public.feature_groups(id) ON DELETE CASCADE
);

COMMENT ON TABLE public.user_feature_groups IS 'Associa usuários a grupos de features, definindo o estado e o ciclo de vida dessa associação.';
COMMENT ON COLUMN public.user_feature_groups.is_active IS 'Indica se o acesso do usuário a este grupo está atualmente ativo (TRUE) ou inativo (FALSE).';
COMMENT ON COLUMN public.user_feature_groups.assigned_at IS 'Timestamp de quando o usuário foi inicialmente concedido acesso a este grupo de features.';
COMMENT ON COLUMN public.user_feature_groups.expires_at IS 'Timestamp opcional indicando quando o acesso do usuário a este grupo de features expira automaticamente.';
COMMENT ON COLUMN public.user_feature_groups.notes IS 'Observações ou justificativas administrativas para esta associação específica ou suas alterações de estado.';

CREATE INDEX IF NOT EXISTS idx_user_feature_groups_user_id_on_user_feature_groups ON public.user_feature_groups(user_id);
CREATE INDEX IF NOT EXISTS idx_user_feature_groups_feature_group_id_on_user_feature_groups ON public.user_feature_groups(feature_group_id);

-- Tabela de junção para definir quais features pertencem a quais grupos
CREATE TABLE public.group_feature_permissions (
    feature_group_id INT NOT NULL,
    feature_id INT NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version INT DEFAULT 1,

    PRIMARY KEY (feature_group_id, feature_id),
    CONSTRAINT fk_gfp_feature_group FOREIGN KEY (feature_group_id) REFERENCES public.feature_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_gfp_feature FOREIGN KEY (feature_id) REFERENCES public.features_catalog(id) ON DELETE CASCADE
);

COMMENT ON TABLE public.group_feature_permissions IS 'Define quais features do catálogo estão habilitadas para cada grupo de features.';
COMMENT ON COLUMN public.group_feature_permissions.is_enabled IS 'Indica se esta feature está atualmente ativa para este grupo (permite desativação temporária).';
COMMENT ON COLUMN public.group_feature_permissions.granted_at IS 'Timestamp de quando a feature foi concedida ao grupo.';
COMMENT ON COLUMN public.group_feature_permissions.feature_group_id IS 'Referência ao ID do grupo de features na tabela "feature_groups".';
COMMENT ON COLUMN public.group_feature_permissions.feature_id IS 'Referência ao ID da feature no catálogo de features.';

CREATE INDEX IF NOT EXISTS idx_group_feature_permissions_feature_group_id_on_gfp ON public.group_feature_permissions(feature_group_id);
CREATE INDEX IF NOT EXISTS idx_group_feature_permissions_feature_id_on_gfp ON public.group_feature_permissions(feature_id);

--rollback sqlFile:path=../rollbacks/migrations/V001_create_tables_undo.sql
