package com.kudig.kwitansidigital.ui.add;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kudig.kwitansidigital.KwitansiAdapter;
import com.kudig.kwitansidigital.KwitansiDAO;
import com.kudig.kwitansidigital.KwitansiDB;
import com.kudig.kwitansidigital.KwitansiEntity;
import com.kudig.kwitansidigital.MainActivity;
import com.kudig.kwitansidigital.R;
import com.kudig.kwitansidigital.databinding.FragmentAddBinding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.opencsv.CSVReader;
import com.wendyliga.terbilang.terbilang;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddFragment extends Fragment {

    EditText idET, namaET, nama1ET, nominalET, deskripsiET;
    Button save, get, print;
    KwitansiDB KwitansiDB;
    List<KwitansiEntity> KwitansiList;
    ListView list;
    KwitansiDAO kwitansiDAO;

    private FragmentAddBinding binding;

    public static final int PERMISSION_BLUETOOTH = 1;

    private final Locale locale = new Locale("id", "ID");
    private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel AboutViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        namaET = (EditText) view.findViewById(R.id.input_nama_terimadari);
        nama1ET = (EditText) view.findViewById(R.id.input_nama_penerima);
        nominalET = (EditText) view.findViewById(R.id.input_nominal);
        deskripsiET = (EditText) view.findViewById(R.id.input_deskripsi);
        idET = (EditText) view.findViewById(R.id.input_id);
        print = (Button) view.findViewById(R.id.print);
        save = (Button) view.findViewById(R.id.simpan);

        String nama = namaET.getText().toString();
        String nominal = nominalET.getText().toString();
        String deskripsi = deskripsiET.getText().toString();
        String nama_penerima = nama1ET.getText().toString();

        KwitansiEntity kwitansi = new KwitansiEntity(nama, nama_penerima, nominal, deskripsi);

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
                getContext().getExternalFilesDir(null) + "/databases/KwitansiDB").addCallback(myCallBack).build();

        kwitansiDAO = KwitansiDB.getKwitansiDAO();

        KwitansiDB = KwitansiDB.getInstance(getContext());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = namaET.getText().toString();
                String nama_penerima = nama1ET.getText().toString();
                String nominal = nominalET.getText().toString();
                String deskripsi = deskripsiET.getText().toString();
                String id = idET.getText().toString();

                if (nama.isEmpty() || nama_penerima.isEmpty() || nominal.isEmpty() || deskripsi.isEmpty()) {
                    // Salah satu atau lebih EditText kosong, berikan pesan kesalahan
                    Toast.makeText(requireContext(), "Harap isi semua data", Toast.LENGTH_SHORT).show();
                } else {
                    KwitansiEntity k1 = new KwitansiEntity(nama, nama_penerima, nominal, deskripsi);

                    addKwitansiInBackground(k1);
                }
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importCSV();
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

    private void importCSV() {
        String filePathAndName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "RoomDB_Backup.csv";
        File csvFile = new File(filePathAndName);

        if (csvFile.exists()) {
            try {
                CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsoluteFile()));
                String[] nextLine;
                while ((nextLine = csvReader.readNext()) != null) {
                    String nama = nextLine[0];
                    String nama_penerima = nextLine[1];
                    String nominal = nextLine[2];
                    String deskripsi = nextLine[3];

                    KwitansiDB db = Room.databaseBuilder(getContext(), KwitansiDB.class, "KwitansiDB").allowMainThreadQueries().build();
                    KwitansiDAO kwitansiDAO = db.getKwitansiDAO();
                    kwitansiDAO.addKwitansi(new KwitansiEntity(nama, nama_penerima, nominal, deskripsi));
                }
                Toast.makeText(getContext(), "Backup Restored", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Tidak Ada Backup Ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    public void doPrint(View view, String id, String nama, String nama1, String nominal, String deskripsi) {
        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
            } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
            } else {
                double nominalValue = Double.parseDouble(nominal);
                String nominalTerbilang = convertToTerbilang(nominalValue);
                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                if (connection != null) {
                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                    final String text =
                            "[L]\n" +
                            "[C]<font size='big'><b>KWITANSI</font></b>\n" +
                            "[L]\n" +
                            "[L]NO." + id + "\n" +
                            "[C]--------------------------------\n" +
                            "[L]<b>TERIMA DARI      :</b>\n" +
                            "[L]"+ nama + "\n" +
                            "[L]<b>UANG SEJUMLAH    :<b>\n" +
                            "[L]"+ nf.format(Integer.parseInt(nominal)) +"\n" +
                            "[L]<b>UNTUK PEMBAYARAN :<b>\n" +
                            "[L]"+ deskripsi + "\n" +
                            "[C]--------------------------------\n" +
                            "[L]<b>TERBILANG        :<b>\n" +
                            "[L]"+ nominalTerbilang + "\n" +
                            "[C]--------------------------------\n" +
                            "[L]\n" +
                            "[L]\n" +
                            "[L]\n" +
                            "[L]\n" +
                            "[R]"+ nama1 + "\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[C]" + df.format(new Date()) + "\n" +
                            "[C]https://chat.openai.com\n";

                    printer.printFormattedText(text);
                } else {
                    Toast.makeText(getContext(), "No printer was connected!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("APP", "Can't print", e);
        }
    }

    private String convertToTerbilang(double nominal) {
        String[] angka = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"};

        if (nominal < 12) {
            return angka[(int) nominal];
        } else if (nominal < 20) {
            return angka[(int) nominal % 10] + " Belas";
        } else if (nominal < 100) {
            return angka[(int) nominal / 10] + " Puluh " + angka[(int) nominal % 10];
        } else if (nominal < 200) {
            return "Seratus " + convertToTerbilang(nominal % 100);
        } else if (nominal < 1000) {
            return angka[(int) nominal / 100] + " Ratus " + convertToTerbilang(nominal % 100);
        } else if (nominal < 2000) {
            return "Seribu " + convertToTerbilang(nominal % 1000);
        } else if (nominal < 1000000) {
            return convertToTerbilang(nominal / 1000) + " Ribu " + convertToTerbilang(nominal % 1000);
        } else if (nominal < 1000000000) {
            return convertToTerbilang(nominal / 1000000) + " Juta " + convertToTerbilang(nominal % 1000000);
        } else if (nominal < 1000000000000L) {
            return convertToTerbilang(nominal / 1000000000) + " Miliar " + convertToTerbilang(nominal % 1000000000);
        }

        return "";
    }

        private Resources.Theme getTheme() {
        return null;
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
                        Toast.makeText(getContext(), "Kwitansi Berhasil Dibuat", Toast.LENGTH_SHORT).show();
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


}