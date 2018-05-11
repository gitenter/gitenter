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

#### User interface

#### Software interface

#### Hardware interface

#### Communications interfaces

#### Memory constrains

#### Operations

#### Site adaptation requirements

### Functions

- [SRS-TA-0011]{SyAD-0018} The software shall be triggered every time a new commit has been pushed to the git server.
    - *(Should it be done before or after push: Before -- inconsistent commit will not be included, but then user need to do more complicated git operation to reverse the previous commit. After (like CI that even if test fails the commit is still updated, just have a fail flag) may cause several unnecessary commit, but can explicitly show in the web UI what's going wrong.)*
- [SRS-TA-0009]{} The software shall trigger the analyses if the documents have been changed.
- [SRS-TA-0010]{SRS-TA-0009,SyAD-0047} The software shall return a boolean value whether the documents changes has been involved in the new commit, by checking if all files returned by `git diff` are document/document accessories (images, ...).
- [SRS-TA-0001]{SyRS-0010} The software shall be able to find the upstream and downstream items of each traceable item.
- [SRS-TA-0003]{SRS-TA-0001,SyAD-0009} The software shall be able to calculate downstream relationship from upstream relationship.
- [SRS-TA-0005]{SRS-TA-0003,SyAD-0007} The software shall be able to parse the markdown format documents to get the upstream relationship.
- [SRS-TA-0006]{SRS-TA-0003,SyAD-0013} The software shall be able to scan all the document to get a complete set of traceable items with relationship, based on the configuration file.
- [SRS-TA-0002]{SyRS-0089} The software shall be able to find items which has common downstream items for each traceable item.
- [SRS-TA-0004]{SRS-TA-0002,SRS-TA-0003} The software shall be able to calculate item pairs' common downstream items based on the upstream/downstream relationship.
- [SRS-TA-0014]{SyAD-0027} The traceability relationship shall be saved to the database.
- [SRS-TA-0013]{SRS-TA-0014} The software shall raise error when something goes wrong while analyzing the document, and don't write any traceability data into the database.
    - *(If it is post git push, the error message should be saved.)*
- [SRS-TA-0007]{SRS-TA-0013,SyAD-0006} The software shall raise exception if the traceable item tag is not unique.
- [SRS-TA-0012]{SRS-TA-0013} The software shall raise exception if the marked upstream item doesn't exist project-wise.
- [SRS-TA-0008]{SRS-TA-0013} The software shall raise error if the traceable items form loop relationship.

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
