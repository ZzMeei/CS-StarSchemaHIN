package csh;
import bean.*;

import java.util.*;

public class HomoGraphBuilder {
    private int graph[][] = null;//data graph, including vertice IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private MetaPath queryMPath = null;//the query meta-path

    public HomoGraphBuilder(int graph[][], int vertexType[], int edgeType[], MetaPath queryMPath) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.queryMPath = queryMPath;
    }

    public Map<Integer, int[]> build(){
        //step 1: collect vertices of the same type
        int STARTTYPE = queryMPath.vertex[0];
        Set<Integer> keepSet = new HashSet<Integer>();
        for(int i = 0;i < vertexType.length;i ++) {
            if(vertexType[i] == STARTTYPE) {
                keepSet.add(i);
            }
        }

        long count = 0; // for test

        //step 2: find neighbors
        Map<Integer, int[]> pnbMap = new HashMap<Integer, int[]>();
        for(int startId:keepSet) {
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for(int i = 0; i <= queryMPath.pathLen; i++){
                visitList.add(new HashSet<Integer>());
            }
            Set<Integer> nbSet = new HashSet<Integer>();
            findAllNeighbors(startId, startId, 0, visitList, nbSet);
            visitList = null;
            int nbArr[] = new int[nbSet.size()];
            int i = 0;
            for(int nbId:nbSet) {
                nbArr[i] = nbId;
                i ++;
            }
            pnbMap.put(startId, nbArr);
            count += nbSet.size();
        }

        System.out.println("Meta-Path : " + queryMPath.toString() + " nb : " + count);
        return pnbMap;
    }

    // BSL
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
}
