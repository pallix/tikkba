;;; Copyright © 2010-2012 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "This namespace wraps org.apache.batik.dom.* 
            and provides utilities functions."}
  tikkba.dom
  (:use analemma.xml)
  (:require [tikkba.utils.dom :as dom])
  (:import org.apache.batik.dom.svg.SVGDOMImplementation))

(def ^{:doc "The SVG namespace URI."} svg-ns SVGDOMImplementation/SVG_NAMESPACE_URI)

(defn dom-implementation
  "Returns the DOM Implementation of the Batik library."
  []
  (SVGDOMImplementation/getDOMImplementation))

(defn svg-doc
  "Converts a XML vector representations to a SVG Document."
  [tag]
  (let [doc (dom/create-document (dom-implementation) svg-ns "svg" nil)
        tree (dom/elements doc svg-ns tag)]
    (dom/append-children (dom/document-element doc) (dom/child-nodes-seq tree))
    (dom/add-map-attrs (dom/document-element doc) (get-attrs tag))
    doc))