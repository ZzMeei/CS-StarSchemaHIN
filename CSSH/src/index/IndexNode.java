package index;
import bean.*;

import java.util.*;

public class IndexNode {
    private MetaPath father = null;
    private ArrayList<MetaPath> childList = null;
    private HashMap<Integer, HashSet<Integer>> map = null;
    private HashMap<Integer, Integer> numberMap = null; // for test

    public IndexNode(){
        this.childList = new ArrayList<MetaPath>();
        this.map = new HashMap<Integer, HashSet<Integer>>();
        this.numberMap = new HashMap<Integer, Integer>();
    }

    public MetaPath getFather() {
        return father;
    }

    public void setFather(MetaPath father) {
        this.father = father;
    }

    public ArrayList<MetaPath> getChildList() {
        return childList;
    }

    public HashMap<Integer, HashSet<Integer>> getMap() {
        return map;
    }

    public void addChild(MetaPath metaPath){
        this.childList.add(metaPath);
    }

    public void addVertex(int k, int vertexId){
        if (!map.containsKey(k)){
            HashSet<Integer> vertexSet = new HashSet<Integer>();
            vertexSet.add(vertexId);
            map.put(k, vertexSet);
        }else{
            map.get(k).add(vertexId);
        }
    }

    public HashSet<Integer> getVertexSet(int k){
        if (!map.containsKey(k)){
            return new HashSet<Integer>();
        }else{
            return map.get(k);
        }
    }

    public Integer getMaxK(){
        int maxK = -1;
        for (int k: map.keySet()){
            if (k > maxK){
                maxK = k;
            }
        }
        return maxK;
    }

    // for test
    public void addVertexNumber(int k){
        if (!numberMap.containsKey(k)){
            Integer count = 1;
            numberMap.put(k, count);
        }else {
           numberMap.put(k, numberMap.get(k) + 1);
        }
    }

    // for test
    public HashMap<Integer, Integer> getNumberMap() {
        return numberMap;
    }
}
