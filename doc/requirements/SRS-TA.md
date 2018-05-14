# Software Requirements Specification -- Traceability Analyzer

## Introduction

### Purpose

### Scope

### Overview

#### Perspective

#### Functions

#### User characteristics

#### Limitations

## References

1. System requirements specification (SyRS).
1. System Architecture Description (SyAD).

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## Specific requirements

### External interfaces

#### System interfaces

(Client-side) traceability validator:

The traceability validator has a lot of overlapping functions with this software. However, we want to do them again in here, because:

+ We have to do it in here to keep flexibility. As we can't force the user to install the traceability validator, if not, something may go wrong.
+ While traceability validator only returns a boolean which may block the action, here we need to record the result.

#### User interface

#### Software interface

#### Hardware interface

#### Communications interfaces

#### Memory constrains

#### Operations

#### Site adaptation requirements

### Functions

- [SRS-TA-0011]{SyAD-0018} The software shall be triggered every time a new commit has been pushed to the git server.
- [SRS-TA-0009]{SRS-TA-0011,SyAD-0010} The software shall be only execute and record the result if the traceability relationship has been changed in the new commit.
- [SRS-TA-0010]{SRS-TA-0009,SyAD-0047} Whether traceability changes have been involved in a commit, shall be pre-screened by:
    - Whether there are document changes: comparing if files returned by `git diff` includes document.
        - Notice that document accessories (images, ...) under the document path will not affect the tradability relationship.
    - Whether implemented code/test cases have traceable items marked, and if yes, comparing if files returned by `git diff` includes them.
- [SRS-TA-0005]{SyAD-0007} The software shall be able to parse the markdown format documents/implemented code (if applicable)/test cases (if applicable) to get the upstream relationship.
- [SRS-TA-0006]{SyAD-0013} The software shall, with the help of the configuration file indicates where the documents are, be able to scan all the document to get a complete set of traceable items with upstream relationship.
- [SRS-TA-0003]{SyAD-0010,SRS-TA-0005,SRS-TA-0006} The software shall be able to calculate downstream relationship based on the provided upstream relationship.
- [SRS-TA-0002]{SyRS-0089} The software shall be able to find traceability items which don't have common downstream items with others.
- [SRS-TA-0004]{SRS-TA-0002,SRS-TA-0003} The software shall be able to calculate item pairs' common downstream items based on the upstream/downstream relationship.
- [SRS-TA-0014]{SyAD-0027} The traceability relationship shall be saved to the database.
- [SRS-TA-0007]{SyAD-0011,SyAD-0006} The software shall raise exceptions if the traceable item tag is not unique.
- [SRS-TA-0012]{SyAD-0011} The software shall raise exceptions if the marked upstream item doesn't exist project-wise.
- [SRS-TA-0008]{SyAD-0011} The software shall raise exceptions if the traceable items form loop relationship.
    - *(Should it also analyze non-normalized relationship, like a->b->c but also a->c? Even if we do, we may just raise warnings rather than exceptions.)*
    - *(TODO: We should rethink this traceability relationship by using the same theory of normal forms.)*
- [SRS-TA-0015]{SyAD-0011,SyAD-002} If any exception has been raised while executing this software, the commit itself is still saved to the system, but that commit will be marked with an associated error message, while no traceability-related data shall be saved to the database.
    - This means the traceability analysis is post-`git push`.
    - It is like CI that even if test fails, the commit is still updated.
    - The other possibility is to have pre-`git push` commit, and reject the commit to go into the git server if anything goes wrong. The reason of not doing it includes:
        - Since we'll also provide the client side traceability validator, so users are not likely to push bad commits.
        - The system may (1) be used in repositories with historical bad commits, and (2) may integrate to (code) git platform for which the `git push` already happened with bad commit. We are doing this to keep best flexibility and independency.

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
