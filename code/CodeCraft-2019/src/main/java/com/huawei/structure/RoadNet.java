package com.huawei.structure;

import com.huawei.FileUtils;

import java.util.*;

public class RoadNet {
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
            int startCrossId=road.getStartCrossId();
            int endCrossId=road.getEndCrossId();
            graph.addOneArcAndTwoVer(road,mapOfCross.get(startCrossId),mapOfCross.get(endCrossId),road.getLength());
        }
        countOfCarInNet=0;
    }

    private void getRoadsAndCrossesFromFile(String roadsFilepath,String crossesFilepath){
        List<String> rows = FileUtils.readFileIntoList(roadsFilepath);
        for (int i=0;i<rows.size();i++) {
            String row=rows.get(i);
            if (row.startsWith("#"))
                continue;
            row=row.substring(1,row.length()-1);
            String[]items= row.split(",\\s*");
            Road road=new Road(2*Integer.parseInt(items[0]),
                    Integer.parseInt(items[1]),
                    Integer.parseInt(items[2]),
                    Integer.parseInt(items[3]),
                    Integer.parseInt(items[4]),
                    Integer.parseInt(items[5]));
            roadSet.add(road);
            mapOfRoad.put(road.getRoadId(),road);
            if (items[6].equals("1")){
                road=new Road(2*Integer.parseInt(items[0])+1,
                        Integer.parseInt(items[1]),
                        Integer.parseInt(items[2]),
                        Integer.parseInt(items[3]),
                        Integer.parseInt(items[5]),
                        Integer.parseInt(items[4]));
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
            Cross cross=new Cross(Integer.parseInt(items[0]),
                    mapOfRoad.get(Integer.parseInt(items[1])),
                    mapOfRoad.get(Integer.parseInt(items[2])),
                    mapOfRoad.get(Integer.parseInt(items[3])),
                    mapOfRoad.get(Integer.parseInt(items[4])));
            crossSet.add(cross);
            mapOfCross.put(cross.getCrossId(),cross);
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
    public boolean removeCar(NetLocation location){

        return location.getRoad().removeCar(location);
    }
    public boolean addCar(NetLocation location,Car car){
        return location.getRoad().addCar(location,car);
    }

    public boolean moveCar(NetLocation location,Car car,int distance){
        return location.getRoad().moveCar(location,car,distance);
    }

    public Car getFistWaitCar(Road road){
        return road.getFistWaitCar();
    }
}

