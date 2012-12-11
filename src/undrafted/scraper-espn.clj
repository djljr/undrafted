(ns undrafted.scraper-espn
	(:require [net.cgrand.enlive-html :as html]))

(def *injuries-url* "http://espn.go.com/nfl/injuries")

(defn fetch-url [url]
	(html/html-resource (java.net.URL. url)))

(def *player-status-and-comments-selector* 
	[:table.tablehead])

(def *player-status-date-selector* 
	(html/select (fetch-url *injuries-url*) [:table.tablehead (html/nth-child 2 3)]))

(def *player-comment-selector*
	(html/select (fetch-url *injuries-url*) [:table.tablehead (html/nth-child 2 4)]))

(defn status-and-comments []
	(html/select (fetch-url *injuries-url*) *player-status-and-comments-selector*))

(defn extract-status-and-comments [node]
	(let [status-and-date (first (html/select [node] *player-status-date-selector*))
		  comment         (first (html/select [node] *player-comment-selector*))
		  result          (map html/text [status-and-date comment])]
		(zipmap [:status-and-date :comment] result)))

;(defn injuries 
;	(map extract-status-and-comments (status-and-comments)))