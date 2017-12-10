package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/6.
 */

public class BatchModel {

    @SerializedName("batch_day")
    private String batchDay;
    @SerializedName("batch_day_list")
    private ArrayList<BatchDay> batchDayList;

    public BatchModel() {
    }

    public BatchModel(String batchDay) {
        this.batchDay = batchDay;
    }

    public BatchModel(String batchDay, ArrayList<BatchDay> batchDayList) {
        this.batchDay = batchDay;
        this.batchDayList = batchDayList;
    }

    public class BatchDay{
        @SerializedName("batch_id")
        private String batchId;

        @SerializedName("batch_name")
        private String batchName;

        @SerializedName("created_time")
        private String createdTime;

        public BatchDay() {
        }

        public BatchDay(String batchId, String batchName) {
            this.batchId = batchId;
            this.batchName = batchName;
        }

        public String getBatchId() {
            return batchId;
        }

        public void setBatchId(String batchId) {
            this.batchId = batchId;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }
    }

    public String getBatchDay() {
        return batchDay;
    }

    public void setBatchDay(String batchDay) {
        this.batchDay = batchDay;
    }

    public ArrayList<BatchDay> getBatchDayList() {
        return batchDayList;
    }

    public void setBatchDayList(ArrayList<BatchDay> batchDayList) {
        this.batchDayList = batchDayList;
    }
}
