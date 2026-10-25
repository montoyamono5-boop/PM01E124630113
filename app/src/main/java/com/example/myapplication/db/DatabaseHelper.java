package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Contacto;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTOS = "contactos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PAIS = "pais";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_NOTA = "nota";
    public static final String COLUMN_IMAGEN = "imagen";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CONTACTOS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PAIS + " TEXT, "
                + COLUMN_NOMBRE + " TEXT, "
                + COLUMN_TELEFONO + " TEXT, "
                + COLUMN_NOTA + " TEXT, "
                + COLUMN_IMAGEN + " BLOB"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }

    // Insertar un nuevo contacto
    public long insertContacto(Contacto contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAIS, contacto.getPais());
        values.put(COLUMN_NOMBRE, contacto.getNombre());
        values.put(COLUMN_TELEFONO, contacto.getTelefono());
        values.put(COLUMN_NOTA, contacto.getNota());
        values.put(COLUMN_IMAGEN, contacto.getImagen());

        long id = db.insert(TABLE_CONTACTOS, null, values);
        db.close();
        return id;
    }

    // Actualizar un contacto existente
    public int updateContacto(Contacto contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAIS, contacto.getPais());
        values.put(COLUMN_NOMBRE, contacto.getNombre());
        values.put(COLUMN_TELEFONO, contacto.getTelefono());
        values.put(COLUMN_NOTA, contacto.getNota());
        values.put(COLUMN_IMAGEN, contacto.getImagen());

        int rows = db.update(TABLE_CONTACTOS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contacto.getId())});
        db.close();
        return rows;
    }

    // Eliminar un contacto por ID
    public int deleteContacto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CONTACTOS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // Obtener todos los contactos
    public List<Contacto> getAllContactos() {
        List<Contacto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTOS + " ORDER BY " + COLUMN_NOMBRE + " ASC", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Contacto c = cursorToContacto(cursor);
                    lista.add(c);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return lista;
    }

    // Buscar contactos por nombre o teléfono
    public List<Contacto> searchContactos(String query) {
        List<Contacto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_CONTACTOS + " WHERE " + COLUMN_NOMBRE + " LIKE ? OR " + COLUMN_TELEFONO + " LIKE ? ORDER BY " + COLUMN_NOMBRE + " ASC";
        String searchPattern = "%" + query + "%";
        Cursor cursor = db.rawQuery(sql, new String[]{searchPattern, searchPattern});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Contacto c = cursorToContacto(cursor);
                    lista.add(c);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return lista;
    }

    // Obtener contacto por ID
    public Contacto getContactoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTOS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Contacto contacto = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contacto = cursorToContacto(cursor);
            }
            cursor.close();
        }
        db.close();
        return contacto;
    }

    private Contacto cursorToContacto(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String pais = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAIS));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
        String telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO));
        String nota = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTA));
        byte[] imagen = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN));

        return new Contacto(id, pais, nombre, telefono, nota, imagen);
    }
}
