package com.example.shared_calendar;

public class Sign_up_item {
     String ID;
     String NAME;
     String Email;
     String Image;

    public Sign_up_item(String id, String name, String Email, String Image) {
        this.ID = id;
        this.NAME = name;
        this.Email = Email;
        this.Image = Image;
    }

    public String getImage() {
        return Image;
    }
    public void setImage(String image) {
        Image = image;
    }
    public String getID() {
        return ID;
    }
    public String getName() {
        return NAME;
    }
    public String getEmail() {
        return Email;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public void setName(String NAME) {
        this.NAME = NAME;
    }
    public void setEmail(String Email) {
        this.Email = Email;
    }


}
