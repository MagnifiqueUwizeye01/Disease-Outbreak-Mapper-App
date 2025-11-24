package com.auca.healthtrack.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;

import java.util.Objects;

@Entity(tableName = "chw")
public class CHW implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "role")
    private String role;

    @ColumnInfo(name = "contact_info")
    private String contactInfo;

    public CHW() {
    }

    public CHW(String id, String name, String role, String contactInfo) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.contactInfo = contactInfo;
    }

    protected CHW(Parcel in) {
        id = in.readString();
        name = in.readString();
        role = in.readString();
        contactInfo = in.readString();
    }

    public static final Creator<CHW> CREATOR = new Creator<CHW>() {
        @Override
        public CHW createFromParcel(Parcel in) {
            return new CHW(in);
        }

        @Override
        public CHW[] newArray(int size) {
            return new CHW[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(contactInfo);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Practitioner toFhir() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId(this.id);

        HumanName humanName = new HumanName();
        humanName.setText(this.name);
        practitioner.addName(humanName);

        // Role is not a direct field in Practitioner, usually handled in
        // PractitionerRole resource
        // But we can add it as an identifier or extension if needed, or just ignore for
        // simple mapping
        // For this exercise, we'll keep it simple.

        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setValue(this.contactInfo);
        contactPoint.setSystem(ContactPoint.ContactPointSystem.PHONE); // Assuming phone for now
        practitioner.addTelecom(contactPoint);

        return practitioner;
    }

    public static CHW fromFhir(Practitioner practitioner) {
        CHW chw = new CHW();
        chw.setId(practitioner.getIdElement().getIdPart());
        if (practitioner.hasName()) {
            chw.setName(practitioner.getNameFirstRep().getText());
        }
        if (practitioner.hasTelecom()) {
            chw.setContactInfo(practitioner.getTelecomFirstRep().getValue());
        }
        // Role cannot be easily extracted from Practitioner alone without
        // PractitionerRole
        return chw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CHW chw = (CHW) o;
        return Objects.equals(id, chw.id) &&
                Objects.equals(name, chw.name) &&
                Objects.equals(role, chw.role) &&
                Objects.equals(contactInfo, chw.contactInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role, contactInfo);
    }

    @Override
    public String toString() {
        return "CHW{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}
