package com.example.andrey.myapplication2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrey.myapplication2.Core.LocalStorage;
import com.example.andrey.myapplication2.SimpleClass.Myresponse;
import com.example.andrey.myapplication2.rep.AccountRepository;

public class RestorePassActivity extends AppCompatActivity {
    private View mProgressView, mFromView;
private LocalStorage myLocalStorage;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_pass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressView = findViewById(R.id.restore_progress);
        mFromView = findViewById(R.id.restore_form);
        myLocalStorage=new LocalStorage(this.getApplicationContext());
        Init();

    }
    class AccountRepositoryResetPassword1CodeTask extends AsyncTask<String, Void, Myresponse> {
        @Override
        protected Myresponse doInBackground(String... params) {
            return AccountRepository.ResetPassword1(params[0]);
        }
        @Override
        protected void onPostExecute(Myresponse res) {
            ((Button) findViewById(R.id.restore_pass_btn)).setEnabled(true);
            TextInputLayout email_layout = (TextInputLayout) findViewById(R.id.edit_email_restore_layout);
            if (res.Status== Myresponse.MyStatus.Ok){
                email_layout.setErrorEnabled(false);
                Toast.makeText(getApplicationContext(), res.Message, Toast.LENGTH_LONG).show();
            }
            else{
                email_layout.setErrorEnabled(true);
                email_layout.setError(res.Message);
            }
        }
    }
    private void Init(){
        EditText editText = (EditText) findViewById(R.id.edit_email_restore);
        editText.setText(myLocalStorage.GetEmail());
    }
    public void RestoreButtonClick(View view) {
        //кнопка входа неактивная
        ((Button) findViewById(R.id.restore_pass_btn)).setEnabled(false);
        Toast.makeText(getApplicationContext(), "Идёт восстановление пароля...", Toast.LENGTH_LONG).show();
        new AccountRepositoryResetPassword1CodeTask().execute(
                ((EditText) findViewById(R.id.edit_email_restore)).getText().toString());
    }

}
