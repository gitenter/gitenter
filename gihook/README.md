# Various Git Hooks

## Test independently

The executable `.jar` will finally run as git hooks. To test independently, the easiest way is setup `Run Configurations` in eclipse to a fixed git repository.

![alt text](eclipse_run_configurations.png "")

## Deployment

Compile using `mvn clean compile assembly:single` (rather than `mvn package`) so the dependencies (`gitar`, `jgit`, and more...) will be in the same jar.

The executable `.jar` should be later moved to `capsid/src/main/resources/git-server-side-hooks` for further uses.

### Client side hook

Client side hook (`pre-commit`, ...) need to installed manually (there seems to have no way to `git clone` with hooks -- double check...). 

Add a file names `pre-commit` in the `.git/hooks` folder of the corresponding repository, make that file executable, and add the following into that file:

```
#!/bin/bash

output=$(java -jar precommit-0.0.2-prototype-jar-with-dependencies.jar)

if [ "$output" != "null" ]; then
    exit 1
fi
```
