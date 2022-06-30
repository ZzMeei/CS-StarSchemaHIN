package test;

import baseline.*;
import util.*;

import java.util.*;

// onlineKExp : NaiveOnline (without Lemma 5 technique, which is as the same as we state in the paper)

public class KTestSlow {

    public static void main(String[] args) {
        onlineKExp();
    }

    public static void onlineKExp(){
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
        long validTimer = 0;
        long invalidTimer = 0;
        int countValid = 0;
        int countInvalid = 0;
        System.out.println("Start to verify query with k = 6");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQuerySlow basicQuerySlow = new BasicQuerySlow(graph, vertexType, edgeType, queryIdSetListK.get(i), 6, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuerySlow.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        long t2 = System.nanoTime();
        System.out.println("The average time cost of query : " + (t2-t1)/queryIdSetListK.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }

        t1 = System.nanoTime();
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with k = 8");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQuerySlow basicQuerySlow = new BasicQuerySlow(graph, vertexType, edgeType, queryIdSetListK.get(i), 8, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuerySlow.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        t2 = System.nanoTime();
        System.out.println("The average time cost of query : " + (t2-t1)/queryIdSetListK.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }

        t1 = System.nanoTime();
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with k = 10");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQuerySlow basicQuerySlow = new BasicQuerySlow(graph, vertexType, edgeType, queryIdSetListK.get(i), 10, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuerySlow.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        t2 = System.nanoTime();
        System.out.println("The average time cost of query : " + (t2-t1)/queryIdSetListK.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }

        t1 = System.nanoTime();
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with k = 12");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQuerySlow basicQuerySlow = new BasicQuerySlow(graph, vertexType, edgeType, queryIdSetListK.get(i), 12, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuerySlow.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        t2 = System.nanoTime();
        System.out.println("The average time cost of query : " + (t2-t1)/queryIdSetListK.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }

        t1 = System.nanoTime();
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with k = 14");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQuerySlow basicQuerySlow = new BasicQuerySlow(graph, vertexType, edgeType, queryIdSetListK.get(i), 14, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuerySlow.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        t2 = System.nanoTime();
        System.out.println("The average time cost of query : " + (t2-t1)/queryIdSetListK.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }
    }
}
