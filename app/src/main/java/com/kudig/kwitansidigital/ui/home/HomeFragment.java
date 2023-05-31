package com.kudig.kwitansidigital.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kudig.kwitansidigital.KwitansiAdapter;
import com.kudig.kwitansidigital.KwitansiDAO;
import com.kudig.kwitansidigital.KwitansiDB;
import com.kudig.kwitansidigital.KwitansiEntity;
import com.kudig.kwitansidigital.R;
import com.kudig.kwitansidigital.databinding.FragmentHomeBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    EditText namaET, nominalET, deskripsiET;
    KwitansiDB KwitansiDB;
    List<KwitansiEntity> KwitansiList;
    ListView list;
    KwitansiDAO kwitansiDAO;
    RecyclerView myRecycler;
    KwitansiAdapter kwitansiAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myRecycler = root.findViewById(R.id.recycler_fragment);
        kwitansiAdapter = new KwitansiAdapter(requireContext());
        myRecycler.setAdapter(kwitansiAdapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

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



        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Panggil metode fetchData() untuk memuat ulang data
                fetchData();

                // Berhenti mengindikasikan proses refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });




        return root;
    }




//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
//
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        myRecycler = (RecyclerView) view.findViewById(R.id.recycler_fragment);
//
//        kwitansiAdapter = new KwitansiAdapter(requireContext());
//
//        myRecycler.setAdapter(kwitansiAdapter);
//        myRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
//            @Override
//            public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                super.onCreate(db);
//            }
//
//            @Override
//            public void onOpen(@NonNull SupportSQLiteDatabase db) {
//                super.onOpen(db);
//            }
//        };
//
//        KwitansiDB = Room.databaseBuilder(getContext(), KwitansiDB.class,
//                "KwitansiDB").addCallback(myCallBack).build();
//
//        kwitansiDAO = KwitansiDB.getKwitansiDAO();
//
//        KwitansiDB = KwitansiDB.getInstance(getContext());
//
//
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        return view;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void fetchData() {
        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setRefreshing(true);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<KwitansiEntity> kwitansiList = kwitansiDAO.getAllKwitansi();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        kwitansiAdapter.clear();

                        for (KwitansiEntity kwitansi : kwitansiList) {
                            kwitansiAdapter.addKwitansi(kwitansi);
                        }

                        if (kwitansiList.isEmpty()) {
                            binding.emptyTextView.setVisibility(View.VISIBLE);
                        } else {
                            binding.emptyTextView.setVisibility(View.GONE);
                        }
                        swipeRefreshLayout.setRefreshing(false);
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