(ns de.prob2.components.events
  (:require [reagent.core :as r]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [de.prob2.helpers :as h]
            [de.prob2.i18n :refer [i18n format]]))


(defn- mk-event-item [state trace-id {:keys [id] :as item}]
  ^{:key id}
  [:li
   [:a {:class "event-entry"
        :href (str "#/trace/" trace-id)
        :on-click #(rf/dispatch [:events/execute {:state-id state :trace-id trace-id :event-id id}])}
    (h/pp-transition item)]])

(defn- disabled-if-not [test]
  (if-not test " disabled" ""))

(defn trace-fwd-back [id fwd? back?]
  [:div {:class "btn-group" :role "group" :aria-label "fwd-back"}
   [:button {:type "button"
             :class (str "btn btn-default " (disabled-if-not back?))
             :on-click #(rf/dispatch [:history/back id])}
    [:span {:class "glyphicon glyphicon-chevron-left"}]]
   [:button {:type "button"
             :class (str "btn btn-default " (disabled-if-not fwd?))
             :on-click #(rf/dispatch [:history/forward id])}
    [:span {:class "glyphicon glyphicon-chevron-right"}]]])


(defn random-panel [id sid rand-panel]
  (let [rand-id (str "random-n-select-" id)]
    [:div.panel.panel-default.random-panel {:id rand-panel}
     [:div {:class "panel-body random-menu list-group"}
      [:a.list-group-item {:on-click (fn [e] (rf/dispatch [:events/random {:state-id sid :trace-id id}]))} (i18n :execute-1-random)]
      [:a.list-group-item {:on-click (fn [e] (rf/dispatch [:events/random {:state-id sid :trace-id id :number 5}]))} (format :execute-n-random 5)]
      [:a.list-group-item {:on-click (fn [e] (rf/dispatch [:events/random {:state-id sid :trace-id id :number 10}]))} (format :execute-n-random 10)]
      [:form.list-group-item.form-inline
       [:div.form-group
        [:input.form-control {:type "text"
                              :id rand-id
                              :placeholder (i18n :n-random)}]
        [:button
         {:type "button"
          :class "btn btn-default random-button"
          :on-click (fn [e]
                      (let [input (.getElementById js/document rand-id)
                            value (js/parseInt (.-value input))]
                        (rf/dispatch
                         [:events/random
                          {:state-id sid
                           :trace-id id
                           :number value}])))}
         (i18n :execute)]]]]]))

(defn toolbar [sid id fwd? back?]
  (let [rand-panel (str "random-panel-" id)]
    [:div
     [:div {:class "btn-toolbar" :role "toolbar" }
      [:div {:class "btn-group" :role "group" }
       [:button {:type "button"
                 :class "btn btn-default dropdown-toggle"}
        [:span {:class "glyphicon glyphicon-random"}]]]
      [trace-fwd-back id fwd? back?]]
     [random-panel id sid rand-panel]
     ]))

(defn events-view [id]
  (let [filtered? (r/atom true)]
    (fn []
      (let [trace (rf/subscribe [:trace id])
            {{sid :state} :current-state ts :out-transitions back? :back? fwd? :forward?} @trace]
        [:div {:class "events-view"}

         [toolbar sid id fwd? back?]
         [:ul {:class "events-list"}
          (map (partial mk-event-item sid id) ts)]]))))

(rf/register-handler :events/execute h/relay)
(rf/register-handler :history/back h/relay)
(rf/register-handler :history/forward h/relay)
(rf/register-handler :events/random h/relay)
