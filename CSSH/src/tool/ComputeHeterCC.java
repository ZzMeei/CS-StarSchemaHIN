package tool;

import bean.*;
import csh.*;

import java.util.*;

public class ComputeHeterCC {

    private ArrayList<Integer> queryIdSet = null;
    private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private Set<Integer> vertexIdSet = null;

    public ComputeHeterCC(ArrayList<Integer> queryIdSet, int graph[][], int vertexType[], int edgeType[]){
        this.queryIdSet = queryIdSet;
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
    };

    public Set<Integer> getCC(MetaPath metaPath, Set<Integer> vertexIdSet){
        this.vertexIdSet = vertexIdSet;
        // compute the connected subgraph via batch-search with labeling (BSL)
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        int queryId = queryIdSet.get(0);

        Set<Integer> keepSet = batchLinker.link(queryId, metaPath, vertexIdSet);

        // judge if connected subgraph contains all vertices in queryIdSet
        for (int vertexId: queryIdSet){
            if (!keepSet.contains(vertexId)){
                return null;
            }
        }

        return keepSet;
    }
}
