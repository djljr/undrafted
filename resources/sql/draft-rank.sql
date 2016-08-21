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
where id = :id;

-- name: find-player-by-name
select id, name, position, team, bye, d.espn_rank
from player
join espn_data d on p.id = d.player_id
where name = :name;

-- name: create-fantasy-team<!
insert into fantasy_team (name, owner)
values (:name, :owner);

-- name: save-draft-pick<!
insert into draft_pick (player_id, owner_id, round)
values (:player_id, :owner_id, :round);

-- name: save-draft-keeper<!
insert into draft_pick (player_id, owner_id, round, keeper)
values (:player_id, :owner_id, :round, true);

-- name: all-teams
select id, name, owner from fantasy_team;

-- name: find-players-like
select id, name, position, team, bye, d.rank as espn_rank
from player p
join espn_data d on p.id = d.player_id
where name like :query

-- name: draft-results
select t.name as owner, p.name, position, team, bye, round
from draft_pick dp
join player p on dp.player_id = p.id
join fantasy_team t on t.id = dp.owner_id
order by round, t.name;

-- name: undrafted
select name, position, team, bye, d.rank as espn_rank
from player p
join espn_data d on p.id = d.player_id
where not exists (select 1 from draft_pick dp where p.id = dp.player_id)
order by espn_rank
limit :limit;

-- name: undrafted-by-position
select name, position, team, bye, d.rank as espn_rank
from player p
join espn_data d on p.id = d.player_id
where not exists (select 1 from draft_pick dp where p.id = dp.player_id)
and position = :position
order by espn_rank
limit :limit;

-- name: undrafted-by-position-not-bye-week
select name, position, team, bye, d.rank as espn_rank
from player p
join espn_data d on p.id = d.player_id
where not exists (select 1 from draft_pick dp where p.id = dp.player_id)
and position = :position
and bye not in (:bye)
order by espn_rank
limit :limit

-- name: team-roster
select name, position, team, bye, d.rank as espn_rank
from player p
join espn_data d on p.id = d.player_id
join draft_pick dp on dp.player_id = p.id
where dp.owner_id = :owner_id
order by dp.round
