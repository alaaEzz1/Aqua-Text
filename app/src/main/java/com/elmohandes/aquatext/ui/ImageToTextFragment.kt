package com.elmohandes.aquatext.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.elmohandes.aquatext.R
import com.elmohandes.aquatext.databinding.FragmentImageToTextBinding
import com.elmohandes.aquatext.utils.CustomProgressDialog
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class ImageToTextFragment : Fragment() {

    private lateinit var binding: FragmentImageToTextBinding
    private var selectedImageUriFromUpload:Uri? = null
    private lateinit var customProgressDialog: CustomProgressDialog

    companion object{
        const val PERMISSION_REQUEST_CODE = 1
        const val PICK_IMAGE_REQUEST_CODE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_to_text, container, false)
        binding = FragmentImageToTextBinding.bind(view)

        // Set the custom Toolbar as the ActionBar
        (activity as AppCompatActivity).supportActionBar!!.title = "Image To Text Conversion"

        customProgressDialog = CustomProgressDialog(requireActivity())

        binding.imageToTextUploadImg.setOnClickListener {
            if (checkPermission()){
                openImagePicker()
            }else{
                requestPermission()
                Toast.makeText(requireContext(), "you should agree permissions first",
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageToTextClearBtn.setOnClickListener { clearImageAndText() }

        binding.imageToTextCopyBtn.setOnClickListener { copyText() }

        binding.imageToTextConvertBtn.setOnClickListener {
            extractTextFromImage(selectedImageUriFromUpload)
        }

        return view
    }

    private fun extractTextFromImage(uri: Uri?){
        customProgressDialog.startLoading()
        if (uri != null){
            try {
                val image = InputImage.fromFilePath(requireContext(),uri)
                val options = TextRecognizerOptions.Builder()
                    .build()
                val recognizer = TextRecognition.getClient(options)
                val result: Task<Text> = recognizer.process(image)

                result.addOnSuccessListener {
                    val extractedText = it.text
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Handler.createAsync(Looper.myLooper()!!).postDelayed({
                            customProgressDialog.dismissDialog()
                        }, 1000)

                    }else{
                        customProgressDialog.dismissDialog()
                    }
                    binding.imageToTextExtractedTxt.text = extractedText
                }.addOnFailureListener {
                    customProgressDialog.dismissDialog()
                    binding.imageToTextExtractedTxt.text = "Failed ${it.message.toString()}"
                }
            }catch (ex: Exception){
                customProgressDialog.dismissDialog()
                Log.e("extractor-error",ex.message.toString())
            }
        }else{
            customProgressDialog.dismissDialog()
        }
    }

    private fun copyText(){
        val text = binding.imageToTextExtractedTxt.text.toString()
        if (text.isNotEmpty()){
            customProgressDialog.startLoading()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Handler.createAsync(Looper.myLooper()!!).postDelayed({
                    customProgressDialog.dismissDialog()
                }, 1000)

            }else{
                customProgressDialog.dismissDialog()
            }
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as
                    ClipboardManager
            val clip = ClipData.newPlainText("Extracted Text",text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(),"Copied Successfully",Toast.LENGTH_SHORT).show()

        }
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE
            )
        }else{
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun openImagePicker(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    val imagePickerLauncher = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null){
            val selectedImageUri = it.data?.data
            selectedImageUriFromUpload = selectedImageUri
            loadImage(selectedImageUri)
        }
    }

    private fun loadImage(uri: Uri?){
        customProgressDialog.startLoading()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.myLooper()!!).postDelayed({
                customProgressDialog.dismissDialog()
            }, 1000)

        }else{
            customProgressDialog.dismissDialog()
        }
        val bitmap = BitmapFactory.decodeStream(requireActivity()
            .contentResolver.openInputStream(uri!!))
        binding.imageToTextUploadImg.setImageBitmap(bitmap)
    }

    private fun clearImageAndText() {
        customProgressDialog.startLoading()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.myLooper()!!).postDelayed({
                customProgressDialog.dismissDialog()
            }, 1000)

        }else{
            customProgressDialog.dismissDialog()
        }
        binding.imageToTextUploadImg.setImageResource(R.drawable.ic_image_upload)
        binding.imageToTextExtractedTxt.text = ""
    }
}