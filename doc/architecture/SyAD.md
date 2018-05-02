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
| Regular backups by authors |  |  | Done in a separated branch locally |
| Pending changes/different opinions |  |  | Different branch |
| To-be-reviewed draft |  |  | Pull request from a different branch |
| Benchmark/version approved as a group/management decision |  |  | Merge in "Integration-Manager Workflow" |
| Separate code and document changes |  |  | Different branch. A configuration file to indicate whether the current commit in a document change or not. |

- [SyAD-0001]{SyRS-0001} The software shall be built on top of `git`.

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

- [SyAD-0013]{} There shall be a configuration file.
- [SyAD-0014]{SyAD-0013,SyAD-0002,StRS-0049} The configuration file indicates the scanned path(s) for which the documents may stay. All the `markdown` files under the included file path(s) are treated as targeting document.
    - *(Should we go the opposite direction to list the ignore files, like `.gitignore`)*
- [SyAD-0015]{SyAD-0013,SyRS-0044} The configuration file indicates the scanned path(s) for the implemented code.
- [SyAD-0016]{SyAD-0013,SyRS-0045} The configuration file indicates the scanned path(s) for test cases.
    - *(What about if the code and tests are mixed together?)*
- [SyAD-0023]{StRS-0050} There shall be an index page for which user can link to a set of documents with order/structure/relation between each other.

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
    - *(Should we further distinguish bubble list `-`, `+` and `star` for different purposes?)*
- [SyAD-0008]{SyRS-0036} Tag names shall begin with a letter `a-zA-Z` or an underscore `_`. Subsequent characters can be letters, underscores, digits `0-9`, and hyphen `-`. Tag shall be case sensitive.
- [SyAD-0010]{SyAD-0009,SyRS-0010} The software shall analyze the upstream/downstream relationship based on the provided upstream tags.
- [SyAD-0012]{SyAD-0010,SyRS-0038} The traceability analysis shall be automatic triggered when there are document changes in the new commit.
- [SyAD-0011]{SyAD-0010} The software shall raise errors if the traceability relationship contains errors:
    - Marked tag in relationship does not exit.
    - Undistinguishable tags appear more than one times.
    - Loops in relationship.
- [SyAD-0024]{SyAD-0011} There shall be a client-side validator to raises traceability errors before the new changes are uploaded to the server.
- [SyAD-0025]{SyAD-0010,SyAD-0011,SyAD-0027} There shall be a server-side analyzer which write the upstream/downstream relationship into the database.

*(TODO: The tagging syntax for implementation code and test cases?)*

### Reviewing and comment

## Decomposition description

### Module decomposition

So from a direct interacting with the requirement, the software shall include the following components:

- [SyAD-0017]{} Web service for reviewing and management.
- [SyAD-0020]{SyAD-0026} `git` revision control system.
- [SyAD-0021]{SyAD-0027} Database.
- [SyAD-0018]{SyAD-0010} Server-side traceability analyzer.
- [SyAD-0022]{SyAD-0010} Client-side traceability validator.
- [SyAD-0019]{SyAD-0006,SyRS-0031} Text editor plugin for:
    - Generate unique tag.
    - Provide template.

![](modole_decomposition.png "")

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
