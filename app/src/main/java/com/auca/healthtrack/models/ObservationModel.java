package com.auca.healthtrack.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "observation")
public class ObservationModel implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "value")
    private String value;

    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "date")
    private long date;

    public ObservationModel() {
    }

    public ObservationModel(String id, String code, String value, String unit, long date) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.unit = unit;
        this.date = date;
    }

    protected ObservationModel(Parcel in) {
        id = in.readString();
        code = in.readString();
        value = in.readString();
        unit = in.readString();
        date = in.readLong();
    }

    public static final Creator<ObservationModel> CREATOR = new Creator<ObservationModel>() {
        @Override
        public ObservationModel createFromParcel(Parcel in) {
            return new ObservationModel(in);
        }

        @Override
        public ObservationModel[] newArray(int size) {
            return new ObservationModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(code);
        dest.writeString(value);
        dest.writeString(unit);
        dest.writeLong(date);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Observation toFhir() {
        Observation observation = new Observation();
        observation.setId(this.id);

        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(new Coding().setCode(this.code).setSystem("http://loinc.org")); // Defaulting to LOINC
        observation.setCode(codeableConcept);

        if (this.value != null) {
            // Assuming value is numeric for simplicity if unit is present, otherwise string
            try {
                double val = Double.parseDouble(this.value);
                Quantity quantity = new Quantity();
                quantity.setValue(val);
                quantity.setUnit(this.unit);
                observation.setValue(quantity);
            } catch (NumberFormatException e) {
                observation.setValue(new org.hl7.fhir.r4.model.StringType(this.value));
            }
        }

        observation.setEffective(new org.hl7.fhir.r4.model.DateTimeType(new Date(this.date)));

        return observation;
    }

    public static ObservationModel fromFhir(Observation observation) {
        ObservationModel model = new ObservationModel();
        model.setId(observation.getIdElement().getIdPart());

        if (observation.hasCode() && observation.getCode().hasCoding()) {
            model.setCode(observation.getCode().getCodingFirstRep().getCode());
        }

        if (observation.hasValue()) {
            if (observation.getValue() instanceof Quantity) {
                model.setValue(String.valueOf(((Quantity) observation.getValue()).getValue()));
                model.setUnit(((Quantity) observation.getValue()).getUnit());
            } else if (observation.getValue() instanceof org.hl7.fhir.r4.model.StringType) {
                model.setValue(((org.hl7.fhir.r4.model.StringType) observation.getValue()).getValue());
            }
        }

        if (observation.hasEffectiveDateTimeType()) {
            model.setDate(observation.getEffectiveDateTimeType().getValue().getTime());
        }

        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ObservationModel that = (ObservationModel) o;
        return date == that.date &&
                Objects.equals(id, that.id) &&
                Objects.equals(code, that.code) &&
                Objects.equals(value, that.value) &&
                Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, value, unit, date);
    }

    @Override
    public String toString() {
        return "ObservationModel{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", unit='" + unit + '\'' +
                ", date=" + date +
                '}';
    }
}
