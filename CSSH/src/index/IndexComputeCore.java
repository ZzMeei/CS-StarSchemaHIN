package index;

import bean.*;

import java.util.*;

public interface IndexComputeCore {
    public abstract Set<Integer> computeCore(int k, ArrayList<MetaPath> metaPathList, ArrayList<Integer> queryIdSet);
    public abstract Set<Integer> indexQuerySinglePathCore(int k, MetaPath curMetaPath, ArrayList<Integer> queryIdSet);
    public abstract void findAllNeighbors(int startID, int curId, int index, List<Set<Integer>> visitList, Set<Integer> pnbSet, MetaPath queryMPath, Set<Integer> unionSet);

    // test
    public abstract int getFastCoreCounter();
}
