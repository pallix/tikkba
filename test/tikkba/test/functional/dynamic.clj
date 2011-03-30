(ns tikkba.test.functional.dynamic
  (:use [analemma svg charts xml]
        [tikkba swing dom core]
        tikkba.utils.xml)
  (:require [tikkba.utils.dom :as dom])
  (:import javax.swing.JFrame))

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

(defn random-color
  []
  (letfn [(color
           []
           (Integer/toHexString (rand-int 16)))]
    (apply format "#%s%s%s%s%s%s" (repeatedly 6 color))))

(defn click-listener
  [event canvas doc]
  (let [rect1 (dom/element-by-id doc "rect1")
        x (Integer/parseInt (dom/attr rect1 :x))]
    ;; changes rectangle position and color
    (do-batik
     canvas
     (dom/add-attrs rect1 {:style (style-str :fill (random-color))
                           :x (+ x 10)}))))

(defn -main
  []
  ;; Converts the SVG representation to a XML Document
  ;; and displays the SVG in a JFrame
  (let [doc (svg-doc (create-svg))
        rect (dom/element-by-id doc "rect0") 
        canvas (jsvgcanvas)
        frame (JFrame.)]
    (dom/add-event-listener rect "click" click-listener canvas doc)
    (set-document canvas doc)
    (.add (.getContentPane frame) canvas)
    (.setSize frame 800 600)
    (.setSize canvas 800 600)
    (.setVisible frame true)))

