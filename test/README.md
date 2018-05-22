# Test Setup

Need to setup a folder `~/Workspace/enterovirus-test` to put dummy files (mostly dummy git repositories) to run the tests.

## Ubuntu

## Mac OS

### `date`

Mac's `date` doesn't support millisecond. Need to run

```
brew install coreutils
```

so the GNU's coreutils `date` program is installed. Shell need to be replaced by `gdate`.

### `sed`

The `sed` in Mac and Linux are different. There is no one-size-fit-all solution so need to switch in code.