-- name: create-player<!
insert into player (name, position, team, bye)
values (:name, :position, :team, :bye)
on conflict (name) do 
update set position = :position, team = :team, bye = :bye
where player.name = :name;

-- name: insert-espn-data!
insert into espn_data (player_id, rank, profile_link)
values (:id, :espn_rank, :profile_link)
on conflict (player_id) do
update set rank = :espn_rank, profile_link = :profile_link
where espn_data.player_id = :id;

-- name: find-player-by-id
select id, name, position, team, bye
from player
where id = :id
