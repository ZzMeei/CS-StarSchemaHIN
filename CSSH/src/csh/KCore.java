package csh;

public class KCore {
    private int graph[][] = null;
    private int n = -1;
    private int deg[] = null;
    private int coreReverseFang[] = null; //2015-9-17, an array sorted by coreness in descend order

    public KCore(int graph[][]){
        this.graph = graph;
        this.n = graph.length - 1;
        this.coreReverseFang = new int[n];// //2015-9-17, initialize this array
    }

    public int[] decompose(){
        deg = new int[n + 1];

        //step 1: obtain the degree and the maximum degree
        int md = -1; // the maximum degree in the graph
        for(int i = 1;i <= n;i ++){
            deg[i] = graph[i].length;
            if(deg[i] > md){
                md = deg[i];
            }
        }

        //step 2: fill the bin
        int bin[] = new int[md + 1];
        for(int i = 1;i <= n;i ++){
            bin[deg[i]] += 1;
        }

        //step 3: update the bin
        int start = 1;
        for(int d = 0; d <= md;d ++){
            int num = bin[d];
            bin[d] = start;
            start += num;
        }

        //step 4: find the position
        int pos[] = new int[n + 1];
        int vert[] = new int[n + 1];
        for(int v = 1; v <= n;v ++){
            pos[v] = bin[deg[v]];
            vert[pos[v]] = v;
            bin[deg[v]] += 1;
        }

        for(int d = md; d >= 1; d--){
            bin[d] = bin[d - 1];
        }
        bin[0] = 1;

        //step 5: decompose
        for(int i = 1;i <= n;i ++){
            int v = vert[i];
            for(int j = 0;j < graph[v].length;j ++){
                int u = graph[v][j];
                if(deg[u] > deg[v]){
                    int du = deg[u];   int pu = pos[u];
                    int pw = bin[du];  int w = vert[pw];
                    if(u != w){
                        pos[u] = pw;   vert[pu] = w;
                        pos[w] = pu;   vert[pw] = u;
                    }
                    bin[du] += 1;
                    deg[u] -= 1;
                }
            }

            coreReverseFang[n - i] = v;
        }
        return deg;
    }

    //obtain the max core
    public int obtainMaxCore(){
        int max = - 1;
        for(int i = 1;i < deg.length;i ++){
            if(deg[i] > max){
                max = deg[i];
            }
        }
        return max;
    }

    public int[] obtainReverseCoreArr(){
        return coreReverseFang;
    }
}
