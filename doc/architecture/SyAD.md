# System Architecture Description

## Introduction

### Purpose

### Scope

### Overview

## References

1. System requirements specification (SyRS).

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## Design decisions

### Data storage

Notice that there are several different kind of data to be saved/cached in this system.

+ Document themselves.
+ Historical states of the documents.
+ Document metadata (author, date/time, version).
+ User and authorization.
+ Traceability caching (since only the upstream item is marked explicitly in the document).
+ Reviewing data (review metadata, comments).

Document themselves and part of the metadata are by-default saved in the revision control system. The other one also may saved in the revision control system (this is the Gitolite approach), with several associated pros and cons.

+ Pros:
    + The evolution history (and timestamps) of user and authorization can be automatically recorded.
    + Traceability caching and reviewing data can by-default associate to some particular commit.
+ Cons:
    + Data can be only saved in plain text form.

Or they may be saved in a separated database (GitHub, BitBucket, GitLab, ... all go in this approach).

+ Pros:
    + Can define flexible database structure based on the requirements, as in general database is more flexible on saving data compare to a revision control system.
    + Have 3rd party data persistent tools we can use.
+ Cons:
    + Need to handle the complicity of query from different places.
    + Difficult to handle the relationship in between data pieces spread between different places.
    + Some data may need to have duplicated storage.

- [SyAD-0004]{} The system shall be build on top of a dual data storage system of (1) a revision control system, and (2) a database.
- [SyAD-0026]{SyAD-0004} The revision control system shall in charge of saving:
    - Document themselves.
    - Historical states of the documents.
    - Document metadata (author, date/time, version).
- [SyAD-0027]{SyAD-0004} The database shall in charge of saving:
    - User and authorization.
    - Traceability caching (since only the upstream item is marked explicitly in the document).
    - Reviewing data (review metadata, comments).

### Revision control system

Comparison of version control platforms for satisfying our targeting functional requirements:

|            | Concurrent versions system (CVS) | Subversion (SVN) | git        |
| ---------- | -------------------------------- | ---------------- | --- |
| Regular backups by authors |  |  | Done as a series of commits in a separated branch |
| Pending changes/different opinions |  |  | Different branch |
| To-be-reviewed draft |  |  | Pull request from a different branch |
| Review meeting approval as a group/management decision |  |  | Merge in "Integration-Manager Workflow" |
| Separate code and document changes |  |  | Different branch. A configuration file to indicate whether the current commit in a document change or not. |

- [SyAD-0001]{SyRS-0001} The software shall be built on top of `git`.
- [SyAD-0032]{SyAD-0001,SyRS-0026} The software shall handle regular backups by author(s by a series of `git` commits in a separated branch.
- [SyAD-0033]{SyAD-0001,SyRS-0026} The software shall handle pending changes/different opinions in a different `git` branch.
- [SyAD-0034]{SyAD-0001,SyRS-0026} The software shall treat review meeting as a pull request from a `git` branch to master branch.
- [SyAD-0035]{SyAD-0001,SyRS-0026} The software shall handle review meeting approval as a merge event in `git`'s "Integration-Manager Workflow", with an associated `git` tag on the particular commit.

### Document formatting

For a comparison with other markup languages, we listed the cons of the alternative choices:

+ HTML:
    + Too complicated to be edited.
    + Most advanced layout setups are not useful for simple documents.
+ LaTeX:
    + Not easy to read and edit.
    + Display math equations are not particularly useful for our case. And if really needed, can be done by extended text boxes (like what WikiPedia is doing).
    + The default PDF output is not easy to use for online activities (review meetings).
+ reStructuredText:
    + Really similar to markdown, just with a little bit less user and library supports.
    + If needed, can be switched in between the two really easy.

- [SyAD-0002]{SyRS-0034} The document shall be written using a modified `markdown` format.
- [SyAD-0005]{SyRS-0073,SyAD-0002} The traceable items are defined by extending the (unordered) bullet list item in markdown.

### Configuration

- [SyAD-0013]{} There shall be a traceability analyzer configuration file.
- [SyAD-0014]{SyAD-0013,SyAD-0002,StRS-0049} The configuration file indicates the scanned path(s) for which the documents may stay. All the `markdown` files under the included file path(s) are treated as targeting document.
    - *(Should we go the opposite direction to list the ignore files, like `.gitignore`)*
    - *(Notice that the software can know whether the documents has been changed or not, by something similar to `git diff` of the included paths.)*
- [SyAD-0015]{SyAD-0013,SyRS-0044} The configuration file indicates the scanned path(s) for the implemented code.
- [SyAD-0016]{SyAD-0013,SyRS-0045} The configuration file indicates the scanned path(s) for test cases.
    - *(What about if the code and tests are mixed together?)*
- [SyAD-0023]{StRS-0050} There shall be an index configuration file for which user can link to a set of documents with order/structure/relation between each other.
    - *(Notice that the relationship can be build inside of (1) the "references" section of every document, (2) traceability markers, it shouldn't be need to mark manually in this configuration file.)*

### Tag and traceability

- [SyAD-0006]{SyRS-0036} The plain-text-based tag shall be unique throughout the entire document system.
- [SyAD-0009]{SyRS-0036} The upstream items shall be explicit marked in file using the plain-text-based tags. There's no need to do the downstream ones.
- [SyAD-0007]{SyAD-0009,SyAD-0005,SyRS-0072,SyRS-0076} In document, the tag and the upstream tag of a particular traceable item, shall be marked using the following format.

```
Optional block comments

- [currentItemTag]{upstreamTags,are,seperated,by,comma} Traceable item context.
    - Enumerate items in the traceable item context.
    + Optional inline comments.
    * Controversial issues/different approach?
```

- [SyAD-0028]{SyAD-0007} Beside the traceable item extension, the software shall fully support the original `markdown` syntax.
    - *(Should we further distinguish bubble list `-`, `+` and `star` for different purposes? E.g. to use the first order ones to distinguish todo/implemented/fully tested)*
- [SyAD-0008]{SyRS-0036} Tag names shall begin with a letter `a-zA-Z` or an underscore `_`. Subsequent characters can be letters, underscores, digits `0-9`, and hyphen `-`. Tag shall be case sensitive.
- [SyAD-0010]{SyAD-0009,SyRS-0010} The software shall analyze the upstream/downstream relationship based on the provided upstream tags.
- [SyAD-0012]{SyAD-0010,SyRS-0038} The traceability analysis shall be automatic triggered when there are document changes in the new commit.
- [SyAD-0011]{SyAD-0010} The software shall raise errors if the traceability relationship contains errors:
    - Marked tag in relationship does not exit.
    - Undistinguishable tags appear more than one times.
    - Loops in relationship.
- [SyAD-0024]{SyAD-0011} There shall be a client-side validator to raises traceability errors before the new changes are committed/uploaded to the server.
- [SyAD-0025]{SyAD-0010,SyAD-0011,SyAD-0027} There shall be a server-side analyzer which write the upstream/downstream relationship into the database.

*(TODO: The tagging syntax for implementation code and test cases?)*

### Reviewing

### Authorization

Since requirement engineering and design control is mostly for enterprise uses, it is less likely that individual user will do it in their hobby projects. And for reviewing, it by default cannot be done by a one-person project. Also, notice that individual user can always sign up an organization and put her projects there. Therefore, project always belongs to some particular organization (unlike in GitHub that project can either belong to an organization or a user).

- [SyAD-0036]{} An organization can have multiple repositories, for which:
    - An organization can be either an enterprise, a non-profit organization, a unofficial group, or some space some individual user registered.
    - A repository is the output of a project. It is a computer folder and its content includes a set of documents.
- [SyAD-0037]{} An organization can have multiple members. A user may be a member of more than one organizations.
- [SyAD-0038]{SyAD-0037} A member of an organization can either be:
    - A normal user.
    - A manager:
        - Create/delete repositories.
        - Add/remove users into/from that organization.
        - Authorize other members particular privilege of some particular repository.
- [SyAD-0039]{SyAD-0038} A user may have the following privilege of a repository:
    - Project leader: Organize the approval process of a review meeting.
        - *(Should we need this person? Or it could be a simple system-based voting?)*
    - Document editor.
    - Document reviewer.
    - Document reader.
        - *(May be code editor?)*

## Decomposition description

### Module decomposition

So from a direct interacting with the requirement, the software shall include the following components:

- [SyAD-0019]{SyAD-0006,SyRS-0031} Text editor plugin for:
    - Generate unique tag.
    - Provide template.
- [SyAD-0022]{SyAD-0010} Client-side traceability validator.
- [SyAD-0018]{SyAD-0010} Server-side traceability analyzer.
- [SyAD-0020]{SyAD-0026} `git` revision control system.
- [SyAD-0021]{SyAD-0027} Database.
- [SyAD-0017]{SyRS-0010,SyRS-0047} Web service(s) for:
    - Document visualization.
    - Reviewing.
    - User management.

We'll not provide a local application for document visualization at this moment, with the following concerns:

+ Several markdown preview tools can be used for a simple visualization of markdown files. And you can always download a pdf version of the document for local references.
+ Several git GUI clients (`gitg`, `gitkraken`) can be used for checking historical versions. And they are generally not useful unless you are the editor of the document.
+ The crucial part that the existing tools cannot provide, is the traceability items with links to upstream/downstream items. However, since a traceability analysis is needed before displaying, we need a local database (maybe a SQLite) to hold the result. That will involve further complication (and potential combination of part of the functions of the traceability validator), we will not implement it right now.

![](module_decomposition.png "")

There are additional design decisions after the previous module decomposition.

- [SyAD-0029]{SyAD-0022} The traceability validator shall be in form of client side git hook.
- [SyAD-0030]{SyAD-0018} The traceability analyzer shall be in form of server side git hook.

For the different kind of web services, there may be one or multiple web server(s) to do the job. For a comparison:

+ One web server:
    + Pros:
        + A highly decoupled implementation is to have lots of microservices (through a RESTful API). In that case it makes no sense to have more than one same/different web server/architecture.
        + There are standard way to do load balancer later.
    + Cons:
        + The crashing/overload of one part will affect the other.
+ Multiple web servers:
    + Pros:
        + It can reduce the server load, since people coming for different reasons end up connecting to different servers.
    + Cons:
        + More complicit implementation. Further questions raise such as weather they should share a database or not, or simply how to split to different servers.
        + Notice that document visualization and reviewing are highly related to each other (and they are also for the same group of people), these two probably cannot be separated. User management have relatively small traffic/load. So this strategy can't really help reduce server load.

We'll not implement the microservices at this moment, but will choice the one server architecture to make things simple.

- [SyAD-0031]{} All different web services shall be on a single web server.

### Process decomposition

![](process_decomposition.png "")

## Dependency description

### Intermodule dependencies

### Interprocess dependencies

## Interface description

### Module interface

### Process interface

## Appendices

### Assumptions and dependencies

## Bibliography

1. ISO/IEC TR 24748-1, Systems and software engineering -- Life cycle management -- Part 1: Guide for life cycle management
2. ISO/IEC/IEEE 15288:2008, System and software engineering -- System life cycle process.
3. ISO/IEC/IEEE 42010:2011, Systems and software engineering -- Architecture description
4. IEEE Std 1016:1998, IEEE recommended practice for software design descriptions.
5. MIL-STD-498 (Military-Standard-498) DI-IPSC-81432, System/subsystem design description (SSDD).
