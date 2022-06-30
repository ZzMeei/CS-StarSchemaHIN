package test;

import baseline.BasicQuery;
import baseline.BasicQueryFast;
import csh.CoreDecomposition;
import index.*;
import bean.*;
import index.KC.BuildIndexKC;
import index.MC.BuildIndexMC;
import index.MKC.BuildIndexMKC;
import tool.*;
import util.*;

import java.io.*;
import java.util.*;

// indexKExp : IndexQuery
// onlineKExp : NaiveOnline (with Lemma 5 technique, which is different with we state in the paper)
// onlineExpFast : FastOnline
// geneQueriesK : generate queries for experiments

public class KTest {
    public static void main(String[] args) {
        indexKExp();
//        onlineKExp();
//        onlineKExpFast();
//        geneQueriesK();
    }

    public static HashMap<String, IndexNode> buildIndexTree(String indexTreeChoice, int l, int targetType){
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

    public static void writeFile(String filePath, ArrayList<ArrayList<Integer>> data){
        try {
            Writer writer = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (int i = 0; i < data.size(); i++){
                for (int j = 0; j < data.get(i).size()-1; j++){
                    bufferedWriter.write(Integer.toString(data.get(i).get(j)));
                    bufferedWriter.write(" ");
                }
                bufferedWriter.write(Integer.toString(data.get(i).get(data.get(i).size()-1)));
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void geneQueriesK(){
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

        MetaPathGenerater metaPathGenerater = new MetaPathGenerater(schemaGraph);
        ArrayList<MetaPath> metaPathList = new ArrayList<MetaPath>();
        for (int i = 0; i < 4; i++){
            ArrayList<MetaPath> tempMPathList = metaPathGenerater.generateHalfMetaPath(4, i);
            for (int j = 0; j < tempMPathList.size(); j++){
                metaPathList.add(tempMPathList.get(j));
            }
        }
        CoreDecomposition coreDecomposition = new CoreDecomposition(graph, vertexType, edgeType);
        Map<String, ArrayList<Integer>> vertexSetMap = new HashMap<String, ArrayList<Integer>>();
        for (int i = 0; i < metaPathList.size(); i++){
            Map<Integer, Integer> reverseOrderArr = coreDecomposition.decompose(metaPathList.get(i));
            ArrayList<Integer> vertexSet = new ArrayList<Integer>();
            for (int vertexId : reverseOrderArr.keySet()){
                if (reverseOrderArr.get(vertexId) >= 6){
                    vertexSet.add(vertexId);
                }
            }
            if (vertexSet.size() != 0){
                vertexSetMap.put(metaPathList.get(i).toString(), vertexSet);
            }
        }

        System.out.println("The number of valid meta paths : " + vertexSetMap.keySet().size());
        for (String metaPathS : vertexSetMap.keySet()){
            System.out.println(metaPathS);
        }

        ArrayList<ArrayList<Integer>> queryIdSetListK = new ArrayList<ArrayList<Integer>>();

        ArrayList<MetaPath> newMetaPathList = new ArrayList<MetaPath>();
        for (int i = 0; i < metaPathList.size(); i++){
            if (vertexSetMap.keySet().contains(metaPathList.get(i).toString())){
                newMetaPathList.add(metaPathList.get(i));
            }
        }

        for (int i = 0; i < 200; i++){
            Collections.shuffle(newMetaPathList);
            MetaPath curMetaPath = newMetaPathList.get(0);
//            System.out.println(curMetaPath.toString());
            ArrayList<Integer> vertexSet = vertexSetMap.get(curMetaPath.toString());
            Collections.shuffle(vertexSet);
            ArrayList<Integer> queryIdSet = new ArrayList<Integer>();
            for (int j = 0; j < 2; j++){
                queryIdSet.add(vertexSet.get(j));
            }
            queryIdSetListK.add(queryIdSet);
        }

        writeFile("./query_data/KQuery/FourSquare_Query_K.txt", queryIdSetListK);
    }

    public static void indexKExp() {
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

        ReadQuery readQuery = new ReadQuery();
        ArrayList<ArrayList<Integer>> queryIdSetListK = readQuery.readFile("./query_data/KQuery/DBLP_Query_K_test.txt");

        long t1 = System.nanoTime();
        long validTimer = 0;
        long invalidTimer = 0;
        int countValid = 0;
        int countInvalid = 0;
        System.out.println("Start to verify query with k = 6");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetListK.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)], 6, queryIdSetListK.get(i));
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
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetListK.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)], 8, queryIdSetListK.get(i));
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
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetListK.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)], 10, queryIdSetListK.get(i));
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
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetListK.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)], 12, queryIdSetListK.get(i));
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
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetListK.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)], 14, queryIdSetListK.get(i));
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

    public static void onlineKExp(){
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
        ArrayList<ArrayList<Integer>> queryIdSetListK = readQuery.readFile("./query_data/KQuery/FourSquare_Query_K.txt");

        long t1 = System.nanoTime();
        long validTimer = 0;
        long invalidTimer = 0;
        int countValid = 0;
        int countInvalid = 0;
        System.out.println("Start to verify query with k = 6");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQuery basicQuery = new BasicQuery(graph, vertexType, edgeType, queryIdSetListK.get(i), 6, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQuery basicQuery = new BasicQuery(graph, vertexType, edgeType, queryIdSetListK.get(i), 8, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQuery basicQuery = new BasicQuery(graph, vertexType, edgeType, queryIdSetListK.get(i), 10, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQuery basicQuery = new BasicQuery(graph, vertexType, edgeType, queryIdSetListK.get(i), 12, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQuery basicQuery = new BasicQuery(graph, vertexType, edgeType, queryIdSetListK.get(i), 14, schemaGraph);
            ArrayList<Set<Integer>> result = basicQuery.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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

    public static void onlineKExpFast(){
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
        ArrayList<ArrayList<Integer>> queryIdSetListK = readQuery.readFile("./query_data/KQuery/PubMed_Query_K.txt");

        long t1 = System.nanoTime();
        long validTimer = 0;
        long invalidTimer = 0;
        int countValid = 0;
        int countInvalid = 0;
        System.out.println("Start to verify query with k = 6");
        for (int i = 0; i < queryIdSetListK.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetListK.get(i));
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSetListK.get(i), 6, schemaGraph);
            ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSetListK.get(i), 8, schemaGraph);
            ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSetListK.get(i), 10, schemaGraph);
            ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSetListK.get(i), 12, schemaGraph);
            ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSetListK.get(i), 14, schemaGraph);
            ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSetListK.get(i).get(0)]);
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


