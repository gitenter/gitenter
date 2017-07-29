# Software Requirements Specification -- Web User Interface

## Document identifier

**Date of issue:**

**Issuing organization:**

**Author(s):**

**Approval signatures:**

**Status/version:**

**Reviewers/pertinent managers:**

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

+ **Milestone:** Time point where documents are to be reviewed.

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

Account management:

- [SRS-UI-0001]{} Each repository shall belong to one organization. Each organization may have multiple repositories.
- [SRS-UI-0002]{} Each organization shall have multiple (IT) managers.
- [SRS-UI-0015]{SRS-UI-0002} Organization managers are in charge of add/delete/maintain repositories.
- [SRS-UI-0003]{} Each repository shall be able to switch on/off (1) reading, (2) reviewing, (3) authoring, and (4) management authorities for users of different roles, with the constrain (i) author can always review, (ii) reviewer can always read, and (iii) manager can always read.
- [SRS-UI-0016]{SRS-UI-0002} Organization managers are in charge of assign users, as well as define authorities of users to some particular repository.

Document viewing:

- [SRS-UI-0005]{} The software shall show the git-based evolution of documents and other files.
- [SRS-UI-0004]{} The software shall be able to distinguish targeting documents from normal files.
- [SRS-UI-0006]{} The software shall identify the traceable items in the documents.
- [SRS-UI-0007]{SRS-UI-0006} The upstream and downstream items of the traceable item shall be shown (and linked to) through the user interface.
- [SRS-UI-0017]{SRS-UI-0005} Reading authorized users shall be able to access git-based informations.
- [SRS-UI-0018]{SRS-UI-0004,SRS-UI-0006} Reading authorized users can view the documents.

Document reviewing:

- [SRS-UI-0008]{} Management authorized users shall be able to setup milestones for some particular git commit.
- [SRS-UI-0010]{} Management authorized users shall choose a set of documents which are to-be-reviewed for a milestone.
- [SRS-UI-0009]{} Management authorized users shall be able to switch the milestone reviewing status in between (1) review, (2) approval, and (3) denial.
- [SRS-UI-0011]{} The software shall show well-defined line numbers for the to-be-reviewed documents.
- [SRS-UI-0012]{SRS-UI-0007,SRS-UI-0011} The software shall export PDF files for the to-be-reviewed documents documents, include (1) milestone name, (2) date of issue, (2) line numbers, (3) upstream and downstream items of the desired traceable item.
- [SRS-UI-0013]{SRS-UI-0011} Reviewing authorized users shall be able to open an issue of a to-be-reviewed document based on a particular line number *(not limited to traceable items)*.
- [SRS-UI-0014]{SRS-UI-0013} Reviewing authorized users can join a discussion by commenting on an opened issue.
- [SRS-UI-0019]{} Authoring authorized users shall be able to switch the issue status in between (1) open and (2) close.

Verification:

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
