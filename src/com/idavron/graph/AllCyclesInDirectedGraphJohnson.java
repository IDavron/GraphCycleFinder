package com.idavron.graph;

import java.util.*;

/**
 * Find all cycles in directed graph using Johnson's algorithm
 * Time complexity - O(E + V).(c+1) where c is number of cycles found
 * Space complexity - O(E + V + s) where s is sum of length of all cycles.
 **/

public class AllCyclesInDirectedGraphJohnson {
    Set<Vertex<Integer>> blockedSet;
    Map<Vertex<Integer>, Set<Vertex<Integer>>> blockedMap;
    Deque<Path> stack;
    List<List<Path>> allCycles;

    /**
     * Main function to find all cycles
     **/
    public List<List<Path>> simpleCycles(Graph<Integer> graph) {

        blockedSet = new HashSet<>();
        blockedMap = new HashMap<>();
        stack = new LinkedList<>();
        allCycles = new ArrayList<>();
        long startIndex = 1;
        TarjanStronglyConnectedComponent tarjan = new TarjanStronglyConnectedComponent();
        while (startIndex <= graph.getAllVertex().size()) {
            Graph<Integer> subGraph = createSubGraph(startIndex, graph);
            List<Set<Vertex<Integer>>> sccs = tarjan.scc(subGraph);
            //this creates graph consisting of strongly connected components only and then returns the
            //least indexed vertex among all the strongly connected component graph.
            //it also ignore one vertex graph since it wont have any cycle.
            Optional<Vertex<Integer>> maybeLeastVertex = leastIndexSCC(sccs, subGraph);
            if (maybeLeastVertex.isPresent()) {
                Vertex<Integer> leastVertex = maybeLeastVertex.get();
                blockedSet.clear();
                blockedMap.clear();
                findCyclesInSCG(leastVertex, leastVertex, null);
                startIndex = leastVertex.getId() + 1;
            } else {
                break;
            }
        }
        return allCycles;
    }

    private Optional<Vertex<Integer>> leastIndexSCC(List<Set<Vertex<Integer>>> sccs, Graph<Integer> subGraph) {
        long min = Integer.MAX_VALUE;
        Vertex<Integer> minVertex = null;
        Set<Vertex<Integer>> minScc = null;
        for (Set<Vertex<Integer>> scc : sccs) {
            if (scc.size() == 1) {
                continue;
            }
            for (Vertex<Integer> vertex : scc) {
                if (vertex.getId() < min) {
                    min = vertex.getId();
                    minVertex = vertex;
                    minScc = scc;
                }
            }
        }

        if (minVertex == null) {
            return Optional.empty();
        }
        Graph<Integer> graphScc = new Graph<>(true);
        for (Edge<Integer> edge : subGraph.getAllEdges()) {
            if (minScc.contains(edge.getVertex1()) && minScc.contains(edge.getVertex2())) {
                graphScc.addEdge(edge.getVertex1().getId(), edge.getVertex2().getId(), edge.getName());
            }
        }
        return Optional.of(graphScc.getVertex(minVertex.getId()));
    }

    private void unblock(Vertex<Integer> u) {
        blockedSet.remove(u);
        if (blockedMap.get(u) != null) {
            blockedMap.get(u).forEach(v -> {
                if (blockedSet.contains(v)) {
                    unblock(v);
                }
            });
            blockedMap.remove(u);
        }
    }

    private boolean findCyclesInSCG(Vertex<Integer> startVertex, Vertex<Integer> currentVertex, Edge<Integer> currentEdge) {
        boolean foundCycle = false;
        stack.push(new Path(currentVertex, currentEdge));
        blockedSet.add(currentVertex);

        for (Edge<Integer> e : currentVertex.getEdges()) {
            Vertex<Integer> neighbor = e.getVertex2();
            //if neighbor is same as start vertex means cycle is found.
            //Store contents of stack in final result.
            if (neighbor == startVertex) {
                List<Path> cycle = new ArrayList<>();
                stack.push(new Path(startVertex, e));
                cycle.addAll(stack);

                Collections.reverse(cycle);
                stack.pop();
                allCycles.add(cycle);
                foundCycle = true;
            } //explore this neighbor only if it is not in blockedSet.
            else if (!blockedSet.contains(neighbor)) {
                boolean gotCycle =
                        findCyclesInSCG(startVertex, neighbor, e);
                foundCycle = foundCycle || gotCycle;
            }
        }
        //if cycle is found with current vertex then recursively unblock vertex and all vertices which are dependent on this vertex.
        if (foundCycle) {
            //remove from blockedSet  and then remove all the other vertices dependent on this vertex from blockedSet
            unblock(currentVertex);
        } else {
            //if no cycle is found with current vertex then don't unblock it. But find all its neighbors and add this
            //vertex to their blockedMap. If any of those neighbors ever get unblocked then unblock current vertex as well.
            for (Edge<Integer> e : currentVertex.getEdges()) {
                Vertex<Integer> w = e.getVertex2();
                Set<Vertex<Integer>> bSet = getBSet(w);
                bSet.add(currentVertex);
            }
        }
        //remove vertex from the stack.
        stack.pop();
        return foundCycle;
    }

    private Set<Vertex<Integer>> getBSet(Vertex<Integer> v) {
        return blockedMap.computeIfAbsent(v, (key) ->
                new HashSet<>());
    }

    private Graph createSubGraph(long startVertex, Graph<Integer> graph) {
        Graph<Integer> subGraph = new Graph<>(true);
        for (Edge<Integer> edge : graph.getAllEdges()) {
            if (edge.getVertex1().getId() >= startVertex && edge.getVertex2().getId() >= startVertex) {
                subGraph.addEdge(edge.getVertex1().getId(), edge.getVertex2().getId(), edge.getName());
            }
        }
        return subGraph;
    }

    public static void main(String args[]) {
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

        //Finding all cycles
        AllCyclesInDirectedGraphJohnson johnson = new AllCyclesInDirectedGraphJohnson();
        List<List<Path>> allCycles = johnson.simpleCycles(graph);

        //Finding MaxSet of cycles
        allCycles.sort((o1, o2) -> -Integer.compare(o1.size(), o2.size()));
        List<List<Path>> maxSet = null;
        int max = 0;
        for (int i = 0; i < allCycles.size(); i++) {
            List<Path> setOne = allCycles.get(i);
            if (setOne.get(0).edge == null)
                setOne.remove(0);

            //Set of edges (names) which are in the set
            HashSet<String> nameSet = new HashSet<>();

            //Local max
            List<List<Path>> tempMaxSet = new ArrayList<>();
            tempMaxSet.add(setOne);
            int temp = setOne.size();

            for (Path path : setOne)
                nameSet.add(path.edge.getName());

            for (int j = i + 1; j < allCycles.size(); j++) {
                List<Path> setTwo = allCycles.get(j);
                if (setTwo.get(0).edge == null)
                    setTwo.remove(0);

                boolean intersect = false;

                //Check for intersection
                for (Path path : setTwo) {
                    if (nameSet.contains(path.edge.getName())) {
                        intersect = true;
                        break;
                    }
                }

                if (!intersect) {
                    temp += setTwo.size();
                    tempMaxSet.add(setTwo);
                    for (Path path : setTwo)
                        nameSet.add(path.edge.getName());
                }
            }

            if (temp > max) {
                max = temp;
                maxSet = tempMaxSet;
            }
        }

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