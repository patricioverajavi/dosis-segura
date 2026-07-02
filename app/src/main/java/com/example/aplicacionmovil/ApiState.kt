package com.example.aplicacionmovil

sealed class ApiState {
    object Loading : ApiState()
    data class Success(val medicamentos: List<FdaResponse.DrugResult>) : ApiState()
    data class Error(val mensaje: String) : ApiState()
}