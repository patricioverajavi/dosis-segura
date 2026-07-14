package com.example.aplicacionmovil;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Importación necesaria para el diálogo
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private List<Medicamento> listaMedicamentos;
    private MedicamentoViewModel viewModel;
    private String categoria;
    private boolean isInvitado;

    public MedicamentoAdapter(MedicamentoViewModel viewModel, ArrayList<Medicamento> lista, String cat, boolean isInvitado) {
        this.viewModel = viewModel;
        this.listaMedicamentos = lista;
        this.categoria = cat;
        this.isInvitado = isInvitado;
    }

    public void actualizarLista(List<Medicamento> nuevaLista) {
        this.listaMedicamentos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento m = listaMedicamentos.get(position);

        holder.txtNombre.setText(m.nombre);
        holder.txtDosis.setText(m.presentacion);

        // 1. Configurar icono estrella y texto
        if (isInvitado) {
            holder.btnEstrella.setImageResource(android.R.drawable.btn_star_big_off);
            holder.txtFavoritoEtiqueta.setText("Guardar");
        } else {
            holder.btnEstrella.setImageResource(m.isFavorito ?
                    android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
            holder.txtFavoritoEtiqueta.setText(m.isFavorito ? "Guardado" : "Guardar");
        }

        // 2. Lógica del botón favorito (contenedor completo para mayor touch target)
        holder.btnFavoritoContainer.setOnClickListener(v -> {
            if (isInvitado) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Acceso Limitado")
                        .setMessage("Los invitados no pueden gestionar favoritos.")
                        .setPositiveButton("Entendido", null)
                        .show();
                return;
            }

            if (m.isFavorito) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Quitar de favoritos")
                        .setMessage("¿Deseas quitar " + m.nombre + " de favoritos?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            m.isFavorito = false;
                            viewModel.update(m);
                            notifyItemChanged(holder.getAdapterPosition());
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                m.isFavorito = true;
                viewModel.update(m);
                notifyItemChanged(holder.getAdapterPosition());
                NotificacionHelper.notificarDirecto(
                        v.getContext(),
                        "⭐ Favorito guardado",
                        m.nombre + " guardado en favoritos."
                );
            }
        });
        // Click normal → abrir formulario (modo ver para invitados o para medicamentos oficiales)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AgregarMedicamentoActivity.class);
            intent.putExtra("medicamento_id", m.id);
            // Si es invitado O es un medicamento oficial, solo se puede ver
            boolean soloVer = isInvitado || m.esOficial;
            intent.putExtra("solo_ver", soloVer); 
            ((android.app.Activity) v.getContext()).startActivity(intent);
        });

        // Long click → diálogo de confirmación eliminar (Bloqueado para invitados y para oficiales)
        holder.itemView.setOnLongClickListener(v -> {
            if (isInvitado) {
                Toast.makeText(v.getContext(), "Invitados no pueden eliminar datos", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (m.esOficial) {
                Toast.makeText(v.getContext(), "No se pueden eliminar medicamentos del catálogo oficial", Toast.LENGTH_SHORT).show();
                return true;
            }
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar medicamento")
                    .setMessage("¿Deseas eliminar " + m.nombre + "?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        viewModel.delete(m);
                        // Snackbar con Deshacer — necesita la Activity
                        android.view.View rootView = ((android.app.Activity) v.getContext())
                                .findViewById(android.R.id.content);
                        com.google.android.material.snackbar.Snackbar
                                .make(rootView, m.nombre + " eliminado",
                                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                                .setAction("Deshacer", sv -> viewModel.insert(m))
                                .show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {

        return listaMedicamentos != null ? listaMedicamentos.size() : 0;
    }

    public static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDosis, txtFavoritoEtiqueta;
        ImageView btnEstrella;
        View btnFavoritoContainer;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDosis = itemView.findViewById(R.id.txtDosis);
            btnEstrella = itemView.findViewById(R.id.btnEstrella);
            txtFavoritoEtiqueta = itemView.findViewById(R.id.txtFavoritoEtiqueta);
            btnFavoritoContainer = itemView.findViewById(R.id.btnFavoritoContainer);
        }
    }
}