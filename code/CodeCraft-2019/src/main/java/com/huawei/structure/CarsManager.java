package com.huawei.structure;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class CarsManager {
    private static CarsManager singleInstance=null;
    private HashMap<Integer,Car> mapOfCar=new HashMap<>();

    public TreeSet<Car> getCarSet() {
        return carSet;
    }

    private TreeSet<Car> carSet;

    public int getCountOfCar() {
        return countOfCar;
    }

    private static final Logger logger = Logger.getLogger(CarsManager.class);
    private int countOfCar;
    public TreeSet<Integer> getCarsMaxSpeedSet() {
        return carsMaxSpeedSet;
    }


    private TreeSet<Integer> carsMaxSpeedSet=new TreeSet<>();

    public CarsManager(String carFile){
        carSet = this.createCars(carFile);
        singleInstance = this;
        countOfCar=carSet.size();
        //sortAllCars(1);
        for (Car car:carSet
        ) {
            //logger.info(car.getId());
        }
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
    private TreeSet<Car> createCars(String carFile){
        TreeSet<Car> cars = new TreeSet();
        List<String> carList = com.huawei.FileUtils.readFileIntoList(carFile);
        carList.remove(0);
        for (String carStr:carList){
            String[] carItem = carStr.substring(1,carStr.length()-1).split(",\\s*");
            Car car = new Car(Integer.parseInt(carItem[0]),
                    Integer.parseInt(carItem[1]),
                    Integer.parseInt(carItem[2]),
                    Integer.parseInt(carItem[3]),
                    Integer.parseInt(carItem[4]));
            cars.add(car);
            carsMaxSpeedSet.add(car.getMaxSpeed());
            mapOfCar.put(car.getId(),car);
        }
        return cars;
    }


    public Car getCar(int carId){
        return mapOfCar.get(carId);
    }

    public void addPathtoCar(int roadId,int carId){
        Car car=mapOfCar.get(carId);
        if (car!=null)
            car.addSubPathIntodrivePath(roadId);
    }
}
