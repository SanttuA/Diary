package com.example.santtu.diary;

/**
 * This class will contain the data to save in database and display in UI
 */

public class DiaryEntry {

    private long id;
    private String date;
    private String diaryEntry;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getDiaryEntry() {return diaryEntry;}
    public void setDiaryEntry(String entry) {this.diaryEntry = entry;}
}
