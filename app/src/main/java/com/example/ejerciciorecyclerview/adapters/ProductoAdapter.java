package com.example.ejerciciorecyclerview.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejerciciorecyclerview.R;
import com.example.ejerciciorecyclerview.modelos.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoVH> {

    private List<Producto> objects;
    private int resource;
    private Context context;

    public ProductoAdapter(List<Producto> objects, int resource, Context context) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productoView = LayoutInflater.from(context).inflate(resource, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        productoView.setLayoutParams(lp);

        return new ProductoVH(productoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        Producto producto = objects.get(position);
        holder.txtNombre.setText(producto.getNombre());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Al presionar sobre un elemento abrirá un AlertDialog que nos mostrará la información
            completa del artículo y este nos permitirá modificar la cantidad o el precio, nunca el
            nombre.*/
                mostrarModificarProducto(holder.getAdapterPosition()).show();
            }
        });

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarProducto("Quieres eliminar el producto?", holder.getAdapterPosition()).show();
            }
        });

    }

    private AlertDialog eliminarProducto(String texto, int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(texto);
        builder.setCancelable(false);
        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                objects.remove(posicion);
                notifyItemRemoved(posicion);
            }
        });


        return builder.create();
    }

    private AlertDialog mostrarModificarProducto(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("INFORMACIÓN PRODUCTO");
        View contenido = LayoutInflater.from(context).inflate(R.layout.edit_producto_alert_dialog, null);
        TextView txtNombre = contenido.findViewById(R.id.lblNombreEditProducto);
        EditText txtPrecio = contenido.findViewById(R.id.txtPrecioEditProducto);
        EditText txtCantidad = contenido.findViewById(R.id.txtCantidadEditProducto);
        TextView txtImporteTotal = contenido.findViewById(R.id.lblImporteTotalEditProducto);

        txtNombre.setText(objects.get(position).getNombre());
        txtPrecio.setText(String.valueOf(objects.get(position).getPrecio()));
        txtCantidad.setText(String.valueOf(objects.get(position).getCantidad()));
        txtImporteTotal.setText(String.valueOf(objects.get(position).getImporteTotal()));

        builder.setView(contenido);
        builder.setCancelable(false);
        builder.setNegativeButton("CERRAR", null);
        builder.setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Producto producto = new Producto(txtNombre.getText().toString(),
                        Double.parseDouble(txtPrecio.getText().toString()),
                        Integer.parseInt(txtCantidad.getText().toString()));
                objects.set(position, producto);
                notifyItemChanged(position);
            }
        });

        return builder.create();
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ProductoVH extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad;
        ImageButton btnEliminar;
        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProductoModelView);
            txtCantidad = itemView.findViewById(R.id.txtCantidadProductoModelView);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProductoModelView);
        }
    }
}
