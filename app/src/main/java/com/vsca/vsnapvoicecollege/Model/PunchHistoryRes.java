package com.vsca.vsnapvoicecollege.Model;

import java.util.List;

public class PunchHistoryRes {
    private int status;

    private String message;

    private List<PunchHistoryData> data;


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

    public void setData(List<PunchHistoryRes.PunchHistoryData> data) {
        this.data = data;
    }
    public List<PunchHistoryRes.PunchHistoryData> getData() {
        return data;
    }


    public class PunchHistoryData {

        private String date;

        private List<PunchHistoryTimings> timings;

        public void setDate(String id) {
            this.date = id;
        }

        public String getDate() {
            return date;
        }

        public void setTimings(List<PunchHistoryTimings> data) {
            this.timings = data;
        }
        public List<PunchHistoryTimings> getTimings() {
            return timings;
        }

        public class PunchHistoryTimings {

            private String r_time;
            private String r_device_id;

            private String r_device_model;
            private PunchType r_punch_type;

            public void setTime(String id) {
                this.r_time = id;
            }

            public String getTime() {
                return r_time;
            }

            public void setDeviceId(String id) {
                this.r_device_id = id;
            }

            public String getDeviceId() {
                return r_device_id;
            }

            public void setDeviceModel(String id) {
                this.r_device_model = id;
            }

            public String getDeviceModel() {
                return r_device_model;
            }

            public void setPunchType(PunchType id) {
                this.r_punch_type = id;
            }

            public PunchType getPunchType() {
                return r_punch_type;
            }

            public class PunchType {

                private int id;
                private String value;

                public void setId(int id) {
                    this.id = id;
                }

                public int getId() {
                    return id;
                }

                public void setValue(String id) {
                    this.value = id;
                }

                public String getValue() {
                    return value;
                }

            }

        }



    }
}