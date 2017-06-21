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

- [StRS-0019]{StRS-0020} The software shall not provide duplicated functions of word processor.
- [StRS-0012]{StRS-0004} The software shall not provide duplicated functions of code revision control platforms.

### Goal and objective

### Business model

### Information environment

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
