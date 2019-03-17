package com.huawei.structure;

import java.util.*;

public class XSGraph {

    private double pathWeightMatrix[][];
    private int pathMatrix[][];
    private HashMap<Integer,int[][]> allPathMatix=new HashMap<>();
    private HashMap<Integer,double[][]> allpathWeightMatrix=new HashMap<>();
    private int  countOfVertex;
    public int[][] getPathMatrix() {
        int result[][]=new int[countOfVertex][countOfVertex];
        for (int i=0;i< countOfVertex;i++){
            for (int j=0;j< countOfVertex;j++){
                if(pathMatrix[i][j]<0)
                    result[i][j]=0;
                else
                    result[i][j]=vertexList.get(pathMatrix[i][j]).getVertexId();
            }
        }
        return result;
    }


    private HashMap<Integer,Integer>mapOfVerIndex=new HashMap<>();
    //private HashMap<Vertex,Integer>mapOf=new HashMap<>();

    public class Vertex<VerInfo> {
        private VerInfo vertexInfo;
        private int vertexId;
        private Arc firstNodeOfArcHead=null;
        private Arc firstNodeOfArcTail=null;

        public Vertex(VerInfo verInfo,int vertexId){
            this.vertexInfo=verInfo;
            this.vertexId=vertexId;
        }

        @Override
        public int hashCode() {
            return this.vertexId;
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Vertex)){
                return false;
            }
            Vertex v =(Vertex)obj;
            return v.vertexId==this.vertexId;
        }

        public VerInfo getVertexInfo() {
            return vertexInfo;
        }

        public void setVertexInfo(VerInfo vertexInfo) {
            this.vertexInfo = vertexInfo;
        }

        public int getVertexId() {
            return vertexId;
        }
    }

    public class Arc<ArcInfo>{
        private ArcInfo arcInfo;
        private int arcHeadIndex;
        private Arc nextArcOfArcHead;
        private int arcTailIndex;
        private Arc nextArcOfArcTail;
        private double arcWeight;
        public Arc(ArcInfo arcInfo,int arcTailId,int arcHeadId){
            this.arcInfo=arcInfo;
            this.arcHeadIndex=mapOfVerIndex.get(arcTailId);
            this.arcTailIndex=mapOfVerIndex.get(arcHeadId);
            //sthis.arcWeith=arcWeith;
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
    }


    private ArrayList<Vertex> vertexList=new ArrayList();
    private ArrayList<Arc> arcList=new ArrayList();

    public void initVertexs(HashSet<Vertex>vertexs){
        Iterator<Vertex> it = vertexs.iterator();
        while (it.hasNext()){
            Vertex ver = it.next();
            vertexList.add(ver);
        }
        for (int i=0;i<vertexList.size();i++){
            Vertex ver = vertexList.get(i);
            mapOfVerIndex.put(ver.vertexId,i);
        }
        countOfVertex=vertexList.size();

    }

    public void initArcs(HashSet<Arc>arcs){
        Iterator<Arc> it1 = arcs.iterator();
        while (it1.hasNext()) {
            Arc arc = it1.next();

            Vertex arcHead=vertexList.get(arc.arcHeadIndex);
            Vertex arcTail=vertexList.get(arc.arcTailIndex);

            arc.nextArcOfArcHead =arcHead.firstNodeOfArcHead;
            arcHead.firstNodeOfArcHead=arc;

            arc.nextArcOfArcTail =arcTail.firstNodeOfArcTail;
            arcTail.firstNodeOfArcTail=arc;

            arcList.add(arc);
        }
    }
    private Arc getArcBySearchAdjHead(int tailIndex,int adjHeadIndex){
        Arc pArc= vertexList.get(tailIndex).firstNodeOfArcTail;
        while(pArc!=null) {
            if (pArc.arcHeadIndex==adjHeadIndex)
                return pArc;
            pArc = pArc.nextArcOfArcTail;
        }
        return null;

    }
    public Arc getArcBySearchVerId(int tailID,int adjHeadId){
        Arc pArc= getArcBySearchAdjHead(mapOfVerIndex.get(tailID),mapOfVerIndex.get(adjHeadId));
        return pArc;

    }
    private int firstAdjVex(int v){
        Arc arc= vertexList.get(v).firstNodeOfArcTail;
        if (arc==null)
            return -1;
        else
            return arc.arcHeadIndex;
    }
    private int nextAdjVex(int tail,int pre){
        Arc pArc= vertexList.get(tail).firstNodeOfArcTail;
       while(pArc!=null) {
           if (pArc.arcHeadIndex==pre)
               return pArc.nextArcOfArcTail.arcHeadIndex;
           pArc = pArc.nextArcOfArcTail;
       }
       return -1;

    }
    private void visitedFun(int verIndex){
        Vertex ver = vertexList.get(verIndex);
    }
    private void DFS(int index,byte[] visited){
        visited[index]=1;
        visitedFun(index);
        for (int w=firstAdjVex(index);w>=0;w=nextAdjVex(index,w)){
            if (visited[w]==0)
                DFS(w,visited);
        }
    }
    private void DFSTraverse(){
        byte visited[]=new byte[vertexList.size()];
        for (int i=0;i<vertexList.size();i++) {
            visited[i]=0;
        }
        for (int i=0;i<vertexList.size();i++) {
            if (visited[i]==0) {
                //Vertex ver = vertexList.get(i);
                DFS(i,visited);
            }
        }
    }

    private void Dijkstra(int originalIndex ,double D[],int path[]){
        Arc  pArc=vertexList.get(originalIndex).firstNodeOfArcTail;
        int countOfVer=vertexList.size();
        byte finish[]=new byte[vertexList.size()];
        for (int i=0;i<countOfVer;i++) {
            D[i]=Double.MAX_VALUE;
            path[i]=Integer.MAX_VALUE;
            finish[i]=0;
        }

        D[originalIndex]=0;
        path[originalIndex]=-1;

        for (int i=0;i<countOfVer;i++){
            Double min=Double.MAX_VALUE;
            int minIndex=0;
            for (int j=0;j<countOfVer;j++){
                if (finish[j]==1){
                    continue;
                }
                if (min>D[j]) {
                    min = D[j];
                    minIndex=j;
                }
            }

            if(min==Double.MAX_VALUE) {
                break;
            }
            D[minIndex]=min;

            finish[minIndex]=1;
            pArc =vertexList.get(minIndex).firstNodeOfArcTail;
            while (pArc!=null){
                if (finish[pArc.arcHeadIndex]==1) {
                    pArc=pArc.nextArcOfArcTail;
                    continue;
                }
                if (D[pArc.arcHeadIndex]>D[minIndex]+pArc.getArcWeight()){
                    D[pArc.arcHeadIndex]=D[minIndex]+pArc.getArcWeight();
                    path[pArc.arcHeadIndex]=minIndex;
                }
                pArc=pArc.nextArcOfArcTail;
            }

        }
    }

    public void creatVertsToVertsMinpath(int level){
        int countOfVers=vertexList.size();
        int pathMatrix[][];
        double pathWeightMatrix[][];
        if (allPathMatix.get(level)!=null)
            return;
        pathMatrix=new int[countOfVers][countOfVers];
        pathWeightMatrix=new double[countOfVers][countOfVers];
        for (int i=0;i<countOfVers;i++){
            Dijkstra(i,pathWeightMatrix[i],pathMatrix[i]);
        }
        allPathMatix.put(level,pathMatrix);
        allpathWeightMatrix.put(level,pathWeightMatrix);
    }

    private ArrayList<Integer> getPathByIndex(int level,int oIndex,int dIndex){
        ArrayList<Integer> result=new ArrayList<>();
        if (oIndex<0 || oIndex>vertexList.size() || dIndex<0 || dIndex>vertexList.size())
            return null;
        int pMatrix[][]=allPathMatix.get(level);
        if (pMatrix==null)
            return null;
        while (pMatrix[oIndex][dIndex]!=-1){
            if (pMatrix[oIndex][dIndex]==Integer.MAX_VALUE)
                return null;
            result.add(dIndex);
            dIndex=pMatrix[oIndex][dIndex];
        }
        result.add(oIndex);

        return result;
    }

    public ArrayList<Integer> getPathById(int level,int oId,int dId){
        ArrayList<Integer> result=new ArrayList<>();
        ArrayList<Integer> temp=getPathByIndex(level,mapOfVerIndex.get(oId),mapOfVerIndex.get(dId));
        if (temp==null)
            return null;
        int count=temp.size()-1;
        while(count>=0){
            result.add(vertexList.get(temp.get(count--)).vertexId);
        }
        return result;
    }

    private double getPathWeightByIndex(int level,int oIndex,int dIndex){
        double result;
        double pwMatrix[][]=allpathWeightMatrix.get(level);
        result=pwMatrix[oIndex][dIndex];
        return result;
    }
    public double getPathWeightById(int level,int oId,int dId){
        double result;
        result=getPathWeightByIndex(level,mapOfVerIndex.get(oId),mapOfVerIndex.get(dId));
        return result;
    }
}
