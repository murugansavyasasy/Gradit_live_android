package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.MyProfileEdit


import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
import com.vsca.vsnapvoicecollege.Model.AddEditProfileRequest
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditbasicdetailsBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditBasicDetails : AppCompatActivity() {

    private lateinit var viewModel: App
    private lateinit var binding: LayoutEditbasicdetailsBinding

    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_PICK_IMAGE = 102

    private var photoURI: Uri? = null
    private var profileImagePath: String? = null
    private var popupWindow: PopupWindow? = null
    var Awsuploadedfile = java.util.ArrayList<String>()

    private var memberId: Int = 0
    private var isUserImage: String? = null
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null

    private var originalName: String = ""
    private var originalPhone: String = ""
    private var originalEmail: String = ""
    private var originalPlacementStatus: String = ""
    private var originalNotificationStatus: Boolean = false
    private var isImageDeleted: Boolean = false

    var fileNameDateTime: String? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var fileName: File? = null
    var filename: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditbasicdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.commonBottomResumeBuilder.btnDefault2.text = getString(R.string.update)
        viewModel = ViewModelProvider(this)[App::class.java]
        viewModel.init()
        binding.commonBottomResumeBuilder.imgDefault.visibility = View.GONE
        binding.commonBottomResumeBuilder.btnDefault1.setOnClickListener {
            checkForChangesBeforeExit()
            resetImageToOriginal()
            finish()
        }

        binding.imgback.setOnClickListener {
            checkForChangesBeforeExit()
            resetImageToOriginal()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            val currentName = binding.edtName.text.toString().trim()
            val currentPhone = binding.edtPhone.text.toString().trim()
            val currentEmail = binding.edtEmail.text.toString().trim()
            val currentPlacementStatus = binding.edtPlacementStatus.text.toString().trim()
            val currentNotificationStatus = binding.customSwitch.isChecked()

            val isChanged = currentName != originalName ||
                    currentPhone != originalPhone ||
                    currentEmail != originalEmail ||
                    currentPlacementStatus != originalPlacementStatus ||
                    currentNotificationStatus != originalNotificationStatus ||
                    CommonUtil.SelcetedFileList.isNotEmpty() ||
                    isImageDeleted
//            // Validation
//            if (currentName.isEmpty()) {
//                binding.edtName.error = "Enter your name"
//                binding.edtName.requestFocus()
//                return@setOnClickListener
//            }
//
//            if (currentPhone.length != 10 || !currentPhone.matches(Regex("\\d{10}"))) {
//                binding.edtPhone.error = "Enter a valid 10-digit phone number"
//                binding.edtPhone.requestFocus()
//                return@setOnClickListener
//            }
//
//            if (currentEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
//                binding.edtEmail.error = "Enter a valid email address"
//                binding.edtEmail.requestFocus()
//                return@setOnClickListener
//            }

            if (isChanged) {
                showConfirmationDialog()
            } else {
                Toast.makeText(this, "No changes found, saving anyway...", Toast.LENGTH_SHORT).show()
                isSaveTheData()
            }
        }

        binding.rlaProfileImage.setOnClickListener {
            showChooseImageDialog()
        }

        binding.imgDeleteProfile.setOnClickListener {
            showDeleteImageConfirmationDialog()
        }

        val name = intent.getStringExtra("name") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val isPlacement = intent.getStringExtra("placementStatus") ?: ""
        val isNotificationStatus = intent.getBooleanExtra("notificationStatus", false)
        Log.d("DEBUG", "Got notificationStatus: $isNotificationStatus")
        isUserImage = intent.getStringExtra("image") ?: ""
        memberId = intent.getIntExtra("memberId", 0)
        val placementStatus = intent.getStringExtra("placementStatus")
        binding.edtPlacementStatus.setText(placementStatus)
        Log.d("MemberId2", memberId.toString())

        originalName = name
        originalPhone = phone
        originalEmail = email
        originalPlacementStatus = isPlacement
        originalNotificationStatus = isNotificationStatus

        binding.edtName.setText(name)
        binding.edtPhone.setText(phone)
        binding.edtEmail.setText(email)
        isAwsUploadingPreSigned = AwsUploadingPreSigned()
        Glide.with(this)
            .load(isUserImage)
            .placeholder(R.drawable.default_profile)
            .error(R.drawable.default_profile)
            .into(binding.imgProfile)

        viewModel.addEditProfileLiveData.observe(this) { response ->
            Log.d("addEditProfile", "API Response: $response")
            Awsuploadedfile.clear()
            Log.d(
                "Cleanup",
                "Clearing Awsuploadedfile and SelcetedFileList after successful API call"
            )
            CommonUtil.SelcetedFileList.clear()
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.customSwitch.setChecked(isNotificationStatus)
    }

    private fun showDeleteImageConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Photo")
        builder.setMessage("Are you sure you want to delete your profile photo?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            clearProfileImage()
            isImageDeleted = true
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteProfileImage() {
        profileImagePath = null
        isUserImage = ""
        CommonUtil.SelcetedFileList.clear()
        Glide.with(this)
            .load(R.drawable.default_profile)
            .into(binding.imgProfile)
        Log.d("ProfileImage", "Profile photo deleted")
    }

    private fun clearProfileImage() {
        profileImagePath = null
        Awsuploadedfile.clear()
        CommonUtil.SelcetedFileList.clear()
        isUserImage = ""

        Glide.with(this)
            .load(R.drawable.default_profile)
            .into(binding.imgProfile)
        Log.d("ProfileImage", "Profile image cleared by user")
    }

    private fun checkForChangesBeforeSave() {
        val currentName = binding.edtName.text.toString().trim()
        val currentPhone = binding.edtPhone.text.toString().trim()
        val currentEmail = binding.edtEmail.text.toString().trim()
        val currentPlacementStatus = binding.edtPlacementStatus.text.toString().trim()
        val currentNotificationStatus = binding.customSwitch.isChecked()
        val isSame = currentName == originalName &&
                currentPhone == originalPhone &&
                currentEmail == originalEmail &&
                currentPlacementStatus == originalPlacementStatus &&
                currentNotificationStatus == originalNotificationStatus &&
                CommonUtil.SelcetedFileList.isEmpty() &&
                !isImageDeleted
        if (isSame) {
            showExitConfirmationDialog()
        } else {
            showConfirmationDialog()
        }
    }

    private fun checkForChangesBeforeExit() {
        val currentName = binding.edtName.text.toString().trim()
        val currentPhone = binding.edtPhone.text.toString().trim()
        val currentEmail = binding.edtEmail.text.toString().trim()
        val currentPlacementStatus = binding.edtPlacementStatus.text.toString().trim()
        val currentNotificationStatus = binding.customSwitch.isChecked()
        val isSame = currentName == originalName &&
                currentPhone == originalPhone &&
                currentEmail == originalEmail &&
                currentPlacementStatus == originalPlacementStatus &&
                currentNotificationStatus == originalNotificationStatus &&
                CommonUtil.SelcetedFileList.isEmpty() &&
                !isImageDeleted
        if (isSame) {
            showExitConfirmationDialog()
        } else {
            finish()
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("No changes detected. Do you want to exit without saving?")
            .setTitle("No Changes")
        builder.setPositiveButton("Yes") { dialog, id ->
            resetImageToOriginal()
            finish()
        }
        builder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to save?")
            .setTitle("Confirmation")
        builder.setPositiveButton("Yes") { dialog, id ->
            Log.d("isSelectedArraySize", CommonUtil.SelcetedFileList.size.toString())
            if (CommonUtil.SelcetedFileList.size > 0) {
                AwsUploadingFile(CommonUtil.SelcetedFileList[0])
            } else {
                isSaveTheData()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun resetImageToOriginal() {
        profileImagePath = null
        CommonUtil.SelcetedFileList.clear()
        Glide.with(this)
            .load(isUserImage)
            .placeholder(R.drawable.default_profile)
            .error(R.drawable.default_profile)
            .into(binding.imgProfile)
    }

    private fun showChooseImageDialog() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialog: View = inflater.inflate(R.layout.popup_choose_file, null)
        popupWindow = PopupWindow(
            dialog, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
        )
        popupWindow?.showAtLocation(dialog, Gravity.BOTTOM, 0, 0)
        val LayoutGallery = dialog.findViewById<ConstraintLayout>(R.id.LayoutGallery)
        val LayoutCamera = dialog.findViewById<ConstraintLayout>(R.id.LayoutCamera)
        val LayoutDocuments =
            dialog.findViewById<ConstraintLayout>(R.id.LayoutDocuments) // <-- find PDF option
        val popClose = dialog.findViewById<ImageView>(R.id.popClose)
        LayoutDocuments.visibility = View.GONE

        val container = popupWindow?.contentView?.parent as View
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.7f
        wm.updateViewLayout(container, p)


        popClose.setOnClickListener {
            popupWindow?.dismiss()
        }
        LayoutGallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
            popupWindow?.dismiss()
        }

        LayoutCamera.setOnClickListener {
            popupWindow?.dismiss()
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                if (photoFile != null) {
                    val imageUri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        photoFile
                    )
                    photoURI = imageUri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

        LayoutCamera.setOnClickListener {
            popupWindow?.dismiss()
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                if (photoFile != null) {
                    val imageUri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        photoFile
                    )
                    photoURI = imageUri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        profileImagePath = image.absolutePath
        Log.d("createImageFile", "Created image file path: $profileImagePath")
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    CommonUtil.SelcetedFileList.clear()
                    val selectedImage: Uri? = data?.data
                    selectedImage?.let {
                        profileImagePath = getPathFromUri(it)
                        Log.d("REQUEST_PICK_IMAGE", "Selected image URI: $it")
                        Log.d("REQUEST_PICK_IMAGE", "Selected image path: $profileImagePath")
                        CommonUtil.SelcetedFileList.add(profileImagePath.toString())
                        Glide.with(this)
                            .load(it)
                            .placeholder(R.drawable.default_profile)
                            .into(binding.imgProfile)
                    }
                }

                REQUEST_IMAGE_CAPTURE -> {
                    CommonUtil.SelcetedFileList.clear()
                    photoURI?.let {
                        Log.d("REQUEST_IMAGE_CAPTURE", "Captured photo URI: $it")
                        Log.d("REQUEST_IMAGE_CAPTURE", "Captured photo path: $profileImagePath")
                        CommonUtil.SelcetedFileList.add(profileImagePath.toString())
                        Glide.with(this)
                            .load(it)
                            .placeholder(R.drawable.default_profile)
                            .into(binding.imgProfile)
                    }
                }
            }
        } else {
            Log.d("onActivityResult", "Result was cancelled or failed")
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        Log.d("getPathFromUri", "Resolved file path: $path")
        return path
    }

    private fun AwsUploadingFile(isFilePath: String) {
        isAwsUploadingPreSigned!!.getPreSignedUrl(
            this,
            isFilePath,
            CommonUtil.Collage_ids,
            object : UploadCallback {
                override fun onUploadSuccess(response: String?, isAwsFile: String?) {
                    Log.d("Upload Success", response!!)
                    Log.d("isAwsFile", isAwsFile!!)
                    Awsuploadedfile.add(isAwsFile.toString())
                    Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
                    if (Awsuploadedfile.size == CommonUtil.SelcetedFileList.size) {
                        Log.d("isAwsFile", Awsuploadedfile[0].toString())
                        isSaveTheData()
                    }
                }
                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
                    Toast.makeText(this@EditBasicDetails, "Upload failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    fun isSaveTheData() {
        val updatedName = binding.edtName.text.toString().trim()
        val updatedPhone = binding.edtPhone.text.toString().trim()
        val updatedEmail = binding.edtEmail.text.toString().trim()

        var isProfileImage = ""
        if (Awsuploadedfile.size > 0) {
            isProfileImage = Awsuploadedfile[0]
        } else if (isImageDeleted) {
            isProfileImage = ""
        } else {
            isProfileImage = isUserImage.toString()
        }

        val jsonObject = JsonObject().apply {
            addProperty("idMember", memberId)
            addProperty("imagePath", isProfileImage)
            addProperty("memberName", updatedName)
            addProperty("notificationPlacement", binding.customSwitch.isChecked())
            addProperty("placementStatus", "Placed")
            addProperty("primaryMobileNo", updatedPhone)
            addProperty("studentEmail", updatedEmail)
        }
        Log.d("isSaveDataRequest", jsonObject.toString())
        viewModel.addEditProfile(jsonObject, this@EditBasicDetails)
    }
}
