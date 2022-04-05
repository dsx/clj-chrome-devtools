(ns clj-chrome-devtools.commands.runtime
  "Runtime domain exposes JavaScript runtime by means of remote evaluation and mirror objects.\nEvaluation results are returned as mirror object that expose object type, string representation\nand unique identifier that can be used for further object reference. Original objects are\nmaintained in memory unless they are either explicitly released or are released along with the\nother objects in their object group."
  (:require [clojure.spec.alpha :as s]
            [clj-chrome-devtools.impl.command :as cmd]
            [clj-chrome-devtools.impl.connection :as c]))

(s/def
 ::script-id
 string?)

(s/def
 ::remote-object-id
 string?)

(s/def
 ::unserializable-value
 string?)

(s/def
 ::remote-object
 (s/keys
  :req-un
  [::type]
  :opt-un
  [::subtype
   ::class-name
   ::value
   ::unserializable-value
   ::description
   ::object-id
   ::preview
   ::custom-preview]))

(s/def
 ::custom-preview
 (s/keys
  :req-un
  [::header]
  :opt-un
  [::body-getter-id]))

(s/def
 ::object-preview
 (s/keys
  :req-un
  [::type
   ::overflow
   ::properties]
  :opt-un
  [::subtype
   ::description
   ::entries]))

(s/def
 ::property-preview
 (s/keys
  :req-un
  [::name
   ::type]
  :opt-un
  [::value
   ::value-preview
   ::subtype]))

(s/def
 ::entry-preview
 (s/keys
  :req-un
  [::value]
  :opt-un
  [::key]))

(s/def
 ::property-descriptor
 (s/keys
  :req-un
  [::name
   ::configurable
   ::enumerable]
  :opt-un
  [::value
   ::writable
   ::get
   ::set
   ::was-thrown
   ::is-own
   ::symbol]))

(s/def
 ::internal-property-descriptor
 (s/keys
  :req-un
  [::name]
  :opt-un
  [::value]))

(s/def
 ::private-property-descriptor
 (s/keys
  :req-un
  [::name]
  :opt-un
  [::value
   ::get
   ::set]))

(s/def
 ::call-argument
 (s/keys
  :opt-un
  [::value
   ::unserializable-value
   ::object-id]))

(s/def
 ::execution-context-id
 integer?)

(s/def
 ::execution-context-description
 (s/keys
  :req-un
  [::id
   ::origin
   ::name
   ::unique-id]
  :opt-un
  [::aux-data]))

(s/def
 ::exception-details
 (s/keys
  :req-un
  [::exception-id
   ::text
   ::line-number
   ::column-number]
  :opt-un
  [::script-id
   ::url
   ::stack-trace
   ::exception
   ::execution-context-id
   ::exception-meta-data]))

(s/def
 ::timestamp
 number?)

(s/def
 ::time-delta
 number?)

(s/def
 ::call-frame
 (s/keys
  :req-un
  [::function-name
   ::script-id
   ::url
   ::line-number
   ::column-number]))

(s/def
 ::stack-trace
 (s/keys
  :req-un
  [::call-frames]
  :opt-un
  [::description
   ::parent
   ::parent-id]))

(s/def
 ::unique-debugger-id
 string?)

(s/def
 ::stack-trace-id
 (s/keys
  :req-un
  [::id]
  :opt-un
  [::debugger-id]))
(defn
 await-promise
 "Add handler to promise with given promise object id.\n\nParameters map keys:\n\n\n  Key                | Description \n  -------------------|------------ \n  :promise-object-id | Identifier of the promise.\n  :return-by-value   | Whether the result is expected to be a JSON object that should be sent by value. (optional)\n  :generate-preview  | Whether preview should be generated for the result. (optional)\n\nReturn map keys:\n\n\n  Key                | Description \n  -------------------|------------ \n  :result            | Promise result. Will contain rejected value if promise was rejected.\n  :exception-details | Exception details if stack strace is available. (optional)"
 ([]
  (await-promise
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys [promise-object-id return-by-value generate-preview]}]
  (await-promise
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys [promise-object-id return-by-value generate-preview]}]
  (cmd/command
   connection
   "Runtime"
   "awaitPromise"
   params
   {:promise-object-id "promiseObjectId",
    :return-by-value "returnByValue",
    :generate-preview "generatePreview"})))

(s/fdef
 await-promise
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::promise-object-id]
    :opt-un
    [::return-by-value
     ::generate-preview]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::promise-object-id]
    :opt-un
    [::return-by-value
     ::generate-preview])))
 :ret
 (s/keys
  :req-un
  [::result]
  :opt-un
  [::exception-details]))

(defn
 call-function-on
 "Calls function with given declaration on the given object. Object group of the result is\ninherited from the target object.\n\nParameters map keys:\n\n\n  Key                   | Description \n  ----------------------|------------ \n  :function-declaration | Declaration of the function to call.\n  :object-id            | Identifier of the object to call function on. Either objectId or executionContextId should\nbe specified. (optional)\n  :arguments            | Call arguments. All call arguments must belong to the same JavaScript world as the target\nobject. (optional)\n  :silent               | In silent mode exceptions thrown during evaluation are not reported and do not pause\nexecution. Overrides `setPauseOnException` state. (optional)\n  :return-by-value      | Whether the result is expected to be a JSON object which should be sent by value. (optional)\n  :generate-preview     | Whether preview should be generated for the result. (optional)\n  :user-gesture         | Whether execution should be treated as initiated by user in the UI. (optional)\n  :await-promise        | Whether execution should `await` for resulting value and return once awaited promise is\nresolved. (optional)\n  :execution-context-id | Specifies execution context which global object will be used to call function on. Either\nexecutionContextId or objectId should be specified. (optional)\n  :object-group         | Symbolic group name that can be used to release multiple objects. If objectGroup is not\nspecified and objectId is, objectGroup will be inherited from object. (optional)\n  :throw-on-side-effect | Whether to throw an exception if side effect cannot be ruled out during evaluation. (optional)\n\nReturn map keys:\n\n\n  Key                | Description \n  -------------------|------------ \n  :result            | Call result.\n  :exception-details | Exception details. (optional)"
 ([]
  (call-function-on
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys
    [function-declaration
     object-id
     arguments
     silent
     return-by-value
     generate-preview
     user-gesture
     await-promise
     execution-context-id
     object-group
     throw-on-side-effect]}]
  (call-function-on
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys
    [function-declaration
     object-id
     arguments
     silent
     return-by-value
     generate-preview
     user-gesture
     await-promise
     execution-context-id
     object-group
     throw-on-side-effect]}]
  (cmd/command
   connection
   "Runtime"
   "callFunctionOn"
   params
   {:object-group "objectGroup",
    :object-id "objectId",
    :silent "silent",
    :arguments "arguments",
    :throw-on-side-effect "throwOnSideEffect",
    :await-promise "awaitPromise",
    :function-declaration "functionDeclaration",
    :return-by-value "returnByValue",
    :execution-context-id "executionContextId",
    :generate-preview "generatePreview",
    :user-gesture "userGesture"})))

(s/fdef
 call-function-on
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::function-declaration]
    :opt-un
    [::object-id
     ::arguments
     ::silent
     ::return-by-value
     ::generate-preview
     ::user-gesture
     ::await-promise
     ::execution-context-id
     ::object-group
     ::throw-on-side-effect]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::function-declaration]
    :opt-un
    [::object-id
     ::arguments
     ::silent
     ::return-by-value
     ::generate-preview
     ::user-gesture
     ::await-promise
     ::execution-context-id
     ::object-group
     ::throw-on-side-effect])))
 :ret
 (s/keys
  :req-un
  [::result]
  :opt-un
  [::exception-details]))

(defn
 compile-script
 "Compiles expression.\n\nParameters map keys:\n\n\n  Key                   | Description \n  ----------------------|------------ \n  :expression           | Expression to compile.\n  :source-url           | Source url to be set for the script.\n  :persist-script       | Specifies whether the compiled script should be persisted.\n  :execution-context-id | Specifies in which execution context to perform script run. If the parameter is omitted the\nevaluation will be performed in the context of the inspected page. (optional)\n\nReturn map keys:\n\n\n  Key                | Description \n  -------------------|------------ \n  :script-id         | Id of the script. (optional)\n  :exception-details | Exception details. (optional)"
 ([]
  (compile-script
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys [expression source-url persist-script execution-context-id]}]
  (compile-script
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys [expression source-url persist-script execution-context-id]}]
  (cmd/command
   connection
   "Runtime"
   "compileScript"
   params
   {:expression "expression",
    :source-url "sourceURL",
    :persist-script "persistScript",
    :execution-context-id "executionContextId"})))

(s/fdef
 compile-script
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::expression
     ::source-url
     ::persist-script]
    :opt-un
    [::execution-context-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::expression
     ::source-url
     ::persist-script]
    :opt-un
    [::execution-context-id])))
 :ret
 (s/keys
  :opt-un
  [::script-id
   ::exception-details]))

(defn
 disable
 "Disables reporting of execution contexts creation."
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
   "Runtime"
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
 discard-console-entries
 "Discards collected exceptions and console API calls."
 ([]
  (discard-console-entries
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (discard-console-entries
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "Runtime"
   "discardConsoleEntries"
   params
   {})))

(s/fdef
 discard-console-entries
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
 "Enables reporting of execution contexts creation by means of `executionContextCreated` event.\nWhen the reporting gets enabled the event will be sent immediately for each existing execution\ncontext."
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
   "Runtime"
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
 evaluate
 "Evaluates expression on global object.\n\nParameters map keys:\n\n\n  Key                               | Description \n  ----------------------------------|------------ \n  :expression                       | Expression to evaluate.\n  :object-group                     | Symbolic group name that can be used to release multiple objects. (optional)\n  :include-command-line-api         | Determines whether Command Line API should be available during the evaluation. (optional)\n  :silent                           | In silent mode exceptions thrown during evaluation are not reported and do not pause\nexecution. Overrides `setPauseOnException` state. (optional)\n  :context-id                       | Specifies in which execution context to perform evaluation. If the parameter is omitted the\nevaluation will be performed in the context of the inspected page.\nThis is mutually exclusive with `uniqueContextId`, which offers an\nalternative way to identify the execution context that is more reliable\nin a multi-process environment. (optional)\n  :return-by-value                  | Whether the result is expected to be a JSON object that should be sent by value. (optional)\n  :generate-preview                 | Whether preview should be generated for the result. (optional)\n  :user-gesture                     | Whether execution should be treated as initiated by user in the UI. (optional)\n  :await-promise                    | Whether execution should `await` for resulting value and return once awaited promise is\nresolved. (optional)\n  :throw-on-side-effect             | Whether to throw an exception if side effect cannot be ruled out during evaluation.\nThis implies `disableBreaks` below. (optional)\n  :timeout                          | Terminate execution after timing out (number of milliseconds). (optional)\n  :disable-breaks                   | Disable breakpoints during execution. (optional)\n  :repl-mode                        | Setting this flag to true enables `let` re-declaration and top-level `await`.\nNote that `let` variables can only be re-declared if they originate from\n`replMode` themselves. (optional)\n  :allow-unsafe-eval-blocked-by-csp | The Content Security Policy (CSP) for the target might block 'unsafe-eval'\nwhich includes eval(), Function(), setTimeout() and setInterval()\nwhen called with non-callable arguments. This flag bypasses CSP for this\nevaluation and allows unsafe-eval. Defaults to true. (optional)\n  :unique-context-id                | An alternative way to specify the execution context to evaluate in.\nCompared to contextId that may be reused across processes, this is guaranteed to be\nsystem-unique, so it can be used to prevent accidental evaluation of the expression\nin context different than intended (e.g. as a result of navigation across process\nboundaries).\nThis is mutually exclusive with `contextId`. (optional)\n\nReturn map keys:\n\n\n  Key                | Description \n  -------------------|------------ \n  :result            | Evaluation result.\n  :exception-details | Exception details. (optional)"
 ([]
  (evaluate
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys
    [expression
     object-group
     include-command-line-api
     silent
     context-id
     return-by-value
     generate-preview
     user-gesture
     await-promise
     throw-on-side-effect
     timeout
     disable-breaks
     repl-mode
     allow-unsafe-eval-blocked-by-csp
     unique-context-id]}]
  (evaluate
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys
    [expression
     object-group
     include-command-line-api
     silent
     context-id
     return-by-value
     generate-preview
     user-gesture
     await-promise
     throw-on-side-effect
     timeout
     disable-breaks
     repl-mode
     allow-unsafe-eval-blocked-by-csp
     unique-context-id]}]
  (cmd/command
   connection
   "Runtime"
   "evaluate"
   params
   {:object-group "objectGroup",
    :expression "expression",
    :silent "silent",
    :repl-mode "replMode",
    :throw-on-side-effect "throwOnSideEffect",
    :await-promise "awaitPromise",
    :allow-unsafe-eval-blocked-by-csp "allowUnsafeEvalBlockedByCSP",
    :unique-context-id "uniqueContextId",
    :return-by-value "returnByValue",
    :disable-breaks "disableBreaks",
    :timeout "timeout",
    :generate-preview "generatePreview",
    :include-command-line-api "includeCommandLineAPI",
    :user-gesture "userGesture",
    :context-id "contextId"})))

(s/fdef
 evaluate
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::expression]
    :opt-un
    [::object-group
     ::include-command-line-api
     ::silent
     ::context-id
     ::return-by-value
     ::generate-preview
     ::user-gesture
     ::await-promise
     ::throw-on-side-effect
     ::timeout
     ::disable-breaks
     ::repl-mode
     ::allow-unsafe-eval-blocked-by-csp
     ::unique-context-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::expression]
    :opt-un
    [::object-group
     ::include-command-line-api
     ::silent
     ::context-id
     ::return-by-value
     ::generate-preview
     ::user-gesture
     ::await-promise
     ::throw-on-side-effect
     ::timeout
     ::disable-breaks
     ::repl-mode
     ::allow-unsafe-eval-blocked-by-csp
     ::unique-context-id])))
 :ret
 (s/keys
  :req-un
  [::result]
  :opt-un
  [::exception-details]))

(defn
 get-isolate-id
 "Returns the isolate id.\n\nReturn map keys:\n\n\n  Key | Description \n  ----|------------ \n  :id | The isolate id."
 ([]
  (get-isolate-id
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (get-isolate-id
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "Runtime"
   "getIsolateId"
   params
   {})))

(s/fdef
 get-isolate-id
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
  [::id]))

(defn
 get-heap-usage
 "Returns the JavaScript heap usage.\nIt is the total usage of the corresponding isolate not scoped to a particular Runtime.\n\nReturn map keys:\n\n\n  Key         | Description \n  ------------|------------ \n  :used-size  | Used heap size in bytes.\n  :total-size | Allocated heap size in bytes."
 ([]
  (get-heap-usage
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (get-heap-usage
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "Runtime"
   "getHeapUsage"
   params
   {})))

(s/fdef
 get-heap-usage
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
  [::used-size
   ::total-size]))

(defn
 get-properties
 "Returns properties of a given object. Object group of the result is inherited from the target\nobject.\n\nParameters map keys:\n\n\n  Key                          | Description \n  -----------------------------|------------ \n  :object-id                   | Identifier of the object to return properties for.\n  :own-properties              | If true, returns properties belonging only to the element itself, not to its prototype\nchain. (optional)\n  :accessor-properties-only    | If true, returns accessor properties (with getter/setter) only; internal properties are not\nreturned either. (optional)\n  :generate-preview            | Whether preview should be generated for the results. (optional)\n  :non-indexed-properties-only | If true, returns non-indexed properties only. (optional)\n\nReturn map keys:\n\n\n  Key                  | Description \n  ---------------------|------------ \n  :result              | Object properties.\n  :internal-properties | Internal object properties (only of the element itself). (optional)\n  :private-properties  | Object private properties. (optional)\n  :exception-details   | Exception details. (optional)"
 ([]
  (get-properties
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys
    [object-id
     own-properties
     accessor-properties-only
     generate-preview
     non-indexed-properties-only]}]
  (get-properties
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys
    [object-id
     own-properties
     accessor-properties-only
     generate-preview
     non-indexed-properties-only]}]
  (cmd/command
   connection
   "Runtime"
   "getProperties"
   params
   {:object-id "objectId",
    :own-properties "ownProperties",
    :accessor-properties-only "accessorPropertiesOnly",
    :generate-preview "generatePreview",
    :non-indexed-properties-only "nonIndexedPropertiesOnly"})))

(s/fdef
 get-properties
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::object-id]
    :opt-un
    [::own-properties
     ::accessor-properties-only
     ::generate-preview
     ::non-indexed-properties-only]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::object-id]
    :opt-un
    [::own-properties
     ::accessor-properties-only
     ::generate-preview
     ::non-indexed-properties-only])))
 :ret
 (s/keys
  :req-un
  [::result]
  :opt-un
  [::internal-properties
   ::private-properties
   ::exception-details]))

(defn
 global-lexical-scope-names
 "Returns all let, const and class variables from global scope.\n\nParameters map keys:\n\n\n  Key                   | Description \n  ----------------------|------------ \n  :execution-context-id | Specifies in which execution context to lookup global scope variables. (optional)\n\nReturn map keys:\n\n\n  Key    | Description \n  -------|------------ \n  :names | null"
 ([]
  (global-lexical-scope-names
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [execution-context-id]}]
  (global-lexical-scope-names
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [execution-context-id]}]
  (cmd/command
   connection
   "Runtime"
   "globalLexicalScopeNames"
   params
   {:execution-context-id "executionContextId"})))

(s/fdef
 global-lexical-scope-names
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :opt-un
    [::execution-context-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :opt-un
    [::execution-context-id])))
 :ret
 (s/keys
  :req-un
  [::names]))

(defn
 query-objects
 "\n\nParameters map keys:\n\n\n  Key                  | Description \n  ---------------------|------------ \n  :prototype-object-id | Identifier of the prototype to return objects for.\n  :object-group        | Symbolic group name that can be used to release the results. (optional)\n\nReturn map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :objects | Array with objects."
 ([]
  (query-objects
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [prototype-object-id object-group]}]
  (query-objects
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [prototype-object-id object-group]}]
  (cmd/command
   connection
   "Runtime"
   "queryObjects"
   params
   {:prototype-object-id "prototypeObjectId",
    :object-group "objectGroup"})))

(s/fdef
 query-objects
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::prototype-object-id]
    :opt-un
    [::object-group]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::prototype-object-id]
    :opt-un
    [::object-group])))
 :ret
 (s/keys
  :req-un
  [::objects]))

(defn
 release-object
 "Releases remote object with given id.\n\nParameters map keys:\n\n\n  Key        | Description \n  -----------|------------ \n  :object-id | Identifier of the object to release."
 ([]
  (release-object
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [object-id]}]
  (release-object
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [object-id]}]
  (cmd/command
   connection
   "Runtime"
   "releaseObject"
   params
   {:object-id "objectId"})))

(s/fdef
 release-object
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::object-id]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::object-id])))
 :ret
 (s/keys))

(defn
 release-object-group
 "Releases all remote objects that belong to a given group.\n\nParameters map keys:\n\n\n  Key           | Description \n  --------------|------------ \n  :object-group | Symbolic object group name."
 ([]
  (release-object-group
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [object-group]}]
  (release-object-group
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [object-group]}]
  (cmd/command
   connection
   "Runtime"
   "releaseObjectGroup"
   params
   {:object-group "objectGroup"})))

(s/fdef
 release-object-group
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::object-group]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::object-group])))
 :ret
 (s/keys))

(defn
 run-if-waiting-for-debugger
 "Tells inspected instance to run if it was waiting for debugger to attach."
 ([]
  (run-if-waiting-for-debugger
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (run-if-waiting-for-debugger
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "Runtime"
   "runIfWaitingForDebugger"
   params
   {})))

(s/fdef
 run-if-waiting-for-debugger
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
 run-script
 "Runs script with given id in a given context.\n\nParameters map keys:\n\n\n  Key                       | Description \n  --------------------------|------------ \n  :script-id                | Id of the script to run.\n  :execution-context-id     | Specifies in which execution context to perform script run. If the parameter is omitted the\nevaluation will be performed in the context of the inspected page. (optional)\n  :object-group             | Symbolic group name that can be used to release multiple objects. (optional)\n  :silent                   | In silent mode exceptions thrown during evaluation are not reported and do not pause\nexecution. Overrides `setPauseOnException` state. (optional)\n  :include-command-line-api | Determines whether Command Line API should be available during the evaluation. (optional)\n  :return-by-value          | Whether the result is expected to be a JSON object which should be sent by value. (optional)\n  :generate-preview         | Whether preview should be generated for the result. (optional)\n  :await-promise            | Whether execution should `await` for resulting value and return once awaited promise is\nresolved. (optional)\n\nReturn map keys:\n\n\n  Key                | Description \n  -------------------|------------ \n  :result            | Run result.\n  :exception-details | Exception details. (optional)"
 ([]
  (run-script
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys
    [script-id
     execution-context-id
     object-group
     silent
     include-command-line-api
     return-by-value
     generate-preview
     await-promise]}]
  (run-script
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys
    [script-id
     execution-context-id
     object-group
     silent
     include-command-line-api
     return-by-value
     generate-preview
     await-promise]}]
  (cmd/command
   connection
   "Runtime"
   "runScript"
   params
   {:script-id "scriptId",
    :execution-context-id "executionContextId",
    :object-group "objectGroup",
    :silent "silent",
    :include-command-line-api "includeCommandLineAPI",
    :return-by-value "returnByValue",
    :generate-preview "generatePreview",
    :await-promise "awaitPromise"})))

(s/fdef
 run-script
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::script-id]
    :opt-un
    [::execution-context-id
     ::object-group
     ::silent
     ::include-command-line-api
     ::return-by-value
     ::generate-preview
     ::await-promise]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::script-id]
    :opt-un
    [::execution-context-id
     ::object-group
     ::silent
     ::include-command-line-api
     ::return-by-value
     ::generate-preview
     ::await-promise])))
 :ret
 (s/keys
  :req-un
  [::result]
  :opt-un
  [::exception-details]))

(defn
 set-async-call-stack-depth
 "Enables or disables async call stacks tracking.\n\nParameters map keys:\n\n\n  Key        | Description \n  -----------|------------ \n  :max-depth | Maximum depth of async call stacks. Setting to `0` will effectively disable collecting async\ncall stacks (default)."
 ([]
  (set-async-call-stack-depth
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [max-depth]}]
  (set-async-call-stack-depth
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [max-depth]}]
  (cmd/command
   connection
   "Runtime"
   "setAsyncCallStackDepth"
   params
   {:max-depth "maxDepth"})))

(s/fdef
 set-async-call-stack-depth
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::max-depth]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::max-depth])))
 :ret
 (s/keys))

(defn
 set-custom-object-formatter-enabled
 "\n\nParameters map keys:\n\n\n  Key      | Description \n  ---------|------------ \n  :enabled | null"
 ([]
  (set-custom-object-formatter-enabled
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [enabled]}]
  (set-custom-object-formatter-enabled
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [enabled]}]
  (cmd/command
   connection
   "Runtime"
   "setCustomObjectFormatterEnabled"
   params
   {:enabled "enabled"})))

(s/fdef
 set-custom-object-formatter-enabled
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

(defn
 set-max-call-stack-size-to-capture
 "\n\nParameters map keys:\n\n\n  Key   | Description \n  ------|------------ \n  :size | null"
 ([]
  (set-max-call-stack-size-to-capture
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [size]}]
  (set-max-call-stack-size-to-capture
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [size]}]
  (cmd/command
   connection
   "Runtime"
   "setMaxCallStackSizeToCapture"
   params
   {:size "size"})))

(s/fdef
 set-max-call-stack-size-to-capture
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::size]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::size])))
 :ret
 (s/keys))

(defn
 terminate-execution
 "Terminate current or next JavaScript execution.\nWill cancel the termination when the outer-most script execution ends."
 ([]
  (terminate-execution
   (c/get-current-connection)
   {}))
 ([{:as params, :keys []}]
  (terminate-execution
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys []}]
  (cmd/command
   connection
   "Runtime"
   "terminateExecution"
   params
   {})))

(s/fdef
 terminate-execution
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
 add-binding
 "If executionContextId is empty, adds binding with the given name on the\nglobal objects of all inspected contexts, including those created later,\nbindings survive reloads.\nBinding function takes exactly one argument, this argument should be string,\nin case of any other input, function throws an exception.\nEach binding function call produces Runtime.bindingCalled notification.\n\nParameters map keys:\n\n\n  Key                     | Description \n  ------------------------|------------ \n  :name                   | null\n  :execution-context-id   | If specified, the binding would only be exposed to the specified\nexecution context. If omitted and `executionContextName` is not set,\nthe binding is exposed to all execution contexts of the target.\nThis parameter is mutually exclusive with `executionContextName`.\nDeprecated in favor of `executionContextName` due to an unclear use case\nand bugs in implementation (crbug.com/1169639). `executionContextId` will be\nremoved in the future. (optional)\n  :execution-context-name | If specified, the binding is exposed to the executionContext with\nmatching name, even for contexts created after the binding is added.\nSee also `ExecutionContext.name` and `worldName` parameter to\n`Page.addScriptToEvaluateOnNewDocument`.\nThis parameter is mutually exclusive with `executionContextId`. (optional)"
 ([]
  (add-binding
   (c/get-current-connection)
   {}))
 ([{:as params,
    :keys [name execution-context-id execution-context-name]}]
  (add-binding
   (c/get-current-connection)
   params))
 ([connection
   {:as params,
    :keys [name execution-context-id execution-context-name]}]
  (cmd/command
   connection
   "Runtime"
   "addBinding"
   params
   {:name "name",
    :execution-context-id "executionContextId",
    :execution-context-name "executionContextName"})))

(s/fdef
 add-binding
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::name]
    :opt-un
    [::execution-context-id
     ::execution-context-name]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::name]
    :opt-un
    [::execution-context-id
     ::execution-context-name])))
 :ret
 (s/keys))

(defn
 remove-binding
 "This method does not remove binding function from global object but\nunsubscribes current runtime agent from Runtime.bindingCalled notifications.\n\nParameters map keys:\n\n\n  Key   | Description \n  ------|------------ \n  :name | null"
 ([]
  (remove-binding
   (c/get-current-connection)
   {}))
 ([{:as params, :keys [name]}]
  (remove-binding
   (c/get-current-connection)
   params))
 ([connection {:as params, :keys [name]}]
  (cmd/command
   connection
   "Runtime"
   "removeBinding"
   params
   {:name "name"})))

(s/fdef
 remove-binding
 :args
 (s/or
  :no-args
  (s/cat)
  :just-params
  (s/cat
   :params
   (s/keys
    :req-un
    [::name]))
  :connection-and-params
  (s/cat
   :connection
   (s/?
    c/connection?)
   :params
   (s/keys
    :req-un
    [::name])))
 :ret
 (s/keys))
