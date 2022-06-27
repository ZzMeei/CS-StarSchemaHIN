package baseline;
import bean.*;
import tool.*;

import java.util.*;

// function : GeneMetaPaths

public class BasicGeneCan {
    public BasicGeneCan(){}

    public ArrayList<ArrayList<MetaPath>> geneCanMetaPath(ArrayList<ArrayList<MetaPath>> originalMetaPathList){
        ArrayList<ArrayList<MetaPath>> candidateList = new ArrayList<ArrayList<MetaPath>>();
        HashSet<String> set = new HashSet<String>();
        int size_ = originalMetaPathList.get(0).size();
        AprioriPruner aprioriPruner = new AprioriPruner(originalMetaPathList);
        for (int i = 0; i < originalMetaPathList.size() - 1; i++){
            for (int j = i + 1; j < originalMetaPathList.size(); j++){
                Set<MetaPath> metapathSetA = new HashSet<MetaPath>();
                Set<MetaPath> metapathSetB = new HashSet<MetaPath>();
                for (int k = 0; k < size_; k++){
                    metapathSetA.add(originalMetaPathList.get(i).get(k));
                    metapathSetB.add(originalMetaPathList.get(j).get(k));
                }
                int count = 0;
                for (MetaPath metaPathA : metapathSetA){
                    if (metapathSetB.contains(metaPathA)){
                        count += 1;
                    }
                }
                if (count == size_ - 1){
                    ArrayList<MetaPath> tempMetaPathList = new ArrayList<MetaPath>();
                    for (MetaPath metaPathA: metapathSetA){
                        tempMetaPathList.add(metaPathA);
                    }
                    for (MetaPath metaPathB : metapathSetB){
                        if (!metapathSetA.contains(metaPathB)){
                            tempMetaPathList.add(metaPathB);
                            break;
                        }
                    }
                    if (checkMetaPathSetVaild(tempMetaPathList) && !aprioriPruner.isPruned(tempMetaPathList)){
                        String s = "";
                        Collections.sort(tempMetaPathList, new Comparator<MetaPath>() {
                            public int compare(MetaPath metaPathA, MetaPath metaPathB){
                                return metaPathA.toString().compareTo(metaPathB.toString());
                            }
                        });
                        for (int k = 0; k < tempMetaPathList.size(); k++){
                            s += tempMetaPathList.get(k).toString();
                        }
                        if (!set.contains(s)){
                            candidateList.add(tempMetaPathList);
                            set.add(s);
                        }
                    }
                }
            }
        }
        return candidateList;
    }

    // check if in the same set has meta-paths with nested relations
    public boolean checkMetaPathSetVaild(ArrayList<MetaPath> metaPathList){
        for (int i = 0; i < metaPathList.size() - 1; i++){
            for (int j = i + 1; j < metaPathList.size(); j++){
                if (metaPathList.get(i).checkNestMetaPath(metaPathList.get(j))){
                    return false;
                }
            }
        }
        return true;
    }
}

