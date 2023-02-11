package com.dbtech.ourwidget

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentAddPostBinding
import com.dbtech.ourwidget.databinding.FragmentHomeScreenBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.io.IOException

class AddPostFragment : Fragment() {
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    val storageRef = Firebase.storage.reference

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) { //REQUEST_STORAGE_PERMISSION
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
                startRecording()
            } else {
                // Permission has been denied

            }
        }
    }

    lateinit var filePath : String
    fun getFilePath() {
//        val timestamp = System.currentTimeMillis()
//        val file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording_$timestamp.3gp"

        val file = File(requireActivity().filesDir, "recording_" + System.currentTimeMillis() + ".3gp")
//        return file.absolutePath

        filePath = file.absolutePath
    }
    var mediaRecorder: MediaRecorder? = null


    fun startRecording() {
        getFilePath()

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(filePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            // TODO: Ask for permissions
            try {
                prepare()
                start()
                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }





    fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
//                AlertDialog.Builder(requireContext())
//                    .setTitle("Permission Needed")
//                    .setMessage("This app needs access to your device's storage to save the recorded audio.")
//                    .setPositiveButton("OK") { _, _ ->
//                        ActivityCompat.requestPermissions(
//                            requireActivity(),
//                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                            1
//                        )
//                    }
//                    .setNegativeButton("Cancel") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .create().show()
                Snackbar.make(
//                    findViewById(R.id.main_layout)
                    binding.root,
                    "This app needs access to your device's storage to save the recorded audio.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("OK") {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                            REQUEST_STORAGE_PERMISSION
                        1
                        )
                    }
                    .show()
            }

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
//                    REQUEST_STORAGE_PERMISSION
            )
        } else {
            // Permission has already been granted
            startRecording()
        }
    }

    fun requestStoragePermissionAndStartRecording() {
        requestStoragePermission()
    }


    fun uploadToFirebase() {
        val file = File(filePath)
        val filename = filePath.substring(filePath.lastIndexOf("/") + 1)
        val audioRef = storageRef.child("audio/$filename")

        audioRef.putFile(Uri.fromFile(file))
            .addOnSuccessListener {
                Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                file.delete()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }

        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
        mediaRecorder = null

        uploadToFirebase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        val view = binding.root


//        storageReference = FirebaseStorage.getInstance().reference




        binding.btnStart.setOnClickListener {
            requestStoragePermissionAndStartRecording()
        }

        binding.btnStop.setOnClickListener {
            stopRecording()
        }






        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
//                Log.i("PhotoPicker", "Selected URI: ${uri}")
                // File or Blob
                val file = uri

// Create the file metadata
                val metadata = storageMetadata {
                    contentType = "image/jpeg"
                }

// Upload file and metadata to the path 'images/mountains.jpg'
                val ref = storageRef.child("images/${AddPostFragmentArgs.fromBundle(requireArguments()).groupId}/${file.lastPathSegment}")
                val uploadTask = ref.putFile(file, metadata)

// Listen for state changes, errors, and completion of the upload.
// You'll need to import com.google.firebase.storage.ktx.component1 and
// com.google.firebase.storage.ktx.component2
                uploadTask.addOnProgressListener { it ->
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
//                    Log.i(TAG, "Upload is $progress% done")
                        Toast.makeText(requireContext(), "Upload is $progress% done", Toast.LENGTH_LONG).show()
                }.addOnPausedListener {
//                    Log.i(TAG, "Upload is paused")
                    Toast.makeText(requireContext(), "Upload paused", Toast.LENGTH_LONG).show()

                }.addOnFailureListener {
                    // Handle unsuccessful uploads
//                    Log.i(TAG, "Failed")
                    Toast.makeText(requireContext(), "Upload failed ${it.cause}", Toast.LENGTH_LONG).show()

                }.addOnSuccessListener { it ->
                    //Add image path in database
                    val databaseRef = Firebase.database.reference
                    val user = Firebase.auth.currentUser
                    var userId : String? = null
                    user?.let {
//                        val name = user.displayName
                        userId = user.uid
                    }
//                    val photoWidget : PhotoWidget
//                    photoWidget.
                    val groupId = AddPostFragmentArgs.fromBundle(requireArguments()).groupId
                    val key = databaseRef.child("groups").child(groupId).child("posts").push().getKey()
                    databaseRef.child("groups").child(groupId).child("posts").child(key!!).child("author").setValue(userId!!)
                    databaseRef.child("groups").child(groupId).child("posts").child(key!!).child("imageUrl").setValue("images/${AddPostFragmentArgs.fromBundle(requireArguments()).groupId}/${file.lastPathSegment}")

                    Toast.makeText(requireContext(), "Upload succeeded", Toast.LENGTH_LONG).show()

                }

            } else {
                Log.i("PhotoPicker", "No media selected")
            }
        }
        binding.addImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        // Inflate the layout for this fragment
        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//            Firebase.auth.signOut()
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            view?.findNavController()?.navigate(R.id.action_addUserNameFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
    }

}


