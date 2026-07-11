package com.example.aplicacionmovil

import org.junit.Assert.*
import org.junit.Test

class ValidacionesTest {

    // ---------------------------------------------------------------
    // TEST 1: Campos obligatorios completos -> debe ser válido
    // Qué verifica: que un Medicamento con nombre, dosis y principio
    // activo llenos pase la validación.
    // ---------------------------------------------------------------
    @Test
    fun validarCamposObligatorios_datosCompletos_retornaTrue() {
        // ARRANGE
        val medicamentoValido = Medicamento(
            "Paracetamol", "Analgésico", "Tableta", "500mg",
            "Insuficiencia hepática", "No exceder dosis diaria",
            "Paracetamol", "Genfar", "Dolor y fiebre", "Analgésicos", false
        )

        // ACT
        val resultado = MedicamentoValidator.validarCamposObligatorios(medicamentoValido)

        // ASSERT
        // assertTrue verifica que el resultado sea exactamente "true".
        // Si la función retornara false aquí, significaría que el validador
        // está rechazando incorrectamente un medicamento con todos sus
        // datos obligatorios completos (falso negativo).
        assertTrue("Un medicamento con todos los campos obligatorios debe ser válido", resultado)
    }

    // ---------------------------------------------------------------
    // TEST 2: Campo obligatorio vacío -> debe ser inválido
    // Qué verifica: que un Medicamento sin nombre (campo vacío) NO
    // pase la validación.
    // ---------------------------------------------------------------
    @Test
    fun validarCamposObligatorios_nombreVacio_retornaFalse() {
        // ARRANGE
        val medicamentoSinNombre = Medicamento(
            "", "Analgésico", "Tableta", "500mg",
            "Insuficiencia hepática", "No exceder dosis diaria",
            "Paracetamol", "Genfar", "Dolor y fiebre", "Analgésicos", false
        )

        // ACT
        val resultado = MedicamentoValidator.validarCamposObligatorios(medicamentoSinNombre)

        // ASSERT
        // assertFalse verifica que el resultado sea exactamente "false".
        // Si la función retornara true aquí, sería un error grave: significaría
        // que se podría guardar en la base de datos un medicamento sin nombre,
        // lo cual es inaceptable en una app de catálogo médico.
        assertFalse("Un medicamento sin nombre no debe ser válido", resultado)
    }

    // ---------------------------------------------------------------
    // TEST 3: Formato de dosis válido vs inválido
    // Qué verifica: que la función reconozca correctamente un formato
    // de dosis bien escrito (número + unidad) y rechace uno mal escrito.
    // ---------------------------------------------------------------
    @Test
    fun validarFormatoDosis_formatoCorrecto_retornaTrue() {
        // ARRANGE
        val dosisValida = "500mg"

        // ACT
        val resultado = MedicamentoValidator.validarFormatoDosis(dosisValida)

        // ASSERT
        // Verifica que un texto que sigue el patrón "número + unidad" sea aceptado.
        assertTrue("'500mg' es un formato de dosis válido", resultado)
    }

    @Test
    fun validarFormatoDosis_formatoIncorrecto_retornaFalse() {
        // ARRANGE
        val dosisInvalida = "quinientos miligramos"

        // ACT
        val resultado = MedicamentoValidator.validarFormatoDosis(dosisInvalida)

        // ASSERT
        // Verifica que un texto libre, sin número ni unidad reconocida, sea rechazado.
        // Si esto retornara true, se aceptarían dosis ambiguas o mal escritas,
        // lo que podría causar errores de interpretación en la app.
        assertFalse("Un texto sin formato numérico + unidad no debe ser válido", resultado)
    }

    // ---------------------------------------------------------------
    // CASO DE BORDE adicional: dosis con solo espacios
    // Qué verifica: que un valor "vacío en apariencia" (solo espacios)
    // sea tratado igual que un campo vacío.
    // ---------------------------------------------------------------
    @Test
    fun validarFormatoDosis_soloEspacios_retornaFalse() {
        // ARRANGE
        val dosisSoloEspacios = "   "

        // ACT
        val resultado = MedicamentoValidator.validarFormatoDosis(dosisSoloEspacios)

        // ASSERT
        assertFalse("Un texto de solo espacios no debe considerarse una dosis válida", resultado)
    }

    // ---------------------------------------------------------------
    // CASO DE BORDE adicional: nombre que es solo números
    // Qué verifica: que el validador de nombre rechace un nombre
    // compuesto solo por dígitos (ej. alguien escribió el código
    // en lugar del nombre del medicamento).
    // ---------------------------------------------------------------
    @Test
    fun validarNombreMedicamento_soloNumeros_retornaFalse() {
        // ARRANGE
        val nombreInvalido = "12345"

        // ACT
        val resultado = MedicamentoValidator.validarNombreMedicamento(nombreInvalido)

        // ASSERT
        assertFalse("Un nombre compuesto solo de números no debe ser válido", resultado)
    }

    // ---------------------------------------------------------------
    // CASO DE BORDE adicional: Medicamento nulo
    // Qué verifica: que la función no lance una excepción (NullPointerException)
    // al recibir un objeto nulo, sino que retorne false de forma controlada.
    // Esto es crítico: si la función lanzara una excepción en vez de manejar
    // el null, la app podría crashear al intentar validar un medicamento
    // que no se cargó correctamente.
    // ---------------------------------------------------------------
    @Test
    fun validarCamposObligatorios_medicamentoNulo_retornaFalse() {
        // ARRANGE
        val medicamentoNulo: Medicamento? = null

        // ACT
        val resultado = MedicamentoValidator.validarCamposObligatorios(medicamentoNulo)

        // ASSERT
        assertFalse("Un medicamento nulo no debe considerarse válido", resultado)
    }

    // ---------------------------------------------------------------
    // CASO DE BORDE adicional: dosis con unidad escrita completa (no abreviada)
    // Qué verifica: que la función rechace formatos como "500 miligramos"
    // en lugar de la abreviatura esperada "500mg". Esto simula un error
    // común de un usuario escribiendo la dosis de forma libre.
    // ---------------------------------------------------------------
    @Test
    fun validarFormatoDosis_unidadEscritaCompleta_retornaFalse() {
        // ARRANGE
        val dosisConUnidadCompleta = "500 miligramos"

        // ACT
        val resultado = MedicamentoValidator.validarFormatoDosis(dosisConUnidadCompleta)

        // ASSERT
        assertFalse("'500 miligramos' no sigue el formato abreviado esperado (ej. 500mg)", resultado)
    }
}