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

HTTP Methods of a REST architecture (act as the "verb" in HTTP request):

+ `GET`
+ `PUT`
+ `DELETE`
+ `POST`
+ `OPTIONS`

Good representations of resource should be:

+ Understandability
+ Completeness: contain other resources if necessary.
+ Link-ability (to another resource)

Safeguard a RESTful web service should be setup:

+ Validation: protect against SQL/NoSQL injection.
+ Session based authentication.
+ Sensitive data in `POST` method (rather than in URL).
+ Set restrictions on methods. E.g., `GET` cannot modify data.
+ (On the server side) check the JSON/XML is well-formed.
+ Throw generic error messages in HTTP response.

Parts:

+ Rest server: Provide access to resources.
	+ Java based:
		+ JAX-RS: JAVA API for RESTful Web Services
		+ Spring
+ Rest client: Access and present resources.

## Useful tools

+ [Postman](https://www.getpostman.com): RESTful API testing tool. Mac, Windows, Linux, and Chrome Apps.

## API Design

### (Theoretical) Design principles

Patterns/rules are always conflict to each other, as they are defined in different time. Rules to fit "tomorrow" is not possible.

*(Different approach: SOAP (Simple Object Access Protocol), more straightforward, not a lot of design.)*

Constrains:

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

### Design rules

+ Use HTTP verbs `GET`, `POST`, `PUT`, `DELETE`
+ Sensible resource names.
	+ Using path variables (`/para1/{__}/para2/{__}`) for *resource names*. Resource names should be nouns.
		+ Encode hierarchy: when the value will affect the entire subtree of your URI space.
		+ Mandatory arguments over GET request.
		+ When you want to return 404 error if the value does not correspond to an existing resource.
			+ Locators.
			+ Unique identifiers.
	+ Using query parameters/GET variables (`/?para1={__}&para2={__}`) only for *filtering*.
		+ Optional parameters.
			+ For RESTful APIs, the representations chosen shall be provided as query parameters.
		+ When you want to return an empty list if the value does not correspond to an existing resource.
			+ Filter parameters.
+ XML and JSON
	+ JSON as default.
	+ Offer both if possible. User switch it by changing the extension in between `.xml` and `.json` *(how this can be done if using path variables??)*
		+ JSON: Standard. Fewer requirements.
		+ XML:
			+ Should not ~~follow XML utilize syntactically correct tags and text. Should not follow XML namespaces. Otherwise providing a XML interface is too staggering.~~
			+ Few consumers uses the XML responses.
	+ Supporting AJAX-style user interfaces *(what does that exactly mean?)*
	+ Provide a wrapped response (`.wxml` or `.wjson`).
+ Create Fine-Grained Resources
	+ (First) may mimic the structure of the underlying (1) application domain, or (2) database architecture. Start with small, easily defined resources.
	+ (Later) aggregate services and create larger use-case-oriented resources to reduce chattiness.
+ Connectedness (via hypermedia links)
	+ Self-descriptive
	+ Links includes:
		+ Self reference: retrieve data.
		+ Location header *(in? about?)* via POST.
		+ For returned collections, at least include `first`, `last`, `next`, `prev` links.

### HTTP Verbs (detailed)

+ `GET`: read
	+ Idempotent
	+ Safe
	+ URI:
		+ `/para1/{__}/para2/`: Collection
		+ `/para1/{__}/para2/{__}`: Individual item
	+ Return code:
		+ 200: OK
		+ 404: NOT FOUND
		+ 400: BAD REQUEST
	+ Along with `HEAD`
+ `PUT`: update ~~(also create if the resource ID is chosen by the client, but that's not recommended)~~
	+ Idempotent
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
	+ URI:
		+ `/para1/{__}/para2/`: Add to collection
	+ Return code:
		+ 201: created. Location header with link to `/para1/{__}` containing new ID.
		+ 404: NOT FOUND
+ `DELETE`: delete
	+ Idempotent
	+ URI:
		+ `/para1/{__}/para2/{__}`: Individual item
	+ Return code:
		+ 200: OK along with response body (maybe JSEND-style with (1) representation of the delete item, (2) wrapped response)
		+ 204: OK but no content/no response body is returned
		+ 404: NOT FOUND (the resource does not exist/cannot be removed)
			+ If no ID is provided (`/para1/{__}/para2/`), should return 404 unless you want to delete every resource in the entire collection -- not often desirable.
+ `HEAD`
	+ Idempotent
	+ Safe
+ `OPTIONS`
	+ Idempotent
	+ Safe
+ `TRACE`
	+ Idempotent
	+ Safe

(Idempotent: multiple identical requests has the same effect as making a single request.)

(Safe: intended only for information retrieval and should not change the state of the server. No side effects beyond logging/caching/web counter++. Safe=>idempotent. Safe=>read-only. The services must adhere to this rule. The return does not need to be the same every time.)

### Patterns

One possibility is to use [HAL-formatted JSON document](http://stateless.co/hal_specification.html). It is (1) standard and will be consistent all over the site, (2) with front-end/back-end supported, but (3) kind of boilerplate.

## Technical Setups

### CORS setup

To turn on cross origin from one specific request (or without `origins` should be all possible URLs)

```
@CrossOrigin                                         // for all sites
@CrossOrigin(origins = "http://localhost:8765")      // for a specific URL
```

Or globally

?

## Remain questions

+ How to prevent CSRF in a RESTful application? Is it contradict with statelessness?
	+ *([This](https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html) should contain part of the answer. Read it carefully later.)*
	+ Basically the web service need to get extra information in each request, and then interpret to get the client's state.
	+ There's nothing special of CSRF compare to HTML POST. Just contain a CSRF key in `GET`, and return it in `PUT`, `POST`, or `DELETE` so the server can check.

## References

1. [RESTful Web Services Tutorial](https://www.tutorialspoint.com/restful/index.htm) in tutorialspoint *(Basic concepts included. But a big part of it is a hello world example with tedious setup wizards which is not quite useful...)*
1. [REST API tutorial](http://www.restapitutorial.com/)
1. RESTful Service Best Practices, Todd Fredrich.
