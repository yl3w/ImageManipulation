package com.tumblr

import java.io.File
import java.io.FilenameFilter

object ImageFilter extends FilenameFilter {

  override def accept(dir: File, name: String): Boolean = {
    name.lastIndexOf("jpg") == name.length - "jpg".length ||
      name.lastIndexOf("JPG") == name.length - "JPG".length ||
      name.lastIndexOf("jpeg") == name.length - "jpeg".length ||
      name.lastIndexOf("JPEG") == name.length - "JPEG".length
  }
}