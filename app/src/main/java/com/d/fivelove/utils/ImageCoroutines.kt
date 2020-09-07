package com.d.fivelove.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Nguyen Kim Khanh on 8/27/2020.
 */
class ImageCoroutines : ViewModel() {
    fun uploadImage(context: Context, outputUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageName = System.currentTimeMillis().toFloat()
            FirebaseStorage.getInstance().reference.child("images")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(imageName.toString())
                    .putFile(outputUri)
                    .addOnSuccessListener { Toast.makeText(context, "Done! Upload Image success!", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { Toast.makeText(context, "Something is wrong! Upload Image fail!", Toast.LENGTH_SHORT).show() }
        }
    }
}