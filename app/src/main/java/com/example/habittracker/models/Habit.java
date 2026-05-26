package com.example.habittracker.models;

public class Habit {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String days; // e.g. "1,2,3,4,5" (1=Mon...7=Sun)
    private String createdAt;
    private int streak;
    private boolean completedToday;

    public Habit() {}

    public Habit(int userId, String title, String description, String days, String createdAt) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.days = days;
        this.createdAt = createdAt;
        this.streak = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDays() { return days; }
    public void setDays(String days) { this.days = days; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public boolean isCompletedToday() { return completedToday; }
    public void setCompletedToday(boolean completedToday) { this.completedToday = completedToday; }
}