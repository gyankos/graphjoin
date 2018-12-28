package it.giacomobergami.utils.datastructures.sets;

import it.giacomobergami.utils.datastructures.iterators.UnidirectionalSetIterator;
import it.giacomobergami.utils.datastructures.pointers.UnidirectionalSetPointer;

import java.util.*;

/**
 * Created by vasistas on 16/04/16.
 */
public class UnidirectionalStack<E> implements ISet<E> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidirectionalStack)) return false;
        UnidirectionalStack<?> that = (UnidirectionalStack<?>) o;
        if (size != that.size) return false;
        return head != null ? head.equals(that.head) : that.head == null;
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + (head != null ? head.hashCode() : 0);
        return result;
    }

    public int size; // policy: only read
    private UnidirectionalSetPointer<E> head;

    public UnidirectionalStack(Collection<E> value) {
        this();
        if (!value.isEmpty()) {
            for (E x : value) add(x);
        }
    }

    public UnidirectionalStack(E value) {
        this();
        add(value);
    }

    public UnidirectionalStack() {
        this.head = null;
        size = 0;
    }

    @Override
    public Optional<E> get(int position) {
        return head==null ? Optional.<E>empty() : head.get(position);
    }

    @Override
    public E first() {
        return head == null ? null : head.value;
    }

    @Override
    public boolean add(E value) {
        if (value==null) return false;
        UnidirectionalSetPointer<E> toadd = new UnidirectionalSetPointer<E>(value);
        if (head==null) head = toadd;
        else {
//            System.out.println(toadd.value+" "+head.value);
//            if (toadd.value.equals(head.value))
//                throw new RuntimeException("Ex");
            toadd.next = head;
            head.prev = toadd;
            head = toadd;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(E value) {
        if (value==null) return false;
        if (head==null) {
            return false;
        } else  {
            if (Objects.equals(head.value,value)) {
                size--;
                head = head.next;
                return true;
            } else return head.remove(value);
        }
    }

    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public Iterator<E> iterator() {
        return new UnidirectionalSetIterator<>(head);
    }

    @Override
    public boolean contains(E src) {
        return head==null ? false : head.contains(src);
    }

    @Override
    public int getSize() {
        return size;
    }


    public void sort() {
        if (getSize()<=0) return;
        Object[] toSort = new Object[getSize()];
        int i = 0;
        for (E obj : this) toSort[i++] = obj;
        Arrays.sort(toSort);
        UnidirectionalSetPointer<E> ptr = head;
        i = 0;
        do {
            ptr.value = (E)toSort[i++];
            ptr = ptr.next;
        } while (ptr!=null);
    }

}
