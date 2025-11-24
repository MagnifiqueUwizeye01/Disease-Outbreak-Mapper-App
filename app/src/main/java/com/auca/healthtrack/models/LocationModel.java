package com.auca.healthtrack.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.hl7.fhir.r4.model.Location;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "location")
public class LocationModel implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "address")
    private String address;

    public LocationModel() {
    }

    public LocationModel(String id, double latitude, double longitude, long timestamp, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.address = address;
    }

    protected LocationModel(Parcel in) {
        id = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        timestamp = in.readLong();
        address = in.readString();
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(timestamp);
        dest.writeString(address);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location toFhir() {
        Location location = new Location();
        location.setId(this.id);
        location.setPosition(
                new Location.LocationPositionComponent().setLatitude(this.latitude).setLongitude(this.longitude));
        location.setAddress(new org.hl7.fhir.r4.model.Address().setText(this.address));
        // Timestamp is not standard in Location resource, usually part of the Encounter
        // or Observation
        return location;
    }

    public static LocationModel fromFhir(Location location) {
        LocationModel model = new LocationModel();
        model.setId(location.getIdElement().getIdPart());
        if (location.hasPosition()) {
            model.setLatitude(location.getPosition().getLatitude().doubleValue());
            model.setLongitude(location.getPosition().getLongitude().doubleValue());
        }
        if (location.hasAddress()) {
            model.setAddress(location.getAddress().getText());
        }
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LocationModel that = (LocationModel) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                timestamp == that.timestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latitude, longitude, timestamp, address);
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", address='" + address + '\'' +
                '}';
    }
}
