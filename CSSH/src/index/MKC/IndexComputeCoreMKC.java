package index.MKC;
import bean.*;
import index.*;
import tool.*;

import java.util.*;

public class IndexComputeCoreMKC implements IndexComputeCore{
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;
    private HashMap<String, IndexNode> indexTree = null;
    private ComputeHeterCC computeHeterCC = null;
    private Map<String, Set<Integer>> communityMap = new HashMap<String, Set<Integer>>();
    private Map<String, Set<Integer>> singleCoreMap = new HashMap<String, Set<Integer>>();

    public int fastCoreCounter = 0; // test

    public IndexComputeCoreMKC(int graph[][], int vertexType[], int edgeType[], HashMap<String, IndexNode> indexTree){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.indexTree = indexTree;
    }

    public Set<Integer> computeCore(int k, ArrayList<MetaPath> metaPathList, ArrayList<Integer> queryIdSet) {
        ArrayList<Set<Integer>> coreList = new ArrayList<Set<Integer>>();

        computeHeterCC = new ComputeHeterCC(queryIdSet, graph, vertexType, edgeType);

        if (metaPathList.size() == 1){
            Set<Integer> core = indexQuerySinglePathCore(k, metaPathList.get(0), queryIdSet);
            if (core != null){
                singleCoreMap.put(metaPathList.get(0).toString(), core);
                return core;
            }else {
                singleCoreMap.put(metaPathList.get(0).toString(), new HashSet<Integer>());
                return null;
            }
        }

        for (int i = 0; i < metaPathList.size(); i++){

            if (singleCoreMap.keySet().contains(metaPathList.get(i).toString())){
                coreList.add(singleCoreMap.get(metaPathList.get(i).toString()));
                continue;
            }

            Set<Integer> core = indexQuerySinglePathCore(k, metaPathList.get(i), queryIdSet);
            if (core != null){
                coreList.add(core);
            }else{
                coreList.add(new HashSet<Integer>());
            }
        }
        HashMap<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < coreList.size(); i++){
            for (int vertexId: coreList.get(i)){
                if (!tempMap.keySet().contains(vertexId)){
                    tempMap.put(vertexId, 1);
                }else{
                    tempMap.put(vertexId, tempMap.get(vertexId) + 1);
                }
            }
        }
        Set<Integer> unionSet = new HashSet<Integer>();
        for (int vertexId : tempMap.keySet()){
            if (tempMap.get(vertexId) == coreList.size()){
                unionSet.add(vertexId);
            }
        }

        ComputeBCoreFast computeBCoreFast = new ComputeBCoreFast(graph, vertexType, edgeType);
        Set<Integer> community = computeBCoreFast.getBasicKPCore(unionSet, queryIdSet, metaPathList, k);

        // test
        this.fastCoreCounter += computeBCoreFast.TestCounter;

        return community;
    }

    public Set<Integer> indexQuerySinglePathCore(int k, MetaPath curMetaPath, ArrayList<Integer> queryIdSet){
        HashSet<Integer> vertexIdSet = new HashSet<>();
        if (communityMap.keySet().contains(curMetaPath.toString())){
            return communityMap.get(curMetaPath.toString());
        }
        MetaPath metaPath = curMetaPath;
        while (true){
            for (int curK: indexTree.get(metaPath.toString()).getMap().keySet()){
                if (curK >= k){
                    for (int vertexId: indexTree.get(metaPath.toString()).getVertexSet(curK)){
                        vertexIdSet.add(vertexId);
                    }
                }
            }
            if (indexTree.get(metaPath.toString()).getFather() != null){
                metaPath = indexTree.get(metaPath.toString()).getFather();
            }else{
                break;
            }
        }
        for (int vertexId : queryIdSet){
            if (!vertexIdSet.contains(vertexId)){
                return null;
            }
        }

        Set<Integer> keepSet = computeHeterCC.getCC(curMetaPath, vertexIdSet);
        communityMap.put(curMetaPath.toString(), keepSet);
        return keepSet;
    }

    public void findAllNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet, MetaPath queryMPath, Set<Integer> unionSet) {
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID] && !visitSet.contains(nbVertexID)) {
                if(index + 1 < queryMPath.pathLen) {
                    findAllNeighbors(startID, nbVertexID, index + 1, visitList, pnbSet, queryMPath, unionSet);
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(nbVertexID != startID && unionSet.contains(nbVertexID))   pnbSet.add(nbVertexID);
                    visitSet.add(nbVertexID);
                }
            }
        }
    }

    @Override
    public int getFastCoreCounter() {
        return fastCoreCounter;
    }
}
