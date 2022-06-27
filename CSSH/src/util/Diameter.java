package util;

import java.util.*;

public class Diameter {
    private Map<Integer, Set<Integer>> graphMap = null;
    private Set<Integer> kcoreSet = null;
    private int roundThreshold = 10;

    public Diameter(Map<Integer, Set<Integer>> graphMap, Set<Integer> kcoreSet) {
        this.graphMap = graphMap;
        this.kcoreSet = kcoreSet;
    }

    public int computeDiameter() {
        int diameter = 0, round = 0;
        for(int vertexId:kcoreSet) {
            int farthest = computeFarthest(vertexId);
            if(farthest > diameter) diameter = farthest;

            round ++;
            if(round >= roundThreshold) break;
        }

        return diameter;
    }

    //compute BFS-based hop number
    private int computeFarthest(int startId) {
        Set<Integer> visitSet = new HashSet<Integer>();
        Set<Integer> set = new HashSet<Integer>();
        set.add(startId);
        visitSet.add(startId);

        int hop = 0;
        while(true) {
            Set<Integer> nextSet = new HashSet<Integer>();
            for(int vertexId:set) {
                for(int nbId:graphMap.get(vertexId)) {//enumerate curId's neighbors
                    if(!visitSet.contains(nbId) && kcoreSet.contains(nbId)) {
                        nextSet.add(nbId);
                        visitSet.add(nbId);
                    }
                }
            }

            if(nextSet.size() == 0) {
                break;
            }else {
                hop ++;
                set = nextSet;
            }
        }
        return hop;
    }
}
