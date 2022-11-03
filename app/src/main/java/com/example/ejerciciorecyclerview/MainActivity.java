package com.example.ejerciciorecyclerview;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;

import com.example.ejerciciorecyclerview.adapters.ProductoAdapter;
import com.example.ejerciciorecyclerview.modelos.Producto;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;


import com.example.ejerciciorecyclerview.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    /*El programa deberá mostrar en la Actividad principal (Basic Activity) el listado de los
productos, mostrando su nombre y cantidad.
Al presionar sobre un elemento abrirá un AlertDialog que nos mostrará la información
completa del artículo y este nos permitirá modificar la cantidad o el precio, nunca el
nombre.
El card deberá contener además un botón en forma de icono de papelera para
eliminar el producto, SIEMPRE TRAS CONFIRMACIÓN DEL USUARIO.
Como extra se pide implementar una funcionalidad sobre el elemento para poder
modificar la cantidad de elementos directamente desde el RecyclerView
El producto se creará con una nueva AlertDialog que se lanzará como acción del
Floating Action Button.*/
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

        adapter = new ProductoAdapter(listaProductos, R.layout.producto_view_model, MainActivity.this);
        binding.contentMain.contenedor.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(MainActivity.this);
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
        builder.setTitle("CREAR PRODUCTO NUEVO");

        View contenido = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_producto_alert_dialog, null);
        EditText txtNombre = contenido.findViewById(R.id.txtNombreAddProducto);
        EditText txtImporte = contenido.findViewById(R.id.txtPrecioAddProducto);
        EditText txtCantidad = contenido.findViewById(R.id.txtCantidadAddProducto);

        builder.setView(contenido);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Producto producto = new Producto(txtNombre.getText().toString(),
                        Double.parseDouble(txtImporte.getText().toString()),
                        Integer.parseInt(txtCantidad.getText().toString()));
                listaProductos.add(producto);
                adapter.notifyDataSetChanged();
            }
        });

        return builder.create();

    }

}
