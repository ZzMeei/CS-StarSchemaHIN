package util;

import java.util.*;

public class SmallGraph {
    public Map<Integer, Integer> newVidMap;
    public Map<Integer, Integer> newEidMap;
    public Map<Integer, Integer> oldVidMap;
    public int[][] smallGraph;
    public int[] smallGraphVertexType;
    public int[] smallGraphEdgeType;

    private int[][] graph;
    private int[] vertexType;
    private int[] edgeType;

    public SmallGraph(int[][] graph, int[] vertexType, int[] edgeType) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
    }

    private void getNewVidMap(int part, int total,
                              Set<Integer> nodesSet/* these nodes are needed in the final graph */) {
        int smallGraphSize = (graph.length * part) / total;
        System.out.println("Size : " + smallGraphSize);
        newVidMap = new HashMap<Integer, Integer>();// oldVid to newVid
        oldVidMap = new HashMap<Integer, Integer>(); // newVid to oldVid
        int newVid = 0;
        for (int nodeId : nodesSet) {
            if (!newVidMap.containsKey(nodeId)) {
                newVidMap.put(nodeId, newVid);
                oldVidMap.put(newVid, nodeId);
                newVid++;
                if (newVidMap.size() >= smallGraphSize) {
                    return;
                }
            }
        }

        for (int i = 0; i < graph.length; i = i + total) {
            for (int j = 0; j < part; j++) {
                int oldVid = i + j;
                if (!newVidMap.containsKey(oldVid)) {
                    newVidMap.put(oldVid, newVid);
                    oldVidMap.put(newVid, oldVid);
                    newVid++;
                    if (newVidMap.size() >= smallGraphSize) {
                        return;
                    }
                }
            }
        }

    }

    private void getSmallGraphVertexType() {
        smallGraphVertexType = new int[newVidMap.size()];
        for (int oldVid : newVidMap.keySet()) {
            int newVid = newVidMap.get(oldVid);
            smallGraphVertexType[newVid] = vertexType[oldVid];
        }
    }

    public void getSmallGraph(int part, int total, Set<Integer> nodesSet) {
        getNewVidMap(part, total, nodesSet);
        getSmallGraphVertexType();
        newEidMap = new HashMap<Integer, Integer>();
        this.smallGraph = new int[newVidMap.size()][];
        int newEid = 0;
        for (int oldVid : newVidMap.keySet()) {
            int newVid = newVidMap.get(oldVid);
            int numOfNeighbor = 0;
            for (int i = 0; i < graph[oldVid].length; i = i + 2) {
                if (newVidMap.containsKey(graph[oldVid][i])) {
                    numOfNeighbor++;
                }
            }
            smallGraph[newVid] = new int[2 * numOfNeighbor];
            int location = 0;
            for (int i = 0; i < graph[oldVid].length; i = i + 2) {
                if (newVidMap.containsKey(graph[oldVid][i])) {
                    int neighborNid = newVidMap.get(graph[oldVid][i]);
                    int neighborEid = graph[oldVid][i + 1];
                    smallGraph[newVid][location] = neighborNid;
                    smallGraph[newVid][location + 1] = newEid;
                    newEidMap.put(neighborEid, newEid);
                    newEid++;
                    location = location + 2;
                }
            }
        }
        smallGraphEdgeType = new int[newEid];
        for (int i = 0; i < edgeType.length; i++) {
            int type = edgeType[i];
            if (newEidMap.containsKey(i)) {
                int newEdgeId = newEidMap.get(i);
                smallGraphEdgeType[newEdgeId] = type;
            }
        }
    }
}
