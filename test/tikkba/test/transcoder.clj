(ns tikkba.test.transcoder
  (:require [clojure.test :refer :all]
            [tikkba.transcoder :as t]
            [tikkba.dom :refer [svg-doc]]
            [analemma.svg :refer [svg rect]]
            [analemma.xml :as xml]
            [clojure.java.io :as io])
  (:import [javax.imageio ImageIO]
           [java.awt.image BufferedImage]))

(def png-output-path "/tmp/test_tikkba_transcoder.png")
(def jpeg-output-path "/tmp/test_tikkba_transcoder.jpeg")

(defn- make-dummy-svg []
  (svg-doc
   (svg 
    (-> (rect 0 10 50 400)
        (xml/add-attrs :fill "red")))))

(deftest test-does-png-transcoder-work-without-options
  (io/delete-file png-output-path true) 
  (let [the-doc (make-dummy-svg)
        result-path (t/to-png the-doc png-output-path)]
    (is (-> result-path nil? not))
    (is (= png-output-path result-path))
    (is (.exists (io/as-file result-path)))
    (io/delete-file png-output-path true)))

(deftest test-does-png-transcoder-work-with-options
  (io/delete-file png-output-path true) 
  (let [options {:width 128 :height 64}
        the-doc (make-dummy-svg)
        result-path (t/to-png the-doc png-output-path options)
        img (ImageIO/read (io/as-file result-path))]
    (is (= png-output-path result-path))
    (is (.exists (io/as-file result-path)))
    (is (= 128 (.getWidth img)))
    (is (= 64 (.getHeight img)))
    (io/delete-file png-output-path true)))

(deftest test-does-jpeg-transcoder-work-without-options
  (io/delete-file jpeg-output-path true) 
  (let [the-doc (make-dummy-svg)
        result-path (t/to-jpeg the-doc jpeg-output-path)]
    (is (-> result-path nil? not))
    (is (= jpeg-output-path result-path))
    (is (.exists (io/as-file result-path)))
    (io/delete-file jpeg-output-path true)))

(deftest test-does-jpeg-transcoder-work-with-options
  (io/delete-file jpeg-output-path true) 
  (let [options {:width 128 :height 64}
        the-doc (make-dummy-svg)
        result-path (t/to-jpeg the-doc jpeg-output-path options)
        img (ImageIO/read (io/as-file result-path))]
    (is (= jpeg-output-path result-path))
    (is (.exists (io/as-file result-path)))
    (is (= 128 (.getWidth img)))
    (is (= 64 (.getHeight img)))
    (io/delete-file jpeg-output-path true)))

(deftest test-indexed
  (let [options {:indexed 1}
        the-doc (make-dummy-svg)
        result-path (t/to-png the-doc png-output-path options)
        img (ImageIO/read (io/as-file result-path))]
    (is (= BufferedImage/TYPE_BYTE_BINARY (.getType img)))
    (io/delete-file png-output-path true))
  (let [options {:indexed 16}
        the-doc (make-dummy-svg)
        result-path (t/to-png the-doc png-output-path options)
        img (ImageIO/read (io/as-file result-path))]
    (is (= BufferedImage/TYPE_4BYTE_ABGR (.getType img)))
    (io/delete-file png-output-path true)))
