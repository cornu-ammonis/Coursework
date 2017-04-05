/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS.  __ANDREW C JONES 4/1/17__
*/

// Homework: revise this file, WRITE YOUR NAME UPSTAIRS, see TODO items below.

// Given a maze of size N, the method findPath(maze) finds an open
// path from (0,0) to (N-1,N-1), if such a path exists.
//
// See main() comments for its command line usage.

// TODO: the current solution is recursive (dfs() calls itself).  You
// need to make it non-recursive.  One way is to use an explicit stack
// instead of the runtime stack.  You do not need to find exactly the
// same open paths as found by the given code.

// TODO(EC): modify findPath so it finds a *shortest* open path from
// (0,0) to (N-1,N-1), when one exists.  You can read about a method
// for this, "breadth first search", in Section 4.1 of your book.

// TODO(EC): define method findWallPath (it currently returns null).
// When findPath fails to find a path, there should be a "blocking"
// path of walls separating S and T.  This path can start at any wall
// on the top row or right column of the maze (i==0 or j==N-1), and
// end at any wall at the bottom row or left column of the maze
// (i==N-1 or j==0).  Two walls can be adjacent by a cardinal step OR
// a diagonal step (so, each wall has 8 potential neighbors).  Again,
// recursion is not allowed here.

// TODO(EC): Finding a wall path is good, finding a shortest wall path
// is even better.  Note that if S (or T) is a wall, it is a wall path
// of size one, the smallest possible.

// TODO: Write your name in the header!

// For grading, we ignore the main() method, so do what you like with
// that.  We only test your findPath and findWallPath methods.

public class PathFinder
{
	// this subclass of position has a custom method listNeighbors 
	// that will return all appropriate neighbors of the current position. 
	// if it is an open position, it will return 4 neighbors 
	// (up, down, left, right) if it is a wall (closed position) it 
	// will return 8 (those above and diagonals)
	static class PositionListedNeighbors extends Position 
	{
		public PositionListedNeighbors(int i, int j)
		{
			super(i, j); //call base class constructor
		}

		// returns a list of this positions neigbors which are 
		// valid candidates for traversal 
		public Deque<PositionListedNeighbors> listNeighbors()
		{
			Deque<PositionListedNeighbors> list = new LinkedDeque<PositionListedNeighbors>();

			//this is on an open path so only up down left right
			if (m.isOpen(this)) 
				for (int i = 0; i < 4; i++)
					list.addFirst(this.neighbor(i));
			else
				for (int i = 0; i < 8; i++)
					list.addFirst(this.neighbor(i));

			return list;
		}

		public PositionListedNeighbors neighbor(int direction) 
		{
			switch (direction) {
	            // cardinal directions:
	        case 0: return new PositionListedNeighbors(i-1, j  ); // 1 row up
	        case 1: return new PositionListedNeighbors(i+1, j  ); // 1 row down
	        case 2: return new PositionListedNeighbors(i  , j-1); // 1 column left
	        case 3: return new PositionListedNeighbors(i  , j+1); // 1 column right
	            // diagonal directions:
	        case 4: return new PositionListedNeighbors(i-1, j-1); // up and left
	        case 5: return new PositionListedNeighbors(i+1, j+1); // down and right
	        case 6: return new PositionListedNeighbors(i-1, j+1); // up and right
	        case 7: return new PositionListedNeighbors(i+1, j-1); // down and left
	        }
	        throw new RuntimeException("bad direction " + direction);			
		}
	}

	// this "phantom position" class is solely for the purpose of more 
	// efficiently implementing bfs for finding shortest wall path.
	// because thare are a range of possible "starting points" 
	// for the wall path, this phantom position is a vertex which is 
	// "neigbors" with (adjascent to) all those positions which are valid
	// start points for the wall path
	static class PhantomPosition extends PositionListedNeighbors 
	{
		public PhantomPosition(int i, int j) 
		{
			super(i, j); //call base class constructor
		}


		// this phantom position is adjascent to all positions in the first
		// column or last row 
		// i==N-1 or j==0
		public Deque<PositionListedNeighbors> listNeighbors() 
		{
			Deque<PositionListedNeighbors> list = new LinkedDeque<PositionListedNeighbors>();

			for (int j = 0; j < N; j++)
				list.addFirst(new PositionListedNeighbors(N-1, j));

			for (int i = 0; i < N; i++)
				list.addFirst(new PositionListedNeighbors(i, 0));

			return list;
		}
	}

    // Any data fields here should be private and static.  They exist
    // only as a convenient way to share search context between your
    // static methods here.   It should be possible to call your
    // findPath() method more than once, on different mazes, and
    // to get valid results for each maze.

	private static PhantomPosition phantom;
    private static int wallCount;

    // The maze we are currently searching, and its size.
    private static Maze m;      // current maze
    private static int N;       // its size (N by N)

    // The parent array:
    private static Position[][] parent;
    // In the path-finding routines: for each position p, as soon as
    // we find a route to p, we set parent[p.i][p.j] to be a position
    // one step closer to the start (i.e., the parent discovered p).

    // Get parent of p (assumes p in range)
    static Position getParent(Position p) { return parent[p.i][p.j]; }

    // Set parent of p, if not yet set.  Value indicates success.
    static boolean setParent(Position p, Position par) {
        if (getParent(p) != null)
            return false;       // p already has a parent
        parent[p.i][p.j] = par;
        return true;
    }

    public static Deque<Position> findPath(Maze maze)
    {
        m = maze;                           // save the maze
        N = m.size();                       // save size (maze is N by N)
        parent = new Position[N][N];        // initially all null
        Position S = new Position(0,0);     // start of open path
        Position T = new Position(N-1,N-1); // end of open path

        // If either of these is a wall, there is no open path.
        if (!m.isOpen(S)) return null;
        if (!m.isOpen(T)) return null;

        // GOAL: for each reachable open position p, parent[p.i][p.j]
        // should be an open position one step closer to S.  That is,
        // it is the position that first discovered a route to p.
        // These parent links will form a tree, rooted at S.

        // Compute parent for each position reachable from S.
        // Since S is the root, we will let S be its own parent.

        // Compute parent links, by recursive depth-first-search!
        //dfs(S, S);
        //dfsNonRecursive(S);
        bfs(S, T);
        // If T has no parent, it is not reachable, so no path.
        if (getParent(T)==null)
        {
            return null;
        }
        // Otherwise, we can reconstruct a path from S to T.
        Deque<Position> path = new LinkedDeque<Position>();
        for (Position u=T; !u.equals(S); u=getParent(u))
            path.addFirst(u);
        path.addFirst(S);
        return path;
    }

    // depth-first-search: set parent for each newly reachable p.
    private static void dfs(Position p, Position from)
    {
        if (!m.inRange(p) || !m.isOpen(p) || getParent(p) != null)
            return;
        // System.out.println("found " + p + " via parent " + from);
        setParent(p, from);
        // Now recursively try the four neighbors of p.
        for (int dir=0; dir<4; ++dir)
            dfs(p.neighbor(dir), p);
    }

    private static void dfsNonRecursive(Position start) 
    {
    	Deque<Position> queue = new LinkedDeque<Position>();
    	setParent(start, start);
    	queue.addLast(start);
    	
    	while(!queue.isEmpty())
    	{
    		Position current = queue.removeLast();
    		for (int i = 0; i < 4; i++)
    		{
    			Position neighbor = current.neighbor(i);
    			if(!m.inRange(neighbor) || !m.isOpen(neighbor) || getParent(neighbor) != null)
    				continue;
    			else
    			{
    				setParent(neighbor, current);
    				queue.addLast(neighbor);
    			}
    		}
    	}
    	return;
    }

    private static void bfs(Position start, Position target)
    {
    	Deque<Position> queue = new LinkedDeque<Position>();
    	setParent(start, start);
    	queue.addFirst(start);
    	while(!queue.isEmpty())
    	{
    		Position current = queue.removeLast();
    		for (int i = 0; i < 4; i++)
    		{
    			Position neighbor = current.neighbor(i);
    			if(!m.inRange(neighbor) || !m.isOpen(neighbor) || getParent(neighbor) != null)
    				continue;
    			else
    			{
    				setParent(neighbor, current);
    				if (neighbor.equals(target))
    					return;
    				queue.addFirst(neighbor);
    			}
    		}

    	}
    }

	// Return a wall path separating S and T, or null.
    // Note: must begin at i==0 or j==N-1 (first row or far right column)
    // must end at i==N-1 or j==0 (last row or far left column)
    public static Deque<Position> findWallPath(Maze maze)
    {
    	m = maze;
    	N = m.size();
    	Deque<Position> path = null;
    	phantom = new PhantomPosition(-1, -1);
    	Position start = bfsWallPhantom();
    	if (start == null)
    		return null;

    	path = unpackWallPathPhantom(start);
    	return path;
    }

    private static Position bfsWallPhantom()
    {
    	Deque<PositionListedNeighbors> queue = new LinkedDeque<PositionListedNeighbors>();
    	queue.addFirst(phantom);
    	while(!queue.isEmpty())
    	{
    		PositionListedNeighbors current = queue.removeLast();
    		for (PositionListedNeighbors neighbor : current.listNeighbors())
    		{
    			if (!m.inRange(neighbor) || !m.isWall(neighbor) || getParent(neighbor) != null)
    				continue;
    			else
    				{
    					setParent(neighbor, current);

    					//this means we reached a valid endpoint and have found
    					//the shortest wall path
    					if (neighbor.i == 0 || neighbor.j == N-1) 
    						return (Position) neighbor;

    					queue.addFirst(neighbor);
    				}
    		}
    	}

    	return null; //this means we didn't find a wall path
    }

    private static Deque<Position> unpackWallPathPhantom(Position start)
    {
    	Deque<Position> pth = new LinkedDeque<Position>();
    	for (Position u = start; !u.equals(phantom); u = getParent(u))
    		pth.addLast(u);
    	return pth;
    }

    private static Deque<Position> unpackWallPath(Position start, Position end) 
    {
    	Deque<Position> pth = new LinkedDeque<Position>();

    	for (Position u=start; !u.equals(end); u=getParent(u))
            pth.addLast(u);
        pth.addLast(end);
        return pth;
    	
    }

    private static Position bfsWall(Position end) 
    {
    	Deque<Position> queue = new LinkedDeque<Position>();
    	//setParent(end, end);
    	queue.addFirst(end);
    	wallCount = 1;
    	while(!queue.isEmpty())
    	{
    		Position current = queue.removeLast();
    		wallCount++;

    		for(int i = 0; i < 8; i++)
    		{
    			Position neighbor = current.neighbor(i);
    			if(!m.inRange(neighbor) || m.isOpen(neighbor) || getParent(neighbor) != null)
    				continue;
    			else {


    			setParent(neighbor, current);
    			if(neighbor.i == 0 || neighbor.j == N-1)
    				return neighbor;

    			queue.addFirst(neighbor);
    			}
    		}
    	}
    	wallCount = -1;
    	return null; // didn't find a valid startPoint
    }

    // Command-line usage:
    //
    //    java PathFinder ARGS...
    //
    // Constructs maze (using same rules as Maze.main()), prints it,
    // finds the paths (open path and/or wall path), and reprints the
    // maze with the path highlighted.
    public static void main(String[] args)
    {
        Maze m = Maze.mazeFromArgs(args);
        System.out.println(m);
        Deque<Position> oPath = findPath(m);
        if (oPath != null)
            System.out.println("findPath() found an open path of size "
                               + oPath.size());
        Deque<Position> wPath = findWallPath(m);
        if (wPath != null)
            System.out.println("findWallPath() found a wall path of size "
                               + wPath.size());
        if (oPath==null && wPath==null)  {
            System.out.println("WARNING: neither path was found");
            // This may be OK, if you are not doing findWallPath (EC).
            // No point in reprinting the map.
            return;
        }
        if (oPath != null && wPath != null) // crossing?
            System.out.println("WARNING: cannot have both paths!");

        // Copy map of maze, and mark oPath with 'o', wPath with 'w'.
        char[][] map = m.copyArray();
        if (oPath != null)
            for (Position p: oPath)
                map[p.i][p.j] = 'o';
        if (wPath != null)
            for (Position p: wPath)
                map[p.i][p.j] = 'w';
        // Now print the marked map.
        System.out.println(Maze.toString(map));
    }

    // Java "defensive programming": we should not instantiate this
    // class.  To enforce that, we give it a private constructor.
    private PathFinder() {}
}
