package com.example.ece441project.ui.theme.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    data class AuthState(
        val email: String = "",
        val password: String = "",
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun updateEmail(v: String) {
        _state.update { it.copy(email = v) }
    }

    fun updatePassword(v: String) {
        _state.update { it.copy(password = v) }
    }

    fun signIn(onSuccess: () -> Unit) {
        val email = state.value.email
        val password = state.value.password

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
    }

    fun register(onSuccess: () -> Unit) {
        val email = state.value.email
        val password = state.value.password

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
    }
}

