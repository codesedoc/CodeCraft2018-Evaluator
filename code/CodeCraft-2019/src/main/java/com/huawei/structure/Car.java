package com.huawei.structure;


import java.util.List;

/**
 * 车辆类
 */
public class Car implements Comparable<Car>{

    /**
     * 车辆id
     */
    private int id;

    /**
     * 始发路口id
     */
    private int startCrossId;

    /**
     * 终点路口id
     */
    private int endCrossId;

    /**
     * 最高速度
     */
    private int maxSpeed;

    public int getPlanTime() {
        return planTime;
    }

    public void setPlanTime(int planTime) {
        this.planTime = planTime;
    }

    /**
     * 出发时间
     */
    private int planTime;

    /**
     * 出发时间
     */
    private int startTime;

    /**
     * 路径id集合
     */
    private List<Integer> path;

    /**
     * 下一个路口id
     */
    private int nextCrossId;



    public Car(int id,int startCrossId,int endCrossId,int maxSpeed,int planTime){
        this.id = id;
        this.startCrossId = startCrossId;
        this.endCrossId = endCrossId;
        this.maxSpeed = maxSpeed;
        this.planTime = planTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getMaxSpeed() {
        if (maxSpeed==1)
            maxSpeed=maxSpeed;
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", startCrossId=" + startCrossId +
                ", endCrossId=" + endCrossId +
                ", maxSpeed=" + maxSpeed +
                ", startTime=" + startTime +
                ", planTime=" + planTime +
                ", path=" + path +
                ", nextCrossId=" + nextCrossId +
                '}';
    }

    @Override
    public int compareTo(Car o) {
        int time= this.planTime-o.getPlanTime();
        if (time==0){
            int speed=o.maxSpeed-this.maxSpeed;
            if (speed==0)
                return -1;

            return speed;
        }
        return time;
    }
}
