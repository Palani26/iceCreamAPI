package com.function;

public class Rating {

    /**
     * 
     * (Required)
     * 
     */
    private String id;
    /**
     * 
     * (Required)
     * 
     */
    private String userId;
    /**
     * 
     * (Required)
     * 
     */
    private String productId;
    /**
     * 
     * (Required)
     * 
     */
    private String timestamp;
    /**
     * 
     * (Required)
     * 
     */
    private String locationName;
    /**
     * 
     * (Required)
     * 
     */
    private Integer rating;
    /**
     * 
     * (Required)
     * 
     */
    private String userNotes;

    /**
     * 
     * (Required)
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getUserNotes() {
        return userNotes;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

}
