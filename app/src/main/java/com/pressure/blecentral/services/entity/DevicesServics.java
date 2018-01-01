package com.pressure.blecentral.services.entity;

import java.io.Serializable;

/**
 * Created by zhangfeng on 19/12/17.
 */

public class DevicesServics implements Serializable {
    private String charUUID;
    private String properties;

    public DevicesServics(String charUUID, String properties) {
        this.charUUID = charUUID;
        this.properties = properties;
    }

    public String getCharUUID() {
        return charUUID;
    }

    public void setCharUUID(String charUUID) {
        this.charUUID = charUUID;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }


    @Override
    public String toString() {
        return "DevicesServics{" +
                "charUUID='" + charUUID + '\'' +
                ", properties='" + properties + '\'' +
                '}';
    }

}
