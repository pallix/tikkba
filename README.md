# Tikkba

Tikkba is Clojure library for the creation and the dynamic modification
of SVG documents. It wraps the Apache Batik library and provides functions to
create SVG images with the Clojure-based SVG DSL of the Analemma library.


Batik is a Java-based toolkit for applications or applets that want to use
images in the Scalable Vector Graphics (SVG) format for various purposes,
such as display, generation or manipulation.

[Apache Batik](http://xmlgraphics.apache.org/batik/index.html)

[Analemma](http://liebke.github.com/analemma/)

## Usage

### Example 1: displaying a SVG into a Swing JFrame.

     (defn analemma-svg
       "Creates a SVG representation with the Analemma functions"
       []
       (svg
        (apply group
               (-> (text "Analemma")
                   (add-attrs :x 120 :y 60)
                   (style :fill "#000066"
                          :font-family "Garamond"
                          :font-size "75px"
                          :alignment-baseline :middle))
               (for [[x y] analemma-data]
                 (circle (translate-value x -30 5 0 125)
                         (translate-value y -25 30 125 0)
                         2 :fill "#000066")))))
     
     (defn -main
       []
       ;; Converts the SVG representation to a XML Document
       ;; and displays the SVG in a JFrame
       (let [doc (svg-doc (analemma-svg))
             canvas (jsvgcanvas)
             frame (JFrame.)]
         (set-document canvas doc)
         (.add (.getContentPane frame) canvas)
         (.setSize frame 800 600)
         (.setSize canvas 800 600)
         (.setVisible frame true)))
     
The full example is available in the test directory.

## License

Copyright (C) 2010 Fraunhofer Gesellschaft

Distributed under the Eclipse Public License, the same as Clojure.
