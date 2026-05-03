package dam.pmdm.api_quiz.view.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import dam.pmdm.api_quiz.R;
import dam.pmdm.api_quiz.controller.RetrofitClient;
import dam.pmdm.api_quiz.model.Module;

public class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ModuleViewHolder> {

    private List<Module> moduleList;
    private final OnModuleClickListener listener;

    // Interfaz para manejar los clics desde la MainActivity
    public interface OnModuleClickListener {
        void onModuleClick(Module module);      // Clic normal para ver unidades

        void onModuleLongClick(Module module);  // Clic largo para borrar (Modo Edición)
    }

    public ModuleListAdapter(List<Module> moduleList, OnModuleClickListener listener) {
        this.moduleList = moduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module currentModule = moduleList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvNombre.setText(currentModule.getName());

        int totalReal = currentModule.getTotunits();
        String msg = "";
        switch (totalReal) {
            case 0 -> msg = "Sin unidades.";
            case 1 -> msg = "1 Unidad.";
            default -> msg = totalReal + " Unidades.";
        }
        holder.tvUnidades.setText(msg);

        // Carga de imagen con Glide
        int[] colors = {0xFF1ABC9C, 0xFF3498DB, 0xFF9B59B6, 0xFFE67E22, 0xFFE74C3C};
        int color = colors[currentModule.getIdmodule() % colors.length];

        Glide.with(context)
                .load(RetrofitClient.getImageUrl(currentModule.getIdmodule()))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(new ColorDrawable(color))
                .centerCrop()
                .into(holder.imgAsignatura);

        // Eventos de clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onModuleClick(currentModule);
        });

        holder.itemView.setOnLongClickListener(v -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean modoEdicion = prefs.getBoolean("modo_edicion", false);
            if (modoEdicion && listener != null) {
                listener.onModuleLongClick(currentModule);
                return true;
            }
            return false;
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean mostrarMedia = prefs.getBoolean("adj_calificacion_media", false);

        if (mostrarMedia) {
            float mediaAsignatura = prefs.getFloat("media_asig_" + currentModule.getIdmodule(), 0.0f);
            holder.tvMediaAsignatura.setVisibility(View.VISIBLE);
            holder.tvMediaAsignatura.setText(String.format("Media: %.1f", mediaAsignatura));
        } else {
            holder.tvMediaAsignatura.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return moduleList != null ? moduleList.size() : 0;
    }

    public void filterList(List<Module> filteredList) {
        this.moduleList = filteredList;
        notifyDataSetChanged();
    }

    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUnidades, tvMediaAsignatura;
        ImageView imgAsignatura;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAsignatura);
            tvUnidades = itemView.findViewById(R.id.tvTotalUnidades);
            tvMediaAsignatura =  itemView.findViewById(R.id.tvMediaAsignatura);
            imgAsignatura = itemView.findViewById(R.id.imgAsignatura);
        }
    }

}