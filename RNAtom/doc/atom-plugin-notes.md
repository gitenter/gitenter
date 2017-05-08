# Atom Plugin Notes

## Languages

- [Less](http://lesscss.org/): an extension to add variables and functions to CSS.
- [CoffeeScript](http://coffeescript.org/): basically just JavaScript.

## General ideas

Every Atom window is essentially a locally-rendered web page, under a specialized variant of Chromium design. APIs are Node.js applications working on top of each window's JavaScript context.

Atom setups are in `~/.atom`, for which `~/.atmo/init.coffee` is the first to be evaluated when loading.

User defined functionalities can either be (1) standing alone packages, or (2) just plain code in `~/.atmo/init.coffee`.

When defining new functionalities, you (1) `atom.commands.add` and define what need to be done as a JavaScript closure, (2) may keybinding it to a keyboard shortcut if desired.

## References

1. [Atom Flight Manual](http://flight-manual.atom.io/)
1. [Building your first Atom plugin](https://github.com/blog/2231-building-your-first-atom-plugin)
