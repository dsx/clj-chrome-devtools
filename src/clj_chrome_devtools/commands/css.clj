(ns clj-chrome-devtools.commands.css
  "This domain exposes CSS read/write operations. All CSS objects (stylesheets, rules, and styles)\nhave an associated `id` used in subsequent operations on the related object. Each object type has\na specific `id` structure, and those are not interchangeable between objects of different kinds.\nCSS objects can be loaded using the `get*ForNode()` calls (which accept a DOM node id). A client\ncan also keep track of stylesheets via the `styleSheetAdded`/`styleSheetRemoved` events and\nsubsequently load the required stylesheet contents using the `getStyleSheet[Text]()` methods."
  (:require [clojure.spec.alpha :as s]
            [clj-chrome-devtools.impl.command :as cmd]
            [clj-chrome-devtools.impl.connection :as c]))

(s/def
 ::style-sheet-id
 string?)

(s/def
 ::style-sheet-origin
 #{"user-agent" "injected" "regular" "inspector"})

(s/def
 ::pseudo-element-matches
 (s/keys
  :req-un
  [::pseudo-type
   ::matches]))

(s/def
 ::inherited-style-entry
 (s/keys
  :req-un
  [::matched-css-rules]
  :opt-un
  [::inline-style]))

(s/def
 ::inherited-pseudo-element-matches
 (s/keys
  :req-un
  [::pseudo-elements]))

(s/def
 ::rule-match
 (s/keys
  :req-un
  [::rule
   ::matching-selectors]))

(s/def
 ::value
 (s/keys
  :req-un
  [::text]
  :opt-un
  [::range]))

(s/def
 ::selector-list
 (s/keys
  :req-un
  [::selectors
   ::text]))

(s/def
 ::css-style-sheet-header
 (s/keys
  :req-un
  [::style-sheet-id
   ::frame-id
   ::source-url
   ::origin
   ::title
   ::disabled
   ::is-inline
   ::is-mutable
   ::is-constructed
   ::start-line
   ::start-column
   ::length
   ::end-line
   ::end-column]
  :opt-un
  [::source-map-url
   ::owner-node
   ::has-source-url]))

(s/def
 ::css-rule
 (s/keys
  :req-un
  [::selector-list
   ::origin
   ::style]
  :opt-un
  [::style-sheet-id
   ::media
   ::container-queries
   ::supports
   ::layers]))

(s/def
 ::rule-usage
 (s/keys
  :req-un
  [::style-sheet-id
   ::start-offset
   ::end-offset
   ::used]))

(s/def
 ::source-range
 (s/keys
  :req-un
  [::start-line
   ::start-column
   ::end-line
   ::end-column]))

(s/def
 ::shorthand-entry
 (s/keys
  :req-un
  [::name
   ::value]
  :opt-un
  [::important]))

(s/def
 ::css-computed-style-property
 (s/keys
  :req-un
  [::name
   ::value]))

(s/def
 ::css-style
 (s/keys
  :req-un
  [::css-properties
   ::shorthand-entries]
  :opt-un
  [::style-sheet-id
   ::css-text
   ::range]))

(s/def
 ::css-property
 (s/keys
  :req-un
  [::name
   ::value]
  :opt-un
  [::important
   ::implicit
   ::text
   ::parsed-ok
   ::disabled
   ::range]))

(s/def
 ::css-media
 (s/keys
  :req-un
  [::text
   ::source]
  :opt-un
  [::source-url
   ::range
   ::style-sheet-id
   ::media-list]))

(s/def
 ::media-query
 (s/keys
  :req-un
  [::expressions
   ::active]))

(s/def
 ::media-query-expression
 (s/keys
  :req-un
  [::value
   ::unit
   ::feature]
  :opt-un
  [::value-range
   ::computed-length]))

(s/def
 ::css-container-query
 (s/keys
  :req-un
  [::text]
  :opt-un
  [::range
   ::style-sheet-id
   ::name]))

(s/def
 ::css-supports
 (s/keys
  :req-un
  [::text
   ::active]
  :opt-un
  [::range
   ::style-sheet-id]))

(s/def
 ::css-layer
 (s/keys
  :req-un
  [::text]
  :opt-un
  [::range
   ::style-sheet-id]))

(s/def
 ::css-layer-data
 (s/keys
  :req-un
  [::name
   ::order]
  :opt-un
  [::sub-layers]))

(s/def
 ::platform-font-usage
 (s/keys
  :req-un
  [::family-name
   ::is-custom-font
   ::glyph-count]))

(s/def
 ::font-variation-axis
 (s/keys
  :req-un
  [::tag
   ::name
   ::min-value
   ::max-value
   ::default-value]))

(s/def
 ::font-face
 (s/keys
  :req-un
  [::font-family
   ::font-style
   ::font-variant
   ::font-weight
   ::font-stretch
   ::unicode-range
   ::src
   ::platform-font-family]
  :opt-un
  [::font-variation-axes]))

(s/def
 ::css-keyframes-rule
 (s/keys
  :req-un
  [::animation-name
   ::keyframes]))

(s/def
 ::css-keyframe-rule
 (s/keys
  :req-un
  [::origin
   ::key-text
   ::style]
  :opt-un
  [::style-sheet-id]))

(s/def
 ::style-declaration-edit
 (s/keys
  :req-un
  [::style-sheet-id
   ::range
   ::text]))
(defn
 add-rule
 "Inserts a new rule with the given `ruleText` in a stylesheet with given `styleSheetId`, at the\nposition specified by `location`.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | The css style sheet identifier where a new rule should be inserted.\n  :rule-text      | The text of a new rule.\n  :location       | Text position of a new rule in the target style sheet.\n\nReturn map keys:\n\n\n  Key   | Description \n  ------|------------ \n  :rule | The newly created rule."
 ([]
  (add-rule
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id rule-text location]}]
  (add-rule
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id rule-text location]}]
  (cmd/command
   connection
   "CSS"
   "addRule"
   params
   {:style-sheet-id "styleSheetId",
    :rule-text "ruleText",
    :location "location"})))

(s/fdef
 add-rule
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::rule-text
     ::location]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::rule-text
     ::location])))
 :ret
 (s/keys
  :req-un
  [::rule]))

(defn
 collect-class-names
 "Returns all class names from specified stylesheet.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n\nReturn map keys:\n\n\n  Key          | Description \n  -------------|------------ \n  :class-names | Class name list."
 ([]
  (collect-class-names
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id]}]
  (collect-class-names
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id]}]
  (cmd/command
   connection
   "CSS"
   "collectClassNames"
   params
   {:style-sheet-id "styleSheetId"})))

(s/fdef
 collect-class-names
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id])))
 :ret
 (s/keys
  :req-un
  [::class-names]))

(defn
 create-style-sheet
 "Creates a new special \"via-inspector\" stylesheet in the frame with given `frameId`.\n\nParameters map keys:\n\n\n  Key       | Description \n  ----------|------------ \n  :frame-id | Identifier of the frame where \"via-inspector\" stylesheet should be created.\n\nReturn map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | Identifier of the created \"via-inspector\" stylesheet."
 ([]
  (create-style-sheet
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [frame-id]}]
  (create-style-sheet
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [frame-id]}]
  (cmd/command
   connection
   "CSS"
   "createStyleSheet"
   params
   {:frame-id "frameId"})))

(s/fdef
 create-style-sheet
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::frame-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::frame-id])))
 :ret
 (s/keys
  :req-un
  [::style-sheet-id]))

(defn
 disable
 "Disables the CSS agent for the given page."
 ([]
  (disable
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (disable
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "disable"
   params
   {})))

(s/fdef
 disable
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys))

(defn
 enable
 "Enables the CSS agent for the given page. Clients should not assume that the CSS agent has been\nenabled until the result of this command is received."
 ([]
  (enable
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (enable
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "enable"
   params
   {})))

(s/fdef
 enable
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys))

(defn
 force-pseudo-state
 "Ensures that the given node will have specified pseudo-classes whenever its style is computed by\nthe browser.\n\nParameters map keys:\n\n\n  Key                    | Description \n  -----------------------|------------ \n  :node-id               | The element id for which to force the pseudo state.\n  :forced-pseudo-classes | Element pseudo classes to force when computing the element's style."
 ([]
  (force-pseudo-state
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id forced-pseudo-classes]}]
  (force-pseudo-state
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id forced-pseudo-classes]}]
  (cmd/command
   connection
   "CSS"
   "forcePseudoState"
   params
   {:node-id "nodeId", :forced-pseudo-classes "forcedPseudoClasses"})))

(s/fdef
 force-pseudo-state
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id
     ::forced-pseudo-classes]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id
     ::forced-pseudo-classes])))
 :ret
 (s/keys))

(defn
 get-background-colors
 "\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :node-id | Id of the node to get background colors for.\n\nReturn map keys:\n\n\n  Key                   | Description \n  ----------------------|------------ \n  :background-colors    | The range of background colors behind this element, if it contains any visible text. If no\nvisible text is present, this will be undefined. In the case of a flat background color,\nthis will consist of simply that color. In the case of a gradient, this will consist of each\nof the color stops. For anything more complicated, this will be an empty array. Images will\nbe ignored (as if the image had failed to load). (optional)\n  :computed-font-size   | The computed font size for this node, as a CSS computed value string (e.g. '12px'). (optional)\n  :computed-font-weight | The computed font weight for this node, as a CSS computed value string (e.g. 'normal' or\n'100'). (optional)"
 ([]
  (get-background-colors
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id]}]
  (get-background-colors
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id]}]
  (cmd/command
   connection
   "CSS"
   "getBackgroundColors"
   params
   {:node-id "nodeId"})))

(s/fdef
 get-background-colors
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id])))
 :ret
 (s/keys
  :opt-un
  [::background-colors
   ::computed-font-size
   ::computed-font-weight]))

(defn
 get-computed-style-for-node
 "Returns the computed style for a DOM node identified by `nodeId`.\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :node-id | null\n\nReturn map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :computed-style | Computed style for the specified DOM node."
 ([]
  (get-computed-style-for-node
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id]}]
  (get-computed-style-for-node
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id]}]
  (cmd/command
   connection
   "CSS"
   "getComputedStyleForNode"
   params
   {:node-id "nodeId"})))

(s/fdef
 get-computed-style-for-node
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id])))
 :ret
 (s/keys
  :req-un
  [::computed-style]))

(defn
 get-inline-styles-for-node
 "Returns the styles defined inline (explicitly in the \"style\" attribute and implicitly, using DOM\nattributes) for a DOM node identified by `nodeId`.\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :node-id | null\n\nReturn map keys:\n\n\n  Key               | Description \n  ------------------|------------ \n  :inline-style     | Inline style for the specified DOM node. (optional)\n  :attributes-style | Attribute-defined element style (e.g. resulting from \"width=20 height=100%\"). (optional)"
 ([]
  (get-inline-styles-for-node
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id]}]
  (get-inline-styles-for-node
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id]}]
  (cmd/command
   connection
   "CSS"
   "getInlineStylesForNode"
   params
   {:node-id "nodeId"})))

(s/fdef
 get-inline-styles-for-node
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id])))
 :ret
 (s/keys
  :opt-un
  [::inline-style
   ::attributes-style]))

(defn
 get-matched-styles-for-node
 "Returns requested styles for a DOM node identified by `nodeId`.\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :node-id | null\n\nReturn map keys:\n\n\n  Key                        | Description \n  ---------------------------|------------ \n  :inline-style              | Inline style for the specified DOM node. (optional)\n  :attributes-style          | Attribute-defined element style (e.g. resulting from \"width=20 height=100%\"). (optional)\n  :matched-css-rules         | CSS rules matching this node, from all applicable stylesheets. (optional)\n  :pseudo-elements           | Pseudo style matches for this node. (optional)\n  :inherited                 | A chain of inherited styles (from the immediate node parent up to the DOM tree root). (optional)\n  :inherited-pseudo-elements | A chain of inherited pseudo element styles (from the immediate node parent up to the DOM tree root). (optional)\n  :css-keyframes-rules       | A list of CSS keyframed animations matching this node. (optional)"
 ([]
  (get-matched-styles-for-node
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id]}]
  (get-matched-styles-for-node
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id]}]
  (cmd/command
   connection
   "CSS"
   "getMatchedStylesForNode"
   params
   {:node-id "nodeId"})))

(s/fdef
 get-matched-styles-for-node
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id])))
 :ret
 (s/keys
  :opt-un
  [::inline-style
   ::attributes-style
   ::matched-css-rules
   ::pseudo-elements
   ::inherited
   ::inherited-pseudo-elements
   ::css-keyframes-rules]))

(defn
 get-media-queries
 "Returns all media queries parsed by the rendering engine.\n\nReturn map keys:\n\n\n  Key     | Description \n  --------|------------ \n  :medias | null"
 ([]
  (get-media-queries
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (get-media-queries
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "getMediaQueries"
   params
   {})))

(s/fdef
 get-media-queries
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys
  :req-un
  [::medias]))

(defn
 get-platform-fonts-for-node
 "Requests information about platform fonts which we used to render child TextNodes in the given\nnode.\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :node-id | null\n\nReturn map keys:\n\n\n  Key    | Description \n  -------|------------ \n  :fonts | Usage statistics for every employed platform font."
 ([]
  (get-platform-fonts-for-node
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id]}]
  (get-platform-fonts-for-node
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id]}]
  (cmd/command
   connection
   "CSS"
   "getPlatformFontsForNode"
   params
   {:node-id "nodeId"})))

(s/fdef
 get-platform-fonts-for-node
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id])))
 :ret
 (s/keys
  :req-un
  [::fonts]))

(defn
 get-style-sheet-text
 "Returns the current textual content for a stylesheet.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n\nReturn map keys:\n\n\n  Key   | Description \n  ------|------------ \n  :text | The stylesheet text."
 ([]
  (get-style-sheet-text
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id]}]
  (get-style-sheet-text
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id]}]
  (cmd/command
   connection
   "CSS"
   "getStyleSheetText"
   params
   {:style-sheet-id "styleSheetId"})))

(s/fdef
 get-style-sheet-text
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id])))
 :ret
 (s/keys
  :req-un
  [::text]))

(defn
 get-layers-for-node
 "Returns all layers parsed by the rendering engine for the tree scope of a node.\nGiven a DOM element identified by nodeId, getLayersForNode returns the root\nlayer for the nearest ancestor document or shadow root. The layer root contains\nthe full layer tree for the tree scope and their ordering.\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :node-id | null\n\nReturn map keys:\n\n\n  Key         | Description \n  ------------|------------ \n  :root-layer | null"
 ([]
  (get-layers-for-node
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id]}]
  (get-layers-for-node
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id]}]
  (cmd/command
   connection
   "CSS"
   "getLayersForNode"
   params
   {:node-id "nodeId"})))

(s/fdef
 get-layers-for-node
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id])))
 :ret
 (s/keys
  :req-un
  [::root-layer]))

(defn
 track-computed-style-updates
 "Starts tracking the given computed styles for updates. The specified array of properties\nreplaces the one previously specified. Pass empty array to disable tracking.\nUse takeComputedStyleUpdates to retrieve the list of nodes that had properties modified.\nThe changes to computed style properties are only tracked for nodes pushed to the front-end\nby the DOM agent. If no changes to the tracked properties occur after the node has been pushed\nto the front-end, no updates will be issued for the node.\n\nParameters map keys:\n\n\n  Key                  | Description \n  ---------------------|------------ \n  :properties-to-track | null"
 ([]
  (track-computed-style-updates
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [properties-to-track]}]
  (track-computed-style-updates
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [properties-to-track]}]
  (cmd/command
   connection
   "CSS"
   "trackComputedStyleUpdates"
   params
   {:properties-to-track "propertiesToTrack"})))

(s/fdef
 track-computed-style-updates
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::properties-to-track]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::properties-to-track])))
 :ret
 (s/keys))

(defn
 take-computed-style-updates
 "Polls the next batch of computed style updates.\n\nReturn map keys:\n\n\n  Key       | Description \n  ----------|------------ \n  :node-ids | The list of node Ids that have their tracked computed styles updated"
 ([]
  (take-computed-style-updates
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (take-computed-style-updates
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "takeComputedStyleUpdates"
   params
   {})))

(s/fdef
 take-computed-style-updates
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys
  :req-un
  [::node-ids]))

(defn
 set-effective-property-value-for-node
 "Find a rule with the given active property for the given node and set the new value for this\nproperty\n\nParameters map keys:\n\n\n  Key            | Description \n  ---------------|------------ \n  :node-id       | The element id for which to set property.\n  :property-name | null\n  :value         | null"
 ([]
  (set-effective-property-value-for-node
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [node-id property-name value]}]
  (set-effective-property-value-for-node
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [node-id property-name value]}]
  (cmd/command
   connection
   "CSS"
   "setEffectivePropertyValueForNode"
   params
   {:node-id "nodeId", :property-name "propertyName", :value "value"})))

(s/fdef
 set-effective-property-value-for-node
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::node-id
     ::property-name
     ::value]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::node-id
     ::property-name
     ::value])))
 :ret
 (s/keys))

(defn
 set-keyframe-key
 "Modifies the keyframe rule key text.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n  :range          | null\n  :key-text       | null\n\nReturn map keys:\n\n\n  Key       | Description \n  ----------|------------ \n  :key-text | The resulting key text after modification."
 ([]
  (set-keyframe-key
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id range key-text]}]
  (set-keyframe-key
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id range key-text]}]
  (cmd/command
   connection
   "CSS"
   "setKeyframeKey"
   params
   {:style-sheet-id "styleSheetId",
    :range "range",
    :key-text "keyText"})))

(s/fdef
 set-keyframe-key
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::key-text]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::key-text])))
 :ret
 (s/keys
  :req-un
  [::key-text]))

(defn
 set-media-text
 "Modifies the rule selector.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n  :range          | null\n  :text           | null\n\nReturn map keys:\n\n\n  Key    | Description \n  -------|------------ \n  :media | The resulting CSS media rule after modification."
 ([]
  (set-media-text
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id range text]}]
  (set-media-text
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id range text]}]
  (cmd/command
   connection
   "CSS"
   "setMediaText"
   params
   {:style-sheet-id "styleSheetId", :range "range", :text "text"})))

(s/fdef
 set-media-text
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::text]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::text])))
 :ret
 (s/keys
  :req-un
  [::media]))

(defn
 set-container-query-text
 "Modifies the expression of a container query.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n  :range          | null\n  :text           | null\n\nReturn map keys:\n\n\n  Key              | Description \n  -----------------|------------ \n  :container-query | The resulting CSS container query rule after modification."
 ([]
  (set-container-query-text
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id range text]}]
  (set-container-query-text
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id range text]}]
  (cmd/command
   connection
   "CSS"
   "setContainerQueryText"
   params
   {:style-sheet-id "styleSheetId", :range "range", :text "text"})))

(s/fdef
 set-container-query-text
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::text]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::text])))
 :ret
 (s/keys
  :req-un
  [::container-query]))

(defn
 set-supports-text
 "Modifies the expression of a supports at-rule.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n  :range          | null\n  :text           | null\n\nReturn map keys:\n\n\n  Key       | Description \n  ----------|------------ \n  :supports | The resulting CSS Supports rule after modification."
 ([]
  (set-supports-text
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id range text]}]
  (set-supports-text
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id range text]}]
  (cmd/command
   connection
   "CSS"
   "setSupportsText"
   params
   {:style-sheet-id "styleSheetId", :range "range", :text "text"})))

(s/fdef
 set-supports-text
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::text]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::text])))
 :ret
 (s/keys
  :req-un
  [::supports]))

(defn
 set-rule-selector
 "Modifies the rule selector.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n  :range          | null\n  :selector       | null\n\nReturn map keys:\n\n\n  Key            | Description \n  ---------------|------------ \n  :selector-list | The resulting selector list after modification."
 ([]
  (set-rule-selector
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id range selector]}]
  (set-rule-selector
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id range selector]}]
  (cmd/command
   connection
   "CSS"
   "setRuleSelector"
   params
   {:style-sheet-id "styleSheetId",
    :range "range",
    :selector "selector"})))

(s/fdef
 set-rule-selector
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::selector]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::range
     ::selector])))
 :ret
 (s/keys
  :req-un
  [::selector-list]))

(defn
 set-style-sheet-text
 "Sets the new stylesheet text.\n\nParameters map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :style-sheet-id | null\n  :text           | null\n\nReturn map keys:\n\n\n  Key             | Description \n  ----------------|------------ \n  :source-map-url | URL of source map associated with script (if any). (optional)"
 ([]
  (set-style-sheet-text
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [style-sheet-id text]}]
  (set-style-sheet-text
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [style-sheet-id text]}]
  (cmd/command
   connection
   "CSS"
   "setStyleSheetText"
   params
   {:style-sheet-id "styleSheetId", :text "text"})))

(s/fdef
 set-style-sheet-text
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::text]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::style-sheet-id
     ::text])))
 :ret
 (s/keys
  :opt-un
  [::source-map-url]))

(defn
 set-style-texts
 "Applies specified style edits one after another in the given order.\n\nParameters map keys:\n\n\n  Key    | Description \n  -------|------------ \n  :edits | null\n\nReturn map keys:\n\n\n  Key     | Description \n  --------|------------ \n  :styles | The resulting styles after modification."
 ([]
  (set-style-texts
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [edits]}]
  (set-style-texts
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [edits]}]
  (cmd/command
   connection
   "CSS"
   "setStyleTexts"
   params
   {:edits "edits"})))

(s/fdef
 set-style-texts
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::edits]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::edits])))
 :ret
 (s/keys
  :req-un
  [::styles]))

(defn
 start-rule-usage-tracking
 "Enables the selector recording."
 ([]
  (start-rule-usage-tracking
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (start-rule-usage-tracking
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "startRuleUsageTracking"
   params
   {})))

(s/fdef
 start-rule-usage-tracking
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys))

(defn
 stop-rule-usage-tracking
 "Stop tracking rule usage and return the list of rules that were used since last call to\n`takeCoverageDelta` (or since start of coverage instrumentation)\n\nReturn map keys:\n\n\n  Key         | Description \n  ------------|------------ \n  :rule-usage | null"
 ([]
  (stop-rule-usage-tracking
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (stop-rule-usage-tracking
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "stopRuleUsageTracking"
   params
   {})))

(s/fdef
 stop-rule-usage-tracking
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys
  :req-un
  [::rule-usage]))

(defn
 take-coverage-delta
 "Obtain list of rules that became used since last call to this method (or since start of coverage\ninstrumentation)\n\nReturn map keys:\n\n\n  Key        | Description \n  -----------|------------ \n  :coverage  | null\n  :timestamp | Monotonically increasing time, in seconds."
 ([]
  (take-coverage-delta
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (take-coverage-delta
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "CSS"
   "takeCoverageDelta"
   params
   {})))

(s/fdef
 take-coverage-delta
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat :params (s/keys))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys)))
 :ret
 (s/keys
  :req-un
  [::coverage
   ::timestamp]))

(defn
 set-local-fonts-enabled
 "Enables/disables rendering of local CSS fonts (enabled by default).\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :enabled | Whether rendering of local fonts is enabled."
 ([]
  (set-local-fonts-enabled
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [enabled]}]
  (set-local-fonts-enabled
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [enabled]}]
  (cmd/command
   connection
   "CSS"
   "setLocalFontsEnabled"
   params
   {:enabled "enabled"})))

(s/fdef
 set-local-fonts-enabled
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::enabled]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::enabled])))
 :ret
 (s/keys))
