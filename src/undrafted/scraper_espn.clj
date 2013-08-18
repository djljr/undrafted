(ns undrafted.scraper-espn
	(:require [net.cgrand.enlive-html :as html])
  (:require [undrafted.utils :as u]))

(def *injuries-url* "http://espn.go.com/nfl/injuries")
(def *odds-url* "http://espn.go.com/nfl/lines")


(def *player-status-and-comments-selector*
	[:table.tablehead (html/attr-contains :class "player")])

(defn status-and-comments []
	(html/select (u/fetch-url *injuries-url*) *player-status-and-comments-selector*))

(defn extract-status-and-comments [node]
	(let [[player-status comment] node
		  player  (nth (html/select [player-status] [:td]) 0)
		  status  (nth (html/select [player-status] [:td]) 1)
		  date    (nth (html/select [player-status] [:td]) 2)
		  comment (nth (html/select [comment] [:td]) 0)
		  result  (map html/text [player status date comment])]
		(zipmap [:player :status :date :comment] result)))

(def loaded-statuses (status-and-comments))

(defn injuries []
	(map extract-status-and-comments (partition 2 loaded-statuses)))

(defn filter-injuries [name]
	(filter (fn [s] (= name (get s :player))) (injuries)))

(defn team-injuries [team]
	(flatten (map filter-injuries (map (fn [p] (get p :name)) team))))

(defn injury-report [team]
	(map
		(fn [i] (format "%-80s %-15s %-10s\n%s\n\n" (get i :player) (get i :status) (get i :date) (get i :comment)))
		(team-injuries team)))
