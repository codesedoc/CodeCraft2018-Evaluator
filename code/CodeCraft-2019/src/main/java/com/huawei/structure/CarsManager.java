package com.huawei.structure;

import java.util.*;

public class CarsManager {
    private static CarsManager singleInstance=null;

    public ArrayList<Car> getCarList() {
        return carList;
    }

    private ArrayList<Car> carList;

    public int getCountOfCar() {
        return countOfCar;
    }

    private int countOfCar;
    public TreeSet<Integer> getCarsMaxSpeedSet() {
        return carsMaxSpeedSet;
    }


    private TreeSet<Integer> carsMaxSpeedSet=new TreeSet<>();

    public CarsManager(String carFile){
        carList = this.createCars(carFile);
        singleInstance = this;
        countOfCar=carList.size();
        sortAllCars(1);
//        for (Car car:carList
//             ) {
//            System.out.println(car);
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
        ArrayList<Car> cars = new ArrayList<>();
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
        }
        return cars;
    }

    public void sortAllCars(int timeScale){
        Collections.sort(carList, new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return o1.getPlanTime()/timeScale -o2.getPlanTime()/timeScale;
            }
        });
        ArrayList<Car> sortCars=new ArrayList<>();
        ArrayList<Car> carsTemp=new ArrayList<>();
        for (int i=0;i<carList.size();i++){
            carsTemp.add(carList.get(i));
            if (i==carList.size()-1){
                Collections.sort(carsTemp, new Comparator<Car>() {
                    @Override
                    public int compare(Car o1, Car o2) {
                        return o2.getMaxSpeed() -o1.getMaxSpeed();
                    }
                });
                sortCars.addAll(carsTemp);
                carsTemp=new ArrayList<>();
                break;
            }
            if(carList.get(i).getPlanTime()==carList.get(i+1).getPlanTime()){
                continue;
            }
            Collections.sort(carsTemp, new Comparator<Car>() {
                @Override
                public int compare(Car o1, Car o2) {
                    return o2.getMaxSpeed() -o1.getMaxSpeed();
                }
            });

            sortCars.addAll(carsTemp);
            carsTemp=new ArrayList<>();
        }
        carList=sortCars;

    }

}
