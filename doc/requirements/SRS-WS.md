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
- [SRS-WS-0012]{SyAD-0028} The software shall show markdown format in a presentative way.
- [SRS-WS-0015]{SRS-WS-0012} The software shall fully support internal and external hyperlinks defined in markdown format.
- [SRS-WS-0013]{SRS-WS-0012} The software shall be able to display local images referred in markdown pages.
- [SRS-WS-0014]{SRS-WS-0013} The software shall have a opinion to show files (images, ...) under particular commit version in their raw format.
- [SRS-WS-0016]{SRS-WS-0012,SyAD-0007} The software shall parse the special syntax of traceable items, and provide a compatible way to represent them and normal markdown items.
- [SRS-WS-0017]{SRS-WS-0016,SyRS-0074} The software shall display both upstream and downstream items of a traceable item in a styled way.
- [SRS-WS-0021]{SRS-WS-0016} The software shall display the comments of traceable item in a styled way.
- [SRS-WS-0018]{SRS-WS-0017,SyRS-0010} The software shall provide hyperlink for both upstream and downstream items, with the destination of the link point to the corresponding line of the targeting item.
- [SRS-WS-0019]{SRS-WS-0017,SyRS-0051} For tree structure items (items which only have one upstream item), there should be an option to rebuild the tree (rather than use the not-quite-virtualizable tag links).
- [SRS-WS-0033]{SyRS-0089} For each particular item, the software shall show a list of items with common downstream items with it (but neither of the two is upstream item of the other). When an item has none of these items in the list, it should be qualified for (1) microservice architecture and (2) feature flags.
    - A pure tree structure contributes to zero common downstream items.
    - *(We may then show a squared matrix that both row and column are the same list of traceable items. One matrix item is the count of common downstream items of two items. Ideally all matrix items shall be zero. May be used for a single document/same level of items. Notice that traceable items in the same document may refer to each other, this may be acted as the upmost or down-most items of a single document.)*
- [SRS-WS-0020]{SRS-WS-0016,SyRS-0044} The software shall provide hyperlink to the implementing code pieces.
- [SRS-WS-0021]{SRS-WS-0016,SyRS-0044} The software shall provide hyperlink to the test cases.
- [SRS-WS-0034]{SyRS-0091,SRS-WS-0021} Next to the traceable item, the software shall shown the corresponding (unit)test status integrated from some continuous integration platform.
- [SRS-WS-0035]{SyRS-0057} The reader (developer/engineer) may manually marked the status (TODO/in progress/done) of each traceable item.
    - This is marked out of the document itself (rather than through special syntax in the document), because of the following reasons:
        - Otherwise the reader need the editing authority.
        - It gives more flexibility of complex management of who marked it and when.
        - It keeps the flexibility that the actual implementation may be either in or out of the software.
    - *(Should status go with traceable item tags (through different version of the software), or should they be limited to every single commit?)*
- [SRS-WS-0036]{SRS-WS-0035,SyRS-0092} The software shall be able to integrate to task management systems, include:
    - A shortcut to add a traceable item into the task management system.
    - Trace the status (TODO/in progress/done) of the corresponding task in the document next to the traceable item.

Versioning:

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

- [SRS-WS-0022]{SyRS-0018,SyAD-0041,SyAD-0034} Project organizer shall setup reviewing by submit a pull request from a commit (contains those drafts) of a different branch, to the master branch.
- [SRS-WS-0023]{SyRS-0063,SyAD-0041} Project organizer shall setup a set of to-be-reviewed documents, which may be not the entire document set.
- [SRS-WS-0024]{SyRS-0056,SyAD-0041} Project organizer shall setup a certain amount of time for people to review.
- [SRS-WS-0025]{SyRS-0077,SyAD-0041} Project organizer shall setup a list of suggested reviewers, as well as the parts they are in charge of holding the liability of the correctness for a particular reviewing.
- [SRS-WS-0026]{SyRS-0088,SyAD-0041,SRS-WS-0025} All readers (no matter whether they have been assigned as reviewers or not) could come and join the reviewing activity as reviewers.
- [SRS-WS-0027]{SyRS-0048} Reviewers shall be able to comment on some particular line of the draft documents through a web interface.
    - It is not limited to only traceable items, because (1) people do have different opinions on anywhere of the document, and (2) since comments are not rolled over to the next commit, there is technically no difficulty to do the generalization.
    - *(What about comments on graphs?)*
- [SRS-WS-0029]{SRS-WS-0027} Commenting shall be based on one single document.
- [SRS-WS-0028]{SyRS-0048,SyAD-0041} Reviewers and author(s) shall be able to discuss by commenting on others' comments.
- [SRS-WS-0030]{SRS-WS-0027,SRS-WS-0028} Reviewer may choice to show or hide other people's comments while reviewing, with the options of:
    - Show all.
    - Hide all.
    - White list.
    - Black list.
- [SRS-WS-0031]{SRS-WS-0027} If there are so many comments on the same document, the software shall provide paging.
    - That's to avoid a similar situation that GitHub becomes extremely slow when there are 100+ comments on the same pull request.
- [SRS-WS-0032]{SRS-WS-0027} The software shall provide a `diff`/comparison to the previous official version in the web reviewing page.
- [SRS-WS-0047]{SyRS-0048,SyRS-0094} The software shall let reviewers to choose status of each document. Status can be given (and changed) any time before/after that reviewer and/or other people give comments, but need to be finalized before the deadline.
- [SRS-WS-0037]{SyRS-0052,SyRS-0026} The software shall be able to generate a paper-based snapshot of the to-be-reviewed documents in `pdf` format, (1) to be used for a traditional review meeting, and/or (2) to fit into a company's the existing work flow in case exists.
- [SRS-WS-0038]{SRS-WS-0037,SyRS-0064,SyRS-0065} In the `pdf` version of reviewing draft, the following items shall be explicitly marked:
    - Author(s).
    - Snapshot date/reviewing deadline.
    - Version/subversion.
    - Line numbers.
- [SRS-WS-0039]{SRS-WS-003} The `pdf` document may have customized header/footer, to fit into the official document format of some particular company.
- [SRS-WS-0040]{SyRS-0052} The software shall provide a recorder of the traditional review meetings through the web interface.
- [SRS-WS-0042]{SRS-WS-0040} Meeting recorder may record the discussions on the review meeting, based on a particular line of the draft document.
- [SRS-WS-0043]{SRS-WS-0040} The software shall provide a display page that can provide the review meeting attendees (1) a synchronization of what part is currently under discussion, and (2) the dynamic records.
- [SRS-WS-0041]{SRS-WS-0040} The review meeting record shall be linked to the to-be-reviewed commit.
- [SRS-WS-0046]{StRS-0045,SyRS-0077,SyRS-0088} The system shall record the approval event at the end of every reviewing process, include the information of:
    - A list of reviewers assigned by the authorized user, with each one has cleared issues (and hold the liability of the correctness of) which part.
    - A list of volunteering reviewers.
    - Approval signature of a pertinent manager.
- [SRS-WS-0045]{SyRS-0064} The software shall provide a list historical review meetings, with the link to all approval documents based on that particular meeting.
- [SRS-WS-0044]{SRS-WS-0045,SyAD-0035} The approval version of the review meeting result, shall be marked by a `git` tag.

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
