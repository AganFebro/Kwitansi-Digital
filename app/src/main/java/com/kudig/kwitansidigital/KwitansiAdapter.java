package com.kudig.kwitansidigital;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class KwitansiAdapter extends RecyclerView.Adapter<KwitansiAdapter.MyViewHolder> {

    private Context context;
    private List<KwitansiEntity> KwitansiList;

    public void clear() {
        KwitansiList.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        KwitansiEntity kwitansi = KwitansiList.get(position);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                KwitansiDB.getInstance(context).getKwitansiDAO().deleteKwitansi(kwitansi);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                KwitansiList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, KwitansiList.size());
                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public KwitansiAdapter(Context context) {
        this.context = context;
        KwitansiList = new ArrayList<>();
    }

    public void addKwitansi(KwitansiEntity Kwitansi) {
        KwitansiList.add(Kwitansi);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_design, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        KwitansiEntity Kwitansi = KwitansiList.get(position);
        holder.nama_pengirim.setText(Kwitansi.getNama());
        holder.nominal_kwitansi.setText(Kwitansi.getNominal());
        holder.deskripsi.setText(Kwitansi.getDeskripsi());


        holder.ListKwitansi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                final String[] action = {"Edit", "Hapus"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i) {
                            case 0:
                                Toast.makeText(context, "Test asdasd asd asda", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Konfirmasi");
                                builder.setMessage("Apakah Anda yakin ingin menghapus data ini?");
                                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int position = holder.getAdapterPosition();
                                        if (position != RecyclerView.NO_POSITION) {
                                            deleteItem(position);
                                            Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                break;

                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });

        holder.ListKwitansi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Data Detail");
                builder.setMessage("Nama      : " + Kwitansi.nama + "\nNominal  : " + Kwitansi.nominal + "\nDeskripsi : " + Kwitansi.deskripsi);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Tindakan yang ingin dilakukan ketika pengguna mengklik tombol OK
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return KwitansiList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nominal_kwitansi, nama_pengirim, deskripsi;
        private LinearLayout ListKwitansi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nominal_kwitansi = itemView.findViewById(R.id.nominal_kwitansi);
            nama_pengirim = itemView.findViewById(R.id.nama_pengirim);
            deskripsi = itemView.findViewById(R.id.deskripsi);
            ListKwitansi = itemView.findViewById(R.id.list_item);
        }
    }
}
