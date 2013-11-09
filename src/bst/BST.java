package bst;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author deepshah
 * 
 */

public class BST<Key extends Comparable<Key>, Data> { // Comparable<T> means T is object that we can compare to
//example. {a,b} is such that a.compareTo(b) < / > / = 0
	private Node root; // root of BST

	private class Node {
		private Key key; // sorted by key
		private Data data; // associated data
		private Node left, right, prev; // left and right subtrees and previous
										// nodes
		private int N = 0; // total number of nodes in a tree

		public Node(Key key, Data data) {
			if(key == null || data==null) {
				throw new NullPointerException("Either Key or Data is Null for Key : "+key+" or Data :"+data);
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
	
	// Here first we insert root then as we go left or right we insert returned <key,value> of "iterateTreetoHashMap"
	// into map and keep on adding  iterating and adding.
	
	private Map<Comparable<Key>, Data> iterateTreetoHashMap(Node node) {
		Map<Comparable<Key>, Data> map = new HashMap<Comparable<Key>, Data>();
		if (node != null) {
			map.putAll(iterateTreetoHashMap(node.left)); // every time it iterates left will return a map and we put all data in  same map
				map.put(node.key, node.data); // First time it will insert root in map
			map.putAll(iterateTreetoHashMap(node.right));  // every time it iterates right will return a map and we put all data in same map
		}
		return map;
	}
	/*************************************************************************
	 * Successor of Tree
	 *************************************************************************/

	private Node successorNode(Node node) {
		if (node.right != null) // Sucessor of a x is min node on right side of
								// x
			return min(node.right);
		Node preNode = node.prev;
		while (preNode != null && node == preNode.right) { // If x does not have
															// right child then
			node = preNode; // successor is first right ancestor of x
			preNode = preNode.prev;
		}
		return preNode;
	}

	/*************************************************************************
	 * Predecessor of Tree
	 *************************************************************************/

	private Node predecessorNode(Node node) {
		if (node.left != null) // predecessor of a x is max node on left side of
								// x
			return max(node.left);
		Node preNode = node.prev;
		while (preNode != null && node == preNode.left) { // If x does not have
															// left child then
			node = preNode; // predecessor is first left ancestor of x
			preNode = preNode.prev;
		}
		return preNode;
	}

	/*************************************************************************
	 * Inserting Node
	 *************************************************************************/

	/* Insert a Node */
	public void insert(Key key, Data data) {
		Node z = new Node(key, data); // Node to be inserted
		Node y = null; //
		Node x = root; // let x be the pointer to be compared and moved starting
						// from root node
		while (x != null) { // parse till the end of tree
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

		if (y == null) { // if no root make inserting node as root node
			root = z;
		} else if (z.key.compareTo(y.key) < 0) { // compare key of inserting
													// node to root node
			y.left = z;
		} else if (z.key.compareTo(y.key) > 0) {
			y.right = z;
		}
		 else if(z.key.compareTo(y.key) == 0) { // key already exists so Replaceable value
				y.data = z.data;
			}
		root.N = root.N + 1;
		assert validate();
	}

	/*************************************************************************
	 * Transplant
	 *************************************************************************/

	private void transplant(Node u, Node v) {
		if (u.prev == null)
			root = v; // This means node u was the root and hence v will be next
						// root
		else if (u == u.prev.left)
			u.prev.left = v;
		else
			u.prev.right = v;
		if (v != null)
			v.prev = u.prev;
	}

	/*************************************************************************
	 * Delete a node
	 *************************************************************************/
	private String delete(Comparable<Key> key) {
		String data;
		try {
			Node z = find(root,key);
		 data = (String) delete(z).data;
		}
		catch(NullPointerException ex){
			return "0";
		}
		return data;
	}
	
	private Node delete(Node z) { // z is node to be deleted
		Node y;
		if (z.left == null)
			transplant(z, z.right);
		else if (z.right == null)
			transplant(z, z.left);
		else {
			y = min(z.right);
			if (y.prev != z) {
				transplant(y, y.right);
				y.right = z.right;
				y.right.prev = y;
			}
			transplant(z, y);
			y.left = z.left;
			y.left.prev = y;
		}
		return z;

	}

	public void deleteMin() {
		if (isEmpty())
			throw new NoSuchElementException("Symbol table underflow");
		root = deleteMin(root);
		assert validate();
	}

	private Node deleteMin(Node x) {
		if (x.left == null)
			return x.right;
		x.left = deleteMin(x.left);
		x.N = size(x.left) + size(x.right) + 1;
		return x;
	}

	public void deleteMax() {
		if (isEmpty())
			throw new NoSuchElementException("Symbol table underflow");
		root = deleteMax(root);
		assert validate();
	}

	private Node deleteMax(Node x) {
		if (x.right == null)
			return x.left;
		x.right = deleteMax(x.right);
		x.N = size(x.left) + size(x.right) + 1;
		return x;
	}
	
	
	private int deleteNodes(Data data) {
		int counter = 0;
		Map<Comparable<Key>, Data> map = iterateTreetoHashMap(root);
		for (Map.Entry<Comparable<Key>, Data> entry : map.entrySet())
		{
			if(entry.getValue().equals(data) ) {
				delete(entry.getKey());
				counter = counter +1;
				System.out.println("Deleted : "+entry.getKey() + "/" + entry.getValue());
			}
		}
		return counter;
	}
	/*************************************************************************
	 * Finding a node
	 *************************************************************************/
	private String find(Key key) {
		String data;
		try {
		data = (String) find(root, key).data;
		}
		catch(NullPointerException e) {
			return  "0";
		}
		return data;
	}
	
	private Node find(Node node, Comparable<Key> key) { //
		if (node == null || key == node.key)
			return node;
		int cmp = key.compareTo(node.key);
		if (cmp < 0)
			return find(node.left, key);
		else if(cmp >0)
			return find(node.right, key);
		else 
			return null;
	}

	/*************************************************************************
	 * Minimum Node
	 *************************************************************************/

	private Node min() {
		return min(root);
	}

	private Node min(Node node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	/*************************************************************************
	 * Maximum Node
	 *************************************************************************/
	private Node max() {
		return max(root);
	}

	private Node max(Node node) {
		while (node.right != null) {
			node = node.right;
		}
		return node;
	}

	/*************************************************************************
	 * Size of Tree (H) & Number of Nodes in the Tree (N)
	 *************************************************************************/

	// is the symbol table empty?
	public boolean isEmpty() {
		return size() == 0;
	}

	// return number of key-value pairs in BST
	public int size() {
		return size(root);
	}

	// return number of key-value pairs in BST rooted at x
	private int size(Node node) {
		if (node == null)
			return 0;
		else
			return node.N;
	}

	/*************************************************************************
	 * Validate Tree
	 *************************************************************************/

	private boolean validate() {
		if (!isBST())
			System.out.println("Not in symmetric order");
		if (!isSizeConsistent())
			System.out.println("Subtree counts not consistent");
		// if (!isRankConsistent()) System.out.println("Ranks not consistent");
		return isBST() && isSizeConsistent();// && isRankConsistent();
	}

	/* Check Symmetric or not */

	private boolean isBST() {
		return isBST(root, null, null);
	}

	private boolean isBST(Node node, Key minKey, Key maxKey) {
		if (node == null)
			return true;
		if (minKey != null && node.key.compareTo(minKey) <= 0)
			return false;
		if (maxKey != null && node.key.compareTo(maxKey) >= 0)
			return false;
		return isBST(node.left, minKey, node.key)
				&& isBST(node.right, node.key, maxKey);
	}

	/* Check Size of Node */

	private boolean isSizeConsistent() {
		return isSizeConsistent(root);
	}

	private boolean isSizeConsistent(Node node) {
		if (node == null)
			return true;
		if (node.N != size(node.left) + size(node.right) + 1)
			return false;
		return isSizeConsistent(node.left) && isSizeConsistent(node.right);
	}

	/*
	 * private boolean isRankConsistent() { for (int i = 0; i < size(); i++) if
	 * (i != rank(select(i))) return false; for (Key key : keys()) if
	 * (key.compareTo(select(rank(key))) != 0) return false; return true; }
	 */

	public static void main(String[] args) {
		BST<String, String> tree = new BST<String, String>();

		System.out.println("Enter File Name : ");
		Scanner scanner = new Scanner(System.in);
		FileReader fileReader;
		try {
			fileReader = new FileReader(scanner.nextLine());
			BufferedReader br = new BufferedReader(fileReader);
			long counter = 0;
			String line;
			double startTime = System.currentTimeMillis();
			while ((line = br.readLine()) != null) {
				String[] splited = line.split("\\s+");
				
				if (splited[0].equalsIgnoreCase("Insert")) {
					//System.out.println("("+counter+") Inserted  : "+splited[1]);
					tree.insert(splited[1], splited[2]);
					
				}
				else if (splited[0].equalsIgnoreCase("Find")) {
					if (!tree.isEmpty()) {
						System.out.println("("+counter+") Find  : "+tree.find(splited[1]));
					
				}
				else {
					System.out.println("SkipList Empty");
				}
				} else if (splited[0].equalsIgnoreCase("FindMin")) {
					System.out.println("("+counter+") Min  : "+tree.min().data);
					
				} else if (splited[0].equalsIgnoreCase("FindMax")) {
					System.out.println("("+counter+") Max  : "+tree.max().data);
					
				} else if (splited[0].equalsIgnoreCase("Remove") || splited[0].equalsIgnoreCase("RemoveValue")) {
					System.out.println("("+counter+") Removed  : "+tree.delete(splited[1]));
					
				} else {

				}
				counter++;
			}
			double endTime = System.currentTimeMillis();
			double totalTime = (endTime - startTime) / 1000;
			System.out.println("Starting Time : " + startTime + " MilliSeconds");
			System.out.println("Ending Time : " + endTime + " MilliSeconds");
			System.out.println("Total Time : " + totalTime + " Seconds");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Scanner scanner = new Scanner(System.in);
//
//		tree.insert(15, "A");
//		tree.insert(6, "E");
//		tree.insert(3, "W");
//		tree.insert(2, "Q");
//		tree.insert(4, "I");
//		tree.insert(7, "Z");
//		tree.insert(13, "L");
//		tree.insert(9, "U");
//		tree.insert(18, "W");
//		tree.insert(17, "N");
//		tree.insert(20, "W");
//
//		System.out.println("****** Inorder traversal ******");
//		tree.printTree(tree.root);
//		System.out.println("******************");
//		System.out.println("Find 9(Exist's in Table) : " + tree.find(9));
//		System.out.println("Find Min : " + tree.min().data);
//		System.out.println("Find Max : " + tree.max().data);
//		System.out
//				.println("Sucessor(X) : " + tree.successorNode(tree.root).key);
//		System.out.println("Predecessor(X) :"
//				+ tree.predecessorNode(tree.root).key);
//
//		// Deleting node = 6
//		System.out.println("Deleting 1(Does Not exists) :"+tree.delete(1));
//		System.out.println("****** After deleting 6 traversal ******");
//		tree.printTree(tree.root);
//		System.out.println("******");
//		System.out.println("Size :"+tree.size());
//
//		// Deleting node = 6
//		System.out.println("Deleting all values W :"+tree.deleteNodes("W"));
//		System.out.println("****** After deleting W traversal ******");
//		tree.printTree(tree.root);
//		System.out.println("******");

	}

}
