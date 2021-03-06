import java.util.*;
/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS.  
__ANDREW C JONES 4/13/17 __
*/

// NOTE: to compile and run this, put graph.jar on your CLASSPATH
// (or unpack it in the same directory as this file).  You are welcome
// to use the classes in graph.jar, and anything from java.util.
// You should not use multiple threads.

  
//                      *** SOURCES USED ***
// 
// http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array/21454317#21454317
// http://mrsleblancsmath.pbworks.com/w/file/fetch/46119304/vertex%20coloring%20algorithm.pdf
// https://en.wikipedia.org/wiki/Graph_coloring
// https://en.wikipedia.org/wiki/Greedy_coloring

//                ** A NOTE ON THE METHODS BELOW **
// 
// The file begins with a constructor and helper functions, followed by
// tryImprove, which tries a variety of approaches for a specified amount
// of time. Those approaches are found immediately below tryImprove. 
// There are 6 or so of them, most of them differeing from one another 
// only slightly, and most are modifications of the greedyColoring algorithm
// included in this file. 
//
// The most relevant (and effective) methods are: greedyColoringShuffled,
// greedyDegreeOrderedShuffledTies, and welshPowell. The constructor uses 
// welshPowell, which is a more nuanced way of coloring vertices in order
// of their degree. There is an implementation of welshPowell which shuffles
// the order of vertices of the same degree, but while it may be a 
// "smarter" algorithm than greedyDegreeOrderedShuffledTies, it is in 
// slower enough that the the latter performes better in tryImprove
// because the opportunity to run more shufflings in this case generally
// produces a better coloring than shuffled WP. 
//
// greedyDegreeOrderedShuffled ties almost always performed better than 
// all other approaches in my testing, but greedColoringShuffled may be 
// included in the final iteration of tryImprove to account for those
// exceptional graphs for which descending degree is a poor coloring order.




// A GraphColoring object represents a vertex coloring for a Graph: it
// specifies a positive integer color for each vertex v, so that
// adjacent vertices get distinct colors.  We say it is a "K coloring"
// if the maximum color used is K.  We prefer colorings with K as
// small as possible, but "best" colorings can be hard to find.
//
// This class defines two ways to define the coloring.  First, the
// constructor computes a quick initial coloring, by some fast method.
// Second, the tryImprove(secs) method can try harder to improve the
// current coloring, but it should stay within the given time limit.
//
// See main() for the command-line usage of this program.  In
// particular, it shows how to call tryImprove in a loop, but within
// some global time limit.

// Note: finding a graph coloring that uses the minimum number of
// colors is a very hard problem ("NP-hard", the worst case time is
// apparently exponential in V).  So we pursue "heuristic" solutions
// here.  That is, we try to do the best we can with the time we have
// available.  Whatever we do, we always produce a valid coloring,
// and we prefer colorings that use fewer colors.

public class GraphColoring
{
    // Custom class representing a vertex and its degree implementing
    // comparable for easy sortin -- used for degreeOrdered
    // algorithm
    public static class VertexDegree implements Comparable<VertexDegree> 
    {
        public int degree;
        public int vertex;

        public VertexDegree(int v, int degree)
        {
            this.vertex = v;
            this.degree = degree;
        }

        // greater than 0 if this vertex's degree is larger
        // than the others, less if smaller, 0 if equal
        public int compareTo (VertexDegree other)
        {
            return degree - other.degree;
        }
    }

    // same as vertexDegree except it represents a vertex and the 
    // sum of its neighbor's degrees 
    public static class VertexNeighborRanked implements Comparable<VertexNeighborRanked>
    {
        public int vertex;
        public int neighborDegreeSum;

        public VertexNeighborRanked(int v, Graph G)
        {
            this.vertex = v;
            this.neighborDegreeSum = 0;

            for (int n : G.adj(v))
                this.neighborDegreeSum += G.degree(n);
        }

        // greater than 0 if this vertex's neighborDegreeSum is larger
        // than the others, less if smaller, 0 if equal
        public int compareTo(VertexNeighborRanked other)
        {
            return this.neighborDegreeSum - other.neighborDegreeSum;
        }
    }

    // Internal data for our coloring object.
    private Graph G;           // the Graph (from the constructor)
    private int[] color;       // color[v] is color of vertex v
    private int maxColor;   // maximum color used (K)

    private boolean isTwoColorable; // used by two coloring algorithm 
    
    // array of vertices sorted by degree in ascending order
    private VertexDegree[] vertexDegrees; 
    
    // sorted by sum of neighbor degrees in ascending order
    private VertexNeighborRanked[] verticesNeighborRanked; 

    // used by try improve to decide what fraction of time to devote
    // to degree ordered shuffled vs vanilla shuffled
    private double timeForDegreeOrdered;
    private int numberOfDegreeTies = 0;

    // Accessor methods:
    public Graph graph() { return G; }
    public int color(int v) { return color[v]; }
    public int maxColor()
    {
        int max = 0;
        for (int c: color)
            if (c > max) max = c;
        return max;
    }

    // Constructor: given a graph G, build some fast initial coloring.
    public GraphColoring(Graph G)
    {
        this.G = G;
        
        // if the graph is bipartite, no need to call greedyColoring, 
        // twoColor suffices
        if (!twoColor(G))
        {
            long start = System.currentTimeMillis();
            // sets to max value because welshPowel will update
            // maxColor if it finds a lower coloring
            maxColor = Integer.MAX_VALUE;
            
            // essentially greedy coloring in order of degree, but slightly
            // more complex and less time efficient 
            // (but usually better on first try)
            this.color = greedyDegreeOrderedShuffledTies(G);
            int V = G.V();

            // count the number of degree ties (used for later optimization)
            for(int i = 1; i < G.V(); i++)
            {
                if (vertexDegrees[i-1].degree == vertexDegrees[i].degree)
                    numberOfDegreeTies++;

                // we consider this the ceiling; if its above this number we 
                // will devote a lot of time to degree ordered shuffled ties
                if (numberOfDegreeTies > V/3)
                    break; 
                
            }

            // assigns fraction of time to spend on running degreeOrderedShuffled
            // based on the number of degree ties we found and the size of the graph
            if (V < 100 && numberOfDegreeTies > 15) timeForDegreeOrdered = .3;
            else if (V < 100 && numberOfDegreeTies == 0) timeForDegreeOrdered = 0;
            else if (V < 100) timeForDegreeOrdered = .1;
            else if (numberOfDegreeTies > V/3 && V > 1000) timeForDegreeOrdered = .825;
            else if (numberOfDegreeTies > V/3) timeForDegreeOrdered = .75;
            else if (numberOfDegreeTies == 0) timeForDegreeOrdered  = 0.0;
            else if (numberOfDegreeTies < 10) timeForDegreeOrdered = .05;
            else timeForDegreeOrdered = .25;
        }

        // otherwise two coloring worked, it's bipartite and we're done
    }



    // *** inspired by Sedgewick and Wayne textbook chapter 4 pp. 547 ***
    // with modifications (it finds 2-coloring at the same time as finding
    // whether it is bipartite, discards if it isnt bipartite)

    // given a graph G, determines if the graph is bipartite (i.e., if it 
    // is two-colorable)
    // 
    // @returns true if the graph is bipartite and a 2-coloring was found
    // @returns false if the graph is not bipartite.
    private boolean twoColor(Graph G)
    {

        // marked[i] == true if we have visited vertex i
        boolean[] marked = new boolean[G.V()];

        // indicates which of two colors is vertex i this both allows us
        // to detect if a graph is not bipartite (because some vertex will 
        // have same color as neighbor) and allows us to create a 
        // two-coloring if it is bipartite (because we know which verticies
        // must be one color and which must be the other)
        boolean[] colorbool = new boolean[G.V()];
        
        // property flips to false once we find adjascent vertices with same
        // color
        this.isTwoColorable = true;

        // recursive dfs on each vertex if its not visited. necessary because 
        // recursion is insufficient if there are disconnected components
        for(int s = 0; s < G.V(); s++ )
            if (!marked[s] && isTwoColorable)
                bipartDfs(G, s, marked, colorbool);


        // if it is bipartite, translate the boolean colorbool array into the
        // color array for output. - if colorbool is false, vertex color is 1,
        // if its true make it color 2. 
        if (isTwoColorable)
        {
            this.color = new int[G.V()];

            // assign color to each vertex
            for (int i = 0; i < G.V(); i++)
                if (colorbool[i]) color[i] = 1;
                else color[i] = 2;


            this.maxColor = 2;
            // return true so that constructor knows we succeeded and greedyColoring
            // call can be skipped. 
            return true;
        }

        //return false so that constructor knows it must call greedyColoring
        else return false;
    }

    // recursive dfs. will call recursively on each neighbor of s which is not marked
    // in @param marked. will set isTwoColorable to false if one of s's neighbors 
    // has the same color as s. 
    private void bipartDfs(Graph G, int s, boolean[] marked, boolean[] colorbool)
    {
        //mark visited
        marked[s] = true;

        //for each vertex connected to s 
        for (int w : G.adj(s))
        {   
            //if it isnt marked, give it the opposite color as s, and recur on it
            if (!marked[w] && isTwoColorable)
            {
                colorbool[w] = !colorbool[s];
                bipartDfs(G, w, marked, colorbool); 
            }

            // otherwise it is marked; if the graph is bipartite, it must not 
            // have the same color as s 
            else if (colorbool[w] == colorbool[s])
            {
                isTwoColorable = false;
                break;
            } 
        }
    }

    // The greedy coloring heuristic.  You may replace this with something
    // better, if you want.
    private static int[] greedyColoring(Graph G)
    {
        int V = G.V();
        assert V >= 1;
        // This will be our coloring array.
        int[] color = new int[V];
        // In loop, we keep track of the maximum color used so far.
        int maxColor = 0;
        // For each vertex v, we let color[v] be the first color which
        // is not already taken by the neighbors of v.
        for (int v=0; v<V; ++v)
        {
            boolean[] taken = new boolean[maxColor+1];
            for (int u: G.adj(v))
                taken[color[u]] = true;
            // Find the first color c not taken by neighbors of v.
            int c = 1;
            while (c <= maxColor && taken[c])
                ++c;
            color[v] = c;
            // Maybe we started using a new color at v.
            if (c > maxColor)
                maxColor = c;
        }

        // All done, return the array.
        return color;
    }

    // Method bugs checks whether the current coloring is valid, just
    // using its public interface.  It returns the number of problems
    // found.  If it returns 0, then the coloring is a valid.
    public int bugs()
    {
        Graph G = graph();
        int V = G.V();
        int K = maxColor();
        int bugCount = 0;
        for (int v=0; v<V; ++v) {
            int c = color(v);
            if (c < 1) ++bugCount;
            if (c > K) ++bugCount;
            for (int u: G.adj(v))
                if (u!=v && c==color(u))
                    ++bugCount;
        }
        return bugCount;
    }

    // toString() lets us print a coloring.  The coloring is printed
    // as two lines of text, all integer values:
    //
    // V K
    // C[0] C[1] ... C[V-1]
    //
    // Here V is the number of graph vertices, K is the maximum color
    // used, and each C[i] is the color of vertex i. The color should
    // be an integer between 1 and K, inclusive.
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(color.length).append(' ').append(maxColor()).append('\n');
        String sep = "";
        for (int c: color) {
            sb.append(sep).append(c);
            sep = " ";
        }
        sb.append('\n');
        return sb.toString();
    }


    // taken from http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array/21454317#21454317
    //shuffles the given array
    public static void shuffleArray(int[] array) 
    {   
        List<Integer> list = new ArrayList<>();
        for (int i : array) 
        {
            list.add(i);
        }

        Collections.shuffle(list);

        for (int i = 0; i < list.size(); i++) 
        {
            array[i] = list.get(i);
        }    
    }

    // initializes the field vertexDegrees by creating an array
    // of VertexDegree objects (which has a vertex and a degree property)
    // and then sorting the array by degree
    public void createDegreeOrderedArray(Graph G)
    {
        this.vertexDegrees = new VertexDegree[G.V()];

        // for each vertex, create a VertexDegree where vertex is v and 
        // degree is G.degree(v)
        for (int v = 0; v < G.V(); v++)
            this.vertexDegrees[v] = new VertexDegree(v, G.degree(v));

        Arrays.sort(this.vertexDegrees);
    }

    // Method tryImprove(secs) is where we may implement some more
    // time-consuming graph coloring heuristic, which tries to improve
    // the current coloring.  The boolean return value indicates
    // whether we succeeded in improving the coloring (that is,
    // reducing the number of colors used).  The "secs" argument is a
    // time limit, in seconds.
    //
    // In general, we should try our heuristic for a while, trying to
    // improve the current coloring.  If we succeed in finding a
    // better coloring, we should return true (then main will report
    // the coloring).  If we want to quit (maybe we are out of time or
    // ideas, or the coloring is already best possible), we should
    // return false.  Note that if we exceed the time limit, then
    // there is a risk that our output will be ignored: our program
    // may be killed externally!


    // devotes an amount of time determined in constructor to 
    // greedyDegreeorderedShuffledTies, and the rest to 
    // greedyColoringShuffled (to account for those rare graphs)
    // where descending degree is a terrible coloring order
    public boolean tryImprove(double secs)
    {

        long start = System.currentTimeMillis();
        // convert secs to ms to avoid constantly converting wrt currentTimeMillis()
        secs = secs*1000;
        int res[];
        int oldMaxColor = this.maxColor;
        int V = G.V();
            
        // runs degreeOrderedShuffled for a fraction of total time
        while (System.currentTimeMillis() - start < (timeForDegreeOrdered*secs))
        {
            res = greedyDegreeOrderedShuffledTies(G);
            if (maxColor < oldMaxColor)
            {
                this.color = res;
                return true;
            }
        }

        // runs regular random greedy for (most of) the rest of the time
        while (System.currentTimeMillis() - start < (.99 * secs))
        {
            res = greedyColoringShuffled(G, null);
            if (maxColor < oldMaxColor)
            {
                this.color = res;
                return true;
            }
        }
        
        return false; // give up :C 
    }



    //              **************************************
    //                  MY COLORING ALGORITHMS BEGIN HERE
    //              **************************************



    // implements greedy coloring with a random order using a translation array - 
    // see extensive notes in tryImprove
    //
    // we pass the translation array as a parameter instead of constructing it in 
    // this method to decouple the method from a particular shuffling implementation
    // -- i might investigate other ways to shuffle the array for better or more efficient
    // randomization

    // @param shuffleTranslation - an array of size V where values are in range 
    //     0 - V-1. shuffleTranslation[i] = some vertex v, where the relationship
    //     between i and v has been randomized.  optional - if null, generated in method
    private int[] greedyColoringShuffled(Graph G, int[] shuffleTranslation)
    {
        int V = G.V();
        assert V >= 1;

        if (shuffleTranslation == null)
        {
            shuffleTranslation = new int[V];
            for (int i = 0; i < V; i++)
                shuffleTranslation[i] = i;

            shuffleArray(shuffleTranslation);
        }

        // This will be our coloring array.
        int[] color = new int[V];
        // In loop, we keep track of the maximum color used so far.
        int maxColor = 0;
        // For each vertex v, we let color[v] be the first color which
        // is not already taken by the neighbors of v.
        for (int i=0; i<V; ++i)
        {
            int v = shuffleTranslation[i];
            boolean[] taken = new boolean[maxColor+1];
            for (int u: G.adj(v))
                taken[color[u]] = true;
            // Find the first color c not taken by neighbors of v.
            int c = 1;
            while (c <= maxColor && taken[c])
                ++c;
            color[v] = c;
            // Maybe we started using a new color at v.
            if (c > maxColor)
                maxColor = c;
        }

        if (this.maxColor > maxColor)
            this.maxColor = maxColor;
        // All done, return the array.
        return color;
    }


    // visits vertices from highest degree to lowest degree
    // TODO: randomize order of visiting vertices between ties,
    // so that this can be run multiple times to potentially find a 
    // better result
    private int[] greedyColoringDegreeOrdered(Graph G)
    {
        int V = G.V();
        VertexDegree[] arr;
        if(vertexDegrees == null)
        {
            createDegreeOrderedArray(G);
        }
        arr = vertexDegrees;

        // This will be our coloring array.
        int[] color = new int[V];
        // In loop, we keep track of the maximum color used so far.
        int maxColor = 0;

        for (int i = G.V()-1; i >=0; i--)
        {
            int v = arr[i].vertex;
            
            boolean[] taken = new boolean[maxColor+1];
            for (int u: G.adj(v))
                taken[color[u]] = true;
            // Find the first color c not taken by neighbors of v.
            int c = 1;
            while (c <= maxColor && taken[c])
                ++c;
            color[v] = c;
            // Maybe we started using a new color at v.
            if (c > maxColor)
                maxColor = c;
        }

        if (this.maxColor > maxColor)
            this.maxColor = maxColor;
        // All done, return the array.
        return color;

    }


    // visits vertices and greedy colors in order from highest degree
    // to lowest degree. if there are ties-vertices with the same degree-
    // (significant number of ties are present in virtually every graph)
    // then those tied vertices are visited in random order. this permits
    // us to repeatedly run this method much like randomGreedy, except
    // often with better results 
    private int[] greedyDegreeOrderedShuffledTies(Graph G)
    {
        int V = G.V();
        VertexDegree[] arr;
        if(vertexDegrees == null)
        {
            createDegreeOrderedArray(G);
        }
        arr = vertexDegrees;

        // This will be our coloring array.
        int[] color = new int[V];
        // In loop, we keep track of the maximum color used so far.
        int maxColor = 0;


        // loops through vertices in descending order of degree
        // NOTE: logic in the loop may update i if there are ties
        for (int i = G.V()-1; i >=0; i--)
        {
            // if we are at the end of array OR there is not a degree tie,
            // proceed normally
            if (i == 0 || arr[i].degree != arr[i-1].degree)
            {
                int v = arr[i].vertex;
                
                //tracks which colors are taken by this vertex's neighbors
                boolean[] taken = new boolean[maxColor+1];
                for (int u: G.adj(v))
                    taken[color[u]] = true;
                // Find the first color c not taken by neighbors of v.
                int c = 1;
                while (c <= maxColor && taken[c])
                    ++c;
                color[v] = c;
                // Maybe we started using a new color at v.
                if (c > maxColor)
                    maxColor = c;
            }
            else // there is a degree tie; shuffle
            {

                //vertices with the same degre (at this level)
                ArrayList<Integer> sameDegree = new ArrayList<Integer>();
                sameDegree.add(arr[i].vertex);

                // takes the vertices and deincrements i until we 
                // reach a vertex with a different degree
                while ( i > 0 && arr[i].degree == arr[i-1].degree)
                {
                    i--;
                    sameDegree.add(arr[i].vertex);
                }

                // shuffle ! this permits multiple trials of this algorithm
                Collections.shuffle(sameDegree);


                // now apply the normal greedyColoring logic to the 
                // randomly ordered list of vertices with the same degree 
                for (int v : sameDegree)
                {
                    boolean[] taken = new boolean[maxColor+1];
                    for (int u: G.adj(v))
                        taken[color[u]] = true;
                    // Find the first color c not taken by neighbors of v.
                    int c = 1;
                    while (c <= maxColor && taken[c])
                        ++c;
                    color[v] = c;
                    // Maybe we started using a new color at v.
                    if (c > maxColor)
                        maxColor = c;
                } // end loop through same degrees
            } // end else (tie found) condition
            
        } // end loop through all vertices

        if (this.maxColor > maxColor)
            this.maxColor = maxColor;
        // All done, return the array.
        return color;
    }


    private int[] greedyNeighborOrdered(Graph G)
    {
        int V = G.V();
        if(verticesNeighborRanked == null)
        {
            verticesNeighborRanked = new VertexNeighborRanked[V];
            for(int i = 0; i < V; i++)
                verticesNeighborRanked[i] = new VertexNeighborRanked(i, G);
        }
        VertexNeighborRanked[] arr = verticesNeighborRanked;

        // This will be our coloring array.
        int[] color = new int[V];
        // In loop, we keep track of the maximum color used so far.
        int maxColor = 0;

        for (int i = G.V()-1; i >=0; i--)
        {
            int v = arr[i].vertex;
            
            boolean[] taken = new boolean[maxColor+1];
            for (int u: G.adj(v))
                taken[color[u]] = true;
            // Find the first color c not taken by neighbors of v.
            int c = 1;
            while (c <= maxColor && taken[c])
                ++c;
            color[v] = c;
            // Maybe we started using a new color at v.
            if (c > maxColor)
                maxColor = c;
        }

        if (this.maxColor > maxColor)
            this.maxColor = maxColor;
        // All done, return the array.
        return color;
    }

    // inspired by http://mrsleblancsmath.pbworks.com/w/file/fetch/46119304/vertex%20coloring%20algorithm.pdf
    public int[] welshPowell(Graph G)
    {
        if (vertexDegrees == null)
            createDegreeOrderedArray(G);

        int V = G.V();

        //so that we can skip vertices after coloring them
        boolean[] alreadyColored = new boolean[V];
        int[] color = new int[V]; //tmp array of colorings
        int coloredCount = 0; //so we know when we're done
        int currentColor = 1; //each loop will use the next color
        while (coloredCount < V)
        {
            //if a vertex is adjascent to a vertex we colored on this loop, we 
            //cant color it on this loop
            boolean[] coloredThisLoop = new boolean[V];


            //visit vertices in descending order of degree
            for (int i = V-1; i >= 0; i--)
            {
                int v = vertexDegrees[i].vertex;
                if (alreadyColored[v]) continue;
                boolean canColor = true;

                // see if we colored any of its neighbors on this loop
                for (int n : G.adj(v))
                    if (coloredThisLoop[n])
                    {
                        canColor = false;
                        break;
                    }

                //if none of its neighbors have been colored this loop
                if (canColor)
                {
                    color[v] = currentColor;
                    alreadyColored[v] = true;
                    coloredThisLoop[v] = true;
                    coloredCount++;
                }
            }
            currentColor++; // use next color for next loop
        }

        if ((currentColor - 1) < maxColor)
            maxColor = currentColor - 1;

        return color;
    }


    // inspired by http://mrsleblancsmath.pbworks.com/w/file/fetch/46119304/vertex%20coloring%20algorithm.pdf
    public int[] welshPowellShuffled(Graph G)
    {
        if (vertexDegrees == null)
            createDegreeOrderedArray(G);

        int V = G.V();

        //so that we can skip vertices after coloring them
        boolean[] alreadyColored = new boolean[V];
        int[] color = new int[V]; //tmp array of colorings
        int coloredCount = 0; //so we know when we're done
        int currentColor = 1; //each loop will use the next color
        while (coloredCount < V)
        {
            //if a vertex is adjascent to a vertex we colored on this loop, we 
            //cant color it on this loop
            boolean[] coloredThisLoop = new boolean[V];


            //visit vertices in descending order of degree
            for (int i = V-1; i >= 0; i--)
            {
                int v = vertexDegrees[i].vertex;
                if (alreadyColored[v]) continue;

                if (i == 0 || 
                    vertexDegrees[i-1].degree != vertexDegrees[i].degree)
                {
                    boolean canColor = true;

                    // see if we colored any of its neighbors on this loop
                    for (int n : G.adj(v))
                        if (coloredThisLoop[n])
                        {
                            canColor = false;
                            break;
                        }

                    //if none of its neighbors have been colored this loop
                    if (canColor)
                    {
                        color[v] = currentColor;
                        alreadyColored[v] = true;
                        coloredThisLoop[v] = true;
                        coloredCount++;
                    }
                }
                else 
                {
                    ArrayList<Integer> sameDegree = new ArrayList<Integer>();
                    sameDegree.add(vertexDegrees[i].vertex);

                    while(i > 0 && 
                        vertexDegrees[i].degree == vertexDegrees[i-1].degree)
                    {
                        i--;
                        sameDegree.add(vertexDegrees[i].vertex);
                    }

                    Collections.shuffle(sameDegree);

                    for ( int ver : sameDegree)
                    {
                        if (alreadyColored[ver]) continue;

                        boolean canColor = true;

                        //check neighbors
                        for (int n: G.adj(ver))
                        {
                            if(coloredThisLoop[n])
                            {
                                canColor = false;
                                break;
                            }
                        }

                        if (canColor)
                        {
                            color[ver] = currentColor;
                            alreadyColored[ver] = true;
                            coloredThisLoop[ver] = true;
                            coloredCount++;
                        }

                    }
                }
                
            }
            currentColor++; // use next color for next loop
        }

        if ((currentColor - 1) < maxColor)
            maxColor = currentColor - 1;

        return color;
    }

    // Print a warning message to System.err (not System.out).
    static void warn(String msg) { System.err.println("WARNING: "+msg); }

    // Method main() defines our command-line usage:
    //
    //     java GraphColoring graph.txt [TIMELIMIT]
    //
    // The Graph filename argument must be given.
    // If omitted, the time limit defaults to 3.0 seconds.
    //
    // This program reads a Graph (in Sedgewick format) from a file,
    // constructs a GraphColoring for that Graph, and then prints that
    // initial coloring (using our toString method).  Then it goes
    // into a loop with tryImprove, printing each improved coloring
    // found, until tryImprove gives up (by returning false), or until
    // we run out of time.


} //end of GraphColoring class
