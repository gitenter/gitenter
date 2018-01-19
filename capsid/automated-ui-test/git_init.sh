clientroot=/home/beta/Workspace/enterovirus-dummy/client
cd $clientroot
# Setup git through git local protocol
git clone /home/beta/Workspace/enterovirus-dummy/server/org1/repo1.git
cd repo1
git remote -v

mkdir doc
cd doc
mkdir requirement
mkdir design

# Commit:
# Turn off the entire system. Expect an ignored commit.
sed -i "s/enable_systemwide = on/enable_systemwide = off/g" $clientroot/repo1/enterovirus.properties

git add -A
git commit -m "Turn off the entire system. Expect an ignored commit."
git push origin master

# Commit:
# Turn the system back on. Add a markdown template file (which include no traceable item).
sed -i "s/enable_systemwide = off/enable_systemwide = on/g" $clientroot/repo1/enterovirus.properties

cp /home/beta/Workspace/enterovirus/test/files/sample.jpg $clientroot/repo1/doc

cat > $clientroot/repo1/doc/markdown-template.md <<-EOM
# H1

## H2

### H3

#### H4

##### H5

###### H6

Emphasis, aka italics, with *asterisks* or _underscores_.

Strong emphasis, aka bold, with **asterisks** or __underscores__.

Combined emphasis with **asterisks and _underscores_**.

Strikethrough uses two tildes. ~~Scratch this.~~

Bubble itemss:

- Item 1.
- Item 2.

Image:

![](sample.jpg "")

Inline `code` has `back-ticks around` it.

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

Blockquotes:

> Blockquotes are very handy in email to emulate reply text.
> This line is part of the same quote.

Horizontal Rule:

---

Link:

This [linked to google](http://www.google.com).
EOM

git add -A
git commit -m "Turn the system back on. Add a markdown template file (which include no traceable item)."
git push origin master

# Commit
# Add a traceable file with wrong format. Expect an invalid commit.
cat > $clientroot/repo1/doc/wrong.md <<- EOM
# Wrong traceable file

- [tag]{not-exist} Content.
EOM

git add -A
git commit -m "Add a traceable file with wrong format. Expect an invalid commit."
git push origin master

# Commit
# Add some valid traceable files.
rm $clientroot/repo1/doc/wrong.md

cat > $clientroot/repo1/doc/requirement/R1.md <<- EOM
# Requirement-1

- [R1T1] Content of R1T1.
- [R1T2] Content of R1T2.
- [R1T3] Content of R1T3.
EOM

cat > $clientroot/repo1/doc/requirement/R2.md <<- EOM
# Requirement-2

- [R2T1] Content of R2T1.
- [R2T2]{R1T1} Content of R2T2.
- [R2T3]{R1T1,R1T2} Content of R2T3.
- [R2T4]{R2T1} Content of R2T4.
- [R2T5]{R1T1,R2T1} Content of R2T5
EOM

cat > $clientroot/repo1/doc/design/D1.md <<- EOM
# Design-1

- [D1T1]{R1T1,R2T1} Content of D1T1
- [D2T2] Content of D2T2.
EOM

git add -A
git commit -m "Add some valid traceable files."
git push origin master
