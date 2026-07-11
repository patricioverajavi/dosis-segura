package com.example.aplicacionmovil

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginIntegrationTest {

    private lateinit var usuarioDao: UsuarioDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        usuarioDao = db.usuarioDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testFlujoCompletoLogin() {
        // 1. REGISTRO: Crear un usuario en la DB en memoria
        val correoPrueba = "test@example.com"
        val passPrueba = "password123"
        val nuevoUsuario = Usuario(correoPrueba, "Usuario de Prueba", passPrueba)
        
        usuarioDao.registrar(nuevoUsuario)
        
        // Verificar que se registró correctamente
        val existe = usuarioDao.verificarSiExiste(correoPrueba)
        assertEquals("El usuario debería estar registrado", 1, existe)

        // 2. LOGIN EXITOSO: Intentar con las credenciales correctas
        val usuarioLogueado = usuarioDao.login(correoPrueba, passPrueba)
        assertNotNull("El login debería ser exitoso con credenciales correctas", usuarioLogueado)
        assertEquals("El nombre del usuario logueado no coincide", "Usuario de Prueba", usuarioLogueado?.nombre)

        // 3. LOGIN FALLIDO (Contraseña incorrecta)
        val loginFallidoPass = usuarioDao.login(correoPrueba, "error123")
        assertNull("El login debería fallar con contraseña incorrecta", loginFallidoPass)

        // 4. LOGIN FALLIDO (Usuario inexistente)
        val loginFallidoUser = usuarioDao.login("noexiste@example.com", passPrueba)
        assertNull("El login debería fallar con un correo no registrado", loginFallidoUser)
    }
}
