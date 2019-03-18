package com.huawei.structure;

public /**
 * 定义路口数据结构
 */
class Cross{
    /**
     * 路口id
     *
     */
    private int crossId;


    /**
     * 与之相连的路口，分为四个方向
     */
    private int upRoadId;
    private int rightRoadId;
    private int downRoadId;
    private int leftRoadId;

    public Cross(int id,int upRoadId,int rightRoadId,int downRoadId, int leftRoadId){
        this.crossId=id;
        this.upRoadId=upRoadId;
        this.rightRoadId=rightRoadId;
        this.downRoadId=downRoadId;
        this.leftRoadId=leftRoadId;
    }

    public int getCrossId() {
        return crossId;
    }

    public void setCrossId(int crossId) {
        this.crossId = crossId;
    }

    public int getUpRoadId() {
        return upRoadId;
    }

    public void setUpRoadId(int upRoadId) {
        this.upRoadId = upRoadId;
    }

    public int getRightRoadId() {
        return rightRoadId;
    }

    public void setRightRoadId(int rightRoadId) {
        this.rightRoadId = rightRoadId;
    }

    public int getDownRoadId() {
        return downRoadId;
    }

    public void setDownRoadId(int downRoadId) {
        this.downRoadId = downRoadId;
    }

    public int getLeftRoadId() {
        return leftRoadId;
    }

    public void setLeftRoadId(int leftRoadId) {
        this.leftRoadId = leftRoadId;
    }
}