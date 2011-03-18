;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "This namespace wraps org.apache.batik.swing.*"}
  tikkba.swing
  (:import org.apache.batik.swing.JSVGCanvas
           org.apache.batik.swing.svg.SVGLoadEventDispatcherAdapter))

(defn jsvgcanvas
  "Creates a new JSVGCanvas. The Document State of the canvas is automatically
   set to ALWAYS_DYNAMIC.
   Returns the canvas."
  []
  (let [canvas (JSVGCanvas. )]
    (.setDocumentState canvas JSVGCanvas/ALWAYS_DYNAMIC)
    canvas))

(defn set-document
  "Set the XML document of the canvas. Returns the canvas."
  [canvas doc]
  (.setDocument canvas doc))

(defn svg-document
  "Returns the SVG document of the canvas."
  [canvas]
  (.getSVGDocument canvas))

(defn update-manager
  "Returns the UpdateManager of the canvas."
  [canvas]
  (.getUpdateManager canvas))

(defn add-svg-load-event-dispatch-started-listener
  "Adds a SVGLoadEventDispatcherListener to the canvas.
   When the DispatchStarted event fires, f will be invoked with the
   event as its first argument followed by args. 
   Returns the listener."
  [canvas f & args]
  (let [listener (proxy [SVGLoadEventDispatcherAdapter] []
                   (svgLoadEventDispatchStarted
                    [event]
                    (apply f event args)))]
   (.addSVGLoadEventDispatcherListener canvas listener)
   listener))
