package embs;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * An implentation of the Kernighan-Lin heuristic algorithm for splitting a graph into 
 * two groups where the weights of the edges between groups (cutting cost) is minimised.
 * @author Jake Coxon
 *
 */
public class KernighanLin {
	public static KernighanLin process(Graph g) {
		return new KernighanLin(g);
	}
	
	public class VertexGroup extends HashSet<Vertex> {  
	  public VertexGroup(HashSet<Vertex> clone) { super(clone); }
	  public VertexGroup() { }
	}
	
	final private VertexGroup A, B;
	final private VertexGroup unswappedA, unswappedB;
	public VertexGroup getGroupA() { return A; }
  public VertexGroup getGroupB() { return B; }
	
	final private Graph graph;
  final private int partitionSize;
	
	private KernighanLin(Graph g) {
		this.graph = g;
		this.partitionSize = g.getVertices().size() / 2;
		
		if (g.getVertices().size() != partitionSize * 2) 
		  throw new RuntimeException("Size of vertices must be even");
		
		A = new VertexGroup();
		B = new VertexGroup();
		
		// Split vertices into A and B
		int i = 0;
		for (Vertex v : g.getVertices()) {
			(++i > partitionSize ? B : A).add(v);
		}
		unswappedA = new VertexGroup(A);
		unswappedB = new VertexGroup(B);
		
		doAllSwaps();
	}
	
	/** Performs |V|/2 swaps and chooses the one with least cut cost one **/
	private void doAllSwaps() {

	  LinkedList<Pair<Vertex>> swaps = new LinkedList<Pair<Vertex>>();
	  int minCost = Integer.MAX_VALUE, minId = -1;
	  
    for (int i = 0; i < partitionSize; i++) {
      int cost = doSingleSwap(swaps);
      if (cost < minCost) {
        minCost = cost; minId = i; 
      }
    }
    
    // Unwind swaps
    while (swaps.size()-1 > minId) {
      Pair<Vertex> pair = swaps.pop();
      // unswap
      swapVertices(A, pair.second, B, pair.first);
    }
	}
	
	/** Chooses the least cost swap and performs it **/
	private int doSingleSwap(Deque<Pair<Vertex>> swaps) {
    
    Pair<Vertex> maxPair = null;
    int maxGain = Integer.MIN_VALUE;
    
    for (Vertex v_a : unswappedA) {
      for (Vertex v_b : unswappedB) {
        
        Edge e = graph.findEdge(v_a, v_b);
        int edge_cost = (e != null) ? e.weight : 0;
        // Calculate the gain in cost if these vertices were swapped
        // subtract 2*edge_cost because this edge will still be an external edge
        // after swapping
        int gain = getVertexCost(v_a) + getVertexCost(v_b) - 2 * edge_cost;
        
        if (gain > maxGain) {
          maxPair = new Pair<Vertex>(v_a, v_b);
          maxGain = gain;
        }
        
      }
    }
    
    swapVertices(A, maxPair.first, B, maxPair.second);
    swaps.push(maxPair);
    unswappedA.remove(maxPair.first);
    unswappedB.remove(maxPair.second);
    
    return getCutCost();
	}

	/** Returns the difference of external cost and internal cost of this vertex.
	 *  When moving a vertex from within group A, all internal edges become external 
	 *  edges and vice versa. **/
  private int getVertexCost(Vertex v) {
    int cost = 0;

    boolean v1isInA = A.contains(v);
    
    for (Vertex v2 : graph.getNeighbors(v)) {
      boolean v2isInA = A.contains(v2);
      Edge edge = graph.findEdge(v, v2);
      
      if (v1isInA != v2isInA) // external
        cost += edge.weight;
      else
        cost -= edge.weight;
    }
    return cost;
  }
  
  /** Returns the sum of the costs of all edges between A and B **/
  public int getCutCost() {
    int cost = 0;

    for (Edge edge : graph.getEdges()) {
      Pair<Vertex> endpoints = graph.getEndpoints(edge);
      
      boolean firstInA = A.contains(endpoints.first);
      boolean secondInA= A.contains(endpoints.second);
      
      if (firstInA != secondInA) // external
        cost += edge.weight;
    }
    return cost;
  }
  
  /** Swaps va and vb in groups a and b **/
  private static void swapVertices(VertexGroup a, Vertex va, VertexGroup b, Vertex vb) {
    if (!a.contains(va) || a.contains(vb) ||
        !b.contains(vb) || b.contains(va)) throw new RuntimeException("Invalid swap");
    a.remove(va); a.add(vb);
    b.remove(vb); b.add(va);
  }
	
	

	
}
