package com.errorplayer.lala_weather.CustomSth.JuheNews;

/**
 * Created by ErrorPlayer on 2018/5/23.
 */

public class JuheNewsListItem {

    private String news_titleString ;

    private String imageId;

    private String NewsContentUrl;

    public JuheNewsListItem(String news_titleString, String imageId, String newsContentUrl) {
        this.news_titleString = news_titleString;
        this.imageId = imageId;
        NewsContentUrl = newsContentUrl;
    }

    public String getNews_titleString() {
        return news_titleString;
    }

    public String getImageId() {
        return imageId;
    }

    public String getNewsContentUrl() {
        return NewsContentUrl;
    }
}
