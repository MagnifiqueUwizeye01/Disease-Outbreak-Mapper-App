# Blood Donation & Transfusion Tracker 🩸
**Project Repository:** `final-project-group-bc`

---

## 1. Concept Note

### Overview
Blood donation is a cornerstone of healthcare systems. Hospitals, blood banks, and community drives require a **centralized and intelligent system** to track donors, donations, transfusions, and blood stock levels.  
Our project, **Blood Donation & Transfusion Tracker**, provides a **FHIR-compliant platform** that ensures interoperability between hospitals and blood banks, while offering data-driven insights for effective decision-making.

---

### Problem Statement
In Rwanda, many hospitals and blood banks rely on **manual logs, spreadsheets, or isolated software**. This leads to:  
- Difficulty tracking donor information and donation events  
- Risk of running out of specific blood types  
- Delays or challenges in sharing data between hospitals and health authorities  
- Limited insight into future blood stock needs  

---

### Proposed Solution
Our system will enable:  
- **Donor Management** – register, update, and maintain accurate donor profiles  
- **Donation Tracking** – log donations, including blood type, volume, and date  
- **Blood Stock Monitoring** – real-time overview of stock by type and location  
- **Transfusion Management** – link blood units to patient transfusions  
- **FHIR-compliant API Integration** – share data across systems using standardized APIs  
- **Predictive Analytics (Advanced Feature)** – estimate future blood demand and highlight potential shortages using past donation trends  

By adhering to **FHIR standards**, our system can integrate seamlessly with existing EMRs or national health systems without modifying their databases. This makes our solution **scalable, interoperable, and future-ready**.

---

### How FHIR Fits In
**FHIR (Fast Healthcare Interoperability Resources)** provides a standard structure for healthcare data exchange. Our project uses FHIR to:  
- Represent donors and patients using the `Patient` resource  
- Record donation events with `Observation` or `Procedure` resources  
- Manage hospitals and blood banks via the `Organization` resource  
- Enable secure **GET and POST requests** to exchange real-time data with external systems  

This ensures **interoperability**, allowing hospitals or health authorities to access critical blood donation data safely and efficiently.

---

### Core Features
1. **Donor Management** – create, update, and view donor profiles  
2. **Donation Tracking** – log events with type, volume, and date  
3. **Blood Stock Dashboard** – visualize availability by type and location  
4. **Transfusion Records** – link blood units to patient transfusions  
5. **FHIR API Endpoints** – simulate healthcare interoperability  
6. **Predictive Analytics** – forecast blood shortages and trends  
7. **Notifications (Future Feature)** – alert eligible donors when a specific blood type is needed  

---

### Workflow Overview (Simulated Diagram)
1. **Donor registers** → data stored as `Patient` resource in FHIR  
2. **Donation recorded** → stored as `Observation`/`Procedure`  
3. **Blood stock updated** → dashboard shows available units  
4. **Hospital requests blood** → system checks availability and suggests donors  
5. **FHIR API** → external systems can GET/POST donation and patient data  

This workflow demonstrates **real-world, end-to-end interoperability** while remaining clear and implementable.

---



### Team Members & Branch Policy
| Team | Name | Branch / Student ID |
|------|------|-------------------|
| Member 1 | Koumba Esther | 25714 |
| Member 2 | Uwizeye Magnifique | 26676 |
| Member 3 | Igizeneza Serge Benit | 27311 |
| Member 4 | Numubyeyi Irumva Raissa | 26325 |
| Member 5 | Tsamba Huberthe Marthina | 25156 |
| Member 6 | Mugisha Ben | 25561 |
| Member 7 | Bimenyimana Prince | 23036 |
| Member 8 | Akendengue Oguizi Vann Alex | 26025 |
| Member 9 | Ishimwe Didace | 26249 |

**Branch rules:**  
- Each member works on their own branch (named by student ID)  
- Merge Requests (MRs) are created to merge changes to `main`  
- Keeps `main` branch clean, organized, and reviewable  

---

### Next Steps
1. Clone this repository:  
```bash
git clone https://gitlab.com/uwizeyemagnifique/final-project-group-bc.git
cd final-project-group-bc
