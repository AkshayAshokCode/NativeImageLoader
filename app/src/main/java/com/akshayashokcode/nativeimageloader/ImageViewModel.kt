package com.akshayashokcode.nativeimageloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.URL
import java.net.UnknownHostException

class ImageViewModel : ViewModel() {

   private val imageListUrl = "https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100"
    private val _imageUrls = MutableLiveData<List<String>>()
    val imageUrls: LiveData<List<String>> get() = _imageUrls

    fun fetchImages() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { URL(imageListUrl).readText() }
                val jsonArray = JSONArray(response)
                val urls = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val thumbnail = jsonObject.getJSONObject("thumbnail")
                    val domain = thumbnail.getString("domain")
                    val basePath = thumbnail.getString("basePath")
                    val key = thumbnail.getString("key")
                    val imageUrl = "$domain/$basePath/0/$key"
                    urls.add(imageUrl)
                }
                _imageUrls.value = urls
            } catch (e: UnknownHostException) {
                println("UnknownHostException: Unable to resolve host: ${e.message}")
                _imageUrls.value = emptyList()
            } catch (e: IOException) {
                println("IOException: Network error: ${e.message}")
                _imageUrls.value = emptyList()
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                _imageUrls.value = emptyList()
            }
        }
    }
}
