package tool;
import bean.*;

import java.util.*;

public class AprioriPruner {
    private Set<String> concatedMPSSet = null;

    public AprioriPruner(ArrayList<ArrayList<MetaPath>> validMPSList){
        this.concatedMPSSet = new HashSet<String>();

        for (ArrayList<MetaPath> MPS:validMPSList){
            Collections.sort(MPS, new Comparator<MetaPath>() {
                public int compare(MetaPath metaPathA, MetaPath metaPathB){
                    return metaPathA.toString().compareTo(metaPathB.toString());
                }
            });
            String MPSString = "";
            for (int i = 0; i < MPS.size(); i++){
                MPSString += MPS.get(i).toString();
            }
            concatedMPSSet.add(MPSString);
        }
    }

    public boolean isPruned(ArrayList<MetaPath> canMPSList){
        if (canMPSList.size() <= 2){
            return false;
        }
        Collections.sort(canMPSList, new Comparator<MetaPath>() {
            public int compare(MetaPath metaPathA, MetaPath metaPathB){
                return metaPathA.toString().compareTo(metaPathB.toString());
            }
        });
        for (int i = 0; i < canMPSList.size(); i++){
            String MPSString = "";
            for (int j = 0; j < canMPSList.size(); j++){
                if (i != j){
                    MPSString += canMPSList.get(j).toString();
                }
            }
            if (!concatedMPSSet.contains(MPSString)){
                return true;
            }
        }
        return false;
    }
}
