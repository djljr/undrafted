(ns undrafted.core
  (:gen-class :main true)
	(:require [undrafted.depth-chart-ourlads :refer [depth-chart]]
            [undrafted.scraper-espn :refer [injuries]]
            [undrafted.espn-top-300 :refer [top-300]]
            [undrafted.cbs-top-200 :refer [top-200]]
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

(defn do-top-300-espn []
  (doseq [t (map pretty-top-300 (top-300))]
    (println t)))

(defn pretty-top-200 [p]
  (format "%s\t%s\t%s" (:cbs p) (:rank p) (:name p)))

(defn do-top-200-cbs [which?]
  (doseq [t (map pretty-top-200 (which? (top-200)))]
    (println t)))

(defn -main [& args]
  (let [args (set args)]
        (cond
         (args "depth") (do-depth)
         (args "injury") (do-injury)
         (args "top-espn") (do-top-300-espn)
         (args "top-cbs1") (do-top-200-cbs :cbs1)
         (args "top-cbs2") (do-top-200-cbs :cbs2)
         (args "top-cbs3") (do-top-200-cbs :cbs3))))
