package skiplist;

public interface IMap<K,V> {

    public V put(K key, V value);

    public V get(K key);

    public V remove(K key);

    public boolean contains(K key);

    public int size();

    public boolean validate();

    public java.util.Map<K,V> toMap();

}
