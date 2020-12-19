package com.example.e_parking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingPlace implements Parcelable {
    private String title, address, location, owner, numberOfBlocks, phoneNumber, hourlyCharge="0";
    private int totalReserved=0;

    public ParkingPlace(){}


    protected ParkingPlace(Parcel in) {
        title = in.readString();
        address = in.readString();
        location = in.readString();
        owner = in.readString();
        numberOfBlocks = in.readString();
        phoneNumber = in.readString();
        hourlyCharge = in.readString();
        totalReserved = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(address);
        dest.writeString(location);
        dest.writeString(owner);
        dest.writeString(numberOfBlocks);
        dest.writeString(phoneNumber);
        dest.writeString(hourlyCharge);
        dest.writeInt(totalReserved);
    }

    public static final Creator<ParkingPlace> CREATOR = new Creator<ParkingPlace>() {
        @Override
        public ParkingPlace createFromParcel(Parcel in) {
            return new ParkingPlace(in);
        }

        @Override
        public ParkingPlace[] newArray(int size) {
            return new ParkingPlace[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getHourlyCharge() {
        return hourlyCharge;
    }

    public void setHourlyCharge(String hourlyCharge) {
        this.hourlyCharge = hourlyCharge;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNumberOfBlocks() {
        return numberOfBlocks;
    }

    public void setNumberOfBlocks(String numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getTotalReserved() {
        return totalReserved;
    }

    public void setTotalReserved(int totalReserved) {
        this.totalReserved = totalReserved;
    }

}
