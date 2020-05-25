package com.example.heroku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @BindView(R.id.et_newPassword)
    EditText et_password;

    @BindView(R.id.et_confirmPassword)
    EditText et_confirmPassword;

    @BindView(R.id.btn_resetSubmit)
    Button btn_resetSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_resetSubmit)
    public void resetSubmit(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String password = et_password.getText().toString().trim();
        String rpassword = et_confirmPassword.getText().toString().trim();

        if (!password.equals(rpassword)){
            Toast.makeText(ResetPasswordActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }
        else if(password.length()<6){
            et_password.setError("Password length at least 6 characters");
            return;
        }
        else {
            user.updatePassword(et_password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "Password send to your email", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
