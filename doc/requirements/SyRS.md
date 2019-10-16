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
- [SyRS-0006]{StRS-0003,SyRS-0005} The software shall support general standards, include:
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
    - (For document editors and reviewers) provide a comparison between a benchmark version and the most up-to-date version, to facilitate document reviewing of a new/unapproved version.
    - (For document users) provide a comparison between different document benchmark versions.
- [SyRS-0061]{SyRS-0001,StRS-0047} The software shall provide revision control for individual documents in both global and document-wise scopes.
- [SyRS-0098]{SyRS-0061} The revision control system shall provide a version number in global scope.
    - A global version is important, since the documents are highly entangled to each other by traceable items.
    - Some global versions may have corresponding specificities (reviewing/approval/...).
- [SyRS-0087]{SyRS-0001} The evolution history of the documents shall be queryable from the software.
- [SyRS-0026]{SyRS-0001} The software shall distinguish document changes/commits of various level:
    - Regular backups by author(s).
    - Pending changes/different opinions.
    - To-be-reviewed draft.
    - Review approval as a group/management decision.
- [SyRS-0102]{StRS-0049} The software shall have an explicit way to define/configure which file is document.
- [SyRS-0100]{StRS-0049} The software shall provide a dual view of (1) document-only, and (2) all files -- code, graphs, historical documents in other format ...
- [SyRS-0101]{SyRS-0100} The software shall be able to show non-document content (e.g. graphs) in document mode.
- [SyRS-0104]{SyRS-0100} In document-only mode, the software shall parse the actual document name (not filename) in the index page.
- [SyRS-0105]{SyRS-0100} In document-only mode, the software shall build the document relationship by references/dependencies (rather than folder structure). *(Should it be defined in a reference section of the documents themselves? Or a configuration file?)*
- [SyRS-0106]{SyRS-0100} In document-only mode, the software shall show work progress of each individual document. *(Should we? At least it seems should be in the web/not git folder layer.)*
- [SyRS-0038]{StRS-0049,StRS-0013} The software shall be able to separate document changes and changes with other files (code-only) involved.
- [SyRS-0028]{StRS-0012,StRS-0049} The software shall provide option for automatic synchronization with code revision control platforms.

Document formatting:

- [SyRS-0034]{StRS-0036,StRS-0037} The documents shall be written using a markup language.
- [SyRS-0031]{StRS-0010} The software shall provide document templates based on the regulatory standards. *(Or should we go the other direction to "compile" the documents to regulatory standards?)*

Traceability:

- [SyRS-0070]{StRS-0044} A traceable document item shall be either a requirement or a design decision.
- [SyRS-0071]{SyRS-0070} A traceable document item is one (or two) short sentence, which may contains an enumerated list.
- [SyRS-0072]{SyRS-0071} A traceable document may have associated comments (e.g. in deep explanation of concerns).
- [SyRS-0076]{SyRS-0070,SyRS-0031} A traceable document item may be surrounded by the descriptive parts of the document, which may follow some kind of document templates.
- [SyRS-0074]{StRS-0044} A traceable item may have multiple upstream and multiple downstream items.
- [SyRS-0050]{SyRS-0074} The upstream items may be prioritized. *(Should they?)*
- [SyRS-0073]{SyRS-0074} Individual traceable items has no or loose order, for which the order comes from the constrain that the upstream items shall come first in a same document.
- [SyRS-0075]{SyRS-0070} Related traceable items, or traceable items about a same aspect of the product, may be grouped together in the document. But this is not a mandatory input.
- [SyRS-0036]{SyRS-0074,SyRS-0034} A plain-text-based tagging system is used to handle traceability between document items.
    - *(Is there any possibility to trace graph items?)*
- [SyRS-0010]{StRS-0074} The software shall provide provide convenient tools to users to navigate between traceable items in both upstream and downstream directions.
- [SyRS-0089]{SyRS-0074} As independent features may benefit (1) microservice architecture and (2) feature flags, the software shall distinguish/isolate independent traceable items with no common downstream item with others.
    - A pure tree structure contributes to zero common downstream items.
- [SyRS-0043]{StRS-0018,StRS-0049} The software shall be able to build relationship between traceable items in documents.
- [SyRS-0044]{StRS-0039,SyRS-0043} The software shall be able to build traceability relationship between traceable items in documents and code in various languages.
- [SyRS-0091]{SyRS-0044} Traceability item in testing code include (1) Gherkin feature files used by Cucumber/behave, (2) markdown feature files used by Thoughtworks Gauge, and (3) unit tests in various languages, the test status from a continuous integration platform shall be integrated into this system.
- [SyRS-0103]{SyRS-0091} The testing results shall be displayed on level up to the leaf items in documents.
- [SyRS-0057]{StRS-0049} In case the non-document files are either not included or not obey the rule, the software shall be able to trace the completeness of traceable items through manual ways.
- [SyRS-0092]{SyRS-0057} The software shall be integrated to task management systems (which provide a TODO/in progress/done.
    - Examples include Trello and Atlassian JIRA.
    - No need to integrate to bug tracking systems, as bugs shall never appear in the requirement/design.
- [SyRS-0069]{SyRS-0036} The software shall provide an easy way for tag renaming.

Reviewing:

- [SyRS-0018]{StRS-0045} Authorized user shall start the review activity.
- [SyRS-0063]{StRS-0045,StRS-0048} Authorized user shall setup a set of to-be-reviewed draft documents, which may be not the entire document set. Some documents may be modified but not in case to-be-reviewed, e.g., the ones which has been triggered by tiny modification for the traceability reasons.
- [SyRS-0077]{StRS-0045} Authorized user shall setup a list of suggested reviewers, as well as the parts they are in charge of holding the liability of the correctness, for a particular review activity.
- [SyRS-0088]{SyRS-0077} Anybody who have the reading authority could come and join the review activity.
- [SyRS-0046]{StRS-0045} Document review activities shall be done either through software, or in a traditional review meeting way.
- [SyRS-0048]{SyRS-0046,SyRS-0026,SyRS-0018} The software shall provide an online reviewing system, for users to review and comment on the draft documents through a web interface.
- [SyRS-0052]{SyRS-0046} The software shall provide tools for traditional review meetings.
- [SyRS-0058]{SyRS-0048,SyRS-0052} The software shall support a hybrid approach that the traditional review meeting after online reviewing for the controversial discussions.
- [SyRS-0056]{StRS-0045} Each new review comes in a series of subsections, and the authorized user shall pace and setup a timeline of both (1) each subsection, and the entire review.
- [SyRS-0096]{SyRS-0056} A new review subsection is triggered by:
    - (Traditional review meeting only) a scheduled meeting in a timeframe: the meeting may only target part of the documents.
    - Document has been changed after the previous subsection.
- [SyRS-0094]{SyRS-0096} Document shall be in one of the following status in a review series:
    - Draft: Not started yet, or (Traditional review meeting only) unable to be touched in the current section.
    - Approval.
    - Approval with postscripts: Comments/questions raised but changes have been decided to rollover to the next review.
    - Request changes.
    - Denial.
- [SyRS-0097]{SyRS-0094} Documents may keep their current status, or change status at the end of each review subsection.
    - Suggested changing direction includes:
        - from "Draft" to "Request change".
        - from "Draft"/"Request changes" to "Approval"/"Approval with postscripts"/"Denial".
    - "Draft" cannot be switched to from any other opinions.
    - We cannot forbidden people to change files which is already approved (because of traceability tag consistency). Then we are facing the risk that documents may be changed to incorrect after it is being approved. Therefore, we keep the possibility of any change of status.
- [SyRS-0039]{SyRS-0094} Authorized user shall make the decision of review status based on everybody's opinion and relative discussions/comments.
    - *(Should we need a person to do it? Or it could be a simple system-based voting?)*
- [SyRS-0093]{SyRS-0097} Review is finalized when all documents are in status of either "Approval", "Approval with postscripts", or "Denial". None, part, or all included documents may be in each category.
- [SyRS-0064]{SyRS-0093} When there's a new review with the same document involved, the previous version of the document changed status from "Approval"/"Approval with postscripts" to "Expired" (no need to change "Denial").
    - A version based on the associated review meeting corresponds to a global version, but only part of the documents are included in that review activity. Even if there is an approval review of that particular commit, one particular document may not be actually included. So documents in status "Approval" may not turn "Expired" when a new review is in place.
- [SyRS-0065]{SyRS-0098,SyRS-0063,SyRS-0064} For documents in status of approval or expired, (1) a version based on the corresponding review meeting, and (2) date of issues shall be provided.
- [SyRS-0066]{SyRS-0064} For documents in status of approval or expired, the reviewer(s) and the pertinent manager(s) show be marked with it.
    - Reviewer(s) is per-document.
    - Pertinent manager(s) is per-review activity.
- [SyRS-0068]{SyRS-0064} All documents in any commit shall be linked to its newest approval version of that, in case that version exist.
    - *(How to know they are the same document? Filename matching?)*

Requirement analysis:

- [SyRS-0029]{StRS-0002,StRS-0018} The software shall provide tools to analyze various properties of requirement items, includes:
    - Stakeholder priority.
    - Risk.
    - Rationale.
    - Difficulty.

Authorization:

- [SyRS-0099]{StRS-0056} A repository can be either public or private.
- [SyRS-0022]{StRS-0011,StRS-0056} The software shall provide authorization control of who can read, review, and/or edit the documents.
- [SyRS-0040]{SyRS-0038,SyRS-0022} The software shall separate the edit authorization of document and code. *(This is challenging)*

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
