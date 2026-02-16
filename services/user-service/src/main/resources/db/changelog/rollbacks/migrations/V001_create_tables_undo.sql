-- Rollback for v001_create_tables.sql
DROP TABLE IF EXISTS public.group_feature_permissions;
DROP TABLE IF EXISTS public.user_feature_groups;
DROP TABLE IF EXISTS public.feature_groups;
DROP TABLE IF EXISTS public.features_catalog;
DROP TABLE IF EXISTS public.notifications;
DROP TABLE IF EXISTS public.user_addresses;
DROP TABLE IF EXISTS public.users;
DROP TABLE IF EXISTS public.addresses;
DROP TABLE IF EXISTS public.assets;
DROP TABLE IF EXISTS public.event_log;
DROP TABLE IF EXISTS public.feature_flags;
