import java.util.*;

class Potholes {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int cases = sc.nextInt();
		while (cases-->0) {
			int numNodes = sc.nextInt();
			EdmondsKarp mf = new EdmondsKarp(numNodes);
			for (int i = 0; i < numNodes-1; i++) {
				int numEdges = sc.nextInt();
				for (int j = 0; j < numEdges; j++) {
					int to = sc.nextInt()-1;
					mf.addEdge(i, to, (i==0||to==(numNodes-1)) ? 1 : 100000);
				}
			}
			System.out.println(mf.maxflow(0, numNodes-1));
		}
	}
}
class EdmondsKarp {
	//capacity[i][j] = the capacity of the edge going from i to j
	int[][] capacity;

	//flow[i][j] = the flow going from i to j
	//0 <= flow[i][j] <= capacity[i][j]
	int[][] flow;

	//the number of nodes in our graph.
	int N;

	public EdmondsKarp(int numNodes) {
		this.capacity = new int[numNodes][numNodes];
		this.flow = new int[numNodes][numNodes];
		this.N = numNodes;
	}

	//add an edge going from `from' to `to'
	//with capacity `cap'
	//note that this code FAILS when a pair of nodes
	//have two edges between them
	void addEdge(int from, int to, int cap) {
		capacity[from][to] = cap;
		flow[from][to] = 0;
	}

	//compute the maximum flow from `src' to `snk'
	int maxflow(int src, int snk) {
		int maximum_flow = 0;
		while (true) {
			//step (1) find an augmenting path
			int[] path = find_path(src, snk);

			//stop if no augmenting path exists!
			if (path == null) break;

			//step (2) augment the path
			maximum_flow += augment(path);

			//step (3) rinse and repeat
			continue;
		}
		return maximum_flow;
	}

	//find_path finds an augmenting path (in the residual graph)
	//from `src' to `snk'
	//if a path exists, we return it as a sequence of the vertices
	//we find the path with breadth-first search.
	int[] find_path(int src, int snk) {
		Queue<Integer> q = new LinkedList<Integer>();
		//pred[i] stores the predecessor of node i
		//along its shortest path from src to snk.
		int[] pred = new int[N];
		q.add(src);
		//-1 means it is the src, -2 means it hasnt been visited
		Arrays.fill(pred, -2);
		pred[src] = -1;
		while (!q.isEmpty()) {
			int front = q.poll();
			for (int next = 0; next < N; next++) {
				//check if we can move go along (front->next)
				//edge in the residual graph
				//we can move along this edge iff:
				//it's a blue edge: (capacity[front][next]-flow[front][next] > 0)
				//it's an orange edge: (flow[next][front] > 0)
				//notice for the orange edge we check flow[next][front]
				//(the reverse)
				if ((capacity[front][next]-flow[front][next]) > 0 
					|| flow[next][front] > 0) {
					//we need to make sure we havent visited `next' yet!
					if (pred[next] == -2) {
						q.add(next);
						pred[next] = front;
					}
				}
			}
		}
		//snk wasnt visited...there is no path to it!
		if (pred[snk] == -2) {
			return null;
		}
		//the path is stored in reverse, so we use
		//a stack to order it properly
		Stack<Integer> path = new Stack<Integer>();
		int cur = snk;
		while (true) {
			path.add(cur);
			cur = pred[cur];
			if (cur == -1) break;
		}
		int[] array_path = new int[path.size()];
		int idx = 0;
		while (!path.isEmpty()) {
			array_path[idx++] = path.pop();
		}
		return array_path;
	}

	//augment the path (add/subtract the minimum edge weight)
	//along the path depending on the edges color
	//`path' is given as a sequence of vertices along the path
	//we return the amount of flow we send from the source to the sink
	int augment(int[] path) {
		int min_edge_weight = Integer.MAX_VALUE;
		//first we find the minimum edge weight on this path:
		for (int i = 0; i < path.length-1; i++) {
			int from = path[i];
			int to = path[i+1];
			int test_forward = capacity[from][to] - flow[from][to];
			int test_reverse = flow[to][from];
			//only one of these is true
			//if it's the first one, then this is a blue edge
			//if the second one is, then it's an orange edge
			if (test_forward > 0) {
				min_edge_weight = Math.min(min_edge_weight, test_forward);
			} else if (test_reverse > 0) {
				min_edge_weight = Math.min(min_edge_weight, test_reverse);
			}
		}
		for (int i = 0; i < path.length-1; i++) {
			int from = path[i];
			int to = path[i+1];
			int test_forward = capacity[from][to] - flow[from][to];
			int test_reverse = flow[to][from];
			if (test_forward > 0) {
				//if its a forward edge, we add to flow[from][to]:
				flow[from][to] += min_edge_weight;
			} else if (test_reverse > 0) {
				//if its a reverse edge, we subtract from flow[to][from]:
				flow[to][from] -= min_edge_weight;
			}
		}
		//we just sent `min_edge_weight' flow from the source 
		//to the sink
		return min_edge_weight;
	}
}