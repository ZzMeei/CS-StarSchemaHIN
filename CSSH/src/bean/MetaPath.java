package bean;

import java.util.*;
import java.io.*;

public class MetaPath implements Cloneable,  Serializable{
    public int vertex[];
    public int edge[];
    public int pathLen = -1;

    public MetaPath(int vertex[], int edge[]) {
        this.vertex = vertex;
        this.edge = edge;
        this.pathLen = edge.length;//the number of relations in a meta-path

        if(vertex.length != edge.length + 1) {
            System.out.println("the meta-path is incorrect");
        }
    }

    public MetaPath(String metaPathStr) {
        String s[] = metaPathStr.trim().split(" ");
        this.pathLen = s.length / 2;
        this.vertex = new int[pathLen +1];
        this.edge = new int[pathLen];

        for(int i = 0;i < s.length;i ++) {
            int value = Integer.parseInt(s[i]);
            if(i % 2 == 0) {
                vertex[i / 2] = value;
            }else {
                edge[i / 2] = value;
            }
        }
    }

    public MetaPath(MetaPath mp){ // use a MetaPath object's value to create a new one
        this.vertex = mp.getVertex().clone();
        this.edge = mp.getEdge().clone();
        this.pathLen = edge.length;
    }

    public String toString() {
        String str = "";
        for(int i = 0;i < pathLen;i ++) {
            str += vertex[i] + "-" + edge[i] + "-";
        }
        str += vertex[pathLen];
        return str;
    }

    public int[] getVertex(){
        return this.vertex;
    }

    public int[] getEdge(){
        return this.edge;
    }

    public void addVertexToPath(int index, int node){
        this.vertex[index] = node;
    }

    public void addEdgeToPath(int index, int edge){
        this.edge[index] = edge;
    }

    // get symmetric meta path by given half meta path
    public void symmetricPath(int halfLength, HashMap<Integer, Integer> edgeTypeMap){
        for (int i = 0; i < halfLength; i++){
            this.edge[halfLength + i] = edgeTypeMap.get(this.edge[halfLength - i - 1]);
            this.vertex[halfLength + i + 1] = this.vertex[halfLength - i - 1];
        }
        int edge_[] = new int[halfLength * 2];
        int vertex_[] = new int[halfLength * 2 + 1];
        for (int i = 0; i < halfLength * 2; i++){
            edge_[i] = this.edge[i];
        }
        for (int i = 0; i < halfLength * 2 + 1; i++){
            vertex_[i] = this.vertex[i];
        }
        this.edge = edge_;
        this.vertex = vertex_;
        this.pathLen = this.edge.length;
    }

    // if nested, return true; else return false
    public boolean checkNestMetaPath(MetaPath checkedMetaPath){
        boolean flag = true;
        int halflength = 0;
        int checkedEdge[] = checkedMetaPath.getEdge();
        if (checkedMetaPath.getEdge().length < this.pathLen){
            halflength = checkedMetaPath.getEdge().length / 2;
        }else{
            halflength = this.pathLen / 2;
        }
        for (int i = 0; i < halflength; i++){
            if (checkedEdge[i] != edge[i]){
                flag = false;
                break;
            }
        }
        return flag;
    }
}

