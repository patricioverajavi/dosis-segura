package com.example.aplicacionmovil;

public class MedicamentoValidator {

    // Valida que los campos obligatorios de un medicamento no estén vacíos ni nulos
    public static boolean validarCamposObligatorios(Medicamento medicamento) {
        if (medicamento == null) return false;
        return esTextoValido(medicamento.nombre)
                && esTextoValido(medicamento.dosis)
                && esTextoValido(medicamento.principioActivo);
    }

    // Valida que un texto no sea nulo ni esté vacío (ni solo espacios)
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // Valida que la dosis tenga un formato razonable: al menos un número seguido de una unidad
    // Ejemplos válidos: "500mg", "10 ml", "1.5g"
    public static boolean validarFormatoDosis(String dosis) {
        if (!esTextoValido(dosis)) return false;
        return dosis.trim().matches("^\\d+(\\.\\d+)?\\s?(mg|ml|g|mcg|UI)$");
    }

    // Valida que el nombre no sea solo números (un nombre de medicamento debe tener letras)
    public static boolean validarNombreMedicamento(String nombre) {
        if (!esTextoValido(nombre)) return false;
        return nombre.trim().matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑ]+.*");
    }
}