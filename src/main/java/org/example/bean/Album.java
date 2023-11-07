package org.example.bean;

public class Album {
    byte[] image;
    Profile profile;

    public Album( byte[] image, Profile profile){
        this.image = image;
        this.profile = profile;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
