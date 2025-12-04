#  Geospatial Disease Outbreak Mapper  
**Project Repository:** `final-project-group-bc`

---
 
## 1. Concept Note

### Overview
Public health emergencies often spread fast — and delayed reporting makes outbreaks even more dangerous.  
Our project, **Geospatial Disease Outbreak Mapper**, enables Community Health Workers (CHWs) to instantly report suspected cases, while the system **automatically captures GPS location** and sends the data to a central server.

This data is aggregated into **real-time outbreak maps**, helping the Ministry of Health respond faster to diseases like **cholera, measles, tuberculosis**, and more.

---

### Problem Statement
Traditional disease reporting in many areas still relies on:
- Slow paper-based reporting  
- Late submission by CHWs  
- Inaccurate or missing location data  
- Difficulty identifying outbreak clusters  
- Delayed response by health authorities  

These issues cause **faster disease spread, higher mortality, and reduced visibility** into public health risks.

---

### Proposed Solution
Our system provides:
- **CHW Mobile Reporting App** – CHW selects disease → system auto-captures GPS → sends to backend  
- **Central Monitoring Dashboard** – real-time outbreak heatmaps and analytics  
- **FHIR-Based Data Standardization** – ensures interoperability with hospital systems  
- **Automated Cluster Detection** – identifies abnormal spikes or risky zones  

The goal is to improve **speed**, **accuracy**, and **coordination** in disease surveillance.

---

## 2. How FHIR Fits In
FHIR standardizes the healthcare data we store and share.  
We use the following FHIR resources:

| FHIR Resource | Purpose |
|---------------|---------|
| **Observation** | Stores the disease report (cholera, measles, dysentery, etc.) |
| **Encounter** | Represents the CHW interaction with the patient |
| **Location** | Auto-captured GPS coordinates |
| **RiskAssessment** | Identifies risk level in specific areas |
| **MeasureReport** | Aggregates outbreak summaries and analytics |

Using FHIR ensures compatibility with national healthcare systems and EMRs.

---

## 3. System Objectives
- Enable quick and accurate reporting of notifiable diseases  
- Automatically capture patient or event location using GPS  
- Provide a central dashboard for outbreak visualization  
- Ensure interoperability using **FHIR standards**  
- Improve early detection of disease clusters  
- Maintain strong privacy and data protection  
- Support national-scale real-time disease tracking  

---

## 4. System Components

### 1. CHW Reporting App
- Simple UI for disease reporting  
- Auto-captures GPS coordinates  
- Supports offline mode (future enhancement)

### 2. Backend API Server
- Receives and validates reports  
- Stores data as FHIR resources  
- Provides APIs for dashboard and analytics

### 3. Outbreak Dashboard
- Interactive map  
- Heatmaps  
- Case trends and time-based spread  
- Risk scoring (using RiskAssessment)

### 4. FHIR Data Layer
- Converts submissions into standardized FHIR resources  
- Ensures interoperability across platforms

---

## 5. Security & Privacy

Since the system handles sensitive health data, we implement:

- **HTTPS/TLS encryption** for all communication  
- **FHIR-compliant data structures** with standardized security  
- **Role-based access control**  
- **GPS data protection** (precision reduction when needed)  
- **Audit logs** for all submissions  
- **Data minimization** – only essential information is stored  

---

## 6. Core Features
1. **GPS Auto-Capture**  
2. **Fast Disease Reporting Form**  
3. **Real-Time Heatmap & Outbreak Map**  
4. **Case Trends Dashboard**  
5. **FHIR-Compatible System Design**  
6. **Risk Assessment Engine**  
7. **Offline Mode** *(future)*  
8. **Predictive Alerts** *(future)*  

---

## 7. Workflow Overview
1. **CHW observes patient**  
2. **Opens reporting app**  
3. **Selects disease**  
4. **GPS auto-captured**  
5. **System converts report to FHIR resources**  
    - `Observation`  
    - `Encounter`  
    - `Location`  
6. **Backend processes and stores report**  
7. **Dashboard updates in real time**  
8. **Officials monitor trends and respond quickly**

---

## 8. Team Members & Branch Policy

| Team Member | Name | Branch / Student ID |
|-------------|------|---------------------|
| Member 1 | Koumba Esther | 25714 |
| Member 2 | Uwizeye Magnifique | 26676 |
| Member 3 | Igizeneza Serge Benit | 27311 |
| Member 4 | Numubyeyi Irumva Raissa | 26325 |
| Member 5 | Tsamba Huberthe Marthina | 25156 |
| Member 6 | Mugisha Ben | 25561 |
| Member 7 | Bimenyimana Prince | 23036 |
| Member 8 | Akendengue Oguizi Vann Alex | 26025 |
| Member 9 | Ishimwe Didace | 26249 |

### Branch Workflow Rules
- Every member works **ONLY** in their personal branch (student ID)  
- All changes must be merged into the **dev** branch first  
- After review, **dev → main**  
- The **main** branch must always remain clean and submission-ready  

---

## 9. Getting Started

Clone the repository:
```bash
git clone https://gitlab.com/uwizeyemagnifique/final-project-group-bc.git
cd final-project-group-bc
