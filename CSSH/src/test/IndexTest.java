package test;
import bean.MetaPath;
import index.MKC.*;
import index.MC.*;
import index.KC.*;
import util.*;
import index.*;

import java.util.*;

// indexBuildExp : for index space cost analysis

public class IndexTest {
    public static void main(String[] args) {
//        indexBuildExp();
        indexBuildAdd();
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

    public static void indexBuildExp(){
        for (int i = 2; i <= 4; i=i+2){
            System.out.println("-----------");
            System.out.println("The max length of meta-path : "+ i);
            HashMap<String, IndexNode> indexTree = null;
            long t1 = System.nanoTime();
            indexTree = buildIndexTree("MKC", i, 1);
            long t2 = System.nanoTime();
            System.out.println("Build Index MKC cost time : " + (t2-t1)/1000000000);
            long count = 0;
            for (String metaPathString : indexTree.keySet()){
                for (int k: indexTree.get(metaPathString).getMap().keySet()){
                    for (int vertexId:indexTree.get(metaPathString).getMap().get(k)){
                        count += 1;
                    }
                }
            }
            System.out.println("The index MKC store vertices : " + count);

            t1 = System.nanoTime();
            indexTree = buildIndexTree("MC", i, 1);
            t2 = System.nanoTime();
            System.out.println("Build Index MC cost time : " + (t2-t1)/1000000000);
            count = 0;
            for (String metaPathString : indexTree.keySet()){
                for (int k: indexTree.get(metaPathString).getMap().keySet()){
                    for (int vertexId:indexTree.get(metaPathString).getMap().get(k)){
                        count += 1;
                    }
                }
//                if (indexTree.get(metaPathString).getFather() == null){
//                    for (int k: indexTree.get(metaPathString).getNumberMap().keySet()){
//                        count += indexTree.get(metaPathString).getNumberMap().get(k);
//                    }
//                }else{
//                    MetaPath metaPathF = indexTree.get(metaPathString).getFather();
//                    for (int k: indexTree.get(metaPathString).getNumberMap().keySet()){
//                        if (indexTree.get(metaPathF.toString()).getNumberMap().keySet().contains(k)){
//                            count += indexTree.get(metaPathString).getNumberMap().get(k) - indexTree.get(metaPathF.toString()).getNumberMap().get(k);
//                        }else {
//                            count += indexTree.get(metaPathString).getNumberMap().get(k);
//                        }
//                    }
//                }
            }
            System.out.println("The index MC store vertices : " + count);

            t1 = System.nanoTime();
            indexTree = buildIndexTree("KC", i, 1);
            t2 = System.nanoTime();
            System.out.println("Build Index KC cost time : " + (t2-t1)/1000000000);
            count = 0;
            for (String metaPathString : indexTree.keySet()){
                for (int k: indexTree.get(metaPathString).getMap().keySet()){
                    for (int vertexId:indexTree.get(metaPathString).getMap().get(k)){
                        count += 1;
                    }
                }
            }
            System.out.println("The index KC store vertices : " + count);
        }
    }

    public static void indexBuildAdd(){
        HashMap<String, IndexNode> indexTree = null;
        long t1 = System.nanoTime();
        indexTree = buildIndexTree("KC", 4, 1);
        long t2 = System.nanoTime();
        System.out.println("Build Index KC cost time : " + (t2-t1)/1000000000);
        long count = 0;
        for (String metaPathString : indexTree.keySet()){
            for (int k: indexTree.get(metaPathString).getMap().keySet()){
                for (int vertexId:indexTree.get(metaPathString).getMap().get(k)){
                    count += 1;
                }
            }
        }
        System.out.println("The index KC store vertices : " + count);
    }
}
