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
    private OnModuleClickListener listener;

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

        // --- Lógica de Preferencias ---
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean mostrarTotal = !prefs.getBoolean("adj_solo_unidades", false);

        if (mostrarTotal) {
            String msg = "";
            switch (currentModule.getTotunits()) {
                case 0  -> msg = "Sin unidades.";
                case 1  -> msg = "1 Unidad.";
                default -> msg = currentModule.getTotunits() + " Unidades.";
            }
            holder.tvUnidades.setText(msg);
            holder.tvUnidades.setVisibility(View.VISIBLE);
        } else {
            holder.tvUnidades.setVisibility(View.GONE);
        }
        // ------------------------------
        // Lista de colores vibrantes para educación
        int[] colors = {0xFF1ABC9C, 0xFF3498DB, 0xFF9B59B6, 0xFFE67E22, 0xFFE74C3C};
        int color = colors[currentModule.getIdmodule() % colors.length];

        Glide.with(context)
                .load(RetrofitClient.getImageUrl(currentModule.getIdmodule()))
                .skipMemoryCache(true) // Opcional: útil si cambias la imagen y quieres que se refresque
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Opcional: evita que cargue la imagen vieja si se ha editado
                .error(new ColorDrawable(color))       // Si falla, dejamos el color
                .centerCrop()
                .into(holder.imgAsignatura);
        // .placeholder(new ColorDrawable(color)) // Usamos un color liso mientras carga


        // Gestionamos los eventos de clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onModuleClick(currentModule);
        });

        holder.itemView.setOnLongClickListener(v -> {
            // Solo permitir clic largo si el modo edición está activo
            boolean modoEdicion = prefs.getBoolean("modo_edicion", false);

            if (modoEdicion && listener != null) {
                listener.onModuleLongClick(currentModule);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return moduleList != null ? moduleList.size() : 0;
    }

    // --- MÉTODO PARA EL BUSCADOR ---
    public void filterList(List<Module> filteredList) {
        this.moduleList = filteredList;
        notifyDataSetChanged();
    }

    // Clase interna ViewHolder
    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUnidades;
        ImageView imgAsignatura;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAsignatura);
            tvUnidades = itemView.findViewById(R.id.tvTotalUnidades);
            imgAsignatura = itemView.findViewById(R.id.imgAsignatura);
        }
    }
}
