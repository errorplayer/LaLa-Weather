package com.errorplayer.lala_weather.gson.Juhe_News_Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ErrorPlayer on 2018/5/23.
 */

public class JuheNewsItem {

    @SerializedName("category")
    public String newsItem_category;

    @SerializedName("url")
    public String newsItem_url;

    @SerializedName("date")
    public String newsItem_date;

    @SerializedName("title")
    public String newsItem_title;

    @SerializedName("author_name")
    public String newsItem_author;

    @SerializedName("thumbnail_pic_s")
    public String newsItem_picUrl;
}
