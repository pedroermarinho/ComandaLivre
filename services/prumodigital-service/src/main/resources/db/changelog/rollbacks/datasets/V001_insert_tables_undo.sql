-- Rollback for datasets/V001_insert_tables.sql
DELETE FROM daily_activity_status WHERE key IN ('not_started', 'in_progress', 'completed_today', 'partially_completed', 'blocked_impeded', 'delayed_by_weather', 'delayed_other_reasons', 'rework_required', 'pending_inspection', 'approved_inspected', 'canceled_activity');
DELETE FROM weather_status WHERE key IN ('sunny_clear', 'partly_cloudy', 'cloudy_overcast', 'light_rain_drizzle', 'moderate_rain', 'heavy_rain_storm', 'strong_wind', 'fog_mist', 'extreme_heat', 'extreme_cold_frost');
DELETE FROM project_status WHERE key IN ('planning', 'bidding', 'pre_construction', 'in_progress', 'on_hold', 'completed', 'post_construction', 'warranty_period', 'canceled', 'archived');
