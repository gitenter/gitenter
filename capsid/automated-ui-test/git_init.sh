cd /home/beta/Workspace/enterovirus-dummy/client
# Setup git through git local protocol
git clone /home/beta/Workspace/enterovirus-dummy/server/org1/repo1.git
cd repo1
git remote -v

mkdir requirement

cat > requirement/R1.md <<- EOM
# Requirement-1

- [R1T1] Content of R1T1.
- [R1T2] Content of R1T2.
- [R1T3] Content of R1T3.

Bubble items which are not traceable items:

- Item 1.
- Item 2.
EOM

git add -A
git commit -m "Add requirement document R1.md"
git push origin master

cat > requirement/R2.md <<- EOM
# Requirement-2

- [R2T1] Content of R2T1.
- [R2T2]{R1T1} Content of R2T2.
- [R2T3]{R1T1,R1T2} Content of R2T3.
- [R2T4]{R2T1} Content of R2T4.
- [R2T5]{R1T1,R2T1} Content of R2T5
EOM

git add -A
git commit -m "Add requirement document R2.md"
git push origin master

mkdir design

cat > design/D1.md <<- EOM
# Design-1

- [D1T1]{R1T1,R2T1} Content of D1T1
- [D2T2] Content of D2T2.

![](../sample.jpg "")
EOM

cp /home/beta/Workspace/enterovirus/test/files/sample.jpg .

git add -A
git commit -m "Add design document D2.md"
git push origin master
