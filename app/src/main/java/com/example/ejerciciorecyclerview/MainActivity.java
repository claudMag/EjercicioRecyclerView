package com.example.ejerciciorecyclerview;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;

import com.example.ejerciciorecyclerview.adapters.ProductoAdapter;
import com.example.ejerciciorecyclerview.modelos.Producto;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;


import com.example.ejerciciorecyclerview.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<Producto> listaProductos;
    private ProductoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        listaProductos = new ArrayList<>();

        int columnas;
        //HORIZONTAL --> 2
        //VERTICAL --> 1
        //DESDE LAS CONFIGURACIONES DE LA ACTIVIDAD --> orientation //PORTRAIT(V)/ LANDSCAPE(H)

        columnas = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;

        adapter = new ProductoAdapter(listaProductos, R.layout.producto_view_model, this);
        layoutManager = new GridLayoutManager(this,columnas);
        binding.contentMain.contenedor.setAdapter(adapter);
        binding.contentMain.contenedor.setLayoutManager(layoutManager);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProducto().show();
            }
        });

    }


    private AlertDialog crearProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.alert_title_crear));
        builder.setCancelable(false);

        View contenido = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_producto_alert_dialog, null);
        EditText txtNombre = contenido.findViewById(R.id.txtNombreAddProducto);
        EditText txtImporte = contenido.findViewById(R.id.txtPrecioAddProducto);
        EditText txtCantidad = contenido.findViewById(R.id.txtCantidadAddProducto);
        TextView lblTotal = contenido.findViewById(R.id.txtImporteTotalAddProducto);

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

        builder.setNegativeButton(R.string.btn_alert_cancel, null);
        builder.setPositiveButton(R.string.btn_alert_crear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!txtImporte.getText().toString().isEmpty()
                && !txtImporte.getText().toString().isEmpty()
                && !txtImporte.getText().toString().isEmpty()){
                    Producto producto = new Producto(txtNombre.getText().toString(),
                            Double.parseDouble(txtImporte.getText().toString()),
                            Integer.parseInt(txtCantidad.getText().toString()));
                    listaProductos.add(0,producto);
                    adapter.notifyItemInserted(0);
                }
                else{
                    Toast.makeText(MainActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();

    }

    /**
     * Se dispara ANTES de que se elimine la actividad
     * @param outState -> guardo los datos
     */

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //para insertar la lista en este bundle el producto tiene que ser serializable
        super.onSaveInstanceState(outState);
        outState.putSerializable("LISTA", listaProductos);
    }

    /**
     * Se dispara DESPUÉS de crear la actividad de nuevo
     * @param savedInstanceState -> recupero los datos
     */

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Producto> temp = (ArrayList<Producto>) savedInstanceState.getSerializable("LISTA");
        listaProductos.addAll(temp);
        adapter.notifyItemRangeInserted(0, listaProductos.size());
    }
}
