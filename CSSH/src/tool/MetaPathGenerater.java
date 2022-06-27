package tool;
import bean.*;

import java.util.*;

public class MetaPathGenerater {
    int schemaGraph[][];

    public MetaPathGenerater(int[][] schemaGraph){
        this.schemaGraph = schemaGraph;
    }

    public ArrayList<MetaPath> generateHalfMetaPath(int l, int targetType){
        ArrayList<MetaPath> S = new ArrayList<MetaPath>();
        ArrayList<MetaPath> X = new ArrayList<MetaPath>();
        HashMap<Integer, Integer> edgeTypeMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < schemaGraph.length; i++){
            for (int j = 0; j < schemaGraph[i].length; j++){
                if (j % 2 == 0){
                    for (int k = 0; k < schemaGraph[schemaGraph[i][j]].length; k++){
                        if (k % 2 == 0){
                            if (schemaGraph[schemaGraph[i][j]][k] == i){
                                edgeTypeMap.put(schemaGraph[i][j+1], schemaGraph[schemaGraph[i][j]][k+1]);
                            }
                        }
                    }
                }
            }
        }
        int metaPathEdges[] = new int[l];
        int metaPathVertices[] = new int[l+1];
        metaPathVertices[0] = targetType;
        S.add(new MetaPath(metaPathVertices, metaPathEdges));
        for (int i = 0; i < l / 2; i++){
            ArrayList<MetaPath> sTemp = new ArrayList<MetaPath>();
            for (int j = 0; j < S.size(); j++){
                MetaPath mp = S.get(j);
                int lastNode = (mp.getVertex())[i];
                for (int k = 0; k < schemaGraph[lastNode].length; k++){
                    if (k % 2 == 0 && schemaGraph[lastNode][k] != targetType){ // ignore the meta-path like 'APAPAPA'
                        // if (k % 2 == 0){ // not ignore the meta-path like 'APAPAPA'
                        MetaPath mpTemp = new MetaPath(mp);
                        mpTemp.addVertexToPath(i+1, schemaGraph[lastNode][k]);
                        mpTemp.addEdgeToPath(i, schemaGraph[lastNode][k+1]);
                        sTemp.add(mpTemp);
                    }
                }
            }
            for (int j = 0; j < sTemp.size(); j++){
                MetaPath mp = new MetaPath(sTemp.get(j));
                mp.symmetricPath(i+1, edgeTypeMap);
                X.add(mp);
            }
            S = sTemp;
        }
        return X;
    }
}
