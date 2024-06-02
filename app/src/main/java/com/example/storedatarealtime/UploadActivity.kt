package com.example.storedatarealtime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storedatarealtime.databinding.ActivityUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.DateFormat
import java.util.Calendar

class UploadActivity : AppCompatActivity() {
    private lateinit var uploadBinding: ActivityUploadBinding
    var imageURL: String? = null
    var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        uploadBinding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(uploadBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) {
            it ->
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                uri = data!!.data
                uploadBinding.uploadImage.setImageURI(uri)
            } else {
                Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show()
            }
        }
        uploadBinding.uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        uploadBinding.idBtnSendData.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val storageReference = FirebaseStorage.getInstance().reference.child("Employee Image").child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@UploadActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener {
            val uriTask = it.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result

            imageURL = urlImage.toString()
            uploadData()
            dialog.dismiss()
        }.addOnFailureListener {
            dialog.dismiss()
        }
    }

    private fun uploadData() {
        val eName = uploadBinding.idEdtEmployeeName.text.toString()
        val eCN = uploadBinding.idEdtEmployeePhoneNumber.text.toString()
        val eA = uploadBinding.idEdtEmployeeAddress.text.toString()

        val employeeInfo = EmployeeInfo(eName, eCN, eA, imageURL)
        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        FirebaseDatabase.getInstance().getReference("Employee List").child(currentDate).setValue(employeeInfo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this@UploadActivity, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this@UploadActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}