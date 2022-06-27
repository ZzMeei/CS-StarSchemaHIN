package tool;

import bean.*;

import java.util.*;

// function : fastNMC

public class ComputeBCoreFast {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;
    private int queryK = -1;
    private ArrayList<MetaPath> metaPathList = null;
    private Set<Integer> vertexSet = null;
    private ArrayList<Integer> queryIdSet = null;

    public int TestCounter = 0;

    public ComputeBCoreFast(int graph[][], int vertexType[], int edgeType[]){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
    }

    public Set<Integer> getBasicKPCore(Set<Integer> unionSet, ArrayList<Integer> queryIdSet, ArrayList<MetaPath> metaPathList, int queryK) {
        this.vertexSet = unionSet;
        this.queryIdSet = queryIdSet;
        this.metaPathList = metaPathList;
        this.queryK = queryK;

        Map<String, Map<Integer, Set<Integer>>> pnbMapM = new HashMap<String, Map<Integer, Set<Integer>>>();

        Map<String, Map<Integer, List<Set<Integer>>>> visitMapM = new HashMap<String, Map<Integer, List<Set<Integer>>>>();
        for (int i = 0; i < metaPathList.size(); i++){
            pnbMapM.put(metaPathList.get(i).toString(), new HashMap<Integer, Set<Integer>>());
            visitMapM.put(metaPathList.get(i).toString(), new HashMap<Integer, List<Set<Integer>>>());
        }

        Queue<Integer> queue = new LinkedList<Integer>();
        Set<Integer> deleteSet = new HashSet<Integer>();

        for (int vertexId : queryIdSet){
            if (!vertexSet.contains(vertexId)){
                return null;
            }
        }

        this.TestCounter += 1;
//        System.out.println(metaPathList);
        for (int i = 0; i < metaPathList.size(); i++){
            MetaPath curMetaPath = metaPathList.get(i);
            for (int startVertex : vertexSet){
                List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
                for (int j = 0; j <= curMetaPath.pathLen; j++){
                    visitList.add(new HashSet<Integer>());
                }
                pnbMapM.get(curMetaPath.toString()).put(startVertex, new HashSet<Integer>());
                visitMapM.get(curMetaPath.toString()).put(startVertex, visitList);
            }
            Map<Integer, List<Set<Integer>>> visitMap = visitMapM.get(curMetaPath.toString());
            Map<Integer, Set<Integer>> pnbMap = pnbMapM.get(curMetaPath.toString());

            for (int startVertex : vertexSet){
                if(pnbMap.get(startVertex).size() >= queryK){
                    continue;
                }
                findFirstKNeighbors(startVertex, startVertex, 0, visitMap, pnbMap, curMetaPath, vertexSet);
            }
        }

        for (int i = 0; i < metaPathList.size(); i++){
            MetaPath curMetaPath = metaPathList.get(i);
            for (int vertexId : queryIdSet){
                if (pnbMapM.get(curMetaPath.toString()).get(vertexId).size() < queryK){
                    return null;
                }
            }
        }

        for (int i = 0; i < metaPathList.size(); i++){
            MetaPath curMetaPath = metaPathList.get(i);
            Map<Integer, Set<Integer>> pnbMap= pnbMapM.get(curMetaPath.toString());
            for (Map.Entry<Integer, Set<Integer>> entry: pnbMap.entrySet()){
                if (!deleteSet.contains(entry.getKey())){
                    if (entry.getValue().size() < queryK){
                        queue.add(entry.getKey());
                        deleteSet.add(entry.getKey());
                    }
                }
            }
        }

        deleteVertices(pnbMapM, visitMapM, queue, deleteSet);

        ComputeHeterCC computeHeterCC = new ComputeHeterCC(queryIdSet, graph, vertexType, edgeType);

        while (true){
            for (int vertexId : queryIdSet){
                if (!vertexSet.contains(vertexId)){
                    return null;
                }
            }
            if (!checkConnectedFast(pnbMapM, visitMapM, queue, deleteSet, computeHeterCC)){
                continue;
            }
            break;
        }
        return vertexSet;
    }

    public boolean checkConnected(Map<String, Map<Integer, Set<Integer>>> pnbMapM, Map<String, Map<Integer, List<Set<Integer>>>> visitMapM, Queue<Integer> queue, Set<Integer> deleteSet, ComputeHeterCC computeHeterCC){
        // Check if connected
        Map<Integer, Integer> helpMap = new HashMap<Integer, Integer>();
        for (int vertexId : vertexSet){
            helpMap.put(vertexId, 0);
        }
        for (int i = 0; i < metaPathList.size(); i++){
            Set<Integer> ccSet = computeHeterCC.getCC(metaPathList.get(i), vertexSet);
            if (ccSet != null){
                for (int vertexId : ccSet){
                    helpMap.put(vertexId, helpMap.get(vertexId) + 1);
                }
            }
        }
        Set<Integer> localDeleteSet = new HashSet<Integer>();
        for (int vertexId : vertexSet){
            if (helpMap.get(vertexId) != metaPathList.size()){
                localDeleteSet.add(vertexId);
            }
        }
        if (localDeleteSet.size() != 0){
            for (int vertexId : localDeleteSet){
                if (!deleteSet.contains(vertexId)){
                    queue.add(vertexId);
                    deleteSet.add(vertexId);
                }
            }
            deleteVertices(pnbMapM, visitMapM, queue, deleteSet);
            return false;
        }else {
            return true;
        }
    }

    public boolean checkConnectedFast(Map<String, Map<Integer, Set<Integer>>> pnbMapM, Map<String, Map<Integer, List<Set<Integer>>>> visitMapM, Queue<Integer> queue, Set<Integer> deleteSet, ComputeHeterCC computeHeterCC){
        // Check if connected
        Set<Integer> helpSet = new HashSet<Integer>();
        for (int vertexId : vertexSet){
            helpSet.add(vertexId);
        }

        for (int i = 0; i < metaPathList.size(); i++){
            helpSet = computeHeterCC.getCC(metaPathList.get(i), helpSet);
            if (helpSet == null){
                vertexSet = new HashSet<Integer>();
                return false;
            }
        }

        Set<Integer> localDeleteSet = new HashSet<Integer>();
        for (int vertexId : vertexSet){
            if (!helpSet.contains(vertexId)){
                localDeleteSet.add(vertexId);
            }
        }
        if (localDeleteSet.size() != 0){
            for (int vertexId : localDeleteSet){
                if (!deleteSet.contains(vertexId)){
                    queue.add(vertexId);
                    deleteSet.add(vertexId);
                }
            }
            deleteVertices(pnbMapM, visitMapM, queue, deleteSet);
            return false;
        }else {
            return true;
        }
    }

    public void findFirstKNeighbors(int startId, int curId, int index, Map<Integer, List<Set<Integer>>> visitMap, Map<Integer, Set<Integer>> pnbMap, MetaPath queryMPath, Set<Integer> keepSet){
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitMap.get(startId).get(index + 1);
            if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID] && !visitSet.contains(nbVertexID)) {
                if(index + 1 < queryMPath.pathLen) {
                    findFirstKNeighbors(startId, nbVertexID, index + 1, visitMap, pnbMap, queryMPath, keepSet);
                    if(pnbMap.get(startId).size() >= queryK){
                        return;
                    }
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(nbVertexID != startId && keepSet.contains(nbVertexID)){
                        pnbMap.get(startId).add(nbVertexID);
                        pnbMap.get(nbVertexID).add(startId);
                        visitMap.get(nbVertexID).get(queryMPath.pathLen).add(startId);
                    }
                    visitSet.add(nbVertexID);
                    if(pnbMap.get(startId).size() >= queryK){
                        return;
                    }
                }
            }
        }
    }

    public void addMoreNeighbors(int startId, int curId, int index, Map<Integer, List<Set<Integer>>> visitMap, Map<Integer, Set<Integer>> pnbMap,MetaPath queryMPath, Set<Integer> keepSet){
        int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitMap.get(startId).get(index + 1);
            if(!visitSet.contains(nbVertexID) && targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
                if(index + 1 < queryMPath.pathLen) {
                    addMoreNeighbors(startId, nbVertexID, index + 1, visitMap, pnbMap, queryMPath, keepSet);
                    if(pnbMap.get(startId).size() >= queryK){
                        return;
                    }
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(keepSet.contains(nbVertexID)) {//restrict it to be in keepSet
                        if(nbVertexID != startId) {
                            pnbMap.get(startId).add(nbVertexID);
                            pnbMap.get(nbVertexID).add(startId);
                            visitSet.add(nbVertexID);
                            visitMap.get(nbVertexID).get(queryMPath.pathLen).add(startId);
                            if(pnbMap.get(startId).size() >= queryK){
                                return;
                            }
                        }
                    }
                }
                visitSet.add(nbVertexID);//mark this vertex (and its branches) as visited
            }
        }
    }

    public void deleteVertices(Map<String, Map<Integer, Set<Integer>>> pnbMapM, Map<String, Map<Integer, List<Set<Integer>>>> visitMapM, Queue<Integer> queue, Set<Integer> deleteSet){
        while (queue.size() > 0){
            int curId = queue.poll();
            vertexSet.remove(curId);

            for (int i = 0; i < metaPathList.size(); i++){
                MetaPath curMetaPath = metaPathList.get(i);
                Set<Integer> pnbSet = pnbMapM.get(curMetaPath.toString()).get(curId);
                for (int pnbId : pnbSet){
                    if (! deleteSet.contains(pnbId)){
                        Set<Integer> tempSet = pnbMapM.get(curMetaPath.toString()).get(pnbId);
                        tempSet.remove(curId);
                        if (tempSet.size() < queryK){
                            addMoreNeighbors(pnbId, pnbId, 0, visitMapM.get(curMetaPath.toString()), pnbMapM.get(curMetaPath.toString()), curMetaPath, vertexSet);
                            if (tempSet.size() < queryK){
                                queue.add(pnbId);
                                deleteSet.add(pnbId);
                            }
                        }
                    }
                }
            }
        }
    }

}
