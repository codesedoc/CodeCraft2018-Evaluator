package com.huawei.structure;

import java.util.ArrayList;

public class Road implements Comparable<Road>{
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
    
    
    private LanOfroad[] lans;

    /**
     * 最高限速
     */
    private int maxSpeed;

    public Road(int id,int length,int maxSpeed,int lanNum,int startId,int endId){
        this.roadId = id;
        this.countOfLane = lanNum;
        this.length = length;
        this.startCrossId = startId;
        this.endCrossId = endId;
        this.maxSpeed = maxSpeed;

        lans=new LanOfroad[lanNum];
        for (int i=0;i<lanNum;i++) {
            lans[i]=new LanOfroad(i,id,length,false);
        }
    }
    
    public boolean isFullOflan(int orderNum){
        return lans[orderNum].isFull();
    }


    public  ArrayList<ArrayList<Car>> getAllCars(){
        ArrayList<ArrayList<Car>> result=new ArrayList<>();
        for (int i=0;i<countOfLane;i++)
            result.add(lans[i].getAllCars());
        return result;
    }

    public int getInterve(int lanOrderNum,int currentLoc) {
        int intervel=lans[lanOrderNum].getIntervel(currentLoc);
        return intervel;
    }

    public boolean removeCar(NetLocation netLocation){
        int lanOrderNum=netLocation.getLanOrderNum();
        if (lanOrderNum<0 ||lanOrderNum>= countOfLane )
            return false;
        return lans[lanOrderNum].removeCar(netLocation);
    }

    public boolean addCar(NetLocation netLocation,Car car){
        int lanOrderNum=netLocation.getLanOrderNum();
        if (lanOrderNum<0 ||lanOrderNum>= countOfLane )
            return false;
        if(lans[lanOrderNum].addCar(netLocation,car)){
            car.setCurMaxSpeed(Math.min(car.getMaxSpeed(),maxSpeed));
            return true;
        }
        return false;
    }
    public Car getFistWaitCar(){
        for (int i=0;i<countOfLane;i++) {
            Car car= lans[i].getFistCar();
            if (car==null)
                return null;
            if (car.getCarStatus()==CarStatus.WAIT)
                return car;
        }
        return null;
    }
    public boolean moveCar(NetLocation location,Car car,int distance){

        return (lans[location.getLanOrderNum()].moveCar(location,car,distance));
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

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public int compareTo(Road o) {
        return this.roadId-o.roadId;
    }

    @Override
    public boolean equals(Object obj) {
        return this.roadId==((Road)obj).getRoadId();
    }
}

class LanOfroad{
    private int orderNum;
    private int roadId;
    private int capacitty;
    private int length;
    private int remainCapacitty;
    private boolean isFull;
    private Car[] carsInLan;
    private int startIndex;
    private int endIndex;
    public LanOfroad(int orderNum,int roadId,int capacitty,boolean isFull){
        
        this.orderNum=orderNum;
        this.roadId=roadId;
        this.capacitty=capacitty;
        this.isFull=isFull;
        this.length=capacitty;
        this.remainCapacitty=capacitty;
        for (int i=0;i<capacitty;i++) {
            carsInLan[i]=null;
        }
        startIndex=0;
        endIndex=capacitty-1;
    }


    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public int getCapacitty() {
        return capacitty;
    }

    public void setCapacitty(int capacitty) {
        this.capacitty = capacitty;
    }

    public int getRemainCapacitty() {
        return remainCapacitty;
    }

    public void setRemainCapacitty(int remainCapacitty) {
        this.remainCapacitty = remainCapacitty;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public Car getCar(int index) {
        return carsInLan[index];
    }


    public boolean addCar(NetLocation netLocation,Car car) {
        int  location=netLocation.getLocInlan();
        if (location<0 ||location>=length)
            return false;
        this.carsInLan[location] = car;

        return true;
    }

    public boolean removeCar(NetLocation netLocation){
        int pos=netLocation.getLocInlan();
        if (pos<0 ||pos>=length)
            return false;
        carsInLan[pos]=null;
        return true;
    }

    public boolean moveCar(NetLocation netLocation,Car car,int distance){
        int pos=netLocation.getLocInlan();
        int nextPos=pos+distance;

        if(removeCar(netLocation)) {
            netLocation.setLocInlan(nextPos);
            if (addCar(netLocation, car)) {
                car.setLocation(netLocation);
                return true;
            }
            netLocation.setLocInlan(pos);
        }
        return false;
    }


    public ArrayList<Car> getAllCars(){
        ArrayList<Car> result=new ArrayList<>();
        for (int i=length-1;i>=0;i-- ) {
            if (carsInLan[i]!=null)
                result.add(carsInLan[i]);
        }
        return result;
    }

    public int getIntervel(int location) {
        int nextCarLoc=length;
        for (int i=location;i<length;i++){
            if (carsInLan[i]!=null) {
                nextCarLoc = i;
            }
        }
        return length-nextCarLoc;
    }
    public Car getFistCar() {
        for (int i=length-1;i>=0;i--){
            if (carsInLan[i]!=null) {
                return carsInLan[i];
            }
        }
        return null;
    }

}
