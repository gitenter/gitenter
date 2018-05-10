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

User/repository management:

- [SRS-WS-0057]{SyAD-0013,SyAD-0023,StRS-0057} The software shall give the user an option to setup the configuration files automatically when a new repository is created, or leave it for the user to setup later.

Document visualization:

- [SRS-WS-0010]{SyAD-0023} The software shall provide an index page to:
    - Show the readers the relationship between documents.
    - Provide readers shortcuts to navigate to targeting documents.
- [SRS-WS-0012]{SyAD-0028} The software shall show markdown format in a presentative way.
- [SRS-WS-0015]{SRS-WS-0012} The software shall fully support internal and external hyperlinks defined in markdown format.
- [SRS-WS-0013]{SRS-WS-0012} The software shall be able to display local images referred in markdown pages.
- [SRS-WS-0014]{SRS-WS-0013} The software shall have a opinion to show files (images, ...) under particular commit version in their raw format.
- [SRS-WS-0016]{SRS-WS-0012,SyAD-0007} The software shall visualize normal markdown items, and special syntax defined by this software in a compatible way.
- [SRS-WS-0017]{SRS-WS-0016} The software shall visualize traceable item tag in a styled way.
- [SRS-WS-0021]{SRS-WS-0016} The software shall visualize traceable item comments in a styled way.
- [SRS-WS-0018]{SRS-WS-0017,SyRS-0010} The software shall provide hyperlink for both upstream and downstream items, with the destination of the link point to the corresponding line of the targeting item.
- [SRS-WS-0048]{SyRS-0010} The software shall optimize traceability visualization if the items in relationship are in the same document.
- [SRS-WS-0019]{SRS-WS-0018,SRS-WS-0048} For tree structure items (items which only have one upstream item), there should provide an optional tree-view along with the hyperlink view.
    - *(Shall the hyperlink view be hided in that case?)*
- [SRS-WS-0033]{SyRS-0089} For each item, the software shall list items with common downstream items with it (but neither of the two is upstream item of the other). When an item has none of these items in the list, it should be qualified for (1) microservice architecture and (2) feature flags.
    - A pure tree structure contributes to zero common downstream items.
    - *(We may then show a squared matrix that both row and column are the same list of traceable items. One matrix item is the count of common downstream items of two items. Ideally all matrix items shall be zero. May be used for a single document/same level of items. Notice that traceable items in the same document may refer to each other, this may be acted as the upmost or down-most items of a single document.)*
- [SRS-WS-0020]{SRS-WS-0016,SyRS-0044,SyRS-0045} The software shall provide hyperlink to the implementing code pieces and the test cases.
    - May link to the corresponding page in the integrated code revision system, with the correct line number provided.
- [SRS-WS-0034]{SyRS-0045,SyRS-0091} Next to the traceable item, the software shall shown the corresponding (unit)test status integrated from some continuous integration platform.
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

- [SRS-WS-0011]{SyAD-0032,SyAD-0034,SyAD-0035,SyAD-0039} The software shall provide different default commit (when both a commit number and a branch name is missing) based on different user roles:
    - For document editor, the newest commit edited by the current user should be returned.
    - For document reviewer, the to-be-reviewed commit should be returned.
    - For document reader (e.g. software engineer), the newest approval version should be returned.
    - *(Then, although "anybody who can read can review", we do have a reason to distinguish reviewers and readers. How?)*
- [SRS-WS-0002]{SyRS-0087} The timestamp of last modification shall be shown in each document.
- [SRS-WS-0001]{SyRS-0087} Author(s) list shall be shown in each document.
- [SRS-WS-0005]{SyRS-0087} The document shall be easily switched between different versions.
- [SRS-WS-0006]{SRS-WS-0005,SyAD-0048} Switch to a different (historical/draft) version can be done by providing a commit ID.
- [SRS-WS-0007]{SRS-WS-0005,SyRS-0065,SyAD-0035} Switch to a historical version can be done by providing a git tag (in case exists -- by either generated by this software, or created by other places like git client command line).
- [SRS-WS-0008]{SRS-WS-0005,SyAD-0033} Switch to a draft version can be done by providing a git branch name.
- [SRS-WS-0009]{SRS-WS-0005,SRS-WS-0011} Switch to the default version shall be done by a shortcut.
- [SRS-WS-0003]{SyRS-0087} There shall be an option to show detailed historical timestamps with authors.
- [SRS-WS-0004]{SyRS-0087} There shall be an option to show the detail of which author write/in charge of which part of the document.
    - *(GitHub has another function "blame", for which user can get the one-step-earlier version of of each line. Also the commit message is involved. We hold these possibility right now. Based on the data saved in git, there may be multiple ways to provide a more vivid way to show the document evolution history, and detailed analysis is needed for what is the most effective and user friendly way of doing that.)*

Reviewing:

- [SRS-WS-0058]{} A reviewing summary page shall be provided for user to navigate all the relevant things of a particular review.
- [SRS-WS-0045]{SyRS-0064,SRS-WS-0058} The software shall provide a list historical review meetings, with the link to the review summary page.
- [SRS-WS-0022]{SyRS-0018,SyRS-0056,SyAD-0041,SyAD-0034} For any review subsection (not only the first one in a series), project organizer shall setup reviewing links to a particular commit (contains to-be-reviewed drafts) a non-master branch.
    - The commit is typically at the end of a particular branch.
- [SRS-WS-0060]{SRS-WS-0058,SRS-WS-0022} The reviewing summary page is setup when the user setup the first subsection of the whole review series.
- [SRS-WS-0061]{SRS-WS-0058,SRS-WS-0022} The reviewing summary page shall list to all review subsections.
- [SRS-WS-0023]{SyRS-0063,SyAD-0041} For the first reviewing subsection, project organizer shall setup a set of to-be-reviewed documents, which may be not the entire document set.
- [SRS-WS-0067]{SRS-WS-0023} The reviewing summary page shall link to all the actively commenting documents.
- [SRS-WS-0024]{SyRS-0056,SyRS-0052,SyAD-0041} For each review subsection, project organizer shall setup a certain amount of time for people to review, and/or review meeting schedule.
- [SRS-WS-0025]{SyRS-0077,SyAD-0041} For the first reviewing subsection, project organizer shall setup a list of suggested reviewers, as well as the parts they are in charge of holding the liability of the correctness for a particular reviewing.
- [SRS-WS-0050]{SRS-WS-0025} Project organizer may add suggested reviewers anytime before the reviewing is finalized.
- [SRS-WS-0026]{SyRS-0088,SyAD-0041,SRS-WS-0025} All readers (no matter whether they have been assigned as reviewers or not) could come and join the reviewing activity as reviewers.
- [SRS-WS-0027]{SyRS-0048,StRS-0057} Reviewers shall be able to comment on some particular line of the draft documents through a web interface.
    - It is not limited to only traceable items, because (1) people do have different opinions on anywhere of the document, and (2) this still works for user who want to use the review feature, but not the traceability feature.
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
- [SRS-WS-0047]{SyRS-0048,SyRS-0094,SRS-WS-0024} The software shall let reviewers to choose status of each document. Status can be given (and changed) any time before/after that reviewer and/or other people give comments, but need to be finalized before the deadline of the current reviewing subsection.
    - Personal status includes:
        - Approval.
        - Approval with postscripts: Comments/questions raised but changes have been decided to rollover to the next reviewing.
        - Request changes.
        - Denial.
    - "Draft" cannot be switched to.
- [SRS-WS-0037]{SyRS-0052,SyRS-0026} The software shall be able to generate a paper-based snapshot of the to-be-reviewed documents in `pdf` format, (1) to be used for a traditional review meeting, and/or (2) to fit into a company's the existing work flow in case exists.
- [SRS-WS-0038]{SRS-WS-0037,SyRS-0064,SyRS-0065} In the `pdf` version of reviewing draft, the following items shall be explicitly marked:
    - Author(s).
    - Snapshot date/reviewing deadline.
    - Version/subversion.
    - Line numbers.
- [SRS-WS-0039]{SRS-WS-003} The `pdf` to-be-reviewed document may have customized header/footer, to fit into the official document format of some particular company.
- [SRS-WS-0040]{SyRS-0052} The software shall provide a recorder of the traditional review meetings through the web interface.
- [SRS-WS-0042]{SRS-WS-0040} Meeting recorder may record the discussions on the review meeting, based on a particular line of the draft document.
- [SRS-WS-0043]{SRS-WS-0040,SyRS-0058} The software shall provide a display page that can provide the review meeting attendees (1) a synchronization of what part is currently under discussion, (2) existing web-based discussions around that part of the document, and (3) the dynamic records.
- [SRS-WS-0041]{SRS-WS-0040} The review meeting record shall be linked to the to-be-reviewed commit.
- [SRS-WS-0049]{SyRS-0097,SRS-WS-0024} The software shall provide an interface for project organizer to keep/change document status in case for the current in-reviewing commit in one of the following cases:
    - The reviewing deadline has approached.
        - The reviewer may refer to the personal status submitted by each reviewer.
        - Since there's no universal way to judge (does the majority decide that, or any negative opinion can keep a document to not be approved), we keep this process manual.
    - At the end of a review meeting.
        - *(Should be at the meeting/recording end of every document?)*
        - *(May integrate to the meeting recorder.)*
- [SRS-WS-0064]{SRS-WS-0049} The overall document status cannot be changed ones made, unless we starts a new review subsection and revise it again.
- [SRS-WS-0052]{SRS-WS-0049} Review comments shall be cleared when the document is switched status, and a snapshot of the comment is made. Otherwise, review comments shall be rollover to a new subsection.
    - This snapshot shall be made using the timestamp of the previous setup deadline/review meeting. This is to avoid the constantly changing comments after the deadline affect the switching decision (and you never know which comment is included while make the decision). Notice that the actual decision timestamp is always later compare to the deadline/review meeting starting time. Also, that makes (even if the decision of different documents are made at different times) the snapshot of the documents are always in the same timestamp.
    - *(Technically how to do the second part especially the corresponding line has been removed in the newer version?)*
- [SRS-WS-0071]{SRS-WS-0052} Editor may marked the snapshotted comments with the following status:
    - Corrected.
    - No change needed.
    - No reason to provide other choices such as different opinions (as then need to provide details), as users (include editors) can still comment on that line, and the comments rollover to the next subsection.
- [SRS-WS-0063]{SyRS-0097,SRS-WS-0061} The review summary page shall show the end status of each document for any expired subsection(s), and keep tracking the status of each document of newest subsection.
- [SRS-WS-0066]{SRS-WS-0052,SRS-WS-0061} The review summary page shall link to the review comment snapshot for the status-changed documents/cleared comments.
- [SRS-WS-0051]{SyAD-0043,SRS-WS-0022,SRS-WS-0024} After the documents has been updated, the project organizer may start a new review subsection, and assign a new deadline/review meeting schedule.
- [SRS-WS-0065]{SRS-WS-0049,SRS-WS-0051} Project organizer are no longer allowed to change document status for the old subsection when a new subsection is setup.
- [SRS-WS-0053]{SRS-WS-0027} Reviewer shall be able to comment on any involved document at any time before the finalization of the entire reviewing.
    - Reviewer may comment on "Approval"/"Approval with postscripts"/"Denial" documents (but the comment itself will not be mixed with the previous ones), since document/status may still be changed.
    - Reviewer may comment on any document version which has an associated review subsection. The comments on the old subsection shall just automatically rollover to the newest subsection.
        - Since the reviewing summary page will not provide a link to the reviewing page of the old versions, comment on old versions don't typically happen. However, this can be robust for the situations that:
            - Document has been changed when a new reviewing subsection is setup, but the old version didn't been switched to different status previously (e.g. doesn't been able to touched in the happened review meeting).
            - Comments made after the deadline but before the new subsection starts.
        - It doesn't matter if there is a snapshot for a newer version of this document. Just that particular document will rollover to a newer snapshot.
        - Since (1) the snapshot is made using the timestamp of the previous setup deadline/review meeting, and (2) project organizer are no longer allowed to change document status for the old subsection when a new subsection is setup, comments/decisions makes far late after the deadline will not been mixed up.
    - Reviewer can still comment if the older subsection has ended but the new subsection has not been started yet.
- [SRS-WS-0054]{SyRS-0093} When all the documents are in status of either "Approval", "Approval with postscripts", or "Denial", the software shall ask the project organizer to finalized the reviewing.
- [SRS-WS-0055]{SRS-WS-0054,SyAD-0045,SyAD-0035} The software shall (1) automatically reverted the "Denial", (2) a overall reviewing version number will be automatically generated/user defined, and (3) merge to the master branch when the reviewing is finalized.
- [SRS-WS-0068]{SRS-WS-0055} The default version number is an auto-increasing integer starting from one.
    - *(Should we do something similar with Semantic Versioning? But to notice that document change has little possibility to be a small change.)*
    - *(For software product, should this version be matching with the software version?)*
- [SRS-WS-0044]{SRS-WS-0055} User shall be given the option to add a git tag on the merging event commit (in the master branch) when she finalize a reviewing through the web interface.
    - The git tag is optional, since we fully record all related commit IDs of events in the database. However, user is given this option to better work on the git repository without using our software.
- [SRS-WS-0056]{SRS-WS-0055,StRS-0057} The software shall be compatible to users who merge branch locally and then push to the remote server. The reverted the "Denial" part is lost, with a warning in the reviewing related page(s), and the status keeps to be "Denial" in the official version.
- [SRS-WS-0062]{SRS-WS-0061,SRS-WS-0024} The reviewing summary page shall show the status of each review subsections:
    - Active reviewing.
    - Deadline passed.
    - Finalized: for the very last subsection when the entire review series itself is finalized.
    - Expired:
        - One (the newest) subsection is in status of either active reviewing/deadline passed/finalized, while all the other subsections are expired.
- [SRS-WS-0046]{SRS-WS-0055,SRS-WS-0061,SyRS-0077,SyRS-0088} The reviewing summary page show shown the information of review finalization, include the information of:
    - Review finalization date.
    - Version.
    - Reviewers assigned by the authorized user, with each one has cleared issues (and hold the liability of the correctness of) which part.
    - Volunteering reviewers.
    - Approval signature of a project organizer/pertinent manager. *(Should it be here?)*
- [SRS-WS-0069]{} The review summary page shall link to the final version all the "Approval"/"Approval with postscripts" pages, and list the denial pages (with no link).
- [SRS-WS-0070]{SRS-WS-0069,SRS-WS-0066} For documents in status "Approval with postscripts", the associated comments shall be shown together with the comment.
    - The associated comments include any one made in this review series, include the ones been snapshotted and cleared earlier. This is to avoid the case that a document is previously marked as "Request changes" and comments has been cleared, but later on people realized it is too many workload to change in the near future, and turns it to "Approval with postscripts" again.
    - May later on provide an interface to choose which comment to include. This may be similar to the editor checkbox for comment snapshots, but we probably want to keep them independent to reduce dependencies.
- [SRS-WS-0072]{} The software shall be able to generate paper-based official documents in `pdf` format to fit into a company's the existing work flow in case exists.
- [SRS-WS-0073]{SRS-WS-0072} In the `pdf` version of official document, the following items shall be explicitly marked:
    - Review finalization date.
    - Version.
    - Author(s).
    - Reviewers (only in case they are involved in reviewing this document):
        - Assigned by the authorized user, with each one has cleared issues (and hold the liability of the correctness of) which part.
        - Volunteered.
    - Line numbers.
- [SRS-WS-0074]{SRS-WS-0072} The `pdf` official document may have customized header/footer, to fit into the official document format of some particular company.

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
