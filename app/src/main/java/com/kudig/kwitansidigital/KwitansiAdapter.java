package com.kudig.kwitansidigital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class KwitansiAdapter extends RecyclerView.Adapter<KwitansiAdapter.MyViewHolder> {


     private Context context;
     private List<KwitansiEntity> KwitansiList;

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
          holder.no_kwitansi.setText(Kwitansi.getNominal());
          holder.deskripsi.setText(Kwitansi.getDeskripsi());
     }

     @Override
     public int getItemCount() {
          return KwitansiList.size();
     }

     public class MyViewHolder extends RecyclerView.ViewHolder {
          private TextView no_kwitansi, nama_pengirim, deskripsi;
          public MyViewHolder(@NonNull View itemView) {
               super(itemView);
               no_kwitansi = itemView.findViewById(R.id.no_kwitansi);
               nama_pengirim = itemView.findViewById(R.id.nama_pengirim);
               deskripsi = itemView.findViewById(R.id.deskripsi);
          }
     }
}
