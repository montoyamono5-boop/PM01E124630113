package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ContactoAdapter;
import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.model.Contacto;

import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    private RecyclerView rvContactos;
    private EditText etSearch;
    private ImageView imgActionAvatar;
    private TextView tvActionNombre, tvActionTelefono;

    private DatabaseHelper dbHelper;
    private ContactoAdapter adapter;
    private Contacto selectedContacto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupSearch();

        findViewById(R.id.btnCatalogBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnActionActualizar).setOnClickListener(v -> actualizarContacto());
        findViewById(R.id.btnActionEliminar).setOnClickListener(v -> eliminarContacto());
        findViewById(R.id.btnActionCompartir).setOnClickListener(v -> compartirContacto());
        findViewById(R.id.btnActionVerFicha).setOnClickListener(v -> verFichaContacto());
        findViewById(R.id.btnLlamarAhora).setOnClickListener(v -> irAVentanaLlamada());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos();
    }

    private void initViews() {
        rvContactos = findViewById(R.id.rvContactos);
        etSearch = findViewById(R.id.etSearch);
        imgActionAvatar = findViewById(R.id.imgActionAvatar);
        tvActionNombre = findViewById(R.id.tvActionNombre);
        tvActionTelefono = findViewById(R.id.tvActionTelefono);
    }

    private void setupRecyclerView() {
        rvContactos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactoAdapter(contacto -> {
            selectedContacto = contacto;
            actualizarPanelAcciones();
        });
        rvContactos.setAdapter(adapter);
    }

    private void cargarContactos() {
        List<Contacto> lista = dbHelper.getAllContactos();
        adapter.setContactos(lista);

        if (!lista.isEmpty()) {
            if (selectedContacto == null) {
                selectedContacto = lista.get(0);
                adapter.setSelectedPosition(0);
            } else {
                // Verificar si el contacto seleccionado sigue existiendo
                Contacto actualizado = dbHelper.getContactoById(selectedContacto.getId());
                if (actualizado != null) {
                    selectedContacto = actualizado;
                } else {
                    selectedContacto = lista.get(0);
                    adapter.setSelectedPosition(0);
                }
            }
            actualizarPanelAcciones();
        } else {
            selectedContacto = null;
            tvActionNombre.setText(R.string.sin_contactos);
            tvActionTelefono.setText(R.string.ver_catalogo);
            imgActionAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                List<Contacto> resultados = dbHelper.searchContactos(query);
                adapter.setContactos(resultados);
                if (!resultados.isEmpty()) {
                    selectedContacto = resultados.get(0);
                    adapter.setSelectedPosition(0);
                    actualizarPanelAcciones();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void actualizarPanelAcciones() {
        if (selectedContacto != null) {
            tvActionNombre.setText(selectedContacto.getNombre());
            tvActionTelefono.setText(selectedContacto.getNumeroCompleto());

            if (selectedContacto.getImagen() != null && selectedContacto.getImagen().length > 0) {
                Bitmap bmp = BitmapFactory.decodeByteArray(selectedContacto.getImagen(), 0, selectedContacto.getImagen().length);
                imgActionAvatar.setImageBitmap(bmp);
            } else {
                imgActionAvatar.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private boolean tieneSeleccion() {
        if (selectedContacto == null) {
            Toast.makeText(this, "Por favor seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void actualizarContacto() {
        if (!tieneSeleccion()) return;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("contacto_editar", selectedContacto);
        startActivity(intent);
    }

    private void eliminarContacto() {
        if (!tieneSeleccion()) return;

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Contacto")
                .setMessage("¿Desea eliminar a " + selectedContacto.getNombre() + " de la base de datos?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    int rows = dbHelper.deleteContacto(selectedContacto.getId());
                    if (rows > 0) {
                        Toast.makeText(this, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show();
                        selectedContacto = null;
                        cargarContactos();
                    } else {
                        Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void compartirContacto() {
        if (!tieneSeleccion()) return;

        String shareText = "Contacto: " + selectedContacto.getNombre() + "\n" +
                "Teléfono: " + selectedContacto.getNumeroCompleto() + "\n" +
                "País: " + selectedContacto.getPais();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Compartir contacto vía");
        startActivity(shareIntent);
    }

    private void verFichaContacto() {
        if (!tieneSeleccion()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ver_ficha, null);

        ImageView imgFicha = dialogView.findViewById(R.id.imgFichaFull);
        TextView tvNombreFicha = dialogView.findViewById(R.id.tvFichaNombre);
        TextView tvTelefonoFicha = dialogView.findViewById(R.id.tvFichaTelefono);
        TextView tvPaisFicha = dialogView.findViewById(R.id.tvFichaPais);
        TextView tvNotaFicha = dialogView.findViewById(R.id.tvFichaNota);

        tvNombreFicha.setText(selectedContacto.getNombre());
        tvTelefonoFicha.setText(selectedContacto.getNumeroCompleto());
        tvPaisFicha.setText(getString(R.string.pais_label, selectedContacto.getPais()));

        if (selectedContacto.getNota() != null && !selectedContacto.getNota().isEmpty()) {
            tvNotaFicha.setText(selectedContacto.getNota());
        } else {
            tvNotaFicha.setText(R.string.nota);
        }

        if (selectedContacto.getImagen() != null && selectedContacto.getImagen().length > 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(selectedContacto.getImagen(), 0, selectedContacto.getImagen().length);
            imgFicha.setImageBitmap(bmp);
        } else {
            imgFicha.setImageResource(R.drawable.ic_person);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void irAVentanaLlamada() {
        if (!tieneSeleccion()) return;
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("contacto_call", selectedContacto);
        startActivity(intent);
    }
}
