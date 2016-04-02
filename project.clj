(defproject tikkba/tikkba "0.6.0"
  :min-lein-version "2.0.0"
  :url "https://github.com/pallix/tikkba"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojars.pallix/analemma "1.0.0" :exclusions [org.clojure/clojure]]
                 [org.clojars.pallix/batik "1.7.0"]]
  :description "Tikkba is Clojure library for the creation and the dynamic modification\nof SVG documents. It wraps the Apache Batik library and provides functions to\ncreate SVG images with the Clojure-based SVG DSL of the Analemma library.")
