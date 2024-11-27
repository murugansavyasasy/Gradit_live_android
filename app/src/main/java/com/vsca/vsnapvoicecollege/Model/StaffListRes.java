package com.vsca.vsnapvoicecollege.Model;

import java.util.List;

public class StaffListRes{
    private int status;

    private String message;

    private List<StaffListData> data;


    public void setStatus(int id) {
        this.status = id;
    }

    public int getStatus() {
        return status;
    }

    public void setMessage(String id) {
        this.message = id;
    }

    public String getMessage() {
        return message;
    }

    public void setData(List<StaffListData> data) {
        this.data = data;
    }

    public List<StaffListData> getData() {
        return data;
    }


    public class StaffListData {

        private int staff_id;

        private String staff_name;

        public void setStaffId(int id) {
            this.staff_id = id;
        }

        public int getStaffId() {
            return staff_id;
        }

        public void setStaffName(String id) {
            this.staff_name = id;
        }

        public String getStaffName() {
            return staff_name;
        }


    }
}
