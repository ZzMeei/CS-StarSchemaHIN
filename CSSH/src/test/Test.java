package test;

import baseline.BasicQueryFast;
import bean.MetaPath;
import csh.*;
import index.*;
import index.KC.BuildIndexKC;
import index.MC.BuildIndexMC;
import index.MKC.BuildIndexMKC;
import tool.MetaPathGenerater;
import util.*;

import java.io.IOException;
import java.util.*;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        indexExp();
//        tempTest();
        fastTest();
    }

    public static HashMap<String, IndexNode> buildIndexTree(String indexTreeChoice, int l, int targetType){
        Config config = new Config();
        int[][] schemaGraph = config.getSchema("imdb");
//        int[][] schemaGraph = config.getSchema("dblp");
//        int[][] schemaGraph = config.getSchema("foursquare");
//        int[][] schemaGraph = config.getSchema("pubMed");

        DataReader dataReader = new DataReader(Config.newIMDBGraph, Config.newIMDBVertex, Config.newIMDBEdge);
//        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);
//        DataReader dataReader = new DataReader(Config.newFsqGraph, Config.newFsqVertex, Config.newFsqEdge);
//        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        if (indexTreeChoice.equals("MKC")){
            BuildIndexMKC buildIndexMKC = new BuildIndexMKC(graph, vertexType, edgeType, schemaGraph);
            return buildIndexMKC.buildIndex(l, targetType);
        }
        if (indexTreeChoice.equals("MC")){
            BuildIndexMC buildIndexMC = new BuildIndexMC(graph, vertexType, edgeType, schemaGraph);
            return buildIndexMC.buildIndex(l, targetType);
        }
        if (indexTreeChoice.equals("KC")){
            BuildIndexKC buildIndexKC = new BuildIndexKC(graph, vertexType, edgeType, schemaGraph);
            return buildIndexKC.buildIndex(l, targetType);
        }
        return  null;
    }

    public static void indexExp() {
        Map<Integer, HashMap<String, IndexNode>> indexTreeMap = new HashMap<Integer, HashMap<String, IndexNode>>();
        int countMPath = 0;
        for (int i = 0; i < 4; i++){
            HashMap<String, IndexNode> indexTree = buildIndexTree("MKC", 4, i);
            indexTreeMap.put(i, indexTree);
            countMPath += indexTree.keySet().size() -1 ;
        }
        System.out.println("The number of meta-paths : " + countMPath);
        System.out.println("The index tree has completed!");
        Config config = new Config();
        int[][] schemaGraph = config.getSchema("imdb");
//        int[][] schemaGraph = config.getSchema("dblp");
//        int[][] schemaGraph = config.getSchema("foursquare");
//        int[][] schemaGraph = config.getSchema("pubMed");

        DataReader dataReader = new DataReader(Config.newIMDBGraph, Config.newIMDBVertex, Config.newIMDBEdge);
//        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);
//        DataReader dataReader = new DataReader(Config.newFsqGraph, Config.newFsqVertex, Config.newFsqEdge);
//        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        ReadQuery readQuery = new ReadQuery();
        ArrayList<ArrayList<Integer>> queryIdSetListK = readQuery.readFile("./query_data/KQuery/IMDB_Query_K.txt");

        long t1 = System.nanoTime();
        for (int i = 0; i < 10; i++){
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetListK.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)], 6, queryIdSetListK.get(i));
            if (result != null) {
                if (result.size() > 0) {
                    for (int j = 0; j < result.size(); j++) {
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                }
            }
        }
        long t2 = System.nanoTime();
        System.out.println("The total time of query : " + (t2 - t1) / 1000000000);
    }

    public static void tempTest(){
        Config config = new Config();
        int[][] schemaGraph = config.getSchema("pubMed");
        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        int queryId = 0;
        for (int i = 0; i < graph.length; i++){
            if (vertexType[i] == 1){
                queryId = i;
                break;
            }
        }

        MetaPathGenerater metaPathGenerater = new MetaPathGenerater(schemaGraph);
        List<MetaPath> metaPathList = metaPathGenerater.generateHalfMetaPath(4, 1);
        MetaPath queryMetaPath = metaPathList.get(2);

        FastBCoreTest fastBCoreTest = new FastBCoreTest(graph, vertexType, edgeType);
        Set<Integer> community = fastBCoreTest.query(queryId, queryMetaPath, 25);

        System.out.println(community.size());
    }

    public static void fastTest(){
        Config config = new Config();
//        int[][] schemaGraph = config.getSchema("imdb");
//        int[][] schemaGraph = config.getSchema("dblp");
        int[][] schemaGraph = config.getSchema("foursquare");
//        int[][] schemaGraph = config.getSchema("pubMed");

//        DataReader dataReader = new DataReader(Config.newIMDBGraph, Config.newIMDBVertex, Config.newIMDBEdge);
//        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);
        DataReader dataReader = new DataReader(Config.newFsqGraph, Config.newFsqVertex, Config.newFsqEdge);
//        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        ArrayList<Integer> queryIdSet = new ArrayList<Integer>();

        queryIdSet.add(1137899);
//        queryIdSet.add(691170);
        queryIdSet.add(3248845);

        System.out.println("The type of the vertex : " + vertexType[queryIdSet.get(0)]);

        BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSet, 6, schemaGraph);
        ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSet.get(0)]);

        System.out.println(result.size());
        System.out.println(result.get(0));
    }
}
