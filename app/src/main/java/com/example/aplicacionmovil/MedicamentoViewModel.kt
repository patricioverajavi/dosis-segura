package com.example.aplicacionmovil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicamentoRepository = MedicamentoRepository(application)

    // Al usar @JvmName, forzamos a que el compilador cree métodos que Java
    // reconocerá con el nombre exacto que escribamos ahí.

    @JvmName("getTodos")
    fun getTodos(): LiveData<List<Medicamento>> = repository.getTodos()

    @JvmName("getFavoritos")
    fun getFavoritos(): LiveData<List<Medicamento>> = repository.getFavoritos()

    // Las funciones de corrutinas (insert, update, delete) funcionan bien en Java
    // como métodos 'void'.

    fun insert(medicamento: Medicamento) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(medicamento)
    }

    fun update(medicamento: Medicamento) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(medicamento)
    }

    fun delete(medicamento: Medicamento) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(medicamento)
    }
}