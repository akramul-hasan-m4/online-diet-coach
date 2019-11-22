package com.daffodil.online.dietcoach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.db.local.SharedPreferencesConfig;
import com.daffodil.online.dietcoach.db.repository.UserRepository;
import com.daffodil.online.dietcoach.model.Users;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.CURRENT_USER;

public class LoginActivity extends AppCompatActivity implements TextWatcher {

    private TextInputLayout phoneLayout;
    private TextInputLayout passLayout;
    private TextInputEditText inPhone;
    private TextInputEditText inPassword;
    private CheckBox rememberMe;
    private TextView tvNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBar));
        initView();

        Button login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = inPhone.getText() == null ? "" : String.valueOf(inPhone.getText()).trim();
                final String pass = inPassword.getText() == null ? "" : String.valueOf(inPassword.getText()).trim();

                if(TextUtils.isEmpty(phone)){
                    phoneLayout.setError("Please Enter Phone Number");
                }else if(TextUtils.isEmpty(pass)){
                    passLayout.setError("Please Enter Password");
                }else {
                    Users users =  new Users();
                    users.setPhone(phone);
                    users.setPassword(pass);

                    new UserRepository(LoginActivity.this).findUser(users, new UserRepository.UserStatus() {

                        @Override
                        public void userIsLoaded(List<Users> users, List<String> keys) {
                            if(users.isEmpty()){
                                phoneLayout.setError("Invalid Phone Number");
                                return;
                            }
                            for(Users user : users){
                                if(!user.getPassword().equalsIgnoreCase(pass)){
                                    passLayout.setError("Please Enter valid Password");
                                }else {
                                    Gson gson = new Gson();
                                    String userJson = gson.toJson(user);
                                    SharedPreferencesConfig.saveStringData(Objects.requireNonNull(getApplicationContext()), CURRENT_USER, userJson);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            }
                        }
                    });
                }

            }
        });

        tvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegActivity.class));
            }
        });

    }

    private void initView() {
        phoneLayout = findViewById(R.id.phin);
        inPhone = findViewById(R.id.in_phone);
        inPassword = findViewById(R.id.in_password);
        rememberMe = findViewById(R.id.remembar_me);
        tvNewUser = findViewById(R.id.tv_new_user);
        passLayout = findViewById(R.id.login_password_layout);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
