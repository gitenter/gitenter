# Stakeholder Requirements Specification

## Document identifier

**Date of issue:**

**Issuing organization:**

**Author(s):**

**Approval signatures:**

**Status/version:**

**Reviewers/pertinent managers:**

## Introduction

### Business purpose

*(Describe the reason and background for pursuing new business or changing the current business.)*

Revision control is needed for serious documentation system which includes multiple related documents and/or collaborate between multiple people. Examples includes: (1) enterprise rules/regulations and requirement/design documents, (2) enactment of legal system/laws *(Better word?)* *(It seems only Civil law fits. Common law fits better to a database/search engine?)*, (3) research/knowledge management notes and papers, ...

However, while there are various different tools for source code revision control and collaboration, similar tool does not exist for documents. Alternative solutions includes:

+ Google Drive or similar services: multiple users can collaborate and edit **one single** document simultaneously.
+ Atlassian Confluence or similar services: mostly an on-line word processor of a project with multiple documents, without providing advanced tools for collaboration.
+ Wiki: can build relations between different items, but in a really less official and anarchical way.
+ Naive and simple version numbers.

We are going to make a tool which provide the flexibility and complicity of a source code revision control system, but fits better to document collaboration.

### Business scope

*(Identifying the business domain by name.)*

*(Defining the range of business activities included in the business domain concerned.)*

*(Describing the scope of the system being developed or changed.)*

Currently, we only aim on serving enterprise requirement/design documents.

### Business overview

## References

## Definitions, acronyms, and abbreviations

### Definition

+ **Revision control system:** A standalone software application that tracks and provides control over changes to source code.
+ **Revision control platform:** A web-based revision control repository and Internet hosting service.
+ **Word processor:** A computer software application, that performs the task of composing, editing, formatting, and printing of documents.

### Acronyms and abbreviations

## Stakeholders

*(List the stakeholders or the classes of stakeholders and describe how they will influence the organization and business, or will be related to the development and operation of the system.)*

We'll divide our stakeholders into three different parties.

Customer:

- R&D engineers (users).
- Companies which obey quality control procedure.
- Regulatory departments.

Technical:

- Developers.
- Operational engineers.
- Infrastructure provider.

Business:

- Owners. *(profitability/market share/succession planning)*
- Distributor/partner.
- Shareholders/investors. *(return of investment/income)*
- Employees. *(Job satisfaction)*

These parties are equally important for the success of the product. But in this series of documents, we'll concentrate mostly for the customer and technical concerns.

## User requirements

### User needs

- [StRS-0001]{} The software shall provide tools to help establishing various official documents used in quality control procedure for R&D activities of a future product.
- [StRS-0003]{StRS-0001} The software shall support waterfall life cycle model.
- [StRS-0005]{StRS-0001} The software shall support Agile life cycle model.
- [StRS-0013]{StRS-0001} The software may be used for software development.
- [StRS-0014]{StRS-0001} The software may be used for development of quality sensitive products.
- [StRS-0015]{StRS-0014} The software may be used for medical device development.
- [StRS-0016]{StRS-0014} The software may be used in energy related industries.
- [StRS-0017]{StRS-0014} The software may be used in transportation related industries.
- [StRS-0010]{StRS-0001} The software shall be provide procedures which follow common standards of various regulatory departments.
- [StRS-0002]{StRS-0001} The software shall be used for the documents of requirement engineering.
- [StRS-0008]{StRS-0001} The software shall be used for the documents of design control.
- [StRS-0020]{StRS-0001} The software shall give flexible for user to use their familiar word processor to edit the documents.
- [StRS-0004]{StRS-0001} The software shall help recording the evolution history and versions of official documents.
- [StRS-0018]{StRS-0001} The software shall assist various activities to establish the documentation system.

### Operational scenarios

- [StRS-0009]{StRS-0001} The software shall support multiple users working together for a single product.
- [StRS-0011]{StRS-0009} The software shall support users of various different roles: project manager, technical manager, business analyzer, architect, developer, tester/QA engineer, ...

### Operational environment

- [StRS-0006]{} Both local files/applications and web services are needed while using this software.

### Performance

### Operational life cycle

### User and operator characteristics

## Concepts of proposed system

### Operational concept

### Operational scenario

## Business management requirements

### Business environment

*(Market trends)*

- [StRS-0026]{} The software shall aim to provide services that does not exist, for unfulfilled user needs.
- [StRS-0019]{StRS-0020,StRS-0026} The software shall not provide duplicated functions of an (on-line or local) word processor, such as Microsoft Office, Google Doc, Emacs, Vim, or Atom.
- [StRS-0025]{StRS-0026} With the aim of handling multiple files in a documentation system, the software shall not provide tools for multiple users to edit the same file simultaneously, such as Google Drive.
- [StRS-0012]{StRS-0004,StRS-0026} The software shall not provide duplicated functions of a code revision control platform, such as GitHub or BitBucket.

*(Laws and regulations)*

- [StRS-0027]{} The development of the software itself shall strictly follow quality procedure, hence user can refer to the use of it to regulatory departments.
- [StRS-0029]{StRS-0027} The software itself shall have complete requirement/design documents.
- [StRS-0028]{} The development team of this software may consider getting CMMI/SWEBOK certification(s), hence to prove to the user the reliability/quality and increase the compatibility of this software.

*(Social responsibilities)*

*(Technology)*

### Goal and objective

- [StRS-0024]{} The software shall become the leading (first?) revision control based service for serious documentation system.

### Business model

- [StRS-0033]{StRS-0006} The software may be sold as a web service, charged per user per period of time, with the data saved centralized for all users in the cloud.
- [StRS-0034]{StRS-0006} The software may be sold as a product with maintenance services, for the infrastructure setup locally in the user's place.
- [StRS-0021]{} The software shall become an independent service for quality control documentation system in the short term.
- [StRS-0030]{} The development of this software shall be kept in a small R&D team in the beginning to keep largest flexibility.
- [StRS-0031]{} The software shall first includes primary functions, and provide them to the beta user as soon as possible.
- [StRS-0032]{} The software R&D team may extend when (1) secondary functions, and (2) serious tests are included in the development activities.
- [StRS-0022]{} The software may later modify its functionalities to other fields, hence extend itself to other markets such as (1) enterprise rules/regulations documents, (2) enactment of legal system/laws, (3) research/knowledge management notes and papers.
- [StRS-0023]{} For the software R&D related documents, the software may later be integrated into a code revision control platform.

### Information environment

#### Project portfolio

*(When multiple system projects are running or planned to pursue the same business goal, the priority, relative positioning, and possible constraints come from the portfolio management strategy.)*

#### Long term system plan

*(When common system infrastructure or architecture has been decided or planned, it should be described as constraints on possible design decisions.)*

#### Database configuration

*(An organization level database configuration plan and possible constraints on availability and accessibility of organization global data.)*

## Business operational requirements

### Business processes

### Business operational policies and rules

### Business operational constrains

### Business operational modes

### Business operational quality

### Business structure

## Project Constrains

## Bibliography

1. ISO/IEC/IEEE 29148:2011, System and software engineering -- Life cycle processes -- Requirement engineering.
2. ISO/IEC/IEEE 15288:2008, System and software engineering -- System life cycle process.
3. A guide to the Project Management Body of Knowledge (PMBOK Guide).
