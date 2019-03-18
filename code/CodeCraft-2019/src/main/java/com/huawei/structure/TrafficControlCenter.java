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
    private static final Logger logger = Logger.getLogger(CarsManager.class);
    public TrafficControlCenter(String carFile,String roadFile,String crossFile,String answerFile){
        this.carFile=carFile;
        this.roadFile=roadFile;
        this.crossFile=crossFile;
        this.answerFile=answerFile;
        roadNet=RoadNet.getInstance(roadFile,crossFile);
        carsManager=CarsManager.getInstance(carFile);
    }

    public void test(){
        TreeSet<Integer> levelsOfSpeed=carsManager.getCarsMaxSpeedSet();
        Iterator<Integer> it = levelsOfSpeed.iterator();
        int speedLevels[]=new int[levelsOfSpeed.size()];
        int i=0;
        while (it.hasNext()) {
            speedLevels[i++]=it.next();
        }
        roadNet.createAllNetWeight(speedLevels);
        TreeSet<Car> carSet=carsManager.getCarSet();
        int count =0;
        //StringBuilder stringBuilder=new StringBuilder("");
        ArrayList<String> anwserStrings=new ArrayList();
        int startTime=0;
        Iterator<Car> carIt=carSet.iterator();
        while (carIt.hasNext()){
            Car car=carIt.next();
            int startCrossId=car.getStartCrossId();
            int endCrossId=car.getEndCrossId();
            if (startTime<car.getPlanTime())
                startTime=car.getPlanTime();
            String tempStr="("+car.getId()+","+(startTime);
            ArrayList<Integer> path=roadNet.getUndirectedRoadIdPath(car.getMaxSpeed(),startCrossId,endCrossId);
            for (Integer roadId:path
                 ) {
                tempStr+=","+String.valueOf(roadId);
            }
            tempStr+=")";
            //anwserStrings.append(tempStr);
            anwserStrings.add(tempStr);
            logger.info(tempStr);
            startTime+=Math.ceil(roadNet.getPathWeight(car.getMaxSpeed(),startCrossId,endCrossId)) ;
            count++;
        }
        logger.info("车辆路线总数："+count);
        //String answerString=stringBuilder.toString();
        FileUtils.writeFileFromList(anwserStrings,answerFile);
    }

}
