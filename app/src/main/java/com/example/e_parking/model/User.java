package com.example.e_parking.model;

public class User {
    private String userName, mobile, userType;
    public String name;
    public String email;
    public String avata;
  //  public Status status;
  //  public Message message;


    public User(){
      //  status = new Status();
      //  message = new Message();
        //status.isOnline = false;
        //status.timestamp = 0;
        /*message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;*/
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
