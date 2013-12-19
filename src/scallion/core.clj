(ns scallion.core
  (:require
   [cheshire.core :refer [parse-string]]
   [clojure.string :as s]
   [clojure.walk :refer [keywordize-keys]]
   [environ.core :as environ]
   [org.httpkit.client :as http]))

(def etcd-port 4001)
(def etcd-hosts (atom nil))
(def etcd-hosts-path '("127.0.0.1" "172.17.42.1"))
(def etcd-get-options {:timeout 50})

(defn- keywordize
  [key]
  (-> (name key)
      (s/replace "-" "/")))

(defn- available-etcd-hosts
  [hosts-path]
  (->> hosts-path
       (map
        (fn [host]
          [host @(http/get (format "http://%s:%d/version" host etcd-port)
                           etcd-get-options)]))
       (reduce
        (fn [coll [host response]]
          (if (= (:status response) 200)
            (conj coll host)
            coll))
        ())
       reverse))

(defn- set-etcd-hosts!
  []
  (let [hosts (available-etcd-hosts etcd-hosts-path)]
    (reset! etcd-hosts
            (if (empty? hosts)
              :none
              hosts))))

(defn etcd-get-from-host
  [s host]
  (let [response @(http/get
                   (format "http://%s:%d/v1/keys/%s" host etcd-port s)
                   etcd-get-options)]
    (if (= (:status response) 200)
      (-> (:body response)
          (parse-string)
          keywordize-keys
          :value))))

(defn- etcd-get
  [s]
  (first (filter (comp not nil?)
                 (map (partial etcd-get-from-host s) @etcd-hosts))))

(defn- etcd-env
  [key]
  (when-not @etcd-hosts (set-etcd-hosts!))
  (if-not (= @etcd-hosts :none)
    (etcd-get (keywordize key))
    nil))

(defn env
  ([key]
     (or (etcd-env key)
         (environ/env key)
         nil)))
