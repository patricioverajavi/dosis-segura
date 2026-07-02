package com.example.aplicacionmovil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FdaAdapter extends RecyclerView.Adapter<FdaAdapter.FdaViewHolder> {

    private List<FdaResponse.DrugResult> lista = new ArrayList<>();

    public void actualizarLista(List<FdaResponse.DrugResult> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FdaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fda, parent, false);
        return new FdaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FdaViewHolder holder, int position) {
        FdaResponse.DrugResult drug = lista.get(position);

        // Nombre del medicamento
        if (drug.openfda != null && drug.openfda.brandName != null
                && !drug.openfda.brandName.isEmpty()) {
            holder.txtNombre.setText(drug.openfda.brandName.get(0));
        } else {
            holder.txtNombre.setText("Sin nombre");
        }

        // Fabricante
        if (drug.openfda != null && drug.openfda.manufacturerName != null
                && !drug.openfda.manufacturerName.isEmpty()) {
            holder.txtFabricante.setText(drug.openfda.manufacturerName.get(0));
        } else {
            holder.txtFabricante.setText("Fabricante desconocido");
        }

        // Indicaciones
        if (drug.indicationsAndUsage != null && !drug.indicationsAndUsage.isEmpty()) {
            holder.txtIndicaciones.setText(drug.indicationsAndUsage.get(0));
        } else {
            holder.txtIndicaciones.setText("Sin indicaciones disponibles");
        }
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class FdaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFabricante, txtIndicaciones;

        public FdaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre       = itemView.findViewById(R.id.txtNombreFda);
            txtFabricante   = itemView.findViewById(R.id.txtFabricanteFda);
            txtIndicaciones = itemView.findViewById(R.id.txtIndicacionesFda);
        }
    }
}