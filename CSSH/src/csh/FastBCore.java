package csh;
import bean.*;

import java.util.*;

public class FastBCore {
    private int graph[][] = null;//data graph, including vertice IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private MetaPath queryMPath = null;

    public FastBCore(int graph[][], int vertexType[], int edgeType[]) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;

    }

    public Map<Integer, Set<Integer>> queryGraph(ArrayList<Integer> queryIdSet, MetaPath queryMPath, int queryK) {
        this.queryMPath = queryMPath;

        //step 1: compute the connected subgraph via batch-search with labeling (BSL)
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        Set<Integer> keepSet = batchLinker.link(queryIdSet, queryMPath);

        //step 2: initialization
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();//a vertex -> its pnbs

        //step 3: find all-neighbors for each vertex
        for(int startVertex:keepSet) {
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for(int i = 0;i <= queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
            Set<Integer> nbSet = new HashSet<Integer>();

            findAllNeighbors(startVertex, startVertex, 0, visitList, nbSet);
            pnbMap.put(startVertex, nbSet);
            visitList = null;
        }
        return pnbMap;
    }

    public Map<Integer, int[]> queryGraphUpdate(ArrayList<Integer> queryIdSet, MetaPath queryMPath, int queryK) {
        this.queryMPath = queryMPath;

        //step 1: compute the connected subgraph via batch-search with labeling (BSL)
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        Set<Integer> keepSet = batchLinker.link(queryIdSet, queryMPath);

        //step 2: initialization
        Map<Integer, int[]> pnbMap = new HashMap<Integer, int[]>();

        //step 3: find all-neighbors for each vertex
        for(int startVertex:keepSet) {
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for(int i = 0;i <= queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
            Set<Integer> nbSet = new HashSet<Integer>();

            findAllNeighbors(startVertex, startVertex, 0, visitList, nbSet);
            visitList = null;
            int nbArr[] = new int[nbSet.size()];
            int i = 0;
            for (int nbId:nbSet){
                nbArr[i] = nbId;
                i++;
            }
            pnbMap.put(startVertex, nbArr);
        }
        return pnbMap;
    }

    private void findAllNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet) {
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID] && !visitSet.contains(nbVertexID)) {
                if(index + 1 < queryMPath.pathLen) {
                    findAllNeighbors(startID, nbVertexID, index + 1, visitList, pnbSet);
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(nbVertexID != startID)   pnbSet.add(nbVertexID);
                    visitSet.add(nbVertexID);
                }
            }
        }
    }

    public Map<Integer, Set<Integer>> queryGraphRestrict(MetaPath queryMPath, Set<Integer> communitySet) {
        this.queryMPath = queryMPath;

        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();//a vertex -> its pnbs

        //step 3: find all-neighbors for each vertex
        for(int startVertex:communitySet) {
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for(int i = 0;i <= queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
            Set<Integer> nbSet = new HashSet<Integer>();

            findAllNeighborsRestrict(startVertex, startVertex, 0, visitList, nbSet, communitySet);
            pnbMap.put(startVertex, nbSet);
            visitList = null;
        }

        return pnbMap;
    }

    public Map<Integer, int[]> queryGraphRestrictUpdate(MetaPath queryMPath, Set<Integer> communitySet) {
        this.queryMPath = queryMPath;

        Map<Integer, int[]> pnbMap = new HashMap<Integer, int[]>();

        //step 3: find all-neighbors for each vertex
        for(int startVertex:communitySet) {
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for(int i = 0;i <= queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
            Set<Integer> nbSet = new HashSet<Integer>();

            findAllNeighborsRestrict(startVertex, startVertex, 0, visitList, nbSet, communitySet);
            visitList = null;
            int nbArr[] = new int[nbSet.size()];
            int i = 0;
            for (int nbId:nbSet){
                nbArr[i] = nbId;
                i++;
            }
            pnbMap.put(startVertex, nbArr);
        }

        return pnbMap;
    }

    private void findAllNeighborsRestrict(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet, Set<Integer> communitySet) {
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID] && !visitSet.contains(nbVertexID)) {
                if(index + 1 < queryMPath.pathLen) {
                    findAllNeighborsRestrict(startID, nbVertexID, index + 1, visitList, pnbSet, communitySet);
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(nbVertexID != startID && communitySet.contains(nbVertexID)){
                        pnbSet.add(nbVertexID);
                    }
                    visitSet.add(nbVertexID);
                }
            }
        }
    }

}

