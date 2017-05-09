# Atom Plugin Notes

## Languages

- [Less](http://lesscss.org/): an extension to add variables and functions to CSS.
- [CoffeeScript](http://coffeescript.org/): basically just JavaScript.

## General ideas

Every Atom window is essentially a locally-rendered web page, under a specialized variant of Chromium design. APIs are Node.js applications working on top of each window's JavaScript context.

Atom setups are in `~/.atom`, for which `~/.atmo/init.coffee` is the first to be evaluated when loading.

User defined functionalities can either be (1) standing alone packages, or (2) just plain code in `~/.atmo/init.coffee`.

When defining new functionalities, you (1) `atom.commands.add` and define what need to be done as a JavaScript closure, (2) may keybinding it to a keyboard shortcut if desired.

## Development environment setup

May use an Atom package called [package-generator](https://github.com/atom/package-generator). That helps (1) generate the packge folder structure, and (2) link this folder from `.atom/package/r-n-atom` which is this package's name.

For doing that, you should

1. Go do the package-generator setup page to change (1) the output from JavaScript to CoffeeScript (that matches the tutorial and nearly all other projects), and (2) choose "Create in Dev mode".
1. Generate the project.
1. Then the new package should be seen by `atom --dev`, and the source code is linked from not `.atom/package/r-n-atom` but `.atom/dev/package/r-n-atom`.

## References

1. [Atom Flight Manual](http://flight-manual.atom.io/)
1. [Building your first Atom plugin](https://github.com/blog/2231-building-your-first-atom-plugin)
