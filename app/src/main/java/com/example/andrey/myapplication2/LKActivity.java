package com.example.andrey.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.andrey.myapplication2.Core.LocalStorage;
import com.example.andrey.myapplication2.SimpleClass.ApplicationUser;
import com.example.andrey.myapplication2.SimpleClass.CategoryChp;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.example.andrey.myapplication2.rep.AccountRepository;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LKActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MenuItem saveMenuItem;
    private ArrayList<CategoryChp> allCategory;
    private LocalStorage myLocalStorage;
    AccountRepository accountRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lk);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //roundedAvatar(R.id.lk_avatar_image, R.drawable.img_stathem);

        myLocalStorage = new LocalStorage(this.getApplicationContext());
        accountRepository = new AccountRepository(myLocalStorage.GetBearerToken());
        //категории выводим все и проставляем галочки
        Intent i = getIntent();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CategoryChp>>() {
        }.getType();
        Type type2 = new TypeToken<ApplicationUser>() {
        }.getType();
        allCategory = gson.fromJson(i.getStringExtra("CategoryChp"), type);
        ApplicationUser user = gson.fromJson(i.getStringExtra("User"), type2);
        //проставляем категории
        setListViewCategory();
        //аватар
        roundedAvatar2(R.id.lk_avatar_image, user.Avatar);
        // проставляем имя и vk в поле
        //new AccountRepositoryMyProfileTask().execute();
        ((EditText) findViewById(R.id.lk_name)).setText(user.Name);
        ((EditText) findViewById(R.id.vk_name)).setText(user.Vk);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        saveMenuItem = menu.findItem(R.id.action_save);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            /**
             * Нажали галочку Сохранить
             * */
            showAnimation();
            //сохраняем данные на сервере
            new AccountRepositorySaveMyInfaTask().execute(((EditText) findViewById(R.id.lk_name)).getText().toString(),
                    ((EditText) findViewById(R.id.vk_name)).getText().toString());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Делаем аватар круглым. Кроме decodeResource есть, например, функция decodeFile
     */
    public void roundedAvatar(int img_id, int res_id) {
        Resources mRes = getApplicationContext().getResources();
        ImageView img = (ImageView) findViewById(img_id);
        Bitmap mBitmap = BitmapFactory.decodeResource(mRes, res_id);
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(mRes, mBitmap);
        rbd.setCircular(true);
        rbd.setAntiAlias(true);
        img.setImageDrawable(rbd);
    }

    public void roundedAvatar2(int img_id, String imageUri) {
        Resources mRes = getApplicationContext().getResources();
        ImageView img = (ImageView) findViewById(img_id);
        Bitmap mBitmap = null;
        try {
            mBitmap = new AccountRepository.AsyncGettingBitmapFromUrl().execute(imageUri).get();
        } catch (Exception ex) {
            return;
        }
        if (mBitmap == null) return;
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(mRes, mBitmap);
        rbd.setCircular(true);
        rbd.setAntiAlias(true);
        if (img != null) img.setImageDrawable(rbd);
    }

    /**
     * Список категорий
     */
    public void setListViewCategory() {
        ListView list = (ListView) findViewById(R.id.category_listview);
        ArrayList<String> categoryList = new ArrayList<String>();
        for (CategoryChp c : allCategory) {
            categoryList.add(c.Name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, categoryList);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        int i = 0;
        for (CategoryChp c : allCategory) {
            list.setItemChecked(i, myLocalStorage.GetBooleanSettings(c.NameOnEnglish));
            i++;
        }
        //list.setOnItemClickListener(this);
    }

    public void getFileAvatar(View view) {
        performFileSearch();
    }

    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "FileLogs";

    /**
     * Открываем диалог выбора файла
     */
    public void performFileSearch() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
//        }
    }

    /**
     * Ответ из диалога выбора файла
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                /**
                 * URI выбранного файла
                 * */
                uri = resultData.getData();
                Log.i(TAG, " " + GetPath.getPath(this, uri));
                new AccountRepositoryLoadAvatarPhotoTask().execute(GetPath.getPath(this, uri));
            }
        }
    }

    private View mProgressView;
    private View mFormView;
    private ProgressAnimation progress;

    /**
     * Тест
     */
    public void testClick(View view) {
        showAnimation();
    }

    /**
     * Анимация загрузки фото/сохранения настроек
     */
    public void showAnimation() {
        if (mProgressView == null) mProgressView = findViewById(R.id.lk_progress);
        if (mFormView == null) mFormView = findViewById(R.id.lk_form);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        progress = new ProgressAnimation(mProgressView, mFormView, shortAnimTime);
        progress.showProgress(true);
    }

    public void stopAnimation() {
        progress.showProgress(false);
    }

    class AccountRepositorySaveMyInfaTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return accountRepository.SaveMyInfa(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Myresponse res) {
            stopAnimation();
            if (res.Status == Myresponse.MyStatus.Ok) {
                //успешно сохранили всё
                //сохраняем в localsotrage
                ListView list = (ListView) findViewById(R.id.category_listview);
                int i = 0;
                for (CategoryChp one : allCategory) {
                    myLocalStorage.SaveBooleanSettings(one.NameOnEnglish, list.isItemChecked(i));
                    //нужно подписку отменить или наоборот подписать
                    i++;
                }
                Toast.makeText(getApplicationContext(), "Данные сохранены успешно", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), res.Message, Toast.LENGTH_LONG).show();
            }
        }
    }

    class AccountRepositoryLoadAvatarPhotoTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return accountRepository.LoadAvatarPhoto(params[0]);
        }

        @Override
        protected void onPostExecute(Myresponse res) {
            if (res.Status == Myresponse.MyStatus.Ok) {
                //успешно сохранили всё. Показываем новый аватар res.message url до него
                roundedAvatar2(R.id.lk_avatar_image, res.Message);
                Toast.makeText(getApplicationContext(), "Фото загружено успешно.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "Ошибка при загрузки фото.", Toast.LENGTH_LONG).show();
            }
        }
    }

  /*  class AccountRepositoryMyProfileTask extends AsyncTask<Void, Void, ApplicationUser> {
        @Override
        protected ApplicationUser doInBackground(Void... params) {
            return accountRepository.MyProfile();
        }

        @Override
        protected void onPostExecute(ApplicationUser res) {
            if (res != null) {
                ((EditText) findViewById(R.id.lk_name)).setText(res.Name);
                ((EditText) findViewById(R.id.vk_name)).setText(res.Vk);
            } else {
                Toast.makeText(getApplicationContext(), "Ошибка при получении данных с сервера...", Toast.LENGTH_LONG).show();
            }

        }
    }*/
}
