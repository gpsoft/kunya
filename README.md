# KuNya
This is a minimal Electron application featuring:

- `ClojureScript` for language
- `Boot` for build tool
- `Reagent` for view engine
- `re-frame` for framework
- `Vim` for editor
- `vim-fireplace` for interactive programming

The project is based on [martinklepsch/electron-and-clojurescript](https://github.com/martinklepsch/electron-and-clojurescript).

# Setup
## `~/.boot/boot.properties`

    BOOT_CLOJURE_NAME=org.clojure/clojure
    BOOT_CLOJURE_VERSION=1.8.0
    BOOT_VERSION=2.6.0

## `~/.boot/profile.boot`

    (require 'boot.repl)

    (swap! boot.repl/*default-dependencies*
           concat '[[cider/cider-nrepl "0.13.0"]])

    (swap! boot.repl/*default-middleware*
           conj 'cider.nrepl/cider-middleware)

This is for vim-fireplace.

# Usage

Using two terminals:

    $ boot dev

    $ electron target/

![ss](ss.png)

Then open `src/cljs/kunya/ui.cljs` with Vim, and `cpp`, `K`, `:Eval (js/alert "hoge")`, etc...

For release build:

    $ boot release

    $ electron release/

