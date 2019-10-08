package com.example.todolist;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPassowordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Database db;
    private EditText ETemail;
    private EditText ETpassword;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_passoword);
        db=new Database(EmailPassowordActivity.this);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent intent = new Intent(EmailPassowordActivity.this, MainAndNavigation.class);
                    intent.putExtra("idUser", user.getUid());//передаю в главное активити id пользователя который вошел
                    startActivity(intent);

                } else {
                    // User is signed out



                }

            }
        };

        ETemail = (EditText) findViewById(R.id.et_email);
        ETpassword = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        EditText editEmail=(EditText)findViewById(R.id.et_email);
        EditText editPass=(EditText)findViewById(R.id.et_password);
        if (view.getId() == R.id.btn_sign_in) {
            //    User user = db.getUser(editEmail.getText().toString());

            if (!isEmpty(editEmail) && !isEmpty(editPass)) {
                signin(ETemail.getText().toString(), ETpassword.getText().toString());
            } else {
                if (isEmpty(editEmail)) {
                    editPass.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
                    editEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);

                } else if (isEmpty(editPass)) {
                    editEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
                    editPass.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                }
            }
        } else if (view.getId() == R.id.btn_registration) {
            db.addUser(new User(editEmail.getText().toString(), editPass.getText().toString()));
            if (!isEmpty(editEmail) && !isEmpty(editPass)) {
                registration(ETemail.getText().toString(), ETpassword.getText().toString());
            } else {
                if (isEmpty(editEmail)) {
                    editPass.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
                    editEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);

                } else if (isEmpty(editPass)) {
                    editEmail.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
                    editPass.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signin(String email, String password) {
        EditText editEmail=(EditText)findViewById(R.id.et_email);
        EditText editPass=(EditText)findViewById(R.id.et_password);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(EmailPassowordActivity.this, "Авторизаци успешна", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EmailPassowordActivity.this, MainAndNavigation.class);
                    intent.putExtra("idUser", user.getUid());//передаю в главное активити id пользователя который вошел
                    startActivity(intent);


                } else
                    Toast.makeText(EmailPassowordActivity.this, "Авторизация провалена", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void registration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPassowordActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(EmailPassowordActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

}
