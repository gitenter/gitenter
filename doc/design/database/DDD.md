# Database Design Description

## Introduction

### Purpose

### Scope

### Overview

*(Supersede or interface with other databases)*

## References

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

+ **3NF:** Third normal form.
+ **CRUD:** Create, read, update, delete.
+ **DBMS:** Database management system.
+ **ERD:** Entity-relationship diagram.
+ **SQL:** Structured query language.

## Assumptions/constraints/risks

### Assumptions

### Constraints

### Risks

## Design Decisions

### Key consideration factors

#### Database boundary

*(Some positive corner cases are no in the current design. Since we haven't think about the details yet,they are not included. We could merge them all later in general columns.)*

#### Reliability concerns

#### SQL semantics rules

+ Proper **privilege control** to define the software modules who can access the database. *(Is the only aim of "schema" for privilege control?)*
+ For database queries by applications, all the queries should be **atomic**.
+ The database schema should follow the rule of **3NF** to some extend. Exceptions include:
	+ For some part which is easy to get it right, it is simply too much details to make it 3NF.
	+ Performance concerns to later flatten the relations. Analysis with confirmed performance bottleneck is needed.
+ A set of proper `VIEW`s shall be defined, for common queries.
+ **Single column primary key** shall be used throughout the schema design.
	+ Avoid using natural keys as primary key. The reasons are shown in [here](http://stackoverflow.com/questions/2204381/what-are-the-pros-and-cons-of-using-multi-column-primary-keys) and [here](http://www.databasejournal.com/features/mssql/article.php/3922066/SQL-Server-Natural-Key-Verses-Surrogate-Key.htm).
	+ For associative table (a.k.a., cross-reference table) implementing many-to-many relationships, we may use compound primary key to make it easy. That is only for *pure* many-to-many relationship. In the following cases we'll not use compound primary key:
		+ Other table is refer to that table.
		+ There are not only the `_id` foreign keys, but also other attribute associate with that table.
			+ The reason for this one, is because otherwise it will cause unnecessary difficulty on implementing data persistent layer using interface specifications such as Java Persistent API (JPA).
+ As part of the physical data model design/implementation, `UNIQUE`, `CHECK` constrain, `REFERENCE` (with `CASCADE`, `RESTRICT`, ...) shall be defined as complete as possible.
	+ To make constraints as strict as possible in the first implement design. If needed, we can always loose it later.
+ For realizing inheritance relations in SQL, there's generally no perfect solution.
	+ [Possible solutions](http://stackoverflow.com/questions/3579079/how-can-you-represent-inheritance-in-a-database):
		+ Single table inheritance (a.k.a. table per hierarchy inheritance)
		+ Concrete table inheritance
		+ Class table inheritance (a.k.a. table per type inheritance)
		+ ~~PostgreSQL inheritance tables~~: Not working for our purpose, since [it doesn't support foreign keys](https://www.postgresql.org/docs/9.6/static/ddl-inherit.html#DDL-INHERIT-CAVEATS).
	+ We'll generally go with "class table inheritance", but may choose other methods case-by-case.
+ General SQL realizations shall be preferred. However, we are okay to use PostgreSQL's support of special types.
	+ Special PostgreSQL supports are always neater, can have more mandatory checks. Hence our code implementation will be quicker, and contain less bugs.
	+ Since most of these special tools are used locally, a later switch back to general SQL will not cause changes of the entire architecture.
	+ If possible, we should write an additional note in the design document, about the alternative choice in SQL.
+ To constrain that we are working on the same database (with PostgreSQL's natural support on distributed configiuration), there's no need to consider using [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier)/[GUID](https://en.wikipedia.org/wiki/Globally_unique_identifier) as primary key. If we later need to merge multiple databases, we shall use data adapter to re-assign primary keys for all table items based on their former relations (rather than the exact former primary key).
+ Identifiers (tables, columns, views, ...) should be written entirely in **lower case letters**, with underscores in between to separate words.
	+ This is a common practice for SQL.
	+ To map the names to/from the CamelCase OOP classes, just lower the letters and and underscores the words.
+ Avoid quotes for identifiers. If you have to quote, mostly that's because you have forbidden characters (e.g., space). Just rename it.
+ Use **singular table name** (rather than plural table names). Practically it seems have more pros.
	+ Pros of plurals:
		+ Table is a set. Every row in the table is an object (columns = fields). In programming, you name the collections using plural names.
		+ Match the logic of `args`, `for every student in students`.
		+ Joe Celko suggests in [SQL Programming Style](http://rads.stackoverflow.com/amzn/click/0120887975). He cites ISO-11179-4.
	+ Cons of plurals:
		+ Name becomes weird for associative tables (if you just want to physically combine the two names, like `friends_gifts`).
		+ Objects can have irregular plurals or not plural at all, but will always have a singular one. E.g., `news`, `info`, `software`.
	+ Pros of singular:
		+ Match OOP objects.
		+ When you do `SELECT table_name.column_name FROM` it makes more sense.
		+ Specially in master-detail scenarios. E.g., `Order` then `OrderDetail` is better than `Orders` then `OrderDetails`.
		+ Uninflected noun.
	+ Cons of singular:
		+ Some singular words are reserved (e.g. `user` is reserved in PostgreSQL).
+ Associative table uses the naming rule `first-table-name_second-table-name_map`.
+ Attribute naming convention:
	+ `id` or `*_id` for SQL primary key or foreign key.
	+ `*_no`, ..., for keys refer to other system (not in this database), even if they are called ID in descriptive documents.
+ Physical unit will not be written clearly in column names. They need to be explicitly defined in related documents.
+ For the primary keys, the property `ON DELETE CASCADE` or `ON DELETE RESTRICT` has been defined properly, so the data belong to one single experiment or analysis can be removed easily as a whole.

#### Extensibility concerns

In the ideal case, requirement and design (of the whole system) are analyzed really carefully at the very beginning. Database design comes from those analysis, and it is fixed in the implementation and follow up phases. That is ideal, because changes database schema will trigger changes of the implemented code. What makes things worse, when the product is already in place of the customer, updates need to be compatible with the previous version; that means any database schema changes are on top of the risk of break the real data of the customer.

However, it is a crucial fact that we do not understand our system deep enough, but we have schedule pressure to give a working version to ask for feedback. Also, a good database design needs deep thoughts through different parts of of system, which is hard to collaborate at the current stage. Therefore, we decided to implement the design in some incremental way.

+ First provide a minimal setup of the schema, for which the happy path of the current implementation works.
	+ It is very very important that the kernel logic of this minimal setup is correct.
	+ We may brainstorming various concerns of the extension and/or the complete version of the database. However, that will be kept in the design document, but not the implementation.
+ For later implementations, hopefully we only need to add (1) new columns and (2) new tables to the database schema, hence do not need to alter any existing schema (hence any existing data).
	+ That may be the best we can do for (1) compatibility and (2) lower the risk of breaking existing data.
	+ By doing that, we should have no need to provide data adapter for updating.
+ By doing that, we need to be really careful in implementation. We should avoid using `SELECT *` and/or `SELECT column_number`, but explicitly using column names, to avoid potential bugs later.

### Functional design decisions

+ For un-allowed inputs, the database should direct raises errors and reject them.
	+ The software modules shall catch up the error code/message, and raise exceptions.
	+ Since all inputs should comes from well-defined user interface, database errors shall mostly reflect serious system bugs.
+ We choose **sequential data access** for various data operations.
	+ The alternative choice is random data access. It has better performance but may it technically more difficult to develop a reliable system.

### Database management system decisions

### Security and privacy design decisions

### Performance design decisions

### Maintenance design decisions

+ Database (structure of tables and columns, constrains of columns, and which columns should be indexing) should be set up one time, in the software system installation process.
+ For updating to future software versions, database schema may be altered. However, we'll try our best to limit the flexibility to add column and table only. No need to change the existing data.
+ Data reorganization (repacking, sorting, table and index maintenances, ...) should be minimized, or if possible, forbidden.
+ The entire database may be (automatically) periodically backed up. So if anything goes wrong, the recovery shall be possible. *(About that, we need to check about concepts such as: disaster recovery; security services; failover clustering; auto backups; replication...)*

## Detailed database design

### Logical data model

For ERD and implementation, (1) to get a quick working model, and (2) to avoid future changes, we'll do just minimal setting. Detailed explanation and possible extensions may be found in the appendix of "data dictionary of ERD".

![](entity_relationship_diagram.svg "")

*(To avoid it to have so may versions, this diagram may not be extremely up-to-date.)*

### Physical data model

### Data software objects and resultant data structures

### Database management system files

## Database administration and monitoring

### Roles and responsibilities

### System information

#### Database management system configuration

#### Database support software

#### Security and privacy

### Performance monitoring and database efficiency

#### Operational implications

#### Database transfer requirements

#### Data formats

### Backup and recovery

## Appendices

### Assumptions and dependencies

### Data dictionary of ERD

This data dictionary shows

1. Data stores.
2. Data element name.
3. ~~Data element type.~~
4. ~~Data element length.~~
5. Data element constraints *(sometimes)*.
6. ~~Data element validation rules.~~
7. ~~Data element maintenance (CRUD capability).~~
8. ~~Audit and data masking requirements.~~
9. ~~Expected data volumes.~~
10. ~~Life expectancy of the data.~~
11. ~~Data information life-cycle management strategy/achiving strategy.~~
12. ~~Data element outputs.~~
13. ~~Data element aliases.~~
14. Data element description.

*(For some of the not shown attributes, currently we can directly refer to the `.sql` implementation for table creation/setup. We may later have a huge excel file to list them for non-professionals, but that will not be done in the near future.)*

This data dictionary will try to be as complete as possible, i.e., includes the columns (attributes) and tables which we are still unsure about. The aim is to write them done somewhere, so we can later improving the algorithm without forgetting them. Those columns and tables will be shown ~~strikethrough~~.

*(TODO: We shall probably trace every single attribute to the software modules who create/read(!!)/update it. Currently there are attributes we believe we need, but no written requirements of the existing modules actually needs them.)*

This data dictionary is written in a descriptive style. The aim is to put all relevant thoughts to some reasonable place. We are not trying to make it complete and/or accurate. Therefore, it is unavailable that it is long and tedious.

#### Schema `settings`

##### Table `organization`

##### Table `member`

##### Table `ssh_key`

##### Table `member_feature_toggle`

- `id`
- `member_id`
- `feature`:
    - Using "ShortName". Actual feature list are defined in the application layer.
    - If value is missing, use system default value (which may be defined by a system-wise feature toggle).
- `is_on`: Boolean.
    - In actual UI, the values include on/off/default.

##### Table `organization_member_map`

- `id`: Primary key.
- [DDD-0001]{SyAD-0037} `organization_id`
- [DDD-0002]{SyAD-0037} `member_id`
- [DDD-0003]{SyAD-0038} `member_organization_role`:
    - `Char(1)` with value:
        - `G`: Non-professional manager.
        - `M`: Ordinary member.
    - For each organization, a user can at most have one role.
    - We define the enum types in the persistent layer. A "shortName" is used, as (1) the ordinal of an Enum depends on the ordering of its values and can create problems, if we need to add new ones, and (2) the String representation of an Enum is often quite verbose and renaming a value will break the database mapping.
    - We are not using a lookup table in SQL, because (1) the business logic for manager and member is completely different. There is no way for controller to handle the two types in a uniform way. (2) We don't need to query for the database if just want to show possible roles.

##### Table `repository`

- `id`
- [DDD-0004]{SyAD-0036} `organization_id`
- `name`
- `display_name`
- `description`
- [DDD-0006]{SyRS-0099} `is_public`

##### Table `repository_member_map`

- `id`
- `repository_id`
- `member_id`
- [DDD-0005]{SyAD-0041,SyAD-0039} `member_repository_role`:
    - `Char(1)` with value:
        - `O`: Project organizer.
        - `E`: Document editor.
        - `R`: Document reviewer.
        - `B`: Blacklist.
        - There is no Reader role, as it is in either case that everybody can read (public repository), or member of the organization can read (private repository).
    - For each repository, an `O` can be an `E` or `R`. Beside that, user cannot share roles.
    - The reason to (1) use short name, and (2) not having a lookup table is similar to `member_organization_role`.

#### Schema `git`

No information about branch and/or commit topological relationship are saved in the database. Data are directly queried from git. May change it later for performance concerns.

##### Table `git_commit`

## Bibliography

1. Database Design Document, Centers for medicare & medicaid services, CMS eXpedited Life Cycle (XLC). [Here](https://www.cms.gov/research-statistics-data-and-systems/cms-information-technology/xlc/downloads/systemdesigndocument.docx).
