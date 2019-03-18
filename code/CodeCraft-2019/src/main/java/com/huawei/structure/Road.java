package com.huawei.structure;

public class Road{
    /**
     * 道路id
     */
    private int roadId;

    /**
     *车道数目
     */
    private int countOfLane;

    /**
     * 道路长度
     */
    private int length;

    /**
     * 起始点id
     */
    private int startCrossId;

    /**
     * 重点id
     */
    private int endCrossId;

    /**
     * 道路的详细信息,顺向
     */
    private int[][] capacity;


    /**
     * 是否逆向
     */
    private boolean isTwoWay;

    /**
     * 最高限速
     */
    private int maxSpeed;

    public Road(int id,int length,int maxSpeed,int lanNum,int startId,int endId){
        this.roadId = id;
        this.countOfLane = lanNum;
        this.length = length;
        this.capacity = new int[lanNum][length];
        this.startCrossId = startId;
        this.endCrossId = endId;
        this.maxSpeed = maxSpeed;
    }

    public int getLength() {
        return length;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public int getCountOfLane() {
        return countOfLane;
    }

    public void setCountOfLane(int countOfLane) {
        this.countOfLane = countOfLane;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStartCrossId() {
        return startCrossId;
    }

    public void setStartCrossId(int startCrossId) {
        this.startCrossId = startCrossId;
    }

    public int getEndCrossId() {
        return endCrossId;
    }

    public void setEndCrossId(int endCrossId) {
        this.endCrossId = endCrossId;
    }

    public int[][] getCapacity() {
        return capacity;
    }

    public void setCapacity(int[][] capacity) {
        this.capacity = capacity;
    }

    public boolean isTwoWay() {
        return isTwoWay;
    }

    public void setTwoWay(boolean twoWay) {
        isTwoWay = twoWay;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
