package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */

@Entity(nameInDb = "incremental")
public class Incremental {
    @Id
    private Long id;

    @Property(nameInDb = "incremental_key")
    private String incrementalKey;

    @Property(nameInDb = "time")
    private String time;

    @Property(nameInDb = "shopId")
    private String shopId;

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIncrementalKey() {
        return this.incrementalKey;
    }

    public void setIncrementalKey(String incrementalKey) {
        this.incrementalKey = incrementalKey;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1078132368)
    public Incremental(Long id, String incrementalKey, String time, String shopId) {
        this.id = id;
        this.incrementalKey = incrementalKey;
        this.time = time;
        this.shopId = shopId;
    }

    @Generated(hash = 400908350)
    public Incremental() {
    }
}
