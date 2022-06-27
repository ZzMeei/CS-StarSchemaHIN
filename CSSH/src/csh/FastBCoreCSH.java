package csh;

import bean.*;

import java.util.*;

public class FastBCoreCSH {
    private int graph[][] = null;//data graph, including vertice IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private int queryK = -1;
    private MetaPath queryMPath = null;

    public FastBCoreCSH(int graph[][], int vertexType[], int edgeType[]) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
    }

    public Set<Integer> query(ArrayList<Integer> querySet, MetaPath queryMPath, int queryK) {
        this.queryK = queryK;
        this.queryMPath = queryMPath;

        int queryId = querySet.get(0);

        //step 1: compute the connected subgraph via batch-search with labeling (BSL)
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        Set<Integer> keepSet = batchLinker.link(queryId, queryMPath);

        //step 2: initialization
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();//a vertex -> its pnbs
        Map<Integer, List<Set<Integer>>> visitMap = new HashMap<Integer, List<Set<Integer>>>();//a vertex -> its visited vertices

        //step 3: find k-neighbors for each vertex
        for(int startVertex:keepSet) {
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for(int i = 0;i <= queryMPath.pathLen;i ++)   visitList.add(new HashSet<Integer>());
            Set<Integer> nbSet = new HashSet<Integer>();

            findFirstKNeighbors(startVertex, startVertex, 0, visitList, nbSet);//find the first k neighbors
            pnbMap.put(startVertex, nbSet);
            visitMap.put(startVertex, visitList);
        }

        //step 4: compute the k-core
        if(pnbMap.get(queryId).size() < queryK)
        {
            return null;
        }
        Queue<Integer> queue = new LinkedList<Integer>();
        Set<Integer> deleteSet = new HashSet<Integer>();//mark the delete vertices
        for(Map.Entry<Integer, Set<Integer>> entry:pnbMap.entrySet()) {
            if(entry.getValue().size() < queryK) {
                queue.add(entry.getKey());
                deleteSet.add(entry.getKey());
            }
        }
        while(queue.size() > 0) {//iteratively delete vertices whose degrees are less than k
            int curId = queue.poll();
            keepSet.remove(curId);

            Set<Integer> pnbSet = pnbMap.get(curId);
            for(int pnbId:pnbSet) {
                if(!deleteSet.contains(pnbId)) {
                    Set<Integer> tmpSet = pnbMap.get(pnbId);
                    tmpSet.remove(curId);
                    if(tmpSet.size() < queryK) {
                        addMoreNeighbors(pnbId, pnbId, 0, visitMap.get(pnbId), tmpSet, keepSet);
                        if(tmpSet.size() < queryK) {
                            queue.add(pnbId);
                            deleteSet.add(pnbId);
                        }
                    }
                }
            }
        }

        //step 5: find the connected community
        BatchLinkerCSH ccFinder = new BatchLinkerCSH(graph, vertexType, edgeType, queryId, queryMPath, keepSet, pnbMap);

        Set<Integer> community = ccFinder.computeCC();

        if (community != null){
            if (!community.contains(querySet.get(1))){
                System.out.println("NULL!");
                return null;
            }
        }

        return community;
    }

    private void findFirstKNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet) {
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID] && !visitSet.contains(nbVertexID)) {
                if(index + 1 < queryMPath.pathLen) {
                    findFirstKNeighbors(startID, nbVertexID, index + 1, visitList, pnbSet);
                    if(pnbSet.size() >= queryK)   return ;//we have found k meta-paths
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(nbVertexID != startID)   pnbSet.add(nbVertexID);
                    visitSet.add(nbVertexID);
                    if(pnbSet.size() >= queryK)   return ;//we have found k meta-paths
                }
            }
        }
    }

    private void addMoreNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet, Set<Integer> keepSet) {
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(!visitSet.contains(nbVertexID) && targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
                if(index + 1 < queryMPath.pathLen) {
                    addMoreNeighbors(startID, nbVertexID, index + 1, visitList, pnbSet, keepSet);
                    if(pnbSet.size() >= queryK)   return ;//we have found k meta-paths
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(keepSet.contains(nbVertexID)) {//restrict it to be in keepSet
                        if(nbVertexID != startID) {
                            pnbSet.add(nbVertexID);
                            visitSet.add(nbVertexID);
                            if(pnbSet.size() >= queryK)   return ;//we have found k meta-paths
                        }
                    }
                }
                visitSet.add(nbVertexID);//mark this vertex (and its branches) as visited
            }
        }
    }
}
