# Blood Donation & Transfusion Tracker 🩸
**Project Repository:** `final-project-group-bc`

---

## 1. Concept Note

### Overview
Blood donation is a critical part of healthcare systems. Hospitals, blood banks, and community drives need an organized way to track donors, donations, transfusions, and blood stock levels. Our project, **Blood Donation & Transfusion Tracker**, provides a lightweight, FHIR-compliant system to manage these processes efficiently while ensuring interoperability between systems.

---

### Problem Statement
Currently, many hospitals and blood banks in Rwanda rely on fragmented spreadsheets, manual logs, or isolated software systems. This leads to:  
- Difficulty tracking donor information and donation events  
- Risk of running out of specific blood types  
- Challenges in sharing information with other hospitals or health authorities

---

### Proposed Solution
Our system will allow hospitals and blood banks to:  
- Register donors and maintain accurate donor profiles  
- Log donation events and monitor blood stock levels by type and location  
- Record transfusions and link them to patient records  
- Share critical data with other institutions using **FHIR APIs**, ensuring standardization and interoperability  

By following **FHIR standards**, our system can integrate with existing Electronic Medical Records (EMRs) or national health systems without requiring major changes to their databases.

---

### How FHIR Fits In
**FHIR (Fast Healthcare Interoperability Resources)** is a standard for exchanging healthcare information in a structured, universal way. Our project will use FHIR to:  
- Represent donors and patients with the `Patient` resource  
- Record donations using `Observation` or `Procedure` resources  
- Track blood banks and hospitals with the `Organization` resource  
- Enable GET and POST endpoints to share and receive data securely between systems  



---

### Core Features
1. **Donor Management** – register and update donor details  
2. **Donation Tracking** – log donation events, blood type, and volume  
3. **Blood Stock Overview** – track availability by type and location  
4. **Transfusion Records** – link blood units to recipients  
5. **FHIR-compliant API Endpoints** – simulate real healthcare interoperability  

---

### Technologies
- Backend: REST API with FHIR-compliant endpoints (JSON)  
- Frontend: Simple web interface (HTML/CSS/JS or framework of choice)  
- Database: Relational database (MySQL, PostgreSQL, or SQLite)  
- GitLab for version control and branch management  

---

### Team Members & Branch Policy
| Team | NAMES | Branch/Student ID |
|------|------------|--------|
| Team Leader | Koumba Esther | 25714 |
| Member 1 | Uwizeye Magnifique | 26676 |
| Member 2 | Igizeneza Serge Benit | 27311 |
| Member 3 | Numubyeyi Irumva Raissa|26325|
| Member 4 | Tsamba Huberthe Marthina | 25156|
| Member 5 | Mugisha Ben|25561|
| Member 6 | Bimenyimana Prince|23036|
| Member 7 |Akendengue Oguizi Vann Alex|26025|
| Member 8 |Ishimwe Didace|26249|

**Branch rules:**  
- Each member works on their own branch (named by student ID)  
- Merge Requests (MRs) are created to add changes to `main`  
- Keeps `main` branch clean and organized  

---

### Next Steps
1. Clone this repository:  
```bash
git clone https://gitlab.com/uwizeyemagnifique/final-project-group-bc.git
cd final-project-group-bc
