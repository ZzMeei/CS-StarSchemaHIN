package tool;
import bean.*;

import java.util.*;

// try to implement core decomposition without building homogeneous graphs, fail by too expensive time cost

public class CoreDecompositionLowMemory {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;

    public CoreDecompositionLowMemory(int graph[][], int vertexType[], int edgeType[]){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
    }

    public Map<Integer, Integer> decompose(MetaPath metaPath){
        HashMap<Integer, Integer> vertexCoreMap = new HashMap<Integer, Integer>();
        // init map
        int STARTTYPE = metaPath.vertex[0];
        Set<Integer> keepSet = new HashSet<Integer>();
        for(int i = 0;i < vertexType.length;i ++) {
            if(vertexType[i] == STARTTYPE) {
                keepSet.add(i);
            }
        }

        HashMap<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>(); // a vertex -> its pnbs
        HashMap<Integer, List<Set<Integer>>> visitMap = new HashMap<Integer, List<Set<Integer>>>(); // a vertex -> its visited vertices

        Set<Integer> deleteSet = new HashSet<Integer>();

        for (int vertexId : keepSet){
            List<Set<Integer>> visitList = new ArrayList<Set<Integer>>();
            for (int i = 0; i <= metaPath.pathLen; i++){
                visitList.add(new HashSet<Integer>());
            }
            visitMap.put(vertexId, visitList);
            pnbMap.put(vertexId, new HashSet<Integer>());
        }

        long t1 = System.nanoTime();

        long time_count = 0;

        // find k-core
        int k = 0;
        while (true){
            k = k + 1;
            Queue<Integer> queue = new LinkedList<Integer>();
            for (int vertexId : keepSet){
                Set<Integer> nbSet = pnbMap.get(vertexId);
                long temp_time1 = System.nanoTime();
                addMoreNeighbors(vertexId, vertexId, 0, visitMap.get(vertexId), nbSet, metaPath, keepSet, k);
                long temp_time2 = System.nanoTime();
                time_count = time_count + temp_time2 - temp_time1;
                if (nbSet.size() < k){
                    queue.add(vertexId);
                    deleteSet.add(vertexId);
                }
            }

            while (queue.size() > 0){
                int curId = queue.poll();
                keepSet.remove(curId);
                vertexCoreMap.put(curId, k - 1);
                Set<Integer> nbSet = pnbMap.get(curId);
                pnbMap.put(curId, null); // release the memory
                visitMap.put(curId, null); // release the memory
                for (int nbId : nbSet){
                    if (!deleteSet.contains(nbId)){
                        Set <Integer> tempSet = pnbMap.get(nbId);
                        tempSet.remove(curId);
                        if (tempSet.size() < k){
                            long temp_time1 = System.nanoTime();
                            addMoreNeighbors(nbId, nbId, 0, visitMap.get(nbId), tempSet, metaPath, keepSet, k);
                            long temp_time2 = System.nanoTime();
                            time_count = time_count + temp_time2 - temp_time1;
                            if (tempSet.size() < k){
                                queue.add(nbId);
                                deleteSet.add(nbId);
                            }
                        }
                    }
                }
            }

            if (keepSet.size() == 0){
                break;
            }
        }

        long t2 = System.nanoTime();
        System.out.println("Cost time of core decomposition : " + (t2-t1)/1000000000);
        System.out.println("Cost time of finding neighbors : " + time_count/1000000000);

        return vertexCoreMap;
    }

    public Map<Integer, Integer> decomposeNew(MetaPath metaPath){
        HashMap<Integer, Integer> vertexCoreMap = new HashMap<Integer, Integer>();
        // init map
        int STARTTYPE = metaPath.vertex[0];
        Set<Integer> keepSet = new HashSet<Integer>();
        for(int i = 0;i < vertexType.length;i ++) {
            if(vertexType[i] == STARTTYPE) {
                keepSet.add(i);
            }
        }

        HashMap<Integer, Set<Integer>> helpMap = new HashMap<Integer, Set<Integer>>();
        for (int i = 0; i < vertexType.length; i++){
            if (!helpMap.keySet().contains(vertexType[i])){
                Set<Integer> helpSet = new HashSet<Integer>();
                helpMap.put(vertexType[i], helpSet);
            }
            helpMap.get(vertexType[i]).add(i);
        }
        List<Integer> typeList = new ArrayList<Integer>();
        for (int typeIndex : helpMap.keySet()){
            typeList.add(typeIndex);
        }

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> typeRestrictMap = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();

        // rebuild graph
        for (int i = 0; i < graph.length; i++){
            HashMap<Integer, HashMap<Integer, Integer>> tempMap = new HashMap<Integer, HashMap<Integer, Integer>>();
            Map<Integer, Set<Integer>> countMap = new HashMap<Integer, Set<Integer>>();
            for (int typeIndex : helpMap.keySet()){
                countMap.put(typeIndex, new HashSet<Integer>());
            }
            for (int j = 0; j < graph[i].length; j+=2){
                countMap.get(vertexType[graph[i][j]]).add(j);
            }
            int arr[] = new int[graph[i].length];
            int counter = 0;
            for (int j = 0; j < typeList.size(); j++){
                HashMap<Integer, Integer> ttempMap = new HashMap<Integer, Integer>();
                ttempMap.put(0, counter);
                int typeIndex = typeList.get(j);
                for (int vertexIndex : countMap.get(typeIndex)){
                    arr[counter] = graph[i][vertexIndex];
                    arr[counter + 1] = graph[i][vertexIndex+1];
                    counter += 2;
                }
                ttempMap.put(1, counter);
                tempMap.put(typeIndex, ttempMap);
            }
            graph[i] = arr;
            typeRestrictMap.put(i, tempMap);
        }

        HashMap<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>(); // a vertex -> its pnbs
        HashMap<Integer, Map<Integer, Map<Integer, Integer>>> visitIndexMap = new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>();

        Set<Integer> deleteSet = new HashSet<Integer>();

        for (int vertexId : keepSet){
            Map<Integer, Map<Integer, Integer>> visitIndex = new HashMap<Integer, Map<Integer, Integer>>();
            for (int i = 0; i <= metaPath.pathLen; i++){
                visitIndex.put(i, new HashMap<Integer, Integer>());
            }
            visitIndexMap.put(vertexId, visitIndex);
            pnbMap.put(vertexId, new HashSet<Integer>());
        }

        long t1 = System.nanoTime();

        long time_count = 0;

        // find k-core
        int k = 0;
        while (true){
            k = k + 1;
            Queue<Integer> queue = new LinkedList<Integer>();
            for (int vertexId : keepSet){
                Set<Integer> nbSet = pnbMap.get(vertexId);
                long temp_time1 = System.nanoTime();
                addMoreNeighborsNew(vertexId, vertexId, 0, visitIndexMap.get(vertexId), nbSet, metaPath, keepSet, k, typeRestrictMap);
                long temp_time2 = System.nanoTime();
                time_count = time_count + temp_time2 - temp_time1;
                if (nbSet.size() < k){
                    queue.add(vertexId);
                    deleteSet.add(vertexId);
                }
            }

            HashSet<Integer> helpSet = new HashSet<Integer>();

            while (queue.size() > 0){
                int curId = queue.poll();
                keepSet.remove(curId);
                vertexCoreMap.put(curId, k - 1);
                Set<Integer> nbSet = pnbMap.get(curId);
                pnbMap.put(curId, null);
                visitIndexMap.put(curId, null);
                for (int nbId : nbSet){
                    if (!deleteSet.contains(nbId)){
                        Set<Integer> tempSet = pnbMap.get(nbId);
                        tempSet.remove(curId);
                        helpSet.add(nbId);
                    }
                }
                if (queue.size() == 0){
                    for (int vertexId : helpSet){
                        Set<Integer> tempSet = pnbMap.get(vertexId);
                        if (tempSet.size() < k){
                            long temp_time1 = System.nanoTime();
                            addMoreNeighborsNew(vertexId, vertexId, 0, visitIndexMap.get(vertexId), tempSet, metaPath, keepSet, k, typeRestrictMap);
                            long temp_time2 = System.nanoTime();
                            time_count = time_count + temp_time2 - temp_time1;
                            if (tempSet.size() < k){
                                queue.add(vertexId);
                                deleteSet.add(vertexId);
                            }
                        }
                    }
                    helpSet = new HashSet<Integer>();
                }
            }

            if (keepSet.size() == 0){
                break;
            }
        }

        long t2 = System.nanoTime();
        System.out.println("Cost time of core decomposition : " + (t2-t1)/1000000000);
        System.out.println("Cost time of finding neighbors : " + time_count/1000000000);

        return vertexCoreMap;
    }

    private void addMoreNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet, MetaPath metaPath, Set<Integer> keepSet, int k) {
        int targetVType = metaPath.vertex[index + 1], targetEType = metaPath.edge[index];

        int nbArr[] = graph[curId];
        for(int i = 0;i < nbArr.length;i += 2) {
            int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
            Set<Integer> visitSet = visitList.get(index + 1);
            if(!visitSet.contains(nbVertexID) && targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
                if(index + 1 < metaPath.pathLen) {
                    addMoreNeighbors(startID, nbVertexID, index + 1, visitList, pnbSet, metaPath, keepSet, k);
                    if(pnbSet.size() >= k)   return ;//we have found a new meta-path
                    visitSet.add(nbVertexID);
                }else {//a meta-path has been found
                    if(keepSet.contains(nbVertexID)) {//restrict it to be in keepSet
                        if(nbVertexID != startID) {
                            pnbSet.add(nbVertexID);
                            visitSet.add(nbVertexID);
                            if(pnbSet.size() >= k)   return ;//we have found a new meta-path
                        }
                    }
                }
                visitSet.add(nbVertexID);//mark this vertex (and its branches) as visited
            }
        }
    }

    private void addMoreNeighborsNew(int startID, int curId, int index, Map<Integer, Map<Integer, Integer>> visitIndex, Set<Integer> pnbSet, MetaPath metaPath, Set<Integer> keepSet, int k, HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> typeRestrictMap) {
        int targetVType = metaPath.vertex[index + 1];
        int nbArr[] = graph[curId];
        Map<Integer, Integer> visitMap = visitIndex.get(index + 1);
        if (!visitMap.keySet().contains(curId)){ // not be visited before
            visitMap.put(curId, typeRestrictMap.get(curId).get(targetVType).get(0));
        }
        int index_start = visitMap.get(curId), index_end = typeRestrictMap.get(curId).get(targetVType).get(1);
        for(int i = index_start;i < index_end;i += 2) {
            int nbVertexID = nbArr[i];
            if(index + 1 < metaPath.pathLen) {
                addMoreNeighborsNew(startID, nbVertexID, index + 1, visitIndex, pnbSet, metaPath, keepSet, k, typeRestrictMap);
                if(pnbSet.size() >= k){
                    return;
                }
                visitMap.put(curId, visitMap.get(curId) + 2);
            }else {//a meta-path has been found
                if(keepSet.contains(nbVertexID)) {//restrict it to be in keepSet
                    if(nbVertexID != startID) {
                        pnbSet.add(nbVertexID);
                        if(pnbSet.size() >= k){
                            visitMap.put(curId, visitMap.get(curId) + 2);
                            return;
                        }
                    }
                }
                visitMap.put(curId, visitMap.get(curId) + 2);
            }
        }
    }
}
