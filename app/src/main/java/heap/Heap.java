// Date: May 18th 2025
package heap;

import java.util.Arrays;
import java.util.NoSuchElementException;

/** An instance is a min-heap of distinct values of type V with
 *  priorities of type P. Since it's a min-heap, the value
 *  with the smallest priority is at the root of the heap. */
public final class Heap<V, P extends Comparable<P>> {

    protected AList<Entry> c;
    protected HashTable<V, Integer> map;

    /** Constructor: an empty heap with capacity 10. */
    public Heap() {
        c = new AList<Entry>(10);
        map = new HashTable<V, Integer>();
    }

    /** An Entry contains a value and a priority. */
    class Entry {
        public V value;
        public P priority;

        /** An Entry with value v and priority p*/
        Entry(V v, P p) {
            value = v;
            priority = p;
        }

        public String toString() {
            return value.toString();
        }
    }


    public void add(V v, P p) throws IllegalArgumentException {

        Entry newEntry = new Entry(v, p);

        //if the value is already in the heap
        if(map.containsKey(v)){
            throw new IllegalArgumentException();
        }

        c.append(newEntry);
        //update size
        map.put(v, c.size - 1);

        int indexAt = c.size -1;
        //if not the root
        if(indexAt != 0) {
            //comparing child to parent
            if (c.get(indexAt).priority.compareTo(c.get((indexAt - 1) / 2).priority) < 0) {
                bubbleUp(indexAt);
            }
        }


    }

    /** Return the number of values in this heap.
     *  This operation takes constant time. */
    public int size() {
        return c.size();
    }

    /** Swap c[h] and c[k].
     *  precondition: h and k are >= 0 and < c.size() */
    protected void swap(int h, int k) {
        //set h to k and k to h
        Entry kEntry = c.get(k);
        Entry hEntry = c.get(h);

        c.put(k, hEntry);
        //updates map
        map.put(hEntry.value, k);

        c.put(h, kEntry);
        //updates map again
        map.put(kEntry.value, h);
    }

    /** Bubble c[k] up in heap to its right place.
     *  Precondition: Priority of every c[i] >= its parent's priority
     *                except perhaps for c[k] */
    protected void bubbleUp(int k) {

        while(c.get(k).priority.compareTo(c.get((k - 1) / 2).priority) < 0){
            //swap them
            swap((k - 1) / 2, k);
            k = (k - 1) / 2;
            if(k == 0){
                break;
            }
        }

    }

    /** Return the value of this heap with lowest priority. Do not
     *  change the heap. This operation takes constant time.
     *  @throws NoSuchElementException if the heap is empty. */
    public V peek() throws NoSuchElementException {
        if(c.size == 0){
            throw new NoSuchElementException();
        }
         return c.get(0).value;
    }

    /** Remove and return the element of this heap with lowest priority.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap.
     *  @throws NoSuchElementException if the heap is empty. */
    public V poll() throws NoSuchElementException {
        if(c.size == 0){
            throw new NoSuchElementException();
        }

        //gets first element from list
        V valueReturning = c.get(0).value;
        //updating the map to remove the item
        map.remove(valueReturning);

        if(c.size > 1) {

            //don't need to update map here because it updates below
            //it replaces the old index with the new index
            Entry lastValue = c.pop();

            // sets last element to first element
            c.put(0, lastValue);
            //updates map
            map.put(lastValue.value, 0);

            if(c.size > 1) {
                bubbleDown(0);
            }
        }
        else{
            c.resize(0);
        }
        return valueReturning;
    }

    protected void bubbleDown(int k) {
        int smallest = smallerChild(0);
        while(c.get(k).priority.compareTo(c.get(smallest).priority) > 0){
            swap(k, smallest);
            k = smallest;
            //updating smallest to continue the loop
            if(c.size > (2 * k) + 1) {
                smallest = smallerChild(k);
            }
            else{
                break;
            }
            //if k reaches the end, stop
            if(k == c.size - 1){
                break;
            }
        }
    }

    /** Prints c
     * Precondition: c is not null
     * Postcondition: prints the array
     */
    public void print(){
        System.out.println(Arrays.toString(c.a));
    }

    /** Return true if the value v is in the heap, false otherwise.
     *  The average case runtime is O(1).  */
    public boolean contains(V v) {
        if(map.get(v) != null){
            return true;
        }
        return false;

    }

    /** Change the priority of value v to p.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap.
     *  @throws IllegalArgumentException if v is not in the heap. */
    public void changePriority(V v, P p) throws IllegalArgumentException {
        //if the value isn't in the heap
        if(! map.containsKey(v)){
            throw new IllegalArgumentException();
        }
        //get entry
        int indexAt = map.get(v);
        Entry theEntry = new Entry(v, p);
        //change priority in c
        //map doesn't need to be updated here since key is the same
        c.put(indexAt, theEntry);

        //I can call them both with indexAt because of the nature of bubbleUp and bubbleDown
        //If bubbleUp moves things, then calling bubbleDown with indexAt shouldn't do anything
        //because the element should already be in the right place
        //And if bubbleUp does nothing and bubbleDown does something, it works fine

        if(c.size > 1) {
            bubbleUp(indexAt);
            bubbleDown(indexAt);
        }
    }
    /** Return the index of the child of k with smaller priority.
     * if only one child exists, return that child's index
     * Precondition: at least one child exists.*/
    private int smallerChild(int k) {
        //checks if there are two children
        if(c.size - 1 <= 2 * k + 2) {
            return 2 * k + 1;
        }
        else if(c.get((2 * k) + 1).priority.compareTo(c.get((2 * k) + 2).priority) < 0){
            return 2 * k + 1;
        }
        //if right child's priority is >= left child, return right
        else{
            return 2 * k + 2;
        }
    }

}
