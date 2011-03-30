;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns tikkba.core
  (:import org.apache.batik.swing.JSVGCanvas))

(defn invoke-later
  "Schedules the function for a later invocation. Used to modify the DOM tree
   asynchronously and in a thread-safe way."
  [canvas f]
  (let [queue (.. canvas getUpdateManager getUpdateRunnableQueue)]
    (.invokeLater queue f)))

(defn invoke-and-wait
  "Schedules the function for an invocation and waits for it to be executed. Used to modify the DOM tree
   in a thread-safe way. This function must NOT be called from the Swing thread."
  [canvas f]
  (let [queue (.. canvas getUpdateManager getUpdateRunnableQueue)]
    (.invokeAndWait queue f)))

(defmacro do-batik [canvas & body]
  "Schedules the block of code for a later invocation. Used to modify the DOM tree
   asynchronously and in a thread-safe way."
  `(let [canvas# ~canvas]
     (invoke-later canvas# (fn [] ~@body))))

(defmacro do-batik-and-wait [canvas & body]
  "Schedules the block of code  for an invocation and waits for it to be executed. 
   Used to modify the DOM tree in a thread-safe way. 
   This function must NOT be called from the Swing thread."
  `(let [canvas# ~canvas]
     (invoke-and-wait canvas# (fn [] ~@body))))
