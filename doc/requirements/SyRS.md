# System Requirements Specification

## Document identifier

**Date of issue:**

**Issuing organization:**

**Author(s):**

**Approval signatures:**

**Status/version:**

**Reviewers/pertinent managers:**

## Introduction

### System purpose

*(Define the reason(s) for which the system is being developed or modified.)*

### System scope

**System name:** Enterovirus.

**Results of finalized needs analysis:** *((1) A brief but clear expression of the user's problem(s). (2) The system will and will not do to satisfy those needs.)*

**Application of the system:** *(All relevant top level benefits, objectives, and goals.)*

### System overview

#### System context

*(Describe at a general level the major elements of the system, to include human elements, and how they interact. The system overview includes appropriate diagrams and narrative to provide the context of the system, defining all significant interfaces crossing the system's boundaries.)*

#### System functions

*(Describe major system capabilities, conditions and constraints.)*

#### User characteristics

*(Identify each type of user/operator/maintainer of the system (by function, location, type of device), the number
in each group, and the nature of their use of the system.)*

## References

1. Stakeholder requirements specification (StRS).

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## System requirements

### Functional requirements

- [SyRS-0002]{StRS-0013,StRS-0010} The software shall support standards of software development.

Regulatory:

- [SyRS-0004]{StRS-0003,SyRS-0002} The software shall support CMMI.
- [SyRS-0005]{SyRS-0002} The software shall support various ISO/IEC/IEEE standards of software development.
- [SyRS-0006]{StRS-0003,SyRS-0005} The software shall support general SDLC standards, include:
    - ISO/IEC/IEEE 12207: Systems and software engineering -- Software life cycle processes.
    - ISO/IEC/IEEE 15288: Systems and software engineering -- System life cycle processes.
- [SyRS-0008]{StRS-0002,SyRS-0005} The software shall support various requirement engineering standards, include:
    - ISO/IEC/IEEE 29148: Systems and software engineering -- Life cycle processes -- Requirements engineering.
    - IEEE 830: IEEE Recommended Practice for Software Requirements Specifications.
    - IEEE 1233: IEEE Guide for Developing System Requirements Specifications.
- [SyRS-0012]{StRS-0008,SyRS-0005} The software shall support various design control standards, include:
    - IEEE 1016: IEEE Recommended Practice for Software Design Descriptions.
- [SyRS-0016]{SyRS-0005} The software shall support IEEE 1028: IEEE Standard for Software Reviews and Audits.
- [SyRS-0003]{StRS-0015,StRS-0010} The software shall support various standards for medical device development, includes:
    - ISO 13485: Medical devices -- Quality management systems -- Requirements for regulatory purposes.
    - FDA's 21 CFR 820.30: Design Control Guidance for Medical Device Industry.
    - FDA's General Principles of Software Validation; Final Guidance for Industry and FDA Staff.
    - IEC 62304: Medical device software -- Software life cycle processes.
- [SyRS-0032]{StRS-0035,StRS-0010} The software shall support various standards for automotive development, includes:
    - ISO 26262. *(Should be only part 3-7 of it is relevant. Check later.)*
    - IEC 61508: Functional Safety of Electrical/Electronic/Programmable Electronic Safety-related Systems. *(This one seems quite general. It is not only used for the automotive industry.)*
- [SyRS-0033]{StRS-0016,StRS-0010} The software shall support various standards for nuclear power development, includes:
    - IEC 61839: Nuclear power plants - Design of control rooms - Functional analysis and assignment.

Features:

- [SyRS-0001]{StRS-0004,StRS-0009} The software shall be built on top of a revision control system.
- [SyRS-0034]{StRS-0036,StRS-0037} The document shall be using some kind of markup languages.
- [SyRS-0019]{StRS-0001,SyRS-0001} The software shall be flexible to the case that only part of files under revision control are quality control documents.
- [SyRS-0031]{StRS-0001,StRS-0010} The software shall provide templates of the documents based on the regulatory requirements.
- [SyRS-0028]{StRS-0012,SyRS-0019} The software shall provide APIs with code revision control platforms.
- [SyRS-0026]{SyRS-0001} The software shall distinguish document changes of various level:
    - Regular backups by authors.
    - Pending changes/different opinions.
    - Changes approved as a group decision.
    - Separate code and document changes. 
    - ...
- [SyRS-0022]{StRS-0011} The software shall provide authorization control of who can edit/approval changes of the documents.
- [SyRS-0010]{StRS-0018,SyRS-0006} The software shall provide tools related to the traceability of requirement/design items.
- [SyRS-0029]{StRS-0002,StRS-0018} The software shall provide tools to analyze various properties of requirement items, includes:
    - Stakeholder priority.
    - Risk.
    - Rationale.
    - Difficulty.
- [SyRS-0030]{StRS-0002,StRS-0008,StRS-0018} The software shall provide tools to help tracking whether a requirement/design has been implemented and/or tested.
- [SyRS-0024]{StRS-0018,SyRS-0016} The software shall provide a flexible way of various review activities, includes:
    - A formal review meeting.
    - Discussions on top of a web service.
- [SyRS-0018]{StRS-0011,SyRS-0016} The software shall support authorized user to setup some frozen time stamp as a milestone of the development life cycle.
- [SyRS-0025]{SyRS-0018,SyRS-0024} Formal review activities can only be based on the a frozen time stamp/milestone.
- [SyRS-0023]{SyRS-0024} The software shall support recoding the meeting moments of the formal review meetings.
- [SyRS-0017]{SyRS-0024,SyRS-0018} The software shall support various different users to submit comments, which will be gathered and later discussed in a review meeting.
- [SyRS-0027]{SyRS-0024} The software shall support users to make discussions on documents and changes.

### Usability requirements

- [SyRS-0020]{StRS-0001,StRS-0006,SyRS-0017} Web services shall be sufficient for user to (1) view and (2) comment on documents.
- [SyRS-0021]{StRS-0020,StRS-0006} Local applications provide advanced tools for user to edit the documents.

### Performance requirements

### System interface

### System operations

#### Human system integration requirements

#### Maintainability

#### Reliability

### System modes and states

### Physical characteristics

#### Adaptability requirements

### Environmental conditions

### System security

### Information management

### Policies and regulations

### System life cycle sustainment

### Packaging, handling, shipping and transportation

## Appendices

### Assumptions and dependencies

## Bibliography

1. ISO/IEC/IEEE 29148:2011, System and software engineering -- Life cycle processes -- Requirement engineering.
2. IEEE Std 1233:1998 Edition(R2002), IEEE Guide for Developing System Requirements Specifications.
3. ISO/IEC/IEEE 15288:2008, System and software engineering -- System life cycle process.
