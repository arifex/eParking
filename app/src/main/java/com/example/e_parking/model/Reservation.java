package com.example.e_parking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Reservation implements Parcelable {
    private String parkingPlaceKey, fromTime, toTime,totalBlock, totalCharge, paymentId;

    public Reservation(){

    }


    protected Reservation(Parcel in) {
        parkingPlaceKey = in.readString();
        fromTime = in.readString();
        toTime = in.readString();
        totalBlock = in.readString();
        totalCharge = in.readString();
        paymentId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkingPlaceKey);
        dest.writeString(fromTime);
        dest.writeString(toTime);
        dest.writeString(totalBlock);
        dest.writeString(totalCharge);
        dest.writeString(paymentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    public String getParkingPlaceKey() {
        return parkingPlaceKey;
    }

    public void setParkingPlaceKey(String parkingPlaceKey) {
        this.parkingPlaceKey = parkingPlaceKey;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(String totalCharge) {
        this.totalCharge = totalCharge;
    }

    public String getTotalBlock() {
        return totalBlock;
    }

    public void setTotalBlock(String totalBlock) {
        this.totalBlock = totalBlock;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
