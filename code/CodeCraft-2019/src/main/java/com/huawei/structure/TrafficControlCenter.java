package com.huawei.structure;

import com.huawei.FileUtils;
import org.apache.log4j.Logger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static com.huawei.structure.CarStatus.*;

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
        Car car;
        int i;
        for (i=0;i<countOfNoStartCars;i++) {
            car=this.carsNoStart.get(i);
            if(car.getStartTime()>systemTime)
                break;
            int maxLen = Math.min(car.getMaxSpeed(), car.getNextRoad().getMaxSpeed());
            car.setRunToNextRoadMaxLen(maxLen);
            if (roadNet.addCarIntoNet(car)) {
                car.toNextRoad();
                car.setCarStatus(END);
            } else
                continue;
            this.carsInNet.add(car);
            this.carsNoStart.remove(i);

            countOfNoStartCars--;
            countOfCarInNet++;
        }
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
            if (systemTime==667)
                systemTime=systemTime;
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
            carsInNet.get(j).setCarStatus(UNTAG);
        if (countOfCarInNet!=carsInNet.size()) {
            logger.error("运行错误");
            return false;
        }
        while(itRoad.hasNext()){
            Road road= itRoad.next();
            if (!tagCarsStatus(road))
                return false;
        }
        for (int j=0;j<countOfCarInNet;j++) {
            Car car=carsInNet.get(j);
            if (car.getCarStatus() == CarStatus.UNTAG)
                countOfCarInNet = countOfCarInNet;
        }
        while (countOfWaitCars>0){
            int preCount=countOfWaitCars;
            Iterator<Cross> itCross=roadNet.getCrossSet().iterator();
            while (itCross.hasNext()) {
                Cross cross = itCross.next();
                cross.createFirstcars();
//                if(cross.getCrossId()==30)
//                    cross=cross;
                Iterator<Road> it;
                it = cross.getAdjRoadInSet().iterator();
                while (it.hasNext()) {
                    Road roadInCross = it.next();
                    Car firstCar = roadInCross.getFistWaitCar();
                    if (firstCar != null) {
//                        if (firstCar.getId()==11846 && roadInCross.getRoadId()==10084)
//                            firstCar=firstCar;
                        if (cross.isConflict(firstCar))
                            continue;
                        NetLocation carLoc=firstCar.getLocation();
//                        if (firstCar.getId()==13161 && carLoc.getRoad().getRoadId()==10078)
//                            firstCar=firstCar;
//                        if (firstCar.getId()==16803 && carLoc.getRoad().getRoadId()==10078)
//                            firstCar=firstCar;
                        if (roadNet.moveCar(carLoc, cross, firstCar)) {

                            firstCar.setCurMaxSpeed(Math.min(firstCar.getLocation().getRoad().getMaxSpeed(),firstCar.getMaxSpeed()));
                            firstCar.setCarStatus(END);
                            tagCarsStatus(carLoc.getRoad(),carLoc.getLanOrderNum());
                            cross.addFirstcar(roadInCross);
                            countOfWaitCars--;
                        }
                    }
                }
            }
            if (preCount==countOfWaitCars){
                //allfirstWaiteCars.clear();
                //allfirstWaiteCars.addAll(roadNet.getAllFirstWaitCars());
                for (int j=0;j<countOfCarInNet;j++) {
                    Car car=carsInNet.get(j);
                    if (car.getCarStatus() != END)
                        countOfCarInNet = countOfCarInNet;
                }
                for (int i=0;i<carsInNet.size();i++)
                    System.out.println(carsInNet.get(i));
                return false;
            }
        }

        return true;
    }


    private boolean tagFirstCar(Car car ){
        boolean result;
//        if (car.getId()==11846)
//            car=car;
        if (car.getCarStatus()== END)//车已经调度过
            result=true;
        else {//车没有调度过
            NetLocation location = car.getLocation();
            int intervel = roadNet.getIntervel(location);
            if (car.getNextRoad() == null) {//可以入库
                if (car.getCarStatus() == WAIT) {//之前标记的是等待，则入库并更新countOfWaitCars
                    countOfWaitCars--;
                    car.setCarStatus(END);
                    if (result = roadNet.removeCar(location, car)) {
                        removeCarFromNet(car);
                    }
                } else if (car.getCarStatus() == UNTAG) {//之前没有标记过，则直接入库
                    car.setCarStatus(END);
                    if (result = roadNet.removeCar(location, car)) {
                        removeCarFromNet(car);
                    }
                } else {
                    result = false;

                }
            }
            else {//不能入库
                int nextMaxSpeed = Math.min(car.getNextRoad().getMaxSpeed(), car.getMaxSpeed());
                if (car.getCarStatus() == WAIT) {
                    if (intervel >= car.getCurMaxSpeed()) {//不能到路边
                        countOfWaitCars--;
                        car.setCarStatus(END);
                        result = roadNet.moveCar(location, car, car.getCurMaxSpeed());
                    } else {//可以到路边
                        if ((nextMaxSpeed - intervel) <= 0) {//不可以过路口
                            countOfWaitCars--;
                            car.setCarStatus(END);
                            result = roadNet.moveCar(location, car, intervel);
                        } else {//可以过路口，继续等待,并刷新nextMaxlen
                            car.setRunToNextRoadMaxLen(nextMaxSpeed - intervel);
                            result = true;
                        }
                    }
                } else if (car.getCarStatus() == UNTAG) {
                    if (intervel >= car.getCurMaxSpeed()) {//不能到路边
                        car.setCarStatus(END);
                        result = roadNet.moveCar(location, car, car.getCurMaxSpeed());
                    } else {//可以到路边
                        if ((nextMaxSpeed - intervel) <= 0) {//不可以过路口
                            car.setCarStatus(END);
                            result = roadNet.moveCar(location, car, intervel);
                        } else {//可以过路口，等待,并刷新nextMaxlen
                            car.setCarStatus(WAIT);
                            countOfWaitCars++;
                            car.setRunToNextRoadMaxLen(nextMaxSpeed - intervel);
                            result = true;
                        }
                    }
                } else {
                    result = false;

                }
            }
        }
        return result;
    }
    private boolean tagOtherCar(Car frontCar,Car curCar ){
        boolean result;
//        if (curCar.getId()==16803 && curCar.getLocation().getRoad().getRoadId()==10078)
//            curCar=curCar;
        if (curCar.getCarStatus()== END)//车已经调度过
            result=true;
        else {//车没有调度过
            NetLocation location=curCar.getLocation();
            int intervel=roadNet.getIntervel(location);
            if (intervel>=curCar.getCurMaxSpeed()) {//没车阻挡
                if (curCar.getCarStatus() == WAIT) {//之前是等待状态（现在前车调度了，腾出了空间），则调度，并更新countOfWaitCars
                    countOfWaitCars--;
                    curCar.setCarStatus(END);
                    result = roadNet.moveCar(location, curCar, intervel);
                }
                else if (curCar.getCarStatus() == UNTAG) {//没有标记过，则直接调度
                    curCar.setCarStatus(END);
                    result = roadNet.moveCar(location, curCar, intervel);
                }
                else {
                    result = false;
                }
            }
            else {//有车阻挡
                CarStatus frontCarCarStatus = frontCar.getCarStatus();
                if (frontCarCarStatus == END) {
                    if (curCar.getCarStatus() == WAIT) {//之前是等待，则调度，并更新countOfWaitCars
                        countOfWaitCars--;
                        curCar.setCarStatus(END);
                        result = roadNet.moveCar(location, curCar, intervel);
                    } else if (curCar.getCarStatus() == UNTAG) {//没有被标记过则直接调度
                        curCar.setCarStatus(END);
                        result = roadNet.moveCar(location, curCar, intervel);
                    } else {
                        result = false;
                    }
                }
                else if(frontCarCarStatus == WAIT){
                    if (curCar.getCarStatus()==WAIT){//已经是等待，则保持
                            result=true;
                        }
                    else if(curCar.getCarStatus()==UNTAG)  {//没有被标记过则标记等待，并累计countOfWaitCars
                            countOfWaitCars++;
                            curCar.setCarStatus(WAIT);
                            result = true;
                        }
                    else{
                            result = false;
                    }
                }
                else
                    result=false;
            }
        }
        return result;
    }

    private boolean tagCarsStatus(Road road,int numOfLan){
        ArrayList<Car> carsInLan=roadNet.getAllCarsInLan(road,numOfLan);
        int countOfCars=carsInLan.size();
        if (countOfCars==0)
            return true;
        int i=0;
        Car car;
//        if (car.getId()==15495)
//            car=car;
        do {
            car=carsInLan.get(i);
            boolean temp= tagFirstCar(car);
            if (!temp)
                return false;
            i++;
        }while (car.getLocation()==null && i<countOfCars);

        Car preCar=car;
        for (;i<countOfCars;i++,preCar=car) {
            car=carsInLan.get(i);
            if (!tagOtherCar(preCar,car))
                return false;
        }
        return true;
    }
    private boolean tagCarsStatus(Road road){
        if (road.getRoadId()==10111)
            road=road;
        int count=road.getCountOfLane(),i;
        for (i=0;i<count;i++){
            if (!tagCarsStatus(road,i))
                return false;
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
