package com.example.arslan;

public class News {
    private String title;
    private String description;
    private String content; // Full article content
    private String category;
    private int imageResource;
    private String imageUrl;
    private String videoUrl; 
    private String newsUrl;  

    public News(String title, String description, String content, String category, String imageUrl, String newsUrl) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
        this.imageUrl = imageUrl;
        this.newsUrl = newsUrl;
        this.videoUrl = null; // Removing video as requested
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public int getImageResource() { return imageResource; }
    public String getImageUrl() { return imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public String getNewsUrl() { return newsUrl; }
}
