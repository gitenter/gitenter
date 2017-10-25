## API Design

One possibility is to use [HAL-formatted JSON document](http://stateless.co/hal_specification.html). It is (1) standard and will be consistent all over the site, (2) with front-end/back-end supported, but (3) kind of boilerplate.

### CORS setup

To turn on cross origin from one specific request (or without `origins` should be all possible URLs)

```
@CrossOrigin                                         // for all sites
@CrossOrigin(origins = "http://localhost:8765")      // for a specific URL
```

Or globally

?
