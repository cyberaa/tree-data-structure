package avltree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class AvlTree
{
	  Map<Long, Long> map = new ConcurrentHashMap<Long, Long>();
    
    public AvlTree( )
    {
        root = null;
    }
    
    public long size() {
        return size(root);
    }
    
    private long size(AvlNode x) {
        if (x == null) return 0;
        else return (1 + size(x.left) + size(x.right));
    }

    public void insert( long key ,long value )
    {
    	if(value != find(key))
    		map.put(key, value);
        root = insert( key,value, root );
    }

    /**
     * Remove from the tree. Nothing is done if x is not found.
     * @param x the item to remove.
     */
    public long remove( long x )
    {
        root = remove( x, root );
        if(root == null) {
        	return 0;
        }
        else {
        map.remove(root.key);
        return root.value;
        }
    }

    
    private AvlNode remove( long key, AvlNode t )
    {
        if( t == null )
        	return null;   // Item not found; do nothing
            
        
        if( key < t.key ) {
            t.left = remove( key, t.left );
        }
        else if( key > t.key ) {
            t.right = remove( key, t.right );
        }
        else if( t.left != null && t.right != null ) // Two children
        {
            t.key = findMin( t.right ).key;
            t.right = remove( t.key, t.right );
        }
        else
            t = ( t.left != null ) ? t.left : t.right;
        
        return balance( t );
    }
    private long removeValues(long value) {
		long counter = 0;
		
		for (Entry<Long, Long> entry : map.entrySet())
		{
			long mapvalue = entry.getKey();
			if(mapvalue == value ) {
				remove(entry.getKey());
				counter = counter +1;
			}
		}
		return counter;
	}
    public AvlNode findmin( )
    {
        if( isEmpty( ) )
            throw new RuntimeException("is Empty" );
        return findMin( root );
    }

    public AvlNode findmax( )
    {
        if( isEmpty( ) )
        	throw new RuntimeException("is Empty" );
        return findMax( root );
    }

    public long find( long key )
    {
        AvlNode node =  contains( key, root );
        if(node == null) {
        	return 0;
        }
        return node.value;
        
    }


    public boolean isEmpty( )
    {
        return root == null;
    }

    public void printTree( )
    {
        if( isEmpty( ) )
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }

    private static final int ALLOWED_IMBALANCE = 1;
    
    private AvlNode balance( AvlNode t )
    {
        if( t == null )
            return t;
        
        if( height( t.left ) - height( t.right ) > ALLOWED_IMBALANCE )
            if( height( t.left.left ) >= height( t.left.right ) )
                t = rotateWithLeftChild( t );
            else
                t = doubleWithLeftChild( t );
        else
        if( height( t.right ) - height( t.left ) > ALLOWED_IMBALANCE )
            if( height( t.right.right ) >= height( t.right.left ) )
                t = rotateWithRightChild( t );
            else
                t = doubleWithRightChild( t );

        t.height = Math.max( height( t.left ), height( t.right ) ) + 1;
        return t;
    }
    
    public void checkBalance( )
    {
        checkBalance( root );
    }
    
    private int checkBalance( AvlNode t )
    {
        if( t == null )
            return -1;
        
        if( t != null )
        {
            int hl = checkBalance( t.left );
            int hr = checkBalance( t.right );
            if( Math.abs( height( t.left ) - height( t.right ) ) > 1 ||
                    height( t.left ) != hl || height( t.right ) != hr )
                System.out.println( "OOPS!!" );
        }
        
        return height( t );
    }
    
    
    private AvlNode insert( long key,Long value, AvlNode t )
    {
    	
        if( t == null ) {
        	map.put(key, value);
        return new AvlNode( key, value,null, null );
        }
        
        if( key < t.key ) {
            t.left = insert( key,value, t.left );
            map.put(key, value);
	    }
        else if( key > t.key ) {
            t.right = insert( key, value,t.right );
            map.put(key, value);
        }
        else  if( key == t.key ){
        	t.value = value;
        }
            
        return balance( t );
    }

    private AvlNode findMin( AvlNode t )
    {
        if( t == null )
            return t;

        while( t.left != null )
            t = t.left;
        return t;
    }

    private AvlNode findMax( AvlNode t )
    {
        if( t == null )
            return t;

        while( t.right != null )
            t = t.right;
        return t;
    }

    private AvlNode contains( long x, AvlNode t )
    {
        while( t != null )
        {
            
            if( x < t.key )
                t = t.left;
            else if( x > t.key )
                t = t.right;
            else
                return t;    // Match
        }

        return null;   // No match
    }
    private void printTree( AvlNode t )
    {
        if( t != null )
        {
            printTree( t.left );
            System.out.println( t.key );
            printTree( t.right );
        }
    }

    /**
     * Return the height of node t, or -1, if null.
     */
    private int height( AvlNode t )
    {
        return t == null ? -1 : t.height;
    }

    /**
     * Rotate binary tree node with left child.
     * For AVL trees, this is a single rotation for case 1.
     * Update heights, then return new root.
     */
    private AvlNode rotateWithLeftChild( AvlNode k2 )
    {
        AvlNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max( height( k2.left ), height( k2.right ) ) + 1;
        k1.height = Math.max( height( k1.left ), k2.height ) + 1;
        return k1;
    }

    /**
     * Rotate binary tree node with right child.
     * For AVL trees, this is a single rotation for case 4.
     * Update heights, then return new root.
     */
    private AvlNode rotateWithRightChild( AvlNode k1 )
    {
        AvlNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max( height( k1.left ), height( k1.right ) ) + 1;
        k2.height = Math.max( height( k2.right ), k1.height ) + 1;
        return k2;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * For AVL trees, this is a double rotation for case 2.
     * Update heights, then return new root.
     */
    private AvlNode doubleWithLeftChild( AvlNode k3 )
    {
        k3.left = rotateWithRightChild( k3.left );
        return rotateWithLeftChild( k3 );
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * For AVL trees, this is a double rotation for case 3.
     * Update heights, then return new root.
     */
    private AvlNode doubleWithRightChild( AvlNode k1 )
    {
        k1.right = rotateWithLeftChild( k1.right );
        return rotateWithRightChild( k1 );
    }

    private static class AvlNode
    {
          long   key;      // The data in the node
          long value;
          AvlNode  left;         // Left child
          AvlNode  right;        // Right child
          int               height;       // Height
        AvlNode( long key,long value )
        {
            this( key,value, null, null );
        }

        AvlNode( long key,long value, AvlNode lt, AvlNode rt )
        {
            this.key  = key;
            this.value = value;
            left     = lt;
            right    = rt;
            height   = 0;
        }

      
    }

      /** The tree root. */
    private AvlNode root;


        // Test program
    public static void main( String [ ] args )
    {
    	 long sum = 0;
        AvlTree tree = new AvlTree( );
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

 /*
        tree.insert(12632165, 152512323);
        tree.insert(52324987, 112123323);
        tree.insert(519898, 1212123323);
        tree.insert(198912212, 12111423);
        tree.insert(51231213, 1211233);
        tree.insert(14521259, 122123343);
        tree.insert(786779, 15241523);
        tree.insert(1990829, 112323);
        tree.insert(1364619, 1212323); 
        tree.insert(22198733, 12233);
        tree.insert(213149, 22198733);
        tree.insert(15123319, 12233);
        tree.insert(15121239, 22198733);
        tree.insert(213123149, 22198733);
        tree.insert(151212339, 12233);
        tree.insert(1512139, 22198733);
        
        System.out.println("The size 0 is: " + tree.size());
        System.out.println(tree.remove(151239));
        System.out.println("The size 0 is: " + tree.size());
        System.out.println(tree.find(14521259));
        System.out.println("The size 4 is: " + tree.size());
        System.out.println(tree.find(1364619));
        System.out.println("The size 5 is: " + tree.size());
        System.out.println(tree.find(213149));
        System.out.println("The size 6 is: " + tree.size());
        
        System.out.println("******");
        tree.printTree(tree.root);
        System.out.println("****");
        
        System.out.println("******");
        System.out.println(tree.findmax().key);
        System.out.println("****");
        
        System.out.println("******");
        System.out.println(tree.findmin().key);
        System.out.println("****");
        


        

        System.out.println("******");
        tree.printTree(tree.root);
        System.out.println("****");
       
        System.out.println("******");
        tree.removeValues(22198733);
        System.out.println("****");
        
        System.out.println("******");
        tree.printTree(tree.root);
        System.out.println("****");
        
        System.out.println("The size 0 is: " + tree.size());
        
        System.out.println("Empty: " + tree.isEmpty());
        */
    }
}