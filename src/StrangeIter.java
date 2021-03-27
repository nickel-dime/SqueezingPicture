import tester.Tester;

import java.util.HashMap;
import java.util.Iterator;

// creates a strange iterator
class StrangeSequenceIter implements Iterator<Integer> {

  int currIndex;
  HashMap<Integer, Integer> prevValsH;
  int lastValue = 0;

  public StrangeSequenceIter() {
    this.currIndex = 0;
    this.prevValsH = new HashMap<Integer, Integer>();
    this.lastValue = 2510;
  }

  public StrangeSequenceIter(int startingVal) {
    this.currIndex = 0;
    this.prevValsH = new HashMap<Integer, Integer>();
    this.lastValue = startingVal;
  }

  // checks if the next value exists
  public boolean hasNext() {
    return true;
  }

  // gets the next value in the strange iter
  public Integer next() {
    if (currIndex == 0) {
      currIndex += 1;
      return lastValue;
    }

    int newVal;
    // tries to find the new value in the hashmap, and if it doesn't returns 0
    if (this.prevValsH.containsKey(this.lastValue)) {
      newVal = this.currIndex - 1 - this.prevValsH.get(this.lastValue);
    }
    else {
      newVal = 0;
    }

    // adds the last value to the data structures
    // we add the last value now because if we added it earlier
    // then the hashmap would always return that value.
    // We want it to return the value before that
    this.prevValsH.put(this.lastValue, this.currIndex - 1);

    // update the current index and the last value
    this.currIndex += 1;
    this.lastValue = newVal;
    return newVal;
  }
}


class ExamplesStrangeIter {

  //  test out our iter
  void testStrangeIter(Tester t) {
    Iterator<Integer> strangeIter = new StrangeSequenceIter(20);
    t.checkExpect(strangeIter.next(), 20);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 1);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 2);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 2);
    t.checkExpect(strangeIter.next(), 2);
    t.checkExpect(strangeIter.next(), 1);
    t.checkExpect(strangeIter.next(), 6);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 5);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 2);
    t.checkExpect(strangeIter.next(), 6);
    t.checkExpect(strangeIter.next(), 5);
    t.checkExpect(strangeIter.next(), 4);
    t.checkExpect(strangeIter.next(), 0);
    t.checkExpect(strangeIter.next(), 5);
    t.checkExpect(strangeIter.next(), 3);

    Iterator<Integer> strangeIter300 = new StrangeSequenceIter(5);

    for (int i = 0; i < 300; i += 1) {
      strangeIter300.next();
    }

    t.checkExpect(strangeIter300.next(), 3);
    t.checkExpect(strangeIter300.next(), 16);
    t.checkExpect(strangeIter300.next(), 192);
    t.checkExpect(strangeIter300.next(), 0);
    t.checkExpect(strangeIter300.next(), 4);
    t.checkExpect(strangeIter300.next(), 12);
    t.checkExpect(strangeIter300.next(), 63);
    t.checkExpect(strangeIter300.next(), 0);
    t.checkExpect(strangeIter300.next(), 4);
    t.checkExpect(strangeIter300.next(), 4);
    t.checkExpect(strangeIter300.next(), 1);

    // This seems pointless, but tests for hasNext()
    t.checkExpect(strangeIter300.hasNext(), true);
    t.checkExpect(strangeIter300.hasNext(), true);
    t.checkExpect(strangeIter300.hasNext(), true);

    // tests with negative starting value
    Iterator<Integer> negIter = new StrangeSequenceIter(-5);

    t.checkExpect(negIter.next(), -5);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 1);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 2);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 2);
    t.checkExpect(negIter.next(), 2);
    t.checkExpect(negIter.next(), 1);
    t.checkExpect(negIter.next(), 6);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 5);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 2);
    t.checkExpect(negIter.next(), 6);
    t.checkExpect(negIter.next(), 5);
    t.checkExpect(negIter.next(), 4);
    t.checkExpect(negIter.next(), 0);
    t.checkExpect(negIter.next(), 5);

  }
}

// NEXT/HASNEXT
// In order to get the nth item of the sequence, we would need to access the hashmap using get,
// put a value in the hashmap, and use the contains method for hashmap. Every other operation in
// the method is trivial, and is just an assignment, arithmetic operation or statement. The
// worst case runtime of this method is O(n), since each hashmap operations has a worst case
// runtime of n. In reality, it is probably 3n + some constant, but for large values it basically
// is a worst-case runtime of n.

// However, in the average and best case runtimes for hashmap all the hash functions have a O(1)
// runtime. Since the rest of the function all run in constant time, the entire function runs in
// constant time. This is also the expected runtime, and is what the method will normally run at.
// The next method's memory usage in the best and worst cases is O(n). We have to keep track of a
// hashmap, which is O(n). Everything else is constant.

// FIRST THROUGH NTH ITEMS
// To compute the first through nth items in the sequence, (our MaxValue and DistinctValue
// methods), we multiply the next() method by n, since we have to go through each value up to n
// and keep track of the maximum value (or count the number of distinct values). Therfore, in
// worst case the big O is n^2, and in the average and best case the runtime is n.

// The memory usage for computing an item from the first through the nth items is O(n). The memory
// usage for next is O(n), and we don't have to keep track of any values (except one or two ints)
// in the MaxValue or DistinctValue methods. Therefore, the memory usage is O(n).