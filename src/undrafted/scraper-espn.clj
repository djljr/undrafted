(ns undrafted.scraper-espn
	(:require [net.cgrand.enlive-html :as html]))

(def *injuries-url* "http://espn.go.com/nfl/injuries")

(defn fetch-url [url]
	(html/html-resource (java.net.URL. url)))

(def *player-status-and-comments-selector* 
	[:table.tablehead (html/attr-contains :class "player")])

(def *player-status-date-selector* 
	[(html/nth-child 2 3)])

(defn status-and-comments []
	(html/select (fetch-url *injuries-url*) *player-status-and-comments-selector*))

(defn extract-status-and-comments [node]
	(let [[player-status comment] node
		  player  (nth (html/select [player-status] [:td]) 0)
		  status  (nth (html/select [player-status] [:td]) 1)
		  date    (nth (html/select [player-status] [:td]) 2)
		  comment (nth (html/select [comment] [:td]) 0)
		  result  (map html/text [player status date comment])]
		(zipmap [:player :status :date :comment] result)))

(defn injuries []
	(map extract-status-and-comments (partition 2 (status-and-comments))))