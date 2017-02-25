package com.example.andrey.myapplication2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrey.myapplication2.Core.AllConfig;
import com.example.andrey.myapplication2.Core.LocalStorage;
import com.example.andrey.myapplication2.SimpleClass.ApplicationUser;
import com.example.andrey.myapplication2.SimpleClass.CategoryChp;
import com.example.andrey.myapplication2.SimpleClass.CategoryandDuration;
import com.example.andrey.myapplication2.SimpleClass.Durationday;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.example.andrey.myapplication2.rep.AccountRepository;
import com.example.andrey.myapplication2.rep.ChpRepository;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener {
    //    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int MICROPHONE_REQUEST_CODE = 121;

    public enum Mode {
        NORMAL, REMOVE, SEARCH
    }

    public static Mode mode;
    private Toolbar toolbar;
    private SearchView mSearchView;
    private MenuItem microMenuItem;
    private MenuItem removeMenuItem;
    private MenuItem searchMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mode = Mode.NORMAL;

        final Intent createIntent = new Intent(this, CreateActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //передаем список категорий
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<CategoryChp>>() {}.getType();
                Type type2 = new TypeToken<ArrayList<Durationday>>(){}.getType();
                createIntent.putExtra("CategoryChp", gson.toJson(categoryandDuration.CategoryChp, type));
                createIntent.putExtra("Durationday", gson.toJson(categoryandDuration.Durationday, type2));
                startActivity(createIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Вкладки Карта/Список
         * */
        ViewPager mainPager = (ViewPager) findViewById(R.id.main_viewpager);
        MainTabsPagerAdapter tabsPagerAdapter = new MainTabsPagerAdapter(getSupportFragmentManager());
        tabsPagerAdapter.addFragment(new MyMapFragment(), getResources().getString(R.string.tab_map));
        tabsPagerAdapter.addFragment(new ListFragment(), "Список");
        mainPager.setAdapter(tabsPagerAdapter);
        TabLayout mainTabs = (TabLayout) findViewById(R.id.main_tabs);
        mainTabs.setupWithViewPager(mainPager);


        myLocalStorage = new LocalStorage(this.getApplicationContext());
        MyInit();

    }

    @Override
    public void onBackPressed() {
        if (mode == Mode.SEARCH) {
            searchMenuItem.collapseActionView();
        } else if (mode != Mode.NORMAL) {
            startMode(Mode.NORMAL);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        microMenuItem = menu.findItem(R.id.action_micro);
        removeMenuItem = menu.findItem(R.id.action_remove);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
        startMode(Mode.NORMAL);

        /**
         * Круглый аватар в левом меню
         * */
        //roundedAvatar(R.id.imageView, R.drawable.img_stathem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_remove) {
            startMode(Mode.NORMAL);
        } else if (id == R.id.action_micro) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint));
            try {
                startActivityForResult(intent, MICROPHONE_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(),
                        "Your device doesn't support Speech to Text",
                        Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROPHONE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.i("Speech", result.get(0));
            if (!result.isEmpty()) {
                mSearchView.setQuery(result.get(0), true);
            }
        }
        switch (requestCode) {
            case REQUEST_CODE_B:
                MyInit();
                //you just got back from activity B - deal with resultCode
                //use data.getExtra(...) to retrieve the returned data
                break;
            case REQUEST_CODE_C:
                //you just got back from activity C - deal with resultCode
                break;
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_login) {
            myLocalStorage.ClearBearerToken();
            MyInit();
            //intent = new Intent(this, LoginActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_profile) {

            intent = new Intent(this, LKActivity.class);
            //передаем список категорий
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CategoryChp>>() {}.getType();
            Type type2 = new TypeToken<ApplicationUser>(){}.getType();
            intent.putExtra("CategoryChp", gson.toJson(categoryandDuration.CategoryChp, type));
            intent.putExtra("User", gson.toJson(categoryandDuration.User, type2));
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, PreferActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        if (img != null) img.setImageDrawable(rbd);
    }


    public void roundedAvatar2(int img_id, String imageUri) {
        Resources mRes = getApplicationContext().getResources();
        ImageView img = (ImageView) findViewById(img_id);
        Bitmap mBitmap = null;
        try {
            mBitmap= new AccountRepository.AsyncGettingBitmapFromUrl().execute(imageUri).get();
        }
        catch (Exception ex)
        {
            return;
        }
        if (mBitmap==null) return;
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(mRes, mBitmap);
        rbd.setCircular(true);
        rbd.setAntiAlias(true);
        if (img != null) img.setImageDrawable(rbd);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        startMode(Mode.SEARCH);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        startMode(Mode.NORMAL);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i("Search", "text change");
        return false;
    }

    private void startMode(Mode modeToStart) {
        if (modeToStart == Mode.NORMAL) {
            removeMenuItem.setVisible(false);
            microMenuItem.setVisible(false);
            searchMenuItem.setVisible(true);
            toolbar.setLogo(null);
            toolbar.setTitle(getString(R.string.app_name));
            mode = modeToStart;
            return;
        } else if (modeToStart == Mode.REMOVE) {
            searchMenuItem.setVisible(false);
            microMenuItem.setVisible(false);
            removeMenuItem.setVisible(true);
            toolbar.setTitle("");
        } else if (modeToStart == Mode.SEARCH) {
            removeMenuItem.setVisible(false);
            searchMenuItem.setVisible(false);
            microMenuItem.setVisible(true);
        }
        mode = modeToStart;
    }

    //chprepository
    class ChpRepositoryGetAllCategoryAndDurationdayTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            HttpRequest response = (new ChpRepository(params[0])).GetAllCategoryAndDurationday();
            return Myresponse.GetBadRequest(response.code(), response.body());
        }
        @Override
        protected void onPostExecute(Myresponse response) {
            if (response.Code == 200) {
                try {
                    JSONObject jObject = new JSONObject(response.Message);
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Durationday>>() {
                    }.getType();
                    Type type2 = new TypeToken<ArrayList<CategoryChp>>() {
                    }.getType();
                    Type type3 = new TypeToken<ApplicationUser>() {
                    }.getType();
                    categoryandDuration = new CategoryandDuration();
                    categoryandDuration.CategoryChp = gson.fromJson(jObject.getString("CategoryChp"), type2);
                    categoryandDuration.Durationday = gson.fromJson(jObject.getString("Durationday"), type);
                    categoryandDuration.User = gson.fromJson(jObject.getString("User"), type3);
                    //проверка первого запуска приложения вообще
                    FirstInitialSetup();
                    //категории выводим
                    Spinner sp = (Spinner) findViewById(R.id.spinner_category_main);
                    ArrayList<String> categoryList = new ArrayList<String>();
                    categoryList.add("Все происшествия");
                    for (CategoryChp c : categoryandDuration.CategoryChp) {
                        categoryList.add(c.Name);
                    }

                    ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
                    categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(categoriesAdapter);

                    //текущего пользвоателя выводим
                    ((TextView) findViewById(R.id.userNameId)).setText(categoryandDuration.User.Name);
                    //аватар проставляем
                    //roundedAvatar(R.id.imageView, R.drawable.img_stathem);
                    roundedAvatar2(R.id.imageView, categoryandDuration.User.Avatar);
                } catch (JSONException e) {

                }
            } else {
                //unotrhized
                if (response.Code == 401) {
                    //отказ авторизации, token неправлиьный
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_B);
                    //startActivity(intent);
                } else {
                    //ошибка соединения с сервером
                    Toast.makeText(getApplicationContext(), "Нет соединения с сервером.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //проверка первоначальной настройки
    private void FirstInitialSetup() {
        if (!myLocalStorage.GetFirstRunParametr()) {
            //значит первый запуск,
            //сохраняем список подписок(тем) в localsotare
            //myLocalStorage.SaveIsEnableNotifications(true);
            //myLocalStorage.SaveIsEnableSoundInNotif(true);
            //подписываемся на все оповещения
            for (CategoryChp categ : categoryandDuration.CategoryChp) {
                ChpRepository.SubscribeToTopic(categ.NameOnEnglish);
                myLocalStorage.SaveBooleanSettings(categ.NameOnEnglish, true);
            }
            //первый запуск успешен, делаем отметку
            myLocalStorage.SaveFirstRunParametr();
        }
    }

    //инциализация.
    private void MyInit() {
        String BeareToken = myLocalStorage.GetBearerToken();
        if (AllConfig.empty(BeareToken)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_B);
        } else {
            new ChpRepositoryGetAllCategoryAndDurationdayTask().execute(BeareToken);
        }
    }

    public final static int REQUEST_CODE_B = 1;
    public final static int REQUEST_CODE_C = 2;

    //все категории и дни
    CategoryandDuration categoryandDuration;
    //хранилище настроек
    LocalStorage myLocalStorage;
}
