package com.idavron.graph;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        //Test graph input
        Graph<Integer> graph = new Graph<>(true);
        graph.addEdge(1, 2, "Azamat");
        graph.addEdge(1, 2, "Davron");
        graph.addEdge(1, 2, "Leha");
        graph.addEdge(2, 1, "Farangiz");
        graph.addEdge(2, 1, "Dilyara");
        graph.addEdge(2, 1, "Ashish");
        graph.addEdge(2, 3, "Kirti");
        graph.addEdge(3, 1, "Naseer");
//        graph.addEdge(5, 6, "Chongkoo");
//        graph.addEdge(6, 5, "Agostini");
//        graph.addEdge(6, 7, "Turdibayev");
//        graph.addEdge(7, 2, "Ahmedov");

        AllCyclesInDirectedGraphJohnson johnson = new AllCyclesInDirectedGraphJohnson();
        List<List<Path>> allCycles = johnson.simpleCycles(graph);

        FindMaxSet findMaxSet = new FindMaxSet();
        List<List<Path>> maxSet = findMaxSet.find(allCycles);

        //Printing Result
        if (maxSet == null)
            System.out.println("No cycles");
        else
            for (List<Path> pathList : maxSet) {
                for (Path path : pathList) {
                    if (path.edge != null) {
                        System.out.println(path.edge);
                    }
                }
                System.out.println("Done");
            }
    }
}
