Generate fake secret by

```
ssh-keygen -f validation.pem -P ""
ssh-keygen -f devhost.pem -P ""
```

Then `chef-zero` command can be run under the chef folder.
