package com.example.aplicacionmovil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAnalgesicos, rvAntibioticos, rvVitaminas;
    private TextView txtContadorAnalgesicos, txtContadorAntibioticos, txtContadorVitaminas;
    private ImageButton btnFavoritos;
    private MedicamentoAdapter adapterAnalgesicos, adapterAntibioticos, adapterVitaminas;
    private MedicamentoViewModel viewModel;

    private List<Medicamento> listaCompleta = new ArrayList<>();
    private boolean mostrandoFavoritos = false;
    public static boolean mensajeAccesoMostrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        viewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        inicializarVistas();
        configurarRecyclerViews();
        configurarBotonFavoritos();
        // Programar recordatorio diario
        NotificacionHelper.programarRecordatorioDiario(this);
        // TEMPORAL — solo para probar, borrar después
        NotificacionHelper.probarRecordatorioAhora(this);

        viewModel.getTodos().observe(this, medicamentos -> {
            if (medicamentos != null && medicamentos.isEmpty()) {
                insertarDatosIniciales();
            }
            if (medicamentos != null) {
                listaCompleta = medicamentos;
                actualizarPantalla(listaCompleta);
            }
        });

        configurarInfoUsuario();
    }

    private void inicializarVistas() {
        rvAnalgesicos = findViewById(R.id.rvAnalgesicos);
        rvAntibioticos = findViewById(R.id.rvAntibioticos);
        rvVitaminas = findViewById(R.id.rvVitaminas);
        txtContadorAnalgesicos = findViewById(R.id.txtContadorAnalgesicos);
        txtContadorAntibioticos = findViewById(R.id.txtContadorAntibioticos);
        txtContadorVitaminas = findViewById(R.id.txtContadorVitaminas);
        btnFavoritos = findViewById(R.id.btnFavoritos);

        if ("invitado".equals(getIntent().getStringExtra("tipo_ingreso"))) {
            btnFavoritos.setVisibility(android.view.View.GONE);
        }

        Button btnFda = findViewById(R.id.btnFda);
        if (btnFda != null) {
            btnFda.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, FdaActivity.class));
            });
        }
        findViewById(R.id.btnAgregar).setOnClickListener(v -> {
            if ("invitado".equals(getIntent().getStringExtra("tipo_ingreso"))) {
                new AlertDialog.Builder(this)
                        .setTitle("Acceso Limitado")
                        .setMessage("Los invitados no pueden agregar nuevos medicamentos.")
                        .setPositiveButton("Entendido", null)
                        .show();
            } else {
                startActivity(new Intent(this, AgregarMedicamentoActivity.class));
            }
        });

        findViewById(R.id.btnAgregar).setOnLongClickListener(v -> {
            startActivity(new Intent(this, FdaActivity.class));
            return true;
        });
        findViewById(R.id.btnAjustesPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra("tipo_ingreso", getIntent().getStringExtra("tipo_ingreso"));
            startActivity(intent);
        });

        EditText searchBar = findViewById(R.id.searchBar);
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) { filtrarMedicamentos(s.toString()); }
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void configurarRecyclerViews() {
        rvAnalgesicos.setLayoutManager(new GridLayoutManager(this, 3));
        rvAntibioticos.setLayoutManager(new GridLayoutManager(this, 3));
        rvVitaminas.setLayoutManager(new GridLayoutManager(this, 3));

        boolean esInvitado = "invitado".equals(getIntent().getStringExtra("tipo_ingreso"));

        adapterAnalgesicos = new MedicamentoAdapter(viewModel, new ArrayList<>(), "Analgésicos", esInvitado);
        adapterAntibioticos = new MedicamentoAdapter(viewModel, new ArrayList<>(), "Antibióticos", esInvitado);
        adapterVitaminas = new MedicamentoAdapter(viewModel, new ArrayList<>(), "Vitaminas", esInvitado);

        rvAnalgesicos.setAdapter(adapterAnalgesicos);
        rvAntibioticos.setAdapter(adapterAntibioticos);
        rvVitaminas.setAdapter(adapterVitaminas);
    }

    private void actualizarPantalla(List<Medicamento> lista) {
        List<Medicamento> a = new ArrayList<>(), b = new ArrayList<>(), c = new ArrayList<>();
        for (Medicamento m : lista) {
            if ("Analgésicos".equals(m.categoria)) a.add(m);
            else if ("Antibióticos".equals(m.categoria)) b.add(m);
            else if ("Vitaminas".equals(m.categoria)) c.add(m);
        }
        adapterAnalgesicos.actualizarLista(a);
        adapterAntibioticos.actualizarLista(b);
        adapterVitaminas.actualizarLista(c);
        actualizarContadoresVisibles(a.size(), b.size(), c.size());
    }

    private void actualizarContadoresVisibles(int a, int b, int c) {
        if (txtContadorAnalgesicos != null) txtContadorAnalgesicos.setText(a + " medicamentos");
        if (txtContadorAntibioticos != null) txtContadorAntibioticos.setText(b + " medicamentos");
        if (txtContadorVitaminas != null) txtContadorVitaminas.setText(c + " medicamentos");
    }

    private void filtrarMedicamentos(String texto) {
        String query = texto.toLowerCase();
        List<Medicamento> filtrada = new ArrayList<>();
        for (Medicamento m : listaCompleta) {
            if (m.nombre != null && m.nombre.toLowerCase().contains(query)) filtrada.add(m);
        }
        actualizarPantalla(filtrada);
    }
    private void configurarBotonFavoritos() {
        btnFavoritos.setOnClickListener(v -> {
            mostrandoFavoritos = !mostrandoFavoritos;
            btnFavoritos.setImageResource(mostrandoFavoritos ?
                    android.R.drawable.btn_star_big_on :
                    android.R.drawable.btn_star_big_off);
            if (mostrandoFavoritos) {
                List<Medicamento> favs = new ArrayList<>();
                for (Medicamento m : listaCompleta) if (m.isFavorito) favs.add(m);
                actualizarPantalla(favs);
            } else {
                actualizarPantalla(listaCompleta);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("invitado".equals(getIntent().getStringExtra("tipo_ingreso")) && !mensajeAccesoMostrado) {
            pedirCedulaInvitado();
        }
    }

    private void pedirCedulaInvitado() {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Acceso Limitado").setMessage("Ingrese su cédula:")
                .setView(input).setPositiveButton("Validar", null).setCancelable(false).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (input.getText().toString().length() == 10) {
                mensajeAccesoMostrado = true;
                dialog.dismiss();
            } else { input.setError("10 dígitos requeridos"); }
        });
    }

    private void configurarInfoUsuario() {
        TextView txt = findViewById(R.id.txtNombreUsuario);
        String tipo = getIntent().getStringExtra("tipo_ingreso");
        txt.setText("invitado".equals(tipo) ? "Invitado" : (getIntent().getStringExtra("nombre_usuario") != null ? getIntent().getStringExtra("nombre_usuario") : "Patricio Vera"));
    }

    private void insertarDatosIniciales() {
// --- ANALGÉSICOS ---
        viewModel.insert(new Medicamento("Paracetamol 500mg", "Alivio eficaz del dolor leve a moderado y la fiebre.", "Caja con 20 tabletas", "500mg cada 6-8 horas", "Hipersensibilidad, insuficiencia hepática grave.", "• No exceder la dosis máxima diaria recomendada.\n• Evitar el consumo simultáneo de alcohol.", "Paracetamol", "Laboratorios Pharma SA", "• Dolor de cabeza\n• Dolor muscular\n• Fiebre\n• Dolor dental", "Analgésicos", false));
        viewModel.insert(new Medicamento("Ibuprofeno 400mg", "Reduce eficazmente la inflamación muscular.", "Caja con 24 tabletas", "400mg cada 8 horas", "Úlcera gástrica activa, insuficiencia renal.", "• Tomar con alimentos.\n• No usar por más de 5 días.", "Ibuprofeno", "Genfar S.A.", "• Inflamación articular\n• Dolor de espalda\n• Dolores menstruales", "Analgésicos", false));
        viewModel.insert(new Medicamento("Aspirina 500mg", "Alivio del dolor de cabeza y protector cardíaco.", "Caja con 30 tabletas", "500mg cada 8 horas", "Úlcera péptica, asma grave, sangrado activo.", "• No administrar en niños por riesgo de Síndrome de Reye.", "Ácido Acetilsalicílico", "Bayer", "• Migraña\n• Fiebre\n• Antigregante plaquetario", "Analgésicos", false));
        viewModel.insert(new Medicamento("Ketorolaco 10mg", "Tratamiento a corto plazo del dolor moderado a severo.", "Caja con 10 tabletas", "10mg cada 6 horas", "Hipersensibilidad, úlcera gastroduodenal, hemorragias.", "• No usar por más de 5 días consecutivos.", "Ketorolaco Trometamina", "La Santé", "• Dolor postoperatorio\n• Dolor dental severo", "Analgésicos", false));
        viewModel.insert(new Medicamento("Naproxeno 500mg", "Dolores e inflamación articular prolongada.", "Caja con 15 tabletas", "500mg cada 12 horas", "Insuficiencia hepática o renal severa.", "• Puede causar molestias estomacales.", "Naproxeno Sódico", "Chalver", "• Artritis\n• Tendinitis\n• Esguinces", "Analgésicos", false));
        viewModel.insert(new Medicamento("Diclofenaco 50mg", "Alivio de dolores reumáticos agudos e inflamación.", "Caja con 20 tabletas", "50mg cada 8-12 horas", "Úlcera activa, embarazo avanzado.", "• Tomar acompañado de protectores gástricos.", "Diclofenaco Sódico", "Mk", "• Reumatismo\n• Lumbalgia\n• Inflamación postraumática", "Analgésicos", false));
        viewModel.insert(new Medicamento("Tramadol 50mg", "Analgésico potente para dolores moderados a severos.", "Caja con 10 cápsulas", "50mg cada 6-8 horas", "Intoxicación por alcohol o psicótropos.", "• Puede generar dependencia o mareos severos.", "Clorhidrato de Tramadol", "Grunenthal", "• Dolor oncológico\n• Dolores neuropáticos", "Analgésicos", false));
        viewModel.insert(new Medicamento("Meloxicam 15mg", "Antiinflamatorio potente para problemas osteoarticulares.", "Caja con 10 tabletas", "15mg una vez al día", "Embarazo, lactancia, insuficiencia cardíaca grave.", "• Tomar siempre a la misma hora.", "Meloxicam", "Tecnoquímicas", "• Osteoartritis\n• Artritis reumatoidea", "Analgésicos", false));
        viewModel.insert(new Medicamento("Clonixinato 125mg", "Alivio de dolores agudos leves a moderados del tejido blando.", "Caja con 20 tabletas", "125mg cada 6-8 horas", "Úlcera gastroduodenal activa, sangrado digestivo.", "• No ingerir en ayunas.", "Clonixinato de Lisina", "Roemmers", "• Espasmos musculares\n• Dolor postparto", "Analgésicos", false));
        viewModel.insert(new Medicamento("Ibuprofeno 600mg", "Antiinflamatorio no esteroideo de alta concentración.", "Caja con 20 tabletas", "600mg cada 12 horas", "Insuficiencia cardíaca, úlcera péptica activa.", "• Ingerir con abundante líquido después del almuerzo.", "Ibuprofeno", "Laboratorios Rocnarf", "• Artritis severa\n• Dolor postquirúrgico dental\n• Traumatismos", "Analgésicos", false));
        viewModel.insert(new Medicamento("Metamizol 500mg", "Potente analgésico antiespasmódico y antipirético.", "Caja con 10 tabletas", "500mg cada 8 horas", "Agranulocitosis, insuficiencia de glucosa-6-fosfato.", "• No usar de manera prolongada sin control hemático.", "Metamizol Sódico", "Grupo Farma", "• Fiebre alta refractaria\n• Cólicos biliares\n• Dolores postoperatorios", "Analgésicos", false));
        viewModel.insert(new Medicamento("Piroxicam 20mg", "Analgésico prolongado indicado para afecciones reumáticas.", "Caja con 10 cápsulas", "20mg una vez al día", "Hipersensibilidad, antecedentes de sangrado digestivo.", "• Alta toxicidad gastrointestinal si se excede el uso.", "Piroxicam", "Laboratorios Life", "• Gota aguda\n• Espondilitis anquilosante\n• Dismenorrea", "Analgésicos", false));
        viewModel.insert(new Medicamento("Celecoxib 200mg", "Inhibidor selectivo de la COX-2 que protege la mucosa estomacal.", "Caja con 10 tabletas", "200mg una vez al día", "Alergia a las sulfonamidas, bypass coronario reciente.", "• Usar con precaución en pacientes con hipertensión arterial.", "Celecoxib", "Pfizer", "• Alivio del dolor crónico\n• Artritis degenerativa", "Analgésicos", false));
        viewModel.insert(new Medicamento("Nimesulida 100mg", "Analgésico antiinflamatorio con rápida acción sobre el dolor.", "Caja con 12 tabletas", "100mg cada 12 horas", "Disfunción o insuficiencia hepática activa.", "• Tratamiento limitado a un máximo de 15 días.", "Nimesulida", "Neofármaco", "• Dolor agudo generalizado\n• Inflamación de vías aéreas superiores", "Analgésicos", false));
        viewModel.insert(new Medicamento("Ketoprofeno 100mg", "Potente inhibidor de la inflamación y dolor somático.", "Caja con 10 tabletas", "100mg cada 12 horas", "Asma inducida por aspirina, insuficiencia renal grave.", "• Tomar siempre acompañado por comidas.", "Ketoprofeno", "Sanofi", "• Crisis de migraña aguda\n• Dolores reumáticos corporales", "Analgésicos", false));

// --- ANTIBIÓTICOS ---
        viewModel.insert(new Medicamento("Amoxicilina 500mg", "Antibiótico para combatir infecciones bacterianas.", "Caja con 21 cápsulas", "500mg cada 8 horas por 7 días", "Alergia verificada a las penicilinas.", "• Cumplir el tratamiento completo.", "Amoxicilina", "Bayer HealthCare", "• Infecciones de garganta\n• Sinusitis bacteriana\n• Otitis media", "Antibióticos", false));
        viewModel.insert(new Medicamento("Azitromicina 500mg", "Tratamiento bacteriano rápido de amplio espectro.", "Caja con 3 tabletas", "500mg una vez al día por 3 días", "Hipersensibilidad a macrólidos.", "• Tomar 1 hora antes o 2 horas después de comer.", "Azitromicina", "Pfizer", "• Amigdalitis\n• Infecciones de la piel\n• Neumonía", "Antibióticos", false));
        viewModel.insert(new Medicamento("Ciprofloxacino 500mg", "Eficaz contra infecciones del tracto urinario y digestivo.", "Caja con 10 tabletas", "500mg cada 12 horas por 5 días", "Alergia a quinolonas.", "• Evitar exposición prolongada al sol.", "Clorhidrato de Ciprofloxacino", "Chile", "• Infecciones urinarias\n• Gastroenteritis severa", "Antibióticos", false));
        viewModel.insert(new Medicamento("Cefalexina 500mg", "Cefalosporina para infecciones en la piel y tejidos blandos.", "Caja con 20 cápsulas", "500mg cada 6 horas por 7 días", "Hipersensibilidad a las cefalosporinas.", "• Administrar con precaución en falla renal.", "Cefalexina Monohidrato", "Kronos", "• Infecciones cutáneas\n• Faringitis estreptocócica", "Antibióticos", false));
        viewModel.insert(new Medicamento("Claritromicina 500mg", "Indicado en infecciones del tracto respiratorio superior.", "Caja con 10 tabletas", "500mg cada 12 horas por 7 días", "Antecedentes de prolongación del intervalo QT.", "• Tomar con o sin alimentos.", "Claritromicina", "Abbott", "• Bronquitis crónica\n• Erradicación de H. pylori", "Antibióticos", false));
        viewModel.insert(new Medicamento("Amoxicilina + Clavulánico", "Infecciones bacterianas complejas resistentes.", "Caja con 14 tabletas", "875mg/125mg cada 12 horas", "Historial de ictericia colestásica.", "• Tomar al inicio de una comida.", "Amoxicilina + Ácido Clavulánico", "GlaxoSmithKline", "• Neumonía\n• Infecciones por mordeduras\n• Sinusitis", "Antibióticos", false));
        viewModel.insert(new Medicamento("Doxiciclina 100mg", "Antibiótico tetraciclínico para infecciones variadas.", "Caja con 10 tabletas", "100mg cada 12 horas después de comer", "Embarazo, lactancia, niños menores de 8 años.", "• No acostarse inmediatamente después de tomar la cápsula.", "Doxiciclina Hiclato", "Genfar", "• Acné severo\n• Infecciones por clamidia\n• Profilaxis de malaria", "Antibióticos", false));
        viewModel.insert(new Medicamento("Nitrofurantoína 100mg", "Antiséptico de vías urinarias de alta concentración.", "Caja con 40 cápsulas", "100mg cada 6 horas por 7 días", "Anuria, oliguria o aclaramiento renal deficiente.", "• Puede cambiar el color de la orina a marrón obscuro.", "Nitrofurantoína Macrocristales", "Laboratorios James Brown", "• Cistitis aguda\n• Prevención de infecciones urinarias recurrentes", "Antibióticos", false));
        viewModel.insert(new Medicamento("Levofloxacino 500mg", "Fluoroquinolona potente para vías respiratorias bajas.", "Caja con 7 tabletas", "500mg una vez al día por 7 días", "Epilepsia, problemas previos en tendones por quinolonas.", "• Tomar con abundante agua durante todo el tratamiento.", "Levofloxacino", "Sandoz", "• Neumonía comunitaria\n• Infecciones renales severas\n• Sinusitis crónica agudizada", "Antibióticos", false));
        viewModel.insert(new Medicamento("Metronidazol 500mg", "Antibacteriano antiparasitario para infecciones anaerobias.", "Caja con 20 tabletas", "500mg cada 8 horas durante las comidas", "Primer trimestre del embarazo, afecciones neurológicas.", "• PROHIBIDO el alcohol absoluto (Efecto Antabus severo).", "Metronidazol", "Laboratorios MK", "• Amebiasis intestinal\n• Giardiasis\n• Infecciones ginecológicas anaeróbicas", "Antibióticos", false));
        viewModel.insert(new Medicamento("Cefuroxima 250mg", "Cefalosporina de segunda generación de amplio espectro.", "Caja con 10 tabletas", "250mg cada 12 horas por 7 días", "Alergia severa cruzada a la penicilina.", "• Absorción óptima si se ingiere tras las comidas.", "Cefuroxima Axetilo", "GlaxoSmithKline", "• Faringitis\n• Amigdalitis bacteriana\n• Infecciones de la piel", "Antibióticos", false));
        viewModel.insert(new Medicamento("Sulfametoxazol + Trim", "Combinación sulfamida de amplio espectro metabólico.", "Caja con 20 tabletas", "800mg/160mg cada 12 horas", "Insuficiencia hepática severa, anemia megaloblástica.", "• Mantener una buena hidratación para evitar cristales urinarios.", "Sulfametoxazol + Trimetoprima", "Laboratorios Life", "• Infecciones gastrointestinales\n• Neumonía por Pneumocystis", "Antibióticos", false));

// --- VITAMINAS ---
        viewModel.insert(new Medicamento("Vitamina C 1g", "Suplemento vitamínico que potencia el sistema inmune.", "Caja con 20 tabletas", "1 tableta efervescente al día", "Cálculos renales recurrentes.", "• No disolver en agua caliente.", "Ácido Ascórbico", "Calox International", "• Prevención de resfriados\n• Estados de fatiga", "Vitaminas", false));
        viewModel.insert(new Medicamento("Complejo B", "Optimiza el rendimiento físico y el sistema nervioso.", "Caja con 30 cápsulas", "1 cápsula por la mañana", "Hipersensibilidad a los componentes.", "• Puede teñir la orina de un color amarillo.", "Vitaminas B1, B6, B12", "Bago", "• Neuritis\n• Anemia megaloblástica\n• Cansancio físico", "Vitaminas", false));
        viewModel.insert(new Medicamento("Vitamina D3 2000 UI", "Soporte vital para la absorción de calcio y salud ósea.", "Caja con 30 tabletas", "1 tableta al día con la comida principal", "Hipercalcemia, hipervitaminosis D.", "• Controlar niveles de calcio en tratamientos largos.", "Colecalciferol", "Sandoz", "• Deficiencia de vitamina D\n• Prevención de osteoporosis", "Vitaminas", false));
        viewModel.insert(new Medicamento("Multivitamínico A-Z", "Aporte completo de micronutrientes diarios esenciales.", "Frasco con 60 gomitas", "2 gomitas masticables al día", "Hipervitaminosis previa.", "• Masticar completamente antes de tragar.", "Polivitamínico con Minerales", "Centrum", "• Suplementación nutricional\n• Convalecencia", "Vitaminas", false));
        viewModel.insert(new Medicamento("Vitamina E 400 UI", "Protección antioxidante celular para piel y tejidos.", "Caja con 30 cápsulas", "1 cápsula blanda al día", "Hipoprothrombinemia por déficit de Vitamina K.", "• No combinar con anticoagulantes sin supervisión.", "Acetato de Tocoferol", "Nature's Bounty", "• Antienvejecimiento celular\n• Déficit nutricional", "Vitaminas", false));
        viewModel.insert(new Medicamento("Calcio + Magnesio", "Fortalecimiento estructural del sistema óseo y muscular.", "Frasco con 90 tabletas", "1 tableta dos veces al día", "Hipercalciuria severa, insuficiencia renal grave.", "• Tomar con un vaso lleno de agua.", "Carbonato de Calcio + Óxido de Magnesio", "GNC", "• Prevención de osteopenia\n• Suplemento en crecimiento", "Vitaminas", false));
        viewModel.insert(new Medicamento("Ácido Fólico 5mg", "Suplemento esencial celular para la síntesis del ADN.", "Caja con 30 tabletas", "1 tableta al día antes del desayuno", "Anemia perniciosa no tratada.", "• Crucial durante la planificación del embarazo.", "Ácido Fólico", "Laboratorios Rocnarf", "• Prevención de defectos del tubo neural\n• Tratamiento de anemias", "Vitaminas", false));
        viewModel.insert(new Medicamento("Suplemento de Zinc 50mg", "Mineral esencial para la regeneración tisular e inmunidad.", "Frasco con 60 tabletas", "1 tableta diaria junto con alimentos.", "Hipersensibilidad o deficiencia severa de cobre.", "• Ingerir con comida para evitar náuseas transitorias.", "Gluconato de Zinc", "Mason Natural", "• Cicatrización de tejidos\n• Refuerzo de defensas biológicas", "Vitaminas", false));
        viewModel.insert(new Medicamento("Sulfato Ferroso 200mg", "Restaurador concentrado de los niveles de hierro sanguíneo.", "Caja con 30 tabletas", "1 tableta diaria en ayunas con jugo de naranja.", "Hemosiderosis, hemocromatosis, anemias hemolíticas.", "• Puede causar estreñimiento y oscurecer las heces.", "Sulfato Ferroso Deshidratado", "Genfar", "• Tratamiento de anemia ferropénica\n• Suplemento nutricional", "Vitaminas", false));
        viewModel.insert(new Medicamento("Biotina 10000 mcg", "Vitamina hidrosoluble que apoya estructuras queratínicas.", "Frasco con 100 cápsulas", "1 cápsula al día con un vaso de agua.", "Hipersensibilidad conocida a la biotina.", "• Puede interferir con ciertas pruebas de laboratorio tiroideas.", "Biotina (Vitamina B7)", "Healthy America", "• Fortalecimiento de cabello quebradizo\n• Fragilidad ungueal", "Vitaminas", false));
        viewModel.insert(new Medicamento("Vitamina A 10000 UI", "Protección de tejidos epiteliales y salud ocular nocturna.", "Frasco con 50 cápsulas blandas", "1 cápsula al día de forma intermitente.", "Embarazo establecido (riesgo teratogénico), hipervitaminosis A.", "• No consumir dosis altas por tiempos prolongados.", "Palmitato de Retinilo", "Whitehall", "• Prevención de ceguera nocturna\n• Tratamiento de acné y piel seca", "Vitaminas", false));
        viewModel.insert(new Medicamento("Colágeno Hidrolizado + C", "Complejo regenerador para articulaciones, ligamentos y piel.", "Frasco con 90 tabletas", "2 tabletas tres veces al día con el estómago vacío.", "Hipersensibilidad a proteínas animales.", "• Mantener una ingesta diaria constante para notar cambios.", "Colágeno + Ácido Ascórbico", "Nature's Blend", "• Elasticidad de la piel\n• Soporte articular y del cartílago", "Vitaminas", false));

    }}