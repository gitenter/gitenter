# System Architecture Description

## Document identifier

**Date of issue:**

**Issuing organization:**

**Author(s):**

**Approval signatures:**

**Status/version:**

**Reviewers/pertinent managers:**

## Introduction

### Purpose

### Scope

### Overview

## References

1. System requirements specification (SyRS).

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## Decomposition description

### Module decomposition

Comparison of markup languages:

|            | markdown   | reStructuredText |
| ---------- | ---------- | ---------- |

- [SyAD-0002]{SyRS-0010,SyRS-0034} The document shall be written using a modified `markdown` format, with extra supports on traceable items.

Comparison of version control platforms:

|            | Subversion | git        |
| ---------- | ---------- | ---------- |
| Regular backups by authors |  | Done in a separated branch locally |
| Pending changes/different opinions |  | Different branch with a pull request |
| Changes approved as a group decision |  | Merge in "Integration-Manager Workflow" |
| Separate code and document changes |  | Different branch. A configuration file to indicate whether the current commit in a document change or not. |

- [SyAD-0001]{SyRS-0001} The software shall be built on top of `git`.
- [SyAD-0003]{SyRS-0024} There shall be a centralized server.

Therefore, the system show includes several parts:

- Git server/hosting service
- Git client
- Text editor add-ons

### Process decomposition

## Dependency description

### Intermodule dependencies

### Interprocess dependencies

## Interface description

### Module interface

### Process interface

## Appendices

### Assumptions and dependencies

## Bibliography

1. ISO/IEC TR 24748-1, Systems and software engineering -- Life cycle management -- Part 1: Guide for life cycle management
2. ISO/IEC/IEEE 15288:2008, System and software engineering -- System life cycle process.
3. ISO/IEC/IEEE 42010:2011, Systems and software engineering -- Architecture description
4. IEEE Std 1016:1998, IEEE recommended practice for software design descriptions.
5. MIL-STD-498 (Military-Standard-498) DI-IPSC-81432, System/subsystem design description (SSDD).
