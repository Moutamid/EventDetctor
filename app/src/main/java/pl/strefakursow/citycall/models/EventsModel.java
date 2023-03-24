package pl.strefakursow.citycall.models;

public class EventsModel {
    String id, userID, desc, category, imageUri, startDate, endDate;
    double reactions;
    double latitude;
    double longitude;

    double star1,star2,star3,star4,star5;

    public EventsModel() {
    }

    public EventsModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public EventsModel(String id, String userID, String desc, String category, String imageUri, String startDate, String endDate, double reactions, double latitude, double longitude, double star1, double star2, double star3, double star4, double star5) {
        this.id = id;
        this.userID = userID;
        this.desc = desc;
        this.category = category;
        this.imageUri = imageUri;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reactions = reactions;
        this.latitude = latitude;
        this.longitude = longitude;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public double getReactions() {
        return reactions;
    }

    public void setReactions(double reactions) {
        this.reactions = reactions;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getStar1() {
        return star1;
    }

    public void setStar1(double star1) {
        this.star1 = star1;
    }

    public double getStar2() {
        return star2;
    }

    public void setStar2(double star2) {
        this.star2 = star2;
    }

    public double getStar3() {
        return star3;
    }

    public void setStar3(double star3) {
        this.star3 = star3;
    }

    public double getStar4() {
        return star4;
    }

    public void setStar4(double star4) {
        this.star4 = star4;
    }

    public double getStar5() {
        return star5;
    }

    public void setStar5(double star5) {
        this.star5 = star5;
    }
}
