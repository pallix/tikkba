(ns tikkba.test.functional.writefile
  (:use [analemma svg charts xml]
        [tikkba swing dom]
        tikkba.utils.xml)
  (:require [tikkba.utils.dom :as dom]))

(defn create-svg
  "Creates a SVG representation with the Analemma functions"
  []
  (svg
   (-> (rect 20 30 100 400 :id "rect0")
       (style :fill "white" :stroke "blue" :stroke-width 10))
   (-> (rect 50 250 50 80 :id "rect1")
       (style :fill "white" :stroke "red" :stroke-width 10))
   (-> (text "Click inside the blue rectangle!")
       (add-attrs :x 450 :y 80)
       (style :font-size "20px"))))

(defn -main
  []
  ;; Converts the SVG representation to a XML Document
  ;; and writes it to a file
  (let [doc (svg-doc (create-svg))]
    (dom/spit-xml "/tmp/rectangle.svg" doc
                  :indent "yes"
                  :encoding "UTF8")))
