/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
A TUTOR OR CODE WRITTEN BY OTHER STUDENTS.  
__ANDREW C JONES 4/1/17 - 4/10/17__
*/

// NOTE - this file contains three nested static classes at the
// bottom of the file (which I wrote myself, consulting the Sedgwick 
// book in the case of the minheap) -- much of the magic happens here. 
// I would normally put these in separate files, but nesting made it  
// easier when repeatedly uploading to mimir for testing.
//
// The classes are as follows:
//
// @CLASS PositionLN, or position listed neighbor, extends Position 
// and implements Comparable<PositionLN> - it has a listNeighbors()
// method which will list the 4 reachable neighbors of an 
// open position and the 8 of a wall (conveniently delineating so that
// it can be reused between findpath and findwallpath)
// it also has a distance property which is used to A* search, and 
// a its compareTo method is based on this property,
// which makes it easier to implement the custom minHeap queue for A*
//
// @CLASS PhantomPosition - extends PositionLN - differs from PositionLN
// in that its listNeighbors function instead returns all positions
// in the first column or last row (j == 0 or i == N-1)
// this allows us to implement bfs on a "range" of starting positions 
// for the findWallPath algorithm. when reconstructing the path, 
// PhantomPosition is omitted, so the final result is a path of just walls
// which is one of the shortes possible paths (or null if DNE)
//
// @CLASS MinHeapPositions

// Deleted all non-clarifying comments from the original file

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
    // Any data fields here should be private and static.  They exist
    // only as a convenient way to share search context between your
    // static methods here.   It should be possible to call your
    // findPath() method more than once, on different mazes, and
    // to get valid results for each maze.

	// see PhantomPosition class at bottom of file for details:
	// this is a fake position which is adjascent to 
	// a range of starting positions for wall paths. 
	// it helps to simplify bfs for wallpaths. 
	private static PhantomPosition phantom;

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
        PositionLN S = new PositionLN(0,0);     // start of open path
        PositionLN T = new PositionLN(N-1,N-1); // end of open path

        // If either of these is a wall, there is no open path.
        if (!m.isOpen(S)) return null;
        if (!m.isOpen(T)) return null;

        // GOAL: for each reachable open position p, parent[p.i][p.j]
        // should be an open position one step closer to S.  That is,
        // it is the position that first discovered a route to p.
        // These parent links will form a tree, rooted at S.

        // compute parent links using A* search
        aStarBFS(S, T);
        
        // If T has no parent, it is not reachable, so no path.
        if (getParent(T)==null)
        {
            return null;
        }
      
        // Otherwise, we can reconstruct a path from S to T.
        Deque<Position> path = new LinkedDeque<Position>();
        
        // beginning at the end of the path, adds each position's parent 
        // to the front of the path queue, effectively reconstructing the path
        for (Position u=T; !u.equals(S); u=getParent(u))
            path.addFirst(u);
        path.addFirst(S);
        return path;
    }


    // deprecated method left for comparison -- final version of FindPath
    // uses the aStarBFS method below

    // @param start - the beginning position for bfs 
    // @param target - the endpoint to which we want to find the 
    //     shortest path, if there is one 
    // 
    private static void bfs(Position start, Position target)
    {
    	
    	// fifo queue used to investigate vertices in the order in which 
    	// they are found
    	Deque<Position> queue = new LinkedDeque<Position>();
    	
    	//start is its own parent (prevents it from being traversed again)
    	setParent(start, start);

    	//initialize queue so that the loop may proceed
    	queue.addFirst(start);
    	
    	//runs until we have exhausted all options or have found the target
    	while(!queue.isEmpty())
    	{
    		Position current = queue.removeLast();

    		// for each of the current position's neighbors
    		for (int i = 0; i < 4; i++)
    		{
    			Position neighbor = current.neighbor(i);

    			// if the neighbor is out or range, is a wall, or has been seem
    			// already, we skip it
    			if(!m.inRange(neighbor) || !m.isOpen(neighbor) || getParent(neighbor) != null)
    				continue; //skip 
    			
    			else
    			{
    				// current node is neighbor's parent
    				setParent(neighbor, current);

    				// if it's target we are done
    				if (neighbor.equals(target))
    					return;

    				//otherwise add the neighbor to the front of the queue 
    				//so that it is processed after all other 
    				//verticies at current's layer
    				queue.addFirst(neighbor);
    			}
    		}

    	}
    }

    // implementation A* bfs which is in this case about 30% more efficient 
    // than normal bfs. the algorithm is inspired by the approach given in
    // lecture. it uses manhattan distance as the estimation function 
    // for a neighbor's distance to the target, and 
    // uses a minheap to traverse neighbors with the shortest 
    // estimated distance first.

    // see the PositionLN class at the bottom of the file(under main)
    // for details about its API, but it is basically a modified 
    // verison of Position with a method listNeighbors() which returns
    // an iterable of the position's neighbors (4 if open, 8 if a wall)
    // because those are the neighbors under consideration for findpath
    // and findwallpath respectively

    // see the minHeapPositions class at the bottom of the file for 
    // details, but essentially it is an implementation of minHeap where 
    // the "key" value for a position p is p.distance, where distance 
    // is the estimated manhattan distance to target plus the known 
    // distance from origin.
    // heap.removeMin will return the position in the heap with the smallest
    // such distance, and will remove it from the heap.


    // @param start - the beginning position for bfs 
    // @param target - the endpoint to which we want to find the 
    //     shortest path, if there is one 
    private static void aStarBFS(PositionLN start, PositionLN target)
    {
    	// initializes a min heap with an initiual size of 
    	// half the total board
    	minHeapPositions heap = new minHeapPositions((N*N)/2);

    	setParent(start, start);
    	start.distanceFromOrigin = 0; //starts distance from itself is 0

    	// estimated distance (best case distance) to the target is it's 
    	// manhattan distance, which is the 
    	start.distance = manhattanDistance(start, target);
    	heap.add(start);
    	
    	while(heap.count > 0)
    	{
    		PositionLN current = heap.removeMin();

    		// iterates over each neighbor of current
    		for (PositionLN neighbor : current.listNeighbors())
    		{
    			// if the neighbor is out or range, is a wall, or has been seem
    			// already, we skip it
    			if (!m.inRange(neighbor) || !m.isOpen(neighbor) || getParent(neighbor) != null)
    				continue;

    			// current is neighbor's parent
    			setParent(neighbor, current);

    			// if neighbor is the target we're done 
    			if (neighbor.equals(target))
    			{
    				//saves total distance for some testing i did 
    				target.distanceFromOrigin = current.distanceFromOrigin + 1;
    				heap = null; // help with garbage collection
    				return;
    			}

    			// a node's distance from origin is its parents + 1
    			neighbor.distanceFromOrigin = current.distanceFromOrigin + 1;

    			// estimated distance is actual distance from origin 
    			// + manhattan distance to target
    			neighbor.distance = neighbor.distanceFromOrigin + manhattanDistance(neighbor, target);
    			heap.add(neighbor); // add neighbor to heap
    		}
    	}
    }
	

	//formula taken from stackoverflow:
	// int distance = Math.abs(x1-x0) + Math.abs(y1-y0);
    private static int manhattanDistance(Position from, Position to)
    {
    	return Math.abs(from.i - to.i) + Math.abs(from.j - to.j);
    }

	// Return a wall path separating S and T, or null.
    
    // Note: must begin at i==0 or j==N-1 (first row or far right column)
    // must end at i==N-1 or j==0 (last row or far left column)
    public static Deque<Position> findWallPath(Maze maze)
    {
    	m = maze;
    	N = m.size();
    	parent = new Position[N][N]; 
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
    	Deque<PositionLN> queue = 
    		new LinkedDeque<PositionLN>();
    	queue.addFirst(phantom);
    	while(!queue.isEmpty())
    	{
    		PositionLN current = queue.removeLast();
    		for (PositionLN neighbor : current.listNeighbors())
    		{
    			if (!m.inRange(neighbor) || !m.isWall(neighbor) || 
    				getParent(neighbor) != null)
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

    // Command-line usage:
    //
    //    java PathFinder ARGS...
    //
    // Constructs maze (using same rules as Maze.main()), prints it,
    // finds the paths (open path and/or wall path), and reprints the
    // maze with the path highlighted.
    public static void main(String[] args)
    {
        
        //System.out.println(m);

      	for (int i = 0; i < args.length * 2; i++){
      	String[] arg = {args[i/2]};
      	Maze m = Maze.mazeFromArgs(arg);
        Deque<Position> oPath = findPath(m);
        if (oPath != null)
            System.out.println("findPath() found an open path of size "
                               + oPath.size());
        //Deque<Position> oPathRegularBFS = findPathRegularBFS(m);
        //if (oPathRegularBFS != null)
        //	System.out.println("findPathRegular() found an open path of size "
        //                       + oPath.size());
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

        /*
        minHeapPositions heapTest = new minHeapPositions(1);
        PositionLN a = new PositionLN(1, 2);
        a.distance = 4;
        PositionLN b = new PositionLN(4, 5);
        b.distance = 5;
        PositionLN c = new PositionLN(3, 4);
        c.distance = 2;
        PositionLN d = new PositionLN(6, 7);
        d.distance = 3;

        heapTest.add(a);
        heapTest.add(b);
        heapTest.add(c);
        heapTest.add(d);

        while(heapTest.N > 3)
        	System.out.println(heapTest.removeMin().distance + "\n");

        PositionLN e = new PositionLN(8, 9);
        e.distance = 80;
        heapTest.add(e);
        PositionLN f = new PositionLN(3, 8);
        f.distance = 4;
        heapTest.add(f);
        PositionLN g = new PositionLN(1, 2);
        g.distance = 2;
        heapTest.add(g);

        while(heapTest.N > 0)
        	System.out.println(heapTest.removeMin().distance + "\n"); */


    }

    // Java "defensive programming": we should not instantiate this
    // class.  To enforce that, we give it a private constructor.
    private PathFinder() {}

    //taken from given code for EMD
    public static class KVPair<K, V> 
    {
        public K key;
        public V value;

        public KVPair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return this.key + ": " + this.value;
        }
    }

   	// PositionLN - Position (with) Listed Neighbors
    // this subclass of position has a custom method listNeighbors 
	// that will return all appropriate neighbors of the current position. 
	// if it is an open position, it will return 4 neighbors 
	// (up, down, left, right) if it is a wall (closed position) it 
	// will return 8 (those above and diagonals)
	static class PositionLN extends Position implements Comparable<PositionLN>
	{
		public PositionLN(int i, int j)
		{
			super(i, j); //call base class constructor
			distance = Integer.MAX_VALUE;
		}

		public int distance;
		public int distanceFromOrigin;

		// returns a list of this positions neigbors which are 
		// valid candidates for traversal 
		public Deque<PositionLN> listNeighbors()
		{
			Deque<PositionLN> list = 
				new LinkedDeque<PositionLN>();

			//this is on an open path so only up down left right
			if (m.isOpen(this)) 
				for (int i = 0; i < 4; i++)
					list.addFirst(this.neighbor(i));
			else
				for (int i = 0; i < 8; i++)
					list.addFirst(this.neighbor(i));

			return list;
		}

		public PositionLN neighbor(int direction) 
		{
			switch (direction) {
	            // cardinal directions:
	        case 0: return new PositionLN(i-1, j  ); // 1 row up
	        case 1: return new PositionLN(i+1, j  ); // 1 row down
	        case 2: return new PositionLN(i  , j-1); // 1 column left
	        case 3: return new PositionLN(i  , j+1); // 1 column right
	            // diagonal directions:
	        case 4: return new PositionLN(i-1, j-1); // up and left
	        case 5: return new PositionLN(i+1, j+1); // down and right
	        case 6: return new PositionLN(i-1, j+1); // up and right
	        case 7: return new PositionLN(i+1, j-1); // down and left
	        }
	        throw new RuntimeException("bad direction " + direction);			
		}

		public int compareTo(PositionLN p)
		{
			if (this.distance > p.distance) return 1;
			if (this.distance < p.distance) return -1;
			return 0;
		}
	}

	// this "phantom position" class is solely for the purpose of more 
	// efficiently implementing bfs for finding shortest wall path.
	// because thare are a range of possible "starting points" 
	// for the wall path, this phantom position is a vertex which is 
	// "neigbors" with (adjascent to) all those positions which are valid
	// start points for the wall path
	static class PhantomPosition extends PositionLN 
	{
		public PhantomPosition(int i, int j) 
		{
			super(i, j); //call base class constructor
		}


		// this phantom position is adjascent to all positions in the first
		// column or last row 
		// i==N-1 or j==0
		public Deque<PositionLN> listNeighbors() 
		{
			Deque<PositionLN> list = 
				new LinkedDeque<PositionLN>();

			for (int j = 0; j < N; j++)
				list.addFirst(new PositionLN(N-1, j));

			for (int i = 0; i < N; i++)
				list.addFirst(new PositionLN(i, 0));

			return list;
		}
	}


    // heap data structure which sorts PositionListedNeighbor instances 
    // according to the value of their distance property such that the 
    // smallest distance is always returned first
    
    // note that an element found at position i has its "parent" at 
    // position (i-1)/2 (unless it is the first element)
    // and that an element at position i has its children at positions
    // 2*i +1 and 2*i +2 (if they exist)
    static class minHeapPositions 
    {
    	//count of elements currently in the list. also points to the first
    	//available index.
    	private int count;
    	private PositionLN[] heap;
    	 
    	//constructor with a specified capacity, auto doubles when capacity is reached
    	public minHeapPositions(int capacity) 
    	{
    		heap = new PositionLN[capacity];
    	}

    	//constructor with default capacity, auto-doubles when capacity is reached
    	public minHeapPositions()
    	{
    		heap = new PositionLN[50];
    	}


    	//adds position p to the current heap and doubles heap capacity if it is full
    	public void add(PositionLN p)
    	{
    		if (count == 0)
    		{
    			heap[count++] = p;
    			return;
    		}

    		if (count >= heap.length)
    			doubleHeap();

    		heap[count] = p;
    		if (p.distance < heap[(count-1)/2].distance)
    			swim(count);
    		count++;
    	}

    	public PositionLN removeMin()
    	{
    		if (count == 0)
    			throw new IllegalStateException("no item to remove");
    		PositionLN toReturn = heap[0];
    		heap[0] = heap[--count];
    		heap[count] = null;

    		if (count > 0)
    			sink(0);

    		return toReturn;
    	}


    	//returns true if heap[i] < heap[j] false otherwise
    	private boolean less(int i, int j)
    	{
    		if (i >= count || j >= count)
    			throw new IllegalStateException("invalid less call - indexes out of range");

    		return heap[i].distance < heap[j].distance;
    	}
    	private void swim(int i)
    	{
    		while (i > 0)
    		{
    			if (heap[i].distance < heap[(i-1)/2].distance)
    			{
    				swap(i, (i-1)/2);
    				i = (i-1)/2;
    			} 
    			else
    				break;
    		}
    	}

    	private void sink(int i)
    	{
    		while((2*i + 1) < count)
    		{
    			int j = 2*i + 1;
    			//if the other child is smaller select it
    			if (j+1 < count && less(j+1, j)) j++;

    			//if i is smaller than both its children we're done
    			if (less(i, j)) break;
    			swap(i, j);
    			i = j;
    		}
    	}

    	private void swap(int i, int j)
    	{
    		PositionLN tmp = heap[j];
    		heap[j] = heap[i];
    		heap[i] = tmp;
    	}

    	private void doubleHeap()
    	{
    		PositionLN[] newHeap = new PositionLN[2*count];
    		for(int i = 0; i < count; i++)
    			newHeap[i] = heap[i];

    		heap = newHeap;
    		
    	}
    }


}
