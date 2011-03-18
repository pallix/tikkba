;;; Copyright © 2010 Fraunhofer Gesellschaft
;;; Licensed under the EPL V.1.0

(ns ^{:doc "Utilities function to manipulate the XML vector representation"}
  tikkba.utils.xml)

(defn style-str
  "Returns a string representing the properties
   as a SVG style"
  [& props]
  (reduce (fn [s [k v]]
            (str s " " (name k) ": "
                 (if (keyword? v)
                   (name v)
                   v)
                 "; "))
          "" (apply hash-map props)))