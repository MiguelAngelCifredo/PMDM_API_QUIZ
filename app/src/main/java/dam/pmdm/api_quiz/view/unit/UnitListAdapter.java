package dam.pmdm.api_quiz.view.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.model.Unit;

public class UnitListAdapter extends RecyclerView.Adapter<UnitListAdapter.UnitViewHolder> {

    private final List<Unit> unitList;
    private final OnUnitClickListener listener;

    public interface OnUnitClickListener {
        void onUnitClick(Unit unit);      // Clic corto (Cuestionario o Listado Preguntas)
        void onUnitLongClick(Unit unit);  // Clic largo (Edición)
    }

    public UnitListAdapter(List<Unit> unitList, OnUnitClickListener listener) {
        this.unitList = unitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        Unit currentUnit = unitList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvNombre.setText(currentUnit.getName());
        holder.tvTotal.setText("Preguntas: " + currentUnit.getTotquestions());
        holder.tvTotal.setVisibility(View.VISIBLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onUnitClick(currentUnit);
        });

        holder.itemView.setOnLongClickListener(v -> {
            boolean modoEdicion = prefs.getBoolean("modo_edicion", false);
            if (modoEdicion && listener != null) {
                listener.onUnitLongClick(currentUnit);
                return true;
            }
            return false;
        });

        boolean mostrarMax = prefs.getBoolean("unit_max_calificacion", false);
        if (mostrarMax) {
            float notaMax = prefs.getFloat("max_unit_" + currentUnit.getIdunit(), 0.0f);
            holder.tvMaxCalificacion.setVisibility(View.VISIBLE);
            holder.tvMaxCalificacion.setText(String.format("Máx: %.1f", notaMax));
        } else {
            holder.tvMaxCalificacion.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return unitList != null ? unitList.size() : 0;
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTotal, tvMaxCalificacion;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreUnidad);
            tvTotal = itemView.findViewById(R.id.tvTotalPreguntas);
            tvMaxCalificacion = itemView.findViewById(R.id.tvMaxCalificacion);
        }
    }

}