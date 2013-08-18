(ns undrafted.core
	(:require [net.cgrand.enlive-html :as html]))

(def teams ["ARZ" "ATL" "BAL" "BUF" "CAR" "CHI" "CIN" "CLE" "DAL" "DEN" "DET" "GB" "HOU" "IND" "JAX" "KC" "MIA" "MIN" "NE" "NO" "NYG" "NYJ" "OAK" "PHI" "PIT" "SD" "SF" "SEA" "STL" "TB" "TEN" "WAS"])

(defn fetch-url [url]
	(html/html-resource (java.net.URL. url)))

(defn -main [s]
  )