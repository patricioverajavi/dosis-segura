package com.example.aplicacionmovil;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

/**
 * Prueba de Interfaz de Usuario (UI) para la pantalla de Login.
 * 
 * Explicación de comandos Espresso:
 * - onView(): Se usa para buscar y "apuntar" a un elemento específico de la pantalla usando su ID u otra propiedad.
 * - perform(click()): Simula la acción de tocar o hacer clic sobre el elemento que encontramos.
 * - perform(typeText("texto")): Simula que el usuario escribe un texto dentro de un campo de entrada.
 * - check(matches(isDisplayed())): Verifica que el elemento realmente sea visible para el usuario en la pantalla.
 */
@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginConCamposVacios_muestraMensajeError() {
        // 1. Dejar ambos campos vacíos y presionar el botón de login
        // Buscamos el botón por su ID (btnIngresar) y le damos clic
        onView(withId(R.id.btnIngresar))
                .perform(click());

        // 2. Verificar que aparece el mensaje 'El correo es obligatorio'
        // Verificamos que el error se muestra en el campo de texto del usuario
        // Usamos hasErrorText() porque el error se estableció con txtUsuario.setError()
        onView(withId(R.id.txtUsuario))
                .check(matches(hasErrorText("El correo es obligatorio")));
    }
}
