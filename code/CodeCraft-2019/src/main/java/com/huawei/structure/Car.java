package com.huawei.structure;


import java.util.ArrayList;
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

    /**
     * 当前最高速度
     */
    private int curMaxSpeed;


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
    private List<Road> path;


    /**
     * 下一个road
     */
    private int  roadpathIndex;

    public int getRunToNextRoadMaxLen() {
        return runToNextRoadMaxLen;
    }

    public void setRunToNextRoadMaxLen(int runToNextRoadMaxLen) {
        this.runToNextRoadMaxLen = runToNextRoadMaxLen;
    }

    private int runToNextRoadMaxLen;

    public Road getNextRoad() {
        if (roadpathIndex==path.size())
            return null;
        if (roadpathIndex+1<path.size())
            return path.get(roadpathIndex+1);
        return null;
    }
    public Road toNextRoad() {
        roadpathIndex++;
        if (roadpathIndex<path.size())
            return path.get(roadpathIndex);
        return null;
    }

    private ArrayList<Integer> drivePath;

    private NetLocation location;

    private CarStatus carStatus;

    public Car(int id,int startCrossId,int endCrossId,int maxSpeed,int planTime){
        this.id = id;
        this.startCrossId = startCrossId;
        this.endCrossId = endCrossId;
        this.maxSpeed = maxSpeed;
        this.planTime = planTime;
        drivePath=new ArrayList<>();
        location=null;
        carStatus=CarStatus.UNTAG;
        curMaxSpeed=maxSpeed;
    }

    public void setPath(ArrayList<Road> path) {
        this.path = path;
        roadpathIndex=-1;
    }

    public void addSubPathIntodrivePath(int roadId){
        drivePath.add(roadId);
    }

    public CarStatus getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(CarStatus carStatus) {
        this.carStatus = carStatus;
    }

    public int getCurMaxSpeed() {
        return curMaxSpeed;
    }

    public void setCurMaxSpeed(int curMaxSpeed) {
        this.curMaxSpeed = curMaxSpeed;
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

    public NetLocation getLocation() {
        return location;
    }

    public void setLocation(NetLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", startCrossId=" + startCrossId +
                ", endCrossId=" + endCrossId +
                ", startTime=" + startTime +
                ", planTime=" + planTime +
                ", path=" + path +
//                ", road=" +location.getRoad().getRoadId()/2+
//                ", lan=" + location.getLanOrderNum() +
//                ", locInlan=" + location.getLocInlan() +
                '}';
    }

    @Override
//    public int compareTo(Car o) {
//        int time= this.planTime-o.getPlanTime();
//        if (time==0){
//            int speed=o.maxSpeed-this.maxSpeed;
//            if (speed==0)
//                return -1;
//
//            return speed;
//        }
//        return time;
//    }
    public int compareTo(Car o) {
        int time= this.startTime-o.getStartTime();
        if (time==0){
            int carId=this.id-o.getId();
            if (carId==0)
                return -1;

            return carId;
        }
        return time;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getId()==((Car)obj).getId();
    }
}
