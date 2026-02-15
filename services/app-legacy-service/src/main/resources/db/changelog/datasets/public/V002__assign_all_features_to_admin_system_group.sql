-- liquibase formatted sql

-- changeset pedroermarinho:v002

-- comment: Associa todas as features existentes da tabela features_catalog ao grupo admin_system na tabela group_feature_permissions.

INSERT INTO public.group_feature_permissions (feature_group_id, feature_id)
SELECT
    public.feature_groups.id,
    public.features_catalog.id
FROM
    public.features_catalog,
    public.feature_groups
WHERE
    public.feature_groups.group_key = 'admin_system'
    AND NOT EXISTS (
        SELECT 1
        FROM public.group_feature_permissions
        WHERE group_feature_permissions.feature_group_id = public.feature_groups.id
          AND group_feature_permissions.feature_id = public.features_catalog.id
    );

--rollback DELETE FROM public.group_feature_permissions WHERE feature_group_id = (SELECT id FROM public.feature_groups WHERE group_key = 'admin_system');

