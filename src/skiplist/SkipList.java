package skiplist;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import apple.laf.JRSUIConstants.Size;

public class SkipList {

	private Node head;
	private Map<Long, Long> map = new ConcurrentHashMap<Long, Long>();
	private long size;

private class Node {

	public long key;

	public long value;

	public List<Node> nextOnLevel;

	public Node(long key,long value) {
		this.key = key;
		this.value = value;
		nextOnLevel = new ArrayList<Node>(1);
	}


	public int getLevels() {
		return nextOnLevel.size();
	}

	public void growToLevel(int level) {
		while (nextOnLevel.size() < level) {
			nextOnLevel.add(null);
		}
	}

	public Node getNextOnLevel(int level) {
		if (level < nextOnLevel.size()) {
			return nextOnLevel.get(level);
		} else {
			return null;
		}
	}

	public void setNextOnLevel(int level, Node next) {
		nextOnLevel.set(level, next);
	}

}


	public SkipList() {
		head = new Node(0,0);
		size = 0;
	}

	public void insert(long key, long value) {
		boolean inserted = true;
		int maxLevels = (int) Math.ceil(Math.log(size + 1)/ Math.log(2.0));
		int levels = randomLevels(maxLevels);
		head.growToLevel(levels);
		Node newNode = new Node(key,value);
		newNode.key = key;
		newNode.growToLevel(levels);
		Node node = head;
		for (int level = levels - 1; level >= 0; level--) {
			node = floor(node, level, key);
			if (node.key == key) {
				node.value = value;
				inserted = false;
			}
			newNode.setNextOnLevel(level, node.getNextOnLevel(level));
			node.setNextOnLevel(level, newNode);
			
			}
		if(inserted) {
			map.put(key, value);
				size++;
			}
		}

	public long find(long key) {
		int levels = head.getLevels();
		Node node = head;
		for (int level = levels - 1; level >= 0; level--) {
			node = floor(node, level, key);
			Node next = node.getNextOnLevel(level);
			if (next != null && next.key == key) {
				return next.value;
			}
		}
		return 0;
	}

	public long remove(long key) {
		boolean found = false;
		long value = 0;
		int levels = head.getLevels();
		Node node = head;
		for (int level = levels - 1; level >= 0; level--) {
			node = floor(node, level, key);
			Node next = node.getNextOnLevel(level);
			if (next != null && next.key == key) {
				found = true;
				value = next.value;
				node.setNextOnLevel(level, next.getNextOnLevel(level));
			}
		}
		if (found) {
			size--;
			map.remove(key);
			}
		return value;
	}

	public Node floor(Node start, int level, long key) {
		// Should ensure start.getKey() < key.
		Node node = start;
		Node next = node.getNextOnLevel(level);
		while (next != null && next.key < key) {
			node = next;
			next = node.getNextOnLevel(level);
		}
		return node;
	}

	public static int randomLevels(int maxLevels) {
		double rand = Math.random();
		int levels = 1;
		double probabilityToLevel = Math.pow(0.5, (double) levels);
		while (levels < maxLevels && probabilityToLevel < rand) {
			++levels;
			probabilityToLevel += Math.pow(0.5, (double) levels);
		}
		return levels;
	}

	public String printTree() {
		int lvl = head.getLevels();
		System.out.println(lvl);
		String result="";
		for(int i = 0 ; i<=lvl ; i++) {
		result += "\n SkipList content: [ ";
		Node node = head.getNextOnLevel(i);
		while (node != null) {
			result += node.key + ", ";
			node = node.getNextOnLevel(i);
		}
		result += "]";
		}
		return result;
	}

	public Node findmin() {
			Node node = head.getNextOnLevel(0);
		return node;
	}
	public Node findmax() {
		Node node = head.getNextOnLevel(0);
		while (node.getNextOnLevel(0) != null) {
			node = node.getNextOnLevel(0);
		}
	return node;
}
	public void testRandomLevels() {
		for (int i = 1; i < 5; ++i) {
		}
	}

	public long size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public long removeValues(long value) {
		long counter = 0;
		
		for (Map.Entry<Long, Long> entry : map.entrySet())
		{
			if(entry.getValue() == value ) {
				map.remove(entry.getKey());
				remove(entry.getKey());
				counter = counter +1;
			}
		}
		return counter;
	}

	public static void main(String[] args) {
		SkipList tree = new SkipList();
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
					long value  = tree.findmin().value;
					sum += value;
					
				} else if (splited[0].equalsIgnoreCase("FindMax")) {
					long value  = tree.findmax().value;
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
				}else {}
				System.out.println("Scanning Line : "+counter);
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
