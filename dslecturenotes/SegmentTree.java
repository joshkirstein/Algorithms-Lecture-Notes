class SegmentTree {
	
	//construct a segment tree for the array
	SegmentTree(int[] array) {
		this.array = array;
		n = array.length;
		tree = new int[4*n];
		left = new int[4*n];
		right = new int[4*n];
		build(0, 0, n-1);
	}

	//backing array
	int[] array;
	//length of the array
	int n;
	//this will store the sum for each node
	int[] tree;
	//stores the left endpoint of each node
	int[] left;
	//stores the right endpoint of each node
	int[] right;

	//recursively build the tree
	//node_idx is the current nodes idx
	//node_left and node_right are the left
	//and right endpoints of the current node's segment
	void build(int node_idx, int node_left, int node_right) {
		left[node_idx] = node_left;
		right[node_idx] = node_right;
		if (node_left == node_right) {
			//base case, the segment can't be
			//split anymore!!!!!!!!!!!
			tree[node_idx] = array[node_left];
		} else {
			//recursively build the children
			build(2*node_idx+1, node_left, (node_left+node_right)/2);
			build(2*node_idx+2, (node_left+node_right)/2+1, node_right);

			//we still need to compute tree[node_idx].
			//observe that the tree[idx] = tree[leftidx]+tree[rightidx]
			//(i.e., we're just taking the sum of the left and right children)
			tree[node_idx] = tree[2*node_idx+1] + tree[2*node_idx+2];
		}
	}

	//driver for the real set function
	void set(int set_idx, int value) {
		real_set(0, set_idx, value-array[set_idx]);
	}

	void real_set(int node_idx, int set_idx, int value) {
		//the updated value is not apart of this node or
		//any of its descendants, so we stop the search here
		if (set_idx > right[node_idx] || set_idx < left[node_idx])
			return;

		//fix current node's value
		tree[node_idx] += value;

		//recursively look to see if we need to fix children
		real_set(2*node_idx+1, set_idx, value);
		real_set(2*node_idx+2, set_idx, value);
	}

	//this function serves as the 'driver' (or API)
	//to our 'real_query' function
	//query_left and query_right are just the endpoints
	int query(int query_left, int query_right) {
		//initialize the real query starting at the root
		return real_query(0, query_left, query_right);
	}

	int real_query(int node_idx, int query_left, int query_right) {
		//determine the color of 'node_idx' and handle the color appropriately

		//query_left <= left[node_idx] <= right[node_idx] <= query_right
		if (left[node_idx] >= query_left && right[node_idx] <= query_right) {
			//this means 'node_idx' is COMPLETELY contained
			//within the bounds of the query....(GREEN NODE)

			return tree[node_idx]; // using fact (2)
		}
		else if (left[node_idx] > query_right || right[node_idx] < query_left) {
			//this means `node_idx' does not contain anything
			//within the bounds of the query....(RED NODE)

			return 0; // using fact (1)
		} else {
			//otherwise, this node is partially contained within
			//the query bounds....(ORANGE NODE)

			//in this case, we can't really figure out what to return,
			//so we just recursively look at our children and have them
			//figure it out
			return real_query(2*node_idx+1, query_left, query_right) +
					real_query(2*node_idx+2, query_left, query_right);
		}
	}
}