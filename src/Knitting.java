import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import tester.Tester;

// a knitted piece of fabric
class KnittedFabric {

  ArrayList<ArrayList<IStitch>> stitches;

  // makes a new empty fabric
  public KnittedFabric() {
    this.stitches = new ArrayList<>();
  }

  // makes a fabric with given stitches
  public KnittedFabric(ArrayList<ArrayList<IStitch>> stitches) {
    int firstSize = 0;
    if (!stitches.isEmpty()) {
      firstSize = stitches.get(0).size();
    }
    for (ArrayList<IStitch> row : stitches) {
      if (row.size() != firstSize) {
        throw new IllegalArgumentException("rows must be the same size");
      }
    }
    this.stitches = stitches;
  }

  // adds a row of stitches to the current fabric
  // EFFECT: includes a row of stitches in this fabric
  KnittedFabric addRow(Iterator<IStitch> rowIter) {
    ArrayList<IStitch> newRow = new ArrayList<>();
    while (rowIter.hasNext()) {
      newRow.add(rowIter.next());
    }
    if(!this.stitches.isEmpty() && (newRow.size() != this.stitches.get(0).size())) {
      throw new RuntimeException("cannot add differently sized row to fabric");
    }

    stitches.add(0, newRow);
    return this;
  }

  // draws a fabric as a string of text
  String renderFabric() {
    String completedFabric = "";

    for (int i = 0; i < stitches.size(); i += 1) {
      for (IStitch stich : this.stitches.get(i)) {
        completedFabric += stich.render(true);
      }
      // to stop from producing a new line at end of rendering
      if (i != stitches.size() - 1) {
        completedFabric += "\n";
      }
    }

    return completedFabric;
  }

  // returns a new reversed knitted fabric
  KnittedFabric reverse() {
    ArrayList<ArrayList<IStitch>> finalList = new ArrayList<>();
    for (int j = this.stitches.size() - 1; j >= 0; j -= 1) {

      ArrayList<IStitch> rowToBeReversed = this.stitches.get(j);
      ArrayList<IStitch> reversedRow = new ArrayList<>();

      for (int k = rowToBeReversed.size() - 1; k >= 0; k -= 1) {
        reversedRow.add(rowToBeReversed.get(k));
      }

      finalList.add(reversedRow);
    }
    return new KnittedFabric(finalList);
  }

  // checks if this knitted fabric is the same as another
  boolean sameFabric(KnittedFabric other) {
    return this.sameFabricHelper(other) || this.sameFabricHelper(other.reverse());
  }

  // checks if this knitted fabric is the same as another
  boolean sameFabricHelper(KnittedFabric other) {
    // extra test to make sure same number of rows
    if (other.stitches.size() != this.stitches.size()) {
      return false;
    }

    for (int i = 0; i < this.stitches.size(); i += 1) {
      ArrayList<IStitch> thisRow = this.stitches.get(i);
      ArrayList<IStitch> otherRow = other.stitches.get(i);

      // extra test to make sure same number of columns in that row
      if (thisRow.size() != otherRow.size()) {
        return false;
      }

      for (int k = 0; k < thisRow.size(); k += 1) {
        if (!thisRow.get(k).equals(otherRow.get(k))) {
          return false;
        }
      }

    }

    return true;
  }
}

// represents a stitch
interface IStitch {
  // renders a stitch
  String render(boolean isFront);

}

// a knit, which is a kind of stitch
class Knit implements IStitch {

  // renders a knit stitch
  public String render(boolean isFront) {
    if (isFront) {
      return "V";
    }
    else
      return "-";
  }

  // checks if this knit is the same as the other
  public boolean equals(Object other) {
    return (other instanceof Knit);
  }

  // hashes this
//  public int hashCode() {
//    return Objects.hash(this);
//  }

}

// represents a purl, which is a kind of stitch
class Purl implements IStitch {

  // renders a purl stitch
  public String render(boolean isFront) {
    if (isFront) {
      return "-";
    }
    else
      return "V";
  }

  // checks if this purl is the same as the other
  public boolean equals(Object other) {
    return (other instanceof Purl);
  }

  // hashes this
//  public int hashCode() {
//    return Objects.hash(this);
//  }

}

// a reversed iterator
class ReverseIterator<T> implements Iterator<T> {

  Stack<T> stack;

  // EFFECT: adds iterator's values to the stack field
  public ReverseIterator(Iterator<T> source) {
    this.stack = new Stack<T>();

    while (source.hasNext()) {
      stack.add(source.next());
    }
  }

  // checks if this reverse iterator has a next value
  public boolean hasNext() {
    return !stack.isEmpty();
  }

  // gets the next of the reversed iterator
  public T next() {
    if (hasNext()) {
      return stack.pop();
    }
    else {
      throw new UnsupportedOperationException("can't get next");
    }
  }

}

// An entire fabric's worth of instructions
class KnitFabricInstructions {

  ArrayList<RowInstruction> fabric;
  boolean isFront;

  // an empty instruction
  public KnitFabricInstructions() {
    this.fabric = new ArrayList<RowInstruction>();
    this.isFront = true;
  }

  // constructs a given instruction
  public KnitFabricInstructions(ArrayList<RowInstruction> fabric) {
    int firstSize = 0;
    if (!fabric.isEmpty()) {
      firstSize = fabric.get(0).rowSize();
    }
    for (RowInstruction row : fabric) {
      if (row.rowSize() != firstSize) {
        throw new IllegalArgumentException("rows must be the same size");
      }
    }
    this.fabric = fabric;
    this.isFront = true;
  }

  // adds a row of instructions to the growing fabric (must be passed only a row)
  // EFFECT: Adds a row of instructions and flips the current fabric side
  KnitFabricInstructions addRow(Iterator<IInstruction> instructions) {
    ArrayList<IInstruction> tempInstruct = new ArrayList<IInstruction>();
    int size = 0;



    if (!this.isFront) {
      while (instructions.hasNext()) {
        tempInstruct.add(instructions.next());
        size += 1;
      }
//      if(!this.fabric.isEmpty() && (instructions.size() != this.fabric.get(0).rowSize())) {
//        throw new RuntimeException("cannot add differently sized row to fabric");
//      }
      this.isFront = false; // next row will be on the back

    }
    else {
      Iterator<IInstruction> reversed = new ReverseIterator<IInstruction>(instructions);
      while (reversed.hasNext()) {
        tempInstruct.add(reversed.next());
        size += 1;
      }

      this.isFront = true; // next row will be on the front
    }
//    if (!this.fabric.isEmpty()) {
//      if (this.fabric.get(0).row.size() != size) {
//        throw new IllegalArgumentException("Trying to construct a non-rectangular fabric");
//      }
//    }
    this.fabric.add(new RowInstruction(tempInstruct));

    return this;
  }

  // adds newly formed rows to the top of the fabric
  KnittedFabric makeFabric() {
    KnittedFabric kf = new KnittedFabric();
    boolean isFront = true;
    for (RowInstruction instruction : this.fabric) {
      ArrayList<IStitch> fabricRow = instruction.fabricRow(isFront);
      isFront = !isFront;
      kf.addRow(fabricRow.iterator());
    }
    return kf;
  }

  boolean sameInstructions(KnitFabricInstructions other) {
    return this.makeFabric().sameFabric(other.makeFabric());
  }
}

class RowInstruction {

  ArrayList<IInstruction> row;

  public RowInstruction(ArrayList<IInstruction> row) {
    this.row = row;
  }

  // take a list of instructions and make it a row of stitches
  ArrayList<IStitch> fabricRow(boolean isFront) {
    ArrayList<IStitch> temp = new ArrayList<IStitch>();
    for (IInstruction inst : this.row) {
      if (isFront) {
        temp.addAll(inst.makeStitch(isFront));
      } else {
        temp.addAll(0, inst.makeStitch(isFront));
      }
    }
    return temp;
  }

  // returns the size of this row
  int rowSize() {
    return this.row.size();
  }

}

interface IInstruction {
  // makes an instruction a sttich
  ArrayList<IStitch> makeStitch(boolean isFront);
}

// an instruction that repeats the given instructions as many times as wanted
class RepeatInstruction implements IInstruction {

  ArrayList<IInstruction> instructions;
  int count;

  public RepeatInstruction(Iterator<IInstruction> instructions, int count) {
    if (count < 0) {
      throw new IllegalArgumentException("Can't have a negative repeat");
    }
    ArrayList<IInstruction> tempVals = new ArrayList<IInstruction>();
    while (instructions.hasNext()) {
      tempVals.add(instructions.next());
    }
    this.instructions = tempVals;
    this.count = count;
  }

  // make a stitches out of a repeat instruction
  public ArrayList<IStitch> makeStitch(boolean isFront) {
    ArrayList<IStitch> temp = new ArrayList<IStitch>();

    for (int i = 0; i < count; i += 1) {
      for (IInstruction inst : instructions) {
        if (isFront) {
          temp.addAll(0, inst.makeStitch(isFront));
        } else {
          temp.addAll(inst.makeStitch(isFront));
        }
      }
    }

    return temp;
  }
}

// an instruction for a purl
class PurlInstruction implements IInstruction {

  // make a stitch out of a purl instruction
  public ArrayList<IStitch> makeStitch(boolean isFront) {
    if (isFront) {
      return new ArrayList<IStitch>(List.of(new Purl()));
    } else {
      return new ArrayList<IStitch>(List.of(new Knit()));
    }
  }
}

// an instruction for a knit
class KnitInstruction implements IInstruction {

  // make a stitch out of a knit instruction
  public ArrayList<IStitch> makeStitch(boolean isFront) {
    if (isFront) {
      return new ArrayList<IStitch>(List.of(new Knit()));
    } else {
      return new ArrayList<IStitch>(List.of(new Purl()));
    }
  }
}

class ExamplesKnitting {
  // empty fabrics
  KnittedFabric mtFab1;
  KnittedFabric mtFab2;

  // knit row
  ArrayList<IStitch> knits;

  // purl row
  ArrayList<IStitch> purls;

  // mixed row
  ArrayList<IStitch> mix;

  // different length mixed row
  ArrayList<IStitch> mix2;
  ArrayList<IStitch> mix2Rev;

  // knit fabric
  KnittedFabric knitFab;
  KnittedFabric knitFabMul;

  // purl fabric
  KnittedFabric purlFab;
  KnittedFabric purlFabMul;

  // mixed fabric
  KnittedFabric mixedFab1;
  KnittedFabric mixedFab1Rev;
  KnittedFabric mixedFab2;
  KnittedFabric mixedFab2Rev;
  KnittedFabric mixedFab3;
  KnittedFabric mixedFab3Rev;
  KnittedFabric mixedFab4;
  KnittedFabric mixedFab4Rev;
  KnittedFabric mixedFab5;
  KnittedFabric mixedFab5Rev;
  KnittedFabric mixedFab6;
  KnittedFabric mixedFab6Rev;

  // Instructions

  // Purl Instruction
  PurlInstruction purlInst;

  // Knit instruction
  KnitInstruction knitInst;

  // Repeat Instruction
  RepeatInstruction repeatInstKnit4;
  RepeatInstruction repeatInstPurl2;
  RepeatInstruction repeatInstKnit2;
  RepeatInstruction repeatInstRepPurl2Knit2_3;
  RepeatInstruction repeatInstPurl4;

  // Second Example (w/ 2 Rows)
  RepeatInstruction repeatInstKnit1;
  RepeatInstruction repeatInstPurl5;

  // Row Instruction
  RowInstruction fancierSet;
  RowInstruction rowOne2RowsSet;
  RowInstruction rowTwo2RowsSet;
  RowInstruction oneRowKnitsSet;
  RowInstruction oneRowMixedSet;


  // Knit Fabric Instructions
  KnitFabricInstructions oneRowFancy;
  KnitFabricInstructions twoRows;
  KnitFabricInstructions oneRowKnits;
  KnitFabricInstructions oneRowMixed;


  void initTestConditions() {
    this.mtFab1 = new KnittedFabric();
    this.mtFab2 = new KnittedFabric(new ArrayList<ArrayList<IStitch>>());
    this.knits = new ArrayList<IStitch>(List.of(new Knit(), new Knit(), new Knit()));
    this.purls = new ArrayList<IStitch>(List.of(new Purl(), new Purl(), new Purl()));
    this.mix = new ArrayList<IStitch>(List.of(new Purl(), new Knit(), new Purl()));
    this.mix2 = new ArrayList<IStitch>(List.of(new Knit(), new Purl(), new Knit(), new Knit()));
    this.mix2Rev = new ArrayList<IStitch>(List.of(new Knit(), new Knit(), new Purl(), new Knit()));
    this.knitFab = new KnittedFabric(new ArrayList<ArrayList<IStitch>>(List.of(this.knits)));
    this.knitFabMul = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.knits, this.knits)));
    this.purlFab = new KnittedFabric(new ArrayList<ArrayList<IStitch>>(List.of(this.purls)));
    this.purlFabMul = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.purls, this.purls, this.purls)));
    this.mixedFab1 = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.purls)));
    this.mixedFab1Rev = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.purls, this.knits)));
    this.mixedFab2 = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.purls, this.mix, this.mix)));
    this.mixedFab2Rev = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.mix, this.mix, this.purls)));
    this.mixedFab3 = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.mix, this.knits, this.knits)));
    this.mixedFab3Rev = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.knits, this.mix)));
    this.mixedFab4 = new KnittedFabric(new ArrayList<ArrayList<IStitch>>(List.of(this.mix)));
    this.mixedFab4Rev = new KnittedFabric(new ArrayList<ArrayList<IStitch>>(List.of(this.mix)));
    this.mixedFab5 = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.knits, this.mix)));
    this.mixedFab5Rev = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.mix, this.knits, this.knits)));
    this.mixedFab6 = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.mix2, this.mix2)));
    this.mixedFab6Rev = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.mix2Rev, this.mix2Rev, this.knits)));
    this.purlInst = new PurlInstruction();
    this.knitInst = new KnitInstruction();
    this.repeatInstKnit4 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.knitInst)).iterator(), 4);
    this.repeatInstPurl2 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.purlInst)).iterator(), 2);
    this.repeatInstKnit2 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.knitInst)).iterator(), 2);
    this.repeatInstRepPurl2Knit2_3 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.repeatInstPurl2, repeatInstKnit2)).iterator(), 3);
    this.repeatInstPurl4 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.purlInst)).iterator(), 4);
    this.fancierSet = new RowInstruction(new ArrayList<IInstruction>(
            List.of(this.repeatInstKnit4, this.repeatInstRepPurl2Knit2_3, this.repeatInstPurl4)));
    this.oneRowFancy = new KnitFabricInstructions(
            new ArrayList<RowInstruction>(List.of(this.fancierSet)));
    this.repeatInstKnit1 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.knitInst)).iterator(), 1);
    this.repeatInstPurl5 = new RepeatInstruction(
            new ArrayList<IInstruction>(List.of(this.purlInst)).iterator(), 5);
    this.rowOne2RowsSet = new RowInstruction(
            new ArrayList<IInstruction>(List.of(this.repeatInstKnit1, this.repeatInstPurl5)));
    this.rowTwo2RowsSet = new RowInstruction(
            new ArrayList<IInstruction>(List.of(this.repeatInstKnit2, this.repeatInstPurl4)));
    this.twoRows = new KnitFabricInstructions(
            new ArrayList<RowInstruction>(List.of(this.rowOne2RowsSet, this.rowTwo2RowsSet)));
    this.oneRowKnitsSet = new RowInstruction(
            new ArrayList<IInstruction>(List.of(this.repeatInstKnit2)));
    this.oneRowKnits = new KnitFabricInstructions(
            new ArrayList<RowInstruction>(List.of(this.oneRowKnitsSet)));
    this.oneRowMixedSet = new RowInstruction(new ArrayList<IInstruction>(List.of(this.purlInst,
            this.purlInst, this.purlInst, this.knitInst, this.purlInst)));
    this.oneRowMixed =
            new KnitFabricInstructions(new ArrayList<RowInstruction>(List.of(this.oneRowMixedSet)));
  }

  // tests adding a row
  void testAddRow(Tester t) {
    this.initTestConditions();

    // add a row of mixed stitches to an empty fabric
    KnittedFabric mixMt = this.mtFab1.addRow(this.mix.iterator());

    // add row of knits to fabric with row of purls
    KnittedFabric newFab = this.purlFab
            .addRow(new ArrayList<IStitch>(List.of(new Knit(), new Knit(), new Knit())).iterator());
    // fabric with row of purls and row of knits
    KnittedFabric purlKnit = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.purls)));

    // add multiple rows of stitches to mixed fabric
    KnittedFabric mixx = this.mixedFab3.addRow(this.mix.iterator());
    KnittedFabric mixx2 = mixx.addRow(this.purls.iterator());
    // row of mixed stitches and mixed fabric
    KnittedFabric mix = new KnittedFabric(new ArrayList<ArrayList<IStitch>>(
            List.of(this.purls, this.mix, this.mix, this.knits, this.knits)));

    // test mutation of add row
    this.mixedFab2.addRow(this.knits.iterator());
    // mixedfab2 with knit row
    KnittedFabric mixKnit = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(this.knits, this.purls, this.mix, this.mix)));

    t.checkExpect(mixMt, this.mixedFab4);
    t.checkExpect(newFab, purlKnit);
    t.checkExpect(mixx2, mix);
    t.checkExpect(this.mixedFab2, mixKnit);
  }

  // tests rendering fabrics
  void testRender(Tester t) {
    this.initTestConditions();

    String emptyRenderS = "";
    String knitFabS = "VVV";
    String knitFabMulS = "VVV\nVVV\nVVV";
    String purlFabS = "---";
    String purlFabMulS = "---\n---\n---";
    String mixedFab1S = "VVV\n---";
    String mixedFab2S = "---\n-V-\n-V-";
    String mixedFab3S = "-V-\nVVV\nVVV";

    t.checkExpect(this.mtFab1.renderFabric(), emptyRenderS);
    t.checkExpect(this.knitFab.renderFabric(), knitFabS);
    t.checkExpect(this.knitFabMul.renderFabric(), knitFabMulS);
    t.checkExpect(this.purlFab.renderFabric(), purlFabS);
    t.checkExpect(this.purlFabMul.renderFabric(), purlFabMulS);
    t.checkExpect(this.mixedFab1.renderFabric(), mixedFab1S);
    t.checkExpect(this.mixedFab2.renderFabric(), mixedFab2S);
    t.checkExpect(this.mixedFab3.renderFabric(), mixedFab3S);

  }

  // tests determining if two fabrics are the same
  // We did not test the helper separately because they would be the exact same
  // tests
  void testSameFabric(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.mtFab1.reverse().sameFabric(this.mtFab1), true);
    t.checkExpect(this.mtFab1.sameFabric(this.mtFab1), true);
    t.checkExpect(this.mtFab1.sameFabric(this.knitFab), false);
    t.checkExpect(this.knitFab.sameFabric(this.knitFab), true);
    t.checkExpect(this.knitFab.sameFabric(this.purlFab), false);
    t.checkExpect(this.knitFab.sameFabric(this.mixedFab1), false);
    t.checkExpect(this.mixedFab2.sameFabric(this.mixedFab2), true);
    t.checkExpect(this.mixedFab5.sameFabric(this.mixedFab3), true);
    t.checkExpect(this.mixedFab5.sameFabric(this.mixedFab2), false);
    t.checkExpect(this.mixedFab2Rev.sameFabric(this.mixedFab2), true);
    t.checkExpect(this.mixedFab1Rev.sameFabric(this.mixedFab1), true);
    t.checkExpect(this.mixedFab1Rev.sameFabric(this.mixedFab1), true);
    t.checkExpect(this.knitFab.sameFabric(this.knitFab.reverse()), true);
    t.checkExpect(this.purlFab.sameFabric(this.knitFab.reverse()), false);

  }

  // test equals method

  // tests reversing an iterator
  void testReverseIterator(Tester t) {
    this.initTestConditions();

    // make more tests

    // knits - mixed2rev
    Iterator knitIter = this.knits.iterator();
    t.checkExpect(knitIter.next(), new Knit());
    t.checkExpect(knitIter.next(), new Knit());
    t.checkExpect(knitIter.next(), new Knit());
    t.checkExpect(knitIter.hasNext(), false);

    ReverseIterator revKnits = new ReverseIterator(this.knits.iterator());
    t.checkExpect(revKnits.next(), new Knit());
    t.checkExpect(revKnits.next(), new Knit());
    t.checkExpect(revKnits.next(), new Knit());
    t.checkExpect(revKnits.hasNext(), false);
  }

  // tests reversing a knitted fabric
  void testReverse(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.mtFab1.reverse(), this.mtFab1);
    t.checkExpect(this.mixedFab1.reverse(), this.mixedFab1Rev);
    t.checkExpect(this.mixedFab2.reverse(), this.mixedFab2Rev);
    t.checkExpect(this.mixedFab3.reverse(), this.mixedFab3Rev);
    t.checkExpect(this.mixedFab4.reverse(), this.mixedFab4Rev);
    t.checkExpect(this.mixedFab5.reverse(), this.mixedFab5Rev);
    t.checkExpect(this.mixedFab6.reverse(), this.mixedFab6Rev);
  }

  // tests adding row(s) to a knitted fabric
  void testAddRow() {
    this.initTestConditions();


  }

  void testMakeFabric(Tester t) {
    this.initTestConditions();

    // the one row fancier fabric
    ArrayList<IStitch> oneRowStitch = new ArrayList<IStitch>(
            List.of(new Purl(), new Purl(), new Purl(), new Purl(), new Knit(), new Knit(),
                    new Purl(),
                    new Purl(), new Knit(), new Knit(), new Purl(), new Purl(), new Knit(),
                    new Knit(),
                    new Purl(), new Purl(), new Knit(), new Knit(), new Knit(), new Knit()));
    KnittedFabric oneRow = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(oneRowStitch)));

    // the double row fabric
    ArrayList<IStitch> firstRowStitch = new ArrayList<IStitch>(
            List.of(new Knit(), new Knit(), new Purl(), new Purl(), new Purl(), new Purl()));
    ArrayList<IStitch> secondRowStitch = new ArrayList<IStitch>(
            List.of(new Purl(), new Purl(), new Purl(), new Purl(), new Purl(), new Knit()));
    KnittedFabric twoRowFab = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(firstRowStitch, secondRowStitch)));

    t.checkExpect(this.oneRowFancy.makeFabric(), oneRow);
    t.checkExpect(this.oneRowFancy.makeFabric().renderFabric(), oneRow);


    this.initTestConditions();

    t.checkExpect(this.twoRows.makeFabric(), twoRowFab);

    this.initTestConditions();

    ArrayList<IStitch> twoKnits = new ArrayList<IStitch>(
            List.of(new Knit(), new Knit()));
    KnittedFabric oneRowKnitsAnswer = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(twoKnits)));

    t.checkExpect(this.oneRowKnits.makeFabric(), oneRowKnitsAnswer);

    ArrayList<IStitch> mixedRow = new ArrayList<IStitch>(
            List.of(new Purl(), new Purl(), new Purl(), new Knit(), new Purl()));
    KnittedFabric oneRowMixedAnswer = new KnittedFabric(
            new ArrayList<ArrayList<IStitch>>(List.of(mixedRow)));

    t.checkExpect(this.oneRowMixed.makeFabric(), oneRowMixedAnswer);

  }

}