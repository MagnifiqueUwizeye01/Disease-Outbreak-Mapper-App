package com.auca.healthtrack.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Reference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "disease_report")
public class DiseaseReport implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "patient_id")
    private String patientId;

    @ColumnInfo(name = "chw_id")
    private String chwId;

    @ColumnInfo(name = "date")
    private long date;

    @Ignore
    private List<String> observationIds;

    public DiseaseReport() {
        observationIds = new ArrayList<>();
    }

    public DiseaseReport(String id, String patientId, String chwId, long date, List<String> observationIds) {
        this.id = id;
        this.patientId = patientId;
        this.chwId = chwId;
        this.date = date;
        this.observationIds = observationIds != null ? observationIds : new ArrayList<>();
    }

    protected DiseaseReport(Parcel in) {
        id = in.readString();
        patientId = in.readString();
        chwId = in.readString();
        date = in.readLong();
        observationIds = in.createStringArrayList();
    }

    public static final Creator<DiseaseReport> CREATOR = new Creator<DiseaseReport>() {
        @Override
        public DiseaseReport createFromParcel(Parcel in) {
            return new DiseaseReport(in);
        }

        @Override
        public DiseaseReport[] newArray(int size) {
            return new DiseaseReport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(patientId);
        dest.writeString(chwId);
        dest.writeLong(date);
        dest.writeStringList(observationIds);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getChwId() {
        return chwId;
    }

    public void setChwId(String chwId) {
        this.chwId = chwId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<String> getObservationIds() {
        return observationIds;
    }

    public void setObservationIds(List<String> observationIds) {
        this.observationIds = observationIds;
    }

    public Composition toFhir() {
        Composition composition = new Composition();
        composition.setId(this.id);
        composition.setSubject(new Reference("Patient/" + this.patientId));
        composition.addAuthor(new Reference("Practitioner/" + this.chwId));
        composition.setDate(new Date(this.date));

        // Sections for observations
        Composition.SectionComponent section = composition.addSection();
        for (String obsId : observationIds) {
            section.addEntry(new Reference("Observation/" + obsId));
        }

        return composition;
    }

    public static DiseaseReport fromFhir(Composition composition) {
        DiseaseReport report = new DiseaseReport();
        report.setId(composition.getIdElement().getIdPart());
        if (composition.hasSubject()) {
            report.setPatientId(composition.getSubject().getReferenceElement().getIdPart());
        }
        if (composition.hasAuthor()) {
            report.setChwId(composition.getAuthorFirstRep().getReferenceElement().getIdPart());
        }
        if (composition.hasDate()) {
            report.setDate(composition.getDate().getTime());
        }

        List<String> obsIds = new ArrayList<>();
        for (Composition.SectionComponent section : composition.getSection()) {
            for (Reference entry : section.getEntry()) {
                if (entry.getReference().startsWith("Observation")) {
                    obsIds.add(entry.getReferenceElement().getIdPart());
                }
            }
        }
        report.setObservationIds(obsIds);

        return report;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DiseaseReport that = (DiseaseReport) o;
        return date == that.date &&
                Objects.equals(id, that.id) &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(chwId, that.chwId) &&
                Objects.equals(observationIds, that.observationIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patientId, chwId, date, observationIds);
    }

    @Override
    public String toString() {
        return "DiseaseReport{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", chwId='" + chwId + '\'' +
                ", date=" + date +
                ", observationIds=" + observationIds +
                '}';
    }
}
