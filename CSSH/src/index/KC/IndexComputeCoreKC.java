package index.KC;
import bean.*;
import index.*;
import tool.*;

import java.util.*;

public class IndexComputeCoreKC implements IndexComputeCore{
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;
    private HashMap<String, IndexNode> indexTree = null;

    public IndexComputeCoreKC(int graph[][], int vertexType[], int edgeType[], HashMap<String, IndexNode> indexTreeKC){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.indexTree = indexTreeKC;
    }

    public Set<Integer> computeCore(int k, ArrayList<MetaPath> metaPathList, ArrayList<Integer> queryIdSet){
        ArrayList<Set<Integer>> coreList = new ArrayList<Set<Integer>>();
        for (int i = 0; i < metaPathList.size(); i++){
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
        Map<String, Map<Integer, Set<Integer>>> graphMap = new HashMap<String, Map<Integer, Set<Integer>>>();
        for (int i = 0; i < metaPathList.size(); i++){
            Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
            MetaPath queryMPath = metaPathList.get(i);
            for(int startVertex:unionSet) {
                List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
                for(int j = 0;j <= queryMPath.pathLen;j ++)   visitList.add(new HashSet<Integer>());
                Set<Integer> nbSet = new HashSet<Integer>();

                findAllNeighbors(startVertex, startVertex, 0, visitList, nbSet, queryMPath, unionSet);
                pnbMap.put(startVertex, nbSet);
            }
            graphMap.put(queryMPath.toString(), pnbMap);
        }
        ArrayList<Map<Integer, Set<Integer>>> graphList = new ArrayList<Map<Integer, Set<Integer>>>();
        for (String metapath: graphMap.keySet()){
            graphList.add(graphMap.get(metapath));
        }
        ComputeBCore computeBCore = new ComputeBCore(queryIdSet, k, graphList);
        Set<Integer> community = computeBCore.getBasicKPCore();
        return community;
    }

    public Set<Integer> indexQuerySinglePathCore(int k, MetaPath curMetaPath, ArrayList<Integer> queryIdSet){
        HashSet<Integer> vertexIdSet = new HashSet<>();
        for (int curK:indexTree.get(curMetaPath.toString()).getMap().keySet()){
            if (curK >= k){
                for (int vertexId:indexTree.get(curMetaPath.toString()).getVertexSet(curK)){
                    vertexIdSet.add(vertexId);
                }
            }
        }
        for (int vertexId: queryIdSet){
            if (!vertexIdSet.contains(vertexId)){
                return null;
            }
        }

        ComputeHeterCC computeHeterCC = new ComputeHeterCC(queryIdSet, graph, vertexType, edgeType);
        Set<Integer> keepSet = computeHeterCC.getCC(curMetaPath, vertexIdSet);

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
        return -1;
    }
}
