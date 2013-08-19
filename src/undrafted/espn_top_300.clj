(ns undrafted.espn-top-300
	(:require [net.cgrand.enlive-html :as html]
            [clojure.string :refer [trim join capitalize split]]
            [undrafted.utils :refer [fetch-url]]))

(def url "http://espn.go.com/fantasy/football/story/_/page/2013preseasonFFLranks250/top-300-position")

(def selector
	[:table :td])

(defn data-raw []
	(html/select (fetch-url url) selector))

(defn transform-row [row]
  (let [rank (-> (nth row 0) :content first trim)
        player (->>
                (-> (nth row 1)
                    :content
                    second
                    :content
                    first
                    trim
                    (split #"\s+"))
                (map capitalize)
                (join " ")
                (filter (partial not= \.))
                (apply str))
        pos-rank (-> (nth row 3) :content first trim)]
    {:rank rank
     :name player
     :pos-rank pos-rank}))

(defn top-300 []
  (map transform-row (partition 5 (data-raw))))
