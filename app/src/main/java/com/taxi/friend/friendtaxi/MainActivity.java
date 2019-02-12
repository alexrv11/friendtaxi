package com.taxi.friend.friendtaxi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.taxi.friend.friendtaxi.model.User;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static uk.co.chrisjenx.calligraphy.CalligraphyConfig.*;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;
    Button btnSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase db;
    DatabaseReference users;

    RelativeLayout rootLayout;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDefault(new CalligraphyConfig.Builder()
                                        .setDefaultFontPath("fonts/Roboto-MediumItalic.ttf")
                                        .setFontAttrId(R.attr.fontPath).build());

        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);


        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        //events
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SignIn");
        dialog.setMessage("Please use email to enter");

        LayoutInflater inflater = LayoutInflater.from(this);
        View loginLayout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText editEMail = loginLayout.findViewById(R.id.editMail);

        final MaterialEditText editPassword = loginLayout.findViewById(R.id.editPassword);


        dialog.setView(loginLayout);

        dialog.setPositiveButton("SignIn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                btnSignIn.setEnabled(false);


                if (TextUtils.isEmpty(editEMail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter your password", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                final AlertDialog waitingDialog = new AlertDialog.Builder(MainActivity.this).create();
                waitingDialog.show();

                firebaseAuth.signInWithEmailAndPassword(editEMail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                startActivity(new Intent(MainActivity.this, TaxiMapActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout, "Failure " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        btnSignIn.setEnabled(true);
                    }
                });

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registerLayout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText editEMail = registerLayout.findViewById(R.id.editMail);
        final MaterialEditText editName = registerLayout.findViewById(R.id.editName);
        final MaterialEditText editPassword = registerLayout.findViewById(R.id.editPassword);
        final MaterialEditText editPhone = registerLayout.findViewById(R.id.editPhone);

        dialog.setView(registerLayout);

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty(editEMail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPhone.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter your password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (editPassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(editEMail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(editEMail.getText().toString());
                                user.setPassword(editPassword.getText().toString());
                                user.setName(editName.getText().toString());
                                user.setPHone(editPhone.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Register success !!!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }
}
