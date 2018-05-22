# `gitar`

## Compile

Currently can only work for Java 8-. There is an error in `return Collections.list(this.children());` for `GitFolderStructure` for Java 10.

Mac:

```
$ export JAVA_HOME=/Library/Java/Home
$ mvn clean install
```

## Deployment

To make other sub-projects (`capsid` and various hooks) compilable, `mvn install` of this package to the `.m2` directory is needed.
