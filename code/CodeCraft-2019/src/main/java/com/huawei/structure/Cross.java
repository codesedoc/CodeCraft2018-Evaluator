package com.huawei.structure;

import java.util.TreeSet;

public /**
 * 定义路口数据结构
 */
class Cross implements Comparable<Cross> {
    /**
     * 路口id
     *
     */
    private int crossId;


    /**
     * 与之相连的路口，分为四个方向
     */
    private Road upRoad;
    private Road rightRoad;
    private Road downRoad;
    private Road leftRoad;

    private TreeSet<Road> adjRoadSet=new TreeSet<>();
    public Cross(int id,Road upRoad,Road rightRoad,Road downRoad, Road leftRoad){
        this.crossId=id;
        this.upRoad=upRoad;
        this.rightRoad=rightRoad;
        this.downRoad=downRoad;
        this.leftRoad=leftRoad;

        adjRoadSet.add(upRoad);
        adjRoadSet.add(rightRoad);
        adjRoadSet.add(downRoad);
        adjRoadSet.add(leftRoad);

    }

    public int getCrossId() {
        return crossId;
    }

    public void setCrossId(int crossId) {
        this.crossId = crossId;
    }

    public TreeSet<Road> getAdjRoadSet() {
        return adjRoadSet;
    }


    public RoadDirectionEn getRelativeDir(Road road,Road nextRoad) {
        if (road.equals(upRoad)){
           if (nextRoad.equals(downRoad))
               return RoadDirectionEn.STRAIGHT;
           else if (nextRoad.equals(leftRoad))
                return RoadDirectionEn.RIGHT;
           else if (nextRoad.equals(rightRoad))
               return RoadDirectionEn.LEFT;
           else
               return null;
        }

        if (road.equals(downRoad)){
            if (nextRoad.equals(upRoad))
                return RoadDirectionEn.STRAIGHT;
            else if (nextRoad.equals(rightRoad))
                return RoadDirectionEn.RIGHT;
            else if (nextRoad.equals(leftRoad))
                return RoadDirectionEn.LEFT;
            else
                return null;
        }

        if (road.equals(leftRoad)){
            if (nextRoad.equals(upRoad))
                return RoadDirectionEn.LEFT;
            else if (nextRoad.equals(rightRoad))
                return RoadDirectionEn.STRAIGHT;
            else if (nextRoad.equals(downRoad))
                return RoadDirectionEn.RIGHT;
            else
                return null;
        }

        if (road.equals(rightRoad)){
            if (nextRoad.equals(upRoad))
                return RoadDirectionEn.RIGHT;
            else if (nextRoad.equals(leftRoad))
                return RoadDirectionEn.STRAIGHT;
            else if (nextRoad.equals(downRoad))
                return RoadDirectionEn.LEFT;
            else
                return null;
        }

        return null;
    }

    @Override
    public int compareTo(Cross o) {
        return this.crossId-o.crossId;
    }
}

