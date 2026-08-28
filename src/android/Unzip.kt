package io.globules.unzip

import org.apache.cordova.*
import org.json.JSONArray
import java.io.*
import java.util.zip.ZipInputStream

class Unzip : CordovaPlugin() {

    override fun execute(
        action: String,
        args: JSONArray,
        callbackContext: CallbackContext
    ): Boolean {

        if (action != "unpack") return false

        val src = args.optString(0)
        val dest = args.optString(1)

        val filePlugin = this.cordova.activity
            .let { this.webView.pluginManager.getPlugin("File") as CDVFile }

        val srcURL = filePlugin.resolveLocalFilesystemURI(src)
        val destURL = filePlugin.resolveLocalFilesystemURI(dest)

        if (srcURL == null || destURL == null) {
            callbackContext.error("Path resolution failed")
            return true
        }

        cordova.threadPool.execute {
            try {
                unzipFile(srcURL.path, destURL.path)
                callbackContext.success()
            } catch (e: Exception) {
                callbackContext.error(e.localizedMessage)
            }
        }

        return true
    }

    @Throws(IOException::class)
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
