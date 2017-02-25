package com.example.andrey.myapplication2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrey.myapplication2.Core.LocalStorage;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.example.andrey.myapplication2.rep.AccountRepository;

public class LoginActivity extends AppCompatActivity {
    private View mProgressView, mProgressViewReg;
    private View mLoginFormView, mRegFormView;
    private LocalStorage myLocalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
        * Вкладки Вход/Регистрация
        * */
        ViewPager loginPager = (ViewPager) findViewById(R.id.login_viewpager);
        LoginTabsPagerAdapter tabsAdapter = new LoginTabsPagerAdapter(getSupportFragmentManager());
        SignInFragment s=new SignInFragment();

        tabsAdapter.addFragment(s, "Вход");
        tabsAdapter.addFragment(new SignUpFragment(), getResources().getString(R.string.tab_sign_up));
        loginPager.setAdapter(tabsAdapter);
        TabLayout loginTabs = (TabLayout) findViewById(R.id.login_tabs);
        loginTabs.setupWithViewPager(loginPager);

        myLocalStorage=new LocalStorage(this.getApplicationContext());

    }

    private ProgressAnimation progress_login, progress_reg;

    /**
     * Анимация авторизации. Вкладка Вход
     */
    public void showAnimationLogin() {
        if (mProgressView == null) mProgressView = findViewById(R.id.login_progress);
        if (mLoginFormView == null) mLoginFormView = findViewById(R.id.login_form);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        progress_login = new ProgressAnimation(mProgressView, mLoginFormView, shortAnimTime);
        progress_login.showProgress(true);
    }

    public void stopAnimationLogin() {
        progress_login.showProgress(false);
    }

    /**
     * Анимация авторизации. Вкладка Регистрация
     */
    public void showAnimationReg() {
        if (mProgressViewReg == null) mProgressViewReg = findViewById(R.id.reg_progress);
        if (mRegFormView == null) mRegFormView = findViewById(R.id.reg_form);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        progress_reg = new ProgressAnimation(mProgressViewReg, mRegFormView, shortAnimTime);
        progress_reg.showProgress(true);
    }

    public void stopAnimationReg() {
        progress_reg.showProgress(false);
    }

    /**
     * Пример, как выводить ошибки на форме
     * */
    private void checkFieldsReg() {
        TextInputLayout name_input_layout = (TextInputLayout) findViewById(R.id.edit_name_reg_layout);
        TextInputLayout pass_input_layout = (TextInputLayout) findViewById(R.id.edit_pass_reg_layout);
        EditText name_edit = (EditText) findViewById(R.id.edit_name_reg);

        /**
         * Ошибка Поле Имя пусто
         * */
        if (name_edit.getText().toString().isEmpty()) {
            name_input_layout.setErrorEnabled(true);
            name_input_layout.setError(getResources().getString(R.string.error_name));
        }
        else {
            name_input_layout.setErrorEnabled(false);
        }

        /**
         * Ошибка Email/пароль
         * */
        pass_input_layout.setErrorEnabled(true);
        pass_input_layout.setError(getResources().getString(R.string.error_login));
    }

    public void forgetPassClick(View view) {
        Intent intent = new Intent(this, RestorePassActivity.class);
        startActivity(intent);
    }


    class AccountRepositoryGetTokenTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return AccountRepository.GetToken(params[0],params[1]);
        }
        @Override
        protected void onPostExecute(Myresponse res) {
            stopAnimationLogin();
            TextInputLayout pass_input_layout = (TextInputLayout) findViewById(R.id.edit_pass_layout);
            if (res.Status== Myresponse.MyStatus.Ok){
                //сохраняем email и
                myLocalStorage.SaveEmail(((EditText) findViewById(R.id.edit_email)).getText().toString());
                //может быть пароль не хранить?
                myLocalStorage.SavePassword(((EditText) findViewById(R.id.edit_pass)).getText().toString());
                //сохраняем token
                myLocalStorage.SaveBearerToken(res.Message);
                //закрываем activiry
                finish();
            }
            else{
                pass_input_layout.setErrorEnabled(true);
                pass_input_layout.setError(res.Message);
            }
        }
    }
    //обработка нажатия кнопки входа
    public void LoginButtonClick(View view) {
        //кнопка входа неактивная
        showAnimationLogin();
        new AccountRepositoryGetTokenTask().execute(
                ((EditText) findViewById(R.id.edit_email)).getText().toString(),
                ((EditText) findViewById(R.id.edit_pass)).getText().toString());
    }

    //register
    class AccountRepositoryRegisterTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return AccountRepository.Register(params[0],params[1],params[2]);
        }
        @Override
        protected void onPostExecute(Myresponse res) {
            //кнопка регистарции вновь станет активной
            stopAnimationReg();
            TextInputLayout pass_input_layout = (TextInputLayout) findViewById(R.id.edit_pass_reg_layout);
            if (res.Status == Myresponse.MyStatus.Ok){
                //сохраняем email и  password
                myLocalStorage.SaveEmail( ((EditText) findViewById(R.id.edit_email_reg)).getText().toString());
                myLocalStorage.SavePassword( ((EditText) findViewById(R.id.edit_pass_reg)).getText().toString());
                ((EditText) findViewById(R.id.edit_email)).setText(((EditText) findViewById(R.id.edit_email_reg)).getText().toString());
                ((EditText) findViewById(R.id.edit_pass)).setText(((EditText) findViewById(R.id.edit_pass_reg)).getText().toString());
                //ошибок нет
                pass_input_layout.setErrorEnabled(false);
                Toast.makeText(getApplicationContext(), res.Message, Toast.LENGTH_LONG).show();
            }
            else{
                pass_input_layout.setErrorEnabled(true);
                pass_input_layout.setError(res.Message);
            }
        }
    }


    //обработка нажатия кнопки регистрации
    public void RegisterButtonClick(View view) {
        //кнопка регистрации неактивная
        showAnimationReg();
        new AccountRepositoryRegisterTask().execute(
                ((EditText) findViewById(R.id.edit_name_reg)).getText().toString(),
                ((EditText) findViewById(R.id.edit_email_reg)).getText().toString(),
                ((EditText) findViewById(R.id.edit_pass_reg)).getText().toString());
    }
}
