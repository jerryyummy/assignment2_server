package org.example.bean;

public class Profile {
    String artist;
    String year;
    String title;

    public Profile(String artist, String year, String title) {
        this.artist = artist;
        this.year = year;
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
