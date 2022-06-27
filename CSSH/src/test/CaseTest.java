package test;

import baseline.*;
import bean.MetaPath;
import csh.BatchSearch;
import csh.FastBCoreCSH;
import util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class CaseTest {

    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;

    public CaseTest(){}

    public void onlineExp(){
        Config config = new Config();
        int[][] schemaGraph = config.getSchema("dblp");
        DataReader dataReader = new DataReader(Config.newDblpGraph, Config.newDblpVertex, Config.newDblpEdge);

        int graph[][] = dataReader.readGraph();
        int vertexType[] = dataReader.readVertexType();
        int edgeType[] = dataReader.readEdgeType();

        HashSet<Integer> queryIdSet = new HashSet<Integer>();

        HashSet<Integer> cmIdSet = new HashSet<Integer>();

        try {
            BufferedReader stdin = new BufferedReader(new FileReader("./CaseStudyData/community.txt"));
            String line  = null;
            while ((line = stdin.readLine()) != null){
                cmIdSet.add(Integer.parseInt(line));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int vertexId : cmIdSet){
            queryIdSet.add(vertexId);
            for (int i = 0; i < graph[vertexId].length; i++){
                if (i % 2 == 0){
                    queryIdSet.add(graph[vertexId][i]);
                }
            }
        }

        System.out.println(queryIdSet.size());

        queryIdSet.add(327324); // Jiawei Han
        queryIdSet.add(656104); // Yizhou Sun
        queryIdSet.add(275572); // Jeffrey Xu Yu
//        queryIdSet.add(851503); // Wenjie Zhang

        SmallGraph sGraph = new SmallGraph(graph , vertexType , edgeType);
        sGraph.getSmallGraph(4, 50, queryIdSet);

        ArrayList<Integer> querySet = new ArrayList<Integer>();
        querySet.add(sGraph.newVidMap.get(327324));
        querySet.add(sGraph.newVidMap.get(656104));
        querySet.add(sGraph.newVidMap.get(275572));
//        querySet.add(sGraph.newVidMap.get(851503));

        ArrayList<ArrayList<MetaPath>> resultMetapathList = null;


        this.graph = sGraph.smallGraph;
        this.vertexType = sGraph.smallGraphVertexType;
        this.edgeType = sGraph.smallGraphEdgeType;

        Set<Integer> NMC = null;

        BasicQueryFast basicQueryFast = new BasicQueryFast(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType, querySet, 4, schemaGraph);
        ArrayList<Set<Integer>> result_1 = basicQueryFast.query(4, 1);
        if (result_1 != null){
            if (result_1.size() > 0){
                resultMetapathList = basicQueryFast.getResultMetaPathList();
                for (int j = 0; j < result_1.size(); j++){
                    System.out.println("The vertices number of community " + j + " : " + result_1.get(j).size());
                    NMC = result_1.get(0);
                    for (int vertexId : result_1.get(j)){
//                        System.out.println("ID : " +  sGraph.oldVidMap.get(vertexId) + " tempID : " + vertexId);
//                        System.out.println(sGraph.oldVidMap.get(vertexId));
                    }
                }
            }
        }

        MetaPath metaPath_1 = resultMetapathList.get(0).get(0);
        MetaPath metaPath_2 = resultMetapathList.get(0).get(1);

        System.out.println("MetaPath 1 : " + metaPath_1.toString());
        System.out.println("MetaPath 2 : " + metaPath_2.toString());

        FastBCoreCSH fastBCoreCSH_1 = new FastBCoreCSH(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType);
        Set<Integer> BCoreSet_1 = fastBCoreCSH_1.query(querySet, metaPath_1, 4);

        FastBCoreCSH fastBCoreCSH_2 = new FastBCoreCSH(sGraph.smallGraph, sGraph.smallGraphVertexType, sGraph.smallGraphEdgeType);
        Set<Integer> BCoreSet_2 = fastBCoreCSH_2.query(querySet, metaPath_2, 4);

        System.out.println("BCoreSet_1 : " + BCoreSet_1.size());
        System.out.println("BCoreSet_2 : " + BCoreSet_2.size());

        // PathSim
        double pathSim = 0;
        Map<Integer, Map<Integer, Integer>> psimMap = batchBuildForPathSim(NMC, metaPath_1);
        pathSim += avgPathSim(NMC, psimMap);
        psimMap = batchBuildForPathSim(NMC, metaPath_2);
        pathSim += avgPathSim(NMC, psimMap);
        pathSim = pathSim / 2;
        System.out.println("NMC PathSim : " + pathSim);

        psimMap = batchBuildForPathSim(BCoreSet_1, metaPath_1);
        System.out.println("BCore_1 PathSim : " + avgPathSim(BCoreSet_1, psimMap));
        psimMap = batchBuildForPathSim(BCoreSet_2, metaPath_2);
        System.out.println("BCore_2 PathSim : " + avgPathSim(BCoreSet_2, psimMap));

        // Diameter
        double diameter = 0;
        Map<Integer, Set<Integer>> graphMap = buildSmallHomGraph(NMC, metaPath_1);
        diameter += computeDiameter(graphMap, NMC);
        graphMap = buildSmallHomGraph(NMC, metaPath_2);
        diameter += computeDiameter(graphMap, NMC);
        System.out.println("NMC Diameter : " + diameter);

        graphMap = buildSmallHomGraph(BCoreSet_1, metaPath_1);
        System.out.println("BCore_1 Diameter : " + computeDiameter(graphMap, BCoreSet_1));
        graphMap = buildSmallHomGraph(BCoreSet_2, metaPath_2);
        System.out.println("BCore_2 Diameter : " + computeDiameter(graphMap, BCoreSet_2));

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
//        System.out.println("before eNum : " + eNum);
        eNum = (double)coreSet.size() * (double)(coreSet.size() - 1) / 2;
//        System.out.println("after eNum : " + eNum);
        cSim = cSim / eNum;
        return cSim;
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
        CaseTest test = new CaseTest();
        test.onlineExp();
    }

}
