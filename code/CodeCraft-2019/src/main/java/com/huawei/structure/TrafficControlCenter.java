package com.huawei.structure;

import com.huawei.FileUtils;

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
        roadNet.createCrossesToCrosses(speedLevels);
        List<Car> carList=carsManager.getCarList();
        int count =carList.size();
        //StringBuilder stringBuilder=new StringBuilder("");
        ArrayList<String> anwserStrings=new ArrayList();
        double startTime=0;
        for(i=0;i<count;i++){
            Car car=carList.get(i);
            int startCrossId=car.getStartCrossId();
            int endCrossId=car.getEndCrossId();
            if (startTime<car.getPlanTime())
                startTime=car.getPlanTime();
            String tempStr="("+car.getId()+","+(startTime);
            ArrayList<Integer> path=roadNet.getPathByCrossId(car.getMaxSpeed(),startCrossId,endCrossId);
            for (Integer roadId:path
                 ) {
                tempStr+=","+String.valueOf(roadId);
            }
            tempStr+=")";
            //anwserStrings.append(tempStr);
            anwserStrings.add(tempStr);
            System.out.println(tempStr);
            startTime+=Math.ceil(roadNet.getPathWeightByCrossId(car.getMaxSpeed(),startCrossId,endCrossId)) ;
        }
        //String answerString=stringBuilder.toString();
        FileUtils.writeFileFromList(anwserStrings,answerFile);
    }

}
