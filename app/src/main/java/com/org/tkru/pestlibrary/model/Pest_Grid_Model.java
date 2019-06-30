package com.org.tkru.pestlibrary.model;

/**
 * Created by tkru on 7/5/2017.
 */

public class Pest_Grid_Model {
    private String pest_id;
    private String pest_title;
    private String pest_image;
    private String pest_contains;
    private String pest_banner_image;

    public String getPest_banner_image() {
        return pest_banner_image;
    }

    public void setPest_banner_image(String pest_banner_image) {
        this.pest_banner_image = pest_banner_image;
    }

    public String getPest_id() {
        return pest_id;
    }

    public void setPest_id(String pest_id) {
        this.pest_id = pest_id;
    }

    public String getPest_title() {
        return pest_title;
    }

    public void setPest_title(String pest_title) {
        this.pest_title = pest_title;
    }

    public String getPest_image() {
        return pest_image;
    }

    public void setPest_image(String pest_image) {
        this.pest_image = pest_image;
    }

    public String getPest_contains() {
        return pest_contains;
    }

    public void setPest_contains(String pest_contains) {
        this.pest_contains = pest_contains;
    }
}
