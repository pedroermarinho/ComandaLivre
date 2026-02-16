-- Rollback for v001_insert_tables.sql

-- Deleting from public.feature_groups
DELETE FROM public.feature_groups WHERE public_id IN (
'01972d4c-527e-7a4e-a28f-1c9905b533d5',
'01972d4c-527e-73d4-910c-26d7ac2dc1ea',
'01972d4c-527e-78dd-b05b-c6c880043f37',
'01972d4c-527e-7478-a545-2bdbc468233c',
'01972d4c-527e-7c8c-bf06-49fcaa15bece',
'01972d4c-527e-781e-b73b-cecbbb5fb01b',
'01972d4c-527e-73fa-8013-b8c2f62e2d6d',
'01972d4c-527e-73db-b027-736346fd9c51'
);

-- Deleting from public.features_catalog
DELETE FROM public.features_catalog WHERE public_id IN (
'01972d4c-527e-7d7b-8ac9-73022df5a607',
'01972d4c-527e-78b8-80d9-95a64bb6934d',
'01972d4c-527e-74b3-9450-01eaab5dbbdc',
'01972d4c-527e-7afe-94fe-360044a34336',
'01972d4c-527e-7786-a892-727a2ee432bf',
'01972d4c-527e-7a14-909d-dd66ada407ff',
'01972d4c-527e-792a-9580-a985830b9400',
'01972d4c-527e-7d70-86e9-d8ea5fbf51ee',
'01972d4c-527e-7642-9915-431399933a2b',
'01972d4c-527e-717c-820f-fd30bbf0ab8b'
);

-- Deleting from public.feature_flags
DELETE FROM public.feature_flags WHERE public_id IN (
'01972d4d-f034-72ca-81a6-2accffbffba3',
'01972d4d-f034-7e3c-b0b6-507d1e9ba434',
'01972d4d-f034-753b-8b6b-17a1dce60019',
'01972d4d-f034-7de6-885f-4ce56aed660e',
'01972d4d-f034-733e-8eb0-614a1ae64bc5',
'01972d4d-f034-7ca9-9883-857f3125ace5',
'01972d4d-f034-757f-9918-0344b9421827'
);
