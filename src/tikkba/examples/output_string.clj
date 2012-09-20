(ns tikkba.examples.output-string
  (:use [analemma svg xml]
        [tikkba swing dom]
        [tikkba.utils.dom :only (spit-str)]))

(defn create-svg
  []
  (svg {:width "15cm" :height "15cm" :viewBox "0 0 15cm 15cm"}
        (-> (circle "8cm" "8cm" "6cm")
            (style :stroke "#000000" :fill "none"))))

(defn -main
  []
  (let [doc (svg-doc (create-svg))]
    (print (spit-str doc :indent "yes"))))