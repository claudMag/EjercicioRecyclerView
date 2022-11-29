package com.example.ejerciciorecyclerview.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.ejerciciorecyclerview.MainActivity;
import com.example.ejerciciorecyclerview.R;
import com.example.ejerciciorecyclerview.configuraciones.Constantes;
import com.example.ejerciciorecyclerview.modelos.Producto;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoVH> {

    private List<Producto> objects;
    private int resource;
    private Context context;

    private SharedPreferences spDatos;
    private Gson gson;

    public ProductoAdapter(List<Producto> objects, int resource, Context context) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;

        spDatos = context.getSharedPreferences(Constantes.DATOS, Context.MODE_PRIVATE);
        this.gson = new Gson();
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
        holder.lblNombre.setText(producto.getNombre());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Al presionar sobre un elemento abrirá un AlertDialog que nos mostrará la información
            completa del artículo y este nos permitirá modificar la cantidad o el precio, nunca el
            nombre.*/
                updateProducto(producto, holder.getAdapterPosition()).show();
            }
        });

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarProducto( holder.getAdapterPosition()).show();
            }
        });

        holder.txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int cantidad;
                try {
                    cantidad = Integer.parseInt(editable.toString());
                }catch (NumberFormatException ex){
                    cantidad = 0;
                }
                producto.setCantidad(cantidad);
            }
        });

    }

    private AlertDialog eliminarProducto(int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Quieres eliminar el producto?");
        builder.setCancelable(false);
        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                objects.remove(posicion);
                notifyItemRemoved(posicion);
                guardarDatos();
            }
        });


        return builder.create();
    }

    private AlertDialog updateProducto(Producto producto, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("CREAR PRODUCTO NUEVO");
        builder.setCancelable(false);

        View contenido = LayoutInflater.from(context).inflate(R.layout.add_producto_alert_dialog, null);
        EditText txtNombre = contenido.findViewById(R.id.txtNombreAddProducto);
        EditText txtImporte = contenido.findViewById(R.id.txtPrecioAddProducto);
        EditText txtCantidad = contenido.findViewById(R.id.txtCantidadAddProducto);
        TextView lblTotal = contenido.findViewById(R.id.txtImporteTotalAddProducto);

        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtImporte.setText(String.valueOf(producto.getPrecio()));

        /**
         * Al modificar un cuadro de texto
         * charsequence --> envia el contenido que habia antes del cambio
         */

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            /**
             * al mnodificar un cuadro de texto
             * @param charSequence --> evia el texto actual despues de la modificacion
             */

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            /**
             * se dispara al terminar la modificacion
             * @param editable -> envia el contenido final del cuadro de texto
             */

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    double precio = Double.parseDouble(txtImporte.getText().toString());
                    //para poner el signo del euro o del dolar(dependiendo de tu configuracion)
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                    lblTotal.setText(numberFormat.format(cantidad*precio));
                }catch (NumberFormatException ex){}
            }
        };

        txtCantidad.addTextChangedListener(textWatcher);
        txtImporte.addTextChangedListener(textWatcher);

        builder.setView(contenido);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtImporte.getText().toString().isEmpty()
                        && !txtImporte.getText().toString().isEmpty()
                        && !txtImporte.getText().toString().isEmpty()){
                    Producto producto = new Producto(txtNombre.getText().toString(),
                            Double.parseDouble(txtImporte.getText().toString()),
                            Integer.parseInt(txtCantidad.getText().toString()));
                    objects.set(adapterPosition, producto);
                    notifyItemChanged(adapterPosition);
                    guardarDatos();


                }
                else{
                    Toast.makeText(context, "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private void guardarDatos(){
        String productoS = gson.toJson(objects);
        SharedPreferences.Editor editor = spDatos.edit();
        editor.putString(Constantes.LISTA, productoS);
        editor.apply();
    }

    public class ProductoVH extends RecyclerView.ViewHolder {
        TextView lblNombre;
        EditText txtCantidad;
        ImageButton btnEliminar;
        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            lblNombre = itemView.findViewById(R.id.txtNombreProductoModelView);
            txtCantidad = itemView.findViewById(R.id.txtCantidadProductoModelView);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProductoModelView);
        }
    }
}
