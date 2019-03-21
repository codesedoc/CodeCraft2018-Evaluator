package com.huawei.structure;

import org.apache.log4j.Logger;

import java.util.*;

public class CarsManager {
    private static CarsManager singleInstance=null;

    private int  countOfCarInNet;

    public HashMap<Integer,Car> mapOfCar=new HashMap<>();

    private ArrayList<Car> carList=new ArrayList<>();

    private static final Logger logger = Logger.getLogger(CarsManager.class);


    public TreeSet<Integer> getCarsMaxSpeedSet() {
        return carsMaxSpeedSet;
    }



    private TreeSet<Integer> carsMaxSpeedSet=new TreeSet<>();

    public CarsManager(String carFile){
        createCars(carFile);
        singleInstance = this;
//        for(Car car:carSet){
//            logger.info(car.getId());
//        }

    }
    public static CarsManager getInstance(String carFile){
        if (singleInstance==null)
            singleInstance=new CarsManager(carFile);
        return singleInstance;
    }

    /**
     * 通过文件构建car的集合
     * @param carFile
     * @return
     */
    private ArrayList<Car> createCars(String carFile){
        ArrayList<Car> cars = new ArrayList();
        List<String> carStrList = com.huawei.FileUtils.readFileIntoList(carFile);
        carStrList.remove(0);
        for (String carStr:carStrList){
            if (carStr.startsWith("#"))
                continue;
            String[] carItem = carStr.substring(1,carStr.length()-1).split(",\\s*");
            Car car = new Car(Integer.parseInt(carItem[0]),
                    Integer.parseInt(carItem[1]),
                    Integer.parseInt(carItem[2]),
                    Integer.parseInt(carItem[3]),
                    Integer.parseInt(carItem[4]));
            cars.add(car);
            carsMaxSpeedSet.add(car.getMaxSpeed());
            mapOfCar.put(car.getId(),car);
            carList.add(car);
        }
        return cars;
    }
}
