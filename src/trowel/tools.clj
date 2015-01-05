(ns trowel.tools
  (:refer-clojure :exclude [+ - * /])
  (:require [garden.def :refer [defstylesheet defstyles]]
            [garden.units :refer [px]]
            [garden.core :refer [css]]
            [garden.color :refer [hsl rgb]]
            [garden.arithmetic :refer [+ - * /]]
            [garden.stylesheet :refer [at-media]]
            [clojure.string :as str]

            [trowel.utils :refer :all]))

(def colors {:white (rgb 255 255 255)
             :off-white (hsl 210 1 97)
             :gray (hsl 213 5 95)
             :black (rgb 0 0 0)

             :blue (rgb 79 158 247)
             :red (rgb 255 0 0)
             :red-light (hsl 360 40 90)
             :green (rgb 50 225 185)})

(defstyles tools
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
             colors)

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
              :bottom "bottom"})

  (selectors (fn [_ f]
               {:float f})
             [:.pull-]
             {:left "left"
              :right "right"})

  (selectors (fn [_ [size height]]
               [{:font-size size
                 :line-height height}])
             [:.font-]
             {:small [(px 14) (px 18)]
              :msmall [(px 18) (px 23)]
              :medium [(px 22) (px 25)]
              :mlarge [(px 28) (px 30)]
              :large [(px 44) (px 50)]
              :xlarge [(px 64) (px 56)]
              :xxlarge ["27vw" "27vw"]})

  (selectors (fn [_ [x y]]
               {:overflow-x x
                :overflow-y y})
             [:.scroll-]
             {:all ["scroll" "scroll"]
              :x ["scroll" "hidden"]
              :y ["hidden" "scroll"]
              :hidden ["hidden" "hidden"]})

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
              {"" (px 1)}
              (into {}
                    (for [w (range 1 6)]
                      [(keyword (str "-" w)) (px w)]))))

  (selectors (fn [d]
                   {:display d})
             {:.hidden "none"
              :.inline "inline"
              :.inline-block "inline-block"})
  [:.inline-block
   {:vertical-align "top"}]

  (selectors (fn [o]
                   {:opacity o})
             {:.transparent 0
              :.light-less 0.7
              :.light 0.5
              :.lighter 0.3})

  (selectors (fn [t]
                   {:text-transform t})
             {:.caps "uppercase"
              :.title-case "capitalize"})

  (selectors(fn [m]
                   {:margin m})
             {:.auto "auto"})

  (selectors (fn [p]
               {:position p})
             {:.relative "relative"
              :.absolute "absolute !important"
              :.fixed "fixed !important"})
  (selectors (fn [r]
               {:border-radius r})
             {:.round "50%"
              :.rounded (px 7)})
  (selectors (fn [w]
                   {:font-weight w})
             {:.thin 100
              :.semi-bold 600
              :.bold 900})

  (selectors (fn [size props]
               (reduce (fn [out prop] (assoc out prop size)) {} props))
             {:.full "100%"
              :.two-third "33.33%"
              :.half "50%"
              :.third "33.33%"}
             {"" [:width :height]
              :-width [:width]
              :-height [:height]})

  (selectors (fn [prop loc s]
               {prop (if loc
                       (if (sequential? loc)
                         (zipmap loc (repeat s))
                         {loc s})
                       s)})
             {:.padding- :padding
              :.margin- :margin}
             {"" nil
              :top- :top
              :right- :right
              :bottom- :bottom
              :left- :left
              :horiz- [:left :right]
              :vert- [:top :bottom]}
             (into {} (grid 15 3 10 px)))

  (selectors (fn [_ loc amt]
               {:margin {loc amt}})
             [:.nudge-]
             {:down :top
              :left :right
              :up :bottom
              :right :left}
             (into {}
                   (for [x (range 1 6)]
                     [(keyword (str "-" x)) (px x)])))

  (selectors (fn [_ bound break]
               (at-media {bound break}
                         [:&
                          {:display "none"}]))
             [:.hide-]
             {:gt- :min-width
              :lt- :max-width}
             {:sm (px 568)
              :md (px 768)
              :lg (px 1024)
              :xl (px 1280)})

  [:.nowrap {:white-space "nowrap"}]
  [:.top {:top (px 0)}]
  [:.right {:right (px 0)}]
  [:.bottom {:bottom (px 0)}]
  [:.left {:left (px 0)}]
  [:.pointer {:cursor "pointer"}]
  [:.cover
   ^:prefix {:background {:size "cover"
                          :position "center center"
                          :repeat "no-repeat"}}]

  [:.clearfix {:*zoom 1}
   [:&:before :&:after {:display "table"
                        :line-height "0"
                        :content "\"\""}]
   [:&:after {:clear "both"}]]
  [:.unselectable {:user-select "none"
                   :cursor "default"}]
  [:.pass-through {:pointer-events "none"}]
  [:.border-box
   ^:prefix {:box-sizing "border-box"}]
  [:.content-box
   ^:prefix {:box-sizing "content-box"}]

  (at-media {:min-width (px 568)}
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

  )
