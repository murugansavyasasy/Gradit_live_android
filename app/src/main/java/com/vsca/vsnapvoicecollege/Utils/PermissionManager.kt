package com.vsca.vsnapvoicecollege.Utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class AppPermission(val label: String) {

    CAMERA("Camera"),

    RECORD_AUDIO("Microphone"),

    LOCATION("Location"),

    LOCATION_PRECISE("Precise location"),

    BACKGROUND_LOCATION("Background location"),

    CONTACTS("Contacts"),


    PHONE_STATE("Phone state"),

    NOTIFICATION("Notifications"),

    AUDIO_FILES("Audio files"),

    STORAGE("Storage"),

    GALLERY("Photos"),

    VIDEO("Videos"),

    PDF("Documents");


    fun permissions(): Array<String> {

        val sdk = Build.VERSION.SDK_INT

        return when (this) {

            CAMERA ->
                arrayOf(
                    Manifest.permission.CAMERA
                )


            RECORD_AUDIO ->
                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                )


            // Approximate or precise location is enough
            LOCATION ->
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )


            // Precise location is required
            LOCATION_PRECISE ->
                if (sdk >= Build.VERSION_CODES.S) {

                    // Android 12+
                    // Fine must be requested together with coarse
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                } else {

                    // Android 7 - 11
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }


            // Android 10+
            // Request this only after LOCATION / LOCATION_PRECISE
            BACKGROUND_LOCATION ->
                if (sdk >= Build.VERSION_CODES.Q) {

                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )

                } else {

                    emptyArray()
                }


            CONTACTS ->
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )



            PHONE_STATE ->
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE
                )


            // Android 13+
            NOTIFICATION ->
                if (sdk >= Build.VERSION_CODES.TIRAMISU) {

                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    )

                } else {

                    emptyArray()
                }



            AUDIO_FILES ->
                emptyArray()


            /*
             * Android 11+:
             * No storage permission required when using
             * app-specific storage / system picker.
             *
             * Android 7 - 10:
             * Keep legacy storage permission only if your
             * application directly reads external storage.
             */
            STORAGE ->
                if (sdk >= Build.VERSION_CODES.R) {

                    emptyArray()

                } else {

                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }



            GALLERY ->
                emptyArray()



            VIDEO ->
                emptyArray()


            PDF ->
                emptyArray()
        }
    }


    fun isGranted(context: Context): Boolean {

        val perms = permissions()

        // No runtime permission required
        if (perms.isEmpty()) {
            return true
        }

        fun ok(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        return when (this) {

            // Either fine OR coarse is enough
            LOCATION ->
                perms.any { ok(it) }


            // Fine location is required
            LOCATION_PRECISE ->
                ok(Manifest.permission.ACCESS_FINE_LOCATION)


            // All other permissions
            else ->
                perms.all { ok(it) }
        }
    }
}


/**
 * Create ONCE as a property of the Activity:
 *
 * private val permissionManager = PermissionManager(this)
 */
class PermissionManager(
    private val activity: ComponentActivity
) {

    private var pending: List<AppPermission> = emptyList()

    private var onGranted: (() -> Unit)? = null

    private var onDenied: (() -> Unit)? = null

    private var dialog: AlertDialog? = null


    private val permissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            handleResult()
        }


    private val settingsLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            finish(
                missing().isEmpty()
            )
        }


    /**
     * Runs onGranted only when ALL given permissions are granted.
     */
    fun runWithPermission(
        vararg permissions: AppPermission,
        onDenied: (() -> Unit)? = null,
        onGranted: () -> Unit
    ) {

        /*
         * Gallery / Video / Audio / PDF have no runtime
         * permission now because they use system pickers.
         */
        if (permissions.all {
                it.isGranted(activity)
            }
        ) {

            onGranted()

            return
        }


        pending = permissions.toList()

        this.onGranted = onGranted

        this.onDenied = onDenied

        request(
            missing()
        )
    }


    /**
     * Java-friendly version.
     */
    fun runWithPermissionJava(
        permission: AppPermission,
        onGranted: Runnable,
        onDenied: Runnable?
    ) {

        runWithPermission(
            permission,
            onDenied = {
                onDenied?.run()
            }
        ) {
            onGranted.run()
        }
    }


    private fun missing(): List<AppPermission> {

        return pending.filter {
            !it.isGranted(activity)
        }
    }


    private fun request(
        list: List<AppPermission>
    ) {

        val perms = list
            .flatMap {
                it.permissions().toList()
            }
            .distinct()
            .toTypedArray()


        /*
         * Gallery / Video / Audio / PDF:
         *
         * No runtime permissions.
         *
         * Continue directly and open the system picker
         * from the calling code.
         */
        if (perms.isEmpty()) {

            finish(true)

            return
        }


        permissionLauncher.launch(perms)
    }


    private fun handleResult() {

        val missing = missing()


        if (missing.isEmpty()) {

            finish(true)

            return
        }


        val permanentlyDenied = missing.any { appPermission ->

            appPermission.permissions().any {

                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            it
                        )
            }
        }


        if (permanentlyDenied) {

            showSettingsDialog(missing)

        } else {

            showRetryDialog(missing)
        }
    }


    private fun names(
        list: List<AppPermission>
    ): String {

        return list.joinToString(", ") {
            it.label
        }
    }


    private fun showRetryDialog(
        missing: List<AppPermission>
    ) {

        dialog?.dismiss()


        dialog = AlertDialog.Builder(activity)

            .setTitle("Permission needed")

            .setMessage(
                "Please allow ${names(missing)} permission to continue."
            )

            .setCancelable(false)

            .setPositiveButton("Allow") { _, _ ->

                request(missing)
            }

            .setNegativeButton("Cancel") { _, _ ->

                finish(false)
            }

            .show()
    }


    private fun showSettingsDialog(
        missing: List<AppPermission>
    ) {

        dialog?.dismiss()


        dialog = AlertDialog.Builder(activity)

            .setTitle("Permission denied")

            .setMessage(
                "Please enable ${names(missing)} permission from App Settings to use this feature."
            )

            .setCancelable(false)

            .setPositiveButton("Open Settings") { _, _ ->

                settingsLauncher.launch(

                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    ).apply {

                        data = Uri.fromParts(
                            "package",
                            activity.packageName,
                            null
                        )
                    }
                )
            }

            .setNegativeButton("Cancel") { _, _ ->

                finish(false)
            }

            .show()
    }


    private fun finish(
        granted: Boolean
    ) {

        val grantedCallback = onGranted

        val deniedCallback = onDenied


        pending = emptyList()

        onGranted = null

        onDenied = null


        if (granted) {

            grantedCallback?.invoke()

        } else {

            deniedCallback?.invoke()
        }
    }
}