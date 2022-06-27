package index;
import bean.*;
import index.KC.IndexComputeCoreKC;
import index.MC.IndexComputeCoreMC;
import index.MKC.IndexComputeCoreMKC;
import tool.*;

import java.util.*;

public class IndexBasedQuery {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;
    private int schemaGraph[][] = null;
    private ArrayList<MetaPath> allMetaPathList = null;
    private HashMap<String, IndexNode> indexTree = null;

    // exp
    private float averageMetaPathLength = 0;
    private float validMetaPathSetSize = 0;
    private float computeCoreCounter = 0;
    private float fastCoreCounter = 0;

    public IndexBasedQuery(int graph[][], int vertexType[], int edgeType[], int schemaGraph[][], HashMap<String, IndexNode> indexTree){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.schemaGraph = schemaGraph;
        this.indexTree = indexTree;
    }

    public ArrayList<Set<Integer>> query(int l, int targetType, int queryK, ArrayList<Integer> queryIdSet){
        MetaPathGenerater metaPathGenerater = new MetaPathGenerater(schemaGraph);
        this.allMetaPathList = metaPathGenerater.generateHalfMetaPath(l, targetType); // generate all single meta-paths
        Map<String, Set<Integer>> communityMap = new HashMap<String, Set<Integer>>();

        IndexComputeCore indexComputeCore = null;
        if (indexTree.keySet().contains("MKC")){
            indexComputeCore = new IndexComputeCoreMKC(graph, vertexType, edgeType, indexTree);
        }
        if (indexTree.keySet().contains("MC")){
            indexComputeCore = new IndexComputeCoreMC(graph, vertexType, edgeType, indexTree);
        }
        if (indexTree.keySet().contains("KC")){
            indexComputeCore = new IndexComputeCoreKC(graph, vertexType, edgeType, indexTree);
        }

        // init X array by single meta-path
        ArrayList<MetaPathSet> X = new ArrayList<MetaPathSet>();
        for (int i = 0; i < allMetaPathList.size(); i++){
            ArrayList<MetaPath> tempSet = new ArrayList<MetaPath>();
            tempSet.add(allMetaPathList.get(i));
            MetaPathSet metaPathSet = new MetaPathSet(tempSet);
            X.add(metaPathSet);
        }

        boolean if_first_loop = true;

        ArrayList<ArrayList<MetaPath>> fatherNestedMetaPathList = null;
        ArrayList<ArrayList<MetaPath>> historyX = null;
        while (true){
            ArrayList<ArrayList<MetaPath>> tempX = new ArrayList<ArrayList<MetaPath>>();

            // sort X by meta-path sets' sum
            Collections.sort(X, new Comparator<MetaPathSet>() {
                public int compare(MetaPathSet metaPathSetA, MetaPathSet metaPathSetB){
                    if (metaPathSetA.getSum() > metaPathSetB.getSum()){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            });

            Set<ArrayList<MetaPath>> S = new HashSet<ArrayList<MetaPath>>();
            for (int i = 0; i < X.size(); i++){
                S.add(X.get(i).getMetapathSet());
            }

            ArrayList<MetaPath> metaPaths = X.get(0).getMetapathSet();
            boolean if_father = false;
            if (X.size() >= 2){
                if (X.get(0).getSum() < X.get(1).getSum()){
                    if_father = true;
                }
            }else {
                if_father = true;
            }

            while (S.size() > 0){
                S.remove(metaPaths);
                Set<Integer> community = indexComputeCore.computeCore(queryK, metaPaths, queryIdSet);
                computeCoreCounter += 1;
                if (community != null && community.size() != 0){ // has valid community
                    String tempString = "";
                    for (int i = 0; i < metaPaths.size(); i++){
                        tempString += metaPaths.get(i).toString();
                    }
                    communityMap.put(tempString, community);
                    if (if_father){ // If the father meta-paths set is valid, then all other candidates are valid, the final result has gotten
                        MetaPathSet metaPathSet = new MetaPathSet(metaPaths);
                        X = new ArrayList<MetaPathSet>();
                        X.add(metaPathSet);
                        break;
                    }
                    Set<ArrayList<MetaPath>> validCan = geneValidCan(metaPaths); // use meta-paths set nested relation to get valid candidates
                    for (ArrayList<MetaPath> metapathsV : validCan){
                        tempX.add(metapathsV);
                        // remove metapathsI from S
                        HashSet<String> helpSet = new HashSet<String>();
                        for (int i = 0; i < metapathsV.size(); i++){
                            helpSet.add(metapathsV.get(i).toString());
                        }
                        for (ArrayList<MetaPath> metaPathsS : S){
                            boolean flag = true;
                            for (int i = 0; i < metaPathsS.size(); i++){
                                if (!helpSet.contains(metaPathsS.get(i).toString())){
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag){
                                S.remove(metaPathsS);
                                break;
                            }
                        }
                    }
                }
                if_father = false; // only valid for the first loop
                // get next candidate for next loop to verify
                for (int i = 0; i < X.size(); i++){
                    if (S.contains(X.get(i).getMetapathSet())){
                        metaPaths = X.get(i).getMetapathSet();
                        break;
                    }
                }
            }
            // add valid candidates that can not be generated by above progress
            if (fatherNestedMetaPathList != null){
                for (int i = 0; i < fatherNestedMetaPathList.size(); i++){
                    tempX.add(fatherNestedMetaPathList.get(i));
                }
            }
            if (tempX.size() == 0){ // does not find valid candidates of the meta-path set size
                if (if_father){ // if_father -> the first candidate valid ;
                    break;
                }else if (! if_first_loop){
                    X = new ArrayList<MetaPathSet>(); // use history to update X
                    for (int i = 0; i < historyX.size(); i++){
                        MetaPathSet metaPathSet = new MetaPathSet(historyX.get(i));
                        X.add(metaPathSet);
                    }
                    break;
                }else {
                    System.out.println("No valid meta-path!");
                    return null;
                }
            }
            historyX = tempX; // record as history
            IndexGeneCan indexGeneCan = new IndexGeneCan();
            ArrayList<ArrayList<MetaPath>> validMetaPathList = new ArrayList<ArrayList<MetaPath>>();
            ArrayList<ArrayList<MetaPath>> newX = indexGeneCan.geneCanMetaPath(tempX, validMetaPathList);
            fatherNestedMetaPathList = validMetaPathList;
            X = new ArrayList<MetaPathSet>(); // update X
            if (newX.size() > 0){ // has new candidates
                for (int i = 0; i < newX.size(); i++){
                    MetaPathSet metaPathSet = new MetaPathSet(newX.get(i));
                    X.add(metaPathSet);
                }
            }else { // does not have new candidates
                for (int i = 0; i < tempX.size(); i++){
                    MetaPathSet metaPathSet = new MetaPathSet(tempX.get(i));
                    X.add(metaPathSet);
                }
                break;
            }
            if_first_loop = false;
        }
        ArrayList<Set<Integer>> communityList = new ArrayList<Set<Integer>>();
        ArrayList<ArrayList<MetaPath>> finalResultMetaPathSets = getValidMetaPathSet(X);
        for (int i = 0; i < finalResultMetaPathSets.size(); i++){
            int tempLength = 0;
            System.out.print("Result meta-path set " + i + ": ");
            for (int j = 0; j < finalResultMetaPathSets.get(i).size(); j++){
                System.out.print(" " + finalResultMetaPathSets.get(i).get(j).toString());
                tempLength += finalResultMetaPathSets.get(i).get(j).pathLen;
            }
            tempLength = tempLength / finalResultMetaPathSets.get(i).size();
            averageMetaPathLength += tempLength;
            System.out.println();
            String tempString = "";
            for (int j = 0; j < finalResultMetaPathSets.get(i).size(); j++){
                tempString += finalResultMetaPathSets.get(i).get(j).toString();
            }
            Set<Integer> community = null;
            if (communityMap.keySet().contains(tempString)){
                community = communityMap.get(tempString);
            }else {
                community = indexComputeCore.computeCore(queryK, finalResultMetaPathSets.get(i), queryIdSet);
            }
            communityList.add(community);
        }
        averageMetaPathLength = averageMetaPathLength / (float) finalResultMetaPathSets.size();
        // exp
        for (int i = 0; i < finalResultMetaPathSets.size(); i++){
            int tempSize = 0;
            for (int j = 0; j < finalResultMetaPathSets.get(i).size(); j++){
                Queue<MetaPath> queue = new LinkedList<MetaPath>();
                queue.add(finalResultMetaPathSets.get(i).get(j));
                while (queue.size() > 0){
                    MetaPath curMetaPath = queue.poll();
                    tempSize += 1;
                    for (MetaPath MPath : indexTree.get(curMetaPath.toString()).getChildList()){
                        queue.add(MPath);
                    }
                }
            }
            validMetaPathSetSize += tempSize;
        }
        validMetaPathSetSize = validMetaPathSetSize / (float) finalResultMetaPathSets.size();
        fastCoreCounter = indexComputeCore.getFastCoreCounter();
        return communityList;
    }

    // exp
    public float getAverageMetaPathLength(){
        return this.averageMetaPathLength;
    }

    // exp
    public float getValidMetaPathSetSize(){
        return this.validMetaPathSetSize;
    }

    // exp
    public float getComputeCoreCounter(){
        return this.computeCoreCounter;
    }

    // exp
    public float getFastCoreCounter(){
        this.fastCoreCounter = (float) fastCoreCounter;
        return this.fastCoreCounter;
    }

    public Set<ArrayList<MetaPath>> geneValidCan(ArrayList<MetaPath> metapaths){
        Set<ArrayList<MetaPath>> validCan = new HashSet<ArrayList<MetaPath>>();
        Set<MetaPath> visit = new HashSet<MetaPath>();
        // get every valid meta-path's nested meta-paths
        HashMap<String, ArrayList<MetaPath>> map = new HashMap<String, ArrayList<MetaPath>>(); // meta-path -> its nested meta-paths (longer)
        for (int i = 0; i < metapaths.size(); i++){
            ArrayList<MetaPath> emptyList = new ArrayList<MetaPath>();
            map.put(metapaths.get(i).toString(), emptyList);
            LinkedList<MetaPath> queue = new LinkedList<MetaPath>();
            queue.add(metapaths.get(i));
            while(queue.size() > 0){
                MetaPath metaPath = queue.removeFirst();
                map.get(metapaths.get(i).toString()).add(metaPath);
                for (MetaPath tempMetapath: indexTree.get(metaPath.toString()).getChildList()){
                    queue.add(tempMetapath);
                }
            }
        }
        geneCombination(metapaths, new ArrayList<MetaPath>(), validCan, visit, map);
        return validCan;
    }

    // combine every meta-path's nested meta-paths
    public void geneCombination(ArrayList<MetaPath> metapathArr, ArrayList<MetaPath> metapathD, Set<ArrayList<MetaPath>> validCan, Set<MetaPath> visit, HashMap<String, ArrayList<MetaPath>> map){
        for (int i = 0 ; i < metapathArr.size(); i++){
            if (!visit.contains(metapathArr.get(i))){
                visit.add(metapathArr.get(i));
                for (int j = 0; j < map.get(metapathArr.get(i).toString()).size(); j++){
                    MetaPath tempMetaPath = map.get(metapathArr.get(i).toString()).get(j);
                    metapathD.add(tempMetaPath);
                    if (metapathArr.size() == metapathD.size()){
                        ArrayList<MetaPath> metapathT = new ArrayList<MetaPath>();
                        for (int k = 0; k < metapathD.size() ; k++){
                            metapathT.add(metapathD.get(k));
                        }
                        validCan.add(metapathT);
                        metapathD.remove(tempMetaPath);
                    }else{
                        geneCombination(metapathArr, metapathD, validCan, visit, map);
                    }
                }
                visit.remove(metapathArr.get(i));
            }
        }
    }

    // delete nested meta-paths set from all valid sets
    public ArrayList<ArrayList<MetaPath>> getValidMetaPathSet(ArrayList<MetaPathSet> X){
        ArrayList<ArrayList<MetaPath>> finalMetaPathSet = new ArrayList<ArrayList<MetaPath>>();
        Collections.sort(X, new Comparator<MetaPathSet>() {
            public int compare(MetaPathSet metaPathSetA, MetaPathSet metaPathSetB){
                if (metaPathSetA.getSum() > metaPathSetB.getSum()){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        Set<ArrayList<MetaPath>> tempS = new HashSet<ArrayList<MetaPath>>();
        for (int i = 0; i < X.size(); i++){
            tempS.add(X.get(i).getMetapathSet());
        }
        for (int i = 0; i < X.size(); i++){
            ArrayList<MetaPath> tempMetaPaths = X.get(i).getMetapathSet();
            if (tempS.contains(tempMetaPaths)){
                Set<ArrayList<MetaPath>> validCan = geneValidCan(X.get(i).getMetapathSet());
                for (ArrayList<MetaPath> metapaths : validCan){
                    // tempS.remove(metapaths);
                    HashSet<String> tSet = new HashSet<String>();
                    for (int j = 0; j < metapaths.size(); j++){
                        tSet.add(metapaths.get(j).toString());
                    }
                    for (ArrayList<MetaPath> metapathss: tempS){
                        int tt = 1;
                        for (int m = 0; m < metapathss.size(); m++){
                            if (!tSet.contains(metapathss.get(m).toString())){
                                tt = 0;
                                break;
                            }
                        }
                        if (tt == 1){
                            tempS.remove(metapathss);
                            break;
                        }
                    }
                }
                finalMetaPathSet.add(X.get(i).getMetapathSet());
            }
        }
        return finalMetaPathSet;
    }
}
