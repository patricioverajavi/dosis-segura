package com.example.aplicacionmovil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class FdaViewModel : ViewModel() {

    private val _estado = MutableStateFlow<ApiState>(ApiState.Loading)
    val estado: StateFlow<ApiState> = _estado

    fun cargarMedicamentos() {
        _estado.value = ApiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.getInstance()
                    .apiService
                    .getMedicamentos(5)
                    .awaitResponse()

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val lista = respuesta.body()!!.results
                    _estado.value = ApiState.Success(lista)
                } else {
                    _estado.value = ApiState.Error("Error: ${respuesta.code()}")
                }
            } catch (e: Exception) {
                _estado.value = ApiState.Error("Sin conexión: ${e.message}")
            }
        }
    }
}