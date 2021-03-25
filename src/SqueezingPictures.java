import java.util.ArrayDeque;
import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.Deque;

// squeezes pictures, removing blank/unimportant space
public class SqueezingPictures {

  String filename;
  FromFileImage image;
  // using our deque implementation (zipped)
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

      if (!grid.isEmpty()) {
        prevRow = new ArrayList<Pixel>(grid.peekLast());
      }

      for (int j = 0; j < image.getWidth(); j += 1) {
        Pixel curPixel;
        if (i == 0 && j == 0) {
          // top left corner
          curPixel = new Pixel(this.image.getColorAt(j, i), new EdgePixel(), null, null, new EdgePixel());

        } else if (i == 0 && j == image.getWidth()) {
          // top right corner
          curPixel = new Pixel(this.image.getColorAt(j, i), new EdgePixel(), new EdgePixel(), null, null);

        } else if (i == image.getHeight() && j == 0) {
          // bottom left corner
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new EdgePixel(), null, new EdgePixel());

        } else if (i == image.getHeight() && j == image.getWidth()) {
          // bottom right corner
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new EdgePixel(), new EdgePixel(), curRow.peekLast());

        } else if (i == 0) {
          // top row
          curPixel = new Pixel(this.image.getColorAt(j, i), new EdgePixel(), null, null, curRow.peekLast());

        } else if (i == image.getHeight()) {
          // bottom row
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), null, new EdgePixel(), curRow.peekLast());

        } else if (j == 0) {
          // left column
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), null, null, new EdgePixel());

        } else if (j == image.getWidth()) {
          // right column
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), new EdgePixel(), null, curRow.peekLast());

        } else {
          // no edges
          curPixel = new Pixel(this.image.getColorAt(j, i), prevRow.get(j), null, null, curRow.peekLast());

        }
        curRow.addLast(curPixel);
      }
      grid.addLast(curRow);
    }
  }
}

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
  void setRight(IPixel right);
  void setDown(IPixel down);
  void setLeft(IPixel left);

}

// constructs an individual pixel with references to neighbors
class Pixel implements IPixel {

  Color color;

  // neighbors
  // We decided to use 8 individual fields because the number of fields never changes
  // and it is easier to access and adjust them without using indices or a complicated data structure
  IPixel up;
  IPixel right;
  IPixel down;
  IPixel left;

  // energy fields
  double energy;

  // creates a pixel with pixels in other directions

  public Pixel(Color color, IPixel up, IPixel right, IPixel down, IPixel left) {
    this.up = up;
    this.right = right;
    this.down = down;
    this.left = left;

    // two pixels are only the same if they are the same object reference
    // .equals here is used for referential equality, which is what we want to check
    // since ** TO DO **
//    if (!(this.getLeft().getUp().equals(this.getUp().getLeft())
//            && this.getUp().getRight().equals(this.getRight().getUp())
//            && this.getRight().getDown().equals(this.getDown().getRight())
//            && this.getDown().getLeft().equals(this.getLeft().getDown()))) {
//      throw new IllegalArgumentException("This pixel is not well formed");
//    }

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
    double horizontalEnergy = (this.getUp().getLeft().getBrightness() + (2 * this.getLeft().getBrightness()) + this.getLeft().getBrightness())
            - (this.getUp().getRight().getBrightness() + (2 * (this.getRight().getBrightness())) + this.getDown().getRight().getBrightness());
    double verticalEnergy = (this.getUp().getLeft().getBrightness() + (2 * this.getUp().getBrightness()) + this.getUp().getRight().getBrightness())
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

}

class EdgePixel implements IPixel {

  public double getBrightness() {
    return 0;
  }

  public double getEnergy() {
    return 0;
  }

  public IPixel getUp() {
    return new EdgePixel();
  }

  public IPixel getRight() {
    return new EdgePixel();
  }

  public IPixel getDown() {
    return new EdgePixel();
  }

  public IPixel getLeft() {
    return new EdgePixel();
  }

  public void setUp(IPixel newUp) {

  }

  public void setRight(IPixel right) {

  }

  public void setDown(IPixel down) {

  }

  public void setLeft(IPixel left) {

  }
}


class ExamplesSqueezingPictures {
  SqueezingPictures sp;

  void initTestConditions() {
    this.sp = new SqueezingPictures("balloons.jpg");
    System.out.println(sp.image.getWidth() + " " + sp.image.getHeight());

  }

  void testStuff(Tester t) {
    this.initTestConditions();
  }
}