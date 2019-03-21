package com.huawei.structure;

public enum RoadDirectionEn {
    STRAIGHT(3),LEFT(2),RIGHT(1);

    private int priority;

    RoadDirectionEn(int priority) {
        this.priority=priority;
    }

    public int getPriority(){
        return this.priority;
    }


}
