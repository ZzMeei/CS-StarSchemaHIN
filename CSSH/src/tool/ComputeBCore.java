package tool;

import java.util.*;

// function : HomNMC

public class ComputeBCore {
    private ArrayList<Integer> queryIdSet = null;
    private int queryK = -1;
    private ArrayList<Map<Integer, Set<Integer>>> graphList = null;
    private Set<Integer> vertexSet = null;

    public ComputeBCore(ArrayList<Integer> queryIdSet, int queryK, ArrayList<Map<Integer, Set<Integer>>> graphList){
        this.queryIdSet = queryIdSet;
        this.queryK = queryK;
        this.graphList = graphList;
    }

    public Set<Integer> getBasicKPCore(){
        // Get the union of each graph's vertices
        vertexSet = new HashSet<Integer>();
        for (int i = 0; i < graphList.size(); i++){
            for (int vertexId: graphList.get(i).keySet()){
                vertexSet.add(vertexId);
            }
        }
        // Get the multi-layer graph
        for (int i = 0; i < graphList.size(); i++){
            for (int vertexId: vertexSet){
                if (! graphList.get(i).keySet().contains(vertexId)){
                    graphList.get(i).put(vertexId, new HashSet<Integer>());
                }
            }
        }

        // test
        int counter = 0;

        while (true){
            counter += 1;
            for (int vertexId:queryIdSet){
                if (!vertexSet.contains(vertexId)){
                    return null;
                }
            }
            ArrayList<Integer> edgeNumList = new ArrayList<Integer>();
            if (!checkConnected(edgeNumList)){
                continue;
            }
            for (int i = 0; i < edgeNumList.size(); i++){
                if (edgeNumList.get(i) - vertexSet.size() < (queryK * queryK - queryK) / 2 - 1){
                    System.out.println("Pruned by Vertex Number and Edge Number");
                    return null;
                }
            }
            Queue<Integer> unqualifiedVertices = new LinkedList<Integer>();
            HashMap<Integer, Boolean> visit = new HashMap<Integer, Boolean>();
            for (int vertexId: vertexSet){
                visit.put(vertexId, false);
            }
            // For each layer graph, compute its degree of every vertex
            ArrayList<HashMap<Integer, Integer>> degreeList = new ArrayList<HashMap<Integer, Integer>>();
            for (int i = 0; i < graphList.size(); i++){
                HashMap<Integer, Integer> degreeMap = new HashMap<Integer, Integer>();
                for (int vertexId: vertexSet){
                    int degree = graphList.get(i).get(vertexId).size();
                    degreeMap.put(vertexId, degree);
                    if (degree < queryK && (! unqualifiedVertices.contains(vertexId))){
                        unqualifiedVertices.add(vertexId);
                        visit.replace(vertexId, false, true);
                    }
                }
                degreeList.add(degreeMap);
            }
            // Delete the vertex whose degree less than k
            while (unqualifiedVertices.size() > 0){
                int curVertexId = unqualifiedVertices.poll();
                if (queryIdSet.contains(curVertexId)){
                    return null;
                }
                for (int i = 0; i < graphList.size(); i++){
                    for (int neighbor : graphList.get(i).get(curVertexId)){
                        if (! visit.get(neighbor)){
                            degreeList.get(i).replace(neighbor, degreeList.get(i).get(neighbor), degreeList.get(i).get(neighbor) - 1);
                            if (degreeList.get(i).get(neighbor) < queryK){
                                unqualifiedVertices.add(neighbor);
                                visit.replace(neighbor, false, true);
                            }
                        }
                    }
                }
            }
            Iterator<Integer> iterator = vertexSet.iterator();
            while(iterator.hasNext()){
                int next = iterator.next();
                if (visit.get(next)){
                    iterator.remove();
                }
            }
            this.graphList = updateGraph(vertexSet, this.graphList);
            if (checkConnected(null)){
                break;
            }
        }
        return vertexSet;
    }

    public ArrayList<Map<Integer, Set<Integer>>> updateGraph(Set<Integer> unionSet, ArrayList<Map<Integer, Set<Integer>>> oldGraphList){
        ArrayList<Map<Integer, Set<Integer>>> newGrapList = new ArrayList<Map<Integer, Set<Integer>>>();
        for (int i = 0; i < oldGraphList.size(); i++){
            Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
            for (int vertexId: oldGraphList.get(i).keySet()){
                if (!unionSet.contains(vertexId)){
                    for (int neighborId: oldGraphList.get(i).get(vertexId)){
                        oldGraphList.get(i).get(neighborId).remove(vertexId);
                    }
                }
            }
            for (int vertexId: oldGraphList.get(i).keySet()){
                if (unionSet.contains(vertexId)){
                    pnbMap.put(vertexId, oldGraphList.get(i).get(vertexId));
                }
            }
            newGrapList.add(pnbMap);
        }
        return newGrapList;
    }

    public boolean checkConnected(ArrayList<Integer> edgeNumList){
        // Check if connected
        Map<Integer, Integer> helpMap = new HashMap<Integer, Integer>();
        for (int vertexId:vertexSet){
            helpMap.put(vertexId, 0);
        }
        for (int i = 0; i < graphList.size(); i++){
            Set<Integer> ccSet = new HashSet<Integer>();
            ComputeHomCC computeHomCC = new ComputeHomCC(graphList.get(i), queryIdSet.get(0));
            ccSet = computeHomCC.getCC();
            if (edgeNumList != null){
                int edgeNum = computeHomCC.getEdgeNum();
                edgeNumList.add(edgeNum);
            }
            for (int vertexId: ccSet){
                helpMap.put(vertexId, helpMap.get(vertexId)+1);
            }
        }
        int flag = 1;
        Set<Integer> unionSet = new HashSet<Integer>();
        for (int vertexId:vertexSet){
            if (helpMap.get(vertexId) == graphList.size()){
                unionSet.add(vertexId);
            }else {
                flag = 0;
            }
        }
        vertexSet = unionSet;
        if (flag == 0){
            graphList = updateGraph(unionSet, graphList);
            return false;
        }else {
            return true;
        }
    }
}
