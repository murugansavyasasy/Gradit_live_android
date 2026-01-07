package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentAdapter
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentHolder
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.EditSkillSet.AttachmentSource
import com.vsca.vsnapvoicecollege.Model.FilePath
import com.vsca.vsnapvoicecollege.Model.FileType
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.albumImage.AlbumSelectActivity
import com.vsca.vsnapvoicecollege.databinding.LayoutEditacademicdetailsBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditAcademicDetails : AppCompatActivity() {


    private  val PERMISSION_REQUEST_CODE = 1001
    private  val SETTINGS_REQUEST_CODE = 1002
    // Holds current adapter's list (internship / certificate / project)
    private var attachmentList: MutableList<AttachmentHolder> = mutableListOf()

    // Which row was clicked
    private var attachmentPosition: Int = RecyclerView.NO_POSITION

    // Which adapter triggered attachment
    private var attachmentSource: AttachmentSource? = null

    private var selectedRowView: View? = null
    val REQUEST_Camera = 1
    val SELECT_DOCUMENT = 101

    val REQUEST_GAllery = 2
    var FilePopup: PopupWindow? = null

    private val MAX_FILES_PER_QUESTION = 10
    private val MAX_VIDEO_PER_QUESTION = 2

    private var uploadDialog: AlertDialog? = null
    private lateinit var txtProgress: TextView
    var Totalfile: String? = null

    var imageFilePath: String? = null
    var photoTempFileWrite: File? = null

    var photoURI: Uri? = null

    var filename: String? = null


    //
    private var appViewModel: App? = null
    private lateinit var binding: LayoutEditacademicdetailsBinding

    private var originalBacklogs: String = ""
    private var originalArrears: String = ""
    private var originalEducationalDetails: List<GetEducationalDetailsData> = listOf()
    var memberId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditacademicdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()


        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        // Hide default icon and update button text
        binding.commonBottomResumeBuilder.imgDefault.visibility = View.GONE
        binding.commonBottomResumeBuilder.btnDefault2.text = getString(R.string.update)

        val academicData = CommonUtil.saveAcademicDetails

        val backlogs = academicData?.backlogs ?: ""
        val arrears = academicData?.numberOfArrears ?: ""
        val educationalDetails = academicData?.educationalDetails ?: emptyList()
        Log.d("AcademicDebug", "Backlogs: ${academicData?.backlogs}")
        Log.d("AcademicDebug", "No of Arrears: ${academicData?.numberOfArrears}")
        Log.d(
            "AcademicDebug", "Educational Details Count: ${academicData?.educationalDetails?.size}"
        )


        originalBacklogs = backlogs
        originalArrears = arrears
//        originalEducationalDetails = educationalDetails

        //Need deep Copy because we store in model it the below will always immutuable
        originalEducationalDetails = educationalDetails.map { edu ->
            GetEducationalDetailsData(
                classDegree = edu.classDegree,
                percentage = edu.percentage,
                institution = edu.institution,
                file_path = edu.file_path.map { it.copy() }.toMutableList()
            )
        }


        binding.edtBacklogs.setText(backlogs)
        binding.edtArrears.setText(arrears)

        if (educationalDetails.isNotEmpty()) {
            educationalDetails.forEach {
                addRow(it)
            }
        } else {
            addRow()
        }


        binding.lblAddAnother.setOnClickListener {
            if (validateCurrentRows()) {
                addRow()
            }
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }
        binding.commonBottomResumeBuilder.btnDefault1.setOnClickListener {
            finish()
        }

        // Observe save response
        appViewModel?.resumeBuilderAcademicAddEditResponse?.observe(this) { response ->
            if (response != null && response.status) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Save failed, please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            if (!validateCurrentRows()) {
                return@setOnClickListener
            }

            if (isDataChanged()) {
                showSaveConfirmationDialog()
            } else {
                showNoChangesDialog()
            }
        }
    }

    private fun validateCurrentRows(): Boolean {
        for (i in 0 until binding.containerLayout.childCount) {
            val row = binding.containerLayout.getChildAt(i)
            val edtClassDegree = row.findViewById<EditText>(R.id.edtClassDegree)
            val edtPercentage = row.findViewById<EditText>(R.id.edtPercentage)
            val edtInstitution = row.findViewById<EditText>(R.id.edtInstitution)

            val classDegree = edtClassDegree.text.toString().trim()
            val percentage = edtPercentage.text.toString().trim()
            val institution = edtInstitution.text.toString().trim()

            if (classDegree.isEmpty()) {
                edtClassDegree.error = "Enter class/degree"
                edtClassDegree.requestFocus()
                return false
            }

            if (percentage.isEmpty()) {
                edtPercentage.error = "Enter % of marks"
                edtPercentage.requestFocus()
                return false
            }

            if (institution.isEmpty()) {
                edtInstitution.error = "Enter institution/school name"
                edtInstitution.requestFocus()
                return false
            }
        }
        return true
    }

    private fun isDataChanged(): Boolean {

        val currentBacklogs = binding.edtBacklogs.text.toString().trim()
        val currentArrears = binding.edtArrears.text.toString().trim()

        if (currentBacklogs != originalBacklogs) return true
        if (currentArrears != originalArrears) return true

        if (binding.containerLayout.childCount != originalEducationalDetails.size) return true

        for (i in 0 until binding.containerLayout.childCount) {

            val row = binding.containerLayout.getChildAt(i)

            val edtClassDegree = row.findViewById<EditText>(R.id.edtClassDegree)
            val edtPercentage = row.findViewById<EditText>(R.id.edtPercentage)
            val edtInstitution = row.findViewById<EditText>(R.id.edtInstitution)

            val holder = row.tag as AcademicRowHolder

            val classDegree = edtClassDegree.text.toString().trim()
            val percentage = edtPercentage.text.toString().trim()
            val institution = edtInstitution.text.toString().trim()

            val original = originalEducationalDetails[i]

            // Text field check
            if (classDegree != original.classDegree || percentage != original.percentage || institution != original.institution) return true

            // Attachment size check
            if (holder.attachments.size != original.file_path.size) return true

            //Attachment content check
            holder.attachments.forEachIndexed { index, currentFile ->
                val originalFile = original.file_path[index]

                if (currentFile.url != originalFile.url || currentFile.type != originalFile.type) {
                    return true
                }
            }
        }
        return false
    }

    private fun showSaveConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Confirm Save")
        builder.setMessage("Are you sure you want to save these changes?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            saveAcademicDetails()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showNoChangesDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("No Changes Detected")
        builder.setMessage("No changes found. Exit without saving?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun addRow(item: GetEducationalDetailsData? = null) {

        val rowView = LayoutInflater.from(this)
            .inflate(R.layout.item_qualification, binding.containerLayout, false)

        val edtClassDegree = rowView.findViewById<EditText>(R.id.edtClassDegree)
        val edtPercentage = rowView.findViewById<EditText>(R.id.edtPercentage)
        val edtInstitution = rowView.findViewById<EditText>(R.id.edtInstitution)
        val imgRemove = rowView.findViewById<View>(R.id.imgRemove)
        val lblQuestionPick = rowView.findViewById<View>(R.id.lblQuestionPick)
        val rcyAttachment = rowView.findViewById<RecyclerView>(R.id.rcyAttachment)

        val attachments = item?.file_path ?: mutableListOf()

        val adapter = AttachmentAdapter(this, attachments) { removePos ->

            if (removePos in attachments.indices) {
                // Remove item from list
                attachments.removeAt(removePos)
                // Notify RecyclerView adapter safely
                (rcyAttachment.adapter as AttachmentAdapter).notifyItemRemoved(removePos)
                (rcyAttachment.adapter as AttachmentAdapter).notifyItemRangeChanged(
                    removePos, attachments.size
                )
            }

            rcyAttachment.visibility = if (attachments.isEmpty()) View.GONE else View.VISIBLE
        }


        rcyAttachment.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        rcyAttachment.adapter = adapter
        rcyAttachment.visibility = if (attachments.isEmpty()) View.GONE else View.VISIBLE

        // Save row reference
        rowView.tag = AcademicRowHolder(attachments, adapter, rcyAttachment)

        lblQuestionPick.setOnClickListener {
            selectedRowView = rowView
//            ChooseFile()
            logPermissionStatus("BeforeChooseFile")
            openFilePickerWithPermission()

        }

        imgRemove.setOnClickListener {
            if (binding.containerLayout.childCount > 1) {
                binding.containerLayout.removeView(rowView)
            } else {
                Toast.makeText(this, "At least one entry is required.", Toast.LENGTH_SHORT).show()
            }
        }

        // ---------- Prefill ----------
        item?.let {
            edtClassDegree.setText(it.classDegree)
            edtPercentage.setText(it.percentage)
            edtInstitution.setText(it.institution)
        }

        binding.containerLayout.addView(rowView)
    }

    private fun saveAcademicDetails() {

        val backlogs = binding.edtBacklogs.text.toString().trim()
        val arrears = binding.edtArrears.text.toString().trim()

        val educationalDetails = mutableListOf<Map<String, Any>>()

        for (i in 0 until binding.containerLayout.childCount) {
            val row = binding.containerLayout.getChildAt(i)
            val holder = row.tag as AcademicRowHolder

            val classDegree =
                row.findViewById<EditText>(R.id.edtClassDegree).text.toString().trim()
            val percentage =
                row.findViewById<EditText>(R.id.edtPercentage).text.toString().trim()
            val institution =
                row.findViewById<EditText>(R.id.edtInstitution).text.toString().trim()

            val filePathList = holder.attachments.map { file ->
                mutableMapOf(
                    "url" to file.url,
                    "type" to file.type
                )
            }.toMutableList()

            educationalDetails.add(
                mutableMapOf(
                    "classDegree" to classDegree,
                    "percentage" to percentage,
                    "institution" to institution,
                    "file_path" to filePathList
                )
            )
        }

        normalizeAcademicFileUrls(educationalDetails)

        uploadAcademicFilesThenSubmit(
            educationalDetails,
            backlogs,
            arrears
        )
    }


    private fun submitAcademicApi(
        educationalDetails: List<Map<String, Any>>, backlogs: String, arrears: String
    ) {
        val request = hashMapOf<String, Any>(
            "idMember" to CommonUtil.MemberId,
            "educationalDetails" to educationalDetails,
            "backlogs" to backlogs,
            "numberOfArrears" to arrears
        )

        Log.d("FinalAcademicList", request.toString())
        appViewModel?.AddEditAcademicDetails(request, this)
    }

    private fun updateAcademicAwsUrl(
        educationalDetails: MutableList<Map<String, Any>>, localPath: String, awsUrl: String
    ) {
        educationalDetails.forEach { detail ->
            val files = detail["file_path"] as? MutableList<Map<String, String>> ?: return@forEach

            files.forEachIndexed { index, file ->
                if (file["url"] == localPath) {
                    files[index] = file.toMutableMap().apply {
                        put("url", awsUrl)
                    }
                }
            }
        }
    }

    private fun collectAcademicPendingFiles(
        context: Context, educationalDetails: List<Map<String, Any>>
    ): List<String> {

        val pending = mutableListOf<String>()

        educationalDetails.forEach { detail ->
            val files = detail["file_path"] as? List<Map<String, String>> ?: return@forEach

            files.forEach { file ->
                val url = file["url"] ?: return@forEach
                if (!url.startsWith("http", true)) {
                    val normalized = normalizePath(context, url)
                    pending.add(normalized)
                }
            }
        }
        return pending
    }

    private fun normalizePath(context: Context, path: String): String {
        return when {
            path.startsWith("content://") -> {
                copyUriToCacheFile(context, Uri.parse(path))
            }

            path.startsWith("file://") -> {
                Uri.parse(path).path ?: path
            }

            else -> path
        }
    }

    private fun copyUriToCacheFile(context: Context, uri: Uri): String {
        val fileName = getFileName(uri)
        val file = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    private fun uploadAcademicFilesThenSubmit(
        educationalDetails: MutableList<Map<String, Any>>,
        backlogs: String,
        arrears: String
    ) {
        val awsUploader = AwsUploadingPreSigned()
        val pendingFiles = collectAcademicPendingFiles(this, educationalDetails)

        if (pendingFiles.isEmpty()) {
            runOnUiThread {
                submitAcademicApi(educationalDetails, backlogs, arrears)
            }
            return
        }

        var completed = 0
        val total = pendingFiles.size

        // ✅ SAFE dialog show
        showUploadProgressDialog(total)

        pendingFiles.forEach { localPath ->

            awsUploader.getPreSignedUrl(
                this,
                localPath,
                "",
                object : UploadCallback {

                    override fun onUploadSuccess(message: String?, fileUrl: String?) {

                        updateAcademicAwsUrl(
                            educationalDetails,
                            localPath,
                            fileUrl ?: ""
                        )

                        completed++
                        updateUploadProgress(completed, total)

                        if (completed == total) {
                            dismissUploadDialog()

                            runOnUiThread {
                                submitAcademicApi(
                                    educationalDetails,
                                    backlogs,
                                    arrears
                                )
                            }
                        }
                    }

                    override fun onUploadError(error: String?) {
                        Log.e("AWS_UPLOAD", error ?: "Upload failed")

                        completed++
                        updateUploadProgress(completed, total)

                        if (completed == total) {
                            dismissUploadDialog()

                            runOnUiThread {
                                submitAcademicApi(
                                    educationalDetails,
                                    backlogs,
                                    arrears
                                )
                            }
                        }
                    }
                }
            )
        }
    }




    private fun normalizeAcademicFileUrls(
        educationalDetails: MutableList<Map<String, Any>>
    ) {
        educationalDetails.forEach { detail ->

            val files =
                detail["file_path"] as? MutableList<MutableMap<String, String>>
                    ?: return@forEach

            files.forEachIndexed { index, file ->

                val url = file["url"] ?: return@forEachIndexed

                if (url.startsWith("content://")) {

                    val realPath = copyUriToCacheFile(
                        this,
                        Uri.parse(url)
                    )

                    files[index] = file.toMutableMap().apply {
                        put("url", realPath)
                    }
                }

                if (url.startsWith("file://")) {
                    files[index] = file.toMutableMap().apply {
                        put("url", Uri.parse(url).path ?: url)
                    }
                }
            }
        }
    }



private fun showUploadProgressDialog(total: Int) {
    runOnUiThread {
        val view = layoutInflater.inflate(R.layout.dialog_upload_progress, null)
        txtProgress = view.findViewById(R.id.txtProgress)
        txtProgress.text = "Please wait… Uploading..... 0/$total"

        uploadDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        uploadDialog?.show()
    }
}



    private fun updateUploadProgress(done: Int, total: Int) {
        runOnUiThread {
            txtProgress?.text = "Please wait… Uploading....."
        }
    }

private fun dismissUploadDialog() {
    runOnUiThread {
        if (uploadDialog?.isShowing == true) {
            uploadDialog?.dismiss()
        }
    }
}


    fun ChooseFile() {

        Log.d("popup", "test")
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialog: View = inflater.inflate(R.layout.popup_choose_file, null)
        FilePopup = PopupWindow(
            dialog, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
        )
        FilePopup?.showAtLocation(dialog, Gravity.BOTTOM, 0, 0)
        FilePopup?.contentView = dialog
        FilePopup?.isOutsideTouchable = true
        FilePopup?.isFocusable = true

        val LayoutGallery = dialog.findViewById<ConstraintLayout>(R.id.LayoutGallery)
        val LayoutCamera = dialog.findViewById<ConstraintLayout>(R.id.LayoutCamera)
        val LayoutDocuments = dialog.findViewById<ConstraintLayout>(R.id.LayoutDocuments)
        val popClose = dialog.findViewById<ImageView>(R.id.popClose)
        val lblDocumentFile = dialog.findViewById<TextView>(R.id.lblDocumentFile)

        lblDocumentFile.text = "Documents"

        val container = FilePopup?.contentView?.parent as View
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.7f
        wm.updateViewLayout(container, p)

        popClose.setOnClickListener {
            FilePopup?.dismiss()
        }
        LayoutGallery.setOnClickListener {
//            CommonUtil.SelcetedFileList.clear()

            val intent1 = Intent(this, AlbumSelectActivity::class.java)
            intent1.putExtra("Gallery", "Images")
            startActivityForResult(intent1, REQUEST_GAllery)
            FilePopup!!.dismiss()
            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

        }

        LayoutCamera.setOnClickListener {
//            CommonUtil.SelcetedFileList.clear()

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                photoTempFileWrite = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoTempFileWrite != null) {
                photoURI = FileProvider.getUriForFile(
                    this, "com.vsca.vsnapvoicecollege.provider", photoTempFileWrite!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_Camera)
                FilePopup?.dismiss()
                Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

            }
        }

//        LayoutDocuments.setOnClickListener({
//
////            CommonUtil.SelcetedFileList.clear()
//
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "application/pdf"
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            startActivityForResult(intent, SELECT_PDF)
//            FilePopup!!.dismiss()
//            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//
//
//        })

        LayoutDocuments.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(
                    Intent.EXTRA_MIME_TYPES, arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "text/plain"
                    )
                )
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            startActivityForResult(intent, SELECT_DOCUMENT)
            FilePopup?.dismiss()

            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

        }


    }


    private fun addPath(uri: Uri, forcedType: FileType? = null) {

        val rowView = selectedRowView ?: return
        val holder = rowView.tag as AcademicRowHolder

        val fileName = getFileName(uri)

        val type = forcedType ?: when {
            fileName.endsWith(".pdf", true) -> FileType.PDF
            fileName.endsWith(".doc", true) || fileName.endsWith(".docx", true) -> FileType.DOC
            fileName.endsWith(".xls", true) || fileName.endsWith(".xlsx", true) -> FileType.EXCEL
            fileName.endsWith(".ppt", true) || fileName.endsWith(".pptx", true) -> FileType.PPT
            fileName.matches(".*\\.(jpg|jpeg|png|webp)$".toRegex(RegexOption.IGNORE_CASE)) -> FileType.IMAGE
            fileName.endsWith(".txt", true) -> FileType.TXT
            else -> FileType.OTHER
        }

        if (holder.attachments.size >= MAX_FILES_PER_QUESTION) {
            Toast.makeText(this, "Only $MAX_FILES_PER_QUESTION files allowed", Toast.LENGTH_SHORT)
                .show()
            return
        }

        holder.attachments.add(FilePath(uri.toString(), type.name))
        holder.recyclerView.visibility = View.VISIBLE
        holder.adapter.notifyItemInserted(holder.attachments.size - 1)
    }


    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE) {
            logPermissionStatus("AfterSettingsReturn")
            openFilePickerWithPermission(fromSettings = true)
            return
        }
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {

            //  CAMERA
            REQUEST_Camera -> {
                imageFilePath?.let {
                    CommonUtil.SelcetedFileList.add(it)
                    addPath(Uri.fromFile(File(it)), FileType.IMAGE)
                }
            }

            //  GALLERY
            REQUEST_GAllery -> {
                val images = data?.getStringArrayListExtra("images") ?: return
                images.forEach { path ->
                    CommonUtil.SelcetedFileList.add(path)
                    addPath(Uri.parse(path), FileType.IMAGE)
                }
            }

            // DOCUMENT
            SELECT_DOCUMENT -> {
                val itemCount = data?.clipData?.itemCount ?: if (data?.data != null) 1 else 0

                for (i in 0 until itemCount) {
                    val uri = data?.clipData?.getItemAt(i)?.uri ?: data?.data ?: continue
                    CommonUtil.SelcetedFileList.add(uri.toString())
                    addPath(uri)
                }
            }
        }

        updateFileCountUI()
        attachmentPosition = RecyclerView.NO_POSITION
    }


    private fun logPermissionStatus(tag: String = "PermissionCheck") {

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        Log.d(tag, "---------- Permission Status ----------")

        permissions.forEach { permission ->

            val granted = ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED

            val canAskAgain = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            )

            val status = when {
                granted -> "GRANTED ✅"
                !canAskAgain -> "DENIED (Don't ask again) ❌"
                else -> "DENIED ❌"
            }

            Log.d(tag, "$permission → $status")
        }

        Log.d(tag, "-------------------------------------")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            logPermissionStatus("AfterPermissionRequest")
            openFilePickerWithPermission()
        }
    }
    private fun openFilePickerWithPermission(fromSettings: Boolean = false) {

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // ✅ All granted
        if (deniedPermissions.isEmpty()) {
            ChooseFile()
            return
        }

        // 👇 NEW: user returned from settings but still denied
        if (fromSettings) {
            Toast.makeText(
                this,
                "Please enable permissions to select files",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val shouldShowRationale = deniedPermissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }

        if (shouldShowRationale) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                PERMISSION_REQUEST_CODE
            )
        } else {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
            startActivityForResult(intent, SETTINGS_REQUEST_CODE)
        }
    }

    private fun canAddAttachment(
        item: AttachmentHolder, newType: FileType
    ): Boolean {

        val attachments = item.file_path ?: return true

        if (attachments.size >= MAX_FILES_PER_QUESTION) {
            Toast.makeText(
                this, "Only $MAX_FILES_PER_QUESTION files allowed", Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: ""
    }

    @Throws(IOException::class)
    fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }


    /**
     * Updates the label showing number of selected files
     */
    private fun updateFileCountUI() {
        if (CommonUtil.SelcetedFileList.isNotEmpty()) {
            Totalfile = CommonUtil.SelcetedFileList.size.toString()
            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

        } else {
            Totalfile = CommonUtil.SelcetedFileList.size.toString()
            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
        }
    }
}