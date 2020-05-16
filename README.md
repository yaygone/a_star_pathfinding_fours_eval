# COMPX301-20A Assignment 3
Ye-Gon Ryoo, Erika Duerre

## Fours

### Grammar

#### Ye-Gon Ryoo

E -> T
  -> T+E
  -> T-E
T -> F
  -> F*T
  -> F/T
W -> numVal
  -> numVal^E
  -> (E)

## A* Algorithm

### Diagonal path search

#### Ye-Gon Ryoo

The program can search for diagonal steps instead of just the four cardinal directions by calling the "-d" flag.

### Performance optimisation

##### Ye-Gon Ryoo

When the first iteration of the program was completed, the algorithm by itself failed to provide satisfactory performance, both in terms of time and resource. Runs were taking excessively long, and failed to complete for all but the most basic maps, even with the heap size increased. Basic looping- and retracing-avoidance was in place, but these did little to improve the overall performance.

The exponential complexity of A* algorithm is one of its known drawbacks. The assignment brief mentioned *multiple path pruning*, where each time an item is added, all other more expensive paths to the same point are removed from the frontier. While this would have improved the performance of the program, it had two drawbacks:
1. Time complexity still increases along with the frontier size, although not as severely as the algorithm itself.
2. Requires a custom implementation of a priority queue beyond the standard java Collections API to search through the internal array.

For this application, the "shape" of the path was irrelevant, and only maximising the efficiency of the path length was to be considered, so I implemented an alternative method of reducing the time complexity: a replication matrix of the input map that stores a single state value in each cell. I don't know if there is a term for this, but I'll just call it *enqueue filtering*, and the difference to path pruning is that less efficient duplicate paths to the same point are not added to the queue in the first place.

The principle of enqueue filtering was that, between *p<sub>0</sub>* and *p<sub>2</sub>*, for **an** optimal path <*p<sub>0</sub>, p<sub>1</sub>, p<sub>2</sub>*>, there exists between *p<sub>0</sub>* and *p<sub>1</sub>* **an** optimal path <*p<sub>0</sub>, p<sub>1</sub>*>. Therefore, I could store the most optimal path to a given point, along with its cost. If a prospective state arrived at the same point, but incur a higher cost, then it would be impossible for this new state's subsequent children states to be more efficient than the children states of the existing recorded state. If this new state proved more efficient, on the other hand, it would replace the old state as the best state for that given point, and its subsequent children states added to the queue for processing. In other words, for each and every given point, only a single most efficient state was allowed to add subsequent states to the priority queue.

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

### Solutions

Map 1:
```
+-------------------------------+
|                               |
|        XXXXXX       X         |
|         XXXXXXX     XX        |
|            XXXX     XG...     |
|            XXXX     XXXX.     |
|   XX                 XXX.     |
|   XXX....................     |
|    XXS           XXXXXXX      |
|   XXXXXX       XXX     X      |
|    XXX XXXXXXXXX       XX     |
|         XXXXXX          X     |
|                               |
+-------------------------------+
Shortest path took 26 steps
Program took 26 milliseconds to run.
```

Map 2:
```
+------------------------------------+
|               .......              |
|               .XXXXX...            |
|               .XXXXXXX....         |
|               . XXXXXXXXX.         |
|               .   XXXXXXXG         |
|               .      XXXXX         |
|              ..     XXXXXXXXX      |
|              .    XXXXXXXXXXX      |
|              .  XXXXXXXXXXXX       |
|              .   XXXXXXX           |
|              .XXXXXXXXXX           |
|             ..XXXXXXXXX            |
|             .XXXXXXXXXX            |
|             .XXXXXXXXX             |
|             . XXXXXXXX             |
|             .    X                 |
|           ...                      |
|           .                        |
|           .                        |
|           .                        |
|           .    XXXXX               |
|           .    XXXXXX              |
|          ..   XXXXXXX              |
|          .   XXXXXX                |
|          .  XXXXXXX                |
|          SXXXXXXXXX                |
|          XXXXXXXX                  |
|         XXXXXXX                    |
|      XXXXXXXX                      |
|       XXXXXX                       |
|                                    |
+------------------------------------+
Shortest path took 45 steps
Program took 37 milliseconds to run.
```

Map 3:
```
+-------------------------------------------+
|                                           |
|     XXXXXXXXXXXXX                         |
|    XXXXXXXXXXXXXXX                        |
|   XXXX         XXXX                       |
|                XXXXX                      |
|                XXXXX                      |
|                XXXX                       |
|              XXXX                         |
|             XXXXX                         |
|            XXXX             XXXXXG.....   |
|           SXXX          XXXXXXXXXXXXXX..  |
|           .XXXX        XXXX       XXXXX.  |
|           .XXXXXX   XXXXXX        XXXXX.  |
|           ...XXXXXXXX           XXXXX...  |
|             ....XXXXX           XXXX..    |
|                ..............  XXXX..     |
|                             .XXXX...      |
|                             ......        |
+-------------------------------------------+
Shortest path took 50 steps
Program took 41 milliseconds to run.
```

Map 4:
```
+------------------------------------+
| XXXXXXXX                  XXXXXXXXX|
|  XXXX       XX      XXXXXXXXXG.... |
|   XX        XXX         XXXXXXXXX. |
|            XXXX           XXXXXX.. |
|     X        X             XX....  |
|    XX     XXX.................     |
|           XX..        XX    XXX    |
|         .....       XXXXX     XX   |
|         .  XXXX   XXXXXXXX         |
|   .......XXXXXXXXXXXXXXXXXX        |
|   .XXXXXXXXXXXXXXXXXXXXXXXXXX      |
|   SXXXXXXXXXXXXXXXXXX   XXXXXXX    |
|     XXXXXXXXXXXXX         XXXXXX   |
|    XXXXXXXXXXXXXXXXXX XXXXXXXXXXXXX|
| XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
+------------------------------------+
Shortest path took 45 steps
Program took 41 milliseconds to run.
```

Map 5:
```
+------------------------------------------------------------------------------------+
|               XX   X  XXX XX                                                       |
|           X XXXXXXXXXX XXXXXXX          XXXX            XXXXXXXXXXXXXXXX           |
|  XXXXXXXXXXXXXX XXXX XX XXXXX    X ... XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX    |
| XXXXXXXXXXXXXXXXXXXXXX XXXXX     X .X.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  X     |
|  XXXXXXXXXXXXXXXXXXXXXXX X     .....XGXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XX   X      |
|        XXXXXXXXXXXXXXXXXX      .  XXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXXX             |
|          XXXXXXXXXXXXX         .XXX XXX XXXXXXXXXXXXXXXXXXXXXXXXXXXX  X            |
|           XXXXXXXXX            .XX     XXXXXXXXXXXXXXXXXXXXXXXXXX X  X             |
|            XXXXXXXX            ..  XXXXXXXXXXX XXXXXXXXXXXXXXXXXX   X              |
|  X          XXXXX   XX          .XXXXXXXXXXXXXX XXX XXXXXXXXXX                     |
|               XX   XXX          .XXXXXXXXXXXXXXX XX   XXXXXX XX                    |
|                XXXXXXXXX        ...XXXXXXXXXXXXX  X    XXXX   XX                X  |
|                  XXXXXXXXXX       .... XXXXXXXXXX       XX  X XX                   |
|                   XXXXXXXXXXXX       .  XXXXXXXX        X  XXX XX;  X              |
|                    XXXXXXXXXX        .  XXXXXXX                                    |
|                     XXXXXXXX         .  XXXXXX   X             XX X                |
|                    XXXXXXXXX         .   XXXX  X             XXXXXXX     X         |
|                    XXXXXX            .    XX                XXXXXXXXX              |
|                    XXXXX             .     X                 XXXXXXX...S           |
|                     XXX              .....                       XX.. X            |
|                     XX                   ...........................               |
|                      XX                                                            |
+------------------------------------------------------------------------------------+
Shortest path took 68 steps
Program took 59 milliseconds to run.
```
