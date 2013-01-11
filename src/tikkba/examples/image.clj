(ns tikkba.examples.image
  (:use [analemma svg charts xml]
        [tikkba swing dom])
  (:import (javax.swing JFrame SwingUtilities)))

(defn image-svg
  "Create an SVG representation with an image in it."
  []
  (svg (image "https://raw.github.com/liebke/analemma/master/images/analemma-logo.png"
              :height 142 :width 473)))

(defn create-frame
  [canvas]
  (let [frame (JFrame.)]
    (.add (.getContentPane frame) canvas)
    (.setSize frame 800 200)
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (SwingUtilities/invokeAndWait
     (fn [] (.setVisible frame true)))))

(defn -main
  []
  ;; Converts the SVG representation to a XML Document
  ;; and displays the SVG in a JFrame
  (let [doc (svg-doc (image-svg))
        canvas (jsvgcanvas)]
    (set-document canvas doc)
    (create-frame canvas)))
