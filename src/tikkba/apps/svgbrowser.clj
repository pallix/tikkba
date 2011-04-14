;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "This namespace wraps org.apache.batik.apps.svgbrowser.*
            and provides helper functions"}
  tikkba.apps.svgbrowser
  (:use clojure.pprint)
  (:require [tikkba.utils.dom :as dom])
  (:import (javax.swing.undo AbstractUndoableEdit CompoundEdit)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; helper functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro create-edit
  "Creates an implementation of an AbstractUndoableEdit
   and executes the todo function"
  [todo toundo toredo]
  `(let  [todo# ~todo
          toundo# ~toundo
          toredo# ~toredo]
     (todo#)
     (proxy [AbstractUndoableEdit] []
       (undo
        []
        (toundo#)
        (proxy-super undo))

       (redo
        []
        (toredo#)
        (proxy-super redo)))))

(defn node-removed-edit
  "Performs and reates a node removed edit"
  [oldparent oldsibling node]
  (letfn [(todo
            []
            (dom/remove-child oldparent node))

          (toundo
           []
           (dom/insert-before oldparent node oldsibling))]
    (create-edit todo toundo todo)))

(defn node-inserted-edit
  "Performs and creates a node inserted edit"
  [parent sibling node]
  (letfn [(todo
           []
           (if (nil? sibling)
             (dom/append-child parent node)
             (dom/insert-before parent node sibling)))

          (toundo
           []
           (dom/remove-child parent node))]
    (create-edit todo toundo todo)))

(defn compound-edit
  "Creates a compound edit"
  [& edits]
  (let [compoundedit (CompoundEdit.)]
    (doseq [edit edits]
      (.addEdit compoundedit edit))
    (.end compoundedit)
    compoundedit))

(defn post-edit
  "See javax.swing.undo.UndoableEditSupport.postEdit"
  [undosupport edit]
  (.postEdit undosupport edit))
