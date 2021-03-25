import java.util.ArrayDeque;
import java.util.ArrayList;

import tester.*;
import javalib.worldimages.*;

import java.awt.Color;
import java.util.Deque;

// squeezes pictures, removing blank/unimportant space
public class SqueezingPictures {

  String filename;
  FromFileImage image;
  Deque<Deque<Pixel>> grid;

  public SqueezingPictures(String filename) {
    this.filename = filename;
    this.image = new FromFileImage(filename);
    this.grid = new ArrayDeque<Deque<Pixel>>();
    // fill in the 2d deque grid
    // there is no more efficient way to fill in every pixel of the grid than to
    // go through each pixel individually
    // i is going down (columns) and j is going right (rows)
    for (int i = 0; i < image.getHeight(); i += 1) {
      Deque<Pixel> curRow = new ArrayDeque<Pixel>();
      ArrayList<Pixel> prevRow = new ArrayList<Pixel>();

      // if there already is a row, we can set the previous row to that value
      if (!grid.isEmpty()) {
        prevRow = new ArrayList<Pixel>(grid.peekLast());
      }

      // for each pixel in the row, insert it into the deque
      for (int j = 0; j < image.getWidth(); j += 1) {
        Pixel curPixel;
        if (i == 0 && j == 0) {
          // top left corner
          curPixel = new Pixel(this.image.getColorAt(j, i), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel(), new UnknownPixel());

        }
        else if (i == 0 && j == image.getWidth()) {
          // top right corner
          curPixel = new Pixel(this.image.getColorAt(j, i), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel(), curRow.peekLast());

        }
        else if (i == image.getHeight() && j == 0) {
          // bottom left corner
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel());

        }
        else if (i == image.getHeight() && j == image.getWidth()) {
          // bottom right corner
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        }
        else if (i == 0) {
          // top row
          curPixel = new Pixel(this.image.getColorAt(j, i), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel(), curRow.peekLast());

        }
        else if (i == image.getHeight()) {
          // bottom row
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        }
        else if (j == 0) {
          // left column
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel());

        }
        else if (j == image.getWidth()) {
          // right column
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        }
        else {
          // no edges
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        }
        curRow.addLast(curPixel);
      }
      grid.addLast(curRow);
    }
  }
}

// represents a pixel
interface IPixel {

  // gets the brightness of a pixel
  double getBrightness();

  // gets the energy of a pixel
  double getEnergy();

  // get pixels in 4 different directions
  // gets the pixel above
  IPixel getUp();

  // gets the pixel to the right
  IPixel getRight();

  // gets the pixel below
  IPixel getDown();

  // gets the pixel to the left
  IPixel getLeft();

  // sets pixels in 4 different directions
  void setUp(IPixel newUp);

  // EFFECT: sets this pixel's right to another pixel
  void setRight(IPixel right);

  // EFFECT: sets this pixel's down to another pixel
  void setDown(IPixel down);

  // EFFECT: sets this pixel's left to another pixel
  void setLeft(IPixel left);

  // checks to see if this pixel is valid
  boolean validPixel();

}

class SeamInfo {

  Pixel currentPixel;
  double totalWeight;
  SeamInfo cameFrom;

  public SeamInfo(Pixel currentPixel, double totalWeight, SeamInfo cameFrom) {
    
  }

}


// constructs an individual pixel with references to neighbors
class Pixel implements IPixel {

  Color color;

  // the 4 neighbors
  // in order to access topright, we would use up then right
  IPixel up;
  IPixel right;
  IPixel down;
  IPixel left;

  // creates a pixel with pixels in other directions
  // EFFECT: modifies up, right, down, and lefts' references to include this new pixel
  public Pixel(Color color, IPixel up, IPixel right, IPixel down, IPixel left) {
    this.color = color;
    this.up = up;
    this.right = right;
    this.down = down;
    this.left = left;

    this.getUp().setDown(this);
    this.getRight().setLeft(this);
    this.getDown().setUp(this);
    this.getLeft().setRight(this);
  }

  // constructs a pixel with only a color
  public Pixel(Color color) {
    this.color = color;

  }

  // gets the brightness of a pixel
  public double getBrightness() {
    int averageOfColor = (color.getBlue() + color.getRed() + color.getGreen()) / 3;
    return averageOfColor / 255.0;
  }

  // gets the eneryg of a pixel
  public double getEnergy() {
    double horizontalEnergy =
            (this.getUp().getLeft().getBrightness() + (2 * this.getLeft().getBrightness()) + this.getLeft().getBrightness())
            - (this.getUp().getRight().getBrightness() + (2 * (this.getRight().getBrightness())) + this.getDown().getRight().getBrightness());
    double verticalEnergy =
            (this.getUp().getLeft().getBrightness() + (2 * this.getUp().getBrightness()) + this.getUp().getRight().getBrightness())
            - (this.getDown().getLeft().getBrightness() + (2 * (this.getDown().getBrightness())) + this.getDown().getRight().getBrightness());
    return Math.sqrt(Math.pow(horizontalEnergy, 2) + Math.pow(verticalEnergy, 2));
  }

  // gets ithe pixel above
  public IPixel getUp() {
    return this.up;
  }

  // gets the ipixel to the right
  public IPixel getRight() {
    // if null return edge? otherwise return this?
    return this.right;
  }

  // gets the ipixel below
  public IPixel getDown() {
    return this.down;
  }

  // gets the ipixel to the left
  public IPixel getLeft() {
    return this.left;
  }

  public void setUp(IPixel newUp) {
    this.up = newUp;
  }

  public void setRight(IPixel right) {
    this.right = right;
  }

  public void setDown(IPixel down) {
    this.down = down;
  }

  public void setLeft(IPixel left) {
    this.left = left;
  }

  // is this pixel valid?
  // two pixels are only the same if they are the same object reference
  // .equals here is used for referential equality, which is what we want to check
  // since a pixel doesn't internally contain its location
  public boolean validPixel() {
    return this.getLeft().getUp().equals(this.getUp().getLeft())
            && this.getUp().getRight().equals(this.getRight().getUp())
            && this.getRight().getDown().equals(this.getDown().getRight())
            && this.getDown().getLeft().equals(this.getLeft().getDown());
  }

  // equals method - because we can't use referential equality
  // getBrightness(), getEnergy(), Color

}

// this is a pixel that we don't know because it's either not in the image
// or has not been processed yet
class UnknownPixel implements IPixel {

  public double getBrightness() {
    return 0;
  }

  public double getEnergy() {
    return 0;
  }

  public IPixel getUp() {
    return new UnknownPixel();
  }

  public IPixel getRight() {
    return new UnknownPixel();
  }

  public IPixel getDown() {
    return new UnknownPixel();
  }

  public IPixel getLeft() {
    return new UnknownPixel();
  }

  public void setUp(IPixel newUp) {

  }

  public void setRight(IPixel right) {

  }

  // sets the pixel below
  public void setDown(IPixel down) {

  }

  // sets the left pixel
  public void setLeft(IPixel left) {

  }

  // is this pixel valid?
  public boolean validPixel() {
    return true;
  }
}


class ExamplesSqueezingPictures {
  SqueezingPictures eightBy6;
  SqueezingPictures balloons;
  UnknownPixel ukPixel;
  IPixel redLine1;
  IPixel redLine2;
  IPixel redLine3;
  IPixel redLine4;

  IPixel greenLineNEU;
  IPixel greenLineSymphony;

  // deque of deque of pixels (to check constructor)

  void initTestConditions() {
    this.eightBy6 = new SqueezingPictures("8x6.jpeg");
    this.balloons = new SqueezingPictures("balloons.jpg");

    this.ukPixel = new UnknownPixel();
    this.redLine1 =  new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(), new UnknownPixel(), new UnknownPixel());
    this.redLine2 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(), new UnknownPixel(), redLine1);
    this.redLine3 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(), new UnknownPixel(), redLine2);
    this.redLine4 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(), new UnknownPixel(), redLine3);

    this.greenLineNEU = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(), new UnknownPixel(), new UnknownPixel());
    this.greenLineSymphony = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(), new UnknownPixel(), new UnknownPixel());
  }

  // we choose to test all getters at once because they all simply return a field's value
  void testGetters(Tester t) {
    this.initTestConditions();
  }

  // testing all the setters
  // we are testing all the setters because they only modify a field
  void testSetters(Tester t) {
    this.initTestConditions();

    this.greenLineNEU.setUp(this.greenLineSymphony);
    t.checkExpect(this.greenLineNEU.getUp(), this.greenLineSymphony);

    this.initTestConditions();

    this.greenLineNEU.setRight(this.greenLineSymphony);
    t.checkExpect(this.greenLineNEU.getRight(), this.greenLineSymphony);

    this.initTestConditions();

    this.greenLineNEU.setDown(this.greenLineSymphony);
    t.checkExpect(this.greenLineNEU.getDown(), this.greenLineSymphony);

    this.initTestConditions();

    this.greenLineNEU.setLeft(this.greenLineSymphony);
    t.checkExpect(this.greenLineNEU.getLeft(), this.greenLineSymphony);

  }

  // tests the grid
  void testGrid(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.eightBy6.grid.peekLast(), null);
  }

  // tests validPixel
//  void testValidPixel(Tester t) {
//    this.initTestConditions();
//
//    t.checkExpect(this.redLine1.validPixel(), true);
//    t.checkExpect(this.redLine2.validPixel(), true);
//
//    this.redLine2.setLeft(this.greenLineNEU);
//
//    t.checkExpect(this.redLine2.validPixel(), false);
//
//
//  }

}