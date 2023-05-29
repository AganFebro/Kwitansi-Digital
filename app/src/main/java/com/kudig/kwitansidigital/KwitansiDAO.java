package com.kudig.kwitansidigital;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KwitansiDAO {

    @Insert
    public void addKwitansi(KwitansiEntity kwitansi);

    @Update
    public void updateKwitansi(KwitansiEntity kwitansi);

    @Delete
    public void deleteKwitansi(KwitansiEntity kwitansi);

    @Query("select * from kwitansi")
    public List<KwitansiEntity> getAllKwitansi();

    @Query("select * from kwitansi where kwitansi_id==:kwitansi_id")
    public KwitansiEntity getKwitansi(int kwitansi_id);
}
