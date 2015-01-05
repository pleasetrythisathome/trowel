(set-env!
 :dependencies '[[adzerk/bootlaces "0.1.6-SNAPSHOT" :scope "test"]
                 [garden "1.2.5"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/clojure "1.7.0-alpha4"]
                 [prismatic/plumbing "0.3.5"]
                 [prismatic/schema "0.3.2"]]
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
