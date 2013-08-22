(ns undrafted.cbs-top-200
	(:require [net.cgrand.enlive-html :as html]
            [clojure.string :refer [trim join capitalize split]]
            [undrafted.utils :refer [fetch-url]]))

(def url "http://localhost:8001/cbs-top-200.html")

(def selector
	[:table.data :td])

(defn data-raw []
	(html/select (fetch-url url) selector))

(defn transform-row [which? row]
  (let [rank (-> (first row) :content first trim)
        player_raw (some-> (second row)
                       :content second
                       :content first
                       (split #"[\s,]+"))
        player (some->> player_raw
                    reverse
                    (map capitalize)
                    (join " ")
                    (filter (partial not= \.))
                    (apply str))]
    {:cbs which?
     :rank rank
     :name player}))

(defn top-200 []
  (let [data-raw (data-raw)
        one (map (partial transform-row "cbs1")
                 (->> data-raw
                      (drop 1)
                      (partition 2)
                      (take 200)))
        two (map (partial transform-row "cbs2")
                 (->> data-raw
                      (drop 402)
                      (partition 2)
                      (take 200)))
        three (map (partial transform-row "cbs3")
                   (->> data-raw
                        (drop 803)
                        (partition 2)
                        (take 200)))]
    {:cbs1 one :cbs2 two :cbs3 three}))
