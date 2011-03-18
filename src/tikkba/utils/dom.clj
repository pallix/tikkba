;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "Utilities functions for DOM manipulation."}
  tikkba.utils.dom
  (:use clojure.pprint)
  (:require [analemma.xml :as xml])
  (:import org.w3c.dom.events.EventListener))

;;; wrapper of the DOM API

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

(defn child-nodes
  "See org.w3c.dom.Node.getChildNodes"
  [node]
  (.getChildNodes node))

(defn child-nodes-seq
  "Returns the child nodes of node as a sequence."
  [node]
  (let [nodes (child-nodes node)
        len (.getLength nodes)]
    (map #(.item nodes %) (range len))))

(defn element-by-id
  "See org.w3c.dom.Document.getElementById"
  [doc id]
  (.getElementById doc id))

(defn add-event-listener
  "Adds an EventListener to the EventTarget.
   When the event fires, f will be invoked with the
   event as its first argument followed by args. The
   event is not consumed by the listener.
   Returns the listener."
  [elt type f & args] ()
  (let [listener (reify EventListener
                   (handleEvent
                    [this evt]
                    (apply f evt args)))]
    (.addEventListener elt type listener false)
    listener))

;;; helper functions

(defn attr
  "Returns the attribute value."
  [elt att]
  (attribute elt (name att)))

(defn add-attrs
  "Adds the attributes represented by the map attrs
   to the element elt."
  [elt attrs]
  (doseq [[key value] attrs]
    (set-attribute elt (name key) (str value))))

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
      (add-attrs e attrs)
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
