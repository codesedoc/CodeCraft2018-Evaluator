package com.huawei.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public /**
 * 定义路口数据结构
 */
class Cross implements Comparable<Cross> {
    /**
     * 路口id
     *
     */
    private int crossId;


    /**
     * 与之相连的路口，分为四个方向
     */
    private Road upRoadOut;
    private Road rightRoadOut;
    private Road downRoadOut;
    private Road leftRoadOut;

    private Road upRoadIn;
    private Road rightRoadIn;
    private Road downRoadIn;
    private Road leftRoadIn;

    private TreeSet<Road> adjRoadOutSet=new TreeSet<>();
    private TreeSet<Road> adjRoadInSet=new TreeSet<>();

    ArrayList<Car> firstCars=new ArrayList<>();

    public Cross(int id){
        this.crossId=id;

    }

    public TreeSet<Road> getAdjRoadInSet() {
        return adjRoadInSet;
    }

    private Road getUpRoadOut() {
        return upRoadOut;

    }

    private void setUpRoadOut(Road upRoadOut) {
        this.upRoadOut = upRoadOut;
        adjRoadOutSet.add(upRoadOut);
    }

    private Road getRightRoadOut() {
        return rightRoadOut;
    }

    private void setRightRoadOut(Road rightRoadOut) {
        this.rightRoadOut = rightRoadOut;
        adjRoadOutSet.add(rightRoadOut);
    }

    private Road getDownRoadOut() {

        return downRoadOut;
    }

    private void setDownRoadOut(Road downRoadOut) {
        this.downRoadOut = downRoadOut;
        adjRoadOutSet.add(downRoadOut);
    }

    private Road getLeftRoadOut() {
        return leftRoadOut;

    }

    private void setLeftRoadOut(Road leftRoadOut) {
        this.leftRoadOut = leftRoadOut;
        adjRoadOutSet.add(leftRoadOut);
    }

    private Road getUpRoadIn() {
        return upRoadIn;
    }

    private void setUpRoadIn(Road upRoadIn) {
        this.upRoadIn = upRoadIn;
        adjRoadInSet.add(upRoadIn);
    }

    private Road getRightRoadIn() {
        return rightRoadIn;
    }

    private void setRightRoadIn(Road rightRoadIn) {
        this.rightRoadIn = rightRoadIn;
        adjRoadInSet.add(rightRoadIn);
    }

    private Road getDownRoadIn() {
        return downRoadIn;
    }

    private void setDownRoadIn(Road downRoadIn) {
        this.downRoadIn = downRoadIn;
        adjRoadInSet.add(downRoadIn);
    }

    private Road getLeftRoadIn() {
        return leftRoadIn;
    }

    private void setLeftRoadIn(Road leftRoadIn) {
        this.leftRoadIn = leftRoadIn;
        adjRoadInSet.add(leftRoadIn);
    }

    public void setUpRoad(Road road){
        if(road==null)
            return;
        if (road.getEndCross()==this)
            this.setUpRoadIn(road);
        else
            this.setUpRoadOut(road);
    }
    public void setDownRoad(Road road){
        if (road.getEndCross()==this)
            this.setDownRoadIn(road);
        else
            this.setDownRoadOut(road);
    }
    public void setLeftRoad(Road road){
        if (road.getEndCross()==this)
            this.setLeftRoadIn(road);
        else
            this.setLeftRoadOut(road);
    }
    public void setRightRoad(Road road){
        if (road.getEndCross()==this)
            this.setRightRoadIn(road);
        else
            this.setRightRoadOut(road);
    }


    public int getCrossId() {
        return crossId;
    }

    public void setCrossId(int crossId) {
        this.crossId = crossId;
    }

    public TreeSet<Road> adjRoadOutSet() {
        return adjRoadOutSet;
    }

    public TreeSet<Road> adjRoadInSet() {
        return adjRoadInSet();
    }

    public RoadDirectionEn getRelativeDir(Road road,Road nextRoad) {
        if(road==null || nextRoad==null)
            return null;

        if (road.equals(upRoadIn)){
           if (nextRoad.equals(downRoadOut))
               return RoadDirectionEn.STRAIGHT;
           else if (nextRoad.equals(leftRoadOut))
                return RoadDirectionEn.RIGHT;
           else if (nextRoad.equals(rightRoadOut))
               return RoadDirectionEn.LEFT;
           else
               return null;
        }

        if (road.equals(downRoadIn)){
            if (nextRoad.equals(upRoadOut))
                return RoadDirectionEn.STRAIGHT;
            else if (nextRoad.equals(rightRoadOut))
                return RoadDirectionEn.RIGHT;
            else if (nextRoad.equals(leftRoadOut))
                return RoadDirectionEn.LEFT;
            else
                return null;
        }

        if (road.equals(leftRoadIn)){
            if (nextRoad.equals(upRoadOut))
                return RoadDirectionEn.LEFT;
            else if (nextRoad.equals(rightRoadOut))
                return RoadDirectionEn.STRAIGHT;
            else if (nextRoad.equals(downRoadOut))
                return RoadDirectionEn.RIGHT;
            else
                return null;
        }

        if (road.equals(rightRoadIn)){
            if (nextRoad.equals(upRoadOut))
                return RoadDirectionEn.RIGHT;
            else if (nextRoad.equals(leftRoadOut))
                return RoadDirectionEn.STRAIGHT;
            else if (nextRoad.equals(downRoadOut))
                return RoadDirectionEn.LEFT;
            else
                return null;
        }

        return null;
    }

    @Override
    public int compareTo(Cross o) {
        return this.crossId-o.crossId;
    }

    @Override
    public boolean equals(Object obj) {
        return  this.crossId==((Cross)obj).getCrossId();
    }

    public void addFirstcar(Road road){
        Car car=road.getFistWaitCar();
        if (car!=null && car.getNextRoad()!=null)
            firstCars.add(car);
    }
    public void createFirstcars(){
        Iterator<Road> it=adjRoadInSet.iterator();
        while (it.hasNext()){
            Road road=it.next();
            addFirstcar(road);
        }
    }
    public boolean removeFirstcar(Car car){
        return  firstCars.remove(car);
    }

    public boolean isConflict(Car car1){
        int car1Id=car1.getId();
        for (Car car2:firstCars) {
            if (car2.getId()!=car1Id)
                return isConflict(car1,car2);
        }
        return false;
    }

    private boolean isConflict(Car car1,Car car2){

        if (car1==null)
            car1=car1;
        if (car2.getLocation()==null)
            car2=car2;

        Road road1=car1.getLocation().getRoad();
        Road road1To=car1.getNextRoad();
        Road road2=car2.getLocation().getRoad();
        Road road2To=car2.getNextRoad();


        if (road1To==null)
            car1=car1;
        if (car2.getLocation()==null)
            car2=car2;

        if (!road1To.equals(road2To))
            return false;

        RoadDirectionEn car1dir=getRelativeDir(road1,road1To);
        RoadDirectionEn car2dir=getRelativeDir(road2,road2To);
        if (compareDirPriority(car1dir,car2dir)<0)
            return true;
        return false;
    }

    private int compareDirPriority(RoadDirectionEn dir1,RoadDirectionEn dir2){
        return dir1.getPriority()-dir2.getPriority();
    }
}

