(ns de.prob2.components.formulabox
  (:require-macros [cljs.core.async.macros :as ma]
                   [de.prob2.macros :as m])
  (:require [cljs.core.async :as a]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [cljs-uuid.core :as uuid]
            [de.prob2.helpers :as h]
            [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.generated.unicodetranslator :as ut]))


(defn parse [trace-id ratom]
  (m/remote-let [res (clojure parse trace-id (:input @ratom))]
                (swap! ratom (fn [s]
                               (logp s)
                               (let [{:keys [status input]} res]
                                 (if-not (= input (:input s))
                                   s
                                   (assoc s :status ({true "" false "has-error"} status))))))))

(defn formulabox
  ([trace-id] (formulabox trace-id nil))
  ;; Button is of form {:text "TEXT!" :click (fn [e] ..click handler)}
  ([trace-id button] (formulabox trace-id button (gensym) nil nil))
  ([trace-id button id bfor aftr]
   (let [ratom (r/atom {:status "" :input ""})
         c (a/chan)]
     (ma/go-loop [formula "" last-formula ""]
       (let [t (a/timeout 500)
             [[v ss se] port] (a/alts! [c t] {:priority true})]
         (if (= port c)
           (do (a/close! t)
               (swap! ratom assoc :input v :ss ss :se se)
               (recur v last-formula))
           (do (when-not (= formula last-formula)
                 (if (empty? formula)
                   (reset! ratom {:status "" :input ""})
                   (do (parse trace-id ratom))))
               (recur formula formula)))))
     (r/create-class
      {:component-did-update
       (fn [x] (let [ss (:ss @ratom)
                    se (:se @ratom)
                    elem (.getElementById js/document id)]
                (set! (.-selectionStart elem) ss)
                (set! (.-selectionEnd elem) se)
                (.focus elem)))
       :reagent-render
       (fn []
         (let [s (:status @ratom)
               formula (get @ratom :input "")]
           ^{:key (h/fresh-id)}
           [:div
            [:div {:class (str "form-group " s)}
             (if bfor bfor "")
             [:div (when button {:class "input-group"})
              [:input {:id id
                      :class "form-control"
                      :value formula
                      :on-change
                      (fn [e] (let [value     (-> e .-target .-value)
                                    selstart  (-> e .-target .-selectionStart)
                                    selend    (-> e .-target .-selectionEnd)
                                    diff      (- selend selstart)
                                    [l r]     (split-at selstart value)
                                    to-trans  (ut/ascii (if (butlast l) (concat (ut/ascii (butlast l)) [(last l)]) l))
                                    p         (ut/unicode to-trans)
                                    v         (str p (apply str r))
                                    pos       (count p)]
                                (ma/go (a/>! c [v pos (+ diff pos)]))))
                      :on-key-down
                      (fn [e] (let [k (.-which e)
                                    value (-> e .-target .-value)
                                    selstart (-> e .-target .-selectionStart)
                                    selend (-> e .-target .-selectionEnd)]
                                (if (and (= k 8) (< 0 selstart) (= selstart selend))
                                  (let [_ (.preventDefault e)
                                        [l r]  (split-at selstart value)
                                        prefix (ut/unicode (butlast (ut/ascii l)))
                                        v      (str prefix (apply str r))
                                        pos    (count prefix)]
                                    (ma/go (a/>! c [v pos pos]))) 
                                  )))
                       }]
              (when button
               [:span {:class "input-group-btn"}
                [:button {:class "btn btn-default" :type "button"
                          :on-click (:click button)}
                 (:text button)]])]
             (if aftr aftr "")]]))}))))