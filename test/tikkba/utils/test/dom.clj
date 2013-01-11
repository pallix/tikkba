(ns tikkba.utils.test.dom
  (:use [tikkba.utils.dom] :reload)
  (:use [clojure.test])
  (:use [tikkba.dom :only [dom-implementation svg-ns]]))

(def xlink-ns "http://www.w3.org/1999/xlink")

(defn attrs
  "Return a map of the element's attributes as strings"
  [el]
  (let [attrs (.getAttributes el)]
    (apply merge (for [n (range (.getLength attrs))
                       :let [attr (.item attrs n)]]
                   {(.getName attr) (.getValue attr)}))))

(deftest t-set-attribute-ns
  (let [doc (create-document (dom-implementation) svg-ns "svg" nil)
        el (create-element-ns doc svg-ns "image")]
    (set-attribute el "xlink:href" "foo.png")
    (.setAttributeNS el xlink-ns "bar" "baz")
    (is (= (get (attrs el) "xlink:href" "foo.png")))
    (is (= (.getAttributeNS el xlink-ns "href") "foo.png"))))
