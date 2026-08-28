package io.globules.unzip

import org.apache.cordova.*
import org.json.JSONArray
import java.io.*
import java.util.zip.ZipInputStream

class Unzip : CordovaPlugin() {

     override fun execute(action: String, args: JSONArray, callbackContext: CallbackContext): Boolean {
          if (action != "unpack") return false
          var src = args.optString(0)
          var dest = args.optString(1)
          if (src.startsWith("file://")) {
               src = src.removePrefix("file://")
          }
          if (dest.startsWith("file://")) {
               dest = dest.removePrefix("file://")
          }
          cordova.threadPool.execute {
               try {
                    unzipFile(src, dest)
                    callbackContext.success()
               } catch (e: Exception) {
                    callbackContext.error(e.localizedMessage)
               }
          }
          return true
     }

     private fun unzipFile(zipPath: String, destPath: String) {
          val buffer = ByteArray(4096)  
          ZipInputStream(FileInputStream(zipPath)).use { zis ->
               var entry = zis.nextEntry
               while (entry != null) {
                    val outFile = File(destPath, entry.name)       
                    if (entry.isDirectory) {
                         outFile.mkdirs()
                    } else {
                         outFile.parentFile?.mkdirs()
                         FileOutputStream(outFile).use { fos ->
                         var len: Int
                         while (zis.read(buffer).also { len = it } > 0) {
                              fos.write(buffer, 0, len)
                         }
                         }                   
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
               }
          }       
     }
}
