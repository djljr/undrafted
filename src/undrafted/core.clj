(ns undrafted.core
  (:require [org.httpkit.client :as http]
            [net.cgrand.enlive-html :as html]
            [yesql.core :as sql]))

(def espn-300 "http://www.espn.com/fantasy/football/story/_/id/16287927/2016-fantasy-football-rankings-fantasy-football-player-rankings-top-fantasy-football-players-fantasy-football-draft")

(def espn-injury "http://games.espn.com/ffl/resources/playernews?&injuryMode=true")

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

(defn scrape-espn-injury []
  (let [{:keys [status body error]} @(http/get espn-injury {:as :stream})
        html (html/html-resource body)
        ]
    html))

(defn save-espn-player [player]
  (let [{:keys [id]} (create-player<! player)
        player (assoc player :id id)]
    (insert-espn-data! player)
    player))

(defn teams []
  (let [format-fn (fn [{:keys [id name owner]}] (format "%-10s | %-25s | %-20s" id name owner))]
    (println (format-fn {:id "id" :name "name" :owner "owner"}))
    (println (apply str (take 61 (repeat "-"))))
    (println (clojure.string/join "\n" (map format-fn (all-teams))))))

(defn player [query]
  (find-players-like {:query (str "%" query "%")}))

(defn result-formatter [{:keys [name position team bye espn_rank]}]
  (format "%-25s | %-4s | %-5s | %-2s | %-4s" name position team bye espn_rank))

(defn pprint-result [result]
  (println (result-formatter {:name "name" :position "pos" :team "team" :bye "bye" :espn_rank "rank"}))
  (println (apply str (take 54 (repeat "-"))))
  (println (clojure.string/join "\n" (map result-formatter result)))
  result)

(comment
  (def espn-full (scrape-espn))
  (save-espn-player (first espn-full))
  (def html-resource (scrape-espn-injury))
  (def table-resource  (html/select html-resource [:table :table :tr]))


  (teams)

  (def keepers ["Benjamin Watson" "Ronnie Hillman" "Andre Ellington" "Brandon Marshall" "Mike Wallace" "Colin Kaepernick" "Zach Ertz" "Steven Hauschka" "Mark Ingram"])
  (pprint-result (map (comp first player) keepers))
  
  (pprint-result (undrafted {:limit 10}))

  (pprint-result (undrafted-by-position {:position "QB" :limit 10}))
  (pprint-result (undrafted-by-position {:position "RB" :limit 10}))
  (pprint-result (undrafted-by-position {:position "WR" :limit 10}))
  (pprint-result (undrafted-by-position {:position "TE" :limit 10}))
  (pprint-result (undrafted-by-position {:position "K" :limit 10}))
  (pprint-result (undrafted-by-position {:position "DST" :limit 10}))


  (pprint-result (undrafted-by-position-not-bye-week
                  {:position "QB" :limit 10 :bye [11]}))
  (pprint-result (undrafted-by-position-not-bye-week
                  {:position "RB" :limit 10 :bye [11]}))
  (pprint-result (undrafted-by-position-not-bye-week
                  {:position "WR" :limit 10 :bye [11]}))
  (pprint-result (undrafted-by-position-not-bye-week
                  {:position "TE" :limit 10 :bye [11]}))
  (pprint-result (undrafted-by-position-not-bye-week
                  {:position "K" :limit 10 :bye [11]}))
  (pprint-result (undrafted-by-position-not-bye-week
                  {:position "DST" :limit 10 :bye [11]}))


  (pprint-result (team-roster {:owner_id 6}))

  )
