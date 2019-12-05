package com.idavron.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FindMaxSet {
    List<List<Path>> find(List<List<Path>> allCycles) {
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

        return maxSet;
    }
}
