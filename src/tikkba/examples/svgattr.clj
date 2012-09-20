(ns tikkba.examples.svgattr
  (:use [analemma svg xml]
        [tikkba swing dom])
  (:require [tikkba.utils.dom :as dom]))

(defn create-svg
  []
  (svg {:width "15cm" :height "15cm" :viewBox "0 0 15cm 15cm"}
        (-> (circle "8cm" "8cm" "6cm")
            (style :stroke "#000000" :fill "none"))))

(defn -main
  []
  (let [doc (svg-doc (create-svg))]
   (dom/spit-xml "/tmp/svgattr.svg" doc
                 :indent "yes"
                 :encoding "UTF8")))