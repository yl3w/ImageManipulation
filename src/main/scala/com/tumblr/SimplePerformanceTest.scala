package com.tumblr

import java.io.File
import java.io.FilenameFilter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import magick.ImageInfo
import magick.MagickImage

object SimplePerformanceTest {
  object jpegFilter extends FilenameFilter {

    override def accept(dir: File, name: String): Boolean = {
      name.lastIndexOf("jpg") == name.length - "jpg".length ||
        name.lastIndexOf("JPG") == name.length - "JPG".length ||
        name.lastIndexOf("jpeg") == name.length - "jpeg".length ||
        name.lastIndexOf("JPEG") == name.length - "JPEG".length
    }
  }

  def main(args: Array[String]) {
    println("Executing performance test on image manipulation, this code makes use of specsbench and twitter util core")
    if (args.length < 1) {
      System.err.println("Directory with images must be specified")
      System.exit(-1)
    }

    if (args.length < 2) {
      System.err.println("Destination directory must be specified")
      System.exit(-1)
    }

    val root = new File(args(0))
    if (!root.isDirectory) {
      System.err.println("Specified file is not a directory")
      System.exit(-1)
    }

    if (root.listFiles(jpegFilter).length == 0) {
      System.err.println("No files found in the directory")
      System.exit(-1)
    }

    val dest = new File(args(1))
    if (!dest.isDirectory) {
      System.err.println("Specified destination is not a directory")
      System.exit(-1)
    }

    benchmark(root, dest)
  }

  def createNestedDirectory(destRoot: File, threadCount: Int): File = {
    val dest = new File(destRoot, "threads-" + threadCount)
    if (dest.exists()) {
      dest.delete()
    }
    if (dest.mkdir()) {
      dest.deleteOnExit()
      dest
    }
    else
      throw new Exception("Failed to create directory")
  }

  private def benchmark(root: File, destRoot: File) {
    val files = root listFiles jpegFilter

    // load the file listing from the directory
    val index = new AtomicInteger
    val countOfFiles = files.size

    def qualityAdjustment(dest: File) {
      var access = index.incrementAndGet() - 1
      while (access < files.size) {
        val file = files(access)
        val info = new ImageInfo(file.getAbsolutePath())
        val image = new MagickImage(info)
        image.setQuality(80)
        val destFile = new File(dest, file.getName())
        image.setFileName(destFile.getAbsolutePath())
        image.writeImage(info)
        access = index.incrementAndGet() - 1
      }
    }

    val destDir = createNestedDirectory(destRoot, 4)
    val executorService = Executors.newFixedThreadPool(4)
    val startTime = System.currentTimeMillis()
    for (val i <- 1 to 4) {
      executorService.submit(new Runnable() {
        override def run() {
          qualityAdjustment(destDir)
        }
      })
    }

    var access = index.get()
    while (access < files.size) {
      Thread.sleep(1000)
      access = index.get()
    }
    val endTime = System.currentTimeMillis()
    println("File count " + countOfFiles + " Time taken " + (endTime - startTime) + " Time to process each file " + (endTime - startTime) / countOfFiles)
    executorService.shutdown()

  }
}
