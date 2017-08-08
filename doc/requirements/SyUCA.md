# System Use Cases Analysis

## Document identifier

**Date of issue:**

**Issuing organization:**

**Author(s):**

**Approval signatures:**

**Status/version:**

**Reviewers/pertinent managers:**

## Introduction

## References

1. Stakeholder requirements specification (StRS).
2. System requirements specification (SyRS).

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## Specific use cases

Account management:

Unlike a general project which may be a one-person project, quality control activities is always by a group of people, typically an enterprise. Therefore, repositories can only belong to organization, rather than users.

- [SyUCA-0001]{} Each repository shall belong to one organization. Each organization may have multiple repositories.
- [SyUCA-0002]{} Each organization shall have multiple (IT) managers.
- [SyUCA-0015]{SyUCA-0002} Organization managers are in charge of add/delete/maintain repositories.
- [SyUCA-0003]{} Each repository shall be able to switch on/off (1) reading, (2) reviewing, (3) authoring, (4) testing, and (4) management authorities for users of different roles, with the constrain (i) author can always review, (ii) reviewer can always read, (iii) tester can always read, and (iv) manager can always read.
- [SyUCA-0016]{SyUCA-0002} Organization managers are in charge of assign users, as well as define authorities of users to some particular repository.

Document reviewing:

- [SyUCA-0008]{} Managers shall be able to setup milestones for some particular git commit.
- [SyUCA-0010]{} Managers shall choose a set of documents which are to-be-reviewed for a milestone.
- [SyUCA-0009]{} Managers shall be able to switch the milestone reviewing status in between (1) reviewing, (2) approval, and (3) denial.
- [SyUCA-0020]{} Managers shall be able to switch the to-be-reviewed document status in between (1) draft, (2) reviewing, (3) approval, and (4) denial when the milestone status is reviewing.
- [SyUCA-0022]{SyUCA-0010,SyUCA-0020} When a milestone is setup, all chosen to-be-reviewed documents turns to status "reviewing".
- [SyUCA-0021]{} When all documents are in status approval but the milestone is in status "reviewing", the milestone review status turns to "approval".
- [SyUCA-0011]{} The software shall show well-defined line numbers for the to-be-reviewed documents.
- [SyUCA-0012]{SyUCA-0007,SyUCA-0011} The software shall export PDF files for the to-be-reviewed documents documents, include (1) milestone name, (2) date of issue, (2) line numbers, (3) upstream and downstream items of the desired traceable item.
- [SyUCA-0013]{SyUCA-0011} Reviewers shall be able to open an issue of a to-be-reviewed document based on a particular line number *(not limited to traceable items)*.
- [SyUCA-0014]{SyUCA-0013} Reviewers can join a discussion by commenting on an opened issue.
- [SyUCA-0019]{} Authoring authorized users shall be able to switch the issue status in between (1) open and (2) close.

Verification:

- [SyUCA-0025]{} For document in milestone status "approval", its traceable items shall be able to switch the status in between (1) unverified, (2) pass, and (3) fail.
- [SyUCA-0027]{SyUCA-0025} The timestamp shall be shown when the traceable item status changes.
- [SyUCA-0026]{SyUCA-0025} The tester's identity shall be shown when the traceable item status changes.
- [SyUCA-0028]{SyUCA-0025} The associated testing approach and detail results shall be attached.

Document Browsing:

- [SyUCA-0004]{} The software shall be able to distinguish targeting documents from normal files.
- [SyUCA-0006]{} The software shall identify the traceable items in the documents.
- [SyUCA-0007]{SyUCA-0006} The upstream and downstream items of the traceable item shall be shown (and linked to) through the user interface.
- [SyUCA-0005]{} The software shall show the history of documents and other files.
- [SyUCA-0017]{SyUCA-0005} While showing the document history, (1) the timestamp of the change, (2) the author, and (3) the relation to previous and follow up changes shall also been shown.
- [SyUCA-0023]{SyUCA-0010,SyUCA-0020} When the document history is being shown, its previous status and associated milestones shall be shown together.
- [SyUCA-0024]{SyUCA-0013,SyUCA-0014,SyUCA-0005} For users without reviewing authority, the issues and comments associate to the document history are viewable.
- [SyUCA-0018]{SyUCA-0004,SyUCA-0006} Reading authorized users can view the documents.

## Verification

## Appendices

### Assumptions and dependencies

## Bibliography
