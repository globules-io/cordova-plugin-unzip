package io.globules.unzip

import android.util.Log
import org.apache.cordova.*
import org.json.JSONArray
import java.io.*
import java.util.zip.ZipInputStream

class Unzip : CordovaPlugin() {

    override fun execute(action: String, args: JSONArray, callbackContext: CallbackContext): Boolean {
        if (action != "unpack") return false

        var src = args.optString(0)
        var dest = args.optString(1)

        Log.d("UNZIP", "RAW SRC = $src")
        Log.d("UNZIP", "RAW DEST = $dest")

        val filePrefixRegex = Regex("^file:/+")
        src = src.replace(filePrefixRegex, "")
        dest = dest.replace(filePrefixRegex, "")

        Log.d("UNZIP", "CLEAN SRC = $src")
        Log.d("UNZIP", "CLEAN DEST = $dest")
        Log.d("UNZIP", "SRC EXISTS = ${File(src).exists()}")
        Log.d("UNZIP", "DEST EXISTS = ${File(dest).exists()}")

        cordova.threadPool.execute {
            try {
                unzipFile(src, dest)
                // After extraction, list everything in dest
                val destDir = File(dest)
                Log.d("UNZIP", "POST-EXTRACT DEST EXISTS = ${destDir.exists()}")
                destDir.listFiles()?.forEach {
                    Log.d("UNZIP", "POST-EXTRACT FILE = ${it.absolutePath}")
                } ?: Log.d("UNZIP", "POST-EXTRACT: NO FILES FOUND")
               callbackContext.success()
            } catch (e: Exception) {
               Log.e("UNZIP", "ERROR = ${e.localizedMessage}", e)
               callbackContext.error(e.localizedMessage)
            }
        }
        return true
    }

    private fun unzipFile(zipPath: String, destPath: String) {
          val zipFile = File(zipPath)
          Log.d("UNZIP", "ZIP SIZE = ${zipFile.length()}")
          val buffer = ByteArray(4096)
          Log.d("UNZIP", "OPENING ZIP = $zipPath")          

          ZipInputStream(FileInputStream(zipPath)).use { zis ->
               var entry = zis.nextEntry

               while (entry != null) {
                    Log.d("UNZIP", "ENTRY = ${entry.name}")
                    val outFile = File(destPath, entry.name)
                    Log.d("UNZIP", "OUTFILE = ${outFile.absolutePath}")
                    Log.d("UNZIP", "OUTFILE PARENT = ${outFile.parentFile?.absolutePath}")
                    if (entry.isDirectory) {
                         outFile.mkdirs()
                         Log.d("UNZIP", "MKDIRS = ${outFile.absolutePath}")
                    } else {
                         outFile.parentFile?.mkdirs()
                         FileOutputStream(outFile).use { fos ->
                         var len: Int
                         while (zis.read(buffer).also { len = it } > 0) {
                              fos.write(buffer, 0, len)
                         }
                         }
                         Log.d("UNZIP", "WROTE FILE = ${outFile.absolutePath}")
                         Log.d("UNZIP", "FILE EXISTS AFTER WRITE = ${outFile.exists()}")
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
               }
          }
          Log.d("UNZIP", "EXTRACTION COMPLETE")
     }
}
