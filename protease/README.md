# `protease` the Persistent Layer

## Test

Currently after each time database be re-installed, tests will fail by `mvn clean install`, but will pass in Eclipse. Also, **after** tests been passed in Eclipse, they'll also pass `mvn clean install`. Need to understand why.

## TODO

### Git access layer

So right now `protease` (hence actual both the `capsid` web server and the `enzymark` parser) directly talk to the database/file system, and the Git files are shared by various web-app instances and various git instances. Although it is proven that is should work for database with load, it is maybe risky to touch the file system this way.

One problem I can see right now is if multiple web-app instances are editing the same file, it may break it.

One possible solution is to add a Git access layer for `protease` to call. It can be either through RPC (that's [what GitHub is doing](https://github.blog/2009-10-20-how-we-made-github-fast/#tracing-an-http-request), not sure if the above is the reason for them to implement it...) or a Git microservice API. Therefore, we can (1) decouple git scaling to web scaling, and (2) handle the file editing problem (maybe by providing a lock) inside of this git access layer, and `protease` doesn't need to take care of it.
