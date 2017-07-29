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
- [SRS-UI-0002]{} Each organization shall have multiple (IT) managers who are in charge of add/delete/maintain the repositories.
- [SRS-UI-0003]{} Each repository shall be able to give different level of authority to application users with different roles:
  - Reader: read-only privilege.
  - Reviewer: who can also comment on milestone document.
  - Editor: who can also edit documents.
  - Project lead: who also can setup milestones.

Document viewing:

- [SRS-UI-0005]{} The software shall show the git-based evolution of documents and other files.
- [SRS-UI-0004]{} The software shall be able to distinguish targeting documents from normal files.
- [SRS-UI-0006]{} The software shall identify the traceable items in the documents.
- [SRS-UI-0007]{SRS-UI-0006} The upstream and downstream items of the traceable item shall be shown (and linked to) through the user interface.

Review:

- [SRS-UI-0008]{} The project lead shall be able to setup milestones for some particular git commit.
- [SRS-UI-0010]{} The project lead shall choose a set of documents which are to-be-reviewed for a milestone.
- [SRS-UI-0009]{} The project lead shall be able to switch the milestone reviewing status in between (1) review, (2) approval, and (3) denial.
- [SRS-UI-0011]{} The software shall show well-defined line numbers for the to-be-reviewed documents.
- [SRS-UI-0012]{SRS-UI-0007,SRS-UI-0011} The software shall export PDF files for the to-be-reviewed documents documents, include (1) milestone tag and time, (2) line numbers, (3) upstream and downstream items of the desired traceable item.
- [SRS-UI-0013]{SRS-UI-0011} (1) Project lead, (2) editor, and (3) reviewer shall be able to open an issue of a to-be-reviewed document based on a particular line number *(not limited to traceable items)*.
- [SRS-UI-0014]{SRS-UI-0013} (1) Project lead, (2) editor, and (3) reviewer can join a discussion by commenting on an opened issue.

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
