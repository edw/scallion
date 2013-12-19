(defproject edw/scallion "0.1.0"
  :description "Scallion: a configuration tool"
  :url "http://github.com/edw/scallion"
  :main scallion.core
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.3.0"]
                 [environ "0.4.0"]
                 [http-kit "2.1.13"]
                 [org.clojure/clojure "1.5.1"]])
