# Geospatial Disease Outbreak Mapper
**Group BC Final Project**

---

## 🌍 Empowering Community Health
In the fight against infectious diseases, **speed is everything**. Public health emergencies often spread faster than they can be reported. Delayed data leads to delayed responses, putting entire communities at risk.

Our project, the **Geospatial Disease Outbreak Mapper (CHW Disease Reporter)**, bridges this gap. It empowers Community Health Workers (CHWs) with a robust tool to instantly report suspected cases from the field. By automatically capturing GPS locations and standardizing data, we provide health authorities with real-time visibility into outbreaks, enabling them to save lives faster.

---

## 🚀 Key Features

### 1. **Secure & Controlled Access**
- **Restricted Registration**: To ensure data integrity, only authorized CHWs can create accounts.
- **Auto-Login**: Robust session management keeps users logged in for quick access.

### 2. **Real-Time Dashboard**
- **Instant Insights**: View pending reports, risk area summaries, and case trends at a glance.
- **Visual Analytics**: Dynamic charts and risk indicators help categorize low, medium, and high-risk zones.

### 3. **Smart Disease Reporting (Online & Offline)**
- **Works Everywhere**: Whether you have internet or not, you can do your job.
    - **Online Mode**: Reports are sent instantly to the central server.
    - **Offline Mode**: If the network is down, reports are safely stored locally on your device.
    - **Auto-Sync**: The app helps you track and sync pending reports when connectivity returns.
- **GPS Auto-Capture**: Automatically records the precise location of every case for accurate mapping.

### 4. **Submission History & Management**
- **Full Control**: View a comprehensive history of all your submitted cases.
- **Edit & Delete**: specialized tools to manage your **local (unsynced)** reports before they are sent to the server. Correct mistakes easily without administrative hassle.

### 5. **Geospatial Outbreak Map**
- **Visual Tracking**: See cases plotted on an interactive map.
- **Risk Coding**: Color-coded markers (Red/High, Orange/Medium, Yellow/Low) indicate severity instantly.

---

## 📝 Important: Registration Instructions

To ensure system security and proper role assignment, **you must use a valid, pre-configured CHW Code** when creating a new account.

> [!IMPORTANT]  
> **The CHW CODE must be exactly one of the following:**
> - `CHW001`
> - `CHW002`
> - `CHW003`
> - `CHW004`
> - `CHW005`

**If you do not enter one of these specific codes during sign-up, your account creation will fail.**

---

## 🛠 Usage Flow

1. **Login / Register**: Use your credentials or sign up with a valid `CHW Code`.
2. **Dashboard**: Check your "Pending Reports" count and latest risk stats.
3. **Report Case**: Fill out the patient details.
    - Select **Severity**:
        - **Mild** (Low Risk)
        - **Moderate** (Medium Risk)
        - **Severe** (High Risk)
    - Submit! (The app handles the connection logic for you).
4. **Submission History**: Review your reports. Tap any "Pending" report to view details, or use the Edit/Delete icons to manage them.
5. **Sync**: If you have pending reports, separate "Sync" indicators will guide you to upload them when online.

---

## 💻 Tech Stack
- **Platform**: Android (Java)
- **Database**: Room Persistence Library (Local Offline Storage)
- **Network**: Retrofit (API Communication)
- **Standard**: FHIR (Fast Healthcare Interoperability Resources) for data compatibility
- **Location**: Android Location Services (GPS)

---

## 👥 Team Members (Group BC)

| Name | Student ID |
|------|------------|
| Koumba Esther | 25714 |
| Uwizeye Magnifique | 26676 |
| Igizeneza Serge Benit | 27311 |
| Numubyeyi Irumva Raissa | 26325 |
| Tsamba Huberthe Marthina | 25156 |
| Mugisha Ben | 25561 |
| Bimenyimana Prince | 23036 |
| Akendengue Oguizi Vann Alex | 26025 |
| Ishimwe Didace | 26249 |

---

**© 2024 Geospatial Disease Outbreak Mapper**  
*Saving lives through data.*
