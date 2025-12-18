# CHW Disease Reporting Mobile App

## Overview
Mobile application for Community Health Workers (CHWs) in Rwanda to report disease cases with automatic GPS capture for the Geospatial Disease Outbreak Mapper system.

## Features Implemented

### ✅ Profile Management (ProfileActivity)
- CHW profile viewing and editing
- Personal information management
- Session management with PreferenceManager
- Profile data validation

### ✅ Reports History (HistoryActivity)
- Disease reports listing with filtering
- Status-based filtering (All, Pending, Submitted, Draft)
- Report management (edit/delete based on status)
- Sample data with Rwanda GPS coordinates

### ✅ Database Entities
- CHW entity for health worker profiles
- DiseaseReport entity for disease case reports
- Support for FHIR standard data structure

### ✅ Utility Classes
- PreferenceManager for session and settings storage
- Constants with Rwanda-specific configuration
- Disease types and location data

## Technical Implementation

### Architecture
- **Language:** Java
- **Database:** Room (prepared for FHIR compliance)
- **UI:** Material Design components
- **Location:** GPS integration for outbreak mapping
- **Offline:** Local storage with sync capability

### Disease Types Supported
- Cholera
- Malaria
- Tuberculosis
- Measles
- Dysentery
- Typhoid
- COVID-19
- Other communicable diseases

### Rwanda-Specific Features
- GPS coordinates for Kigali and districts
- Rwanda health system integration ready
- Local language support preparation
- FHIR standard compliance

## Usage

### Navigation
1. **Main Screen:** Entry point with CHW portal access
2. **Profile Screen:** Manage CHW personal information
3. **History Screen:** View and manage disease reports

### Sample Data
- Demo CHW profile: Louis Uwizeyimana (CHW001)
- Sample disease reports with various statuses
- Kigali GPS coordinates for testing

## Future Enhancements
- XML layout implementation for better UI
- Room database full integration
- FHIR API backend connectivity
- Offline sync capabilities
- Real-time outbreak mapping
- Multi-language support (Kinyarwanda)

## Development Status
- ✅ Core functionality implemented
- ✅ Navigation between screens working
- ✅ Data models and utilities complete
- 🔄 UI layouts (programmatic implementation)
- 🔄 Database persistence
- 🔄 Backend integration