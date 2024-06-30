package com.akshayashokcode.nativeimageloader

import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference


class ImageAdapter(private val imageUrls: List<String>, private val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
      //  var imageLoadJob: Job? = null
      var imageLoadTask: ImageLoadTask? = null
        fun loadImage(imageUrl: String) {
            imageLoadTask?.cancel(true)
            imageLoadTask = ImageLoadTask(imageView)
            imageLoadTask?.execute(imageUrl)
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.imageView.tag = imageUrl
        //holder.imageLoadJob?.cancel()
        holder.loadImage(imageUrl)

        // Keeping these code just to show my initial Approach

//        holder.imageLoadJob = CoroutineScope(Dispatchers.IO).launch {
//            val bitmap = ImageLoader.getBitmap(imageUrl)
//            // To make sure the images don't get set over another view/position due to asynchronous nature of coroutine
//            withContext(Dispatchers.Main){
//                if(bitmap!=null) {
//                    if (holder.imageView.tag == imageUrl) {
//                        holder.imageView.setImageBitmap(bitmap)
//                    }
//                }else{
//                    holder.imageView.setImageResource(R.drawable.placeholder)
//                }
//            }
//        }
    }


    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.imageLoadTask?.cancel(true)
       // holder.imageLoadJob?.cancel()
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }


    class ImageLoadTask(imageView: ImageView) : AsyncTask<String, Void, ImageLoadResult?>() {
        private val imageViewReference = WeakReference(imageView)

        override fun doInBackground(vararg params: String): ImageLoadResult? {
            val imageUrl = params[0]
            var bitmap: Bitmap? = null
            try {
                bitmap= ImageLoader.getBitmap(imageUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ImageLoadResult(imageUrl, bitmap)
        }

        override fun onPostExecute(result: ImageLoadResult?) {
            if (isCancelled) {
                return
            }

            val imageView = imageViewReference.get()

            if (imageView != null && result?.bitmap != null) {
                if (imageView.tag == result.imageUrl) {
                imageView.setImageBitmap(result.bitmap)
            }else{
                    imageView.setImageResource(R.drawable.placeholder)
                }
            }else{
                imageView?.setImageResource(R.drawable.placeholder)
            }
        }
    }
    data class ImageLoadResult(val imageUrl: String, val bitmap: Bitmap?)


}
