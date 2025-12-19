-- Supabase Database Schema for Geospatial Disease Outbreak Mapper
-- Run this script in your Supabase SQL Editor to create the tables

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- CHW Table
CREATE TABLE IF NOT EXISTS chw (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100) DEFAULT 'Community Health Worker',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Table
CREATE TABLE IF NOT EXISTS patient (
    patient_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Encounter Table (must be created before tables that reference it)
CREATE TABLE IF NOT EXISTS encounter (
    encounter_id VARCHAR(255) PRIMARY KEY,
    encounter_date TIMESTAMP NOT NULL,
    encounter_type VARCHAR(50) NOT NULL,
    chw_id VARCHAR(255),
    patient_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chw_id) REFERENCES chw(id) ON DELETE SET NULL,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE CASCADE
);

-- GPS Location Table (references encounter, so must be created after encounter)
CREATE TABLE IF NOT EXISTS gps_location (
    location_id VARCHAR(255) PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address TEXT,
    encounter_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id) ON DELETE CASCADE
);

-- Observation Table
CREATE TABLE IF NOT EXISTS observation (
    observation_id VARCHAR(255) PRIMARY KEY,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    encounter_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id) ON DELETE CASCADE
);

-- Disease Report Table
CREATE TABLE IF NOT EXISTS disease_report (
    report_id VARCHAR(255) PRIMARY KEY,
    disease_type VARCHAR(100) NOT NULL,
    report_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    encounter_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id) ON DELETE CASCADE
);

-- Risk Assessment Table
CREATE TABLE IF NOT EXISTS risk_assessment (
    risk_id VARCHAR(255) PRIMARY KEY,
    level VARCHAR(50) NOT NULL,
    description TEXT,
    disease_report_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (disease_report_id) REFERENCES disease_report(report_id) ON DELETE CASCADE
);

-- Measure Report Table
CREATE TABLE IF NOT EXISTS measure_report (
    measure_id VARCHAR(255) PRIMARY KEY,
    report_type VARCHAR(50),
    period_start DATE,
    period_end DATE,
    case_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Junction table for Disease Report and Measure Report (Many-to-Many)
CREATE TABLE IF NOT EXISTS disease_report_measure_report (
    disease_report_id VARCHAR(255),
    measure_report_id VARCHAR(255),
    PRIMARY KEY (disease_report_id, measure_report_id),
    FOREIGN KEY (disease_report_id) REFERENCES disease_report(report_id) ON DELETE CASCADE,
    FOREIGN KEY (measure_report_id) REFERENCES measure_report(measure_id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_encounter_chw ON encounter(chw_id);
CREATE INDEX IF NOT EXISTS idx_encounter_patient ON encounter(patient_id);
CREATE INDEX IF NOT EXISTS idx_encounter_date ON encounter(encounter_date);
CREATE INDEX IF NOT EXISTS idx_location_encounter ON gps_location(encounter_id);
CREATE INDEX IF NOT EXISTS idx_observation_encounter ON observation(encounter_id);
CREATE INDEX IF NOT EXISTS idx_disease_report_encounter ON disease_report(encounter_id);
CREATE INDEX IF NOT EXISTS idx_disease_report_date ON disease_report(report_date);
CREATE INDEX IF NOT EXISTS idx_risk_assessment_report ON risk_assessment(disease_report_id);

-- Enable Row Level Security (RLS) - adjust policies as needed
ALTER TABLE chw ENABLE ROW LEVEL SECURITY;
ALTER TABLE patient ENABLE ROW LEVEL SECURITY;
ALTER TABLE encounter ENABLE ROW LEVEL SECURITY;
ALTER TABLE gps_location ENABLE ROW LEVEL SECURITY;
ALTER TABLE observation ENABLE ROW LEVEL SECURITY;
ALTER TABLE disease_report ENABLE ROW LEVEL SECURITY;
ALTER TABLE risk_assessment ENABLE ROW LEVEL SECURITY;
ALTER TABLE measure_report ENABLE ROW LEVEL SECURITY;

-- Create policies for public access (adjust based on your security requirements)
-- For development, you might want to allow all operations
-- For production, create more restrictive policies

CREATE POLICY "Allow all operations on chw" ON chw FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on patient" ON patient FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on encounter" ON encounter FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on gps_location" ON gps_location FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on observation" ON observation FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on disease_report" ON disease_report FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on risk_assessment" ON risk_assessment FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all operations on measure_report" ON measure_report FOR ALL USING (true) WITH CHECK (true);

