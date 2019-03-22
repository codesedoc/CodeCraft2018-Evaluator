package com.huawei.structure;

import com.huawei.FileUtils;
import org.apache.log4j.Logger;

import java.text.NumberFormat;
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

    private ArrayList<Car>  carsInNet;
    private ArrayList<Car>  carsNoStart;
    private ArrayList<Car>  carsInNetPre;

    private int  countOfCars;
    private int  countOfNoStartCars;
    private int  countOfArriveCars;
    private int  countOfCarInNet;

    private static final Logger logger = Logger.getLogger(TrafficControlCenter.class);

    public TrafficControlCenter(String carFile,String roadFile,String crossFile,String answerFile){
        this.carFile=carFile;
        this.roadFile=roadFile;
        this.crossFile=crossFile;
        this.answerFile=answerFile;
        roadNet=RoadNet.getInstance(roadFile,crossFile);
        carsManager=CarsManager.getInstance(carFile);
        //countOfCar=carsManager.getCountOfCar();
    }

    private int systemTime=0;
    private int allCarsRunTime=0;
    private void addCarIntoNet() {
        if (carsNoStart.size()==0)
            return;
        Car car=this.carsNoStart.get(0);
        if (car.getStartTime()>systemTime)
            return;
        int max=Math.min(car.getMaxSpeed(),car.getNextRoad().getMaxSpeed());
        car.setRunToNextRoadMaxLen(max);
        car.setCurMaxSpeed(max);
        if (roadNet.addCarIntoNet(car)) {
            car.toNextRoad();
            car.setCarStatus(CarStatus.END);
        }
        else
            return;
        this.carsInNet.add(car);
        this.carsNoStart.remove(0);

        countOfNoStartCars--;
        countOfCarInNet++;
    }


    private void removeCarFromNet(Car car) {
        if (!this.carsInNet.remove(car))
            car=car;
        countOfCarInNet--;
        countOfArriveCars++;
        allCarsRunTime+=(systemTime-car.getPlanTime()+1);
    }

    public void initCarsWithAnswer(){
        TreeSet<Car> cars = new TreeSet();
        List<String> carStrList = com.huawei.FileUtils.readFileIntoList(answerFile);
        for (String carStr:carStrList){
            if (carStr.startsWith("#"))
                continue;
            if (carStr.equals(""))
                continue;
            String[] carItems = carStr.substring(1,carStr.length()-1).split(",\\s*");

            Car car=carsManager.mapOfCar.get(Integer.parseInt(carItems[0]));

            car.setStartTime(Integer.parseInt(carItems[1]));
            ArrayList<Integer> roadIdPath=new ArrayList<>();

            for (int i=2;i<carItems.length;i++) {
                try {
                    roadIdPath.add(Integer.parseInt(carItems[i]));
                } catch (Exception e) {
                    e = e;
                }
            }
            ArrayList<Road> roadPath=roadNet.getRoadPath(car,roadIdPath);
            car.setPath(roadPath);
            cars.add(car);
        }

        carsNoStart=new ArrayList<>();
        carsInNet=new ArrayList<>();
        Iterator<Car> it=cars.iterator();
        while (it.hasNext()){
            Car car=it.next();
            carsNoStart.add(car);
        }

        countOfNoStartCars=carsNoStart.size();
        countOfCarInNet=0;
        countOfArriveCars=0;
    }

//    private void driveNet{
//        int countOfNolabelCar=countOfCarInNet;
//        while(countOfNolabelCar>0){
//            for (Road road :roadNet.ge) {
//
//            }
//        }
//    }

    public void driveAllCars(){
        systemTime=0;
        allCarsRunTime=0;
        addCarIntoNet();
        while (countOfCarInNet>0 || countOfNoStartCars>0) {
            //recordScene();
            if (!driveAllCarInNet()) {
                //recoveryScene();
                //changePathOfCars();
                logger.error("死锁");
                return;
            }
            systemTime++;
            addCarIntoNet();
//            if (systemTime==318707)
//                systemTime=systemTime;
        }
        logger.error("系统调度时间："+systemTime);
        logger.error("所有车辆总调度时间："+allCarsRunTime);
        //outPut();
    }

    private int countOfWaitCars=0;
    private ArrayList<Car> allfirstWaiteCars=new ArrayList<>();

    private boolean driveAllCarInNet(){
        Iterator<Road> itRoad=roadNet.getRoadSet().iterator();
        countOfWaitCars=0;
        for (int j=0;j<countOfCarInNet;j++)
            carsInNet.get(j).setCarStatus(CarStatus.UNDEFINE);
        if (countOfCarInNet!=carsInNet.size()) {
            logger.error("运行错误");
            return false;
        }
        while(itRoad.hasNext()){
            Road road= itRoad.next();
            if (!tagCarsStatus(road))
                return false;
        }
        while (countOfWaitCars>0){
            int preCount=countOfWaitCars;
            Iterator<Cross> itCross=roadNet.getCrossSet().iterator();
            while (itCross.hasNext()) {
                Cross cross = itCross.next();
                cross.createFirstcars();

                Iterator<Road> it;
                it = cross.getAdjRoadInSet().iterator();
                while (it.hasNext()) {
                    Road roadInCross = it.next();
                    Car firstCar = roadInCross.getFistWaitCar();
                    if (firstCar != null) {
                        if (cross.isConflict(firstCar))
                            continue;
                        NetLocation carLoc=firstCar.getLocation();
                        if (roadNet.moveCar(carLoc, cross, firstCar)) {
                            if (firstCar.getNextRoad()==null)
                                removeCarFromNet(firstCar);
                            else
                                firstCar.setCurMaxSpeed(Math.min(firstCar.getLocation().getRoad().getMaxSpeed(),firstCar.getMaxSpeed()));
                            firstCar.setCarStatus(CarStatus.END);
                            tagCarsStatusInLan(carLoc.getRoad(),carLoc.getLanOrderNum());
                            countOfWaitCars--;
                            cross.addFirstcar(roadInCross);
                        }
                    }
                }
            }
            if (preCount==countOfWaitCars){
                //allfirstWaiteCars.clear();
                //allfirstWaiteCars.addAll(roadNet.getAllFirstWaitCars());
                for (int i=0;i<carsInNet.size();i++)
                    System.out.println(carsInNet.get(i));
                return false;
            }
        }
        return true;
    }


    private boolean tagFirstCar(Car car ){
        if (car.getId()==17588)
            car=car;
        NetLocation location=car.getLocation();
        int intervel=roadNet.getIntervel(location);
        if (intervel>=car.getCurMaxSpeed()) {
            if (car.getId()==13886)
                car=car;
            if (car.getCarStatus()!=CarStatus.END) {
                if (car.getCarStatus()==CarStatus.WAIT)
                    countOfWaitCars--;
                car.setCarStatus(CarStatus.END);
                return roadNet.moveCar(location, car, car.getCurMaxSpeed());
            }
        }
        else{
            Road nextRoad= car.getNextRoad();
            int maxSpeed;
            if (nextRoad!=null)
                maxSpeed= Math.min(car.getNextRoad().getMaxSpeed(),car.getMaxSpeed());
            else{
                if (car.getCarStatus()!=CarStatus.END) {
                    NetLocation location1=car.getLocation();
                    if (car.getCarStatus()==CarStatus.WAIT)
                        countOfWaitCars--;
                    car.setCarStatus(CarStatus.END);
                    roadNet.removeCar(location1,car);
                    removeCarFromNet(car);
                    tagCarsStatusInLan(location1.getRoad(),location1.getLanOrderNum());
                }
                return true;
            }
            if((maxSpeed-intervel)<=0){
                if (car.getCarStatus()!=CarStatus.END) {
                    if (car.getCarStatus()==CarStatus.WAIT)
                        countOfWaitCars--;
                    car.setCarStatus(CarStatus.END);
                    return roadNet.moveCar(location, car, intervel);
                }
            }
            else {
                if (car.getCarStatus()==CarStatus.UNDEFINE) {
                    countOfWaitCars++;
                    car.setCarStatus(CarStatus.WAIT);
                    car.setRunToNextRoadMaxLen(maxSpeed-intervel);
                }
            }
        }
        return true;
    }
    private boolean tagOtherCar(Car frontCar,Car curCar ){
        if (curCar.getId()==17588)
            curCar=curCar;
        NetLocation location=curCar.getLocation();
        int intervel=roadNet.getIntervel(location);
        if (intervel>=curCar.getCurMaxSpeed()) {
            if (curCar.getCarStatus()!=CarStatus.END) {
                if (curCar.getCarStatus()==CarStatus.WAIT)
                    countOfWaitCars--;
                curCar.setCarStatus(CarStatus.END);
                return roadNet.moveCar(location, curCar, intervel);
            }
        }
        else{
            CarStatus carStatus= frontCar.getCarStatus();
            if(carStatus==CarStatus.END){
                if (curCar.getCarStatus()!=CarStatus.END) {
                    if (curCar.getCarStatus()==CarStatus.WAIT)
                        countOfWaitCars--;
                    curCar.setCarStatus(CarStatus.END);
                    return roadNet.moveCar(location, curCar, intervel);
                }
            }
            else if (carStatus==CarStatus.WAIT){
                if (curCar.getCarStatus()==CarStatus.UNDEFINE) {
                    countOfWaitCars++;
                }
                curCar.setCarStatus(carStatus);
            }
            else
                return false;

        }
        return true;
    }

    private boolean tagCarsStatusInLan(Road road,int numOfLan){
        ArrayList<Car> carsInLan=roadNet.getAllCarsInLan(road,numOfLan);
        int countOfCars=carsInLan.size();
        if (countOfCars==0)
            return true;
        Car car=carsInLan.get(0);
//        if (car.getId()==15495)
//            car=car;
        if (car.getLocation()==null)
            return true;
        if(!tagFirstCar(car))
            return false;
        Car preCar=car;
        for (int i=1;i<countOfCars;i++,preCar=car) {
            car=carsInLan.get(i);
            if (!tagOtherCar(preCar,car))
                return false;
        }
        return true;
    }
    private boolean tagCarsStatus(Road road){
        if (road.getRoadId()==10111)
            road=road;
        ArrayList<ArrayList<Car>> carsIdInRoad=roadNet.getAllCarsInRoad(road);
        for (ArrayList<Car> carsInLan:carsIdInRoad) {
            int countOfCars=carsInLan.size();
            if (countOfCars==0)
                continue;
            Car car=carsInLan.get(0);
            if (car.getId()==19098 && systemTime==72)
                car=car;
            if(!tagFirstCar(car))
                return false;
            if (car.getLocation()==null)
                return true;
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
