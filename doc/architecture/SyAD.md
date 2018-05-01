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

+ Documents themselves.
+ Document metadata (version, date/time, author).
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

### Revision control system

Comparison of version control platforms:

|            | Subversion | git        |
| ---------- | ---------- | ---------- |
| Regular backups by authors |  | Done in a separated branch locally |
| Pending changes/different opinions |  | Different branch |
| To-be-reviewed draft | | Pull request from a different branch |
| Benchmark/version approved as a group/management decision |  | Merge in "Integration-Manager Workflow" |
| Separate code and document changes |  | Different branch. A configuration file to indicate whether the current commit in a document change or not. |

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
- [SyAD-0005]{SyRS-0073,SyAD-0002} The traceable items are defined by extending the "bullet lists" item in markdown.

### Tag and traceability

- [SyAD-0006]{SyRS-0036} The plain-text-based tag shall be unique throughout the entire document system.
- [SyAD-0009]{SyRS-0036} The upstream items shall be explicit marked in file using the plain-text-based tags. There's no need to do the downstream ones.
- [SyAD-0007]{SyAD-0009,SyAD-0005,SyRS-0072,SyRS-0076} The tag and the upstream tag of a particular traceable item, shall be marked using the following format.

```
Optional block comments

- [currentItemTag]{upstreamTags,are,seperated,by,comma} Traceable item context.
    - Enumerate items in the traceable item context.
    + Optional inline comments
```

- [SyAD-0008]{SyRS-0036} Tag names shall begin with a letter `a-zA-Z` or an underscore `_`. Subsequent characters can be letters, underscores, digits `0-9`, and hyphen `-`. Tag shall be case sensitive.
- [SyAD-0010]{SyAD-0009,SyRS-0010} The software shall automatically analyze the upstream/downstream relationship based on the provided upstream tags.
- [SyAD-0011]{SyAD-0010} The software shall raise errors while analyzing the traceability relationship, with errors include:
    - Marked tag in relationship does not exit.
    - Undistinguishable tags appear more than one times.
    - Loops in relationship.

## Decomposition description

### Module decomposition

- [SyAD-0003]{SyRS-0024} There shall be a centralized server.

- Git server/hosting service
- Git client
- Text editor add-ons

### Process decomposition

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
