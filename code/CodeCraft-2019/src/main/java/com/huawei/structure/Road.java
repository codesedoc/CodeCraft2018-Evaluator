package com.huawei.structure;

import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Road implements Comparable<Road>{
    private static final Logger logger = Logger.getLogger(TrafficControlCenter.class);
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
    private Cross startCross;

    /**
     * 重点id
     */
    private Cross endCross;
    
    
    private LanOfroad[] lans;

    /**
     * 最高限速
     */
    private int maxSpeed;

    public Road(int id,int length,int maxSpeed,int lanNum,Cross startCross,Cross endCross){
        this.roadId = id;
        this.countOfLane = lanNum;
        this.length = length;
        this.startCross = startCross;
        this.endCross = endCross;
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

    public  ArrayList<Car> getAllCars(int  numOfLan){
        ArrayList<Car> result=new ArrayList<>();
        ArrayList<Car> temp=lans[numOfLan].getAllCars();
        if (temp!=null)
            result.addAll(temp);
        return result;
    }

    public int getInterve(int lanOrderNum,int currentLoc) {
        int intervel=lans[lanOrderNum].getIntervel(currentLoc);
        return intervel;
    }

    public Car getFistWaitCar() {
        for (int j = 0; j < length; j++){
            for (int i = 0; i < countOfLane; i++) {
                Car car = lans[i].getFirstCar(j);
                if (car==null)
                    continue;;
                if (car.getCarStatus() == CarStatus.WAIT)
                    return car;
            }
        }
        return null;
    }
    public NetLocation getEmptyLoc(int intervel){
        NetLocation result;
        int i;
        for (i=0;i<countOfLane;i++) {
            result= lans[i].getEmptyLoc(intervel);
            if (result!=null){
                result.setRoad(this);
                return result;
            }
        }
        return null;
    }

    public boolean isAllEnd(){
        boolean result=true;
        int i;
        for (i=0;i<countOfLane;i++) {
            result =result&&lans[i].tailIsEnd();
        }
        return result;
    }


    public boolean moveCar(NetLocation netLocation,Car car,int distance){
        int lanOrderNum=netLocation.getLanOrderNum();
        if (lanOrderNum<0 ||lanOrderNum>= countOfLane ) {
            logger.error("number of lan too max");
            return false;
        }
        return (lans[netLocation.getLanOrderNum()].moveCar(netLocation,car,distance));
    }

    public boolean moveCarToHead(NetLocation netLocation,Car car){
        return (lans[netLocation.getLanOrderNum()].moveCarToHead(netLocation,car));
    }

    public boolean removeCar(NetLocation netLocation,Car car){
        int lanOrderNum=netLocation.getLanOrderNum();
        if (lanOrderNum<0 ||lanOrderNum>= countOfLane ) {
            logger.error("number of lan too max");
            return false;
        }
        return (lans[netLocation.getLanOrderNum()].removeCar(netLocation,car));
    }

    public boolean addCar(NetLocation netLocation,Car car){
        int lanOrderNum=netLocation.getLanOrderNum();
        if (lanOrderNum<0 ||lanOrderNum>= countOfLane ) {
            logger.error("number of lan too max");
            return false;
        }
        return (lans[netLocation.getLanOrderNum()].addCar(netLocation,car));
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

    public Cross getStartCross() {
        return startCross;
    }

    public void setStartCross(Cross startCross) {
        this.startCross = startCross;
    }

    public Cross getEndCross() {
        return endCross;
    }

    public void setEndCross(Cross endCross) {
        this.endCross = endCross;
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
        if (obj==null)
            return false;
        return this.roadId==((Road)obj).getRoadId();
    }
    @Override
    public String toString() {
        return ""+roadId;
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
    private int headIndex;
    private int tailIndex;
    private static final Logger logger = Logger.getLogger(TrafficControlCenter.class);

    public LanOfroad(int orderNum,int roadId,int capacitty,boolean isFull){
        
        this.orderNum=orderNum;
        this.roadId=roadId;
        this.capacitty=capacitty;
        this.isFull=isFull;
        this.length=capacitty;
        this.remainCapacitty=capacitty;
        carsInLan=new Car[capacitty];
        for (int i=0;i<capacitty;i++) {
            carsInLan[i]=null;
        }
        headIndex=capacitty-1;
        tailIndex=0;
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
        car.setLocation(netLocation);
        return true;
    }

    public boolean removeCar(NetLocation netLocation,Car car){
        int pos=netLocation.getLocInlan();
        if (pos<0 ||pos>=length)
            return false;
        carsInLan[pos]=null;
        car.setLocation(null);
        return true;
    }

    public boolean moveCar(NetLocation netLocation,Car car,int distance){
        int pos=netLocation.getLocInlan();
        int nextPos=pos+distance;
        if(removeCar(netLocation,car)) {
            netLocation.setLocInlan(nextPos);
            if (addCar(netLocation, car)) {
                car.setLocation(netLocation);
                return true;
            }
            netLocation.setLocInlan(pos);
        }
        logger.error("distance too max");
        return false;
    }


    public boolean moveCarToHead(NetLocation netLocation,Car car){
        for (int i=netLocation.getLocInlan()+1;i<length;i++) {
            if (carsInLan[i] != null)
                return false;
        }
        removeCar(netLocation,car);
        NetLocation newLoc=new NetLocation(netLocation.getRoad(),netLocation.getLanOrderNum(),length-1);
        carsInLan[length-1]=car;
        car.setLocation(newLoc);
        return true;
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
        int i;
        for (i=location+1;i<length;i++){
            if (carsInLan[i]!=null) {
                break;
            }
        }
        return i-1-location;
    }
    public Car getFirstCar(int index) {
        return carsInLan[headIndex-index];
    }

    public NetLocation getEmptyLoc(int intervel) {
        NetLocation result=new NetLocation();
        int i;
        if (carsInLan[0]!=null)
            return null;
        for (i=1;i<intervel;i++){
            if (carsInLan[i]!=null){
                break;
            }
        }
        result.setLocInlan(i-1);
        return result;
    }
    public boolean tailIsEnd() {
        if (carsInLan[0].getCarStatus()==CarStatus.END)
            return true;
        return false;

    }

    @Override
    public String toString() {
        return ""+roadId;
    }
}
