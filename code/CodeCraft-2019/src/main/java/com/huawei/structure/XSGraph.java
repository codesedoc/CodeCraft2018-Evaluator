package com.huawei.structure;


import java.util.*;

public class XSGraph<VerInfo,ArcInfo>{

    private HashMap<Integer,int[][]> allPathMatix=new HashMap<>();
    private HashMap<Integer,double[][]> allpathWeightMatrix=new HashMap<>();
    private HashMap<Vertex,Integer>mapOfVerIndex=new HashMap<>();
    private HashMap<Object,Vertex>mapOfVer=new HashMap<>();
    private HashMap<Object,Arc>mapOfArc=new HashMap<>();
    private HashMap<Object,Integer>mapOfVerInfoToIndex=new HashMap<>();
    private int  countOfVertex;

    private ArrayList<Vertex<VerInfo>> vertexList=new ArrayList<>();

    private void addVertex(VerInfo verInfo){
        Vertex<VerInfo> ver=new Vertex(verInfo);
        mapOfVer.put(verInfo,ver);
        mapOfVerIndex.put(ver,countOfVertex);
        mapOfVerInfoToIndex.put(verInfo,countOfVertex);
        vertexList.add(ver);
        countOfVertex=vertexList.size();
    }
    private void addArc(ArcInfo arcInfo,Vertex arcTail,Vertex arcHead,double arcWeight){
        int arcHeadIndex=mapOfVerIndex.get(arcHead);
        int arcTailIndex=mapOfVerIndex.get(arcTail);
        Arc<ArcInfo> arc=new Arc(arcInfo,arcTailIndex,arcHeadIndex,arcWeight);

        arc.setNextArcOfArcHead(arcHead.getFirstNodeOfArcHead());
        arcHead.setFirstNodeOfArcHead(arc);

        arc.setNextArcOfArcTail(arcTail.getFirstNodeOfArcTail());
        arcTail.setFirstNodeOfArcTail(arc);
        mapOfArc.put(arcInfo,arc);
    }
    public void addOneArcAndTwoVer(ArcInfo arcInfo,VerInfo tailInfo,VerInfo headInfo,double arcWeight){
        Vertex verTail;
        Vertex verHead;
        if (mapOfVer.get(tailInfo)==null) {
            addVertex(tailInfo);
        }
        if (mapOfVer.get(headInfo)==null) {
            addVertex(headInfo);
        }
        verTail=mapOfVer.get(tailInfo);
        verHead=mapOfVer.get(headInfo);
        addArc(arcInfo,verTail,verHead,arcWeight);
    }

    public void changeWeightOfArc(ArcInfo arcInfo,double weight){
        Arc arc=mapOfArc.get(arcInfo);
        if (arc!=null){
            arc.setArcWeight(weight);
        }
    }

    private void updateAllWeightOfArcs(HashMap<ArcInfo,Double> allWeight){
        Iterator<ArcInfo> it=allWeight.keySet().iterator();
        while (it.hasNext()){
            ArcInfo arcInfo=it.next();
            changeWeightOfArc(arcInfo,allWeight.get(arcInfo));
        }
    }

    public void  addNetWeight(int level, HashMap<ArcInfo,Double> allWeight){
        if (allPathMatix.get(level)==null){
            updateAllWeightOfArcs(allWeight);
            addPathMatrix(level);
        }

    }
    public void  addNetWeight(int level){
        if (allPathMatix.get(level)==null){
            addPathMatrix(level);
        }

    }

    private Arc<ArcInfo> getArcBySearchAdjHeadIndex(int tailIndex,int adjHeadIndex){
        Arc pArc= vertexList.get(tailIndex).getFirstNodeOfArcTail();
        while(pArc!=null) {
            if (pArc.getArcHeadIndex()==adjHeadIndex)
                return pArc;
            pArc = pArc.getNextArcOfArcTail();
        }
        return null;

    }
    private int firstAdjVex(int v){
        Arc arc= vertexList.get(v).getFirstNodeOfArcTail();
        if (arc==null)
            return -1;
        else
            return arc.getArcHeadIndex();
    }
    private int nextAdjVex(int tail,int pre){
        Arc pArc= vertexList.get(tail).getFirstNodeOfArcTail();
       while(pArc!=null) {
           if (pArc.getArcHeadIndex()==pre)
               return pArc.getNextArcOfArcTail().getArcHeadIndex();
           pArc = pArc.getNextArcOfArcTail();
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
        Arc  pArc;
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
            pArc =vertexList.get(minIndex).getFirstNodeOfArcTail();
            while (pArc!=null){
                if (finish[pArc.getArcHeadIndex()]==1) {
                    pArc=pArc.getNextArcOfArcTail();
                    continue;
                }
                if (D[pArc.getArcHeadIndex()]>D[minIndex]+pArc.getArcWeight()){
                    D[pArc.getArcHeadIndex()]=D[minIndex]+pArc.getArcWeight();
                    path[pArc.getArcHeadIndex()]=minIndex;
                }
                pArc=pArc.getNextArcOfArcTail();
            }

        }
    }

    private void addPathMatrix(int level){
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

    private ArrayList<Vertex<VerInfo>> getPathByIndex(int level,int oIndex,int dIndex){
        ArrayList<Vertex<VerInfo>> result=new ArrayList<>();
        if (oIndex<0 || oIndex>vertexList.size() || dIndex<0 || dIndex>vertexList.size())
            return null;
        int pMatrix[][]=allPathMatix.get(level);
        if (pMatrix==null)
            return null;
        while (pMatrix[oIndex][dIndex]!=-1){
            if (pMatrix[oIndex][dIndex]==Integer.MAX_VALUE)
                return null;
            result.add(vertexList.get(dIndex));
            dIndex=pMatrix[oIndex][dIndex];
        }
        result.add(vertexList.get(oIndex));

        return result;
    }

    public ArrayList<VerInfo> getVerPath(int level,VerInfo oId,VerInfo dId){
        ArrayList<VerInfo> result=new ArrayList<>();
        ArrayList<Vertex<VerInfo>> temp=getPathByIndex(level,mapOfVerInfoToIndex.get(oId),mapOfVerInfoToIndex.get(dId));
        if (temp==null)
            return null;
        int count=temp.size()-1;
        while(count>=0){
            result.add(temp.get(count--).getVertexInfo());
        }
        return result;
    }

    public ArrayList<ArcInfo> getArcPath(int level,VerInfo oId,VerInfo dId){
        ArrayList<ArcInfo> result=new ArrayList<>();

        ArrayList<VerInfo> temp=getVerPath(level,oId,dId);
        for(int count=0;count<temp.size()-1;count++){
            int arcTailIndex=mapOfVerInfoToIndex.get(temp.get(count));
            int arcHeadIndex=mapOfVerInfoToIndex.get(temp.get(count+1));
            ArcInfo arc=getArcBySearchAdjHeadIndex(arcTailIndex,arcHeadIndex).getArcInfo();
            result.add(arc);
        }
        return result;
    }

    private double getPathWeightByIndex(int level,int oIndex,int dIndex){
        double result;
        double pwMatrix[][]=allpathWeightMatrix.get(level);
        result=pwMatrix[oIndex][dIndex];
        return result;
    }
    public double getPathWeight(int level,VerInfo oId,VerInfo dId){
        double result;
        result=getPathWeightByIndex(level,mapOfVerInfoToIndex.get(oId),mapOfVerInfoToIndex.get(dId));
        return result;
    }
}
