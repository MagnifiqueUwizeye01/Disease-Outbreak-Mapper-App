package com.auca.healthtrack.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "outbreak_map")
public class OutbreakMap implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "region_name")
    private String regionName;

    @ColumnInfo(name = "case_count")
    private int caseCount;

    @ColumnInfo(name = "risk_level")
    private String riskLevel;

    @ColumnInfo(name = "center_lat")
    private double centerLat;

    @ColumnInfo(name = "center_lng")
    private double centerLng;

    public OutbreakMap() {
    }

    public OutbreakMap(String id, String regionName, int caseCount, String riskLevel, double centerLat,
            double centerLng) {
        this.id = id;
        this.regionName = regionName;
        this.caseCount = caseCount;
        this.riskLevel = riskLevel;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
    }

    protected OutbreakMap(Parcel in) {
        id = in.readString();
        regionName = in.readString();
        caseCount = in.readInt();
        riskLevel = in.readString();
        centerLat = in.readDouble();
        centerLng = in.readDouble();
    }

    public static final Creator<OutbreakMap> CREATOR = new Creator<OutbreakMap>() {
        @Override
        public OutbreakMap createFromParcel(Parcel in) {
            return new OutbreakMap(in);
        }

        @Override
        public OutbreakMap[] newArray(int size) {
            return new OutbreakMap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(regionName);
        dest.writeInt(caseCount);
        dest.writeString(riskLevel);
        dest.writeDouble(centerLat);
        dest.writeDouble(centerLng);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(int caseCount) {
        this.caseCount = caseCount;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }

    // No FHIR mapping for this custom visualization class

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OutbreakMap that = (OutbreakMap) o;
        return caseCount == that.caseCount &&
                Double.compare(that.centerLat, centerLat) == 0 &&
                Double.compare(that.centerLng, centerLng) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(regionName, that.regionName) &&
                Objects.equals(riskLevel, that.riskLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, regionName, caseCount, riskLevel, centerLat, centerLng);
    }

    @Override
    public String toString() {
        return "OutbreakMap{" +
                "id='" + id + '\'' +
                ", regionName='" + regionName + '\'' +
                ", caseCount=" + caseCount +
                ", riskLevel='" + riskLevel + '\'' +
                ", centerLat=" + centerLat +
                ", centerLng=" + centerLng +
                '}';
    }
}
