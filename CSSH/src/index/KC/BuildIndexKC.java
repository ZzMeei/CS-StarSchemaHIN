package index.KC;
import bean.*;
import index.*;
import tool.*;
import csh.*;

import java.util.*;

public class BuildIndexKC {
    private int graph[][] = null;
    private int vertexType[] = null;
    private int edgeType[] = null;
    private int schemaGraph[][] = null;
    private ArrayList<MetaPath> allMetaPathList = null;
    private HashMap<String, IndexNode> indexTree = null;

    public BuildIndexKC(int graph[][], int vertexType[], int edgeType[], int schemaGraph[][]){
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.schemaGraph = schemaGraph;
    }

    public HashMap<String, IndexNode> buildIndex(int l, int targetType){
        this.indexTree = new HashMap<String, IndexNode>();
        // Get all single meta-paths
        MetaPathGenerater metaPathGenerater = new MetaPathGenerater(schemaGraph);
        this.allMetaPathList = metaPathGenerater.generateHalfMetaPath(l, targetType);

        indexTree.put("KC", new IndexNode());

        if (targetType != 0){
            // Get the shortest meta-path
            MetaPath shortestMetaPath = allMetaPathList.get(0);
            indexTree.put(shortestMetaPath.toString(), new IndexNode());

            System.out.println("start to build meta-path tree!");
            long t1 = System.nanoTime();
            BuildMetaPathTree(shortestMetaPath, 2);
            long t2 = System.nanoTime();
            System.out.println("Finish building meta-path tree!, cost : " + (t2-t1)/1000000000);
            System.out.println("start to build index nodes!");
            t1 = System.nanoTime();
            BuildIndexNode(shortestMetaPath);
            t2 = System.nanoTime();
            System.out.println("Finish building index nodes!, cost : " + (t2-t1)/1000000000);
        }else {
            for (int i = 0; i < allMetaPathList.size(); i++){
                indexTree.put(allMetaPathList.get(i).toString(), new IndexNode());
                BuildMetaPathTree(allMetaPathList.get(i), 2);
                BuildIndexNode(allMetaPathList.get(i));
            }
        }

        return indexTree;
    }

    public void BuildMetaPathTree(MetaPath curMetaPath, int curLength){
        curLength = curLength + 2;
        for (int i = 0; i < allMetaPathList.size(); i++){
            if (allMetaPathList.get(i).getEdge().length == curLength){
                if (curMetaPath.checkNestMetaPath(allMetaPathList.get(i))){
                    MetaPath metaPath = allMetaPathList.get(i);
                    IndexNode indexNode = new IndexNode();
                    indexTree.get(curMetaPath.toString()).addChild(metaPath);
                    indexNode.setFather(curMetaPath);
                    indexTree.put(metaPath.toString(), indexNode);
                    BuildMetaPathTree(metaPath, curLength);
                }
            }
        }
    }

    public void BuildIndexNode(MetaPath curMetaPath){
        System.out.println("Build the index node of " + curMetaPath.toString());
        CoreDecomposition coreDecomposition = new CoreDecomposition(graph, vertexType, edgeType);
        Map<Integer, Integer> reverseOrderArr = coreDecomposition.decompose(curMetaPath);

        for (int vertexId: reverseOrderArr.keySet()){
            int k = reverseOrderArr.get(vertexId);
            indexTree.get(curMetaPath.toString()).addVertex(k, vertexId);
        }

        for (MetaPath metaPath: indexTree.get(curMetaPath.toString()).getChildList()){
            BuildIndexNode(metaPath);
        }
    }
}
