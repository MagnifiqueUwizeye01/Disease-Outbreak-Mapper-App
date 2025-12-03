# Supabase Database Setup Guide

This guide will help you set up your Supabase database for the Geospatial Disease Outbreak Mapper application.

## Prerequisites

1. A Supabase account (sign up at https://supabase.com)
2. Your Supabase project URL: `https://pbmtudumjersmvpnnujj.supabase.co`
3. Database connection string (already provided)

## Step 1: Create Database Tables

1. Go to your Supabase Dashboard
2. Navigate to **SQL Editor**
3. Copy and paste the contents of `supabase_schema.sql` into the SQL Editor
4. Click **Run** to execute the script

This will create all necessary tables:
- `chw` - Community Health Workers
- `patient` - Patients
- `encounter` - Healthcare encounters
- `gps_location` - GPS coordinates
- `observation` - Clinical observations
- `disease_report` - Disease case reports
- `risk_assessment` - Risk level assessments
- `measure_report` - Aggregated statistics

## Step 2: Get Your API Key

1. In Supabase Dashboard, go to **Settings** → **API**
2. Copy the **anon/public** key (or **service_role** key for server-side operations)
3. Update `SupabaseConfig.java` with your API key:

```java
public static final String API_KEY = "your-actual-api-key-here";
```

Alternatively, you can set it programmatically:

```java
SupabaseClient.setApiKey(context, "your-api-key");
```

## Step 3: Configure Row Level Security (RLS)

The SQL script creates basic RLS policies that allow all operations. For production:

1. Go to **Authentication** → **Policies** in Supabase Dashboard
2. Review and customize policies based on your security requirements
3. Consider restricting access based on user roles

## Step 4: Test the Connection

1. Run the Android app
2. Fill out the report form in `ReportCaseFragment`
3. Submit a test case
4. Check Supabase Dashboard → **Table Editor** to verify data was saved

## Database Schema Overview

### Relationships:
- **CHW** → creates many **Encounters**
- **Patient** → visits many **Encounters**
- **Encounter** → has one **GPSLocation**, records many **Observations**, generates one **DiseaseReport**
- **DiseaseReport** → produces one **RiskAssessment**, aggregates many **MeasureReports**

### Key Fields:
- All tables use VARCHAR(255) for IDs (UUIDs as strings)
- Dates use DATE or TIMESTAMP types
- Coordinates use DOUBLE PRECISION
- Text fields use TEXT or VARCHAR with appropriate lengths

## Troubleshooting

### Error: "API key not found"
- Make sure you've set the API key in `SupabaseConfig.java` or via `SupabaseClient.setApiKey()`

### Error: "Table does not exist"
- Run the `supabase_schema.sql` script in the SQL Editor

### Error: "Permission denied"
- Check RLS policies in Supabase Dashboard
- Ensure your API key has proper permissions

### Error: "Foreign key constraint violation"
- Make sure parent records (CHW, Patient) are created before child records (Encounter, etc.)
- The `SupabaseService` handles this automatically by saving in the correct order

## API Endpoints

The app uses Supabase's PostgREST API automatically. Endpoints are:
- `POST /rest/v1/{table_name}` - Create record
- `GET /rest/v1/{table_name}` - Get records
- `PUT /rest/v1/{table_name}` - Update record

All endpoints require:
- `apikey` header with your API key
- `Authorization: Bearer {api_key}` header
- `Content-Type: application/json` header

## Next Steps

1. Set up proper authentication if needed
2. Create indexes for frequently queried fields
3. Set up database backups
4. Configure real-time subscriptions if needed
5. Set up database functions/triggers for automated tasks

