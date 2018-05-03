# System Requirements Specification

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

Regulatory:

General features (both in stakeholder requirement and in here) are includes as the soul of these standards. The reason to list these standards is to (1) provide templates, (2) fine-tune the detail of the product work flow, and (3) fine-tune the procedure to make this product more regulatory-certification friendly.

In general, there's no need to trace the key procedures such as requirement engineer or design into the items of these standards. But for the detailed items in downstream documents, we may want to trace back to some particular regulatory for their origins.

- [SyRS-0002]{StRS-0013,StRS-0010} The software shall support standards of software development.
- [SyRS-0004]{StRS-0003,SyRS-0002} The software shall support CMMI.
- [SyRS-0005]{SyRS-0002} The software shall support ISO/IEC/IEEE standards of software development.
- [SyRS-0006]{StRS-0003,SyRS-0005} The software shall support general SDLC standards, include:
    - ISO/IEC/IEEE 12207: Systems and software engineering -- Software life cycle processes.
    - ISO/IEC/IEEE 15288: Systems and software engineering -- System life cycle processes.
- [SyRS-0008]{StRS-0002,SyRS-0005} The software shall support requirement engineering standards, include:
    - ISO/IEC/IEEE 29148: Systems and software engineering -- Life cycle processes -- Requirements engineering.
    - IEEE 830: IEEE Recommended Practice for Software Requirements Specifications.
    - IEEE 1233: IEEE Guide for Developing System Requirements Specifications.
- [SyRS-0012]{StRS-0008,SyRS-0005} The software shall support design control standards, include:
    - IEEE 1016: IEEE Recommended Practice for Software Design Descriptions.
- [SyRS-0016]{SyRS-0005} The software shall support IEEE 1028: IEEE Standard for Software Reviews and Audits.
- [SyRS-0003]{StRS-0015} The software shall support medical device standards, include:
    - ISO 13485: Medical devices -- Quality management systems -- Requirements for regulatory purposes.
    - FDA's 21 CFR 820.30: Design Control Guidance for Medical Device Industry.
- [SyRS-0035]{StRS-0015,StRS-0010} The software shall support medical device software standards, include:
    - FDA's General Principles of Software Validation; Final Guidance for Industry and FDA Staff.
    - IEC 62304: Medical device software -- Software life cycle processes.
- [SyRS-0032]{StRS-0035,StRS-0010} The software shall support various standards for automotive development, includes:
    - ISO 26262. *(Should be only part 3-7 of it is relevant. Check later.)*
    - IEC 61508: Functional Safety of Electrical/Electronic/Programmable Electronic Safety-related Systems. *(This one seems quite general. It is not only used for the automotive industry.)*
- [SyRS-0033]{StRS-0016,StRS-0010} The software shall support various standards for nuclear power development, includes:
    - IEC 61839: Nuclear power plants - Design of control rooms - Functional analysis and assignment.

Revision control:

- [SyRS-0001]{StRS-0046,StRS-0004,StRS-0009,StRS-0045} The software shall be built on top of a revision control system, which helps:
    - Provide benchmark/version of documents.
    - Recording and monitoring the evolution history of documents.
    - Provide a platform for coordinating document editing jobs among multiple people.
    - (For document editors) provide a comparison between versions to facilitate document reviewing of a new/unapproved version.
    - (For document users) provide a comparison between different document benchmark versions.
- [SyRS-0061]{SyRS-0001,StRS-0047} The software shall provide revision control for individual documents by giving both a global version, and a document-wise version.
    - The global version tends to be more important, as the documents are highly entangled to each other by traceable items.
    - Some global versions may have corresponding review activities.
- [SyRS-0087]{SyRS-0001} The evolution history of the documents shall be queryable from the software.
- [SyRS-0026]{SyRS-0001} The software shall distinguish document changes/commits of various level:
    - Regular backups by author(s).
    - Pending changes/different opinions.
    - To-be-reviewed draft.
    - Review meeting approval as a group/management decision.
- [SyRS-0019]{StRS-0049} The software shall be flexible to the case that only part of files under revision control are documents.
- [SyRS-0038]{SyRS-0019,StRS-0013} The software shall be able to separate document and code-only changes.
- [SyRS-0028]{StRS-0012,SyRS-0019} The software shall provide option for automatic synchronization with code revision control platforms.

Document formatting:

- [SyRS-0034]{StRS-0036,StRS-0037} The documents shall be written using a markup language.
- [SyRS-0031]{StRS-0010} The software shall provide document templates based on the regulatory standards. *(Or should we go the other direction to "compile" the documents to regulatory standards?)*

Traceability:

- [SyRS-0070]{StRS-0044} A traceable document item shall be either a requirement or a design decision.
- [SyRS-0071]{SyRS-0070} A traceable document item is one (or two) short sentence, which may contains an enumerated list.
- [SyRS-0072]{SyRS-0071} A traceable document may have associated comments (e.g. in deep explanation of concerns).
- [SyRS-0074]{StRS-0044} A traceable item may have multiple upstream and multiple downstream items.
- [SyRS-0050]{SyRS-0074} The upstream items may be prioritized. *(Should they?)*
- [SyRS-0073]{SyRS-0074} Individual traceable items has no or loose order, for which the order comes from the constrain that the upstream items shall come first.
- [SyRS-0075]{SyRS-0070} Related traceable items, or traceable items about a same aspect of the product, shall be grouped together.
- [SyRS-0076]{SyRS-0070,SyRS-0031} A traceable document item may be surrounded by the descriptive parts of the document, which may follow some kind of document templates.
- [SyRS-0036]{SyRS-0074,SyRS-0034} A plain-text-based tagging system is used to handle traceability between document items.
    - *(Is there any possibility to trace graph items?)*
- [SyRS-0069]{SyRS-0036} The software shall provide an easy way for tag renaming.
- [SyRS-0010]{StRS-0044} The software shall provide user shortcuts/hyperlinks to both the upstream and downstream items of the current document item.
- [SyRS-0051]{SyRS-0074,SyRS-0010} For tree structure items (items which only have one upstream item), there should be an option to rebuild the tree (rather than use the not-quite-virtualizable tag links).
- [SyRS-0043]{StRS-0018,SyRS-0019} The software shall be able to build traceability relationship between documents and non-document files (if applicable).
- [SyRS-0044]{StRS-0039,SyRS-0043} The software shall be able to build traceability relationship between design items and implementing code pieces.
- [SyRS-0045]{StRS-0039,SyRS-0043} The software shall be able to build traceability relationship between design items and test cases.
- [SyRS-0057]{SyRS-0019} In case the non-document files are either not included or not obey the rule, the software shall be able to mark the completeness of documented traceable items.

Reviewing:

- [SyRS-0018]{StRS-0045} Authorized user shall setup some particular to-be-reviewed commit.
- [SyRS-0063]{StRS-0045,StRS-0048} Authorized user shall setup a set of to-be-reviewed documents, which may be not the entire document set. Some documents may be modified but not in case to-be-reviewed, e.g., the ones which has been triggered by tiny modification for the traceability reasons.
- [SyRS-0056]{StRS-0045} Authorized user shall setup a certain amount of time for people to review.
- [SyRS-0077]{StRS-0045} Authorized user shall setup a list of reviewers.
- [SyRS-0046]{StRS-0045} Document reviewing activities shall be done either through a directly using of this software, or in a traditional review meeting way.
- [SyRS-0047]{SyRS-0046} The software shall provide an online reviewing system.
- [SyRS-0048]{SyRS-0047,SyRS-0026,SyRS-0018} Users shall be able to review and comment on the to-be-reviewed documents through a web interface.
    - This is different to the GitHub code review workflow in several aspects: (1) Review comment is linked to a marked position of a frozen snapshot of document, rather than a marked position of document changes. (2) Reviewing is by default turned off, and is only turned on for special cases. The reason is that's the scenario provided reviewing and auditing regulatory. The other reason is design documents are in general in global scope, and have complicated internal relationship; on the other hand, code (a majority of bugs in code) is mostly local scope. It is really costly to sit down and discuss in among multiple stakeholders, and it is hard for people to follow these discussions all the time. Definitely this is a really waterfall approach which is contradict with the sprint of continuous-X. The possibility of a more Agile styled requirement and design review workflow can be discuss later.
- [SyRS-0079]{SyRS-0048} User comments shall be on some particular line of the document.
    - *(Or on traceable item only?)*
    - *(What about comments on graphs?)*
- [SyRS-0078]{SyRS-0048} User shall also be able to comment on other user's comment.
- [SyRS-0052]{SyRS-0046} The software shall provide tools for traditional review meetings.
- [SyRS-0053]{SyRS-0052,SyRS-0026} The software shall be able to generate a paper-based snapshot of the to-be-reviewed documents in PDF version, with the following items explicitly marked.
    - Author(s).
    - Date.
    - Status/version.
    - Line numbers.
    - *(Should the upstream/downstream items be hyperlinked in the PDF file?)*
- [SyRS-0054]{SyRS-0052} The software shall provide tools for recording the review meetings.
- [SyRS-0080]{SyRS-0054} The review meeting record shall be linked to the to-be-reviewed commit.
- [SyRS-0058]{SyRS-0048,SyRS-0054} The software shall support a hybrid approach that the traditional review meeting after online reviewing for the controversial discussions.
- [SyRS-0059]{StRS-0045} The system shall record the authorized user approval event at the end of every reviewing process, include the information of:
    - A list of reviewers, with each one has cleared issues (and hold the liability of the correctness of) which part.
    - Approval signature of a pertinent manager.
- [SyRS-0064]{} Each document under certain commit of the revision control system, shall be in one of the following status:
    - Draft.
    - In review.
    - Approval.
    - Expired.
- [SyRS-0065]{SyRS-0061,SyRS-0063,SyRS-0064} For documents in status of approval or expired, (1) a version based on the corresponding review meeting, and (2) date of issues shall be provided.
    - A version based on the associated review meeting corresponds to a global version, but only part of the documents are included in the review activity. Even if there is an approval reviewing of that particular commit, one particular document may not be actually included, so it should be in status draft.
- [SyRS-0066]{SyRS-0064} For documents in status of approval or expired, the reviewer(s) and the pertinent manager(s) show be marked with it.
    - Reviewer(s) is per-document.
    - Pertinent manager(s) is per-reviewing activity.
- [SyRS-0068]{SyRS-0064} All documents in draft or in reviewer status shall be linked to its newest approval version.
    - The newest approval version of a particular document may be order then the last approval reviewing (even if it has been modified), as it may not be included in that reviewing.

Requirement analysis:

- [SyRS-0029]{StRS-0002,StRS-0018} The software shall provide tools to analyze various properties of requirement items, includes:
    - Stakeholder priority.
    - Risk.
    - Rationale.
    - Difficulty.

Authorization:

- [SyRS-0022]{StRS-0011} The software shall provide authorization control of who can read, review, and/or edit the documents.
- [SyRS-0040]{SyRS-0038,SyRS-0022} The software shall separate the edit authorization of document and code. *(This is challenging)*
- [SyRS-0039]{StRS-0011,SyRS-0059} The software shall provide authorization control who can approve the benchmark of the document.

### Usability requirements

- [SyRS-0021]{StRS-0020,StRS-0006} Local applications provide advanced tools for user to edit the documents.
- [SyRS-0042]{} The software shall provide a public API.

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

SaaS:

- [SyRS-0084]{StRS-0033} The software shall be able to save/centralize operational data in the cloud.

### Policies and regulations

### System life cycle sustainment

### Packaging, handling, shipping and transportation

SaaS:

- [SyRS-0083]{StRS-0033} The software shall have a pipeline to be deployed to a cloud computing platform.

Local hosting:

- [SyRS-0085]{StRS-0034} The software shall include a (semi-)automatic installation pipeline.

## Appendices

### Assumptions and dependencies

## Bibliography

1. ISO/IEC/IEEE 29148:2011, System and software engineering -- Life cycle processes -- Requirement engineering.
2. IEEE Std 1233:1998 Edition(R2002), IEEE Guide for Developing System Requirements Specifications.
3. ISO/IEC/IEEE 15288:2008, System and software engineering -- System life cycle process.
