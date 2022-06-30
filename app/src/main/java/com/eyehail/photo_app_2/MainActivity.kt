package com.eyehail.photo_app_2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import com.eyehail.photo_app_2.databinding.ActivityMainBinding
import java.io.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var outputStream: OutputStream
    lateinit var context: Context
    lateinit var drawable2: Drawable
    lateinit var bitmap2: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivPhoto.setImageBitmap(createBitmap())
        drawable2 = binding.ivPhoto.drawable
        bitmap2 = drawable2.toBitmap()
        //currentImage.getDrawingCache(true)
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) binding.ivPhoto.setImageURI(result.data?.data)
        }

        binding.btnGallery.setOnClickListener {
            // before set the other image - start
            //currentImage.destroyDrawingCache()
            // before set the other image - end
            resultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }


        binding.btnSave.setOnClickListener {
            /*// Save the image in internal storage and get the uri
            val uri: Uri = saveImageToInternalStorage(currentImage.drawable)

            // Display the internal storage saved image in image view
            currentImage.setImageURI(uri)*/
            val drawable = binding.ivPhoto.drawable
            val bitmap = drawable.toBitmap()
            //val filepath = Environment.getExternalStorageDirectory()
            //var dir = File(filepath.absolutePath + "/Demo/")
            context = this
            var dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "saved_images")
            dir.mkdir()
            var file = File(dir, System.currentTimeMillis().toString() + ".jpg")

                outputStream = FileOutputStream(file)



            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            binding.ivPhoto.setImageBitmap(assetsToBitmap(binding.ivPhoto))
            drawable2 = binding.ivPhoto.drawable
            bitmap2 = drawable.toBitmap()
            Toast.makeText(applicationContext, "Image Save To Internal!!", Toast.LENGTH_SHORT).show()

            outputStream.flush()
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }


        // implementing slider - start
        bitmap2.apply {
            binding.ivPhoto.setImageBitmap(this)
            binding.slBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                //var seekBarProgress = 0
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val bright = progress.toFloat() - 250
                    //val contrast = brightness.progress.toFloat()/10F

                    binding.ivPhoto.setImageBitmap(
                        bitmap2.setBrightness(bright)
                    )

                    findViewById<TextView>(R.id.counter).text = "Brightness $bright F"
                }

                override fun onStartTrackingTouch(seekbar: SeekBar?) {
                    //Toast.makeText(this, "processing", Toast.LENGTH_SHORT).show()
                }

                override fun onStopTrackingTouch(seekbar: SeekBar?) {
                    //currentImage.setImageBitmap(changeBrightness(seekBarProgress, currentImage))
                }
            })
        }

        // implementing slider - end



    }


    // creating default bitmap - start
    fun createBitmap(): Bitmap {
        val width = 200
        val height = 100
        val pixels = IntArray(width * height)
        //get pixel array from source
        var R: Int
        var G: Int
        var B: Int
        var index: Int
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D matrix
                index = y * width + x
                // get color
                R = x % 100 + 40
                G = y % 100 + 80
                B = (x + y) % 100 + 120
                pixels[index] = Color.rgb(R, G, B)
            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }
    // creating default bitmap - end

    // extension function to change bitmap brightness - start
    fun Bitmap.setBrightness(
        brightness:Float = 0.0F,
        contrast: Float = 1F

    ):Bitmap?{
        val bitmap = copy(Bitmap.Config.ARGB_8888,true)
        val paint = Paint()
        val matrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, 1f, contrast, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val filter = ColorMatrixColorFilter(matrix)
        paint.colorFilter = filter

        Canvas(bitmap).drawBitmap(this,0f,0f,paint)
        return bitmap
    }
    // extension function to change bitmap brightness - end

    // extension function to get bitmap from ivPhoto = start
    fun Context.assetsToBitmap(fileName: ImageView): Bitmap?{
        return try {
            val drawable = fileName.drawable
            var bitmap = drawable.toBitmap()
            val workingBitmap: Bitmap = Bitmap.createBitmap(bitmap)
            var newBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
            newBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    // extension function to get bitmap from ivPhoto - end


}