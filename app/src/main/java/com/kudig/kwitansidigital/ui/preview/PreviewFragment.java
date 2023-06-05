package com.kudig.kwitansidigital.ui.preview;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.pdf.PdfDocument;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import com.kudig.kwitansidigital.KwitansiDAO;
import com.kudig.kwitansidigital.KwitansiDB;
import com.kudig.kwitansidigital.KwitansiEntity;
import com.kudig.kwitansidigital.MainActivity;
import com.kudig.kwitansidigital.PDFGenerator;
import com.kudig.kwitansidigital.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PreviewFragment extends Fragment {
    private TextView textNamaPengirim, textNamaPenerima, textNominal, textDeskripsi, textTerbilang;
    private PreviewViewModel viewModel;
    private Button print;
    private KwitansiDB kwitansiDB;
    private KwitansiDAO kwitansiDAO;
    private KwitansiEntity kwitansiEntity;
    Bitmap bitmap;
    LinearLayout linearLayout;
    private static final int STORAGE_PERMISSION_CODE = 100;

    public PreviewFragment() {
    }

    public static final int PERMISSION_BLUETOOTH = 1;

    private final Locale locale = new Locale("id", "ID");
    private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        setHasOptionsMenu(true);

        textNamaPengirim = view.findViewById(R.id.text_nama_pengirim);
        textNamaPenerima = view.findViewById(R.id.text_nama_penerima);
        textNominal = view.findViewById(R.id.text_nominal);
        textDeskripsi = view.findViewById(R.id.text_deskripsi);
        textTerbilang = view.findViewById(R.id.text_terbilang);
        linearLayout = view.findViewById(R.id.lld);

        viewModel = new ViewModelProvider(this).get(PreviewViewModel.class);
        kwitansiDB = KwitansiDB.getInstance(requireContext());
        kwitansiDAO = kwitansiDB.getKwitansiDAO();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String namaPengirim = bundle.getString("DataNamaPengirim");
            String namaPenerima = bundle.getString("DataNamaPenerima");
            String nominal = bundle.getString("DataNominal");
            String deskripsi = bundle.getString("DataDeskripsi");

            double nominalValue = Double.parseDouble(nominal);
            String nominalTerbilang = convertToTerbilang(nominalValue);

            textNamaPengirim.setText(namaPengirim);
            textNamaPenerima.setText(namaPenerima);
            textNominal.setText(nominal);
            textDeskripsi.setText(deskripsi);
            textTerbilang.setText(nominalTerbilang);

        }
        return view;
    }

    public void doPrint(View view, String id, String namaPengirim, String namaPenerima, String nominal, String deskripsi) {
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
                BluetoothPrintersConnections printers = new BluetoothPrintersConnections();
                BluetoothConnection[] connections = printers.getList();
                if (connections != null) {
                    // Print Berdasarkan Array List Bluetooth Pertama
                    BluetoothConnection hadeh = connections[0];
                    DeviceConnection connection = new BluetoothConnection(hadeh.getDevice());
                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                    final String text =
                            "[L]\n" +
                                    "[C]<font size='big'><b>KWITANSI</font></b>\n" +
                                    "[L]\n" +
                                    "[L]NO." + id + "\n" +
                                    "[C]--------------------------------\n" +
                                    "[L]<b>TERIMA DARI      :</b>\n" +
                                    "[L]" + namaPengirim + "\n" +
                                    "[L]<b>UANG SEJUMLAH    :<b>\n" +
                                    "[L]" + nf.format(Integer.parseInt(nominal)) + "\n" +
                                    "[L]<b>UNTUK PEMBAYARAN :<b>\n" +
                                    "[L]" + deskripsi + "\n" +
                                    "[C]--------------------------------\n" +
                                    "[L]<b>TERBILANG        :<b>\n" +
                                    "[L]" + nominalTerbilang + "\n" +
                                    "[C]--------------------------------\n" +
                                    "[L]\n" +
                                    "[L]\n" +
                                    "[L]\n" +
                                    "[L]\n" +
                                    "[R]" + namaPenerima + "\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[C]" + df.format(new Date()) + "\n" +
                                    "[C]https://chat.openai.com\n";

                    printer.printFormattedText(text);
                    Toast.makeText(getContext(), "Berhasil print!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Tidak ada printer yang terpairing!", Toast.LENGTH_SHORT).show();
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

    private void createPdf() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int convertHeight = displayMetrics.heightPixels;
        int convertWidth = displayMetrics.widthPixels;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);


        Calendar instance = Calendar.getInstance();
        String format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(instance.getTime());
        String format2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(instance.getTime());
        String[] split = format.split("/");
        String[] split2 = format2.split(":");
        String str = (split[0] + split[1] + split[2]) + (split2[0] + split2[1] + split2[2]) + "_Kwitansi";
        //pdf download location
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        String targetPdf = directory.getAbsolutePath() + File.separator + str + ".pdf";
        File file = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "PDF saved: " + targetPdf, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }

        //close document
        document.close();
        //progress bar
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving..." + targetPdf);
        progressDialog.show();
        //time after cancel
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        }, 5000);
    }

    private Bitmap loadBitmapFromView(LinearLayout linearLayout, int width, int height) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        linearLayout.draw(canvas);
        return bitmap;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_preview, menu);
        MenuItem printMenuItem = menu.findItem(R.id.menu_item_print);
        printMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String id = "5";
                String namaPengirim = textNamaPengirim.getText().toString();
                String namaPenerima = textNamaPenerima.getText().toString();
                String nominal = textNominal.getText().toString();
                String deskripsi = textDeskripsi.getText().toString();

                doPrint(getView(), id, namaPengirim, namaPenerima, nominal, deskripsi);
                return true;
            }
        });
        MenuItem saveMenuItem = menu.findItem(R.id.menu_item_save);
        saveMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d("size", linearLayout.getWidth() + " " + linearLayout.getHeight());
                bitmap = loadBitmapFromView(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
                createPdf();
                return true;
            }
        });

        MenuItem shareMenuItem = menu.findItem(R.id.menu_item_share);
        shareMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                performExportCSV();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_edit) {
            View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_edit_data, null);

            EditText editTextNamaPengirim = popupView.findViewById(R.id.edit_text_DataNamaPengirim);
            EditText editTextNamaPenerima = popupView.findViewById(R.id.edit_text_DataNamaPenerima);
            EditText editTextNominal = popupView.findViewById(R.id.edit_text_DataNominal);
            EditText editTextDeskripsi = popupView.findViewById(R.id.edit_text_DataDeskripsi);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Edit Data")
                    .setView(popupView)
                    .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Ambil nilai yang diubah dari EditText
                            String namaPengirimBaru = editTextNamaPengirim.getText().toString();
                            String namaPenerimaBaru = editTextNamaPenerima.getText().toString();
                            String nominalBaru = editTextNominal.getText().toString();
                            String deskripsiBaru = editTextDeskripsi.getText().toString();

                            // Update data pada Room Database
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    try {
                                        // Retrieve the existing KwitansiEntity object from the database
                                        KwitansiEntity existingKwitansiEntity = kwitansiDAO.getKwitansi(kwitansiEntity.getId());
                                        if (existingKwitansiEntity != null) {
                                            // Update data pada objek KwitansiEntity
                                            existingKwitansiEntity.setNama(namaPengirimBaru);
                                            existingKwitansiEntity.setNama(namaPenerimaBaru);
                                            existingKwitansiEntity.setNominal(nominalBaru);
                                            existingKwitansiEntity.setDeskripsi(deskripsiBaru);

                                            // Update data pada Room Database
                                            kwitansiDAO.updateKwitansi(existingKwitansiEntity);
                                        }
                                    } catch (Exception e) {
                                        // Log the exception
                                        Log.e("UpdateError", "Error updating KwitansiEntity: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    Toast.makeText(requireContext(), "Berhasil Diedit", Toast.LENGTH_SHORT).show();
                                }
                            }.execute();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void performExportCSV() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) {
            if (!downloadsDir.mkdirs()) {
                Toast.makeText(getContext(), "Failed to create Downloads directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Specify the file path and name
        String csvFileName = "RoomDB_Backup.csv";
        File file = new File(downloadsDir, csvFileName);
        String filePathAndName = file.getAbsolutePath();


        ArrayList<KwitansiEntity> recordsList = new ArrayList<>();
        recordsList.clear();

        KwitansiDB db = Room.databaseBuilder(getContext(), KwitansiDB.class, "KwitansiDB")
                .allowMainThreadQueries().build();

        KwitansiDAO kwitansiDAO = db.getKwitansiDAO();
        List<KwitansiEntity> kwitansiList = kwitansiDAO.getAllKwitansi();
        recordsList = new ArrayList<>(kwitansiList);

        try {
            FileWriter fileWriter = new FileWriter(filePathAndName);
            for (int i = 0; i < recordsList.size(); i++) {
                fileWriter.append("" + recordsList.get(i).getNama());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getNama_penerima());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getNominal());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getDeskripsi());
                fileWriter.append("\n");
            }
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(getContext(), "Backup Berhasil di Export ke " + filePathAndName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}