package com.tumblr

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import com.tumblr.benchmark.benchmarking.Benchpress
import com.tumblr.benchmark.reporting.BenchmarkReportingBase
import com.tumblr.benchmark.reporting.ReportGenerator
import com.tumblr.benchmark.reporting.ReportMarkup._
import com.tumblr.benchmark.reporting.ReportStructure
import com.tumblr.benchmark.reporting.ReportSummaryGenerator
import magick.MagickImage
import magick.ImageInfo

trait PerformanceTestMain {
  def main(args: Array[String]) {
    println("Executing performance test on image manipulation, this code makes use of specsbench and twitter util core")
    val reportFormat = ReportStructure("image-manipulation", Seq(
      metrics("*", Seq(walltime, total, count, throughput))))
    val reportingBase = new BenchmarkReportingBase() {}
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

    if (root.listFiles(ImageFilter).length == 0) {
      System.err.println("No files found in the directory")
      System.exit(-1)
    }

    val dest = new File(args(1))
    if (!dest.isDirectory) {
      System.err.println("Specified destination is not a directory")
      System.exit(-1)
    }

    benchmark(reportingBase, root, dest)
    println((new ReportSummaryGenerator(ReportGenerator(reportFormat, reportingBase).generate())).generate())
  }

  def createNestedDirectory(destRoot: File, threadCount: Int): File = {
    val dest = new File(destRoot, "threads-" + threadCount)
    if (dest.exists()) {
      dest.listFiles().foreach(_.delete())
      dest.delete()
    }
    
    if (dest.mkdir()) {
      dest.deleteOnExit()
      dest
    }
    else
      throw new Exception("Failed to create directory")
  }
  
  def benchmark(reporter: BenchmarkReportingBase, root: File, destRoot: File) {
    val files = root listFiles ImageFilter

    // load the file listing from the directory
    val index = new AtomicInteger
    val countOfFiles = files.size

    def invokeBenchmarkAction(dest: File) {
      //val startTime = System.currentTimeMillis()
      val access = index.incrementAndGet() - 1
      if (access < files.size) {
        val file = files(access)
        doAction(dest, file)
      }
    }

    val threadCounts = Seq(2,4,8)
    val benchmark = Benchpress("conc-" + testName, reporter)

    for (threadCount <- threadCounts) {
      index.set(0)
      val destDir = createNestedDirectory(destRoot, threadCount)
      benchmark
        .iterations(countOfFiles)
        .concurrent(threadCount)
        .aggregateTiming
        .bench(invokeBenchmarkAction(destDir))
    }
  }

  def doAction(dest: File, file: File)
  def testName: String
}
