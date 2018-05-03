# Software Requirements Specification -- Web Services

## Introduction

### Purpose

### Scope

### Overview

#### Perspective

#### Functions

#### User characteristics

#### Limitations

## References

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## Specific requirements

### External interfaces

#### System interfaces

#### User interface

#### Software interface

#### Hardware interface

#### Communications interfaces

#### Memory constrains

#### Operations

#### Site adaptation requirements

### Functions

Document visualization:

- [SRS-WS-0010]{SyAD-0023} The software shall provide an index page to:
    - Show the readers the relationship between documents.
    - Provide readers shortcuts to navigate to targeting documents.
- [SRS-WS-0011]{StRS-0011,SyRS-0026,SyRS-0038,SyRS-0018} The software shall provide different definition of default version based on different user roles:
    - For document editor, the newest commit edited by the current user should be returned.
    - For document reviewer, the to-be-reviewed commit should be returned.
    - For document user (e.g. software engineer), the newest review meeting approval version should be returned.
- [SRS-WS-0002]{SyRS-0087} The timestamp of last modification shall be shown in each document.
- [SRS-WS-0001]{SyRS-0087} Author(s) list shall be shown in each document.
- [SRS-WS-0005]{SyRS-0087,SyRS-0061} The document shall be easily switched between different versions.
- [SRS-WS-0006]{SRS-WS-0005} Switch to a different (historical/draft) version can be done by providing a version number.
- [SRS-WS-0007]{SRS-WS-0005,SyRS-0065,SyAD-0035} Switch to a historical version can be done by providing a `git` tag.
- [SRS-WS-0008]{SRS-WS-0005,SyAD-0033} Switch to a draft version can be done by providing a `git` branch name.
- [SRS-WS-0009]{SRS-WS-0005,SRS-WS-0011} Switch to the default version shall be done by a shortcut.
- [SRS-WS-0003]{SyRS-0087} There shall be an option to show detailed historical timestamps with authors.
- [SRS-WS-0004]{SyRS-0087} There shall be an option to show the detail of which author write/in charge of which part of the document.
    - *(GitHub has another function "blame", for which user can get the one-step-earlier version of of each line. Also the commit message is involved. We hold these possibility right now. Based on the data saved in git, there may be multiple ways to provide a more vivid way to show the document evolution history, and detailed analysis is needed for what is the most effective and user friendly way of doing that.)*

Reviewing:

User management:

### Usability requirements

### Performance requirements

### Logical database requirement

### Design constraints

### Standards compliance

### Software system attributes

Reliability:

Availability:

Security:

Maintainability:

Portability:

### Supporting information

## Verification

## Appendices

### Assumptions and dependencies

## Bibliography

1. ISO/IEC/IEEE 29148:2011, System and software engineering -- Life cycle processes -- Requirement engineering.
2. IEEE Std 830:1998, IEEE recommended practice for software requirements specifications.
3. ISO/IEC/IEEE 12207:2008, System and software engineering -- Software life cycle process.
