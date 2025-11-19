# Software Requirements Specification (SRS)

**Project:** Geospatial Disease Outbreak Mapper  
**Repository:** final-project-group-bc  
**Date:** November 19, 2025  
**Version:** 1.0  
**Prepared by:** Final Project Group BC  
**Status:** Draft  

---

## 1. Document Control

**Document Information**  
- **Title:** Software Requirements Specification for Geospatial Disease Outbreak Mapper  
- **Prepared for:** Project Supervisors & Course Instructors  
- **Team Members:**  
  - 25714: Koumba Esther  
  - 26676: Uwizeye Magnifique  
  - 27311: Igizeneza Serge Benit  
  - 26325: Numubyeyi Irumva Raissa  
  - 25156: Tsamba Huberthe Marthina  
  - 25561: Mugisha Ben  
  - 23036: Bimenyimana Prince  
  - 26025: Akendengue Oguizi Vann Alex  
  - 26249: Ishimwe Didace  

**Disclaimer:**  
This document is the intellectual property of Group BC. Unauthorized modification, distribution, or plagiarism is prohibited.

---

## 2. Table of Contents

1. [Introduction](#1-introduction)  
2. [Overall Description](#2-overall-description)  
3. [System Features and Functional Requirements](#3-system-features-and-functional-requirements)  
4. [External Interface Requirements](#4-external-interface-requirements)  
5. [Non-Functional Requirements](#5-non-functional-requirements)  
6. [System Models (Analysis Diagrams)](#6-system-models-analysis-diagrams)  
7. [Appendices](#7-appendices)  

---

## 1. Introduction

### 1.1 Purpose
This SRS defines the requirements for the **Geospatial Disease Outbreak Mapper**, a system to allow Community Health Workers (CHWs) to report suspected disease cases with GPS location. The system enables real-time monitoring of outbreaks for the Ministry of Health.

### 1.2 Scope
- **CHW Mobile App:** Report diseases → auto-capture GPS → send to backend.  
- **Central Dashboard:** Interactive outbreak maps, heatmaps, trends, and risk zones.  
- **FHIR Standardization:** Convert reports into FHIR Observation, Encounter, Location, RiskAssessment resources.  
- **Risk Detection:** Identify abnormal spikes or clusters.  

**Not included:** Automated treatment recommendations, full EMR integration (future enhancement).

### 1.3 Definitions, Acronyms, and Abbreviations
| Acronym | Meaning |
|---------|---------|
| CHW | Community Health Worker |
| FHIR | Fast Healthcare Interoperability Resources |
| Observation | FHIR resource for disease report |
| Encounter | FHIR resource representing CHW-patient interaction |
| Location | GPS data resource |
| RiskAssessment | FHIR resource for outbreak risk |

### 1.4 References
- [HL7 FHIR Release 4 Specification](http://hl7.org/fhir)  
- Ministry of Health Disease Reporting Guidelines  
- GIS Mapping Best Practices for Public Health  

---

## 2. Overall Description

### 2.1 Product Perspective
Three-tier architecture:  
1. **Client Layer (Mobile App)** – CHWs report cases, works offline, auto-syncs.  
2. **Application Layer (Backend API)** – Stores reports as FHIR resources, validates data, calculates risk.  
3. **Dashboard Layer** – Real-time outbreak visualization for health officials.  

### 2.2 Product Functions
- Secure CHW login  
- Disease reporting with GPS capture  
- Real-time outbreak heatmap  
- Risk scoring and alerts  
- Reporting analytics  

### 2.3 User Characteristics
- **CHWs:** Basic digital literacy, field reporters.  
- **Health Officials:** Monitor outbreak trends, respond to alerts.  
- **Admins:** Manage users, configure system.  

### 2.4 Constraints
- Must comply with FHIR standards  
- Offline functionality mandatory  
- Data encryption and access control  

### 2.5 User Documentation
Embedded **Help** section in app and PDF guide for dashboard use.  

### 2.6 Assumptions and Dependencies
- CHWs have GPS-enabled Android devices  
- FHIR server available and reachable  
- Stable network needed for real-time updates  

---

## 3. System Features and Functional Requirements

### 3.1 User Management
**FR-01:** Admin creates CHW accounts  
**FR-02:** CHWs log in securely with role-based access  

### 3.2 Disease Reporting
**FR-03:** CHW selects disease → system auto-captures GPS  
**FR-04:** Report stored as FHIR Observation and Location  

### 3.3 Encounter Recording
**FR-05:** Record CHW-patient encounter as FHIR Encounter resource  

### 3.4 Risk Assessment
**FR-06:** System computes risk score based on reported cases  
**FR-07:** Generate RiskAssessment resource if cluster detected  

### 3.5 Dashboard
**FR-08:** Real-time heatmap of outbreaks  
**FR-09:** Trend graphs and statistics  
**FR-10:** Alerts for high-risk zones  

---

## 4. External Interface Requirements

### 4.1 User Interfaces
- Mobile App: Simple reporting form, offline mode  
- Dashboard: Map, charts, alerts  

### 4.2 Software Interfaces
- REST API with JSON (FHIR-compliant)  
- Local SQLite storage for offline mode  

### 4.3 Hardware Interfaces
- Android smartphones (≥2GB RAM, Android 7.0+)  
- Optional external storage  

---

## 5. Non-Functional Requirements

- **Performance:** Map updates < 5s, sync < 5s  
- **Security:** HTTPS, encrypted local storage, role-based access  
- **Reliability:** Auto-recover from network interruptions  
- **Usability:** Simple UI for field use  
- **Maintainability:** Follows clean architecture, modular code  

---

## 6. System Models (Analysis Diagrams)

- **6.1 Use Case Diagram:** CHW reports cases, Health Officials view heatmaps  
- **6.2 Class Diagram:** FHIR resource classes (Observation, Encounter, Location, RiskAssessment)  
- **6.3 Data Flow Diagrams:** Level 0, 1, 2  

---

## 7. Appendices

### 7.1 FHIR JSON Examples

**Patient Example**
```json
{
  "resourceType": "Patient",
  "id": "patient-01",
  "name": [{"family": "Doe","given": ["John"]}],
  "gender": "male",
  "birthDate": "2000-01-01"
}

Observation Example

{
  "resourceType": "Observation",
  "status": "final",
  "code": {"text": "Cholera"},
  "subject": {"reference": "Patient/patient-01"},
  "effectiveDateTime": "2025-11-19T10:00:00Z",
  "valueString": "Suspected case"
}


RiskAssessment Example

{
  "resourceType": "RiskAssessment",
  "subject": {"reference": "Location/location-01"},
  "prediction": [{"outcome": {"text": "High-risk cluster"}}],
  "date": "2025-11-19T10:05:00Z"
}


7.2 Risk Thresholds

| Cases in Zone | Risk Level |
| ------------- | ---------- |
| 0–5           | Low        |
| 6–15          | Medium     |
| 16+           | High       |
