gitclonepath=$1

clientroot=/home/beta/Workspace/enterovirus-test/fake_client
cd $clientroot

git clone $gitclonepath
cd repo1
git remote -v

mkdir doc
cd doc
mkdir requirement
mkdir design

# Commit:
# Turn off the entire system. Expect an ignored commit.
sed -i "s/enable_systemwide = on/enable_systemwide = off/g" $clientroot/repo1/gitenter.properties

git add -A
git commit -m "Turn off the entire system. Expect an ignored commit."
git push origin master

# Commit:
# Turn the system back on. Add a markdown template file (which include no traceable item).
sed -i "s/enable_systemwide = off/enable_systemwide = on/g" $clientroot/repo1/gitenter.properties

cp /home/beta/Workspace/enterovirus/test/files/sample.jpg $clientroot/repo1/doc

# Use 'EOM' rather than EOM because the content include code:
# https://stackoverflow.com/questions/22697688/how-to-cat-eof-a-file-containing-code-in-shell
cat > $clientroot/repo1/doc/markdown-template.md <<-'EOM'
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

#### Link

This [linked to google](http://www.google.com).

#### Bubble items

- Item 1.
	- nested text 1.
	- nested text 2.
- Item 2.
	1. nested ordered item
	2. nested ordered item
- **bold** started list item
- *italic* started list item
- paragraph with **bold** and *italic*

#### Ordered items

1. Item 1.
2. Item 2.

#### Image

![](sample.jpg "")

#### Code

Inline `code` has `back-ticks around` it.

```javascript
var s = "JavaScript syntax highlighting";
alert(s);
```
 
```python
s = "Python syntax highlighting"
print s
```
 
```
No language indicated, so no syntax highlighting. 
But let's throw in a <b>tag</b>.
```

#### Table

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

#### Blockquotes

> Blockquotes are very handy in email to emulate reply text.
> This line is part of the same quote.

#### Horizontal Rule

---
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

- [R1T1] Content of R1T1 with **bold** and *italic*.
- [R1T2] **Bold** started content of R1T2.
- [R1T3]{} Content of R1T3.
EOM

cat > $clientroot/repo1/doc/requirement/R2.md <<- EOM
# Requirement-2

- [R2T1] Content of R2T1.
- [R2T2]{R1T1} Content of R2T2.
- [R2T3]{R1T1,R1T2,R1T3} Content of R2T3.
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
