package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.model.Contacto;

public class CallActivity extends AppCompatActivity {

    private ImageView imgCallAvatar;
    private TextView tvCallNombre, tvCallTelefono, tvConfirmMsgPreview;

    private Contacto contacto;

    // Launcher para solicitar el permiso CALL_PHONE en tiempo de ejecución
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    ejecutarLlamada();
                } else {
                    Toast.makeText(this, "Permiso de llamada denegado por el usuario", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        initViews();

        findViewById(R.id.btnCallBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnTriggerCall).setOnClickListener(v -> confirmarYLlamar());

        cargarDatosContacto();
    }

    private void initViews() {
        imgCallAvatar = findViewById(R.id.imgCallAvatar);
        tvCallNombre = findViewById(R.id.tvCallNombre);
        tvCallTelefono = findViewById(R.id.tvCallTelefono);
        tvConfirmMsgPreview = findViewById(R.id.tvConfirmMsgPreview);
    }

    @SuppressWarnings("deprecation")
    private void cargarDatosContacto() {
        if (getIntent().hasExtra("contacto_call")) {
            contacto = (Contacto) getIntent().getSerializableExtra("contacto_call");
        }

        if (contacto != null) {
            tvCallNombre.setText(contacto.getNombre());
            tvCallTelefono.setText(contacto.getNumeroCompleto());

            String previewMsg = "¿Desea iniciar la llamada con " + contacto.getNombre() + " (" + contacto.getNumeroCompleto() + ")?";
            tvConfirmMsgPreview.setText(previewMsg);

            if (contacto.getImagen() != null && contacto.getImagen().length > 0) {
                Bitmap bmp = BitmapFactory.decodeByteArray(contacto.getImagen(), 0, contacto.getImagen().length);
                imgCallAvatar.setImageBitmap(bmp);
            } else {
                imgCallAvatar.setImageResource(R.drawable.ic_person);
            }
        } else {
            tvCallNombre.setText(R.string.contacto_sin_nombre);
            tvCallTelefono.setText("---");
        }
    }

    private void confirmarYLlamar() {
        if (contacto == null) {
            Toast.makeText(this, "No hay datos de contacto para llamar", Toast.LENGTH_SHORT).show();
            return;
        }

        String mensaje = "¿Desea iniciar la llamada con " + contacto.getNombre() + " (" + contacto.getNumeroCompleto() + ")?";

        new AlertDialog.Builder(this)
                .setTitle("AlertDialog.Builder")
                .setMessage(mensaje)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> comprobarPermisoYEjecutar())
                .show();
    }

    private void comprobarPermisoYEjecutar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            ejecutarLlamada();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void ejecutarLlamada() {
        if (contacto == null) return;

        String numeroLimpio = contacto.getNumeroCompleto().replaceAll("[^0-9+]", "");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + numeroLimpio));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "No se cuenta con el permiso para realizar la llamada", Toast.LENGTH_SHORT).show();
        }
    }
}
