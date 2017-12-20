# Various Git Hooks

## Deployment

Compile using `mvn clean compile assembly:single` (rather than `mvn package`) so the dependencies (`gitar`, `jgit`, and more...) will be in the same jar.

The executable `.jar` should be later moved to `capsid/src/main/resources/git-server-side-hooks` for further uses.
