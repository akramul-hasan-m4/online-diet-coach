package com.daffodil.online.dietcoach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.db.repository.UserRepository;
import com.daffodil.online.dietcoach.model.Users;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout phoneLayout;
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
                String phone = inPhone.getText() == null ? "" : String.valueOf(inPhone.getText());
                String pass = inPassword.getText() == null ? "" : String.valueOf(inPassword.getText());
                if(phone.equalsIgnoreCase("01777999777") && pass.equalsIgnoreCase("1234") ){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else {
                    Toast.makeText(LoginActivity.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegActivity.class));
            }
        });
        Log.d("mtest", "onCreate: =====================");
        new UserRepository(this).findUser(null, new UserRepository.UserStatus() {

            @Override
            public void userIsLoaded(List<Users> users, List<String> keys) {
                for(Users user : users){
                    Log.d("mtest", "userIsLoaded: " + user.getAddress()
                            + "\n getUsername==>> "+ user.getUserName()
                            + "\n getFirstName==>> "+ user.getFirstName()
                            + "\n getLastName==>> "+ user.getLastName()
                            + "\n getAge==>> "+ user.getAge()
                            + "\n getPassword==>> "+ user.getPassword()
                            + "\n getPhone==>> "+ user.getPhone()
                            + "\n getEmail==>> "+ user.getEmail()
                            + "\n getBloodGroup==>> "+ user.getBloodGroup()
                            + " getUserId==>> "+ user.getUserId()
                    );
                }
            }
        });
        Log.d("mtest", "onCreate: ===========**************==========");
    }

    private void initView() {
        phoneLayout = findViewById(R.id.phin);
        inPhone = findViewById(R.id.in_phone);
        inPassword = findViewById(R.id.in_password);
        rememberMe = findViewById(R.id.remembar_me);
        tvNewUser = findViewById(R.id.tv_new_user);
    }
}
