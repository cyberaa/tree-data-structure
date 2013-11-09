package com.deep.skiplist;

public interface ISet<T> {

	public boolean add(T value);

    public boolean remove(T value);

    public boolean contains(T value);

    public int size();

    public boolean validate();

    public java.util.Set<T> toSet();

    public java.util.Collection<T> toCollection();

}
