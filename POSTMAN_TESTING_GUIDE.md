# FHIR API Testing Guide for Postman

## 📋 Table of Contents
1. [Setup](#setup)
2. [Required Headers](#required-headers)
3. [POST Operations](#post-operations)
4. [GET Operations](#get-operations)
5. [Testing Workflow](#testing-workflow)

---

## 🔧 Setup

### Base URL
```
https://hapi.fhir.org/baseR4
```

### Postman Collection Setup
1. Open Postman
2. Create a new Collection: "FHIR CHW Testing"
3. Set Collection Variables:
   - `base_url`: `https://hapi.fhir.org/baseR4`
   - `patient_id`: (will be set after creating patient)
   - `location_id`: (will be set after creating location)
   - `encounter_id`: (will be set after creating encounter)
   - `observation_id`: (will be set after creating observation)
   - `riskassessment_id`: (will be set after creating risk assessment)

---

## 📤 Required Headers

For ALL requests, add these headers:

| Header Name | Header Value |
|------------|--------------|
| `Content-Type` | `application/fhir+json` |
| `Accept` | `application/fhir+json` |

**How to add in Postman:**
1. Go to the "Headers" tab
2. Add both headers manually, OR
3. Use the Pre-request Script (see below)

---

## ✅ POST Operations

### 1. POST Patient (Step 1)

**Endpoint:** `POST {{base_url}}/Patient`

**Request Body:**
```json
{
  "resourceType": "Patient",
  "name": [
    {
      "text": "John Doe",
      "family": "Doe"
    }
  ],
  "gender": "male",
  "birthDate": "1990-05-15"
}
```

**Expected Response:**
- Status: `201 Created`
- Response will include the created Patient with an `id` field
- **IMPORTANT:** Copy the `id` from response and save it as `patient_id` variable

**Example Response:**
```json
{
  "resourceType": "Patient",
  "id": "123456",
  "name": [
    {
      "text": "John Doe",
      "family": "Doe"
    }
  ],
  "gender": "male",
  "birthDate": "1990-05-15"
}
```

---

### 2. POST Location (Step 2)

**Endpoint:** `POST {{base_url}}/Location`

**Request Body:**
```json
{
  "resourceType": "Location",
  "name": "GPS Location",
  "description": "Disease case location",
  "position": {
    "latitude": 37.421998,
    "longitude": -122.084000
  },
  "address": {
    "text": "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA"
  }
}
```

**Expected Response:**
- Status: `201 Created`
- Copy the `id` from response and save as `location_id`

---

### 3. POST Encounter (Step 3)

**Endpoint:** `POST {{base_url}}/Encounter`

**Request Body:**
```json
{
  "resourceType": "Encounter",
  "status": "finished",
  "class": {
    "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
    "code": "AMB",
    "display": "ambulatory"
  },
  "type": [
    {
      "coding": [
        {
          "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
          "code": "home",
          "display": "Home Visit"
        }
      ]
    }
  ],
  "subject": {
    "reference": "Patient/{{patient_id}}"
  },
  "location": [
    {
      "location": {
        "reference": "Location/{{location_id}}"
      }
    }
  ],
  "period": {
    "start": "2024-01-15T10:30:00.000Z",
    "end": "2024-01-15T10:30:00.000Z"
  }
}
```

**Expected Response:**
- Status: `201 Created`
- Copy the `id` from response and save as `encounter_id`

---

### 4. POST Observation (Step 4)

**Endpoint:** `POST {{base_url}}/Observation`

**Request Body:**
```json
{
  "resourceType": "Observation",
  "status": "final",
  "code": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "malaria",
        "display": "Malaria"
      }
    ],
    "text": "Malaria"
  },
  "subject": {
    "reference": "Patient/{{patient_id}}"
  },
  "encounter": {
    "reference": "Encounter/{{encounter_id}}"
  },
  "effectiveDateTime": "2024-01-15T10:30:00.000Z",
  "valueCodeableConcept": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "moderate",
        "display": "Moderate"
      }
    ],
    "text": "moderate"
  },
  "valueString": "Patient presents with fever and chills",
  "component": [
    {
      "code": {
        "coding": [
          {
            "system": "http://snomed.info/sct",
            "code": "fever",
            "display": "Fever"
          }
        ],
        "text": "Fever"
      },
      "valueCodeableConcept": {
        "text": "present"
      }
    },
    {
      "code": {
        "coding": [
          {
            "system": "http://snomed.info/sct",
            "code": "cough",
            "display": "Cough"
          }
        ],
        "text": "Cough"
      },
      "valueCodeableConcept": {
        "text": "present"
      }
    }
  ]
}
```

**Expected Response:**
- Status: `201 Created`
- Copy the `id` from response and save as `observation_id`

---

### 5. POST RiskAssessment (Step 5)

**Endpoint:** `POST {{base_url}}/RiskAssessment`

**Request Body:**
```json
{
  "resourceType": "RiskAssessment",
  "status": "final",
  "subject": {
    "reference": "Patient/{{patient_id}}"
  },
  "encounter": {
    "reference": "Encounter/{{encounter_id}}"
  },
  "prediction": [
    {
      "outcome": {
        "coding": [
          {
            "system": "http://snomed.info/sct",
            "code": "moderate",
            "display": "Moderate"
          }
        ],
        "text": "Moderate Risk"
      },
      "probabilityDecimal": 0.5,
      "rationale": "Risk level: moderate. Symptoms: Fever, Cough. Notes: Patient requires medical attention."
    }
  ]
}
```

**Expected Response:**
- Status: `201 Created`
- Copy the `id` from response and save as `riskassessment_id`

---

## 📥 GET Operations

### 1. GET Patient by ID

**Endpoint:** `GET {{base_url}}/Patient/{{patient_id}}`

**Headers:** Same as above (Content-Type and Accept)

**Expected Response:**
- Status: `200 OK`
- Returns the Patient resource with all details

---

### 2. GET Location by ID

**Endpoint:** `GET {{base_url}}/Location/{{location_id}}`

**Expected Response:**
- Status: `200 OK`
- Returns the Location resource

---

### 3. GET Encounter by ID

**Endpoint:** `GET {{base_url}}/Encounter/{{encounter_id}}`

**Expected Response:**
- Status: `200 OK`
- Returns the Encounter resource

---

### 4. GET Observation by ID

**Endpoint:** `GET {{base_url}}/Observation/{{observation_id}}`

**Expected Response:**
- Status: `200 OK`
- Returns the Observation resource

---

### 5. GET RiskAssessment by ID

**Endpoint:** `GET {{base_url}}/RiskAssessment/{{riskassessment_id}}`

**Expected Response:**
- Status: `200 OK`
- Returns the RiskAssessment resource

---

### 6. SEARCH Observations (GET with Query Parameters)

**Endpoint:** `GET {{base_url}}/Observation`

**Query Parameters:**
- `_count`: `10` (limit results)
- `code`: `malaria` (optional - filter by disease code)
- `subject`: `Patient/{{patient_id}}` (optional - filter by patient)

**Example:**
```
GET {{base_url}}/Observation?_count=10
```

**Expected Response:**
- Status: `200 OK`
- Returns a Bundle containing multiple Observation resources

**Example Response:**
```json
{
  "resourceType": "Bundle",
  "type": "searchset",
  "total": 5,
  "entry": [
    {
      "resource": {
        "resourceType": "Observation",
        "id": "obs-123",
        ...
      }
    },
    ...
  ]
}
```

---

### 7. SEARCH RiskAssessments

**Endpoint:** `GET {{base_url}}/RiskAssessment`

**Query Parameters:**
- `_count`: `10`
- `subject`: `Patient/{{patient_id}}` (optional)
- `encounter`: `Encounter/{{encounter_id}}` (optional)

**Example:**
```
GET {{base_url}}/RiskAssessment?_count=10&subject=Patient/{{patient_id}}
```

---

## 🔄 Complete Testing Workflow

### Step-by-Step Demonstration:

1. **POST Patient** → Get `patient_id` → Save to variable
2. **POST Location** → Get `location_id` → Save to variable
3. **POST Encounter** (using `patient_id` and `location_id`) → Get `encounter_id` → Save to variable
4. **POST Observation** (using `patient_id` and `encounter_id`) → Get `observation_id` → Save to variable
5. **POST RiskAssessment** (using `patient_id` and `encounter_id`) → Get `riskassessment_id` → Save to variable

6. **GET Patient** (using saved `patient_id`) → Verify data
7. **GET Location** (using saved `location_id`) → Verify data
8. **GET Encounter** (using saved `encounter_id`) → Verify data
9. **GET Observation** (using saved `observation_id`) → Verify data
10. **GET RiskAssessment** (using saved `riskassessment_id`) → Verify data

11. **SEARCH Observations** → Verify you can retrieve multiple observations
12. **SEARCH RiskAssessments** → Verify you can retrieve multiple risk assessments

---

## 🎯 Postman Tips for Demonstration

### 1. Use Environment Variables
Create an environment in Postman with:
- `base_url`: `https://hapi.fhir.org/baseR4`
- `patient_id`: (auto-populated after POST)
- `location_id`: (auto-populated after POST)
- etc.

### 2. Use Tests Tab to Auto-Save IDs
Add this to the "Tests" tab of each POST request:

**For Patient POST:**
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("patient_id", jsonData.id);
    console.log("Patient ID saved: " + jsonData.id);
}
```

**For Location POST:**
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("location_id", jsonData.id);
    console.log("Location ID saved: " + jsonData.id);
}
```

(Repeat for Encounter, Observation, RiskAssessment)

### 3. Pre-request Script (Optional)
Add to Collection or Request Pre-request Script to auto-set headers:
```javascript
pm.request.headers.add({
    key: 'Content-Type',
    value: 'application/fhir+json'
});
pm.request.headers.add({
    key: 'Accept',
    value: 'application/fhir+json'
});
```

---

## ✅ Success Criteria

For each POST:
- ✅ Status Code: `201 Created`
- ✅ Response contains `id` field
- ✅ Response contains `resourceType` matching what you sent

For each GET:
- ✅ Status Code: `200 OK`
- ✅ Response contains the resource you requested
- ✅ Data matches what you posted

For SEARCH:
- ✅ Status Code: `200 OK`
- ✅ Response is a Bundle with `type: "searchset"`
- ✅ Bundle contains `entry` array with resources

---

## 🐛 Common Issues

1. **400 Bad Request**: Check JSON syntax, required fields
2. **404 Not Found**: Check if ID exists, verify base URL
3. **500 Server Error**: Server issue, try again later
4. **Missing Headers**: Ensure `Content-Type` and `Accept` are set

---

## 📝 Notes for Lecturer

- All requests follow FHIR R4 specification
- Headers use `application/fhir+json` as per FHIR standard
- Resources are linked via references (e.g., `Patient/123`)
- The workflow demonstrates the complete CHW disease reporting process
- All resources are stored on HAPI FHIR public test server

