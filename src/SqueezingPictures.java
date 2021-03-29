import javalib.impworld.*;
import javalib.worldimages.ComputedPixelImage;
import javalib.worldimages.FromFileImage;
import tester.Tester;

// added get color for pixels
// 2 world methods for squeezing pictures
// remove prof

import java.awt.*;
import java.util.*;

// represents a pixel
interface IPixel {
  // gets the color of this pixel
  Color getColor();

  // gets the brightness of a pixel
  double getBrightness();

  // gets the energy of a pixel
  double getEnergy();

  // get pixels in 4 different directions
  // gets the pixel above
  IPixel getUp();

  // sets pixels in 4 different directions
  // EFFECT: sets this pixel's up to another pixel
  void setUp(IPixel newUp);

  // gets the pixel to the right
  IPixel getRight();

  // EFFECT: sets this pixel's right to another pixel
  void setRight(IPixel right);

  // gets the pixel below
  IPixel getDown();

  // EFFECT: sets this pixel's down to another pixel
  void setDown(IPixel down);

  // gets the pixel to the left
  IPixel getLeft();

  // EFFECT: sets this pixel's left to another pixel
  void setLeft(IPixel left);

  // checks to see if this pixel is valid
  boolean validPixel();

}

// squeezes pictures, removing blank/unimportant space
public class SqueezingPictures extends World {

  Deque<Deque<IPixel>> grid;
  ComputedPixelImage compImg;
  WorldScene ws;

  public WorldScene makeScene() {
    return this.ws;
    // return new WorldScene(1080, 720);
  }

  @Override
  public void onTick() {
    Iterator<Deque<IPixel>> griderator = this.grid.iterator();
    int rowCount = 0;
    int colCount = 0;
    while (griderator.hasNext()) {
      Iterator<IPixel> rowIter = griderator.next().iterator();
      colCount = 0;
      while (rowIter.hasNext()) {
        IPixel cur = rowIter.next();
        this.compImg.setPixel(colCount, rowCount, cur.getColor());
        colCount += 1;
      }
      rowCount += 1;
    }
    this.ws.placeImageXY(this.compImg, 500, 300);
    this.removeSeam();
  }

  public SqueezingPictures(String filename) {
    FromFileImage image = new FromFileImage(filename);
    this.grid = new ArrayDeque<Deque<IPixel>>();
    this.ws = new WorldScene(1080, 720);
    this.compImg = new ComputedPixelImage((int) image.getWidth(), (int) image.getHeight());
    // fill in the 2d deque grid
    // there is no more efficient way to fill in every pixel of the grid than to
    // go through each pixel individually
    // i is going down (columns) and j is going right (rows)
    for (int i = 0; i < image.getHeight(); i += 1) {
      Deque<IPixel> curRow = new ArrayDeque<IPixel>();
      Iterator<IPixel> prevRow = new ArrayDeque<IPixel>().iterator();

      // if there already is a row, we can set the previous row to that value
      if (!grid.isEmpty()) {
        // we converted the deque to an iterator to access rows in a clean way
        prevRow = grid.peekLast().iterator();
      }

      // for each pixel in the row, insert it into the deque
      for (int j = 0; j < image.getWidth(); j += 1) {
        IPixel curPixel;
        IPixel upperPixel = new UnknownPixel();
        if (prevRow.hasNext()) {
          upperPixel = prevRow.next();
        }
        if (i == 0 && j == 0) {
          // top left corner
          curPixel = new Pixel(image.getColorAt(j, i), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel(), new UnknownPixel());

        } else if (i == 0 && j == image.getWidth()) {
          // top right corner
          curPixel = new Pixel(image.getColorAt(j, i), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel(), curRow.peekLast());

        } else if (i == image.getHeight() && j == 0) {
          // bottom left corner
          curPixel = new Pixel(image.getColorAt(j, i), upperPixel, new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel());

        } else if (i == image.getHeight() && j == image.getWidth()) {
          // bottom right corner
          curPixel = new Pixel(image.getColorAt(j, i), upperPixel, new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        } else if (i == 0) {
          // top row
          curPixel = new Pixel(image.getColorAt(j, i), new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel(), curRow.peekLast());

        } else if (i == image.getHeight()) {
          // bottom row
          curPixel = new Pixel(image.getColorAt(j, i), upperPixel, new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        } else if (j == 0) {
          // left column
          curPixel = new Pixel(image.getColorAt(j, i), upperPixel, new UnknownPixel(),
                  new UnknownPixel(), new UnknownPixel());

        } else if (j == image.getWidth()) {
          // right column
          curPixel = new Pixel(image.getColorAt(j, i), upperPixel, new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        } else {
          // no edges
          curPixel = new Pixel(image.getColorAt(j, i), upperPixel, new UnknownPixel(),
                  new UnknownPixel(), curRow.peekLast());

        }
        curRow.addLast(curPixel);
      }
      grid.addLast(curRow);
    }
  }

  // constructor for testing
  public SqueezingPictures(Deque<Deque<IPixel>> grid) {
    this.grid = grid;
  }

  // computes the minimum seam for a grid of IPixels
  SeamInfo minEnergy() {
    Deque<Deque<SeamInfo>> seamGrid = new ArrayDeque<Deque<SeamInfo>>();
    Iterator<Deque<IPixel>> gridIterator = grid.iterator();

    seamGrid.add(this.firstRow(gridIterator.next()));
    // for each row in the grid iterator
    while (gridIterator.hasNext()) {
      // gives us the current row of pixesl
      Iterator<IPixel> curRow = gridIterator.next().iterator();
      // gives us the last row of the seam info grid
      ArrayList<SeamInfo> lastSeamInfoRow = new ArrayList<SeamInfo>(seamGrid.peekLast());

      // the current row of seam infos
      Deque<SeamInfo> seamGridRow = new ArrayDeque<SeamInfo>();

      int pixelIdx = 0;
      // for each pixel in the row, check it's upper three neighbors and update the seam so far
      // with the highest energy seaminfo
      while (curRow.hasNext()) {
        IPixel curPixel = curRow.next();
        SeamInfo minSeamInfo = lastSeamInfoRow.get(pixelIdx);
        // check a pixel's upper three neighbors and get the smallest energy seam info
        for (int i = -1; i <= 1; i += 1) {
          if (i + pixelIdx >= 0 && i + pixelIdx < lastSeamInfoRow.size()
                  && lastSeamInfoRow.get(i + pixelIdx).totalWeight < minSeamInfo.totalWeight) {
            minSeamInfo = lastSeamInfoRow.get(i + pixelIdx);
          }
        }
        SeamInfo newSeam = new SeamInfo(curPixel, curPixel.getEnergy() + minSeamInfo.totalWeight,
                minSeamInfo);
        seamGridRow.add(newSeam);
        pixelIdx += 1;

      }
      seamGrid.add(seamGridRow);
    }

    Iterator<SeamInfo> lastRowSeams = seamGrid.peekLast().iterator();
    SeamInfo minSeamInfo = new SeamInfo(new UnknownPixel(), Integer.MAX_VALUE, null);

    // finds the minimum seam info energy in the last row
    while (lastRowSeams.hasNext()) {
      SeamInfo curSeam = lastRowSeams.next();
      if (curSeam.totalWeight < minSeamInfo.totalWeight) {
        minSeamInfo = curSeam;
      }
    }
    return minSeamInfo;
  }

  // constructs the first row of a seam grid
  Deque<SeamInfo> firstRow(Deque<IPixel> row) {
    Iterator<IPixel> rowIterator = row.iterator();
    Deque<SeamInfo> rowOfSeamInfo = new ArrayDeque<>();

    // update each pixel's seaminfo in the first row
    while (rowIterator.hasNext()) {
      IPixel curPixel = rowIterator.next();
      SeamInfo currSeamInfo = new SeamInfo(curPixel, curPixel.getEnergy(), null);
      rowOfSeamInfo.add(currSeamInfo);
    }
    return rowOfSeamInfo;
  }

  // EFFECT: removes the seam from our grid
  void removeSeam() {
    SeamInfo minSeam = this.minEnergy();
    removeSeamHelp(minSeam, new ReverseIterator<Deque<IPixel>>(this.grid.iterator()));
  }

  // EFFECT: removes the minimum seam from our grid
  void removeSeamHelp(SeamInfo minSeam, Iterator<Deque<IPixel>> revRowIterator) {
    // will be null once we reach the top row of the grid
    if (minSeam != null) {
      IPixel curPixel = minSeam.currentPixel;
      curPixel.getLeft().setRight(curPixel.getRight());
      curPixel.getRight().setLeft(curPixel.getLeft());
      curPixel.getDown().setUp(curPixel.getRight());
      curPixel.getRight().setDown(curPixel.getDown());

      curPixel.getUp().setDown(curPixel.getRight());
      curPixel.getRight().setUp(curPixel.getUp());

      if ((minSeam.cameFrom != null) && minSeam.diagonalSeam()) {
        // seaminfo is diagonal
        // if (right) {
        // }
      }
      // removes this pixel from the grid
      revRowIterator.next().remove(curPixel);

      this.removeSeamHelp(minSeam.cameFrom, revRowIterator);
    }

  }

}

// a reversed iterator
class ReverseIterator<T> implements Iterator<T> {

  Stack<T> stack;

  // EFFECT: adds iterator's values to the stack field
  public ReverseIterator(Iterator<T> source) {
    this.stack = new Stack<T>();
    // add items in source iterator to the stack
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

// constructs a seam info
class SeamInfo {

  IPixel currentPixel;
  double totalWeight;
  SeamInfo cameFrom;

  public SeamInfo(IPixel currentPixel, double totalWeight, SeamInfo cameFrom) {
    this.currentPixel = currentPixel;
    this.totalWeight = totalWeight;
    this.cameFrom = cameFrom;
  }

  // is this seaminfo diagonal
  boolean diagonalSeam() {
    return this.cameFrom.currentPixel != this.currentPixel.getUp();
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

  public void setUp(IPixel newUp) {
    this.up = newUp;
  }

  // gets the ipixel to the right
  public IPixel getRight() {
    // if null return edge? otherwise return this?
    return this.right;
  }

  public void setRight(IPixel right) {
    this.right = right;
  }

  // gets the ipixel below
  public IPixel getDown() {
    return this.down;
  }

  public void setDown(IPixel down) {
    this.down = down;
  }

  // gets the ipixel to the left
  public IPixel getLeft() {
    return this.left;
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

  public Color getColor() {
    return this.color;
  }

  // equals method - because we can't use referential equality
  // getBrightness(), getEnergy(), Color

}

// this is a pixel that we don't know because it's either not in the image
// or has not been processed yet
class UnknownPixel implements IPixel {

  // gets the brightness of a pixel
  public double getBrightness() {
    return 0;
  }

  // gets the energy of the pixel
  public double getEnergy() {
    return 0;
  }

  // gets the pixel above
  public IPixel getUp() {
    return new UnknownPixel();
  }

  // sets the pixel above
  public void setUp(IPixel newUp) {

  }

  // gets the pixel to the right
  public IPixel getRight() {
    return new UnknownPixel();
  }

  // sets the pixel to the right
  public void setRight(IPixel right) {

  }

  // gets the pixel below
  public IPixel getDown() {
    return new UnknownPixel();
  }

  // sets the pixel below
  public void setDown(IPixel down) {

  }

  // gets the pixel to the left
  public IPixel getLeft() {
    return new UnknownPixel();
  }

  // sets the left pixel
  public void setLeft(IPixel left) {

  }

  // is this pixel valid?
  public boolean validPixel() {
    return true;
  }

  public Color getColor() {
    return Color.BLACK;
  }
}


class ExamplesSqueezingPictures {

  SqueezingPictures eightBy6;
  SqueezingPictures twoByThree;
  SqueezingPictures threeByThree;
  SqueezingPictures solidThree;
  SqueezingPictures balloons;
  SqueezingPictures handMadeThreeByThree;
  SqueezingPictures handMadeFourByFour;
  SqueezingPictures onePix;
  SqueezingPictures prof;

  UnknownPixel ukPixel;
  IPixel redLine1;
  IPixel redLine2;
  IPixel redLine3;
  IPixel redLine4;

  // for 3x3 grid
  IPixel topLeft;
  IPixel topMid;
  IPixel topRight;
  IPixel midLeft;
  IPixel midMid;
  IPixel midRight;
  IPixel botLeft;
  IPixel botMid;
  IPixel botRight;

  // for 3x3 solid grid
  IPixel topLeftSolid;
  IPixel topMidSolid;
  IPixel topRightSolid;
  IPixel midLeftSolid;
  IPixel midMidSolid;
  IPixel midRightSolid;
  IPixel botLeftSolid;
  IPixel botMidSolid;
  IPixel botRightSolid;

  // for one pixel edge case
  IPixel singlePixel;

  // for given grid
  IPixel zeroZero;
  IPixel zeroOne;
  IPixel zeroTwo;
  IPixel zeroThree;
  IPixel oneZero;
  IPixel oneOne;
  IPixel oneTwo;
  IPixel oneThree;
  IPixel twoZero;
  IPixel twoOne;
  IPixel twoTwo;
  IPixel twoThree;
  IPixel threeZero;
  IPixel threeOne;
  IPixel threeTwo;
  IPixel threeThree;

  IPixel greenLineNEU;
  IPixel greenLineSymphony;

  Deque<IPixel> topThreeRowSolid;
  Deque<IPixel> midThreeRowSolid;
  Deque<IPixel> botThreeRowSolid;

  Deque<IPixel> topThreeRow;
  Deque<IPixel> midThreeRow;
  Deque<IPixel> botThreeRow;

  SeamInfo si1;
  SeamInfo si2;
  SeamInfo si3;

  Deque<IPixel> givenRowOne;
  Deque<IPixel> givenRowTwo;
  Deque<IPixel> givenRowThree;
  Deque<IPixel> givenRowFour;

  Deque<Deque<IPixel>> gridSolid;
  Deque<Deque<IPixel>> grid3x3;
  Deque<Deque<IPixel>> givenGrid;

  void initTestConditions() {
    this.balloons = new SqueezingPictures("balloons.jpg");
    this.eightBy6 = new SqueezingPictures("8x6.jpeg");
    this.twoByThree = new SqueezingPictures("twoByThree.jpeg");
    this.threeByThree = new SqueezingPictures("threeByThree.jpeg");
    this.solidThree = new SqueezingPictures("solid.jpeg");
    this.onePix = new SqueezingPictures("onePix.jpeg");
    this.prof = new SqueezingPictures("IMG_0160.jpeg");

    this.ukPixel = new UnknownPixel();
    this.redLine1 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    this.redLine2 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), redLine1);
    this.redLine3 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), redLine2);
    this.redLine4 = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), redLine3);

    this.greenLineNEU = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    this.greenLineSymphony = new Pixel(Color.RED, new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());

    // 3x3 GRID
    this.topThreeRow = new ArrayDeque<IPixel>();
    this.midThreeRow = new ArrayDeque<IPixel>();
    this.botThreeRow = new ArrayDeque<IPixel>();

    // we have no knowledge of other pixels when we create the first
    // we also assume the constructor works correctly, as tested before
    this.topLeft = new Pixel(new Color(12, 240, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    topThreeRow.add(topLeft);
    // top middle
    this.topMid = new Pixel(new Color(100, 24, 89), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.topLeft);
    topThreeRow.add(topMid);
    // top right
    this.topRight = new Pixel(new Color(29, 2, 2), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.topMid);
    topThreeRow.add(topRight);
    // mid left
    this.midLeft = new Pixel(new Color(124, 38, 99), this.topLeft, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    midThreeRow.add(midLeft);
    // mid mid
    this.midMid = new Pixel(new Color(0, 0, 0), this.topMid, new UnknownPixel(),
            new UnknownPixel(), this.midLeft);
    midThreeRow.add(midMid);
    // mid right
    this.midRight = new Pixel(new Color(240, 240, 58), this.topRight, new UnknownPixel(),
            new UnknownPixel(), this.midMid);
    midThreeRow.add(midRight);
    // bot left
    this.botLeft = new Pixel(new Color(250, 0, 0), this.midLeft, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    botThreeRow.add(botLeft);
    // bot mid
    this.botMid = new Pixel(new Color(0, 0, 250), this.midMid, new UnknownPixel(),
            new UnknownPixel(), this.botLeft);
    botThreeRow.add(botMid);
    // bot right
    this.botRight = new Pixel(new Color(100, 40, 240), this.midRight, new UnknownPixel(),
            new UnknownPixel(), this.botMid);
    botThreeRow.add(botRight);

    this.grid3x3 = new ArrayDeque<Deque<IPixel>>();
    this.grid3x3.add(topThreeRow);
    this.grid3x3.add(midThreeRow);
    this.grid3x3.add(botThreeRow);

    // SOLID 3x3 GRID
    this.topThreeRowSolid = new ArrayDeque<IPixel>();
    this.midThreeRowSolid = new ArrayDeque<IPixel>();
    this.botThreeRowSolid = new ArrayDeque<IPixel>();

    // creating a solid grid from our 3x3 (12,240,58) RGB image
    this.topLeftSolid = new Pixel(new Color(12, 240, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    this.topThreeRowSolid.add(topLeftSolid);
    // top middle
    this.topMidSolid = new Pixel(new Color(12, 240, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.topLeftSolid);
    this.topThreeRowSolid.add(topMidSolid);
    // top right
    this.topRightSolid = new Pixel(new Color(12, 240, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.topMidSolid);
    this.topThreeRowSolid.add(topRightSolid);
    // mid left
    this.midLeftSolid = new Pixel(new Color(12, 240, 58), this.topLeftSolid, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    this.midThreeRowSolid.add(midLeftSolid);
    // mid mid
    this.midMidSolid = new Pixel(new Color(12, 240, 58), this.topMidSolid, new UnknownPixel(),
            new UnknownPixel(), this.midLeftSolid);
    this.midThreeRowSolid.add(midMidSolid);
    // mid right
    this.midRightSolid = new Pixel(new Color(12, 240, 58), this.topRightSolid, new UnknownPixel(),
            new UnknownPixel(), this.midMidSolid);
    this.midThreeRowSolid.add(midRightSolid);
    // bot left
    this.botLeftSolid = new Pixel(new Color(12, 240, 58), this.midLeftSolid, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    this.botThreeRowSolid.add(botLeftSolid);
    // bot mid
    this.botMidSolid = new Pixel(new Color(12, 240, 58), this.midMidSolid, new UnknownPixel(),
            new UnknownPixel(), this.botLeftSolid);
    this.botThreeRowSolid.add(botMidSolid);
    // bot right
    this.botRightSolid = new Pixel(new Color(12, 240, 58), this.midRightSolid, new UnknownPixel(),
            new UnknownPixel(), this.botMidSolid);
    this.botThreeRowSolid.add(botRightSolid);

    this.gridSolid = new ArrayDeque<Deque<IPixel>>();
    this.gridSolid.add(topThreeRowSolid);
    this.gridSolid.add(midThreeRowSolid);
    this.gridSolid.add(botThreeRowSolid);

    // 4x4 GIVEN GRID
    this.givenRowOne = new ArrayDeque<IPixel>();
    this.givenRowTwo = new ArrayDeque<IPixel>();
    this.givenRowThree = new ArrayDeque<IPixel>();
    this.givenRowFour = new ArrayDeque<IPixel>();

    // we have no knowledge of other pixels when we create the first
    // we also assume the constructor works correctly, as tested before

    // top row
    this.zeroZero = new Pixel(new Color(12, 240, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    givenRowOne.add(zeroZero);

    this.zeroOne = new Pixel(new Color(122, 20, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.zeroZero);
    givenRowOne.add(zeroOne);

    this.zeroTwo = new Pixel(new Color(12, 240, 248), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.zeroOne);
    givenRowOne.add(zeroTwo);

    this.zeroThree = new Pixel(new Color(12, 0, 58), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), this.zeroTwo);
    givenRowOne.add(zeroThree);

    // second row
    this.oneZero = new Pixel(new Color(200, 0, 0), this.zeroZero, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    givenRowTwo.add(oneZero);

    this.oneOne = new Pixel(new Color(123, 120, 120), this.zeroOne, new UnknownPixel(),
            new UnknownPixel(), this.oneZero);
    givenRowTwo.add(oneOne);

    this.oneTwo = new Pixel(new Color(3, 3, 3), this.zeroTwo, new UnknownPixel(),
            new UnknownPixel(), this.oneOne);
    givenRowTwo.add(oneTwo);

    this.oneThree = new Pixel(new Color(240, 240, 58), this.zeroThree, new UnknownPixel(),
            new UnknownPixel(), this.oneTwo);
    givenRowTwo.add(oneThree);

    // third row
    this.twoZero = new Pixel(new Color(240, 240, 240), this.oneZero, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    givenRowThree.add(twoZero);

    this.twoOne = new Pixel(new Color(5, 3, 180), this.oneOne, new UnknownPixel(),
            new UnknownPixel(), this.twoZero);
    givenRowThree.add(twoOne);

    this.twoTwo = new Pixel(new Color(12, 240, 58), this.oneTwo, new UnknownPixel(),
            new UnknownPixel(), this.twoOne);
    givenRowThree.add(twoTwo);

    this.twoThree = new Pixel(new Color(192, 33, 120), this.oneThree, new UnknownPixel(),
            new UnknownPixel(), this.twoTwo);
    givenRowThree.add(twoThree);

    // fourth row
    this.threeZero = new Pixel(new Color(180, 240, 130), this.twoZero, new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
    givenRowFour.add(threeZero);

    this.threeOne = new Pixel(new Color(28, 111, 22), this.twoOne, new UnknownPixel(),
            new UnknownPixel(), this.threeZero);
    givenRowFour.add(threeOne);

    this.threeTwo = new Pixel(new Color(200, 140, 1), this.twoTwo, new UnknownPixel(),
            new UnknownPixel(), this.threeOne);
    givenRowFour.add(threeTwo);

    this.threeThree = new Pixel(new Color(220, 130, 58), this.twoThree, new UnknownPixel(),
            new UnknownPixel(), this.threeTwo);
    givenRowFour.add(threeThree);

    this.givenGrid = new ArrayDeque<Deque<IPixel>>();
    this.givenGrid.add(givenRowOne);
    this.givenGrid.add(givenRowTwo);
    this.givenGrid.add(givenRowThree);
    this.givenGrid.add(givenRowFour);

    this.handMadeThreeByThree = new SqueezingPictures(grid3x3);
    this.handMadeFourByFour = new SqueezingPictures(givenGrid);

    this.si1 = new SeamInfo(this.topLeft, this.topLeft.getEnergy(), null);
    this.si2 = new SeamInfo(this.topMid, this.topMid.getEnergy(), null);
    this.si3 = new SeamInfo(this.topRight, this.topRight.getEnergy(), null);

    this.singlePixel = new Pixel(new Color(240, 14, 240), new UnknownPixel(), new UnknownPixel(),
            new UnknownPixel(), new UnknownPixel());
  }

  // tests if we are creating well-structured grids
  void testPixelConstructor(Tester t) {
    this.initTestConditions();

    // check a few key pixels and edge cases

    t.checkExpect(this.topLeft.getUp(), new UnknownPixel());
    t.checkExpect(this.topLeft.getLeft(), new UnknownPixel());
    t.checkExpect(this.topLeft.getRight(), this.topMid);
    t.checkExpect(this.topLeft.getRight().getDown(), this.midMid);
    t.checkExpect(this.topLeft.getDown(), this.midLeft);

    t.checkExpect(this.topMid.getUp(), new UnknownPixel());
    t.checkExpect(this.topMid.getRight(), this.topRight);
    t.checkExpect(this.topMid.getRight().getDown(), this.midRight);
    t.checkExpect(this.topMid.getDown(), this.midMid);
    t.checkExpect(this.topMid.getLeft(), this.topLeft);
    t.checkExpect(this.topMid.getLeft().getDown(), this.midLeft);

    // include referencing from different directions produces same result
    t.checkExpect(this.midMid.getUp(), this.topMid);
    t.checkExpect(this.midMid.getUp().getLeft(), this.topLeft);
    t.checkExpect(this.midMid.getUp().getRight(), this.topRight);
    t.checkExpect(this.midMid.getRight(), this.midRight);
    t.checkExpect(this.midMid.getRight().getDown(), this.botRight);
    t.checkExpect(this.midMid.getDown().getRight(), this.botRight);
    t.checkExpect(this.midMid.getDown(), this.botMid);
    t.checkExpect(this.midMid.getDown().getLeft(), this.botLeft);
    t.checkExpect(this.midMid.getLeft(), this.midLeft);
    t.checkExpect(this.midMid.getLeft().getUp(), this.topLeft);

    t.checkExpect(this.botRight.getRight(), new UnknownPixel());
    t.checkExpect(this.botRight.getDown(), new UnknownPixel());
    t.checkExpect(this.botRight.getUp(), this.midRight);
    t.checkExpect(this.botRight.getUp().getLeft(), this.midMid);
    t.checkExpect(this.botRight.getLeft(), this.botMid);

    t.checkExpect(this.twoTwo.getLeft(), this.twoOne);
    t.checkExpect(this.twoTwo.getUp(), this.oneTwo);
    t.checkExpect(this.twoTwo.getRight(), this.twoThree);
  }

  // we choose to test all getters at once because they all simply return a field's value
  void testGetters(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.redLine1.getUp(), this.ukPixel);
    t.checkExpect(this.redLine2.getUp(), this.ukPixel);
    t.checkExpect(this.redLine3.getUp(), this.ukPixel);
    t.checkExpect(this.redLine4.getUp(), this.ukPixel);

    t.checkExpect(this.redLine1.getRight(), this.redLine2);
    t.checkExpect(this.redLine2.getRight(), this.redLine3);
    t.checkExpect(this.redLine3.getRight(), this.redLine4);
    t.checkExpect(this.redLine4.getRight(), this.ukPixel);

    t.checkExpect(this.redLine1.getDown(), this.ukPixel);
    t.checkExpect(this.redLine2.getDown(), this.ukPixel);
    t.checkExpect(this.redLine3.getDown(), this.ukPixel);
    t.checkExpect(this.redLine4.getDown(), this.ukPixel);

    t.checkExpect(this.redLine1.getLeft(), this.ukPixel);
    t.checkExpect(this.redLine2.getLeft(), this.redLine1);
    t.checkExpect(this.redLine3.getLeft(), this.redLine2);
    t.checkExpect(this.redLine4.getLeft(), this.redLine3);
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

    // ensure we're meeting basic size properties of rows
    t.checkExpect(this.eightBy6.grid.peekLast().size(), 8);
    t.checkExpect(this.twoByThree.grid.peekLast().size(), 3);
    t.checkExpect(this.threeByThree.grid.peekLast().size(), 3);
    t.checkExpect(this.onePix.grid.peekLast().size(), 1);

    // image of three by three solid
    t.checkExpect(this.solidThree.grid, this.gridSolid);

    // image of one pixel edge case
    ArrayDeque<Deque<IPixel>> onePix = new ArrayDeque<Deque<IPixel>>();
    ArrayDeque<IPixel> oneRow = new ArrayDeque<IPixel>();
    oneRow.add(this.singlePixel);
    onePix.add(oneRow);

    t.checkExpect(this.onePix.grid, onePix);
  }

  // tests if we can calculate brightness
  void testBrightness(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.ukPixel.getBrightness(), 0.0);
    t.checkExpect(this.redLine1.getBrightness(), 1.0 / 3.0);
    t.checkExpect(this.redLine2.getBrightness(), 1.0 / 3.0);
    t.checkExpect(this.redLine3.getBrightness(), 1.0 / 3.0);
    t.checkExpect(this.redLine4.getBrightness(), 1.0 / 3.0);
    t.checkExpect(this.greenLineNEU.getBrightness(), 1.0 / 3.0);
    t.checkExpect(this.eightBy6.grid.peekLast().peekLast().getBrightness(), 0.4117647058823529);
    t.checkExpect(this.eightBy6.grid.peekFirst().peekLast().getBrightness(), 0.396078431372549);
    t.checkExpect(this.onePix.grid.peekLast().peekLast().getBrightness(), 0.6431372549019608);
  }

  // tests if we can calculate energy
  void testEnergy(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.ukPixel.getEnergy(), 0.0);
    t.checkExpect(this.redLine1.getEnergy(), 2.0 / 3.0);
    t.checkExpect(this.redLine2.getEnergy(), 0.33333333333333337);
    t.checkExpect(this.redLine3.getEnergy(), 0.33333333333333337);
    t.checkExpect(this.redLine4.getEnergy(), 1.0);
    t.checkExpect(this.greenLineNEU.getEnergy(), 0.0);
    t.checkExpect(this.onePix.grid.peekLast().peekLast().getEnergy(), 0.0);

  }

  // tests getting the min energy
  void testMinEnergy(Tester t) {
    this.initTestConditions();

    // I used these to find out energies of each value
    Deque<SeamInfo> topR = this.handMadeThreeByThree.firstRow(this.topThreeRow);
    Deque<SeamInfo> midR = this.handMadeThreeByThree.firstRow(this.midThreeRow);
    Deque<SeamInfo> botR = this.handMadeThreeByThree.firstRow(this.botThreeRow);

    SeamInfo firstVal = this.si1;
    SeamInfo secondVal = new SeamInfo(this.midLeft,
            this.midLeft.getEnergy() + firstVal.totalWeight, firstVal);
    SeamInfo thirdVal = new SeamInfo(this.botLeft,
            this.botLeft.getEnergy() + secondVal.totalWeight, secondVal);

    t.checkExpect(this.handMadeThreeByThree.minEnergy(), thirdVal);

    // I used these to find out energies of each value
    Deque<SeamInfo> zeroR = this.handMadeFourByFour.firstRow(this.givenRowOne);
    Deque<SeamInfo> oneR = this.handMadeFourByFour.firstRow(this.givenRowTwo);
    Deque<SeamInfo> twoR = this.handMadeFourByFour.firstRow(this.givenRowThree);
    Deque<SeamInfo> threeR = this.handMadeFourByFour.firstRow(this.givenRowFour);

    firstVal = new SeamInfo(this.zeroTwo, this.zeroTwo.getEnergy(), null);
    secondVal = new SeamInfo(this.oneOne, this.oneOne.getEnergy() + firstVal.totalWeight, firstVal);
    thirdVal = new SeamInfo(this.twoTwo, this.twoTwo.getEnergy() + secondVal.totalWeight,
            secondVal);
    SeamInfo fourthVal = new SeamInfo(this.threeTwo,
            this.threeTwo.getEnergy() + thirdVal.totalWeight, thirdVal);

    t.checkExpect(this.handMadeFourByFour.minEnergy(), fourthVal);

    t.checkExpect(this.onePix.minEnergy(), new SeamInfo(this.singlePixel, 0.0, null));

  }

  // test to get the first row of the seam grid
  void testFirstRow(Tester t) {
    this.initTestConditions();

    Deque<SeamInfo> result3by3 = new ArrayDeque<>();

    result3by3.add(this.si1);
    result3by3.add(this.si2);
    result3by3.add(this.si3);

    Deque<SeamInfo> result4by4 = new ArrayDeque<>();
    SeamInfo si4 = new SeamInfo(this.zeroZero, this.zeroZero.getEnergy(), null);
    SeamInfo si5 = new SeamInfo(this.zeroOne, this.zeroOne.getEnergy(), null);
    SeamInfo si6 = new SeamInfo(this.zeroTwo, this.zeroTwo.getEnergy(), null);
    SeamInfo si7 = new SeamInfo(this.zeroThree, this.zeroThree.getEnergy(), null);

    result4by4.add(si4);
    result4by4.add(si5);
    result4by4.add(si6);
    result4by4.add(si7);

    ArrayDeque<IPixel> oneRow = new ArrayDeque<IPixel>();
    oneRow.add(this.singlePixel);
    Deque<SeamInfo> onePixSeam = new ArrayDeque<>();
    onePixSeam.add(new SeamInfo(this.singlePixel, this.singlePixel.getEnergy(), null));

    t.checkExpect(this.handMadeThreeByThree.firstRow(this.topThreeRow), result3by3);
    t.checkExpect(this.handMadeFourByFour.firstRow(this.givenRowOne), result4by4);
    t.checkExpect(this.onePix.firstRow(oneRow), onePixSeam);
  }

  // tests removimg a seam
  void testRemoveSeam(Tester t) {
    this.initTestConditions();
    t.checkExpect(this.handMadeThreeByThree.grid.peekLast().size(), 3);
    this.handMadeThreeByThree.removeSeam();
    t.checkExpect(this.handMadeThreeByThree.grid.peekLast().size(), 2);

    Iterator<Deque<IPixel>> griderator = this.handMadeThreeByThree.grid.iterator();
    boolean wellFormed = true;
    while (griderator.hasNext()) {
      Iterator<IPixel> rowIter = griderator.next().iterator();
      while (rowIter.hasNext()) {
        IPixel cur = rowIter.next();
        if (!(cur.getLeft() instanceof UnknownPixel)) {
          wellFormed = (cur.getLeft().getRight() == cur);
        }
        if (!(cur.getUp() instanceof UnknownPixel)) {
          wellFormed = (cur.getUp().getDown() == cur);
        }
        if (!(cur.getRight() instanceof UnknownPixel)) {
          wellFormed = (cur.getRight().getLeft() == cur);
        }
        if (!(cur.getDown() instanceof UnknownPixel)) {
          wellFormed = (cur.getDown().getUp() == cur);
        }
      }
    }
    t.checkExpect(wellFormed, true);
  }

  void testRun(Tester t) {
    this.initTestConditions();
    this.prof.bigBang(1080, 720, 0.2);
  }
}