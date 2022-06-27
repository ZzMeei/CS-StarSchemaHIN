package test;

import baseline.*;
import csh.FastBCoreCSH;
import util.*;
import bean.*;

import java.util.*;

public class PathSimTest {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;

    public PathSimTest(){
    }

    public void process() {
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

        ArrayList<Double> simMMCList = new ArrayList<Double>();
        ArrayList<Double> simBCoreList = new ArrayList<Double>();

        // step 3 : compute PathSim for each query
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

            // MMC PathSim
            double pathSimSum = 0;
            for (int j = 0; j < validMetaPathList.get(i).size(); j++){
                Map<Integer, Map<Integer, Integer>> psimMap = batchBuildForPathSim(MMC, validMetaPathList.get(i).get(j));
                pathSimSum += avgPathSim(MMC, psimMap);
            }
            pathSimSum = pathSimSum / validMetaPathList.get(i).size();
            simMMCList.add(pathSimSum);

            // CSH PathSim
            pathSimSum = 0;
            for (int j = 0; j < validMetaPathList.get(i).size(); j++){
                Map<Integer, Map<Integer, Integer>> psimMap = batchBuildForPathSim(BCoreList.get(j), validMetaPathList.get(i).get(j));
                pathSimSum += avgPathSim(BCoreList.get(j), psimMap);
            }
            pathSimSum = pathSimSum / validMetaPathList.get(i).size();
            simBCoreList.add(pathSimSum);
        }

        // step 4 : compute statistics
        double MMCPathSim = 0, CSHPathSim = 0;
        for (int i = 0; i < validQueryList.size(); i++){
            MMCPathSim += simMMCList.get(i);
            CSHPathSim += simBCoreList.get(i);
        }

        System.out.println("MMC path sim : " + MMCPathSim / validQueryList.size() + " ; CSH path sim : " + CSHPathSim / validQueryList.size());
    }

    public Map<Integer, Map<Integer, Integer>> batchBuildForPathSim(Set<Integer> keepSet, MetaPath queryMPath) {
        Map<Integer, Map<Integer, Integer>> vertexNbMap = new HashMap<Integer, Map<Integer, Integer>>();
        for (int startId : keepSet) {
            Map<Integer, Integer> anchorMap = new HashMap<Integer, Integer>();
            anchorMap.put(startId, 1);
            for(int layer = 0;layer < queryMPath.pathLen;layer ++) {
                int targetVType = queryMPath.vertex[layer + 1], targetEType = queryMPath.edge[layer];
                Map<Integer, Integer> nextAnchorMap = new HashMap<Integer, Integer>();
                for (int anchorId : anchorMap.keySet()) {
                    int anchorPNum = anchorMap.get(anchorId);
                    int nb[] = graph[anchorId];
                    for (int i = 0; i < nb.length; i+=2) {
                        int nbVertexId = nb[i], nbEdgeId = nb[i+1];
                        if (targetVType == vertexType[nbVertexId] && targetEType == edgeType[nbEdgeId]) {
                            if (layer < queryMPath.pathLen - 1) {
                                if (!nextAnchorMap.containsKey(nbVertexId))	nextAnchorMap.put(nbVertexId, 0);
                                int curPNum = nextAnchorMap.get(nbVertexId);
                                nextAnchorMap.put(nbVertexId, anchorPNum + curPNum);
                            } else {
                                if (keepSet.contains(nbVertexId)) {
                                    if (!nextAnchorMap.containsKey(nbVertexId))	nextAnchorMap.put(nbVertexId, 0);
                                    int curPNum = nextAnchorMap.get(nbVertexId);
                                    nextAnchorMap.put(nbVertexId, anchorPNum + curPNum);
                                }
                            }
                        }
                    }
                }
                anchorMap = nextAnchorMap;
            }
            vertexNbMap.put(startId, anchorMap);
        }
        return vertexNbMap;
    }

    public double avgPathSim(Set<Integer> coreSet, Map<Integer, Map<Integer, Integer>> psimMap) {
        double eNum = 0;
        double cSim = 0;
        for (int vid : coreSet) {
            Map<Integer, Integer> nbMap = psimMap.get(vid);
            for (int nbVid : nbMap.keySet()) {
                if (!coreSet.contains(nbVid)) continue;
                if (nbVid <= vid) continue;
                eNum++;
                cSim += ((double) psimMap.get(vid).get(nbVid) + (double) psimMap.get(vid).get(nbVid)) / ((double) psimMap.get(vid).get(vid) + (double) psimMap.get(nbVid).get(nbVid));
            }
        }
        eNum = (double)coreSet.size() * (double)(coreSet.size() - 1) / 2;
        cSim = cSim / eNum;
        return cSim;
    }

    public static void main(String[] args) {
        PathSimTest test = new PathSimTest();
        test.process();
    }
}
