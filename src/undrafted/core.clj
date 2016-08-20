(ns undrafted.core
  (:require [org.httpkit.client :as http]
            [net.cgrand.enlive-html :as html]
            [yesql.core :as sql]))

(def espn-300 "http://www.espn.com/fantasy/football/story/_/id/16287927/2016-fantasy-football-rankings-fantasy-football-player-rankings-top-fantasy-football-players-fantasy-football-draft")

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/undrafted"
              :user "undrafted"
              :password "undrafted"})

(sql/defqueries
  "sql/draft-rank.sql"
  {:connection db-spec})

(defn parse-espn-player [idx [name pos team bye _]]
  (let [full-link (first (html/select name [:a]))
        player-name (html/text full-link)
        player-link (get-in full-link [:attrs :href])]
    {:espn_rank (+ 1 idx)
     :name player-name
     :profile_link player-link
     :position (html/text pos)
     :team (html/text team)
     :bye (Integer/parseInt (html/text bye))}))

(defn scrape-espn []
  (let [{:keys [status body error]} @(http/get espn-300 {:as :stream})
        html (html/html-resource body)
        rankings (drop 3 (html/select html [:table :> :tbody :> :tr]))
        players (map-indexed parse-espn-player (map #(html/select % [:td]) rankings))]
    players))

(defn save-espn-player [player]
  (let [{:keys [id]} (create-player<! player)
        player (assoc player :id id)]
    (insert-espn-data! player)
    player))




(comment
  (def espn-full (scrape-espn))
  (save-espn-player (first espn-full))
  )
