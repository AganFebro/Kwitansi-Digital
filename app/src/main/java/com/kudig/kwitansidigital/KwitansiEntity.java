package com.kudig.kwitansidigital;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Kwitansi")
public class KwitansiEntity {

    @ColumnInfo(name = "kwitansi_id")
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "nama")
    String nama;

    @ColumnInfo(name = "nama_penerima")
    String nama_penerima;

    @ColumnInfo(name = "nominal")
    String nominal;

    @ColumnInfo(name = "deskripsi")
    String deskripsi;

    public KwitansiEntity(String nama, String nama_penerima, String nominal, String deskripsi) {
        this.nama = nama;
        this.nama_penerima = nama_penerima;
        this.nominal = nominal;
        this.deskripsi = deskripsi;
        this.id = 0;
    }

    @Ignore
    public KwitansiEntity() {

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama_penerima() {
        return nama_penerima;
    }

    public void setNama_penerima(String nama_penerima) {
        this.nama_penerima = nama_penerima;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
