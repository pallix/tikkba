;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "This namespace wraps org.apache.batik.apps.svgbrowser.*
            and provides helper functions"}
  tikkba.apps.svgbrowser
  (:use clojure.pprint)
  (:require [tikkba.utils.dom :as dom])
  (:import (org.apache.batik.apps.svgbrowser
            HistoryBrowserInterface
            HistoryBrowser
            HistoryBrowser$CommandController)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; wrappers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn history-browser-interface
  "Constructor. 

   See org.apache.batik.apps.svgbrowser.HistoryBrowserInterface"
  [command-controller]
  (HistoryBrowserInterface. command-controller))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; helper functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol Cmd
  "A protocol representing a command to execute"
  (execute [this])
  (undo [this])
  (redo [this]))

(defrecord TikkbaCmd
    [toexecute toundo toredo]
    Cmd
    (execute
     [this]
     (toexecute))
    
    (undo
     [this]
     (toundo))

    (redo
     [this]
     (toredo)))

(defprotocol CmdStack
  "A stack of commands that can be undone or redone"
  (add-cmd [this cmd])
  (can-undo? [this])
  (undo-cmd [this])
  (redo-cmd [this])
  (can-redo? [this]))

(defrecord TikkbaCmdStack
    [cmds idx]
  CmdStack

  (add-cmd
   [this cmd]
   (execute cmd)
   (let [stack this
         stack (update-in stack [:idx] inc)
         stack (update-in stack [:cmds] subvec 0 (:idx stack))
         stack (update-in stack [:cmds] conj cmd)]
     stack))

  (can-undo?
   [this]
   (not (neg? idx)))

  (can-redo?
   [this]
   (not= idx (dec (count cmds))))

  (undo-cmd
   [this]
   (if (can-undo? this)
     (let [currentcmd (get cmds idx)]
       (undo currentcmd)
       (update-in this [:idx] dec))
     this))

  (redo-cmd
   [this]
   (if (can-redo? this)
     (let [currentcmd (get cmds idx)]
       (redo currentcmd)
       (update-in this [:idx] inc))
     this)))

(defn cmdstack
  "Creates a new simple command stack"
  []
  (TikkbaCmdStack. [] -1))

(defn node-removed-cmd
  "Creates a node removed command"
  [oldparent oldsibling node]
  (letfn [(toexecute
           []
           (dom/remove-child oldparent node))

          (toundo
           []
           (dom/insert-before oldparent node oldsibling))]
    (TikkbaCmd. toexecute toundo toexecute)))

(defn node-inserted-cmd
  "Creates a node inserted command"
  [parent sibling node]
  (letfn [(toexecute
           []
           (if (nil? sibling)
             (dom/append-child parent node)
             (dom/insert-before parent node sibling)))

          (toundo
           []
           (dom/remove-child parent node))]
    (TikkbaCmd. toexecute toundo toexecute)))

