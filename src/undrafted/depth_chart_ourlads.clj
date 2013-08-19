(ns undrafted.depth-chart-ourlads
  (:require [clojure.string :refer [split capitalize join trim]]
            [clojure.tools.cli :refer [cli]]
	          [net.cgrand.enlive-html :as html]
            [undrafted.utils :refer [fetch-url]]))

(defn url [team]
  (str "http://www.ourlads.com/nfldepthcharts/depthchart/" team))

(def depth-chart-selector
	[:table#gvChart :td])

(defn depth-chart-raw [team]
	(html/select (fetch-url (url team)) depth-chart-selector))

(defn filter-headers [team]
  (fn [row] (not= (str "dcsub_" team) (get-in row [:attrs, :class]))))

(defn extract-name [row cnt]
  (->>
   (some-> (drop cnt row) first :content second :content first (split #"[\s,]+"))
   drop-last
   reverse
   (map capitalize)
   (join " ")))

(defn transform-row [row]
  (let [position (some-> row first :content first :content first trim)
        pos1 (extract-name row 2)
        pos2 (extract-name row 4)
        pos3 (extract-name row 6)
        pos4 (extract-name row 8)
        pos5 (extract-name row 10)]
    (list
     {:name pos1 :position position :ord 1}
     {:name pos2 :position position :ord 2}
     {:name pos3 :position position :ord 3}
     {:name pos4 :position position :ord 4}
     {:name pos5 :position position :ord 5})))

(defn depth-chart [team]
  (flatten (map transform-row (partition 11 (filter (filter-headers team) (depth-chart-raw team))))))


