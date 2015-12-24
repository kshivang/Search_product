package com.adurcup.search_experiment;


/**
 * Created by NikTin on 07/11/15.
 */
public class FeedItem {
    private String title;
    private String thumbnail;
    private String url;
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category=category;
    }
}
