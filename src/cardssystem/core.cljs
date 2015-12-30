(ns cardssystem.core
  (:require
   #_[om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true]
    ;[goog.dom :as gdom]
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom]
   [clojure.string :refer [join]]
    ;[goog.string :rename {format format-str}]
   [goog.string :as gstring]
   [goog.string.format]
   )
  ;(:import goog.string.format)
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

;(def format-str format)

(defcard first-card
  (sab/html [:div
             [:h1 "This is your first devcard!"]]))

(defcard second-card
         (sab/html [:div
                    [:h1 "This is my second devcard!"]]))

(defui HelloWorld
       Object
       (render [this]
               (dom/div nil "Hello, world!")))

(def hello (om/factory HelloWorld))

(defcard third-card (hello))

(defn class-names [m]
  (->> (filter second m)
       (map (comp name first))
       (join " ")))

(defui Assignee
  static om/Ident
  (ident [this props]
    [:user/by-id (:id props)])
  static om/IQuery
  (query [this]
    [:id :username :name])
  Object
  (render [this]
    (let [{:keys [username name]} (om/props this)
          {:keys [selected with-name activate-fn]} (om/get-computed this)]
      (dom/span #js {:className (class-names {:assignee true
                                              :selected selected})
                     :onClick #(when activate-fn
                                (activate-fn (om/get-ident this)))
                     :title name}
                (if with-name
                  (gstring/format "%s (@%s) " name username)
                  (gstring/format "@%s " username)
                  )))))

(def assignee (om/factory Assignee {:keyfn :id}))

(defui Card
  static om/Ident
  (ident [this props]
    [:card/by-id (:id props)])
  static om/IQuery
  (query [this]
    [:id :text {:assignees (om/get-query Assignee)}])
  Object
  (render [this]
    (let [{:keys [id text assignees]} (om/props this)
          {:keys [drag-fns activate-fn]} (om/get-computed this)]
      (let [ref (om/get-ident this)]
        (dom/div #js {:className "card"
                      :onClick #(some-> activate-fn (apply [ref]))
                      :draggable true
                      :onDragStart
                                 (fn [e]
                                   (.setData (.-dataTransfer e) "text/plain" (str ref))
                                   (some-> drag-fns :start (apply [ref])))
                      :onDragEnd #(some-> drag-fns :end (apply [ref]))}
                 (dom/span #js {:className "card-id"} id)
                 (for [a assignees]
                   (assignee a))
                 (dom/span #js {:className "text"} text))))))

(def card (om/factory Card {:keyfn :id}))

(defcard
  "### Card with one assignee"
  (fn [props _] (card @props))
  {:id 1 :text "Card with one assignee"
   :assignees [{:id 2 :username "ada" :name "Ada Lovelace"}]}
  {:inspect-data true})

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

