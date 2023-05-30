package com.kudig.kwitansidigital.ui.add;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kudig.kwitansidigital.KwitansiAdapter;
import com.kudig.kwitansidigital.KwitansiDAO;
import com.kudig.kwitansidigital.KwitansiDB;
import com.kudig.kwitansidigital.KwitansiEntity;
import com.kudig.kwitansidigital.R;
import com.kudig.kwitansidigital.databinding.FragmentAddBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddFragment extends Fragment {

    EditText namaET, nominalET, deskripsiET;
    Button save, get;

    KwitansiDB KwitansiDB;

    List<KwitansiEntity> KwitansiList;

    ListView list;

    KwitansiDAO kwitansiDAO;

    RecyclerView myRecycler;

    KwitansiAdapter kwitansiAdapter;

    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel AboutViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        namaET = (EditText) view.findViewById(R.id.input_nama);
        nominalET = (EditText) view.findViewById(R.id.input_nominal);
        deskripsiET = (EditText) view.findViewById(R.id.input_deskripsi);

        save = (Button) view.findViewById(R.id.simpan);
        get = (Button) view.findViewById(R.id.get);
        myRecycler = (RecyclerView) view.findViewById(R.id.kwitansiRecyclerr);

        kwitansiAdapter = new KwitansiAdapter(requireContext());

        myRecycler.setAdapter(kwitansiAdapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));


        String nama = namaET.getText().toString();
        String nominal = nominalET.getText().toString();
        String deskripsi = deskripsiET.getText().toString();

        KwitansiEntity kwitansi = new KwitansiEntity(nama, nominal, deskripsi);
        kwitansiAdapter.addKwitansi(kwitansi);


        RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        };


        KwitansiDB = Room.databaseBuilder(getContext(), KwitansiDB.class,
                "KwitansiDB").addCallback(myCallBack).build();

        kwitansiDAO = KwitansiDB.getKwitansiDAO();

        KwitansiDB = KwitansiDB.getInstance(getContext());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = namaET.getText().toString();
                String nominal = nominalET.getText().toString();
                String deskripsi = deskripsiET.getText().toString();

                KwitansiEntity k1 = new KwitansiEntity(nama, nominal, deskripsi);

                addKwitansiInBackground(k1);
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKwitansiListInBackground();
            }
        });

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addKwitansiInBackground(KwitansiEntity kwitansi) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //Background Task
                KwitansiDB.getKwitansiDAO().addKwitansi(kwitansi);


                //Finishing Task
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Berhasil Disimpan ke DB!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public void getKwitansiListInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //Background Task
                KwitansiList = KwitansiDB.getKwitansiDAO().getAllKwitansi();


                //Finishing Task
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder sb = new StringBuilder();
                        for (KwitansiEntity k : KwitansiList) {
                            sb.append(k.getNama() + " : " + k.getNominal() + " : " + k.getDeskripsi());
                            sb.append("\n");
                        }
                        String finalData = sb.toString();
                        Toast.makeText(getContext(), "" + finalData, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchData() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<KwitansiEntity> kwitansiList = kwitansiDAO.getAllKwitansi();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (KwitansiEntity kwitansi : kwitansiList) {
                            kwitansiAdapter.addKwitansi(kwitansi);
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }
}