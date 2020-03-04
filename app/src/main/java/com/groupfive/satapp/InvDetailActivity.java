package com.groupfive.satapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.data.viewModel.InventariableViewModel;
import com.groupfive.satapp.models.inventariable.Inventariable;
import com.groupfive.satapp.retrofit.SatAppInvService;
import com.groupfive.satapp.retrofit.SatAppServiceGenerator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvDetailActivity extends AppCompatActivity {
    private TextView tvName, tvDescription, tvCreate, tvUpdate, tvLocation;
    private ImageView ivPhoto, ivType;
    private InventariableViewModel inventariableViewModel;
    private SatAppInvService service;
    private Inventariable inventariable;
    private String id;
    private String photoCode;
    private LocalDate changerCreated, changerUpdated;
    private static final int READ_REQUEST_CODE = 42;
    private EditInventariableFragment inventariableFragment;
    private Uri uriS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventariableFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        service = SatAppServiceGenerator.createService(SatAppInvService.class);

        inventariableViewModel = ViewModelProviders.of(InvDetailActivity.this).get(InventariableViewModel.class);

        tvName = findViewById(R.id.textViewName);
        tvDescription = findViewById(R.id.textViewDescription);
        tvCreate = findViewById(R.id.textViewCreate);
        tvUpdate = findViewById(R.id.textViewUpdate);
        tvLocation = findViewById(R.id.textViewLocation);
        ivPhoto = findViewById(R.id.imageViewPhotoPlus);
        ivType = findViewById(R.id.imageViewType);

        id = getIntent().getExtras().getString("id");

        inventariableFragment = new EditInventariableFragment(InvDetailActivity.this, id);

        getInventariable();

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });


    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("Filechooser URI", "Uri: " + uri.toString());
                Glide
                        .with(this)
                        .load(uri)
                        .into(ivPhoto);
                uriS = uri;
            }
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(uriS);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int cantBytes;
            byte[] buffer = new byte[1024*4];

            while ((cantBytes = bufferedInputStream.read(buffer,0,1024*4)) != -1) {
                baos.write(buffer,0,cantBytes);
            }


            RequestBody requestFile =
                    RequestBody.create(baos.toByteArray(),
                            MediaType.parse(getContentResolver().getType(uriS)));


            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("avatar", "avatar", requestFile);

            service.putInventariableImg(id, body);
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getInventariable() {
        inventariableViewModel.getInventariable(id).observe(InvDetailActivity.this, new Observer<Inventariable>() {
            @Override
            public void onChanged(Inventariable inventariable) {

                changerCreated = LocalDate.parse(inventariable.getCreatedAt().split("T")[0], DateTimeFormat.forPattern("yyyy-mm-dd"));
                changerUpdated = LocalDate.parse(inventariable.getUpdatedAt().split("T")[0], DateTimeFormat.forPattern("yyyy-mm-dd"));
                tvName.setText(inventariable.getNombre());
                tvDescription.setText(inventariable.getDescripcion());
                tvCreate.setText(Constants.FORMATTER.print(changerCreated));
                tvUpdate.setText(Constants.FORMATTER.print(changerUpdated));
                tvDescription.setText(inventariable.getDescripcion());
                tvLocation.setText(inventariable.getUbicacion());

                switch(inventariable.getTipo()) {
                    case "PC":
                        Glide.with(InvDetailActivity.this).load(getResources().getDrawable(R.drawable.ic_ordenador_personal)).into(ivType);
                        break;
                    case "MONITOR":
                        Glide.with(InvDetailActivity.this).load(getResources().getDrawable(R.drawable.ic_television)).into(ivType);
                        break;
                    case "RED":
                        Glide.with(InvDetailActivity.this).load(getResources().getDrawable(R.drawable.ic_wifi)).into(ivType);
                        break;
                    case "IMPRESORA":
                        Glide.with(InvDetailActivity.this).load(getResources().getDrawable(R.drawable.ic_herramientas_y_utensilios)).into(ivType);
                        break;
                    case "OTRO":
                        Glide.with(InvDetailActivity.this).load(getResources().getDrawable(R.drawable.ic_desconocido)).into(ivType);
                        break;
                    case "PERIFERICO":
                        Glide.with(InvDetailActivity.this).load(getResources().getDrawable(R.drawable.ic_teclado)).into(ivType);
                        break;
                }
                photoCode = inventariable.getImagen().split("/")[3];
                Call<ResponseBody> call = service.getInventariableImage(photoCode);
//        Toast.makeText(ctx, photoCode, Toast.LENGTH_SHORT).show();

                call.enqueue(new Callback<ResponseBody>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                Glide
                                        .with(InvDetailActivity.this)
                                        .load(bmp)
                                        .error(Glide.with(InvDetailActivity.this).load(R.drawable.ic_interrogation))
                                        .thumbnail(Glide.with(InvDetailActivity.this).load(Constants.LOADING_GIF))
                                        .into(ivPhoto);
                            } else {
                                Glide
                                        .with(InvDetailActivity.this)
                                        .load(R.drawable.ic_faqs)
                                        .error(Glide.with(InvDetailActivity.this).load(R.drawable.ic_interrogation))
                                        .thumbnail(Glide.with(InvDetailActivity.this).load(Constants.LOADING_GIF))
                                        .into(ivPhoto);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(InvDetailActivity.this, "Error loading picture", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
