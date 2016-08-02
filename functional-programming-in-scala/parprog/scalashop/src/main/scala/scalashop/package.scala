
import common._

package object scalashop {

  /** The value of every pixel is represented as a 32 bit integer. */
  type RGBA = Int

  /** Returns the red component. */
  def red(c: RGBA): Int = (0xff000000 & c) >>> 24

  /** Returns the green component. */
  def green(c: RGBA): Int = (0x00ff0000 & c) >>> 16

  /** Returns the blue component. */
  def blue(c: RGBA): Int = (0x0000ff00 & c) >>> 8

  /** Returns the alpha component. */
  def alpha(c: RGBA): Int = (0x000000ff & c) >>> 0

  /** Used to create an RGBA value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA = {
    (r << 24) | (g << 16) | (b << 8) | (a << 0)
  }

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int = {
    if (v < min) min
    else if (v > max) max
    else v
  }

  /** Image is a two-dimensional matrix of pixel values. */
  class Img(val width: Int, val height: Int, private val data: Array[RGBA]) {
    def this(w: Int, h: Int) = this(w, h, new Array(w * h))
    def apply(x: Int, y: Int): RGBA = data(y * width + x)
    def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c
  }

  /** Computes the blurred RGBA value of a single pixel of the input image. */
  def boxBlurKernel(src: Img, x: Int, y: Int, radius: Int): RGBA = {
    // TODO implement using while loops
    val widthMax = src.width-1
    val heightMax = src.height-1

    val rgbas = for{
      i <- clamp(x-radius, 0, widthMax) to clamp(x+radius, 0, widthMax)
      j <- clamp(y-radius, 0, heightMax) to clamp(y+radius, 0, heightMax)
      p = src(i, j)
    } yield (red(p), green(p), blue(p), alpha(p))

    val len = rgbas.size

    val (r, g, b, a) = rgbas.foldLeft((0, 0, 0, 0)){(acc, rgba) =>
      (acc._1 + rgba._1, acc._2 + rgba._2, acc._3 + rgba._3, acc._4 + rgba._4)
    }

    rgba(r/len, g/len, b/len, a/len)
  }

  def jobs(numTasks: Int, size: Int):List[(Int,Int)] = {
    val block = size/numTasks
    val boundaries =
      (0 until numTasks)
        .map(_*block)
        .toList

    boundaries.zip(boundaries.tail :+ size)
  }
}
