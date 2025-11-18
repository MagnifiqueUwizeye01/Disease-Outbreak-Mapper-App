

# Software Requirements Specification
## Geospatial Disease Outbreak Mapper
  
**Team:** Group BC

## 1. Introduction

### 1.1 Project Overview
The Geospatial Disease Outbreak Mapper is a mobile and web application that enables real-time tracking and mapping of disease outbreaks. Community Health Workers (CHWs) can instantly report suspected cases while the system automatically captures GPS location data. This information is aggregated into real-time outbreak maps to help health officials respond faster to diseases like cholera, measles, and tuberculosis.

### 1.2 Problem Statement
Traditional disease reporting systems suffer from:
- Slow paper-based reporting processes
- Inaccurate or missing location data
- Delayed identification of outbreak clusters
- Late response by health authorities

### 1.3 Solution
Our digital system provides:
- Mobile reporting app for CHWs with auto-GPS capture
- Real-time outbreak maps and analytics dashboard
- FHIR-based data standardization for interoperability
- Automated cluster detection and risk assessment

---

## 2. Use Case Diagram

### 2.1 Actors
- **Community Health Worker (CHW):** Reports disease cases from the field
- **Public Health Official:** Monitors outbreaks and views analytics
- **System:** Automated processes for data handling

### 2.2 Use Cases
```
+-------------------+      +--------------------------------------+
|                   |      | Geospatial Disease Outbreak Mapper   |
|  CHW              |------|                                      |
|                   |      | ○ Log In                            |
+-------------------+      | ○ Report Disease Case               |
                           | ○ Auto-Capture GPS Location         |
                           | ○ Submit Case Report                |
+---------------------+    +--------------------------------------+
| Public Health       |        |                   |
| Official            |--------|                   |
+---------------------+        |                   |
                           | ○ View Outbreak Map   |
                           | ○ Filter Map Data     |
                           | ○ View Analytics      |
                           | ○ Generate Reports    |
                           +--------------------------------------+
                                      |
                                      |
                           +---------------------+
                           |    System           |
                           |                     |
                           | ○ Aggregate Data    |
                           | ○ Calculate Risk    |
                           | ○ Send Notifications|
                           +---------------------+
```

### 2.3 Use Case Descriptions

#### 2.3.1 Report Disease Case
- **Actor:** CHW
- **Description:** CHW submits a new disease case report through the mobile app
- **Preconditions:** CHW is logged in, GPS is available
- **Postconditions:** Case is recorded in system, location is captured

#### 2.3.2 View Outbreak Map
- **Actor:** Public Health Official
- **Description:** Official views real-time map showing disease outbreaks
- **Preconditions:** Official is logged in, has appropriate permissions
- **Postconditions:** Map is displayed with current outbreak data

---

## 3. Class Diagram

### 3.1 Core Classes

```
┌─────────────────┐    ┌───────────────────┐    ┌─────────────────┐
│     User        │    │  DiseaseReport    │    │    Location     │
├─────────────────┤    ├───────────────────┤    ├─────────────────┤
│ - userId: String│    │ - reportId: String│    │ - locationId:   │
│ - username:     │    │ - diseaseType:    │    │   String        │
│   String        │    │   String          │    │ - latitude:     │
│ - password:     │    │ - symptoms: String│    │   double        │
│   String        │    │ - reportDate: Date│    │ - longitude:    │
│ - role: String  │    │ - status: String  │    │   double        │
│ + login()       │    │ - patientAge: int │    │ - timestamp:    │
│ + logout()      │    │ + submitReport()  │    │   Date          │
└─────────────────┘    │ + validateData()  │    │ + captureGPS()  │
         △              └───────────────────┘    │ + validate()    │
         |                       △              └─────────────────┘
         |                       |                      △
         |                       |                      |
┌─────────────────┐    ┌───────────────────┐            |
│ CommunityHealth │    │   OutbreakMap     │            |
│   Worker        │    ├───────────────────┤            |
├─────────────────┤    │ - mapId: String   │            |
│ - workerId:     │    │ - cases: List     │            |
│   String        │    │   <DiseaseReport> │            |
│ - region:       │    │ - lastUpdated:    │            |
│   String        │    │   Date            │            |
│ - phoneNumber:  │    │ - region: String  │            |
│   String        │    │ + displayMap()    │            |
│ + reportCase()  │    │ + updateMap()     │            |
└─────────────────┘    │ + filterCases()   │            |
         |              └───────────────────┘            |
         |                       △                      |
         |                       |                      |
┌─────────────────┐    ┌───────────────────┐            |
│PublicHealth     │    │ RiskAssessment    │            |
│ Official        │    ├───────────────────┤            |
├─────────────────┤    │ - assessmentId:   │            |
│ - department:   │    │   String          │            |
│   String        │    │ - riskLevel:      │            |
│ - accessLevel:  │    │   String          │            |
│   String        │    │ - affectedArea:   │            |
│ + viewDashboard()│   │   String          │            |
│ + generateReport()│  │ - assessmentDate: │            |
└─────────────────┘    │   Date            │            │
                       │ + calculateRisk() │            │
                       │ + generateAlert() │            │
                       └───────────────────┘            │
                                  △                    │
                                  |                    │
                       ┌───────────────────┐          │
                       │    Observation    │          │
                       ├───────────────────┤          │
                       │ - observationId:  │          │
                       │   String          │----------┘
                       │ - diseaseCode:    │
                       │   String          │
                       │ - patientInfo:    │
                       │   String          │
                       │ - clinicalNotes:  │
                       │   String          │
                       │ + toFHIR()        │
                       └───────────────────┘
```

### 3.2 Class Relationships
- **CommunityHealthWorker** creates **1..*** **DiseaseReport**
- **DiseaseReport** has **1** **Location**
- **DiseaseReport** generates **1** **Observation** (FHIR)
- **DiseaseReport** generates **1** **Encounter** (FHIR)
- Multiple **DiseaseReports** create **1** **OutbreakMap**
- **OutbreakMap** generates **1** **RiskAssessment**

---

## 4. Data Flow Diagram

### 4.1 Context Diagram (Level 0)

```
                   +---------------------+
                   |    GPS System       |
                   +---------------------+
                            |
                            | GPS Coordinates
                            v
+---------------------+     Disease Report     +-----------------------+
| Community Health    |----------------------->| Disease Mapper        |
| Worker              |                        | System                |
+---------------------+                        |                       |
                            Map Data &         |                       |
+---------------------+     Analytics          |                       |
| Public Health       |<-----------------------|                       |
| Official            |                        |                       |
+---------------------+                        |                       |
                            Structured Data    |                       |
+---------------------+                        |                       |
| Central Database    |<-----------------------|                       |
+---------------------+                        +-----------------------+
```

### 4.2 Level 1 DFD

```
+---------------------+     +---------------------+     +---------------------+
| Community Health    |     |   Report Processing |     |    Data Storage     |
| Worker              |---->|                     |---->|                     |
+---------------------+     | - Validate Input    |     | - Store FHIR        |
           ʌ                | - Capture GPS       |     |   Resources         |
           |                | - Create FHIR       |     | - Update Database   |
           |                |   Resources         |     +---------------------+
+---------------------+     +---------------------+               |
| GPS Data            |                |                          |
+---------------------+                |                          |
                                       v                          v
+---------------------+     +---------------------+     +---------------------+
| Public Health       |     |   Map & Analytics   |     |   Risk Analysis     |
| Official            |<----|   Generation        |<----|                     |
+---------------------+     |                     |     | - Calculate Risk    |
                            | - Generate Maps     |     | - Detect Clusters   |
                            | - Create Analytics  |     | - Send Alerts       |
                            +---------------------+     +---------------------+
```

### 4.3 Data Stores
- **Cases Database:** Stores all disease reports and locations
- **User Database:** Stores CHW and official information
- **Analytics Cache:** Stores pre-calculated map and risk data

---

## 5. UI Prototypes

### 5.1 CHW Mobile App Screens

#### Screen 1: Login
```
+-----------------------+
|                       |
|    🏥 HEALTH TRACK    |
|                       |
| [Username] _________  |
| [Password] _________  |
|                       |
|    [LOGIN BUTTON]     |
|                       |
| Forgot Password?      |
+-----------------------+

```

#### Screen 2: Disease Reporting
```
+-----------------------+
| ← Back     New Report |
|-----------------------|
| Disease: [▼ Cholera]  |
|                       |
| Symptoms:             |
| [__________________]  |
|                       |
| Patient Age: [____]   |
|                       |
| Location:             |
| 📍 Capturing GPS...   |
| [Refresh Location]    |
|                       |
|      [SUBMIT]         |
+-----------------------+
```

#### Screen 3: Submission Confirmation
```
+-----------------------+
|                       |
|       ✓ SUCCESS!      |
|                       |
| Report #CH-2024-001   |
| submitted successfully|
|                       |
| Location: Captured    |
| Time: 14:30 25/11/2024|
|                       |
|   [NEW REPORT]        |
|   [VIEW HISTORY]      |
+-----------------------+
```

### 5.2 Health Official Dashboard

#### Screen 4: Main Dashboard
```
+-----------------------+
| Outbreak Dashboard    | 🔔 👤
|-----------------------|
| [All Diseases▼] [7d▼] |
|                       |
|    🗺️ MAP VIEW        |
|   • • ○ •             |
|   • ○ ○ • •           |
|     ○ • • ○           |
|                       |
| Cases Today: 24 ▲15%  |
| Active Zones: 3       |
| High Risk: Zone A     |
|                       |
| [Alerts][Reports][Map]|
+-----------------------+
```

#### Screen 5: Detailed Analytics
```
+-----------------------+
| Analytics ← Dashboard |
|-----------------------|
| Cholera Outbreak      |
|                       |
| 📈 Cases Over Time    |
| _____/¯¯¯¯¯¯¯¯¯       |
|      Nov 2024         |
|                       |
| 🔴 High Risk: Zone A  |
| 🟡 Medium: Zone B     |
| 🟢 Low Risk: Zone C   |
|                       |
| [Export Report]       |
+-----------------------+
```

#### Screen 6: Case Details
```
+-----------------------+
| Case Details ← Map    |
|-----------------------|
| Report: CH-2024-001   |
| Disease: Cholera      |
| Location: Zone A      |
| Date: 25/11/2024 14:30|
| CHW: Esther K.        |
|                       |
| Symptoms:             |
| - Diarrhea            |
| - Dehydration         |
| - Abdominal pain      |
|                       |
| [View on Map] [Edit]  |
+-----------------------+
```

---

## 6. Non-Functional Requirements

### 6.1 Performance
- Map should load within 3 seconds
- Case reports should submit within 5 seconds
- System should support 1000+ concurrent users

### 6.2 Security
- All data encrypted in transit (HTTPS/TLS)
- Role-based access control
- GPS data anonymization options
- Audit logs for all actions

### 6.3 Reliability
- 99.5% uptime requirement
- Offline capability for mobile app
- Automatic data sync when online

---

## 7. Future Enhancements

### 7.1 Planned Features
- Offline mode support
- Predictive outbreak alerts
- Multi-language support
- Integration with hospital EMR systems
- Mobile payment for CHW incentives

### 7.2 Technical Roadmap
- Phase 1: Core reporting and mapping (Current)
- Phase 2: Advanced analytics and predictions
- Phase 3: Multi-region scalability
- Phase 4: AI-powered outbreak detection
```

