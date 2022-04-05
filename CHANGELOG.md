## 20220405
- Major changes under the hood
- Add support for using with Babashka
- Remove jetty (use JDK11 HTTP library native WebSocket)
- Remove timbre (prefer clojure.tools.logging in libraries)
- Remove core.async (use simpler promises and callbacks)

## 20200423
- Avoid blocking IO in go-block (#27)

## 20190601
- Change test output video to APNG for better quality
- Generate videos even if cljs tests fail
- Add :loop-video? option to control looping

## 20190530.1
- Add :verbose? flag to test runner (for better troubleshooting)
- Add :ring-handler flag to test runner (if tests need to load some resources)
- Detect if test page has error before tests are started

## 20190530
- Ability to take screenshots in cljs tests (use `(js/screenshot)`)
- Combine multiple screenshots to GIF animation


## 20190529
- More reliable project.clj loading in cljs test runner

## 20190502
- Deps update release

## 20190329
- Made ws client optionally configurable

## 20180528
- Add `:no-sandbox?` option (mikkoronkkomaki)

## 20180310
- ClojureScript test runner as a Clojure test (see `clj-chrome-devtools.cljs.test` namespace)
- Moved to date based versioning

## [0.2.4] - 2018-01-04
- Fixed connection timeout (hagmonk)
- Changed generated API inflections (hagmonk)

## [0.2.3] - 2017-12-20
- Started this change log
- Improved connection handling (thanks hagmonk)
