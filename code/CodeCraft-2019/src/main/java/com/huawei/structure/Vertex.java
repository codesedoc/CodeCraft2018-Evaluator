package com.huawei.structure;

public class Vertex<VerInfo> {
    private VerInfo vertexInfo;
    private Arc firstNodeOfArcHead=null;
    private Arc firstNodeOfArcTail=null;

    public Vertex(VerInfo verInfo){
        this.vertexInfo=verInfo;
    }

    public VerInfo getVertexInfo() {
        return vertexInfo;
    }

    public void setVertexInfo(VerInfo vertexInfo) {
        this.vertexInfo = vertexInfo;
    }

    public Arc getFirstNodeOfArcHead() {
        return firstNodeOfArcHead;
    }

    public void setFirstNodeOfArcHead(Arc firstNodeOfArcHead) {
        this.firstNodeOfArcHead = firstNodeOfArcHead;
    }

    public Arc getFirstNodeOfArcTail() {
        return firstNodeOfArcTail;
    }

    public void setFirstNodeOfArcTail(Arc firstNodeOfArcTail) {
        this.firstNodeOfArcTail = firstNodeOfArcTail;
    }
}
