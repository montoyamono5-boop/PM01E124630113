package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.model.Contacto;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Spinner spinnerPais;
    private EditText etNombre, etTelefono, etNota;
    private ImageView imgContacto;
    private LinearLayout layoutPlaceholder;

    private byte[] currentImageBytes = null;
    private int editingContactoId = -1;
    private DatabaseHelper dbHelper;

    // RegEx para validaciones estrictas
    private static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    private static final String REGEX_TELEFONO = "^[0-9\\s-]+$";

    private final String[] paisesArray = new String[]{
            "Honduras (+504)",
            "Guatemala (+502)",
            "El Salvador (+503)",
            "Nicaragua (+505)",
            "Costa Rica (+506)",
            "Panamá (+507)",
            "México (+52)",
            "Estados Unidos (+1)",
            "Colombia (+57)",
            "España (+34)"
    };

    // ActivityResultLaunchers para Cámara y Galería
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupSpinner();
        registerImageLaunchers();
        checkEditMode();

        findViewById(R.id.containerImage).setOnClickListener(v -> mostrarOpcionesImagen());
        findViewById(R.id.btnGuardarFicha).setOnClickListener(v -> guardarFicha());
        findViewById(R.id.btnVerCatalogo).setOnClickListener(v -> abrirCatalogo());
        findViewById(R.id.btnHeaderBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerPais = findViewById(R.id.spinnerPais);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);
        imgContacto = findViewById(R.id.imgContacto);
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, paisesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);
    }

    @SuppressWarnings("deprecation")
    private void registerImageLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.get("data") != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            setBitmapToView(imageBitmap);
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                setBitmapToView(bitmap);
                            } catch (Exception e) {
                                Log.e(TAG, "Error al cargar la imagen de galeria", e);
                                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void setBitmapToView(Bitmap bitmap) {
        if (bitmap != null) {
            imgContacto.setImageBitmap(bitmap);
            layoutPlaceholder.setVisibility(View.GONE);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
            currentImageBytes = stream.toByteArray();
        }
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar Foto con Cámara", "Seleccionar de Galería"};
        new AlertDialog.Builder(this)
                .setTitle("Capturar Imagen")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(intentCamera);
                    } else {
                        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(intentGallery);
                    }
                })
                .show();
    }

    @SuppressWarnings("deprecation")
    private void checkEditMode() {
        if (getIntent().hasExtra("contacto_editar")) {
            Contacto contacto = (Contacto) getIntent().getSerializableExtra("contacto_editar");
            if (contacto != null) {
                editingContactoId = contacto.getId();
                etNombre.setText(contacto.getNombre());
                etTelefono.setText(contacto.getTelefono());
                etNota.setText(contacto.getNota());

                // Seleccionar país en el spinner
                if (contacto.getPais() != null) {
                    for (int i = 0; i < paisesArray.length; i++) {
                        if (paisesArray[i].equalsIgnoreCase(contacto.getPais())) {
                            spinnerPais.setSelection(i);
                            break;
                        }
                    }
                }

                // Cargar imagen
                if (contacto.getImagen() != null && contacto.getImagen().length > 0) {
                    currentImageBytes = contacto.getImagen();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(currentImageBytes, 0, currentImageBytes.length);
                    imgContacto.setImageBitmap(bitmap);
                    layoutPlaceholder.setVisibility(View.GONE);
                }
            }
        }
    }

    private void guardarFicha() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();
        String pais = spinnerPais.getSelectedItem().toString();

        // Validaciones
        if (nombre.isEmpty()) {
            mostrarAlertaError("Validación de Campo", "El nombre es obligatorio.");
            return;
        }

        if (!Pattern.matches(REGEX_NOMBRE, nombre)) {
            mostrarAlertaError("Error de Formato", "El nombre solo debe contener letras y espacios.");
            return;
        }

        if (telefono.isEmpty()) {
            mostrarAlertaError("Validación de Campo", "El teléfono es obligatorio.");
            return;
        }

        if (!Pattern.matches(REGEX_TELEFONO, telefono)) {
            mostrarAlertaError("Error de Formato", "El teléfono solo debe contener dígitos numéricos.");
            return;
        }

        if (nota.isEmpty()) {
            mostrarAlertaError("Validación de Campo", "La nota o detalle del contacto es obligatoria.");
            return;
        }

        // Crear/Actualizar contacto
        Contacto contacto = new Contacto(editingContactoId, pais, nombre, telefono, nota, currentImageBytes);

        if (editingContactoId == -1) {
            long id = dbHelper.insertContacto(contacto);
            if (id > 0) {
                Toast.makeText(this, "¡Ficha guardada exitosamente!", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                abrirCatalogo();
            } else {
                mostrarAlertaError("Error de BD", "No se pudo guardar la ficha.");
            }
        } else {
            int rows = dbHelper.updateContacto(contacto);
            if (rows > 0) {
                Toast.makeText(this, "¡Ficha actualizada exitosamente!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mostrarAlertaError("Error de BD", "No se pudo actualizar la ficha.");
            }
        }
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etTelefono.setText("");
        etNota.setText("");
        imgContacto.setImageDrawable(null);
        layoutPlaceholder.setVisibility(View.VISIBLE);
        currentImageBytes = null;
        editingContactoId = -1;
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void abrirCatalogo() {
        Intent intent = new Intent(this, CatalogActivity.class);
        startActivity(intent);
    }
}
