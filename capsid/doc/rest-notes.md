# RESTful Service Notes

REST: **RE**presentational **S**tate **T**ransfer

Key design decisions/assumptions:

+ **REST architecture**: Every component is a resource.
+ Resources are identified by URIs (Uniform Resource Identifier)/global IDs.
+ HTTP protocol is used for data communication.
	+ Resources are accessible by a common interface using HTTP standard methods.
	+ Statelessness just like HTTP protocol.
+ Representation of resources may be (1) text, (2) JSON, or (3) XML.
	+ JSON is the most popular one right now.

RESTful service pros:

+ Light weight
+ Highly scalable
+ Highly maintainable

Usage: create APIs for web-based applications.

## Concepts

### Resource and representations

+ Resource:
	+ (Definition:) anything that's important enough to be referenced as a thing in itself.
	+ May be used for:
		+ Create a hypertext link on it
		+ Make or refute assertions about it
		+ Retrieve or cache a representation of it
			+ Reference into another representation
			+ Annotate it
			+ Perform other operations on it
	+ Can be stored on a computer (a.k.a, information resources)
		+ An electronic document
		+ A row in a database
		+ Result of a running algorithm
	+ Client doesn't care about resource. It only cares about URLs and representations.
+ Representation:
	+ (Definition:) A machine-readable explanation of the current state of a resource.
	+ Can be transfered back and forth
	+ A resource may have many representations
		+ Content negotiation
		+ Multiple URLs
	+ Examples:
		+ XML document
		+ JSON object
		+ A set of comma-separated values
		+ A SQL INSERT statement
+ Representational state transfer (definition): The server sends a representation describing the state of a resource. The client sends a representation describing the state it would like the resource to have.

### HTTP Protocol

The predefined protocol sending in between clients and servers in a RESTful system.

Alternative choices of HTTP:

+ Gopher protocol (RFC 1436): Similar to HTTP but without addressability.
+ FTP (RFC 959): File transfer.
	+ No machine-readable way to point a file.
	+ Long-live sessions.
+ BitTorrent: peer-to-peer protocol.
+ SSH: real-time protocol.

#### HTTP Verbs

HTTP verbs define the *protocol semantics* (for server to understand approximately what the client wants) of HTTP. HTTP verbs don't define *application semantics* (content related to the application what really need to be done).

+ `GET`: read
	+ Idempotent
	+ Safe (as a liberating promise)
	+ URI:
		+ `/para1/{__}/para2/`: Collection
		+ `/para1/{__}/para2/{__}`: Individual item
	+ Return code:
		+ 200: OK
		+ 404: NOT FOUND
		+ 400: BAD REQUEST
	+ Along with `HEAD`
+ `PUT`: update ~~(also create if the resource ID is chosen by the client, but that's not recommended)~~
	+ Idempotent but not safe.
	+ URI:
		+ `/para1/{__}/para2/{__}`: Individual item
	+ Return:
		+ Optional to return a body in the response (may waste bandwidth)
		+ Optional to return a link via Location header (since ID is known)
	+ Return code:
		+ 200: OK
		+ 204: OK but not returning any content in the body
		+ 201: OK for creation
		+ 404: NOT FOUND
			+ The ID is not found/invalid.
			+ If no ID is provided (`/para1/{__}/para2/`), should return 404 unless you want to update/replace every resource in the entire collection.
+ `POST`: create (*subordinate* resources, server assign ID)
	+ Neither idempotent nor safe.
	+ URI:
		+ `/para1/{__}/para2/`: Add to collection
	+ Return code:
		+ 201: created. Location header with link to `/para1/{__}` containing new ID. Empty body. (refer to HATEOAS minimal setting)
		+ 404: NOT FOUND
	+ In HTML (which only has GET and POST), POST is for every unsafe activity.
+ `DELETE`: delete
	+ Idempotent but not safe.
	+ URI:
		+ `/para1/{__}/para2/{__}`: Individual item
	+ Return code:
		+ 200: OK along with response body (maybe JSEND-style with (1) representation of the delete item, (2) wrapped response)
		+ 204: OK but no content/no response body is returned
		+ 404: NOT FOUND (the resource does not exist/cannot be removed)
			+ If no ID is provided (`/para1/{__}/para2/`), should return 404 unless you want to delete every resource in the entire collection -- not often desirable.
+ `HEAD`: Get the headers that would be sent along with a representation of this resource, but not the representation itself.
	+ Idempotent
	+ Safe
	+ A lightweight version of GET: May not save time, but definitely save bandwidth.
+ `OPTIONS`: Discover which HTTP methods this resource responds to.
	+ Idempotent
	+ Safe
	+ A good idea but nobody is using it. Function overlapped by hypermedia documents (good API) and human-readable documentation (poor API).
+ ~~`CONNECT` (Used with HTTP proxies only)~~
+ ~~`TRACE` (Used with HTTP proxies only)~~
	+ Idempotent
	+ Safe
+ `PATCH` (Defined not in HTTP standard, but RFC 5789)
	+ Like `PUT` but for fine-grained changes.
	+ Using a "diff" representation. May define an `op` with values such as `test`, `remove`, `add`, `replace`, `move`, `copy`, ...
	+ Reasons to use:
		+ For really large resource
		+ Avoid unintentional conflicts
	+ Neither idempotent nor safe.
+ `LINK` and `UNLINK` (Defined not in HTTP standard, but in the Internet-Draft "snell-link-
method". Used to be in RFC 2068 but later removed. Not approved by RFC yet.)
	+ Idempotent but not safe.
+ The WebDAV standard `COPY`, `MOVE`, and `LOCK` (defined by RFC 4918.)

(Idempotent: multiple identical requests has the same effect as making a single request.)

(Safe: intended only for information retrieval and should not change the state of the server. No side effects beyond logging/caching/web counter++. Safe=>idempotent. Safe=>read-only. The services must adhere to this rule. The return does not need to be the same every time.)

HTTP verbs are redundant:

+ PUT can substitute for PATCH.
+ GET can do the job of HEAD.
+ POST can substitute for anything.

Choose HTTP methods to use in an API == choose a community of clients and other components that understand this methods.

+ HTML: only uses GET and POST.
+ APIs before 2008: GET, PUT, DELETE and POST.
+ Filesystem GUI: HTTP plus WebDAV.
+ Various HTTP caches and proxies: stay away anything not in RFC 2616 include PATCH.

### Responses

+ The status code
+ The response header
	+ `Content-Type`: Body's media type=MIME type=content type
		+ `application/json`: JSON. Looks like JavaScript or Python code.
		+ `application/vnd.collection+json`: Collection+JSON document.
			+ A standard for publishing a searchable list of resources over the web.
			+ JSON plus constrains.
				+ Object need to have a property called `collection`
				+ `template` for creating a new item through HTTP POST.
				+ `data`
				+ `link`
		+ `applidation/x-www-form-urlencoded`: Used for POST
+ The entire body

*(So it is like for hypermedia type, you can choose from HAL, Collection+JSON, and others. That's really about taste.)*

### HTTP Status Codes

Only the popular ones.

+ 200: OK
+ 201: CREATED
+ 204: NO CONTENT
+ 303: See Other (for redirect)
+ 304: NOT MODIFIED
+ 400: BAD REQUEST
+ 401: UNAUTHORIZED
+ 403: FORBIDDEN
+ 404: NOT FOUND
+ 409: CONFLICT
+ 500: INTERNAL SERVER ERROR

## API Design

### Fine-grained/Microservice architecture

Concepts along with microservice:

+ Continuous delivery (Domain-Driven Design, Eric Evans)
+ Hexagonal architecture (Alistair Cockburn): opposite to layered architecture, pro: business logic cannot hide
+ On-demand Visualization: provision and resize the machine
+ Infrastructure automation
+ Small automation team
+ System at scale
+ Microservice
+ Single Responsibility Principle: Gather together those things that change for the same reason, and separate those things that change for different reasons.
+ A platform as a service (PAAS)
+ Application programming interface (API)
+ Technology Heterogeneity:  Use different technologies (programming languages, databases, ...) inside of each job.
	+ Try new technology in limited scope. So if fails, that will not impact a lot.
+ Resilience (recover quickly from difficulties) engineering
+ Service oriented architecture (SOA): multiple services collaborate to provide some end set of capability.
	+ Need to have proper
		+ Communication protocol: SOAP, ...
		+ Vendor middleware
		+ Service granularity
		+ Splitting methodology

Microservice (definition): small, autonomous services that work together.

+ Service boundary: business boundary
+ Goal: avoid a service to grow too large
+ Small (definition):
	+ Could be rewritten in two weeks
	+ Small enough and no smaller
	+ Manageable by a small team
+ Communication via network calls
	+ Enforce separation between services
	+ Avoid the perils to tight coupling
+ Golden rule: make change of a service and deploy it by itself without changing anything else.
+ May understand microservice as a specific approach of SOA
	+ Same pattern for the following: microservice=>SOA / XP=>SOA / scrum=>Agile
+ Pros:
	+ Mostly benefits around interdependency
	+ (Share with any distributed system)
		+ Technology Heterogeneity
		+ Resilience
		+ Scale every part separately. Run each part in small/less powerful hardware.
			+ As a comparison, in monolithic service everything need to be scaled together.
		+ Easy to deploy: only need to deploy the tiny microservice which is under change, rather than the entire application.
		+ Organizational alignment of otherwise large team/codebases, especially if the team itself is distributed.
		+ Composability/reuse of functionality in different ways/purposes
		+ Optimizing for replaceability
	+ (Share with any service-oriented architecture)
+ Cons:
	+ Complexity of a distributed system
		+ Hard to manage
	+ Technically harder to handling deployment
	+ Technically harder to handling testing
	+ Technically harder for monitoring
	+ distributed transaction
	+ CAP theorem
+ (The communication in between is under network rather than function calls of the same machine. As network (WORLD wide web) is already build all abound the world, it technically makes the difference.)
+ (Doing that forces people to define clearer interfaces which (solidly) cannot be changed -- that is an sociology/human natural thing.)
+ (It indeed can give us better flexibility to work on each part, the cutting (and interface design) become crucial, and doesn't share any flexibility at all.)

Decompositions similar to microservice (w/ comparison):

+ Shared libraries
	+ Cons compare to microservice:
		+ Same language/platform, so lost true technology heterogeneity.
		+ Hard to scale part.
		+ Always need to deploy the entire system (unless do dynamical link).
		+ Obvious seams/division of the system are not built into library boundary.
	+ Shared pros:
		+ Reusability
+ Modules
	+ (Definition:) modular decomposition techniques provided by specific programming languages.
		+ Java - Open Source Gateway Initiative (OSGI): A framework under Eclipse which can retrofit modules in Java.
		+ Erlang
	+ Cons:
		+ Enforce module lifecycle management without enough support in the language itself. So extra works need to be done for module isolation.

### (A General) API

APIs are distributed system under the World Wide Web (Using HTTP).

+ Public API
+ Internal API
+ API accessible by trusted partners only

Cons:

+ Once deployed, they cannot be changed. REST is a way to adapting the changes.
+ Semantic challenge: bridging the semantic gap between understanding a document's structure and understand what it means.

Alternative choices of REST:

+ Atom Publishing Protocol (2005): which nobody is using right now.
+ SOAP: Lost a standoff with REST in 2007. Only used in big company (and not for public-facing API) now.

### RESTful API

REST system components (created/developed by different people):

+ Servers
+ Clients
+ Caches
+ Proxies
+ Caching proxies
+ ...

Key components about RESTful API (mostly share with a general web service/API):

+ Resources: The thing behind a URL.
+ Representations: The document the server sends.
+ Properly use HTTP methods
+ Addressability: Name resources with URLs. Every resource should have its own URL.
+ Short sessions/Statelessness:
	+ HTTP sessions last for one request.
	+ Server and client both keep state, but different kind of state. Server doesn't care what state the client is in.
		+ Application state (definition): which URL are you on.
		+ Resource state
			+ Sending a GET doesn't change the state.
			+ Receive a POST will create a new state. No way to back to the old state.
+ Connectedness
	+ Self-descriptive message: User can make educated guesses of what is behind the link.
	+ Hypermedia/link *(The single most important aspect of REST. Otherwise it is just a functional API)*:
		+ Hypermedia as the engine of application state (HATEOAS)
		+ (May in the future) have API client which not fit to a single particular API.
		+ (May in the future) redesign of API server should not break the site.
	+ Shouldn't have a separate human-readable document describe how to construct the URL.

Concept related to RESTful API:

+ Antipattern: *?*
+ Breadth-first search: *?*

### (Theoretical) Design principles of a RESTful Service

REST is a set of **design constraints**. REST is neither a protool, nor a file format, nor a development framework.

Patterns/rules are always conflict to each other, as they are defined in different time. Rules to fit "tomorrow" is not possible.

*(Different approach: SOAP (Simple Object Access Protocol), more straightforward, not a lot of design.)*

Fielding constrains (by Roy T. Fielding):

+ Uniform Interface: simplify, decouple.
	+ Resource-based: Using URIs as resource identifiers for **individual** resources. Separate from the representations.
	+ Manipulation of Resources Through Representations: Client (hold representation) has enough information to modify/delete resources.
	+ Self-descriptive Messages: Message describes how to process the message. Response indicates its cache-ability.
	+ Hypermedia as the Engine of Application State (HATEOAS)
		+ **hypermedia** (definition):
			+ Clients deliver state via body contents, query-string parameters, request headers and the requested URI (the resource name).
			+ Services deliver state to clients via body content, response codes, and response headers.
		+ Links are contained in the returned body (or headers) for (1) itself, and (2) related objects.
+ Stateless:
	+ State is contained within request itself so need to be transfered back and force (Contradict with the concept of "container" which maintain the state by itself). Server doesn't maintain/update/communicate session states.
		+ State (application state): Varies. Hold by client.
		+ Resource (resource state): Constant. Hold by server.
	+ Pro(s): Scalability. Load balance goes to client.
+ Cacheable
+ Client-Server: clear interface in between so the development can be done separately.
	+ Client: User interface, user state.
	+ Server: Data storage.
+ Layered System: intermediary servers for (1) enable load-balancing, (2) provide shared caches, and (3) enforce security policies.
+ Code on Demand (optional):
	+ Definition: temporarily extension/customization of client functionality by executible codes.
	+ Examples: Java applets, JavaScript.

Pro(s):

+ Performance
+ Scalability
+ Simplicity
+ Modifiability
+ Visibility
+ Portability
+ Reliability

## API Interface Rules

### Resource Naming Rule

+ Uniform interface
+ Not change over time
	+ May include API version in the URI
+ Use path variables (`/para1/{__}/para2/{__}`)
	+ Pros:
		+ Encode hierarchy: when the value will affect the entire subtree of your URI space.
		+ Mandatory arguments over `GET` request.
		+ You can return 404 error if the value does not correspond to an existing resource for (1) locators and (2) unique identifiers.
	+ Exceptions of using query parameters/GET variables (`/?para1={__}&para2={__}`)
		+ Filtering (then return an empty list if the value does not correspond to an existing resource)
		+ Sorting priority. E.g., `/tasks?sort=-priority` or `tasks?sort=+date&skip={__}`
		+ Full text search: Use query parameter `q` to match with the setup of general search engine such as [ElasticSearch](http://www.elasticsearch.org/) or [Lucene](http://lucene.apache.org/).
		+ Optional parameters
+ Strict low case names.
    + Avoid camel caps, since URLs are not case sensitive.
    + Use underscores rather than dashes.
        + The RESTful API for Dropbox, Twitter, Google Web Services and Facebook all uses underscores.
+ Using nouns (*things* rather than *actions*)
+ Using plurals (as the concept is a collection)
	+ Exception(s):
		+ Singleton resource such as `/configuration` (no ID and no `POST` verb usage)
		+ `PUT /parameters/{__}/star` for star and `DELETE /parameters/{__}/star` for unstar.
+ Each resource have at least one URI to identify it.
+ Resources should maps to domain object, and be consistent within the same application.
	+ Hence the noun in use should be user rather than user_id.
+ Predictable structure of URI for consistency/understandability/usability.
+ Hierarchical structure of URI for structure/relationship.
	+ Default to no nesting unless there is a strong relation. Use nesting only on strong relations (the nested resource cannot exist outside the parent). For nested paths which are under not so strong relations, use them but treat them as aliases.
+ Use HTTP Accepts Header for [content negotiation](https://en.wikipedia.org/wiki/Content_negotiation) for return format in between (1) JSON, (2) XML, (3) wrapped JSON, and (4) wrapped XML.
	+ E.g. `Accept: application/json; version=2`, `Content-Type: application/xml; version=2`
	+ Don't use ~~file-extension-style format specifier (e.g. `http://www.example.com/customers/12345.json`)~~
			+ An counter argument is don't use suffixes, since user is in the resource rather than the implementation detail.
	+ Don't use ~~Query-string parameter~~
	+ *(I think this is out-of-date. Don't do it. Just return JSON.)*

### Return body design rules

May devided to HTTP Header and content rules separately...

+ Data boundaries: Normally not clear but use common sense.
+ HTTP Accepts Header
	+ E.g. for `POST`
		+ `HTTP/1.1 201 CREATED`
		+ `Status`
		+ `Connection`
		+ `Content-Type`
		+ `Location` (so that's for self reference)
+ Provide (1) JSON, (2) XML, (3) wrapped JSON `.wjson`, and (4) wrapped XML `.wxml`.
 	+ *(Should be out of date. Only provide JSON should be okay.)*
	+ Set JSON as default. It is standard with fewer requirements.
	+ If really want to return XML, it should be JSON like -- simple and easy o read. Should not ~~follow XML utilize syntactically correct tags and text. Should not follow XML namespaces. Otherwise providing a XML interface is too staggering.~~ Currently really few consumers uses the XML responses.
	+ Supporting AJAX-style user interfaces *(what does that exactly mean?)*
+ Create Fine-Grained Resources
	+ (First) may mimic the structure of the underlying (1) application domain, or (2) database architecture. Start with small, easily defined resources.
	+ (Later) aggregate services and create larger use-case-oriented resources to reduce chattiness.
+ Completeness: contain other resources if necessary.
+ Link-ability: Use Hypermedia as the Engine of Application State (HATEOAS) for connectedness/navigability.
	+ Self-descriptive: API should be usable and understandable given an initial URI without prior knowledge or out-of-band information.
	+ Con(s):
		+ A major cause of network chattiness.
		+ increase implementation complexity
		+ Impose a significant burden on service clients
		+ Decreasing developer productivity on both client and server ends
	+ Not often followed by current industry leaders :-( but at least **a minimal set** of hyperlinking practices should be provided.
		+ Minimal links includes:
			+ Self reference: retrieve data.
			+ Newly-created using `POST`:
			 	+ URI of the new resource should be return in  Location response header (and body is empty).
			+ For collection returned by `GET`:
				+ Self link for each representation.
				+ At least also include `first`, `last`, `next`, `prev` links.
	+ Link pattern(s):
		+ Standard styles:
			+ Atom (most popular)
				+ Top level `data`
					+ for every data element, with a nested `links` sub-element and the pair-wised `rel` and `href` sub-sub-element:
						+ `rel` element with standard values `alternate`, `related`, `self`, `enclosure`, `via`.
				+ Top level `links` with pair-wised `rel` and `href` sub-element:
					+ `rel` for navigating links `first`, `last`, `previous`, `next`.
				+ Its XML format has more (REST irrelevant) concepts then JSON.
					+ `METHOD` property
			+ AtomPub
			+ Xlink
			+ JSON-LD
			+ [HAL-formatted JSON document](http://stateless.co/hal_specification.html). It is (1) standard and will be consistent all over the site, (2) with front-end/back-end supported, but (3) kind of boilerplate.
+ Wrapped Responses
	+ Reason: end-developer doesn't really care about the details of (1) status code, or (2) response body.
	+ Patterns (none of them can cover all cases):
		+ [OmniTI Labs JSEND](http://labs.omniti.com/labs/jsend)
		+ [Douglas Crockford](http://www.json.org/JSONRequest.html)
		+ Just wrap regular (non-JSONP) responses, with the following properties
			+ `code`
			+ `status`: `success`/`fail`/`error`
			+ `message` for fail or error
			+ `data` with response body/error cause and exception name
+ Limiting the amount of data returned
	+ For large data set
		+ Querying
		+ Pagination
			+ Methods
				+ Page-oriented
				+ Item-oriented
			+ Pros:
				+ Important from a band-width standpoint
				+ Make UI processing easier
			+ Standards:
				+ HTTP Range header
					+ Used by UI tools such as [Dojo JSON Database objects](https://www.npmjs.com/package/jodo)
					+ Keywords: `Range:`, `content-Range:`
				+ Query-string parameter `offset` and `limit` (overwrite Range header)
					+ Clear
					+ Easily-understood
					+ Human readable
					+ Easy to construct and parse
		+ Filtering
			+ Operations:
				+ Equality
				+ Starts-with
				+ Contain comparison
				+ ...
			+ Should use a single (rather than multiple) parameter to minimize name clashes/overlap.
				+ Query-string parameters: `jsonp`, `format`, `after`, `before`...
				+ Use `::` and separator `|` for a list of name/value pairs search. *(That should be really specific for SOME author. check what is common right now...)*. Use `*` for wild-card.
				+ Server-side to determine if filtering functionality is requested by checking that single filtering parameter.
				+ May need to use operators (as part of the value rather than property name).
		+ Sorting
			+ To utilize a single `sort` query-string parameter with `+` `-` and separator `|`.
	+ When to use
		+ Not recommended for all services
		+ Resource specific
		+ Should not be supported on all resources by default
	+ Standard(s):
		+ [Open Data Protocol (OData) URL Conventions](http://docs.oasis-open.org/odata/odata/v4.0/odata-v4.0-part2-url-conventions.html)
+ Versioning
	+ Make efforts to not need versioned representations
		+ Verisioning indicates a poor API design
		+ Big con of versioning: Adds a lot of complexity to an API and to the clients.
		+ But if decide to do will cause consumers to break later when the change is unavoidable.
	+ Should use version if:
		+ Uncertain the consequence of the design.
	+ Change version number when
		+ change a property name
		+ Remove a property
		+ Change a property data type
		+ Change validation rule
		+ Modify the `rel` value in Atom
		+ Add a request resource in an existing work flow
		+ Resource's state has a different meaning
		+ ...
	+ Non-breaking changes:
		+ Add new properties in JSON
		+ Add `link` to other resources
		+ New content-type supported formats.
		+ New content-language supported formats.
	+ Version rules:
		+ At individual resource level
		+ Support no more than 2 versions (otherwise time cumbersome/complex/error prone/costly)
	+ Technical methods:
		+ ~~Indicate in URL itself~~
			+ Used by Twitter/Facebook/Google
			+ API management tools like WSO2 require this form.
			+ Why not use it:
				+ REST constraints should embrace the built-in header system of the HTTP specification
				+ Break the rule that a new URI should be added only when a new resource or concept is introduced--not representation changes
		+ Write in HTTP header, and resolve via content negotiation
			+ In GET request: `Accept: [format]; version=[____]`
			+ In response: `Content-Type: [format]; version=[____]`
			+ Use "best match" when no version is specified -- oldest supported version.
			+ For unsupported/no longer supported versions, return 406 (Not acceptable).
			+ May `Deprecated: true` for nearly out-of-data version.
+ Date/Time
	+ Just `string` in JSON (and XML)
		+ From `timestamps` in database. Either UTC or GMT.
		+ Rules:
			+ Same format
			+ include time portion (time zone)
		+ Use standard:
			+ ISO 8601 (e.g. `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`)
				+ Parsing library
					+ DateAdapterJ (Java)
					+ ECMAScript 5, (out-of-date) momentjs, (out-of-date) datejs (JavaScript)
	+ HTTP Header force using RFC 822/RFC 1123.
		+ Use its timestamp format `Sun, 06 Nov 1994 08:49:37 GMT`
			+ Parsing ith Java `SimpleDateFormat` with `EEE, dd MMM yyyy HH:mm:ss 'GMT'`
			+ Not consist with JSON but nothing we can do about it.
+ Authorization
	+ Session based authentication.
	+ Protocol/work flow
		+ Client=>server (or):
			+ Authentication token in `X-Authorization` header
			+ `token` query-string parameter
		+ Server (1) verify (2) validate (3) parse/load authorization principle
		+ Authentication principle (server=>authentication service)
		+ Resource and required permission for operation (authentication service=>server)
		+ Service continues if authorized
	+ Standard:
		+ [OAuth2](https://oauth.net/2/): highly recommended but still in draft state
		+ OAuth1: acceptable alternative
		+ 3-Legged OAuth: may use in certain cases
		+ [OpenID](http://openid.net/developers/specs/): should use an additional and set OAuth as primary
	+ Transport through
		+ ~~SSL (Secure Sockets Layer)~~: minimal requirement, predecessor of TSL.
		+ TLS (Transport Layer Security): required by OAuth2
		+ ~~Switching between HTTP and HTTPS~~: has security weakness
+ Caching
	+ May use HTTP Header `Cache-Control: [how many seconds]` to set it up.

### Misc

How to break down to interlinked resources:

+ Starting from your business requirement (*RESTful Web Services* 2007).
+ Starting from resource design without thinking about hypermedia (*RESTful Web APIs* 2013, *RESTful Web Services Cookbook* 2010).

No matter what the target client is, it should at least support the XMLHttpRequest library (JavaScript).

### Security

Loophole protection (exclude authorization):

+ Used verbs for allowable methods only
	+ `GET` cannot modify data.
	+ Sensitive data in `POST` method (rather than in URL).
+ Validate input
	+ Accept known good input
	+ Check the JSON/XML is well-formed, and reject bad input *(Should validation be default for all input, or only do it if doubt? Will the first choice cause too many bundle on the server side?)*
		+ Validate JSON and XML for malformed data.
+ Protect SQL/NoSQL injection
+ Be aware of race conditions *(example in RESTful service)*
+ Output encode data using known libraries
	+ Microsoft’s Anti-XSS
	+ OWASP’s AntiSammy
+ Restrict the message size to the exact length of the field.
+ Services should only display generic error messages in HTTP response.
+ Log suspicious activity.
+ Monitor usage of the API
	+ Identify activities that fall out of the normal usage pattern.
+ Throttle API usage
	+ Malicious user cannot take down the API endpoint (DOS attack).
	+ Block malicious IP address if needed.
+ Store API keys in a cryptographically secure keystore.

### Cross-Origin Resource Sharing (CORS)

Browser have same origin policy/common-source requirement: the site displayed cannot perform a request against another site.

Methods o support cross-domain requests:
	+ ~~JSON with padding (JSONP): the service returning arbitrary JavaScript code instead of JSON. Evaluated by JavaScript interpreter rather than JSON parser.~~ -- NOT RECOMMENDED
	+ Cross-Origin Resource Sharing (CORS): A web browser technology specification to define ways for a web server to allow its resources to be accessed by a web page from a different domain.

Support CORS: sending an additional HTTP header in the response

```
Access-Control-Allow-Origin: [which domains should be able to initiate a CORS request]
Access-Control-Allow-Credentials: true // this one has side effect of sending the cookies/sessions if the user is logged into the application, so only use it if necessary...
```

Configuration via:

+ Web server
+ Proxy
+ Sent from the service itself

## Technical Setups

### REST server

Provide access to resources.

+ Java based:
	+ JAX-RS: JAVA API for RESTful Web Services
	+ Spring

#### Various Spring setups

##### CORS setup

To turn on cross origin from one specific request (or without `origins` should be all possible URLs)

```
@CrossOrigin                                         // for all sites
@CrossOrigin(origins = "http://localhost:8765")      // for a specific URL
```

Or globally

?

### REST client

Access and present resources.

### Other tools

+ [Postman](https://www.getpostman.com): RESTful API testing tool. Mac, Windows, Linux, and Chrome Apps.

## Remain questions

+ How to prevent CSRF in a RESTful application? Is it contradict with statelessness?
	+ *([This](https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html) should contain part of the answer. Read it carefully later.)*
	+ Basically the web service need to get extra information in each request, and then interpret to get the client's state.
	+ There's nothing special of CSRF compare to HTML POST. Just contain a CSRF key in `GET`, and return it in `PUT`, `POST`, or `DELETE` so the server can check.

## References

1. [RESTful Web Services Tutorial](https://www.tutorialspoint.com/restful/index.htm) in tutorialspoint *(Basic concepts included. But a big part of it is a hello world example with tedious setup wizards which is not quite useful...)*
1. [REST API tutorial](http://www.restapitutorial.com/)
1. RESTful Service Best Practices, Todd Fredrich.
