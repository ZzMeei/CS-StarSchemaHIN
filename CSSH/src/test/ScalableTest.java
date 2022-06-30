package test;

import baseline.BasicQuery;
import baseline.BasicQueryFast;
import index.IndexNode;
import index.KC.BuildIndexKC;
import index.MC.BuildIndexMC;
import index.MKC.BuildIndexMKC;
import util.*;

import java.util.*;

// onlineScalableExp : scalability test for NaiveOnline
// onlineScalableExpFast : scalability test for FastOnline
// indexScalableExp : for index construction time analysis

public class ScalableTest {
    public static void main(String[] args) {
//        onlineScalableExp();
//        onlineScalableExpFast();
        indexScalableExp();
    }

    public static void onlineScalableExp(){
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

        ReadQuery readQuery = new ReadQuery();

        HashSet<Integer> queryIdSet = new HashSet<Integer>();
        ArrayList<ArrayList<Integer>> queryIdSetList = readQuery.readFile("./query_data/KQuery/FourSquare_Query_K.txt");
        for (int i = 0; i < queryIdSetList.size(); i++){
            for (int j = 0; j < queryIdSetList.get(i).size(); j++){
                queryIdSet.add(queryIdSetList.get(i).get(j));
            }
        }
        SmallGraph sGraph = new SmallGraph(graph , vertexType , edgeType);

        for (int i = 1; i <= 5; i++){
            long t1 = System.nanoTime();
            long validTimer = 0;
            long invalidTimer = 0;
            long countValid = 0;
            long countInvalid = 0;
            System.out.println("Scalable : " + (float)i / 5);
            sGraph.getSmallGraph(i , 5 , queryIdSet);
            for (int j = 0; j < queryIdSetList.size(); j++){
                System.out.println(j + " -> " + "The query vertices : " + queryIdSetList.get(j));
                ArrayList<Integer> newQueryIdSet = new ArrayList<Integer>();
                for (int vertexId : queryIdSetList.get(j)){
                    newQueryIdSet.add(sGraph.newVidMap.get(vertexId));
                }
                long tt1 = System.nanoTime();
                BasicQuery basicQuery = new BasicQuery(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType, newQueryIdSet, 6, schemaGraph);
                ArrayList<Set<Integer>> result = basicQuery.query(4, sGraph.smallGraphVertexType[newQueryIdSet.get(0)]);

                if (result != null){
                    if (result.size() > 0){
                        countValid += 1;
                        validTimer += System.nanoTime() - tt1;
                        for (int t = 0; t < result.size(); t++){
                            System.out.println("The vertices number of community " + t + " : " + result.get(t).size());
                        }
                        continue;
                    }
                }
                countInvalid += 1;
                invalidTimer += System.nanoTime() - tt1;
            }
            long t2 = System.nanoTime();
            System.out.println("The average time cost of query : " + (t2-t1)/200);
            System.out.println("The average time cost of query (clean) : " + (validTimer + invalidTimer) / 200);
            System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
            if (countInvalid != 0){
                System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
            }
        }
    }

    public static void onlineScalableExpFast(){
        Config config = new Config();
//        int[][] schemaGraph = config.getSchema("imdb");
//        int[][] schemaGraph = config.getSchema("dblp");
//        int[][] schemaGraph = config.getSchema("foursquare");
        int[][] schemaGraph = config.getSchema("pubMed");

//        DataReader dataReader = new DataReader(Config.newIMDBGraph, Config.newIMDBVertex, Config.newIMDBEdge);
//        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);
//        DataReader dataReader = new DataReader(Config.newFsqGraph, Config.newFsqVertex, Config.newFsqEdge);
        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        ReadQuery readQuery = new ReadQuery();

        HashSet<Integer> queryIdSet = new HashSet<Integer>();
        ArrayList<ArrayList<Integer>> queryIdSetList = readQuery.readFile("./query_data/KQuery/PubMed_Query_K.txt");
        for (int i = 0; i < queryIdSetList.size(); i++){
            for (int j = 0; j < queryIdSetList.get(i).size(); j++){
                queryIdSet.add(queryIdSetList.get(i).get(j));
            }
        }
        SmallGraph sGraph = new SmallGraph(graph , vertexType , edgeType);

        for (int i = 1; i <= 5; i++){
            long t1 = System.nanoTime();
            long validTimer = 0;
            long invalidTimer = 0;
            long countValid = 0;
            long countInvalid = 0;
            System.out.println("Scalable : " + (float)i / 5);
            sGraph.getSmallGraph(i , 5 , queryIdSet);
            for (int j = 0; j < queryIdSetList.size(); j++){
                System.out.println(j + " -> " + "The query vertices : " + queryIdSetList.get(j));
                ArrayList<Integer> newQueryIdSet = new ArrayList<Integer>();
                for (int vertexId : queryIdSetList.get(j)){
                    newQueryIdSet.add(sGraph.newVidMap.get(vertexId));
                }
                long tt1 = System.nanoTime();
                BasicQueryFast basicQueryFast = new BasicQueryFast(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType, newQueryIdSet, 6, schemaGraph);
                ArrayList<Set<Integer>> result = basicQueryFast.query(4, sGraph.smallGraphVertexType[newQueryIdSet.get(0)]);

                if (result != null){
                    if (result.size() > 0){
                        countValid += 1;
                        validTimer += System.nanoTime() - tt1;
                        for (int t = 0; t < result.size(); t++){
                            System.out.println("The vertices number of community " + t + " : " + result.get(t).size());
                        }
                        continue;
                    }
                }
                countInvalid += 1;
                invalidTimer += System.nanoTime() - tt1;
            }
            long t2 = System.nanoTime();
            System.out.println("The average time cost of query : " + (t2-t1)/queryIdSetList.size());
            System.out.println("The average time cost of query (clean) : " + (validTimer + invalidTimer) / queryIdSetList.size());
            System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
            if (countInvalid != 0){
                System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
            }
        }
    }

    public static void indexScalableExp(){
        Config config = new Config();
//        int[][] schemaGraph = config.getSchema("imdb");
        int[][] schemaGraph = config.getSchema("dblp");
//        int[][] schemaGraph = config.getSchema("foursquare");
//        int[][] schemaGraph = config.getSchema("pubMed");

//        DataReader dataReader = new DataReader(Config.newIMDBGraph, Config.newIMDBVertex, Config.newIMDBEdge);
        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);
//        DataReader dataReader = new DataReader(Config.newFsqGraph, Config.newFsqVertex, Config.newFsqEdge);
//        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        SmallGraph sGraph = new SmallGraph(graph , vertexType , edgeType);

        for (int i = 1; i <= 4; i++){
            sGraph.getSmallGraph(i , 5 , new HashSet<Integer>());
            System.out.println("Scalable : " + (float)i / 5);
            long t1 = System.nanoTime();
            BuildIndexMKC buildIndexMKC = new BuildIndexMKC(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType,schemaGraph);
            HashMap<String, IndexNode> indexTree = buildIndexMKC.buildIndex(4, 1);
            long t2 = System.nanoTime();
            System.out.println("Build Index MKC cost time : " + (t2-t1));

            t1 = System.nanoTime();
            BuildIndexKC buildIndexKC = new BuildIndexKC(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType, schemaGraph);
            indexTree = buildIndexKC.buildIndex(4, 1);
            t2 = System.nanoTime();
            System.out.println("Build Index KC cost time : " + (t2-t1));

            t1 = System.nanoTime();
            BuildIndexMC buildIndexMC = new BuildIndexMC(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType, schemaGraph);
            indexTree = buildIndexMC.buildIndex(4, 1);
            t2 = System.nanoTime();
            System.out.println("Build Index MC cost time : " + (t2-t1));
        }
    }
}
