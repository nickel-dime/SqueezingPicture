//import java.util.function.Predicate;
//import tester.Tester;
//
//// a deque
//class Deque<T> {
//
//  Sentinel<T> header;
//
//  // constructs a deque
//  public Deque() {
//    this.header = new Sentinel<T>();
//  }
//
//  // constructs a deque with a given sentinal header
//  public Deque(Sentinel<T> header) {
//    this.header = header;
//  }
//
//  // counts the number of nodes in a list deque, not including header
//  int size() {
//    return this.header.size();
//  }
//
//  // EFFECT: inserts an element of type T at the head of the list
//  void addAtHead(T element) {
//    this.header.addAtHead(element);
//  }
//
//  // EFFECT: inserts an element of type T at the tail of the list
//  void addAtTail(T element) {
//    this.header.addAtTail(element);
//  }
//
//  // gets the first element's data in the list
//  // EFFECT: removes the first element of the list
//  T removeFromHead() {
//    return this.header.next.removeThisNode();
//  }
//
//  // gets the last element's data in the list
//  // EFFECT: removes the last element of the list
//  T removeFromTail() {
//    return this.header.prev.removeThisNode();
//  }
//
//  // produces the first node in this Deque for which the given predicate returns
//  // true
//  ANode<T> find(Predicate<T> pred) {
//    return this.header.find(pred);
//  }
//
//  // changes previous and next node references
//  // EFFECT: removes the given node from this Deque
//  void removeNode(ANode<T> arg) {
//    if (arg != this.header) {
//      arg.removeThisNode();
//    }
//  }
//}
//
//// a node in a list
//abstract class ANode<T> {
//  ANode<T> prev;
//  ANode<T> next;
//
//  // counts this node
//  int size() {
//    return this.next.sizeHelper();
//  }
//
//  // counts the individual node, and stops the infinite cycle
//  abstract int sizeHelper();
//
//  // sees if a given predicate returns true in this node
//  abstract ANode<T> findHelper(Predicate<T> pred);
//
//  // gets the removed element's data in the list
//  // EFFECT: modifies the previous and next references to exclude this node
//  abstract T removeThisNode();
//
//}
//
//// an always present node
//class Sentinel<T> extends ANode<T> {
//
//  public Sentinel() {
//    this.prev = this;
//    this.next = this;
//  }
//
//  // counts this node
//  int sizeHelper() {
//    return 0;
//  }
//
//  // EFFECT: changes sentinel's previous and/or next to accommodate new node
//  // also modifies next node's references to accommodate new node
//  void addAtHead(T element) {
//    new Node<T>(element, this.next, this);
//  }
//
//  // EFFECT: changes sentinel's previous and/or next to accommodate new node
//  // also modifies previous node's references to accommodate new node
//  void addAtTail(T element) {
//    new Node<T>(element, this, this.prev);
//  }
//
//  // returns the found node matching the predicate, if any
//  ANode<T> find(Predicate<T> pred) {
//    return this.next.findHelper(pred);
//  }
//
//  // returns the sentinel since the element could not be found
//  ANode<T> findHelper(Predicate<T> pred) {
//    return this;
//  }
//
//  // tries to remove a node from an empty list - which throws an error
//  T removeThisNode() {
//    throw new RuntimeException("Trying to delete an element from an empty list");
//  }
//
//}
//
//// a node in a list
//class Node<T> extends ANode<T> {
//
//  T data;
//
//  // constructs a node with only data
//  public Node(T data) {
//    this.data = data;
//    this.prev = null;
//    this.next = null;
//  }
//
//  // constructs a node with a previous and next
//  public Node(T data, ANode<T> next, ANode<T> prev) {
//    this.data = data;
//
//    if (prev == null) {
//      throw new IllegalArgumentException("previous node cannot be null");
//    }
//    else if (next == null) {
//      throw new IllegalArgumentException("next node cannot be null");
//    }
//
//    this.prev = prev;
//    this.next = next;
//
//    // update the given nodes to refer back to this node
//    prev.next = this;
//    next.prev = this;
//  }
//
//  // counts this node
//  int sizeHelper() {
//    return 1 + this.next.sizeHelper();
//  }
//
//  // returns this node if it matches the predicate
//  ANode<T> findHelper(Predicate<T> pred) {
//    if (pred.test(this.data)) {
//      return this;
//    }
//    else {
//      return this.next.findHelper(pred);
//    }
//  }
//
//  // uses the sentinel to find a given node matching a predicate and removes that
//  // node from the list
//  T removeThisNode() {
//    this.prev.next = this.next;
//    this.next.prev = this.prev;
//    return this.data;
//  }
//
//}
//
//// predicate to see if two values are the same
//class SameNode<T> implements Predicate<T> {
//
//  T comparedData;
//
//  public SameNode(T comparedData) {
//    this.comparedData = comparedData;
//  }
//
//  public boolean test(T t) {
//    return comparedData.equals(t);
//  }
//
//}
//
//// PREDICATE CLASSES
//
//// returns true if there are more than 4 letters in a word
//class BigWords implements Predicate<String> {
//
//  // is the word more than 4 letters long
//  public boolean test(String t) {
//    return t.length() > 3;
//  }
//
//}
//
//// returns if a string is the same word as another string
//class SameWord implements Predicate<String> {
//  String target;
//
//  SameWord(String target) {
//    this.target = target;
//  }
//
//  // is the string the same as a given string
//  public boolean test(String t) {
//    return t.equals(target);
//  }
//
//}
//
//// returns true if the number is greater than 39
//class BigNumbers implements Predicate<Integer> {
//
//  // is the number greater than 39
//  public boolean test(Integer t) {
//    return t > 39;
//  }
//
//}
//
////returns if a string is the same word as another string
//class SameNumber implements Predicate<Integer> {
//  int target;
//
//  SameNumber(int target) {
//    this.target = target;
//  }
//
//  // is the string the same as a given string
//  public boolean test(Integer t) {
//    return t == target;
//  }
//
//}
//
//class ExamplesDeque {
//  // empty deque
//  Deque<String> deque1;
//  Deque<Integer> deque1Int;
//
//  // lexographically ordered deque
//  Sentinel<String> sentinel2;
//  Node<String> abc;
//  Node<String> bcd;
//  Node<String> cde;
//  Node<String> def;
//  Deque<String> deque2;
//
//  // random deque
//  Sentinel<String> sentinel3;
//  Node<String> aoun;
//  Node<String> fundies;
//  Node<String> lerner;
//  Node<String> white;
//  Deque<String> deque3;
//
//  // small ints
//  Sentinel<Integer> sentinel2Int;
//  Node<Integer> two;
//  Node<Integer> four;
//  Node<Integer> six;
//  Node<Integer> ten;
//  Deque<Integer> deque2Int;
//
//  // mixed ints
//  Sentinel<Integer> sentinel3Int;
//  Node<Integer> eleven;
//  Node<Integer> fifty;
//  Node<Integer> twelve;
//  Node<Integer> twohundred;
//  Deque<Integer> deque3Int;
//
//  // big ints
//  Sentinel<Integer> sentinel4Int;
//  Node<Integer> million;
//  Node<Integer> sixty;
//  Deque<Integer> deque4Int;
//
//  void initTestConditions() {
//    // String deques
//    this.deque1 = new Deque<>();
//
//    this.sentinel2 = new Sentinel<String>();
//    this.abc = new Node<String>("abc", this.sentinel2, this.sentinel2);
//    this.bcd = new Node<String>("bcd", this.sentinel2, this.abc);
//    this.cde = new Node<String>("cde", this.sentinel2, this.bcd);
//    this.def = new Node<String>("def", this.sentinel2, this.cde);
//    this.deque2 = new Deque<>(this.sentinel2);
//
//    this.sentinel3 = new Sentinel<String>();
//    this.aoun = new Node<String>("president", this.sentinel3, this.sentinel3);
//    this.fundies = new Node<String>("hard", this.sentinel3, this.aoun);
//    this.lerner = new Node<String>("professor", this.sentinel3, this.fundies);
//    this.white = new Node<String>("building", this.sentinel3, this.lerner);
//    this.deque3 = new Deque<>(this.sentinel3);
//
//    // Integer deques
//    this.deque1Int = new Deque<>();
//
//    this.sentinel2Int = new Sentinel<Integer>();
//    this.two = new Node<Integer>(2, this.sentinel2Int, this.sentinel2Int);
//    this.four = new Node<Integer>(4, this.sentinel2Int, this.two);
//    this.six = new Node<Integer>(6, this.sentinel2Int, this.four);
//    this.ten = new Node<Integer>(10, this.sentinel2Int, this.six);
//    this.deque2Int = new Deque<>(this.sentinel2Int);
//
//    this.sentinel3Int = new Sentinel<Integer>();
//    this.eleven = new Node<Integer>(11, this.sentinel3Int, this.sentinel3Int);
//    this.fifty = new Node<Integer>(50, this.sentinel3Int, this.eleven);
//    this.twelve = new Node<Integer>(12, this.sentinel3Int, this.fifty);
//    this.twohundred = new Node<Integer>(200, this.sentinel3Int, this.twelve);
//    this.deque3Int = new Deque<>(this.sentinel3Int);
//
//    this.sentinel4Int = new Sentinel<Integer>();
//    this.million = new Node<Integer>(1000000, this.sentinel4Int, this.sentinel4Int);
//    this.sixty = new Node<Integer>(60, this.sentinel4Int, this.million);
//    this.deque4Int = new Deque<>(this.sentinel4Int);
//  }
//
//  // tests constructing a node
//  void testConstructingNode(Tester t) {
//    Sentinel<String> sentinelBad = new Sentinel<String>();
//    Node<String> badNode = null;
//
//    t.checkConstructorException(new IllegalArgumentException("next node cannot be null"), "Node",
//        "yeet", badNode, sentinelBad);
//    t.checkConstructorException(new IllegalArgumentException("previous node cannot be null"),
//        "Node", "yeet", sentinelBad, badNode);
//  }
//
//  // tests getting the size of a deque
//  void testSize(Tester t) {
//    this.initTestConditions();
//
//    t.checkExpect(deque1.size(), 0);
//    t.checkExpect(deque2.size(), 4);
//    t.checkExpect(deque3.size(), 4);
//  }
//
//  // tests inserting an element at the head
//  void testAddAtHead(Tester t) {
//    this.initTestConditions();
//
//    this.deque1.addAtHead("aaa");
//
//    this.deque2.addAtHead("aaa");
//
//    this.deque3.addAtHead("neel");
//
//    Sentinel<String> newSent1 = new Sentinel<String>();
//    Sentinel<String> newSent2 = new Sentinel<String>();
//    Sentinel<String> newSent3 = new Sentinel<String>();
//
//    Node<String> bigBuffMuscles = new Node<String>("aaa", newSent1, newSent1);
//
//    Node<String> aaa = new Node<String>("aaa", newSent2, newSent2);
//    Node<String> abc = new Node<String>("abc", newSent2, aaa);
//    Node<String> bcd = new Node<String>("bcd", newSent2, abc);
//    Node<String> cde = new Node<String>("cde", newSent2, bcd);
//    Node<String> def = new Node<String>("def", newSent2, cde);
//
//    Node<String> neel = new Node<String>("neel", newSent3, newSent3);
//    Node<String> aoun = new Node<String>("president", newSent3, neel);
//    Node<String> fundies = new Node<String>("hard", newSent3, aoun);
//    Node<String> lerner = new Node<String>("professor", newSent3, fundies);
//    Node<String> white = new Node<String>("building", newSent3, lerner);
//
//    Deque<String> newDeque1 = new Deque<>(newSent1);
//    Deque<String> newDeque2 = new Deque<>(newSent2);
//    Deque<String> newDeque3 = new Deque<>(newSent3);
//
//    t.checkExpect(deque1, newDeque1);
//    t.checkExpect(deque2, newDeque2);
//    t.checkExpect(deque3, newDeque3);
//  }
//
//  // tests inserting an element at the head
//  void testAddAtTail(Tester t) {
//    this.initTestConditions();
//
//    this.deque1.addAtTail("aaa");
//
//    this.deque2.addAtTail("aaa");
//
//    this.deque3.addAtTail("neel");
//
//    Sentinel<String> newSent1 = new Sentinel<String>();
//    Sentinel<String> newSent2 = new Sentinel<String>();
//    Sentinel<String> newSent3 = new Sentinel<String>();
//
//    Node<String> bigBuffMuscles = new Node<String>("aaa", newSent1, newSent1);
//
//    Node<String> abc = new Node<String>("abc", newSent2, newSent2);
//    Node<String> bcd = new Node<String>("bcd", newSent2, abc);
//    Node<String> cde = new Node<String>("cde", newSent2, bcd);
//    Node<String> def = new Node<String>("def", newSent2, cde);
//    Node<String> aaa = new Node<String>("aaa", newSent2, def);
//
//    Node<String> aoun = new Node<String>("president", newSent3, newSent3);
//    Node<String> fundies = new Node<String>("hard", newSent3, aoun);
//    Node<String> lerner = new Node<String>("professor", newSent3, fundies);
//    Node<String> white = new Node<String>("building", newSent3, lerner);
//    Node<String> neel = new Node<String>("neel", newSent3, white);
//
//    Deque<String> newDeque1 = new Deque<>(newSent1);
//    Deque<String> newDeque2 = new Deque<>(newSent2);
//    Deque<String> newDeque3 = new Deque<>(newSent3);
//
//    t.checkExpect(deque1, newDeque1);
//    t.checkExpect(deque2, newDeque2);
//    t.checkExpect(deque3, newDeque3);
//  }
//
//  // tests removing element at the head
//  void testRemoveHead(Tester t) {
//    this.initTestConditions();
//
//    Sentinel<String> withoutHead2 = new Sentinel<String>();
//    Node<String> bcd = new Node<String>("bcd", withoutHead2, withoutHead2);
//    Node<String> cde = new Node<String>("cde", withoutHead2, bcd);
//    Node<String> def = new Node<String>("def", withoutHead2, cde);
//    Deque<String> dequeWithoutHead2 = new Deque<String>(withoutHead2);
//
//    Sentinel<String> withoutHead3 = new Sentinel<String>();
//    Node<String> fundies = new Node<String>("hard", withoutHead3, withoutHead3);
//    Node<String> lerner = new Node<String>("professor", withoutHead3, fundies);
//    Node<String> white = new Node<String>("building", withoutHead3, lerner);
//    Deque<String> dequeWithoutHead3 = new Deque<String>(withoutHead3);
//
//    String removedVal1 = this.deque2.removeFromHead();
//    String removedVal2 = this.deque3.removeFromHead();
//    t.checkException(new RuntimeException("Trying to delete an element from an empty list"),
//        this.deque1, "removeFromHead");
//    t.checkExpect(deque2, dequeWithoutHead2);
//    t.checkExpect(deque3, dequeWithoutHead3);
//    t.checkExpect(removedVal1, "abc");
//    t.checkExpect(removedVal2, "president");
//  }
//
//  // tests removing element at tail
//  void testRemoveTail(Tester t) {
//    this.initTestConditions();
//
//    Sentinel<String> withoutTail2 = new Sentinel<String>();
//    Node<String> abc = new Node<String>("abc", withoutTail2, withoutTail2);
//    Node<String> bcd = new Node<String>("bcd", withoutTail2, abc);
//    Node<String> cde = new Node<String>("cde", withoutTail2, bcd);
//    Deque<String> dequeWithoutTail2 = new Deque<String>(withoutTail2);
//
//    Sentinel<String> withoutTail3 = new Sentinel<String>();
//    Node<String> aoun = new Node<String>("president", withoutTail3, withoutTail3);
//    Node<String> fundies = new Node<String>("hard", withoutTail3, aoun);
//    Node<String> lerner = new Node<String>("professor", withoutTail3, fundies);
//    Deque<String> dequeWithoutTail3 = new Deque<String>(withoutTail3);
//
//    String removedVal1 = this.deque2.removeFromTail();
//    String removedVal2 = this.deque3.removeFromTail();
//    t.checkException(new RuntimeException("Trying to delete an element from an empty list"),
//        this.deque1, "removeFromHead");
//    t.checkExpect(deque2, dequeWithoutTail2);
//    t.checkExpect(deque3, dequeWithoutTail3);
//    t.checkExpect(removedVal1, "def");
//    t.checkExpect(removedVal2, "building");
//  }
//
//  // tests removing element at tail
//  void testFind(Tester t) {
//    this.initTestConditions();
//
//    // test empty
//    Sentinel<String> randSent1 = new Sentinel<String>();
//    Node<String> bcd = new Node<String>("bcd", randSent1, randSent1);
//    Node<String> cdefg = new Node<String>("cdefg", randSent1, bcd);
//    Node<String> def = new Node<String>("def", randSent1, cdefg);
//    Deque<String> randDeqString = new Deque<String>(randSent1);
//
//    // TEST BIG WORDS
//
//    // empty list
//    t.checkExpect(this.deque1.find(new BigWords()), new Sentinel<String>());
//
//    // non-empty list but test fails
//    t.checkExpect(this.deque2.find(new BigWords()), this.sentinel2);
//
//    // non-empty list where test succeeds
//    t.checkExpect(this.deque3.find(new BigWords()),
//        new Node<String>("president", this.fundies, this.sentinel3));
//
//    // mixed test with both strings that fail and succeed (returns 2nd element)
//    t.checkExpect(randDeqString.find(new BigWords()), new Node<String>("cdefg", def, bcd));
//
//    // TEST SAME WORD
//
//    // tests finding first word matching a given word
//    // empty list
//    t.checkExpect(this.deque1.find(new SameWord("professor")), new Sentinel<String>());
//
//    // "def" should be in deque2
//    t.checkExpect(this.deque2.find(new SameWord("def")),
//        new Node<String>("def", this.sentinel2, this.cde));
//
//    // but "def" should not be in deque3
//    t.checkExpect(this.deque3.find(new SameWord("def")), this.sentinel3);
//
//    // "professor" should not be in deque2
//    t.checkExpect(this.deque2.find(new SameWord("professor")), this.sentinel2);
//
//    // but "professor" should be in deque3
//    t.checkExpect(this.deque3.find(new SameWord("professor")),
//        new Node<String>("professor", this.white, this.fundies));
//
//    // TESTS BIG INTEGERS
//
//    // empty list
//    t.checkExpect(this.deque1Int.find(new BigNumbers()), new Sentinel<Integer>());
//
//    // non-empty list but test fails
//    t.checkExpect(this.deque2Int.find(new BigNumbers()), this.sentinel2Int);
//
//    // non-empty list where test succeeds
//    t.checkExpect(this.deque4Int.find(new BigNumbers()),
//        new Node<Integer>(1000000, this.sixty, this.sentinel4Int));
//
//    // mixed test with both ints that fail and succeed (returns 2nd element)
//    t.checkExpect(this.deque3Int.find(new BigNumbers()),
//        new Node<Integer>(50, this.twelve, this.eleven));
//
//    // TESTS SAMENUMBER
//
//    // tests if we can find the first number of a given value
//    // empty list
//    t.checkExpect(this.deque1Int.find(new SameNumber(10)), new Sentinel<Integer>());
//
//    // 6 should be in deque2
//    t.checkExpect(this.deque2Int.find(new SameNumber(6)),
//        new Node<Integer>(6, this.ten, this.four));
//
//    // but 6 should not be in deque3
//    t.checkExpect(this.deque3Int.find(new SameNumber(6)), this.sentinel3Int);
//
//    // 12 should not be in deque2
//    t.checkExpect(this.deque2Int.find(new SameNumber(12)), this.sentinel2Int);
//
//    // but 12 should be in deque3
//    t.checkExpect(this.deque3Int.find(new SameNumber(12)),
//        new Node<Integer>(12, this.twohundred, this.fifty));
//
//  }
//
//  // tests removing a node in a deque
//  void testRemoveNode(Tester t) {
//    this.initTestConditions();
//
//    Sentinel<String> newSent2 = new Sentinel<String>();
//    Sentinel<String> newSent3 = new Sentinel<String>();
//
//    Node<String> abc = new Node<String>("abc", newSent2, newSent2);
//    Node<String> cde = new Node<String>("cde", newSent2, abc);
//    Node<String> def = new Node<String>("def", newSent2, cde);
//
//    Node<String> fundies = new Node<String>("hard", newSent3, newSent3);
//    Node<String> lerner = new Node<String>("professor", newSent3, fundies);
//    Node<String> white = new Node<String>("building", newSent3, lerner);
//
//    Deque<String> newDeque2 = new Deque<>(newSent2);
//    Deque<String> newDeque3 = new Deque<>(newSent3);
//
//    // remove the header in empty (should do nothing)
//    this.deque1.removeNode(this.deque1.header);
//
//    // remove bcd
//    this.deque2.removeNode(this.bcd);
//
//    // remove aoun
//    this.deque3.removeNode(this.aoun);
//
//    t.checkExpect(this.deque1, deque1);
//    t.checkExpect(this.deque2, newDeque2);
//    t.checkExpect(this.deque3, newDeque3);
//
//    Sentinel<String> newSent4 = new Sentinel<String>();
//    Node<String> abc2 = new Node<String>("abc", newSent4, newSent4);
//    Node<String> def2 = new Node<String>("def", newSent4, abc2);
//    Deque<String> newDeque4 = new Deque<>(newSent4);
//
//    // remove a second value from deque2
//    this.deque2.removeNode(this.cde);
//
//    // remove the header in non-empty (should do nothing)
//    this.deque3.removeNode(this.deque3.header);
//
//    t.checkExpect(this.deque2, newDeque4);
//    t.checkExpect(this.deque3, newDeque3);
//
//  }
//
//}