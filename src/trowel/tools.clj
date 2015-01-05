(ns trowel.tools
  (:refer-clojure :exclude [+ - * /])
  (:require [garden.def :refer [defstylesheet defstyles]]
            [garden.units :refer [px]]
            [garden.core :refer [css]]
            [garden.color :refer [hsl rgb]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.stylesheet :refer [at-media]]
            [clojure.string :as str]
            [plumbing.core :refer :all :exclude [update]]
            [schema.core :as s]
            [trowel.utils :refer :all]))

(s/defn colors
  [colors :- {s/Keyword garden.color.CSSColor}]
  (list
   (selectors (fn [_ c]
                [{:color c}
                 [:&:before {:color c}]])
              [:.color-]
              colors)

   (selectors (fn [_ c]
                {:background c})
              [:.bg-]
              colors)

   (selectors (fn [_ c]
                [:&
                 [:path {:fill c}]])
              [:.fill-]
              colors)))

(s/defn font-size
  [sizes :- {s/Keyword (or garden.types.CSSUnit
                           (s/both [garden.types.CSSUnit]
                                   (s/pred (comp (partial = 2) count))))}]
  (selectors (fn [_ [size height]]
               [(cond-> {:font-size size}
                        height (merge {:line-height height}))])
             [:.font-]
             sizes))

(s/defn border
  ([] (border 1 5))
  ([max] (border 1 max))
  ([min max]
     (list
      (selectors (fn [r]
                   {:border-radius r})
                 {:.round "50%"
                  :.rounded (px 7)})
      (selectors (fn [_ loc width]
                   (letfn [(make-border [loc]
                             {:border (cond->> {:style :solid
                                                :width width
                                                :color "rgba(157, 161, 166, 0.3)"}
                                               loc (hash-map loc))})]
                     (if (sequential? loc)
                       (mapv make-border loc)
                       (make-border loc))))
                 [:.border]
                 {"" nil
                  :-top :top
                  :-right :right
                  :-bottom :bottom
                  :-left :left
                  :-vert [:top :bottom]
                  :-horiz [:left :right]}
                 (merge
                  {"" (px min)}
                  (into {}
                        (for [w (range min (inc max))]
                          [(keyword (str "-" w)) (px w)])))))))

(defn spacing
  [grid]
  (list
   (selectors (fn [prop loc s]
                (cond->> (cond
                          (sequential? loc) (zipmap loc (repeat s))
                          loc {loc s}
                          :else s)
                         prop (hash-map prop))
                #_(-> (cond
                     (sequential? loc) (zipmap loc (repeat s))
                     loc {loc s}
                     :else s)
                    (?>> prop )))
              {"" nil
               :.padding- :padding
               :.margin- :margin}
              {"" nil
               :top- :top
               :right- :right
               :bottom- :bottom
               :left- :left
               :horiz- [:left :right]
               :vert- [:top :bottom]}
              (into {} grid))
   [:.top {:top (px 0)}]
   [:.right {:right (px 0)}]
   [:.bottom {:bottom (px 0)}]
   [:.left {:left (px 0)}]))

(defn nudge
  ([] (nudge 5))
  ([max]
     (selectors (fn [_ loc amt]
                  {:margin {loc amt}})
                [:.nudge-]
                {:down :top
                 :left :right
                 :up :bottom
                 :right :left}
                (into {}
                      (for [x (range 1 (inc max))]
                        [(keyword (str "-" x)) (px x)])))))

(s/defn media-breaks
  ([] (media-breaks {:sm (px 568)
                     :md (px 768)
                     :lg (px 1024)
                     :xl (px 1280)}))
  ([breaks :- {s/Keyword garden.types.CSSUnit}]
     (selectors (fn [_ bound break]
                  (at-media {bound break}
                            [:&
                             {:display "none"}]))
                [:.hide-]
                {:gt- :min-width
                 :lt- :max-width}
                breaks)))

(defn align []
  (list
   [:.nowrap {:white-space "nowrap"}]
   (selectors (fn [_ a]
                {:text-align a})
              [:.align-]
              {:left "left"
               :right "right"
               :center "center"
               :justify "justify"})
   (selectors (fn [_ a]
                {:vert-align a})
              [:.align-]
              {:top "top"
               :bottom "bottom"})))

(defn opacity []
  (selectors (fn [o]
               {:opacity o})
             {:.transparent 0
              :.light-less 0.7
              :.light 0.5
              :.lighter 0.3}))

(defn display []
  (list
   (selectors (fn [d]
                {:display d})
              {:.hidden "none"
               :.inline "inline"
               :.inline-block "inline-block"})
   [:.inline-block
    {:vertical-align "top"}]))

(defn box-sizing []
  (list
   [:.border-box
    ^:prefix {:box-sizing "border-box"}]
   [:.content-box
    ^:prefix {:box-sizing "content-box"}]))

(defn position []
  (list
   (selectors(fn [m]
               {:margin m})
             {:.auto "auto"})
   (selectors (fn [p]
                {:position p})
              {:.relative "relative"
               :.absolute "absolute !important"
               :.fixed "fixed !important"})
   (selectors (fn [_ f]
                {:float f})
              [:.pull-]
              {:left "left"
               :right "right"})
   [:.clearfix {:*zoom 1}
    [:&:before :&:after {:display "table"
                         :line-height "0"
                         :content "\"\""}]
    [:&:after {:clear "both"}]]))

(defn overflow []
  (selectors (fn [_ [x y]]
               {:overflow-x x
                :overflow-y y})
             [:.scroll-]
             {:all ["scroll" "scroll"]
              :x ["scroll" "hidden"]
              :y ["hidden" "scroll"]
              :hidden ["hidden" "hidden"]}))

(defn text-transform []
  (selectors (fn [t]
               {:text-transform t})
             {:.caps "uppercase"
              :.title-case "capitalize"}))

(defn font-weight []
  (selectors (fn [w]
               {:font-weight w})
             {:.thin 100
              :.semi-bold 600
              :.bold 900}))
(defn size []
  (selectors (fn [size props]
               (reduce (fn [out prop] (assoc out prop size)) {} props))
             {:.full "100%"
              :.two-third "33.33%"
              :.half "50%"
              :.third "33.33%"}
             {"" [:width :height]
              :-width [:width]
              :-height [:height]}))

(defn interaction []
  [:.unselectable {:user-select "none"
                   :cursor "default"}]
  [:.pass-through {:pointer-events "none"}]
  [:.pointer {:cursor "pointer"}]

  [:.hover-opacity
   :.selected-opacity
   :.active-opacity
   {:opacity 1}
   [:&:hover :&.active :&.selected
    {:opacity 0.5
     :cursor "pointer"}]]

  [:.hover-opacity-reverse
   :.selected-opacity-reverse
   :.active-opacity-reverse
   {:opacity 0.5}
   [:&:hover :&.selected :&.active
    {:opacity 1
     :cursor "pointer"}]]

  [:.hover-opacity-parent
   :.selected-opacity-parent
   :.active-opacity-parent
   [:&:hover :&.active :&.selected
    {:cursor "pointer"}
    [:.hover-opacity-child
     :.selected-opacity-child
     :.active-opacity-child
     {:opacity 0.5}]]]

  [:.hover-opacity-parent-reverse
   :.selected-opacity-parent-reverse
   :.active-opacity-parent-reverse
   [:&:hover :&.active :&.selected
    {:cursor "pointer"}
    [:.hover-opacity-child-reverse
     :.selected-opacity-child-reverse
     :.active-opacity-child-reverse
     {:opacity 0.5}]]])

(defn image []
  [:.cover
   ^:prefix {:background {:size "cover"
                          :position "center center"
                          :repeat "no-repeat"}}])

(defn base []
  (list
   (align)
   (opacity)
   (box-sizing)
   (position)
   (overflow)
   (text-transform)
   (font-weight)
   (size)
   (interaction)
   (image)))
