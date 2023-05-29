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

    @ColumnInfo(name = "nominal")
    String nominal;

    @ColumnInfo(name = "deskripsi")
    String deskripsi;

    public KwitansiEntity(String nama, String nominal, String deskripsi) {
        this.nama = nama;
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
