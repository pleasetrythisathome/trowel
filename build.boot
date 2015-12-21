(set-env!
 :dependencies '[[adzerk/bootlaces "0.1.6-SNAPSHOT" :scope "test"]
                 [garden "1.3.0"]
                 [org.clojure/clojure "1.8.0-RC4"]
                 [prismatic/plumbing "0.5.2"]
                 [prismatic/schema "1.0.4"]]
 :source-paths #{"src"}
 :resource-paths #(conj % "resources"))

(require
 '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0-SNAPSHOT")
(bootlaces! +version+)

(task-options!
 pom {:project 'trowel
      :version +version+
      :description "tools for garden"
      :url "https://github.com/pleasetrythisathome/trowel"
      :scm {:url "https://github.com/pleasetrythisathome/trowel"}})
