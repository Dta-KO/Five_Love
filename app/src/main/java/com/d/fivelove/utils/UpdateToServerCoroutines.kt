package com.d.fivelove.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Nguyen Kim Khanh on 9/5/2020.
 */
class UpdateToServerCoroutines : ViewModel() {
    fun updateAbilityListener(id: String, abilityListener: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val reference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(id)
            reference.update("abilityListener", abilityListener)
        }
    }
}