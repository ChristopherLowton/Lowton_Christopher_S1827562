package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562
import android.os.Build;

import java.io.Serializable;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;

import androidx.annotation.RequiresApi;

public class Item implements Serializable, Comparable<Item> {
    //Christopher Lowton - S1827562
    private String title = "";
    private String description = "";
    private String link = "";
    private String georssPoint = "";
    private String author = "";
    private String comments = "";
    private String pubDate = "";

    private String startDate = "";
    private String endDate = "";
    private String works = "";
    private String management = "";
    private String delayInformation = "";
    private String type = "";

    private float lat = 0;
    private float lng = 0;

    public Item() {
    }

    public Item(String title, String description, String link, String georssPoint, String author, String comments, String pubDate) {
        this.title = title;
        this.description = description;
        updateDescriptionData();
        this.link = link;
        this.georssPoint = georssPoint;
        updateLatLng();
        this.author = author;
        this.comments = comments;
        this.pubDate = pubDate;
    }

    private String formatDateString(String date) {
        if (date.isEmpty() == false) {
            return date.trim().split(",")[1].trim().replace("- ", "");
        }
        return "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateDescriptionData();
    }

    private void updateDescriptionData() {
        //Regex with positive lookahead so delimiters are kept for further recognition
        String[] desc = this.description.split("(?=Start Date:|End Date:|Works:|Management:|Delay Information:|TYPE :)");

        for (String part : desc) {
            if (part.contains("Start Date:")) {
                this.startDate = part.replace("Start Date:", "").trim();
            } else if (part.contains("End Date:")) {
                this.endDate = part.replace("End Date:", "").trim();
            } else if (part.contains("Works:")) {
                this.works = part.replace("Works:", "").trim();
            } else if (part.contains("Management:")) {
                this.management = part.replace("Management:", "").trim();
            } else if (part.contains("Delay Information:")) {
                this.delayInformation = part.replace("Delay Information:", "").trim();
            } else if (part.contains("TYPE :")) {
                this.delayInformation = part.replace("TYPE :", "").trim();
            }
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGeorssPoint() {
        return georssPoint;
    }

    public void setGeorssPoint(String georssPoint) {
        this.georssPoint = georssPoint;
        updateLatLng();
    }

    private void updateLatLng() {
        String[] points = this.georssPoint.split(" ");
        lat = Float.parseFloat(points[0]);
        lng = Float.parseFloat(points[1]);
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getStartDateString() {
        return startDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getStartDate() {
        String start = formatDateString(startDate);
        LocalDateTime start_date = LocalDateTime.MIN;
        if (start.isEmpty() == false) {
            start_date = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH));
        }
        return start_date;
    }

    public String getEndDateString() {
        return endDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getEndDate() {
        String end = formatDateString(endDate);
        LocalDateTime end_date = LocalDateTime.MIN;
        if (end.isEmpty() == false) {
            end_date = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH));
        }
        return end_date;
    }

    public String getWorks() {
        return works;
    }

    public String getManagement() {
        return management;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Long getWorksLength() {
        String start = formatDateString(startDate);
        String end = formatDateString(endDate);
        if (start.isEmpty() == false && end.isEmpty() == false) {
            LocalDateTime start_date = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH));
            LocalDateTime end_date = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH));
            return Duration.between(start_date, end_date).toHours();
        } else {
            return new Long(0);
        }
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n"
                + "Start Date: " + startDate + "\n"
                + "End Date: " + endDate + "\n"
                + "Works: " + works + "\n"
                + "Management: " + management + "\n"
                + "Delay Information: " + delayInformation + "\n"
                + "Link: " + link + "\n"
                + "Georss Point: " + georssPoint + "\n"
                + "Author: " + author + "\n"
                + "Comments: " + comments + "\n"
                + "Pub Date: " + pubDate;
    }

    public String shortString() {
        return "Start Date: " + startDate + "\n"
                + "End Date: " + endDate + "\n"
                + "Works: " + works + "\n"
                + "Management: " + management + "\n"
                + "Delay Information: " + delayInformation;
    }

    @Override
    public int compareTo(Item item) {
        return this.title.compareTo(item.title);
    }
}
