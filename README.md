# Tikkba

Tikkba is a Clojure library for the **creation and the dynamic modification
of SVG documents**. It wraps the **Apache Batik library** and provides functions to
create SVG images with the Clojure-based SVG DSL of the Analemma library.


Batik is a Java-based toolkit for applications or applets that want to use
images in the Scalable Vector Graphics (SVG) format for various purposes,
such as display, generation or manipulation.

[Apache Batik](http://xmlgraphics.apache.org/batik/index.html)

[Analemma](http://liebke.github.com/analemma/)

## Installation

The Tikkba library is available on Clojars:

[![Clojars Project](https://img.shields.io/clojars/v/tikkba.svg)](https://clojars.org/tikkba)


## Usage

### Example 1: creating a SVG and displaying it into a Swing JFrame.

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
       (let [doc (svg-doc (analemma-svg))
             canvas (jsvgcanvas)]
         (set-document canvas doc)
         (create-frame canvas)))

[See the full code of this example](https://github.com/pallix/tikkba/blob/master/src/tikkba/examples/analemma.clj)

### Example 2: dynamically modifying a SVG

This example draw two rectangles. When the user clicks on the biggest
rectangle, the color and position of the other rectangle will change.

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
          (dom/add-attrs rect1
                         :style (style-str :fill (random-color))
                         :x (+ x 10)))))

     (defn create-frame
       [canvas]
       (let [frame (JFrame.)]
         (.add (.getContentPane frame) canvas)
         (.setSize frame 800 600)
         (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
         ;; or use the do-swing macro of clojure.contrib.swing-utils:
         (SwingUtilities/invokeAndWait
          (fn [] (.setVisible frame true)))))

     (defn -main
       []
       ;; Converts the SVG representation to a XML Document
       ;; and displays the SVG in a JFrame
       (let [doc (svg-doc (create-svg))
             rect (dom/element-by-id doc "rect0")
             canvas (jsvgcanvas)]
         (dom/add-event-listener rect "click" click-listener canvas doc)
         (set-document canvas doc)
         (create-frame canvas)))

[See the full code of this example](https://github.com/pallix/tikkba/blob/master/src/tikkba/examples/dynamic.clj)

### Example 3: creating a SVG file

     (let [doc (svg-doc (create-svg))]
         (dom/spit-xml "/tmp/rectangle.svg" doc
                       :indent "yes"
                       :encoding "UTF8"))

[See the full code of this example](https://github.com/pallix/tikkba/blob/master/src/tikkba/examples/writefile.clj)

### Example 4: transcode svg image to raster file

Here is a simple example, which shows usage of [Batik's transcoders](http://xmlgraphics.apache.org/batik/using/transcoder.html#howtousetranscoderAPI).

```
(ns tikkba.examples.transcoder
	(:require [tikkba.dom :refer [svg-doc]]
          	 [analemma.svg :refer [svg rect]]
          	 [analemma.xml :as xml]
          	 [tikkba.transcoder :as t])

    (def canvas  (svg-doc
                    (svg
                      (->
                        (rect 10 10 400 50)
                        (xml/add-attrs :fill "red")))))

    ;; using PNG transcoder
    (t/to-png canvas "/var/tmp/test.png")
    (t/to-png canvas
    		  "/var/tmp/temp/test.png"
    		  {:width 410 :height 60})

    ;; using JPEG transcoder
    (t/to-jpeg canvas "/var/temp/test.jpeg")
    (t/to-jpeg canvas
    			"/var/temp/test.jpeg"
    		   {:quality 0.6})

```


__All examples are available in the__ [examples directory](https://github.com/pallix/tikkba/tree/master/src/tikkba/examples/).

You can run the examples with the following command:

    lein run -m <namespace-of-the-example>

For examples:

    lein run -m tikkba.examples.dynamic
    lein run -m tikkba.examples.output-string


## License

Copyright (C) 2010-2012 Fraunhofer Gesellschaft

Distributed under the Eclipse Public License, the same as Clojure.
