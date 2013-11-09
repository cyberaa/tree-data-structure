package splaytree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;



public class SplayTree {

	private Map<Long, Long> map = new ConcurrentHashMap<Long, Long>();

	
	/** Inner node class, used to populate tree. */
	private class Node {
		public long value;
		public long element;
		public Node left, right;

		public Node(long element,long value) {
			this.value = value;
			this.element = element;
			left = right = null;
		}

		public String toString() {
			return "<" + (element != 0 ? element : "") + "> ";
		}
	}

	/** The tree's root node. */
	private Node root;

	public SplayTree() {
		root = null;
	}

		private Node leftChildRotate(Node t) {
		Node newT = t.left;
		t.left = newT.right;
		newT.right = t;
		return newT;
	}

	private Node rightChildRotate(Node t) {
		Node newT = t.right;
		t.right = newT.left;
		newT.left = t;
		return newT;
	}

	private void splay(long element) {
		Node t = root;
		Node pops, gramps, gg;
		pops = gramps = gg = null;
		boolean checkGG = true;

		while (true) {
			if (t == null || element == t.element)
				break;
			else if (t.left != null && element < t.element) {
				// zig case
				if (element == t.left.element) {
					t = leftChildRotate(t);
				}
				// zig-zig case
				else if (t.left.left != null
						&& element == t.left.left.element) {
					gramps = t;
					pops = t.left;
					t = leftChildRotate(gramps);
					t = leftChildRotate(pops);
					checkGG = true;
				}
				// zig-zag case
				else if (t.left.right != null
						&& element == t.left.right.element) {
					gramps = t;
					pops = t.left;
					gramps.left = rightChildRotate(pops);
					t = leftChildRotate(gramps);
					checkGG = true;
				}
				// Walk down tree if target is neither child nor grand-child
				else if (element < t.element) {
					gg = t;
					t = t.left;
				}
			} else if (t.right != null && element > t.element) {
				// zig case
				if (element == t.right.element) {
					t = rightChildRotate(t);
				}
				// zig-zig case
				else if (t.right.right != null
						&& element == t.right.right.element) {
					gramps = t;
					pops = t.right;
					t = rightChildRotate(gramps);
					t = rightChildRotate(pops);
					checkGG = true;
				}
				// zig-zag case
				else if (t.right.left != null
						&& element == t.right.left.element) {
					gramps = t;
					pops = t.right;
					gramps.right = leftChildRotate(pops);
					t = rightChildRotate(gramps);
					checkGG = true;
				}
				// Walk down tree if target is neither child nor grand-child
				else if (element > t.element) {
					gg = t;
					t = t.right;
				}
			}
			// If target not found, mark current (i.e. last visited) as new
			// target
			else if ((t.left == null && element < t.element)
					|| (t.right == null && element > t.element)) {
				element = t.element;
				t = root;
				gg = null;
			}

			// Link t and its great-grandparent after a zig-zig or zig-zag
			// operation.
			// A new round of splaying is then begun by setting root to t.
			if (checkGG && gg != null) {
				
				if (t.element < gg.element)
					gg.left = t;
				else if (t.element > gg.element)
					gg.right = t;
				t = root;
				gg = null;
				checkGG = false;
			}
		}
		// The root is now that of the final tree
		root = t;
	}

	public boolean isEmpty() {
		return root == null;
	}

	public long size() {
		return isEmpty() ? 0 : size(root);
	}

	private long size(Node t) {
		int count = 0;
		if (t != null) {
			count++;
			count += size(t.left);
			count += size(t.right);
		}
		return count;
	}

	/**
	 * Adds a node with given element to the tree and then splays it to the
	 * root.
	 * 
	 * @param T
	 *            The new node's element
	 */
	public void insert(long item,long value) {
			root = insert(item,value, root);
			splay(item);
	}

	private Node insert(long key,Long value, Node t) {
		if (t == null) {
			map.put(key, value);
			return new Node(key,value);
		}
		else {
			
			if (key < t.element) {
				t.left = insert(key,value, t.left);
			map.put(key, value);
			}
			else if (key > t.element) {
				t.right = insert(key,value, t.right);
				map.put(key, value);
				}
			return t;
		}
		
	}

	public void remove(long key) {
		if (!isEmpty()) {
			splay(key);
			if (root != null && root.element == key) {
				if (root.left != null) {
					Node temp = root.right;
					root = root.left;
					splay(key);
					root.right = temp;
					map.remove(key);
				} else
					root = root.right;
				map.remove(key);
			}
		}
	}

	public long removeValues(long value) {
		long counter = 0;
		
		for (Map.Entry<Long, Long> entry : map.entrySet())
		{
			if(entry.getValue() == value ) {
				remove(entry.getKey());
				counter = counter +1;
			}
		}
		return counter;
	}
	
	public long findmin() {
		Node t = root;
		if (t != null) {
			while (t.left != null)
				t = t.left;
			splay(t.element);
			return root.value;
		}
		return 0;
	}

	public long findmax() {
		Node t = root;
		if (t != null) {
			while (t.right != null)
				t = t.right;
			splay(t.element);
			return root.value;
		}
		return 0;
	}

	
	public long find(long item) {
		if (isEmpty())
			return 0;
		splay(item);
		if (root.element == item) {
			return root.value;
		}
		return 0;
	}

		public Long getRootValue() {
		return (root == null) ? null : root.element;
	}

		public Long getLeftmostChild() {
		Node current = root;
		if (current != null) {
			while (current.left != null)
				current = current.left;
			return current.element;
		}
		return null;
	}

		public Long getRightmostChild() {
		Node current = root;
		if (current != null) {
			while (current.right != null)
				current = current.right;
			return current.element;
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		SplayTree tree = new SplayTree();
		long sum=0;
        System.out.println("Enter File Name : ");
		Scanner scanner = new Scanner(System.in);
		FileReader fileReader;
		try {
			fileReader = new FileReader(scanner.nextLine());
			BufferedReader br = new BufferedReader(fileReader);
			String line;
			int counter = 1;
			double startTime = System.currentTimeMillis();
			while ((line = br.readLine()) != null) {
				String[] splited = line.split("\\s+");
				
				if (splited[0].equalsIgnoreCase("Insert")) {
					tree.insert(Long.parseLong(splited[1]), Long.parseLong(splited[2]));
				}
				else if (splited[0].equalsIgnoreCase("Find")) {
					if (!tree.isEmpty()) {
						tree.find(Long.parseLong(splited[1]));
					}
				else {
					System.out.println("SkipList Empty");
				}
				} else if (splited[0].equalsIgnoreCase("FindMin")) {
					long value  = tree.findmin();
					sum += value;
				} else if (splited[0].equalsIgnoreCase("FindMax")) {
					long value  = tree.findmax();
					sum += value;
				} else if (splited[0].equalsIgnoreCase("Remove") ) {
					tree.remove(Long.parseLong(splited[1]));
				}  else if (splited[0].equalsIgnoreCase("RemoveValue")) {
						long value  = tree.removeValues(Long.parseLong(splited[1]));
						sum += value;
				
				}
				 else if (splited[0].equalsIgnoreCase("Size")) {
						long value  = tree.size();
						sum += value;
				}else {
				}
				System.out.println("Scanning Line "+counter);
				counter +=1;
			}
			double endTime = System.currentTimeMillis();
			double totalTime = (endTime - startTime) / 1000;
			System.out.println("Answer = "+sum);
			
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
	
	}


}
