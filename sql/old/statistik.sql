select top 55 to_meta_id,count(to_meta_id),meta_headline from track_log join meta on track_log.to_meta_id = meta.meta_id where log_datetime >= '2000-07-17' and log_datetime < '2000-07-24' group by to_meta_id, meta.meta_headline order by count(to_meta_id) desc

select cast(cast(log_datetime as int) as datetime),count(cast(cast(log_datetime as int) as datetime)) from main_log where log_datetime >= '2000-07-17' and log_datetime < '2000-07-24' and event like '%fully logged in.' group by cast(cast(log_datetime as int) as datetime) order by cast(cast(log_datetime as int) as datetime)

