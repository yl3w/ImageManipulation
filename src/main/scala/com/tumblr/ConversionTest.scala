package com.tumblr

import java.io.File

import magick.ImageInfo
import magick.MagickImage

object ConversionTest extends PerformanceTestMain {
  override def doAction(dest: File, file: File) {
    val info = new ImageInfo(file.getAbsolutePath())
    val image = new MagickImage(info)
    val fileName = createPngFileName(file)
    val destFile = new File(dest, fileName)
    image.setFileName(destFile.getAbsolutePath())
    image.writeImage(info)

  }

  private def createPngFileName(file: File): String = {
    val name = file.getName()
    val index = name.lastIndexOf('.')
    name.substring(0, index + 1) + "PNG"
  }
  
  def testName = "conversion"
}