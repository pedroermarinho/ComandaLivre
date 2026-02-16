-- Rollback for V002__assign_all_features_to_admin_system_group.sql
DELETE FROM public.group_feature_permissions
WHERE feature_group_id IN (SELECT id FROM public.feature_groups WHERE group_key = 'admin_system');
