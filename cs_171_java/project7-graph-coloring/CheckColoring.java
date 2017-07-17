// NOTE: to compile and run this, put graph.jar on your CLASSPATH
// (or unpack it in the same directory as this file).

// You may use this program to check output from GraphColoring.
//
// Usage:
//    java GraphColoring graph.txt > out.txt  # capture output
//    java CheckColoring graph.txt out.txt    # check output
//
// This program expects two filename arguments.  It reads a Graph from
// the first file (in Sedgewick format), and a sequence of one or more
// colorings from the second file (in GraphColoring.toString() format).
// For each coloring, it checks whether it is a valid coloring of the
// Graph.  This program immediately exits (with code 1) on any error.
// If all the colorings are valid, it reports the best K seen.

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class CheckColoring
{
    // Return null if colors is a valid K coloring of G, else return a
    // String describing what is wrong with it.
    public static String check(Graph G, int[] colors, int K)
    {
        int V = G.V();
        if (colors.length != V)
            return "V is " + V + ", but colors.length is " + colors.length;
        for (int v=0; v<V; ++v)
        {
            int c = colors[v];
            if (c < 1)
                return "colors["+v+"] is " + c + " (less than 1)";
            if (c > K)
                return "colors["+v+"] is " + c + " (more than K="+K+")";
            for (int u: G.adj(v))
                if (u != v && colors[u] == c)
                    return v + " and " + u + " have same color " + c;
        }
        return null;            // valid K coloring
    }

    static void rte(String msg) { throw new RuntimeException(msg); }

    public static void main(String[] args) throws IOException
    {
        Graph G = new Graph(new In(args[0]));
        int count=0, bestK = G.V(); // number of colorings, best K
        LineNumberReader lines = new LineNumberReader(new FileReader(args[1]));
        // We read colorings until we reach end of file (EOF).
        try {
            while (true)
            {
                // Get the first non-blank input line.
                String line = lines.readLine();
                while (line != null && line.length()==0)
                    line = lines.readLine();
                if (line==null) // end of input, all done.
                    break;
                // First line should have two int tokens: V K
                String[] toks = line.split(" ");
                if (toks.length != 2)
                    rte("expected 2 tokens, got " + toks.length);
                int V = Integer.parseInt(toks[0]);
                int K = Integer.parseInt(toks[1]);
                // Second line should have V int tokens: C0 C1 ...
                line = lines.readLine();
                if (line == null)
                    rte("got EOF instead of second line");
                toks = line.split(" ");
                if (toks.length != V)
                    rte("expected " + V + " colors, got "
                                               + toks.length);
                int [] colors = new int[V];
                for (int v=0; v<V; ++v)
                    colors[v] = Integer.parseInt(toks[v]);
                // Ok we have the coloring, now check it.
                String bug = check(G, colors, K);
                if (bug != null)
                    rte("bad coloring! " + bug);
                ++count;
                System.out.println("valid K=" + K + " coloring");
                if (K < bestK) bestK = K;
            }
            if (count==0)
                rte("no colorings were found!");
        } catch (Exception e) {
            String msg = args[1] + ":" + lines.getLineNumber() + ": ";
            if (e instanceof RuntimeException)
                msg += e.getMessage();
            else
                msg += "caught " + e;
            System.err.println(msg);
            System.exit(1);
        }
        System.out.println("Read " + count + " valid colorings, "
                           + "the best K was " + bestK);
    }
}
