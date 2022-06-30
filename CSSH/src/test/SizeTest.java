package test;

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

// geneQueryiesSize : generate queries for experiments
// indexSizeExp : for Semantic richness and Relationships closeness

public class SizeTest {
    public static void main(String[] args) {
//        geneQueryiesSize();
        indexSizeExp();
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

    public static void geneQueryiesSize(){
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

        ArrayList<ArrayList<Integer>> queryIdSetListSize2 = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> queryIdSetListSize3 = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> queryIdSetListSize4 = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> queryIdSetListSize5 = new ArrayList<ArrayList<Integer>>();

        ArrayList<MetaPath> newMetaPathList = new ArrayList<MetaPath>();
        for (int i = 0; i < metaPathList.size(); i++){
            if (vertexSetMap.keySet().contains(metaPathList.get(i).toString())){
                newMetaPathList.add(metaPathList.get(i));
            }
        }

        for (int i = 0; i < 200; i++){
            Collections.shuffle(newMetaPathList);
            MetaPath curMetaPath = newMetaPathList.get(0);
            ArrayList<Integer> vertexSet = vertexSetMap.get(curMetaPath.toString());
            Collections.shuffle(vertexSet);
            ArrayList<Integer> queryIdSet2 = new ArrayList<>();
            ArrayList<Integer> queryIdSet3 = new ArrayList<>();
            ArrayList<Integer> queryIdSet4 = new ArrayList<>();
            ArrayList<Integer> queryIdSet5 = new ArrayList<>();
            for (int j = 0; j < 2; j++){
                queryIdSet2.add(vertexSet.get(j));
                queryIdSet3.add(vertexSet.get(j));
                queryIdSet4.add(vertexSet.get(j));
                queryIdSet5.add(vertexSet.get(j));
            }
            queryIdSet3.add(vertexSet.get(2));
            queryIdSet4.add(vertexSet.get(2));
            queryIdSet5.add(vertexSet.get(2));
            queryIdSet4.add(vertexSet.get(3));
            queryIdSet5.add(vertexSet.get(3));
            queryIdSet5.add(vertexSet.get(4));
            queryIdSetListSize2.add(queryIdSet2);
            queryIdSetListSize3.add(queryIdSet3);
            queryIdSetListSize4.add(queryIdSet4);
            queryIdSetListSize5.add(queryIdSet5);
        }

        writeFile("./query_data/SizeQuery/FourSquare_Query_Size_2_new.txt", queryIdSetListSize2);
        writeFile("./query_data/SizeQuery/FourSquare_Query_Size_3_new.txt", queryIdSetListSize3);
        writeFile("./query_data/SizeQuery/FourSquare_Query_Size_4_new.txt", queryIdSetListSize4);
        writeFile("./query_data/SizeQuery/FourSquare_Query_Size_5_new.txt", queryIdSetListSize5);
    }

    public static void indexSizeExp() {
        Map<Integer, HashMap<String, IndexNode>> indexTreeMap = new HashMap<Integer, HashMap<String, IndexNode>>();
        for (int i = 0; i < 4; i++){
            HashMap<String, IndexNode> indexTree = buildIndexTree("MKC", 4, i);
            indexTreeMap.put(i, indexTree);
        }
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

        ArrayList<ArrayList<Integer>> queryIdSetList = null;

        queryIdSetList = readQuery.readFile("./query_data/SizeQuery/DBLP_Query_Size_2_new.txt");
        float validResultLength = 0;
        float validResultSize = 0;
        float fastCounter = 0;
        long validTimer = 0;
        long invalidTimer = 0;
        int countValid = 0;
        int countInvalid = 0;
        System.out.println("Start to verify query with set size = 2");
        for (int i = 0; i < queryIdSetList.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetList.get(i));
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetList.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetList.get(i).get(0)], 6, queryIdSetList.get(i));
            fastCounter += indexBasedQuery.getFastCoreCounter();
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    validResultLength += indexBasedQuery.getAverageMetaPathLength();
                    validResultSize += indexBasedQuery.getValidMetaPathSetSize();
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        System.out.println("The average time cost of query : " + (validTimer + invalidTimer)/queryIdSetList.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        System.out.println("The average meta-path length of query : " + validResultLength / (float) countValid);
        System.out.println("The average meta-path set size of query : " + validResultSize / (float) countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }
//        System.out.println("The average fastcore counter : " + fastCounter / (float)queryIdSetList.size());

        queryIdSetList = readQuery.readFile("./query_data/SizeQuery/DBLP_Query_Size_3_new.txt");
        validResultLength = 0;
        validResultSize = 0;
        fastCounter = 0;
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with set size = 3");
        for (int i = 0; i < queryIdSetList.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetList.get(i));
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetList.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetList.get(i).get(0)], 6, queryIdSetList.get(i));
            fastCounter += indexBasedQuery.getFastCoreCounter();
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    validResultLength += indexBasedQuery.getAverageMetaPathLength();
                    validResultSize += indexBasedQuery.getValidMetaPathSetSize();
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        System.out.println("The average time cost of query : " + (validTimer + invalidTimer)/queryIdSetList.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        System.out.println("The average meta-path length of query : " + validResultLength / (float) countValid);
        System.out.println("The average meta-path set size of query : " + validResultSize / (float) countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }
//        System.out.println("The average fastcore counter : " + fastCounter / (float)queryIdSetList.size());

        queryIdSetList = readQuery.readFile("./query_data/SizeQuery/DBLP_Query_Size_4_new.txt");
        validResultLength = 0;
        validResultSize = 0;
        fastCounter = 0;
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with set size = 4");
        for (int i = 0; i < queryIdSetList.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetList.get(i));
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetList.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetList.get(i).get(0)], 6, queryIdSetList.get(i));
            fastCounter += indexBasedQuery.getFastCoreCounter();
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    validResultLength += indexBasedQuery.getAverageMetaPathLength();
                    validResultSize += indexBasedQuery.getValidMetaPathSetSize();
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        System.out.println("The average time cost of query : " + (validTimer + invalidTimer)/queryIdSetList.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        System.out.println("The average meta-path length of query : " + validResultLength / (float) countValid);
        System.out.println("The average meta-path set size of query : " + validResultSize / (float) countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }
//        System.out.println("The average fastcore counter : " + fastCounter / (float)queryIdSetList.size());

        queryIdSetList = readQuery.readFile("./query_data/SizeQuery/DBLP_Query_Size_5_new.txt");
        validResultLength = 0;
        validResultSize = 0;
        fastCounter = 0;
        validTimer = 0;
        invalidTimer = 0;
        countValid = 0;
        countInvalid = 0;
        System.out.println("Start to verify query with set size = 5");
        for (int i = 0; i < queryIdSetList.size(); i++){
            long tt1 = System.nanoTime();
            System.out.println("The query vertices : " + queryIdSetList.get(i));
            IndexBasedQuery indexBasedQuery = new IndexBasedQuery(graph, vertexType, edgeType, schemaGraph, indexTreeMap.get(vertexType[queryIdSetList.get(i).get(0)]));
            ArrayList<Set<Integer>> result = indexBasedQuery.query(4, vertexType[queryIdSetList.get(i).get(0)], 6, queryIdSetList.get(i));
            fastCounter += indexBasedQuery.getFastCoreCounter();
            if (result != null){
                if (result.size() > 0){
                    countValid += 1;
                    validTimer += System.nanoTime() - tt1;
                    validResultLength += indexBasedQuery.getAverageMetaPathLength();
                    validResultSize += indexBasedQuery.getValidMetaPathSetSize();
                    for (int j = 0; j < result.size(); j++){
                        System.out.println("The vertices number of community " + j + " : " + result.get(j).size());
                    }
                    continue;
                }
            }
            countInvalid += 1;
            invalidTimer += System.nanoTime() - tt1;
        }
        System.out.println("The average time cost of query : " + (validTimer + invalidTimer)/queryIdSetList.size());
        System.out.println("The valid query : " + countValid + " , the average time cost of valid query : " + validTimer / countValid);
        System.out.println("The average meta-path length of query : " + validResultLength / (float) countValid);
        System.out.println("The average meta-path set size of query : " + validResultSize / (float) countValid);
        if (countInvalid != 0){
            System.out.println("The invalid query : " + countInvalid + " , the average time cost of invalid query : " + invalidTimer / countInvalid);
        }
//        System.out.println("The average fastcore counter : " + fastCounter / (float)queryIdSetList.size());
    }
}
