package bean;

import java.util.*;

public class MetaPathSet {
    private int sum = 0; // the sum of evey meta path length in this set
    private ArrayList<MetaPath> metapathSet = null;

    public MetaPathSet(ArrayList<MetaPath> metapathSet){
        this.metapathSet = metapathSet;
        for (int i = 0; i < metapathSet.size(); i++){
            sum += metapathSet.get(i).getEdge().length;
        }
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public ArrayList<MetaPath> getMetapathSet() {
        return metapathSet;
    }

    public void setMetapathSet(ArrayList<MetaPath> metapathSet) {
        this.metapathSet = metapathSet;
    }
}
