package com.tumblr

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import com.tumblr.benchmark.benchmarking.Benchpress
import com.tumblr.benchmark.reporting.BenchmarkReportingBase
import com.tumblr.benchmark.reporting.ReportGenerator
import com.tumblr.benchmark.reporting.ReportMarkup.count
import com.tumblr.benchmark.reporting.ReportMarkup.metrics
import com.tumblr.benchmark.reporting.ReportMarkup.throughput
import com.tumblr.benchmark.reporting.ReportMarkup.total
import com.tumblr.benchmark.reporting.ReportMarkup.walltime
import com.tumblr.benchmark.reporting.ReportStructure
import com.tumblr.benchmark.reporting.ReportSummaryGenerator

import magick.ImageInfo
import magick.MagickImage

object QualityTest extends PerformanceTestMain {
  override def doAction(dest: File, file: File) {
    val info = new ImageInfo(file.getAbsolutePath())
    val image = new MagickImage(info)
    info.setQuality(80)
    val destFile = new File(dest, file.getName())
    image.setFileName(destFile.getAbsolutePath())
    image.writeImage(info)
  }
  
  def testName = "quality-adjustment"
}
