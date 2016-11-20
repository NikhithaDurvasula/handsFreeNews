package com.example.nikhithadurvasula.news;

class newsItem
{
    private String author;
    private String newsHeading;
    private String newsDesc;
    private String url;
    private String imageURL;
    private String publishedAt;
    newsItem(String author,String newsHeading, String newsDesc, String url, String imageURL, String publishedAt)
    {
        this.author = author;
        this.newsHeading = newsHeading;
        this.url = url;
        this.newsDesc = newsDesc + "...";
        this.imageURL = imageURL;
        this.publishedAt = publishedAt;
    }

    String getAuthor()
    {
        return author;
    }

    String getNewsHeading()
    {
        return newsHeading;
    }

    String getUrl()
    {
        return url;
    }

    String getImageURL()
    {
        return imageURL;
    }
    String getPublishedAt()
    {
        return publishedAt;
    }

    String getNewsDesc()
    {
        return newsDesc;
    }
}

