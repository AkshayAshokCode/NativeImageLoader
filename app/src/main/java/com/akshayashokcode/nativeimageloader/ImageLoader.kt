package com.akshayashokcode.nativeimageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ImageLoader {

    private val memoryCache: LruCache<String, Bitmap>
    private lateinit var diskCacheDir: File

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = LruCache(cacheSize)
    }

    fun initialize(context: Context) {
        diskCacheDir = File(context.cacheDir, "images")
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs()
        }
    }

     fun getBitmap(url: String):Bitmap? {
        return memoryCache.get(url) ?: loadFromDiskOrNetwork(url)
    }

    private fun loadFromDiskOrNetwork(url: String) : Bitmap? {
        val diskFile = File(diskCacheDir, url.hashCode().toString())
        if (diskFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(diskFile.absolutePath)
            memoryCache.put(url, bitmap)
            return bitmap
        } else {

            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                if (bitmap != null) {
                    memoryCache.put(url, bitmap)
                    saveBitmapToDisk(bitmap, diskFile)
                }
                bitmap
            } catch (e: Exception) {
                println("Job: I'm cancelled!")
                null
            }
        }
    }

    private fun saveBitmapToDisk(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }

}


