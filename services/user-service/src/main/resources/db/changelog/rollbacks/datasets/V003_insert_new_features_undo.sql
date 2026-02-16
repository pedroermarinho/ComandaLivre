-- Rollback for V003__insert_new_features.sql
DELETE FROM public.features_catalog WHERE public_id IN (
'019791a9-705b-7774-a359-debbb770699e',
'019791a9-705b-7de6-97b3-85f4ef7698b1',
'019791a9-705b-7dfb-8931-8aed56d46c0e',
'019791a9-705b-788c-9945-c48d78d04021'
);
