(ns lein2nix.core
  (:require [leiningen.core.project :as project]
            [clojure.tools.logging :as log])
  (:import (org.eclipse.aether.graph Dependency Exclusion)
           (org.eclipse.aether.artifact DefaultArtifact))
  (:gen-class))

(defn lein-exclusion-to-aether-exclusion
  "Convert a lein exclusion vector to an aether Exclusion"
  [excl]
  (let [excl0 (nth excl 0)
        exclMap (reduce (fn [acc kv]
                          (case (first kv)
                            :classifier (assoc acc :classifier (fnext kv))
                            :extension (assoc acc :extension (fnext kv))))
                        {:groupId (or (namespace excl0) (name excl0)),
                         :artifactId (name excl0)} (partition 2
                                                              (subvec excl 1)))]
    (Exclusion. (exclMap :groupId) (exclMap :artifactId) (exclMap :classifier)
                (exclMap :extension))))
(defn lein-dependency-to-aether-dependency
  "Convert a lein dependency vector to an aether Dependency"
  [dep]
  (let [dep0 (nth dep 0)
        depMap (reduce (fn [acc kv]
                         (case (first kv)
                           :extension (assoc acc :extension (fnext kv))
                           :scope (assoc acc :scope (fnext kv))
                           :optional (assoc acc :scope (fnext kv))
                           :exclusions
                           (assoc acc :exclusions
                                  (map lein-exclusion-to-aether-exclusion
                                       (fnext kv)))
                           :native-prefix acc
                           (do
                             (log/warn "Unknown key in dependency specification")
                             (assoc acc :properties
                                    (assoc
                                      (acc :properties)
                                      (first kv)
                                      (fnext kv))))))
                         {:groupId (or (namespace dep0) (name dep0)),
                          :artifactId (name dep0),
                          :version (nth dep 1),
                          :extension "jar",
                          :scope "compile",
                          :properties {},
                          :optional false} (partition 2 (subvec dep 2)))]
    (Dependency. (DefaultArtifact. (depMap :groupId) (depMap :artifactId)
                                 (depMap :classifier) (depMap :extension)
                                 (depMap :version) (depMap :properties) nil)
                (depMap :scope) (depMap :optional) (depMap :exclusions))))
(defn -main
  "I don't do a whole lot ... yet."
  ([arg]
    (map
      lein-dependency-to-aether-dependency
      ((project/read arg) :dependencies)))
  ([] (-main "project.clj")))
