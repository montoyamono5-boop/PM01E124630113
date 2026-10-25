package com.example.myapplication.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Contacto;

import java.util.ArrayList;
import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    public interface OnContactoClickListener {
        void onContactoClick(Contacto contacto);
    }

    private List<Contacto> contactos = new ArrayList<>();
    private final OnContactoClickListener listener;
    private int selectedPosition = -1;

    public ContactoAdapter(OnContactoClickListener listener) {
        this.listener = listener;
    }

    public void setContactos(List<Contacto> nuevosContactos) {
        this.contactos = nuevosContactos;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public Contacto getSelectedContacto() {
        if (selectedPosition >= 0 && selectedPosition < contactos.size()) {
            return contactos.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view, listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = contactos.get(position);
        holder.bind(contacto, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAvatar;
        TextView tvNombre;
        TextView tvTelefono;

        public ContactoViewHolder(@NonNull View itemView, OnContactoClickListener listener, ContactoAdapter adapter) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgItemAvatar);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvTelefono = itemView.findViewById(R.id.tvItemTelefono);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    adapter.selectedPosition = pos;
                    adapter.notifyDataSetChanged();
                    if (listener != null) {
                        listener.onContactoClick(adapter.contactos.get(pos));
                    }
                }
            });
        }

        public void bind(Contacto contacto, boolean isSelected) {
            tvNombre.setText(contacto.getNombre());
            tvTelefono.setText(contacto.getTelefono());

            if (contacto.getImagen() != null && contacto.getImagen().length > 0) {
                Bitmap bmp = BitmapFactory.decodeByteArray(contacto.getImagen(), 0, contacto.getImagen().length);
                imgAvatar.setImageBitmap(bmp);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_person);
            }

            if (isSelected) {
                itemView.setAlpha(0.7f);
            } else {
                itemView.setAlpha(1.0f);
            }
        }
    }
}
