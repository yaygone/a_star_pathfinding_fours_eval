# COMPX301-20A Assignment 3
Ye-Gon Ryoo, Erika Duerre

## Fours

## A* Algorithm

### Performance optimisation

##### Ye-Gon Ryoo

When the first iteration of the program was completed, the algorithm by itself failed to provide satisfactory performance, both in terms of time and resource. Runs were taking excessively long, and failed to complete for all but the most basic maps, even with the heap size increased. Basic looping- and retracing-avoidance was in place, but these did little to improve the overall performance.

The exponential complexity of A* algorithm is one of its known drawbacks. For this application, the "shape" of the path was irrelevant, and only maximising the efficiency of the path length was to be considered, so I implemented a replication matrix of the input map that stores a single state value in each cell. I don't know if there is a term for this, but I'll just call it *enqueue filtering*.

The principle of enqueue filtering was that, between *p<sub>0</sub>* and *p<sub>3</sub>*, for **an** optimal path <*p<sub>0</sub>, p<sub>1</sub>, p<sub>2</sub>, p<sub>3</sub>*>, there exists between *p<sub>0</sub>* and *p<sub>2</sub>* **an** optimal path <*p<sub>0</sub>, p<sub>1</sub>, p<sub>2</sub>*>. Therefore, I could store the most optimal path to a given point, along with its cost. If a prospective state arrived at the same point, but incur a higher cost, then it would be impossible for this new state's subsequent children states to be more efficient than the children states of the existing recorded state. If this new state proved more efficient, on the other hand, it would replace the old state as the best state for that given point, and its subsequent children states added to the queue for processing. In other words, for each and every given point, only a single most efficient state was allowed to add subsequent states to the priority queue.

Without enqueue filtering (map1.txt):
```
Shortest path took 26 steps
Program took 5213 milliseconds to run.
```

With enqueue filtering (map1.txt):
```
Shortest path took 26 steps
Program took 26 milliseconds to run.
```
No example maps took longer than 50 milliseconds to run (at the time before GUI was implemented).