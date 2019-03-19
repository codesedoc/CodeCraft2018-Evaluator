package com.huawei.structure;

import com.huawei.FileUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class TrafficControlCenter {
    private static  TrafficControlCenter SingleInstance=null;
    private String carFile;
    private String roadFile;
    private String crossFile;
    private String answerFile;
    private RoadNet roadNet;
    private CarsManager carsManager;
    private int  countOfCarInNet;
    private static final Logger logger = Logger.getLogger(CarsManager.class);
    public TrafficControlCenter(String carFile,String roadFile,String crossFile,String answerFile){
        this.carFile=carFile;
        this.roadFile=roadFile;
        this.crossFile=crossFile;
        this.answerFile=answerFile;
        roadNet=RoadNet.getInstance(roadFile,crossFile);
        carsManager=CarsManager.getInstance(carFile);
    }

//    private void driveNet{
//        int countOfNolabelCar=countOfCarInNet;
//        while(countOfNolabelCar>0){
//            for (Road road :roadNet.ge) {
//
//            }
//        }
//    }
    private int countOfWaitCars=0;
    private boolean driveAllendStateCarInNet(){
        Iterator<Road> itRoad=roadNet.getRoadSet().iterator();
        while(itRoad.hasNext()){
            Road road= itRoad.next();
            if (!tagCarsStatus(road))
                return false;
        }

        Iterator<Cross> itCross=roadNet.getCrossSet().iterator();
        while(itCross.hasNext()){
            Cross cross= itCross.next();
            Iterator<Road> it=cross.getAdjRoadSet().iterator();
            while(it.hasNext()){
                Road roadInCross = it.next();
                Car firstCar =roadNet.getFistWaitCar();
                if (firstCar)
            }
        }
        return true;
    }

    private isConflict(Cross cross,Road road,Road)
    private boolean tagFirstCar(Car car ){
        NetLocation location=car.getLocation();
        int intervel=roadNet.getIntervel(location);
        if (intervel>=car.getCurMaxSpeed()) {
            car.setCarStatus(CarStatus.END);
            return roadNet.moveCar(location,car,intervel);
        }
        else{
            int maxSpeed= Math.min(car.getNextRoad().getMaxSpeed(),car.getMaxSpeed());
            if((maxSpeed-intervel)<=0){
                car.setCarStatus(CarStatus.END);
                return roadNet.moveCar(location,car,intervel);
            }
            else {
                car.setCarStatus(CarStatus.WAIT);
                countOfWaitCars++;
            }
        }
        return true;
    }
    private boolean tagOtherCar(Car frontCar,Car curCar ){
        NetLocation location=curCar.getLocation();
        int intervel=roadNet.getIntervel(location);
        if (intervel>=curCar.getCurMaxSpeed()) {
            curCar.setCarStatus(CarStatus.END);
            return  roadNet.moveCar(location,curCar,intervel);
        }
        else{
            CarStatus carStatus= frontCar.getCarStatus();
            curCar.setCarStatus(carStatus);
            if(carStatus==CarStatus.END){
                return roadNet.moveCar(location,curCar,intervel);
            }
            else if (carStatus==CarStatus.END){
                countOfWaitCars++;
            }
            else
                return false;
        }
        return true;
    }

    private boolean tagCarsStatus(Road road){
        ArrayList<ArrayList<Car>> carsIdInRoad=roadNet.getAllCarsInRoad(road);
        for (ArrayList<Car> carsInLan:carsIdInRoad) {
            int countOfCars=carsInLan.size();
            Car car=carsInLan.get(0);
            if(!tagFirstCar(car))
                return false;
            Car preCar=car;
            for (int i=1;i<countOfCars;i++,preCar=car) {
                car=carsInLan.get(i);
                if (!tagOtherCar(preCar,car))
                    return false;
            }
        }
        return true;
    }

//    public void test(){
//        TreeSet<Integer> levelsOfSpeed=carsManager.getCarsMaxSpeedSet();
//        Iterator<Integer> it = levelsOfSpeed.iterator();
//        int speedLevels[]=new int[levelsOfSpeed.size()];
//        int i=0;
//        while (it.hasNext()) {
//            speedLevels[i++]=it.next();
//        }
//        roadNet.createAllNetWeight(speedLevels);
//        TreeSet<Car> carSet=carsManager.getCarSet();
//        int count =0;
//        //StringBuilder stringBuilder=new StringBuilder("");
//        ArrayList<String> anwserStrings=new ArrayList();
//        int startTime=0;
//        Iterator<Car> carIt=carSet.iterator();
//        while (carIt.hasNext()){
//            Car car=carIt.next();
//            int startCrossId=car.getStartCrossId();
//            int endCrossId=car.getEndCrossId();
//            if (startTime<car.getPlanTime())
//                startTime=car.getPlanTime();
//            String tempStr="("+car.getId()+","+(startTime);
//            ArrayList<Integer> path=roadNet.getUndirectedRoadIdPath(car.getMaxSpeed(),startCrossId,endCrossId);
//            for (Integer roadId:path
//                 ) {
//                tempStr+=","+String.valueOf(roadId);
//            }
//            tempStr+=")";
//            //anwserStrings.append(tempStr);
//            anwserStrings.add(tempStr);
//            logger.info(tempStr);
//            startTime+=Math.ceil(roadNet.getPathWeight(car.getMaxSpeed(),startCrossId,endCrossId)) ;
//            count++;
//        }
//        logger.info("车辆路线总数："+count);
//        //String answerString=stringBuilder.toString();
//        FileUtils.writeFileFromList(anwserStrings,answerFile);
//    }

}
