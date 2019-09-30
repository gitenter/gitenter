# Stolon

`stolon` holds the static contents for GitEnter. It is backed by [hugo](https://gohugo.io/).

Test:

`hugo server -D` and check `localhost:1313`.

Build:

`hugo`

TODO:

+ [ ] Wonder if CSS and other static resources can be symlink or not (need to confirm their content has been *copied* to the `/public` folder)?
+ [ ] Right now it is very tricky that for every hyperlink we need to do `/subfolder/` (not `/subfolder`) so Nginx can redirect it to `/subfolder/index.html`. It is not the same behavior of `hugo server -D` and wonder if we should setup [a Nginx rewriting rule](https://stackoverflow.com/a/16640381/2467072) so they behave the same.
