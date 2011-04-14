;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "Utilities functions for DOM manipulation."}
  tikkba.utils.dom
  (:use clojure.pprint)
  (:require [analemma.xml :as xml]
            [clojure.java.io :as jio])
  (:import org.w3c.dom.events.EventListener
           java.io.File
           javax.xml.transform.dom.DOMSource
           javax.xml.transform.stream.StreamResult
           javax.xml.transform.TransformerFactory))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; wrapper of the DOM API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn document-element
  "See org.w3c.dom.Document.getDocumentElement"
  [doc]
  (.getDocumentElement doc))

(defn create-document
  "See org.w3c.dom.DOMImplementation.createDocument"
  [domimpl ns name doctype]
  (.createDocument domimpl ns name nil))

(defn set-attribute-ns
  "See org.w3c.dom.Element.setAttributeNS."
  [elt ns name value]
  (.setAttributeNS elt ns name value))

(defn set-attribute
  "See org.w3c.dom.Element.setAttribute."
  [elt name value]
  (.setAttribute elt name value))

(defn attribute
  "See org.w3c.dom.Element.getAttribute."
  [elt name]
  (.getAttribute elt name))

(defn set-text-content
  "See org.w3c.dom.Node.setTextContent."
  [node text-content]
  (.setTextContent node text-content))

(defn create-element-ns
  "See org.w3c.dom.Document.createElementNS."
  [doc ns name]
  (.createElementNS doc ns name))

(defn append-child
  "See org.w3c.dom.Node.appendChild"
  [node child]
  (.appendChild node child))

(defn insert-before
  "See org.w3c.dom.Node.insertBefore"
  [node new-child ref-child]
  (.insertBefore node new-child ref-child))

(defn remove-child
  "See org.w3c.dom.Node.appendChild"
  [node child]
  (.removeChild node child))

(defn child-nodes
  "See org.w3c.dom.Node.getChildNodes"
  [node]
  (.getChildNodes node))

(defn next-sibling
  "See org.w3c.dom.Node.getNextSibling"
  [node]
  (.getNextSibling node))

(defn element-by-id
  "See org.w3c.dom.Document.getElementById"
  [doc id]
  (.getElementById doc id))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; helper functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn add-event-listener
  "Adds an EventListener to the EventTarget.
   When the event fires, f will be invoked with the
   event as its first argument followed by args. The
   event is not consumed by the listener.
   Returns the listener."
  [elt type f & args]
  (let [listener (reify EventListener
                   (handleEvent
                    [this evt]
                    (apply f evt args)))]
    (.addEventListener elt type listener false)
    listener))

(defn child-nodes-seq
  "Returns the child nodes of node as a sequence."
  [node]
  (let [nodes (child-nodes node)
        len (.getLength nodes)]
    (map #(.item nodes %) (range len))))

(defn element-id
  "Returns the element with the given id"
  [doc id]
  (element-by-id doc (name id)))

(defn spit-xml
  "Open f with writer and writes the XML content
   into it, then closes f.

   Options is a suite of key/value and is used for 
   the output properties of the XML transformation.
   Valid options are :indent, :encoding etc. 
   See javax.xml.transform.OutputKeys"
  [f doc & options]
  (with-open [writer (jio/writer f)]
    (let [src (DOMSource. doc)
          result (StreamResult. writer)
          xformer (.newTransformer (TransformerFactory/newInstance))
          options (apply hash-map options)]
      (doseq [[k v] options]
        (.setOutputProperty xformer (name k) v))
      (.transform xformer src result))))

(defn attr
  "Returns the attribute value."
  [elt att]
  (attribute elt (name att)))

(defn add-attrs
  "Adds the attributes to the element elt."
  [elt & attrs]
  (doseq [[key value] (partition 2 attrs)]
    (set-attribute elt (name key) (str value))))

(defn add-map-attrs
  "Adds the attributes represented by a map to the element elt"
  [elt attrs]
  (apply add-attrs elt (flatten (seq attrs))))

(defn append-children
  "Add the children to node."
  [node children]
  (doseq [child children]
    (append-child node child)))

(defn elt [doc ns tag]
  (if (string? tag)
    (throw (IllegalArgumentException. (format "Illegal argument %s" tag)))
    (let [name (xml/get-name tag)
          e (create-element-ns doc ns name)
          attrs (xml/get-attrs tag)]
      (add-map-attrs e attrs)
      e)))

(defn elements-helper
  ([doc ns tag]
     (let [root-elt (elt doc ns tag)
           children (xml/get-content tag)
           children-elts (map #(elt doc ns %) children)]
       (append-children root-elt children-elts)
       (elements-helper doc ns root-elt children children-elts)))
  ([doc ns root-elt queued-tags queued-elts]
     (if (empty? queued-tags)
       root-elt
       (let [[tag & xs] queued-tags
             [e & rst-elts] queued-elts]
         (if (string? tag)
           (recur doc ns root-elt xs rst-elts)
           (let [children (xml/get-content tag)
                 fchild (first children)]
             (if (and (= (count children) 1) (string? fchild))
               (do
                 (set-text-content e fchild)
                 (recur doc
                    ns
                    root-elt
                    (concat xs children)
                    rst-elts))
               (let [children-elts (map #(elt doc ns %) children)]
                 (append-children e children-elts)
                 (recur doc
                        ns
                        root-elt
                        (concat xs children)
                        (concat rst-elts children-elts))))))))))

(defn elements
  "Converts the XML vector representations into XML elements."
  [doc ns tag]
  (elements-helper doc ns tag))
