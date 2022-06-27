package baseline;
import bean.*;
import tool.*;
import csh.*;

import java.util.*;

// online query algorithm with intersecting core tech

public class BasicQuery {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;
    private ArrayList<Integer> queryIdSet = null;
    private int queryK = -1;
    private ArrayList<MetaPath> allMetaPathList = null;
    private int schemaGraph[][] = null;
    private HashMap<String, Set<Integer>> communityMap = null;

    public BasicQuery(int graph[][], int vertexType[], int edgeType[], ArrayList<Integer> queryIdSet, int queryK, int schemaGraph[][]){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.queryIdSet = queryIdSet;
        this.queryK = queryK;
        this.schemaGraph = schemaGraph;
        this.communityMap = new HashMap<String, Set<Integer>>();
    }

    public ArrayList<Set<Integer>> query(int l, int targetType){
        // Get all single meta-paths
        MetaPathGenerater metaPathGenerater = new MetaPathGenerater(schemaGraph);
        this.allMetaPathList = metaPathGenerater.generateHalfMetaPath(l, targetType);

        FastBCore fastBCore = new FastBCore(graph, vertexType, edgeType);

        // Initialize the final result meta-path set
        ArrayList<ArrayList<MetaPath>> metaPathListResult = new ArrayList<ArrayList<MetaPath>>();
        for (int i = 0; i < allMetaPathList.size(); i++){
            ArrayList<MetaPath> tempMetaPathList = new ArrayList<MetaPath>();
            tempMetaPathList.add(allMetaPathList.get(i));
            ArrayList<Map<Integer, int[]>> graphList = new ArrayList<Map<Integer, int[]>>();
            for (int j = 0; j < tempMetaPathList.size(); j++){
                Map<Integer, int[]> pnbMap = fastBCore.queryGraphUpdate(queryIdSet, tempMetaPathList.get(j), queryK);
                graphList.add(pnbMap);
            }
            ComputeBCoreLowM computeBCoreLowM = new ComputeBCoreLowM(queryIdSet, queryK, graphList);
            Set<Integer> community = computeBCoreLowM.getBasicKPCore();
            if (community != null){
                communityMap.put(allMetaPathList.get(i).toString(), community);
                metaPathListResult.add(tempMetaPathList);
            }

            graphList = null;
        }
        int count = 0;
        BasicGeneCan basicGeneCan = new BasicGeneCan();
        // Validation and generation
        ArrayList<ArrayList<MetaPath>> allValidatedMetaPathList = new ArrayList<ArrayList<MetaPath>>();
        while (true){
            ArrayList<ArrayList<MetaPath>> tempMetaPathList = new ArrayList<ArrayList<MetaPath>>();
            if (count == 0){
                tempMetaPathList = metaPathListResult;
                count += 1;
            }else{
                for (int i = 0; i < metaPathListResult.size(); i++){
                    // only work when limit the max length of meta-path with 4
                    Collections.sort(metaPathListResult.get(i), new Comparator<MetaPath>() {
                        public int compare(MetaPath o1, MetaPath o2) {
                            return o1.toString().compareTo(o2.toString());
                        }
                    });
                    String MPSStringX = "";
                    String MPSStringY = "";
                    for (int j = 0; j < metaPathListResult.get(i).size() - 1; j++){
                        MPSStringX += metaPathListResult.get(i).get(j).toString();
                    }
                    for (int j = 0; j < metaPathListResult.get(i).size(); j++){
                        if (j != metaPathListResult.get(i).size() - 2){
                            MPSStringY += metaPathListResult.get(i).get(j).toString();
                        }
                    }

                    Set<Integer> unionSet = new HashSet<Integer>();
                    for (int vertexId : communityMap.get(MPSStringX)){
                        if (communityMap.get(MPSStringY).contains(vertexId)){
                            unionSet.add(vertexId);
                        }
                    }

                    ArrayList<Map<Integer, int[]>> graphList = new ArrayList<Map<Integer, int[]>>();
                    for (int j = 0; j < metaPathListResult.get(i).size(); j++){
                        Map<Integer, int[]> pnbMap = fastBCore.queryGraphRestrictUpdate(metaPathListResult.get(i).get(j), unionSet);
                        graphList.add(pnbMap);
                    }

                    ComputeBCoreLowM computeBCoreLowM = new ComputeBCoreLowM(queryIdSet, queryK, graphList);
                    Set<Integer> community = computeBCoreLowM.getBasicKPCore();
                    if (community != null){
                        String MPSString = "";
                        for (int j = 0; j < metaPathListResult.get(i).size(); j++){
                            MPSString += metaPathListResult.get(i).get(j).toString();
                        }
                        communityMap.put(MPSString, community);
                        tempMetaPathList.add(metaPathListResult.get(i));
                    }
                    graphList = null;
                }
            }
            for (int i = 0; i < tempMetaPathList.size(); i++){
                allValidatedMetaPathList.add(tempMetaPathList.get(i));
            }
            if (tempMetaPathList.size() == 0){
                break;
            }
            ArrayList<ArrayList<MetaPath>> tempList = basicGeneCan.geneCanMetaPath(tempMetaPathList);
            if (tempList.size() != 0){
                metaPathListResult = tempList;
            }else{
                metaPathListResult = tempMetaPathList;
                break;
            }
        }
        // Compute valid communities

        ArrayList<ArrayList<MetaPath>> finalMetaPathSet = getValidMetaPathSet(allValidatedMetaPathList);
        ArrayList<Set<Integer>> communityList = new ArrayList<Set<Integer>>();
        for (int i = 0; i < finalMetaPathSet.size(); i++){
            String MPSString = "";
            for (int j = 0; j < finalMetaPathSet.get(i).size(); j++){
                MPSString += finalMetaPathSet.get(i).get(j).toString();
            }
            communityList.add(communityMap.get(MPSString));
        }
        if (finalMetaPathSet.size() != 0){
            System.out.println("The meta-path set : " + finalMetaPathSet.toString());
        }else{
            System.out.println("No valid meta-path set!");
        }
        return communityList;
    }

    public ArrayList<ArrayList<MetaPath>> getValidMetaPathSet(ArrayList<ArrayList<MetaPath>> allValidatedMetaPathList){
        ArrayList<ArrayList<MetaPath>> finalMetaPathSet = new ArrayList<ArrayList<MetaPath>>();
        int maxSize = -1;
        for (int i = 0; i < allValidatedMetaPathList.size(); i++){
            if (maxSize < allValidatedMetaPathList.get(i).size()){
                maxSize = allValidatedMetaPathList.get(i).size();
            }
        }
        int temp = 0;
        for (int i = maxSize; i >= 1; i--){
            if (temp == 1){
                break;
            }
            for (int j = 0; j < allValidatedMetaPathList.size(); j++){
                if (allValidatedMetaPathList.get(j).size() == i){
                    boolean flag = true;
                    for (int k = 0; k < allValidatedMetaPathList.size(); k++){
                        if (allValidatedMetaPathList.get(k).size() <= i && !allValidatedMetaPathList.get(j).toString().equals(allValidatedMetaPathList.get(k).toString())){
                            if (!judgeValid(allValidatedMetaPathList.get(j), allValidatedMetaPathList.get(k))){
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (flag){
                        finalMetaPathSet.add(allValidatedMetaPathList.get(j));
                        temp = 1;
                    }
                }
            }
        }
        return finalMetaPathSet;
    }

    public boolean judgeValid(ArrayList<MetaPath> metaPathSetA, ArrayList<MetaPath> metaPathSetB){
        boolean flagSetA[] = new boolean[metaPathSetA.size()];
        boolean flagSetB[] = new boolean[metaPathSetB.size()];
        for (int i = 0; i < metaPathSetA.size(); i++){
            flagSetA[i] = false;
        }
        for (int i = 0; i < metaPathSetB.size(); i++){
            flagSetB[i] = false;
        }
        for (int i = 0; i < metaPathSetA.size(); i++){
            for (int j = 0; j < metaPathSetB.size(); j++){
                if (metaPathSetA.get(i).checkNestMetaPath(metaPathSetB.get(j) )&& (metaPathSetA.get(i).toString().length() >= metaPathSetB.get(j).toString().length())){
                    flagSetA[i] = true;
                    flagSetB[j] = true;
                }
                if (metaPathSetA.get(i).checkNestMetaPath(metaPathSetB.get(j) )&& (metaPathSetA.get(i).toString().length() < metaPathSetB.get(j).toString().length())){
                    return true;
                }
            }
        }
        for (int i = 0; i < metaPathSetA.size(); i++){
            if (!flagSetA[i]){
                return true;
            }
        }
        for (int i = 0; i < metaPathSetB.size(); i++){
            if (!flagSetB[i]){
                return true;
            }
        }
        return false;
    }
}
