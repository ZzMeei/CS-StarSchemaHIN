package test;

import baseline.*;
import util.*;
import csh.*;
import bean.*;

import java.util.*;

public class DiameterTest {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;

    public DiameterTest(){}

    public void process(){
        Config config = new Config();
        int[][] schemaGraph = config.getSchema("imdb");
//        int[][] schemaGraph = config.getSchema("dblp");
//        int[][] schemaGraph = config.getSchema("foursquare");
//        int[][] schemaGraph = config.getSchema("pubMed");

        DataReader dataReader = new DataReader(Config.newIMDBGraph, Config.newIMDBVertex, Config.newIMDBEdge);
//        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);
//        DataReader dataReader = new DataReader(Config.newFsqGraph, Config.newFsqVertex, Config.newFsqEdge);
//        DataReader dataReader = new DataReader(Config.pubMedGraph, Config.pubMedVertex, Config.pubMedEdge);

        graph = dataReader.readGraph();
        vertexType = dataReader.readVertexType();
        edgeType = dataReader.readEdgeType();

        // step 1: read query
        ReadQuery readQuery = new ReadQuery();
        ArrayList<ArrayList<Integer>> queryIdSetList = null;
        queryIdSetList = readQuery.readFile("./query_data/KQuery/IMDB_Query_K.txt");

        // step 2: find query with more than one meta-path
        ArrayList<ArrayList<Integer>> validQueryList = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<MetaPath>> validMetaPathList = new ArrayList<ArrayList<MetaPath>>();
        for (int i = 0; i < queryIdSetList.size(); i++){
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, queryIdSetList.get(i), 6, schemaGraph);
            ArrayList<Set<Integer>> result = basicQueryFast.query(4, vertexType[queryIdSetList.get(i).get(0)]);

            if (result != null){
                if (result.size() == 1){
                    ArrayList<ArrayList<MetaPath>> resultMetapathList = basicQueryFast.getResultMetaPathList();
                    if (resultMetapathList.get(0).size() > 1){
                        validQueryList.add(queryIdSetList.get(i));
                        validMetaPathList.add(resultMetapathList.get(0));
                    }
                }
            }
        }

        System.out.println("Valid query number : " + validQueryList.size());

        ArrayList<Double> diameterMMCList = new ArrayList<Double>();
        ArrayList<Double> diameterBCoreList = new ArrayList<Double>();

        // step 3: compute diameter
        for (int i = 0; i < validQueryList.size(); i++){
            System.out.println("Query : " + validQueryList.get(i));
            BasicQueryFast basicQueryFast = new BasicQueryFast(graph, vertexType, edgeType, validQueryList.get(i), 6, schemaGraph);
            ArrayList<Set<Integer>> tempSet = basicQueryFast.query(4, vertexType[validQueryList.get(i).get(0)]);
            Set<Integer> MMC = tempSet.get(0);
            List<Set<Integer>> BCoreList = new ArrayList<Set<Integer>>();
            for (int j = 0; j < validMetaPathList.get(i).size(); j++){
                FastBCoreCSH fastBCoreCSH = new FastBCoreCSH(graph, vertexType, edgeType);
                Set<Integer> BCoreSet = fastBCoreCSH.query(validQueryList.get(i), validMetaPathList.get(i).get(j), 6);
                BCoreList.add(BCoreSet);
            }

            // MMC diameter
            double diameterSum = 0;
            for (int j = 0; j < validMetaPathList.get(i).size(); j++){
                Map<Integer, Set<Integer>> graphMap = buildSmallHomGraph(MMC, validMetaPathList.get(i).get(j));
                diameterSum += computeDiameter(graphMap, MMC);
            }
//            System.out.println(diameterSum);
            diameterSum = diameterSum / validMetaPathList.get(i).size();
            diameterMMCList.add(diameterSum);
//            System.out.println(diameterSum);

            // CSH diameter
            diameterSum = 0;
            for (int j = 0; j < validMetaPathList.get(i).size(); j++){
                Map<Integer, Set<Integer>> graphMap = buildSmallHomGraph(BCoreList.get(j), validMetaPathList.get(i).get(j));
                diameterSum += computeDiameter(graphMap, BCoreList.get(j));
            }
//            System.out.println(diameterSum);
            diameterSum = diameterSum / validMetaPathList.get(i).size();
            diameterBCoreList.add(diameterSum);
//            System.out.println(diameterSum);
        }

        // step 4 : compute statistics
        double MMCDiameter = 0, CSHDiameter = 0;
        for (int i = 0; i < validQueryList.size(); i++){
            MMCDiameter += diameterMMCList.get(i);
            CSHDiameter += diameterBCoreList.get(i);
        }

        System.out.println("MMC Diameter : " + MMCDiameter / validQueryList.size() + " ; CSH Diameter : " + CSHDiameter / validQueryList.size());
    }

    public Map<Integer, Set<Integer>> buildSmallHomGraph(Set<Integer> keepSet, MetaPath metaPath) {
		System.out.println("vertexNum: " + keepSet.size());
        int sum = 0;
        BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, metaPath);
        Map<Integer, Set<Integer>> vertexNbMap = new HashMap<Integer, Set<Integer>>();
        for (int curId : keepSet) {
            Set<Integer> pnbSet = batchSearch.collect(curId, keepSet);
            vertexNbMap.put(curId, pnbSet);
//            sum += pnbSet.size();
//            if(sum > 10000000 && sum % 1000 == 0) System.out.println("edgeNum:" + sum);
        }

        return vertexNbMap;
    }

    private static int computeDiameter(Map<Integer, Set<Integer>> graphMap, Set<Integer> aSet) {
        int diameter = 0;

        Diameter tmp = new Diameter(graphMap, aSet);
        diameter = tmp.computeDiameter();

        return diameter;
    }

    public static void main(String[] args) {
        DiameterTest test = new DiameterTest();
        test.process();
    }
}
