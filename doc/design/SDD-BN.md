# Software Design Description -- Back End

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

## Definitions, acronyms, and abbreviations

### Definition

### Acronyms and abbreviations

## Design Issues

#### Cross-Origin Resource Sharing (CORS)

Should design whether the API is open to everybody, or just some particular sites.

Another problem is should `capsid` and `peplomer` to be under the same IP/port, or completely two sites. For the first case, CORS is not at all a concern at all (if other parties are not using this API).

The technical part of how to set it up, can be referred to [here](https://spring.io/guides/gs/rest-service-cors/).

## Appendices

### Assumptions and dependencies

## Bibliography
