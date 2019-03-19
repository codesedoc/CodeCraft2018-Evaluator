package com.huawei.structure;

public class NetLocation {
    private Road road;
    private int lanOrderNum;
    private int locInlan;

    NetLocation(Road road,int lanOrderNum,int locInlan){
        this.road=road;
        this.lanOrderNum=lanOrderNum;
        this.locInlan=locInlan;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public int getLanOrderNum() {
        return lanOrderNum;
    }

    public void setLanOrderNum(int lanOrderNum) {
        this.lanOrderNum = lanOrderNum;
    }

    public int getLocInlan() {
        return locInlan;
    }

    public void setLocInlan(int locInlan) {
        this.locInlan = locInlan;
    }
}
