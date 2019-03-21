package com.huawei.structure;

import com.huawei.FileUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class RoadNet {
    private static final Logger logger =Logger.getLogger(RoadNet.class);
    private XSGraph<Cross,Road> graph=new XSGraph();
    private TreeSet<Road> roadSet=new TreeSet<>();
    private TreeSet<Cross>crossSet=new TreeSet<>();
    private HashMap<Integer,Cross> mapOfCross=new HashMap<>();
    private HashMap<Integer,Road> mapOfRoad=new HashMap<>();
    private int  countOfCarInNet;

//    private HashMap<Integer,Road> mapOfRoad=new HashMap<>();

    public RoadNet(String roadsFilepath, String crossesFilepath){
        this.getRoadsAndCrossesFromFile(roadsFilepath,crossesFilepath);
        Iterator<Road> it=roadSet.iterator();
        while (it.hasNext()){
            Road road=it.next();
            graph.addOneArcAndTwoVer(road,road.getStartCross(),road.getEndCross(),road.getLength());
        }
        countOfCarInNet=0;
    }

    private void getRoadsAndCrossesFromFile(String roadsFilepath,String crossesFilepath){
        List<String> rows;
        rows= FileUtils.readFileIntoList(roadsFilepath);
        for (int i=0;i<rows.size();i++) {
            String row=rows.get(i);
            if (row.startsWith("#"))
                continue;
            row=row.substring(1,row.length()-1);
            String[]items= row.split(",\\s*");

            Cross cross1,cross2;
            if ((cross1=mapOfCross.get(Integer.parseInt(items[4])))==null) {
                cross1= new Cross(Integer.parseInt(items[4]));
                mapOfCross.put(cross1.getCrossId(),cross1);
                crossSet.add(cross1);
            }
            if ((cross2=mapOfCross.get(Integer.parseInt(items[5])))==null) {
                cross2= new Cross(Integer.parseInt(items[5]));
                mapOfCross.put(cross2.getCrossId(),cross2);
                crossSet.add(cross2);
            }
            Road road=new Road(2*Integer.parseInt(items[0]),
                    Integer.parseInt(items[1]),
                    Integer.parseInt(items[2]),
                    Integer.parseInt(items[3]),
                    cross1,
                    cross2);
            roadSet.add(road);
            mapOfRoad.put(road.getRoadId(),road);
            if (items[6].equals("1")){
                road=new Road(2*Integer.parseInt(items[0])+1,
                        Integer.parseInt(items[1]),
                        Integer.parseInt(items[2]),
                        Integer.parseInt(items[3]),
                        cross2,
                        cross1);
            }
            roadSet.add(road);
            mapOfRoad.put(road.getRoadId(),road);
        }
        rows = FileUtils.readFileIntoList(crossesFilepath);
        for (int i=0 ,j=0;i<rows.size();i++) {
            String row=rows.get(i);
            if (row.startsWith("#")) {
                continue;
            }
            row=row.substring(1,row.length()-1);
            String[]items= row.split(",\\s*");
            int crossId= Integer.parseInt(items[0]);
            Cross cross=mapOfCross.get(crossId);
            int uniqueUpRoadId=Integer.parseInt(items[1]);
            int uniqueRightRoadId=Integer.parseInt(items[2]);
            int uniqueDownRoadId=Integer.parseInt(items[3]);
            int uniqueLeftRoadId=Integer.parseInt(items[4]);

            Road road;
            if ((road=mapOfRoad.get(2*uniqueUpRoadId))!=null)
                cross.setUpRoad(road);

            if ((road=mapOfRoad.get(2*uniqueUpRoadId+1))!=null){
                cross.setUpRoad(road);
            }
            if ((road=mapOfRoad.get(2*uniqueRightRoadId))!=null)
                cross.setRightRoad(road);

            if ((road=mapOfRoad.get(2*uniqueRightRoadId+1))!=null){
                cross.setRightRoad(road);
            }

            if ((road=mapOfRoad.get(2*uniqueDownRoadId))!=null)
                cross.setDownRoad(road);

            if ((road=mapOfRoad.get(2*uniqueDownRoadId+1))!=null){
                cross.setDownRoad(road);
            }

            if ((road=mapOfRoad.get(2*uniqueLeftRoadId))!=null)
                cross.setLeftRoad(road);

            if ((road=mapOfRoad.get(2*uniqueLeftRoadId+1))!=null){
                cross.setLeftRoad(road);
            }

        }
    }

    private static com.huawei.structure.RoadNet SingeleInstance=null;


    public static com.huawei.structure.RoadNet getInstance(String roadFile, String crossFile){
        if (SingeleInstance==null)
            SingeleInstance=new com.huawei.structure.RoadNet(roadFile,crossFile);
        return SingeleInstance;

    }

    private void  updataAllRoadWeight(int limitedMaxSpeed){
        Iterator<Road> it=roadSet.iterator();
        while (it.hasNext()){
            Road road=it.next();
            double weight=Math.ceil(road.getLength()*1.0/Integer.min(road.getMaxSpeed(),limitedMaxSpeed))  ;
            graph.changeWeightOfArc(road,weight);
        }
    }

    private void  addRoadNetWeight(int level){
        updataAllRoadWeight(level);
        graph.addNetWeight(level);
    }

    public void createAllNetWeight(int speedLevels[]){
        for (int i:speedLevels) {
            addRoadNetWeight(i);
        }
    }

    public ArrayList<Cross> getCrossPath(int level,Cross origin,Cross destination){
        ArrayList<Cross> result=graph.getVerPath(level,origin,destination);
        return result;
    }

    public ArrayList<Road> getRoadPath(int level,Cross origin,Cross destination){
        ArrayList<Road> result=graph.getArcPath(level,origin,destination);
        return result;
    }

    public ArrayList<Integer> getUndirectedRoadIdPath(int level,Cross origin,Cross destination){
        ArrayList<Integer> result=new ArrayList<>();
        ArrayList<Road> temp=getRoadPath(level,origin,destination);
        for (Road road:temp) {
            result.add(road.getRoadId()/2);
        }
        return result;
    }

    public ArrayList<Integer> getUndirectedRoadIdPath(int level,int originId,int  destinationId){
        ArrayList<Integer> result;
        Cross origin=mapOfCross.get(originId);
        Cross destination=mapOfCross.get(destinationId);
        result=getUndirectedRoadIdPath(level,origin,destination);
        return result;
    }


    public double getPathWeight(int level,Cross origin,Cross destination){
        double result=graph.getPathWeight(level,origin,destination);
        return result;
    }
    public double getPathWeight(int level,int originId,int  destinationId){
        Cross origin=mapOfCross.get(originId);
        Cross destination=mapOfCross.get(destinationId);
        double result=getPathWeight(level,origin,destination);
        return result;
    }

    public ArrayList<ArrayList<Car>> getAllCarsInRoad(Road road){
        return road.getAllCars();
    }

    public TreeSet<Road> getRoadSet() {
        return roadSet;
    }

    public TreeSet<Cross> getCrossSet() {
        return crossSet;
    }

    public int getIntervel(NetLocation currentLoc) {
        return currentLoc.getRoad().getInterve(currentLoc.getLanOrderNum(),currentLoc.getLocInlan());
    }
    public boolean removeCar(NetLocation location,Car car){

        return location.getRoad().removeCar(location,car);
    }
    public boolean addCar(NetLocation location,Car car){
        return location.getRoad().addCar(location,car);
    }

    public boolean moveCar(NetLocation location,Car car,int distance){
        return location.getRoad().moveCar(location,car,distance);
    }

    private boolean removeCarFromCross(Cross cross ,Car car){
        return cross.removeFirstcar(car);

    }

    public boolean moveCar(NetLocation netLocation,Cross cross ,Car car){
        if (car.getNextRoad()==null) {
            netLocation.getRoad().removeCar(netLocation,car);
            removeCarFromCross(cross,car);
            return true;
        }
        NetLocation nextLoc=car.getNextRoad().getEmptyLoc(car.getRunToNextRoadMaxLen());
        if (nextLoc==null) {
            if (!car.getNextRoad().isAllEnd())
                return false;
            else {
                removeCarFromCross(cross,car);
                return netLocation.getRoad().moveCarToHead(netLocation,car);
            }
        }
        if(netLocation.getRoad().removeCar(netLocation,car)) {
            if (removeCarFromCross(cross, car))
                if (nextLoc.getRoad().addCar(nextLoc, car)){
                    car.toNextRoad();
                    return true;
                }
        }
        return false;
    }


    public ArrayList<Car> getAllFirstWaitCars() {
        ArrayList<Car> result = new ArrayList<>();
        Iterator<Road> it = roadSet.iterator();
        while (it.hasNext()) {
            Road roadInCross = it.next();
            Car firstCar = roadInCross.getFistWaitCar();
            if (firstCar != null) {
                result.add(firstCar);
            }
        }
        return result;
    }

    public ArrayList<Road> getRoadPath(Car car, ArrayList<Integer> roadUnIdPath) {
        ArrayList<Road> result = new ArrayList<>();
        Cross  cross=mapOfCross.get(car.getStartCrossId());
        for (int i=0;i<roadUnIdPath.size();i++) {
            Road road1 = mapOfRoad.get(2 * roadUnIdPath.get(i));
            Road road2 = mapOfRoad.get(2 * roadUnIdPath.get(i)+1);
            if (road1!= null) {
                if (road1.getStartCross().equals(cross)) {
                    result.add(road1);
                    cross=road1.getEndCross();
                }
                else if(road2.getStartCross().equals(cross)){
                    result.add(road2);
                    cross=road2.getEndCross();
                }
            }
        }
        if (!cross.equals(mapOfCross.get(car.getEndCrossId()))) {
            logger.error("路径还原错误");
            return null;
        }
        return result;
    }

    public ArrayList<Integer> getRoadIdPath(ArrayList<Road> roadPath) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i=0;i<roadPath.size();i++)
            result.add(roadPath.get(i).getRoadId());
        return result;
    }

    public boolean addCarIntoNet(Car car){
        NetLocation nextLoc=car.getNextRoad().getEmptyLoc(car.getRunToNextRoadMaxLen());
        if (nextLoc==null) {
            return false;
        }
        return addCar(nextLoc,car);
    }

}

