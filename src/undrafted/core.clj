(ns undrafted.core
  (:gen-class :main true)
	(:require [undrafted.depth-chart-ourlads :refer [depth-chart]]
            [clojure.string :refer [join]]))

(def teams ["ARZ" "ATL" "BAL" "BUF" "CAR" "CHI" "CIN" "CLE" "DAL" "DEN" "DET" "GB" "HOU" "IND" "JAX" "KC" "MIA" "MIN" "NE" "NO" "NYG" "NYJ" "OAK" "PHI" "PIT" "SD" "SF" "SEA" "STL" "TB" "TEN" "WAS"])

(defn filter-blank [p]
  (not (empty? (:name p))))

(defn pretty [team]
  (fn [p] (format "%s,%s,%s,%s" team (:position p) (:name p) (:ord p))))

(defn scrape-depth [team]
  (join "\n" (map (pretty team) (filter filter-blank (depth-chart team)))))

(defn -main []
  (doseq [r (map scrape-depth teams)]
    (println r)))
