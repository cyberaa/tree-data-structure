package RedBlackTree;

import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.dv.xs.YearDV;

import RedBlackTree.Queue;

public class RBT<Key extends Comparable<Key>, Data> {

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private Node root; // root of the BST
	private Node nil;

	public RBT() {
		this.nil.color = BLACK;
		this.nil.prev = null;
		this.nil.right = null;
		this.nil.left = null;
	}

	// BST helper node data type
	private class Node {
		private Key key; // key
		private Data data; // associated data
		private Node left, right, prev; // links to left and right subtrees
		private boolean color; // color of parent link
		private int N = 0;

		public Node(Key key, Data data) {
			if (key == null || data == null) {
				throw new NullPointerException(
						"Either Key or Data is Null for Key : " + key
								+ " or Data :" + data);
			}
			this.key = key;
			this.data = data;

		}
	}

	/*************************************************************************
	 * Inorder Tree Walk
	 *************************************************************************/
	private void printTree(Node node) {
		if (node != null) {
			printTree(node.left);
			System.out.println(node.key + " - " + node.data);
			printTree(node.right);
		}
	}

	// Here first we insert root then as we go left or right we insert returned
	// <key,value> of "iterateTreetoHashMap"
	// into map and keep on adding iterating and adding.

	private Map<Comparable<Key>, Data> iterateTreetoHashMap(Node node) {
		Map<Comparable<Key>, Data> map = new HashMap<Comparable<Key>, Data>();
		if (node != null) {
			map.putAll(iterateTreetoHashMap(node.left)); // every time it
															// iterates left
															// will return a map
															// and we put all
															// data in same map
			map.put(node.key, node.data); // First time it will insert root in
											// map
			map.putAll(iterateTreetoHashMap(node.right)); // every time it
															// iterates right
															// will return a map
															// and we put all
															// data in same map
		}
		return map;
	}

	/*************************************************************************
	 * Rotate Trees
	 *************************************************************************/

	public void rightLeft(Node x) {
		Node y = x.right;
		x.right = y.left;
		if (y.left != null)
			y.left.prev = x;
		y.prev = x.prev;

		if (x.prev == null)
			root = y;
		else if (x == x.prev.left)
			x.prev.left = y;
		else
			x.prev.right = y;
		y.left = x;
		x.prev = y;
	}

	public void rightRight(Node x) {
		Node y = x.left;
		x.left = y.right;
		if (y.right != null)
			y.right.prev = x;
		y.prev = x.prev;

		if (x.prev == null)
			root = y;
		else if (x == x.prev.right)
			x.prev.right = y;
		else
			x.prev.left = y;
		y.right = x;
		x.prev = y;
	}

	/*************************************************************************
	 * Inserting Node
	 *************************************************************************/

	/* Insert a Node */
	public void insert(Key key, Data data) {
		Node z = new Node(key, data); // Node to be inserted
		Node y = nil; //
		Node x = root; // let x be the pointer to be compared and moved starting
						// from root node
		while (x != nil) { // parse till the end of tree
			y = x; // intially node y stores root

			// compare key of node to be inserted(Z) to root key(x.key)
			if (z.key.compareTo(x.key) < 0) // if(inserting node key is less
											// than root key move pointer x to )
				x = x.left;
			else
				// else move pointer to right
				x = x.right;
		}
		z.prev = y; // after moving pointer to left or right make root node as
					// prev of inserting node

		if (y == nil) { // if no root make inserting node as root node
			root = z;
			System.out.println("Making root as +" + z.key + " Previous : null");
		} else if (z.key.compareTo(y.key) < 0) { // compare key of inserting
													// node to root node
			y.left = z;
			System.out.println("Inserting : " + z.key + " Z.Previous :"
					+ z.prev.key);
		} else if (z.key.compareTo(y.key) > 0) {
			y.right = z;
			System.out.println("Inserting : " + z.key + " Z.Previous :"
					+ z.prev.key);
		} else if (z.key.compareTo(y.key) == 0) { // key already exists so
													// Replaceable value
			y.data = z.data;
			System.out.println("Replacing : " + z.key + " Y.Previous :"
					+ y.prev.key);
		}
		root.N = root.N + 1;

		z.left = nil;
		z.right = nil;
		z.color = RED;
		insert_fixup(z);

		// assert validate();
	}

	private void insert_fixup(Node z) {

		Node y;
		while (z.prev.color == RED) {
			if (z.prev == z.prev.prev.left) {
				y = z.prev.prev.right;
				if (y.color == RED) {
					z.prev.color = BLACK;
					y.color = BLACK;
					z.prev.prev.color = RED;
					z = z.prev.prev;
				} else if (z == z.prev.right) {
					z = z.prev;
					rotateLeft(z.prev.prev);
					z.prev.color = BLACK;
					z.prev.prev.color = RED;
					rotateRight(z.prev.prev);
				} else {
					z = z.prev;
					rotateRight(z.prev.prev);
					z.prev.color = BLACK;
					z.prev.prev.color = RED;
					rotateLeft(z.prev.prev);
				}
			}
		}
		root.color = BLACK;
	}

	/*************************************************************************
	 * Node helper methods
	 *************************************************************************/
	// is node x red; false if x is null ?
	private boolean isRed(Node x) {
		if (x == null)
			return false;
		return (x.color == RED);
	}

	// number of node in subtree rooted at x; 0 if x is null
	private int size(Node x) {
		if (x == null)
			return 0;
		return x.N;
	}

	/*************************************************************************
	 * Size methods
	 *************************************************************************/

	// return number of key-value pairs in this symbol table
	public int size() {
		return size(root);
	}

	// is this symbol table empty?
	public boolean isEmpty() {
		return root == null;
	}

	/*************************************************************************
	 * red-black tree helper functions
	 *************************************************************************/

	// make a left-leaning link lean to the right
	private Node rotateRight(Node h) {
		assert (h != null) && isRed(h.left);
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// make a right-leaning link lean to the left
	private Node rotateLeft(Node h) {
		assert (h != null) && isRed(h.right);
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// flip the colors of a node and its two children
	private void flipColors(Node h) {
		// h must have opposite color of its two children
		assert (h != null) && (h.left != null) && (h.right != null);
		assert (!isRed(h) && isRed(h.left) && isRed(h.right))
				|| (isRed(h) && !isRed(h.left) && !isRed(h.right));
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	// Assuming that h is red and both h.left and h.left.left
	// are black, make h.left or one of its children red.
	private Node moveRedLeft(Node h) {
		assert (h != null);
		assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

		flipColors(h);
		if (isRed(h.right.left)) {
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
		}
		return h;
	}

	// Assuming that h is red and both h.right and h.right.left
	// are black, make h.right or one of its children red.
	private Node moveRedRight(Node h) {
		assert (h != null);
		assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
		flipColors(h);
		if (isRed(h.left.left)) {
			h = rotateRight(h);
		}
		return h;
	}

	// restore red-black tree invariant
	private Node balance(Node h) {
		assert (h != null);

		if (isRed(h.right))
			h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left))
			h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))
			flipColors(h);

		h.N = size(h.left) + size(h.right) + 1;
		return h;
	}

	/*************************************************************************
	 * Utility functions
	 *************************************************************************/

	// height of tree (1-node tree has height 0)
	public int height() {
		return height(root);
	}

	private int height(Node x) {
		if (x == null)
			return -1;
		return 1 + Math.max(height(x.left), height(x.right));
	}

	/*************************************************************************
	 * Ordered symbol table methods.
	 *************************************************************************/

	// the smallest key; null if no such key
	public Key min() {
		if (isEmpty())
			return null;
		return min(root).key;
	}

	// the smallest key in subtree rooted at x; null if no such key
	private Node min(Node x) {
		assert x != null;
		if (x.left == null)
			return x;
		else
			return min(x.left);
	}

	// the largest key; null if no such key
	public Key max() {
		if (isEmpty())
			return null;
		return max(root).key;
	}

	// the largest key in the subtree rooted at x; null if no such key
	private Node max(Node x) {
		assert x != null;
		if (x.right == null)
			return x;
		else
			return max(x.right);
	}

	// the largest key less than or equal to the given key
	public Key floor(Key key) {
		Node x = floor(root, key);
		if (x == null)
			return null;
		else
			return x.key;
	}

	// the largest key in the subtree rooted at x less than or equal to the
	// given key
	private Node floor(Node x, Key key) {
		if (x == null)
			return null;
		int cmp = key.compareTo(x.key);
		if (cmp == 0)
			return x;
		if (cmp < 0)
			return floor(x.left, key);
		Node t = floor(x.right, key);
		if (t != null)
			return t;
		else
			return x;
	}

	// the smallest key greater than or equal to the given key
	public Key ceiling(Key key) {
		Node x = ceiling(root, key);
		if (x == null)
			return null;
		else
			return x.key;
	}

	// the smallest key in the subtree rooted at x greater than or equal to the
	// given key
	private Node ceiling(Node x, Key key) {
		if (x == null)
			return null;
		int cmp = key.compareTo(x.key);
		if (cmp == 0)
			return x;
		if (cmp > 0)
			return ceiling(x.right, key);
		Node t = ceiling(x.left, key);
		if (t != null)
			return t;
		else
			return x;
	}

	// the key of rank k
	public Key select(int k) {
		if (k < 0 || k >= size())
			return null;
		Node x = select(root, k);
		return x.key;
	}

	// the key of rank k in the subtree rooted at x
	private Node select(Node x, int k) {
		assert x != null;
		assert k >= 0 && k < size(x);
		int t = size(x.left);
		if (t > k)
			return select(x.left, k);
		else if (t < k)
			return select(x.right, k - t - 1);
		else
			return x;
	}

	// number of keys less than key
	public int rank(Key key) {
		return rank(key, root);
	}

	// number of keys less than key in the subtree rooted at x
	private int rank(Key key, Node x) {
		if (x == null)
			return 0;
		int cmp = key.compareTo(x.key);
		if (cmp < 0)
			return rank(key, x.left);
		else if (cmp > 0)
			return 1 + size(x.left) + rank(key, x.right);
		else
			return size(x.left);
	}

	/***********************************************************************
	 * Range count and range search.
	 ***********************************************************************/

	// all of the keys, as an Iterable
	public Iterable<Key> keys() {
		return keys(min(), max());
	}

	// the keys between lo and hi, as an Iterable
	public Iterable<Key> keys(Key lo, Key hi) {
		Queue<Key> queue = new Queue<Key>();
		// if (isEmpty() || lo.compareTo(hi) > 0) return queue;
		keys(root, queue, lo, hi);
		return queue;
	}

	// add the keys between lo and hi in the subtree rooted at x
	// to the queue
	private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
		if (x == null)
			return;
		int cmplo = lo.compareTo(x.key);
		int cmphi = hi.compareTo(x.key);
		if (cmplo < 0)
			keys(x.left, queue, lo, hi);
		if (cmplo <= 0 && cmphi >= 0)
			queue.enqueue(x.key);
		if (cmphi > 0)
			keys(x.right, queue, lo, hi);
	}

	/*************************************************************************
	 * Check integrity of red-black BST data structure
	 *************************************************************************/
	private boolean check() {
		if (!isBST())
			System.out.println("Not in symmetric order");
		if (!isSizeConsistent())
			System.out.println("Subtree counts not consistent");
		if (!isRankConsistent())
			System.out.println("Ranks not consistent");
		if (!is23())
			System.out.println("Not a 2-3 tree");
		if (!isBalanced())
			System.out.println("Not balanced");
		return isBST() && isSizeConsistent() && isRankConsistent() && is23()
				&& isBalanced();
	}

	// does this binary tree satisfy symmetric order?
	// Note: this test also ensures that data structure is a binary tree since
	// order is strict
	private boolean isBST() {
		return isBST(root, null, null);
	}

	// is the tree rooted at x a BST with all keys strictly between min and max
	// (if min or max is null, treat as empty constraint)
	// Credit: Bob Dondero's elegant solution
	private boolean isBST(Node x, Key min, Key max) {
		if (x == null)
			return true;
		if (min != null && x.key.compareTo(min) <= 0)
			return false;
		if (max != null && x.key.compareTo(max) >= 0)
			return false;
		return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
	}

	// are the size fields correct?
	private boolean isSizeConsistent() {
		return isSizeConsistent(root);
	}

	private boolean isSizeConsistent(Node x) {
		if (x == null)
			return true;
		if (x.N != size(x.left) + size(x.right) + 1)
			return false;
		return isSizeConsistent(x.left) && isSizeConsistent(x.right);
	}

	// check that ranks are consistent
	private boolean isRankConsistent() {
		for (int i = 0; i < size(); i++)
			if (i != rank(select(i)))
				return false;
		for (Key key : keys())
			if (key.compareTo(select(rank(key))) != 0)
				return false;
		return true;
	}

	// Does the tree have no red right links, and at most one (left)
	// red links in a row on any path?
	private boolean is23() {
		return is23(root);
	}

	private boolean is23(Node x) {
		if (x == null)
			return true;
		if (isRed(x.right))
			return false;
		if (x != root && isRed(x) && isRed(x.left))
			return false;
		return is23(x.left) && is23(x.right);
	}

	// do all paths from root to leaf have same number of black edges?
	private boolean isBalanced() {
		int black = 0; // number of black links on path from root to min
		Node x = root;
		while (x != null) {
			if (!isRed(x))
				black++;
			x = x.left;
		}
		return isBalanced(root, black);
	}

	// does every path from the root to a leaf have the given number of black
	// links?
	private boolean isBalanced(Node x, int black) {
		if (x == null)
			return black == 0;
		if (!isRed(x))
			black--;
		return isBalanced(x.left, black) && isBalanced(x.right, black);
	}

	/*****************************************************************************
	 * Test client
	 *****************************************************************************/
	public static void main(String[] args) {
		RBT<Integer, String> tree = new RBT<Integer, String>();

		tree.insert(15, "A");
		tree.insert(6, "E");
		tree.insert(3, "W");
		tree.insert(2, "Q");
		tree.insert(4, "I");
		tree.insert(7, "Z");
		tree.insert(13, "L");
		tree.insert(9, "U");
		tree.insert(18, "W");
		tree.insert(17, "N");
		tree.insert(20, "W");

		System.out.println("****** Inorder traversal ******");
		tree.printTree(tree.root);
		System.out.println("******************");

	}
}