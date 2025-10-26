
// Date: May 18th 2025

package heap;

/** A hash table modeled after java.util.Map. It uses chaining for collision
 * resolution and grows its underlying storage by a factor of 2 when the load
 * factor exceeds 0.8. */
public class HashTable<K,V> {

    protected Pair[] buckets; // array of list nodes that store K,V pairs
    protected int size; // how many items currently in the map


    /** class Pair stores a key-value pair and a next pointer for chaining
     * multiple values together in the same bucket, linked-list style*/
    public class Pair {
        protected K key;
        protected V value;
        protected Pair next;

        /** constructor: sets key and value */
        public Pair(K k, V v) {
            key = k;
            value = v;
            next = null;
        }

        /** constructor: sets key, value, and next */
        public Pair(K k, V v, Pair nxt) {
            key = k;
            value = v;
            next = nxt;
        }

        /** returns (k, v) String representation of the pair */
        public String toString() {
            return "(" + key + ", " + value + ")";
        }
    }

    /** constructor: initialize with default capacity 17 */
    public HashTable() {
        this(17);
    }

    /** constructor: initialize the given capacity */
    public HashTable(int capacity) {
        buckets = createBucketArray(capacity);
    }

    /** Return the size of the map (the number of key-value mappings in the
     * table) */
    public int getSize() {
        return size;
    }

    /** Return the current capacity of the table (the size of the buckets
     * array) */
    public int getCapacity() {
        return buckets.length;
    }

    /** Return the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * Runtime: average case O(1); worst case O(size) */
    public V get(K key) {

        int hashIndex = (Math.abs(key.hashCode()) % getCapacity());
        Pair cur;
        //if it's not the in the array, there's no value
        if(buckets[hashIndex] == null){
            return null;
        }
        cur = buckets[hashIndex];
        while(cur != null){
            //if current value
            if(cur.key == key){
                return cur.value;
            }
            cur = cur.next;
        }
        //value doesn't exist
        return null;

    }

    /** Associate the specified value with the specified key in this map. If
     * the map previously contained a mapping for the key, the old value is
     * replaced. Return the previous value associated with key, or null if
     * there was no mapping for key. If the load factor exceeds 0.8 after this
     * insertion, grow the array by a factor of two and rehash.
     * Precondition: val is not null.
     * Runtime: average case O(1); worst case O(size + a.length)*/
    public V put(K key, V val) {

        //check array w/ absolute value of hash % capacity of array
        //hashing it to find the index
        int hashIndex = (Math.abs(key.hashCode()) % getCapacity());
        Pair pairInserting = new Pair(key, val);
        V prevValue;
        Pair cur;

        //if the index is null, then the pair is not in the array and should be inserted
        if (buckets[hashIndex] == null) {
            buckets[hashIndex] = pairInserting;
            this.size = size + 1;
            growIfNeeded();
            return null;
        }
        else {
            cur = buckets[hashIndex];
            while (cur != null) {
                //if the value is in the hashMap, replace it and return previous value
                if (cur.key.equals(pairInserting.key)) {
                    prevValue = cur.value;
                    cur.value = val;
                    return prevValue;
                }
                //continue searching
                else {
                    //if the next node is null, insert the pair
                    if (cur.next == null) {
                        cur.next = pairInserting;
                        size = size + 1;
                        growIfNeeded();
                        return null;
                    }
                    cur = cur.next;
                }
            }
            //it shouldn't ever not return after the if/else block
            throw new IllegalStateException();
        }
    }

    //Return true if this map contains a mapping for the specified key.
    public boolean containsKey(K key) {
        int hashIndex = (Math.abs(key.hashCode()) % getCapacity());
        Pair cur;

        cur = buckets[hashIndex];
        while(cur != null){
            if(cur.key == key){
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    /**Remove the mapping for the specified key from this map if present.
     *  Return the previous value associated with key, or null if there was no
     *  mapping for key.
     */
    public V remove(K key) {

        int hashIndex = (Math.abs(key.hashCode()) % getCapacity());
        Pair cur = buckets[hashIndex];
        V prevValue;

        //if null, it's not in the map
        if(cur == null){
            return null;
        }
        //if it's the node we're removing
        else if(cur.key == key){
            prevValue = cur.value;
            buckets[hashIndex] = cur.next;
            size = size - 1;
            return prevValue;
        }

        //checks other nodes
        while(cur.next != null){
            //if it's the node
            if(cur.next.key == key){
                //get value
                prevValue = cur.next.value;
                //remove it from list
                cur.next = cur.next.next;
                size = size - 1;
                return prevValue;
            }
            cur = cur.next;
        }

        //key isn't in the hashMap
        return null;
    }

    /* check the load factor; if it exceeds 0.8, double the capacity 
     * and rehash values from the old array to the new array */
    private void growIfNeeded() {
        Pair itemOn;
        Pair cur;
        int hashIndex;

        //cast as a float or the division just doesn't work
        if(getSize()/((float)(getCapacity())) <= .8){
            return;
        }
        Pair[] newBucketArray = createBucketArray(getCapacity() * 2);
        //for each bucket
        for(int i = 0; i < getCapacity(); i++){
            //hash the thing
            itemOn = buckets[i];
            while(itemOn != null){

                //making a new object so that pointers work
                Pair newItem = new Pair (itemOn.key, itemOn.value);
                hashIndex = (Math.abs(itemOn.key.hashCode()) % newBucketArray.length);

                if(newBucketArray[hashIndex] == null){
                    newBucketArray[hashIndex] = newItem;
                }

                //if it's not null, iterate until it is and then put itemOn in the hashMap
                else {
                    cur = newBucketArray[hashIndex];
                    while (cur != null) {
                        //if the next node is null, insert the pair
                        if (cur.next == null) {
                            cur.next = newItem;
                            //I shouldn't need this line anymore, but it's staying
                            cur.next.next = null;
                            break;
                        }
                        cur = cur.next;
                    }
                }
                //update it
                itemOn = itemOn.next;
            }
        }
        //updating the map to the newBucketArray
        buckets = newBucketArray;
    }

    /* useful method for debugging - prints a representation of the current
     * state of the hash table by traversing each bucket and printing the
     * key-value pairs in linked-list representation */
    protected void dump() {
        System.out.println("Table size: " + getSize() + " capacity: " +
                getCapacity());
        for (int i = 0; i < buckets.length; i++) {
            System.out.print(i + ": --");
            Pair node = buckets[i];
            while (node != null) {
                System.out.print(">" + node + "--");
                node = node.next;

            }
            System.out.println("|");
        }
    }

    /*  Create and return a bucket array with the specified size, initializing
     *  each element of the bucket array to be an empty LinkedList of Pairs.
     *  The casting and warning suppression is necessary because generics and
     *  arrays don't play well together.*/
    @SuppressWarnings("unchecked")
    protected Pair[] createBucketArray(int size) {
        return (Pair[]) new HashTable<?,?>.Pair[size];
    }
}
