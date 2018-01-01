package com.pressure.blecentral.services.entity;

/**
 * Created by zhangfeng on 2017/12/19.
 */

public class Devices {
    private String name;
    private String address;
    private String UUID;
    private String readuuid;

    public Devices(String name, String address, String UUID, String readuuid) {
        this.name = name;
        this.address = address;
        this.UUID = UUID;
        this.readuuid = readuuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getReaduuid() {
        return readuuid;
    }

    public void setReaduuid(String readuuid) {
        this.readuuid = readuuid;
    }
}
