(ns undrafted.scraper-espn
	(:require [net.cgrand.enlive-html :as html]
            [clojure.string :refer [split join capitalize]]
            [undrafted.utils :refer [fetch-url]]))

(def injuries-url "http://espn.go.com/nfl/injuries")

(def player-status-and-comments-selector
	[:table.tablehead (html/attr-contains :class "player")])

(defn status-and-comments []
	(html/select (fetch-url injuries-url) player-status-and-comments-selector))

(defn extract-status-and-comments [node]
  (let [[player-status comment] node
        text-for (fn [block it] (-> block (html/select [:td]) (nth it) html/text))
        player  (->>
                 (-> (text-for player-status 0)
                     (split #",")
                     first
                     (split #"\s+"))
                 (map capitalize)
                 (take 2)
                 (join " ")
                 (filter (partial not= \.))
                 (apply str))
        injury  (text-for player-status 1)
        date    (text-for player-status 2)
        comment (text-for comment 0)
        result  [player injury date comment]]
    (zipmap [:player :status :date :comment] result)))

(defn injuries []
	(map extract-status-and-comments (partition 2 (status-and-comments))))

(defn filter-injuries [name]
	(filter (fn [s] (= name (get s :player))) (injuries)))

(defn team-injuries [team]
	(flatten (map filter-injuries (map (fn [p] (get p :name)) team))))

(defn injury-report [team]
	(map
		(fn [i] (format "%-80s %-15s %-10s\n%s\n\n" (get i :player) (get i :status) (get i :date) (get i :comment)))
		(team-injuries team)))
