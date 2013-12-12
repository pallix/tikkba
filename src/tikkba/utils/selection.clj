;;; Copyright © 2013 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "Utilities functions to query the DOM.

The code from this namespace is takken
from http://stackoverflow.com/questions/14407817/queryselector-in-apache-batik"}
  tikkba.utils.selection
  (:import org.apache.batik.css.engine.sac.CSSConditionFactory
           org.apache.batik.css.engine.sac.CSSSelectorFactory
           org.apache.batik.css.parser.Parser
           org.apache.batik.dom.traversal.TraversalSupport
           org.w3c.dom.traversal.NodeFilter))

(def ^:private condition-factory
  (CSSConditionFactory. nil "class" nil "id"))

(defn- parse-selector [selector]
  (let [parser (Parser.)]
    (doto parser
      (.setSelectorFactory CSSSelectorFactory/INSTANCE)
      (.setConditionFactory condition-factory))
    (.parseSelectors parser selector)))

(defn- matches?
  ([selector element] (matches? selector element ""))
  ([selector element pseudo]
     (let [length (.getLength selector)]
       (loop [i 0]
         (if (< i length)
           (if (.. selector (item i) (match element pseudo))
             true
             (recur (inc i)))
           false)))))

(defn selection-seq [root selector]
  (let [selector (parse-selector selector)
        iterator (.createNodeIterator (TraversalSupport.)
                                      (.getOwnerDocument root)
                                      root
                                      NodeFilter/SHOW_ELEMENT
                                      (reify NodeFilter
                                        (acceptNode [_ element]
                                          (if (matches? selector element)
                                            NodeFilter/FILTER_ACCEPT
                                            NodeFilter/FILTER_REJECT)))
                                      false)
        node-seq ((fn step []
                    (lazy-seq
                     (when-let [node (.nextNode iterator)]
                       (cons node (step))))))]
    ;; Iterator always returns the reference node, so match it.
    (when-let [node (first node-seq)]
      (if (matches? selector (first node-seq))
        node-seq
        (next node-seq)))))
