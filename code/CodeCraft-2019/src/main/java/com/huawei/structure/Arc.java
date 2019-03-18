package com.huawei.structure;

public class Arc<ArcInfo>{
    private ArcInfo arcInfo;
    private int arcHeadIndex;
    private Arc nextArcOfArcHead;
    private int arcTailIndex;
    private Arc nextArcOfArcTail;
    private double arcWeight;
    public Arc(ArcInfo arcInfo,int arcTailIndex,int arcHeadIndex,double arcWeight){
        this.arcInfo=arcInfo;
        this.arcHeadIndex=arcHeadIndex;
        this.arcTailIndex=arcTailIndex;
        this.arcWeight=arcWeight;
    }

    @Override
    public int hashCode() {
        return this.arcHeadIndex+this.arcTailIndex;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Arc)){
            return false;
        }
        Arc arc =(Arc)obj;
        if (arc.arcTailIndex==this.arcTailIndex && arc.arcHeadIndex==this.arcHeadIndex) {
            return true;
        }
        return false;
    }

    public ArcInfo getArcInfo() {
        return arcInfo;
    }

    public void setArcInfo(ArcInfo arcInfo) {
        this.arcInfo = arcInfo;
    }

    public double getArcWeight() {
        return arcWeight;
    }

    public void setArcWeight(double arcWeith) {
        this.arcWeight = arcWeith;
    }


    public int getArcHeadIndex() {
        return arcHeadIndex;
    }

    public void setArcHeadIndex(int arcHeadIndex) {
        this.arcHeadIndex = arcHeadIndex;
    }

    public Arc getNextArcOfArcHead() {
        return nextArcOfArcHead;
    }

    public void setNextArcOfArcHead(Arc nextArcOfArcHead) {
        this.nextArcOfArcHead = nextArcOfArcHead;
    }

    public int getArcTailIndex() {
        return arcTailIndex;
    }

    public void setArcTailIndex(int arcTailIndex) {
        this.arcTailIndex = arcTailIndex;
    }

    public Arc getNextArcOfArcTail() {
        return nextArcOfArcTail;
    }

    public void setNextArcOfArcTail(Arc nextArcOfArcTail) {
        this.nextArcOfArcTail = nextArcOfArcTail;
    }
}
