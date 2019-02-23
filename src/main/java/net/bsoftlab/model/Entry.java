package net.bsoftlab.model;

import java.util.Date;

public class Entry {

    private Integer id = null;
    private Date dateTime = null;
    private String ip = null;
    private String request = null;
    private Integer status = null;
    private String client = null;

    public Entry(){}

    public Integer getId() {
        return this.id;
    }
    public Date getDateTime() {
        return this.dateTime;
    }
    public String getIp() {
        return this.ip;
    }
    public String getRequest() {
        return this.request;
    }
    public Integer getStatus() {
        return this.status;
    }
    public String getClient() {
        return this.client;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setRequest(String request) {
        this.request = request;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!this.getClass().equals(object.getClass())) {
            return false;
        }
        Entry entry = (Entry) object;
        return this.id.equals(entry.getId()) &&
                this.dateTime.equals(entry.getDateTime()) &&
                this.ip.equals(entry.getIp()) &&
                this.request.equals(entry.getRequest()) &&
                this.status.equals(entry.getStatus()) &&
                this.client.equals(entry.getClient());
    }

    @Override
    public String toString() {
        return this.id + "|" +
                this.dateTime + "|" +
                this.ip + "|" +
                this.request + "|" +
                this.status + "|" +
                this.client;
    }
}
