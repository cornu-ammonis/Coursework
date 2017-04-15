import java.util.*;
/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS.  
__ANDREW C JONES 4/13/17 __
*/
// Students should edit this file for homework.

// NOTE: to compile and run this, put graph.jar on your CLASSPATH
// (or unpack it in the same directory as this file).  You are welcome
// to use the classes in graph.jar, and anything from java.util.
// You should not use multiple threads.

// As usual: add honor statement up here, and fix the TODO's below.
// If you use documents or websites (beyond our textbook) for ideas,
// you should list those here.  Your code should be your own.

// The only methods you must edit are the constructor and tryImprove,
// but you are welcome to redesign other parts of the class too, if
// you like.  When we test your code on Gimle, we will use a top-level
// loop that resembles main (but maybe with more testing and output).


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
    // Internal data for our coloring object.
    private Graph G;           // the Graph (from the constructor)
    private int[] color;       // color[v] is color of vertex v
    private int maxColor;   // maximum color used (K)

    private boolean isTwoColorable;

    //used so that tryImprove does greedy coloring on degree ordered 
    //graph exactly once
    private boolean alreadyDegreeOrderColored = false;
    private VertexDegree[] vertexDegrees;
    private VertexNeighborRanked[] verticesNeighborRanked;

    // if there are no ties its a waste of time to keep running 
    // the degree oredered algorithm because its the same each time so we 
    // track if there are ties and only run it multiple times if there are 
    private boolean degreeTiesExist = false;
    private int shuffledTiesAttemptCount = 0;
    private int vanillaShuffledAttemptCount = 0;
    private int neighborRankedAttemptCount = 0;

    // Accessor methods:
    public Graph graph() { return G; }
    public int color(int v) { return color[v]; }
    public int maxColor()
    {
        // If you prefer, you could maintain the "maxColor" class
        // variable, and redefine this method to return its value.
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
            this.color = greedyColoring(G);
            maxColor = maxColor();
        }
        else
            System.out.println("found bipartite!");
        // TODO: if G is bipartite, then you should find a 2-coloring
        // here in the constructor.
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
        // color array for output. - if colorbool is false, that vertex color 1,
        // if its true make it color 2. 
        if (isTwoColorable)
        {
            this.color = new int[G.V()];

            // assign color to each vertex
            for (int i = 0; i < G.V(); i++)
            {
                if (colorbool[i]) color[i] = 1;
                else color[i] = 2;
            }

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
            if (!marked[w])
            {
                colorbool[w] = !colorbool[s];
                bipartDfs(G, w, marked, colorbool); 
            }

            // otherwise it is marked; if the graph is bipartite, it must not 
            // have the same color as s 
            else if (colorbool[w] == colorbool[s]) isTwoColorable = false;
        }
    }

    /*private boolean twoColor(Graph G)
    {
        int V = G.V();
        int[] color = new int[V];
        boolean[] marked = new boolean[V];
        Stack<Integer> stack = new Stack<Integer>();
        color[0] = 1;
        stack.push(0);

        while(!stack.isEmpty())
        {
            int v = stack.pop();
            marked[v] = true;
            for (int w : G.adj(v))
            {
                if (!marked[w])
                {
                    if (color[v] == 1) color[w] = 2;
                    else color[w] = 1;
                    stack.push(w);
                }
                else if (color[w] == color[v]) return false;
            }

        }
        this.color = color;
        return true; 
    }*/

    // The greedy coloring heuristic.  You may replace this with something
    // better, if you want.
    private static int[] greedyColoring(Graph G)
    {
        System.out.println("didnt find bipartite");
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

    public boolean tryImprove(double secs)
    {
        // TODO: implement some graph coloring heuristic here.
        // Minimal good enough: do repeated trials of greedy-coloring,
        // each time with a random vertex order.  If you find a better
        // coloring (better than your previous best), return true.  Or
        // else if you run out of time, return false.

        // Note you can use System.currentTimeMillis() to estimate how
        // much time you have used.  For an example of its use, see
        // the code in main.

        // As given, this does nothing at all, it just stops early.

        // convert to ms because why bother with seconds
        long start = System.currentTimeMillis();
        secs = secs*1000;
        boolean foundBetterColoring = false;
        int V = G.V();
        int oldMaxColor = maxColor;


        // we want to do greedy algorithm on vertices ordered according to their
        // degree at least once 
        // we also go ahead and populate the array of VertexDegree so taht 
        // we dont have to recreate it on subsequent runs (if their are ties
        // we will run this multiple times with shuffled order for tied degrees)
        if (!alreadyDegreeOrderColored)
        {
            // start timer
            long degreeOrderStart = System.currentTimeMillis();
            
            // populate our vertexDegrees array
            vertexDegrees = new VertexDegree[V];
            for (int i = 0; i < V; i++)
                vertexDegrees[i] = new VertexDegree(i, G.degree(i));

            // sort according to degree
            Arrays.sort(vertexDegrees);

            int[] res = greedyDegreeOrderedShuffledTies(G);
            System.out.println("GreedyColoring took " + (System.currentTimeMillis() - degreeOrderStart) );
            alreadyDegreeOrderColored = true;

            if (maxColor < oldMaxColor)
            {
                System.out.println("Degree ordered found better!!!");
                this.color = res;
                return true;
            }
        }


        if (System.currentTimeMillis() - start < secs && neighborRankedAttemptCount == 0)
        {
            long neighborRankedStart = System.currentTimeMillis()
            oldMaxColor = maxColor;

            //initialize verticesNeighborRanked
            verticesNeighborRanked = new VertexNeighborRanked[V];
            for(int i = 0; i < V; i++)
                verticesNeighborRanked[i] = ne VertexNeighborRanked(i, G);

            Arrays.sort(verticesNeighborRanked);

            int[] res = greedyNeighborOrdered(G);
            neighborRankedAttemptCount++;

            if (maxColor < oldMaxColor)
            {
                System.out.println("neighbor ordered found better!");
                this.color = res;
                return true;
            }
        }

        // repeatedly tries greedyDegreeOrdered with shuffled ties 
        // unless thare are no ties or we run out of time. it tries 10 times, which i chose 
        // completely arbitrarily (TODO: this number could potentially be raised or lowered)
        while (System.currentTimeMillis() - start < secs && shuffledTiesAttemptCount < 10
            && degreeTiesExist)
        {
            int[] res = greedyDegreeOrderedShuffledTies(G);
            shuffledTiesAttemptCount++;
            if (maxColor < oldMaxColor)
            {
                System.out.println("Shuffled ties found better! at attempt " + shuffledTiesAttemptCount);
                this.color = res;
                return true;
            }
        }

        System.out.println("shuffled tries attempt count: " + shuffledTiesAttemptCount);


        int[] shuffleTranslationArray = new int[G.V()];

        for (int i = 0; i < V; i++)
            shuffleTranslationArray[i] = i;
        // keeps trying vanilla greedy with shuffled vertex order until 
        // runs out of time
        while (System.currentTimeMillis() - start < secs)
        {
            // this array allows us to associate sequential indexes 0 through V-1
            // with one and only one random vertex 
            // this is accomplished by assigning values i {0 through V - 1}
            // to index position i (arr[i] = i) , and then shuffling the array randomly.
            // the only modiication then required to the greedy implementation 
            // is to loop i from 0 to V -1, and say v = shuffleTranslationArray[i]
            // this will in effect visit each vertex once in a random order. 

            shuffleArray(shuffleTranslationArray);

            int[] betterColor = greedyColoringShuffled(G, shuffleTranslationArray);
            vanillaShuffledAttemptCount++;
            if (maxColor < oldMaxColor)
            {
                System.out.println("shuffled vanilla found better at attempt: " + vanillaShuffledAttemptCount);
                this.color = betterColor;
                return true;
            }
        }

        System.out.println("exiting after: " + vanillaShuffledAttemptCount + " vanilla shuffled attempts");
        return false;
    }


    // implements greedy coloring with a random order using a translation array - 
    // see extensive notes in tryImprove
    //
    // we pass the translation array as a parameter instead of constructing it in 
    // this method to decouple the method from a particular shuffling implementation
    // -- i might investigate other ways to shuffle the array for better or more efficient
    // randomization

    // @param shuffleTranslation - an array of size V where values are in range 
    //     0 - V-1. shuffleTranslation[i] = some vertex v, where the relationship
    //     between i and v has been randomized. 
    private int[] greedyColoringShuffled(Graph G, int[] shuffleTranslation)
    {
        int V = G.V();
        assert V >= 1;
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
        VertexDegree[] arr = vertexDegrees;

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

    private int[] greedyDegreeOrderedShuffledTies(Graph G)
    {
        int V = G.V();
        VertexDegree[] arr = vertexDegrees;

        // This will be our coloring array.
        int[] color = new int[V];
        // In loop, we keep track of the maximum color used so far.
        int maxColor = 0;

        for (int i = G.V()-1; i >=0; i--)
        {
            if (i == 0 || arr[i].degree != arr[i-1].degree)
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
            else
            {
                degreeTiesExist = true;
                ArrayList<Integer> sameDegree = new ArrayList<Integer>();
                sameDegree.add(arr[i].vertex);
                while ( i > 0 && arr[i].degree == arr[i-1].degree)
                {
                    i--;
                    sameDegree.add(arr[i].vertex);
                }
                Collections.shuffle(sameDegree);

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
                }
            }
            
        }

        if (this.maxColor > maxColor)
            this.maxColor = maxColor;
        // All done, return the array.
        return color;
    }

    private int[] greedyNeighborOrdered(Graph G)
    {

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
    public static void main(String[] args)
    {
        // Usage message, if not at least one argument.
        if (args.length == 0) {
            System.err.println
                ("Usage: java GraphColoring graph.txt [TIMELIMIT]");
            System.exit(1);
        }
        // Read the Graph from the file named by args[0].
        Graph G = new Graph(new In(args[0]));
        // Get our time limit (in seconds).
        double secs = 3.0;      // default
        if (args.length > 1)
            secs = Double.parseDouble(args[1]);

        // We are ready to start coloring, start timer.
        long start = System.currentTimeMillis();

        // Compute fast initial coloring in its constructor.
        GraphColoring coloring = new GraphColoring(G);
        // Print it.
        StdOut.println(coloring);
        if (coloring.bugs()>0)
            warn("initial coloring has bugs!");

        // Now loop: each time that method tryImprove succeeds in
        // improving the coloring, we print it again.  We stop when we
        // run out of time, or when tryImprove returns false.
        int lastK = coloring.maxColor();
        while (true)
        {
            // How much time have we used already? (in seconds)
            double used = (System.currentTimeMillis()-start)/1000.0;
            if (used >= secs)   // out of time?
                break;
            // Ok, try to improve the coloring.
            if (!coloring.tryImprove(secs-used))
                break;          // tryImprove gave up, stop early
            // tryImprove succeeded!  Print the result.
            StdOut.println(coloring);
            if (coloring.bugs() > 0)
                warn("tryImprove coloring has bugs");
            // Check that it really was an improvement.
            int K = coloring.maxColor();
            if (K >= lastK)
                warn("tryImprove returned true, but not really improved");
            lastK = K;
        }
    }

    public static class VertexDegree implements Comparable<VertexDegree> 
    {
        public int degree;
        public int vertex;

        public VertexDegree(int v, int degree)
        {
            this.vertex = v;
            this.degree = degree;
        }

        public int compareTo (VertexDegree other)
        {
            return degree - other.degree;
        }
    }

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

        public int compareTo(VertexNeighborRanked other)
        {
            return this.neighborDegreeSum - other.neighborDegreeSum;
        }
    }
}
