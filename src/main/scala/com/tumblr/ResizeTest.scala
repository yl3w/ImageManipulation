package com.tumblr

import java.io.File
import magick.MagickImage
import magick.ImageInfo

object ResizeTest extends PerformanceTestMain {
  override def doAction(dest: File, file: File) {
    val info = new ImageInfo(file.getAbsolutePath())
    val image = new MagickImage(info)
    val dimensions = image.getDimension()
    val scaledImage = image.scaleImage(dimensions.width / 2, dimensions.height / 2)
    val destFile = new File(dest, file.getName())
    scaledImage.setFileName(destFile.getAbsolutePath())
    scaledImage.writeImage(info)
  }
  
  def testName = "resize"
}