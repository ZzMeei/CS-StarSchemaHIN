package util;

import java.io.*;
import java.util.*;

public class ReadQuery {

    public ReadQuery(){};

    public ArrayList<ArrayList<Integer>> readFile(String filePath){
        ArrayList<ArrayList<Integer>> querySetList = new ArrayList<ArrayList<Integer>>();
        try {
            BufferedReader stdin = new BufferedReader(new FileReader(filePath));
            String line = null;
            while((line = stdin.readLine()) != null){
                ArrayList<Integer> querySet = new ArrayList<Integer>();
                int index_start = 0;
                int index_end = 0;
                for (int i = 0; i < line.length(); i++){
                    if (line.charAt(i) == ' '){
                        index_end = i;
                        querySet.add(Integer.parseInt(line.substring(index_start, index_end)));
                        index_start = i + 1;
                    }
                }
                querySet.add(Integer.parseInt(line.substring(index_start, line.length())));
                querySetList.add(querySet);
            }
            stdin.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return querySetList;
    }
}
