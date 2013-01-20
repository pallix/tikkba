(ns tikkba.utils.test.dom
  (:use [tikkba.utils.dom] :reload)
  (:use [clojure.test])
  (:use [tikkba.dom :only [dom-implementation svg-ns]]))

(def xlink-ns "http://www.w3.org/1999/xlink")

(deftest test-add-attrs
  (let [doc (create-document (dom-implementation) svg-ns "svg" nil)
        el (create-element-ns doc svg-ns "image")
        atts ["xlink:href" "foo.png"
              "key1" "val1"
              :key2  "val2"]]
    (apply add-attrs el atts)
    (is (= "foo.png" (.getAttributeNS el xlink-ns "href")) "namespaced")
    (is (= "val1" (.getAttribute el "key1")) "un-namespaced string")
    (is (= "val2" (.getAttribute el "key2")) "un-namespaced keyword")))
