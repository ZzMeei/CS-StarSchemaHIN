package csh;
import bean.*;

import java.util.*;

public class BatchSearch {
    private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private MetaPath queryMPath = null;

    public BatchSearch(int graph[][], int vertexType[], int edgeType[], MetaPath queryMPath) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.queryMPath = queryMPath;
    }

    public Set<Integer> collect(int startId, Set<Integer> keepSet) {
        Set<Integer> anchorSet = new HashSet<Integer>();
        anchorSet.add(startId);

        for(int layer = 0;layer < queryMPath.pathLen;layer ++) {
            int targetVType = queryMPath.vertex[layer + 1], targetEType = queryMPath.edge[layer];

            Set<Integer> nextAnchorSet = new HashSet<Integer>();
            for(int anchorId:anchorSet) {
                int nb[] = graph[anchorId];
                for(int i = 0;i < nb.length;i += 2) {
                    int nbVertexID = nb[i], nbEdgeID = nb[i + 1];
                    if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
                        if(layer < queryMPath.pathLen - 1) {
                            nextAnchorSet.add(nbVertexID);
                        }else {
                            if(keepSet.contains(nbVertexID))   nextAnchorSet.add(nbVertexID);//impose restriction
                        }
                    }
                }
            }
            anchorSet = nextAnchorSet;
        }

        anchorSet.remove(startId);
        return anchorSet;
    }
}
