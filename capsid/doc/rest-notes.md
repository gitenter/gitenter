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

### Patterns/rules

Patterns/rules are always conflict to each other, as they are defined in different time. Rules to fit "tomorrow" is not possible.

*(Different approach: SOAP (Simple Object Access Protocol), more straightforward, not a lot of design.)*

Constrains:

- Uniform Interface: simplify, decouple.
	- Resource-based: Using URIs as resource identifiers for **individual** resources. Separate from the representations.
	- Manipulation of Resources Through Representations: Client (hold representation) has enough information to modify/delete resources.
	- Self-descriptive Messages: Message describes how to process the message. Response indicates its cache-ability.
	- Hypermedia as the Engine of Application State (HATEOAS)
		- **hypermedia** (definition):
			- Clients deliver state via body contents, query-string parameters, request headers and the requested URI (the resource name).
			- Services deliver state to clients via body content, response codes, and response headers.
		- Links are contained in the returned body (or headers) for (1) itself, and (2) related objects.
- Stateless:
	- State is contained within request itself so need to be transfered back and force (Contradict with the concept of "container" which maintain the state by itself). Server doesn't maintain/update/communicate session states.
		- State (application state): Varies. Hold by client.
		- Resource (resource state): Constant. Hold by server.
	- Pro(s): Scalability. Load balance goes to client.
- Cacheable
- Client-Server
- Layered System
- Code on Demand

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
