package com.example.andrey.myapplication2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.andrey.myapplication2.Core.AllConfig;
import com.example.andrey.myapplication2.Core.LocalStorage;
import com.example.andrey.myapplication2.SimpleClass.ApplicationUser;
import com.example.andrey.myapplication2.SimpleClass.CategoryChp;
import com.example.andrey.myapplication2.SimpleClass.Durationday;
import com.example.andrey.myapplication2.SimpleClass.EventChp;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.example.andrey.myapplication2.rep.EventRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int READ_REQUEST_CODE = 2;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 3;
    private static final String TAG = "FileLogs";
    private static final String TAG_L = "LocationLogs";
    private ImageArray[] imageViews;
    public static int currentImageView;
    private View imageLayout;
    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private TextView gps_text;
    private RadioButton radio_gps;
    private RadioButton radio_address;
    private String geocode_text, latlang_text;
    private View btn_settings;
    private Geocoder geocoder;
    public static GoogleApiClient myApiClient = null;
    public static Location LastLocation = null;
    private LocationRequest locationRequest;
    private  ArrayList<CategoryChp> CategoryChpAll;
    private ArrayList<Durationday> DurationdayAll;
    LocalStorage myLocalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * imageViews[n].mCurrentPhotoPath - полный путь к конкретной картинке
         * */
        imageViews = new ImageArray[3];
        imageViews[0] = new ImageArray();
        imageViews[1] = new ImageArray();
        imageViews[2] = new ImageArray();
        imageViews[0].imageView = (ImageView) findViewById(R.id.image_preview1);
        imageViews[1].imageView = (ImageView) findViewById(R.id.image_preview2);
        imageViews[2].imageView = (ImageView) findViewById(R.id.image_preview3);
        currentImageView = 0;
        imageLayout = findViewById(R.id.image_preview_layout);
        mAlbumStorageDirFactory = new AlbumDirFactory();

        /**
         * Список категорий
         * */
/*        Spinner category_spinner = (Spinner) findViewById(R.id.event_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.event_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.contact_spinner_row_nothing_selected, this));*/

        /**
         * Координаты
         * */
        gps_text = (TextView) findViewById(R.id.gps_text);
        EditText address_text = (EditText) findViewById(R.id.event_address);
        btn_settings = findViewById(R.id.open_gps_settings);
        radio_gps = (RadioButton) findViewById(R.id.radio_gps);
        radio_address = (RadioButton) findViewById(R.id.radio_address);
        geocoder = new Geocoder(this, Locale.getDefault());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (myApiClient == null) {
            myApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        showLocation();

        address_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) radio_address.setChecked(true);
            }
        });

        NikoInit();

    }
    private void NikoInit(){
        //получаем все категории и дни
        Intent i = getIntent();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CategoryChp>>() {
        }.getType();
        Type type2 = new TypeToken<ArrayList<Durationday>>() {
        }.getType();
        CategoryChpAll = gson.fromJson(i.getStringExtra("CategoryChp"), type);
        DurationdayAll = gson.fromJson(i.getStringExtra("Durationday"), type2);
        //адаптер для категоирй
        Spinner sp = (Spinner) findViewById(R.id.event_category_spinner);
        ArrayList<String> categoryList = new ArrayList<String>();
        for (CategoryChp c : CategoryChpAll) {
            categoryList.add(c.Name);
        }
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(new NothingSelectedSpinnerAdapter(categoriesAdapter, R.layout.contact_spinner_row_nothing_selected, this));
        //адаптер для дней
        Spinner sp2 = (Spinner) findViewById(R.id.event_time_spinner);
        ArrayList<String> dayList = new ArrayList<String>();
        for (Durationday c : DurationdayAll) {
            dayList.add(c.Name);
        }
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, dayList);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(new NothingSelectedSpinnerAdapter(daysAdapter, R.layout.contact_spinner_row_nothing_selected, this));
        myLocalStorage=new LocalStorage(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        myApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        myApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myApiClient.isConnected()) {
            getLocationServices();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocationServices();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LastLocation = location;
            showLocation();
        }
    };

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(myApiClient, locationListener);
    }

    private void getLocationServices() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Вам необхожимо разрешенить приложению получать данные о местоположении", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
//        LastLocation = LocationServices.FusedLocationApi.getLastLocation(myApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(myApiClient, locationRequest, locationListener);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationServices();
            }
            else {
                Toast.makeText(this, "Unable to define location!", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showLocation() {
        if (LastLocation == null) {
            gps_text.setText(R.string.gps_disabled_text);
            radio_address.setChecked(true);
            radio_gps.setEnabled(false);
            btn_settings.setVisibility(View.VISIBLE);
        }
        else {
            latlang_text = LastLocation.getLatitude() + ", " + LastLocation.getLongitude();
            Log.i(TAG_L, latlang_text);
            gps_text.setText(latlang_text);
            radio_gps.setEnabled(true);
            btn_settings.setVisibility(View.GONE);
            geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            String errorMessage = "";
            try {
                addresses = geocoder.getFromLocation(
                        LastLocation.getLatitude(),
                        LastLocation.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
                Log.e(TAG_L, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);
                Log.e(TAG_L, errorMessage + ". " +
                        "Latitude = " + LastLocation.getLatitude() +
                        ", Longitude = " +
                        LastLocation.getLongitude(), illegalArgumentException);
            }
            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG_L, errorMessage);
                }
            } else {
                Address address = addresses.get(0);
                geocode_text =  address.getAddressLine((0));
                gps_text.setText(latlang_text + " (" + geocode_text + ")");
            }
        }
    }


    public void testClick(View view) {
        showAnimation();
    }


    private View mProgressView;
    private View mFormView;
    private ProgressAnimation progress;

    /**
     * Анимация сохранения
     */
    public void showAnimation() {
        if (mProgressView == null) mProgressView = findViewById(R.id.create_event_progress);
        if (mFormView == null) mFormView = findViewById(R.id.create_event_form);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        progress = new ProgressAnimation(mProgressView, mFormView, shortAnimTime);
        progress.showProgress(true);
    }

    public void stopAnimation() {
        progress.showProgress(false);
    }

    public void showPhotoDialog(View view) {
        if (currentImageView == imageViews.length) {
            Toast.makeText(this, "Вы можете загрузить не больше " + imageViews.length + " фотографий", Toast.LENGTH_LONG).show();
            Log.i(TAG, imageViews[0].mCurrentPhotoPath);
            Log.i(TAG, imageViews[1].mCurrentPhotoPath);
            Log.i(TAG, imageViews[2].mCurrentPhotoPath);
            return;
        }
        AttachPhotoDialogFragment photoDialogFragment = AttachPhotoDialogFragment.newInstance(getString(R.string.dialog_photo_title));
        photoDialogFragment.show(getSupportFragmentManager(), "fragment_attach_photo_dialog");
    }

    public void takePicture(View view) {
        if (currentImageView == imageViews.length) {
            Toast.makeText(this, "Вы можете загрузить не больше " + imageViews.length + " фотографий", Toast.LENGTH_LONG).show();
            return;
        }
        dispatchTakePictureIntent();
    }

    public void showGallery(View view) {
        if (currentImageView == imageViews.length) {
            Toast.makeText(this, "Вы можете загрузить не больше " + imageViews.length + " фотографий", Toast.LENGTH_LONG).show();
            Log.i(TAG, imageViews[0].mCurrentPhotoPath);
            Log.i(TAG, imageViews[1].mCurrentPhotoPath);
            Log.i(TAG, imageViews[2].mCurrentPhotoPath);
            return;
        }
        performFileSearch();
    }

    /**
     * Вызов галереи
     * */
    public void performFileSearch() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
//        }
    }

    /**
     * Вызов приложения камеры
     * */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = setUpPhotoFile();
            } catch (IOException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                Log.i(TAG, mCurrentPhotoPath);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Ответ из фото-приложения / галереи
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentImageView == 0) imageLayout.setVisibility(View.VISIBLE);
            else if (currentImageView == imageViews.length) return;

            setPic();
            galleryAddPic();
        }
        else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                /**
                 * URI выбранного файла (в галерее)
                 * */
                uri = data.getData();
                mCurrentPhotoPath = GetPath.getPath(this, uri);
                Log.i(TAG, " " + mCurrentPhotoPath);

                if (currentImageView == 0) imageLayout.setVisibility(View.VISIBLE);
                else if (currentImageView == imageViews.length) return;
                setPic();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getAlbumDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("Camera", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private void setPic() {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

        imageViews[currentImageView].imageView.setVisibility(View.VISIBLE);
		/* Get the size of the ImageView */
        /*int targetW = imageViews[currentImageView].getWidth();
        int targetH = imageViews[currentImageView].getHeight();*/
        int targetW = 240;
        int targetH = 240;

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        imageViews[currentImageView].imageView.setImageBitmap(bitmap);
        imageViews[currentImageView].mCurrentPhotoPath = mCurrentPhotoPath;
        currentImageView++;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_gps:
                if (checked)

                    break;
            case R.id.radio_address:
                if (checked)

                    break;
        }
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    //niko
    public void SaveButtonClick(View view) {

        String ber=myLocalStorage.GetBearerToken();
        //клиентская валидация
        //определеям categoryid и durationdayid
        Spinner sp = (Spinner) findViewById(R.id.event_category_spinner);
        Spinner sp2 = (Spinner) findViewById(R.id.event_time_spinner);
        if (sp.getSelectedItem()==null){
            Toast.makeText(getApplicationContext(), "Выберите категорию события", Toast.LENGTH_LONG).show();
            return;
        }
        if (sp2.getSelectedItem()==null){
            Toast.makeText(getApplicationContext(), "Выберите срок актуальности", Toast.LENGTH_LONG).show();
            return;
        }
        String description=((EditText)findViewById(R.id.event_text)).getText().toString();

        if (AllConfig.empty(description)){
            Toast.makeText(getApplicationContext(), "Введите описание происшествия", Toast.LENGTH_LONG).show();
            return;
        }

        String spvalue=sp.getSelectedItem().toString();
        String sp2value=sp2.getSelectedItem().toString();
        String categoryId="";
        String dayforshow="";
        for(CategoryChp c: CategoryChpAll){
            if (c.Name==spvalue){
                categoryId=String.valueOf(c.CategoryChpId);
                break;
            }
        }
        for(Durationday c: DurationdayAll){
            if (c.Name==sp2value){
                dayforshow=String.valueOf(c.DaysForShow);
                break;
            }
        }


        new EventRepositoryCreateEventTask().execute(myLocalStorage.GetBearerToken(),
                description,
                "45:43",
                ((EditText)findViewById(R.id.event_address)).getText().toString(),
                categoryId,
                dayforshow);
    }
    class EventRepositoryCreateEventTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected void onPreExecute() {
            showAnimation();
        }
        @Override
        protected Myresponse doInBackground(String... params) {
            EventChp ct = new EventChp();
            ct.Description = params[1];
            ct.Koordinates = params[2];
            ct.Adress = params[3];
            ct.CategoryChpId = Integer.parseInt(params[4]);
            ct.DaysForShow = Integer.parseInt(params[5]);
            return (new EventRepository(params[0])).CreateEvent(ct);
        }

        @Override
        protected void onPostExecute(Myresponse response) {
            stopAnimation();
            if (response.Status == Myresponse.MyStatus.Ok) {
                Toast.makeText(getApplicationContext(), "Успешно сохранено. Ожидает модерации.", Toast.LENGTH_LONG).show();
                Button savebutton= ((Button) findViewById(R.id.create_event_btn));
                savebutton.setText("Закрыть");
                savebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), response.Message, Toast.LENGTH_LONG).show();
            }
        }
    }
}

class ImageArray {
    public ImageView imageView;
    public String mCurrentPhotoPath;

    public ImageArray() {

    }
}
