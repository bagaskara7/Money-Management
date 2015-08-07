package com.andronomy.moneymanagement.Lists;

/**
 * Created by bagaskara on 7/2/2015.
 */
public class ItemData {
    String text;
    String imageId;
    int Id;

    public ItemData(String text, String imageId, int Id) {
        this.text = text;
        this.imageId = imageId;
        this.Id = Id;
    }

    public String getText() {
        return text;
    }

    public String getImageId() {
        return imageId;
    }

    public int getId() { return Id; }
}
