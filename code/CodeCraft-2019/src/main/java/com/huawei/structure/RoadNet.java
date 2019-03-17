package com.huawei.structure;

import com.huawei.FileUtils;


import java.util.*;

public class RoadNet {
    private XSGraph graph=new XSGraph();

    public  class Road{
        /**
         * 道路id
         */
        private int roadId;

        /**
         *车道数目
         */
        private int countOfLane;

        /**
         * 道路长度
         */
        private int length;

        /**
         * 起始点id
         */
        private int startCrossId;

        /**
         * 重点id
         */
        private int endCrossId;

        /**
         * 道路的详细信息,顺向
         */
        private int[][] data;

        /**
         * 道路的详细信息，逆向
         */
        private int[][] reverseData;

        /**
         * 是否逆向
         */
        private boolean isTwoWay;

        /**
         * 最高限速
         */
        private int maxSpeed;

        public Road(int id,int lanNum,int length,int startId,int endId,int maxSpeed,boolean isDup){
            this.roadId = id;
            this.countOfLane = lanNum;
            this.length = length;
            this.data = new int[lanNum][length];
            this.startCrossId = startId;
            this.endCrossId = endId;
            this.maxSpeed = maxSpeed;
            this.isTwoWay = isDup;
            if (isDup)
                this.reverseData = new int[lanNum][length];
        }

        public int getLength() {
            return length;
        }

        public int getMaxSpeed() {
            return maxSpeed;
        }


    }

    /**
     * 定义路口数据结构
     */
    public  class Cross{
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
    }
    private ArrayList<Road>roadList=new ArrayList<>();
    private ArrayList<Cross>crossList=new ArrayList<>();
    private HashMap<Integer,XSGraph.Arc> mapOfArc=new HashMap<>();
    private HashMap<Integer,Road> mapOfRoad=new HashMap<>();
    public RoadNet(String roadsFilepath, String crossesFilepath){
        this.getRoadsAndCrossesFromFile(roadsFilepath,crossesFilepath);
        HashSet<XSGraph.Vertex> vertexs=new HashSet<>();
        HashSet<XSGraph.Arc> arcs=new HashSet<>();


        for (int i=0;i<crossList.size();i++) {
            Cross cross=crossList.get(i);
            XSGraph.Vertex<Road> vertex=graph.new Vertex(cross,cross.crossId);
            vertexs.add(vertex);
        }
        graph.initVertexs(vertexs);

        for (int i=0;i<roadList.size();i++) {
            Road road=roadList.get(i);
            if (road.isTwoWay) {
                XSGraph.Arc<Road> arc = graph.new Arc(road, road.endCrossId, road.startCrossId);
                arcs.add(arc);
                mapOfArc.put(2*road.roadId+1,arc);
            }
            XSGraph.Arc<Road> arc=graph.new Arc(road,road.startCrossId,road.endCrossId);
            arcs.add(arc);
            mapOfArc.put(2*road.roadId,arc);
        }
        graph.initArcs(arcs);
    }
    private void getRoadsAndCrossesFromFile(String roadsFilepath,String crossesFilepath){
        List<String> rows = FileUtils.readFileIntoList(roadsFilepath);
        for (int i=0;i<rows.size();i++) {
            String row=rows.get(i);
            if (row.startsWith("#"))
                continue;
            row=row.substring(1,row.length()-1);
            String[]items= row.split(",\\s*");
            Road road=new Road(Integer.parseInt(items[0]),
                    Integer.parseInt(items[3]),
                    Integer.parseInt(items[1]),
                    Integer.parseInt(items[4]),
                    Integer.parseInt(items[5]),
                    Integer.parseInt(items[2]),
                    items[6].equals("1")?true:false);
            roadList.add(road);
            //mapOfRoad.put(road.roadId,road);

        }
        rows = FileUtils.readFileIntoList(crossesFilepath);
        for (int i=0 ,j=0;i<rows.size();i++) {
            String row=rows.get(i);
            if (row.startsWith("#")) {
                continue;
            }
            row=row.substring(1,row.length()-1);
            String[]items= row.split(",\\s*");
            Cross cross=new Cross(Integer.parseInt(items[0]),
                    Integer.parseInt(items[1]),
                    Integer.parseInt(items[2]),
                    Integer.parseInt(items[3]),
                    Integer.parseInt(items[4]));
            crossList.add(cross);
        }
    }

    private static RoadNet SingeleInstance=null;


    public static RoadNet getInstance(String roadFile,String crossFile){
        if (SingeleInstance==null)
            SingeleInstance=new RoadNet(roadFile,crossFile);
        return SingeleInstance;

    }

    //private limitedMaxSpeed carsManager=CarsManager.getInstance();
    private void  updataAllArcWeight(int limitedMaxSpeed){
        Iterator<Integer> it = mapOfArc.keySet().iterator();
        while (it.hasNext()){
            Integer key = it.next();
            XSGraph.Arc arc=mapOfArc.get(key);
            Road road=((Road)arc.getArcInfo());
            double weight= road.length*1.0/Integer.min(road.maxSpeed,limitedMaxSpeed);
            arc.setArcWeight(weight);
        }
    }

    private void  updataStatusOfNew(int level){
        updataAllArcWeight(level);
        graph.creatVertsToVertsMinpath(level);
    }

    public void createCrossesToCrosses(int speedLevels[]){
        for (int i:speedLevels
             ) {
            updataStatusOfNew(i);
        }
    }

    public ArrayList<Integer> getPathByCrossId(int level,int oId,int dId){
        ArrayList<Integer> result=new ArrayList<>();
        ArrayList<Integer> temp=graph.getPathById(level,oId,dId);
        if (temp==null)
            return null;
        for(int count=0;count<temp.size()-1;count++){
            int startCrossId=temp.get(count);
            int endCrossId=temp.get(count+1);
            Road road=(Road)graph.getArcBySearchVerId(startCrossId,endCrossId).getArcInfo();
            int roadId= (road).roadId;
            result.add(roadId);
        }
        return result;
    }

    public double getPathWeightByCrossId(int level,int oId,int dId){
        double result;
        result=graph.getPathWeightById(level,oId,dId);
        return result;
    }
}
