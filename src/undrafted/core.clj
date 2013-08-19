(ns undrafted.core
  (:gen-class :main true)
	(:require [undrafted.depth-chart-ourlads :refer [depth-chart]]
            [undrafted.scraper-espn :refer [injuries]]
            [undrafted.espn-top-300 :refer [top-300]]
            [clojure.string :refer [join escape]]))

(def teams ["ARZ" "ATL" "BAL" "BUF" "CAR" "CHI" "CIN" "CLE" "DAL" "DEN" "DET" "GB" "HOU" "IND" "JAX" "KC" "MIA" "MIN" "NE" "NO" "NYG" "NYJ" "OAK" "PHI" "PIT" "SD" "SF" "SEA" "STL" "TB" "TEN" "WAS"])

(defn filter-blank-name [p]
  (not (empty? (:name p))))



(defn pretty-depth [team]
  (fn [p] (format "%s,%s,%s,%s" team (:position p) (:name p) (:ord p))))

(defn scrape-depth [team]
  (join "\n" (map (pretty-depth team) (filter filter-blank-name (depth-chart team)))))

(defn do-depth []
  (doseq [r (map scrape-depth teams)]
    (println r)))


(defn pretty-injuries [p]
  (format "%s\t%s\t%s\t%s" (:player p) (:status p) (:comment p) (:date p)))

(defn scrape-injury []
  (map pretty-injuries (injuries)))

(defn do-injury []
  (doseq [i (scrape-injury)]
    (println i)))

(defn pretty-top-300 [p]
  (format "%s\t%s\t%s" (:rank p) (:name p) (:pos-rank p)))

(defn do-top-300 []
  (doseq [t (map pretty-top-300 (top-300))]
    (println t)))

(defn -main [& args]
  (let [args (set args)]
        (cond
         (args "depth") (do-depth)
         (args "injury") (do-injury)
         (args "top") (do-top-300))))
