package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */

@Entity(nameInDb = "category")
public class Category {
    @Id
    private Long id;


    @Property(nameInDb = "user_id")
    private String userId;

    @Property(nameInDb = "category_id")
    private String categoryId;

    @Property(nameInDb = "category")
    private String category;

    @Property(nameInDb = "request_time")
    private String requestTime;

    public String getRequestTime() {
        return this.requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 803584027)
    public Category(Long id, String userId, String categoryId, String category,
            String requestTime) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.category = category;
        this.requestTime = requestTime;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }
}
