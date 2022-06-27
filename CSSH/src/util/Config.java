package util;

public class Config {
    // new DBLP
    public static String newDblpRoot = "./../new_data/dblp/";
    public static String newDblpGraph = newDblpRoot + "graph.txt";
    public static String newDblpVertex = newDblpRoot + "vertex.txt";
    public static String newDblpEdge = newDblpRoot + "edge.txt";
    // new IMDB
    public static String newIMDBRoot = "./../new_data/imdb/";
    public static String newIMDBGraph = newIMDBRoot + "graph.txt";
    public static String newIMDBVertex = newIMDBRoot + "vertex.txt";
    public static String newIMDBEdge = newIMDBRoot + "edge.txt";
    // new Foursquare
    public static String newFsqRoot = "./../new_data/new_foursquare_dataset/";
    public static String newFsqGraph = newFsqRoot + "graph.txt";
    public static String newFsqVertex = newFsqRoot + "vertex.txt";
    public static String newFsqEdge = newFsqRoot + "edge.txt";
    // MedPub
    public static String pubMedRoot = "./../new_data/PubMed/";
    public static String pubMedGraph = pubMedRoot + "graph.txt";
    public static String pubMedVertex = pubMedRoot + "vertex.txt";
    public static String pubMedEdge = pubMedRoot + "edge.txt";

    private int schemaGraph[][] = null;

    public Config(){
    }

    public int[][] getSchema(String choice){
        if (choice.equals("imdb")){
            int[] movieGraph = {1, 0, 2, 2, 3, 4};
            int[] actorGraph = {0, 1};
            int[] directorGraph = {0, 3};
            int[] writerGraph = {0, 5};
            schemaGraph = new int[4][];
            schemaGraph[0] = movieGraph;
            schemaGraph[1] = actorGraph;
            schemaGraph[2] = directorGraph;
            schemaGraph[3] = writerGraph;
        }
        if (choice.equals("dblp")){
            int[] paperGraph = {1, 0, 2, 1, 3, 2};
            int[] authorGraph = {0, 3};
            int[] venueGraph = {0, 4};
            int[] topicGraph = {0, 5};
            schemaGraph = new int[4][];
            schemaGraph[0] = paperGraph;
            schemaGraph[1] = authorGraph;
            schemaGraph[2] = venueGraph;
            schemaGraph[3] = topicGraph;
        }
        if (choice.equals("foursquare")){
            int[] recordGraph = {1, 0, 2, 2, 3, 4};
            int[] userGraph = {0, 1};
            int[] venueGraph = {0, 3};
            int[] categoryGraph = {0, 5};
            schemaGraph = new int[4][];
            schemaGraph[0] = recordGraph;
            schemaGraph[1] = userGraph;
            schemaGraph[2] = venueGraph;
            schemaGraph[3] = categoryGraph;
        }
        if (choice.equals("pubMed")){
            int[] geneGraph = {1, 0, 2, 1, 3, 2};
            int[] diseGraph = {0, 3};
            int[] chemGraph = {0, 4};
            int[] specGraph = {0, 5};
            schemaGraph = new int[4][];
            schemaGraph[0] = geneGraph;
            schemaGraph[1] = diseGraph;
            schemaGraph[2] = chemGraph;
            schemaGraph[3] = specGraph;
        }
        return schemaGraph;
    }
}
