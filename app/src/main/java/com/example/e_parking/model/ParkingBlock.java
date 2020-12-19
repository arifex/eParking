package com.example.e_parking.model;

public class ParkingBlock {
    private boolean isSelected;
    private boolean isBooked;
    public ParkingBlock(){

    }
    public ParkingBlock(boolean b) {
        isBooked=true;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
