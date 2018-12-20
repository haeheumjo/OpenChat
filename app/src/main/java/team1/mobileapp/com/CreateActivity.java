package team1.mobileapp.com;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

import team1.mobileapp.com.model.ChatMember;
import team1.mobileapp.com.model.ChatRoom;

public class CreateActivity extends AppCompatActivity {

    public final int CHATROOM_STATUS_ACTIVE = 2;
    public final int CHATROOM_STATUS_ALERT = 1;
    public final int CHATROOM_STATUS_BANNED = 0;

    private final int POSITION_MASTER = 0;
    private final int POSITION_MANAGER = 1;
    private final int POSITION_MEMBER = 2;

    private final int PERMISSION_READ_STORAGE = 1;
    private final int PICK_IMAGE = 1;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    private Uri imageUri;
    private android.app.AlertDialog dialog;
    TextView textViewProgress;

    ImageView imageViewProfile;

    View viewName;
    View viewDescription;

    EditText editTextName;
    EditText editTextDescription;

    TextView textViewMaxCount;
    ImageButton imageButtonDecrease;
    ImageButton imageButtonIncrease;

    CheckBox checkBoxName;
    CheckBox checkBoxAge;
    CheckBox checkBoxSex;
    CheckBox checkBoxStudentNumber;
    CheckBox checkBoxDepartment;

    Button buttonCreate;

    Bundle bundle = new Bundle();

    private String name;
    private String studentNumber;
    private int gender;
    private int age;
    private int birthYear;
    private String department;

    private String roomName;
    private String roomDescription;
    int optionMaxCount;
    int optionName;
    int optionAge;
    int optionSex;
    int optionStudentNumber;
    int optionDepartment;

    String roomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        bundle = getIntent().getExtras();

        studentNumber = bundle.getString("my_key");

        imageViewProfile = findViewById(R.id.imageViewProfile);

        viewName = findViewById(R.id.viewName);
        viewDescription = findViewById(R.id.viewDescription);

        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);

        textViewMaxCount = findViewById(R.id.textViewMaxCount);
        imageButtonDecrease = findViewById(R.id.imageButtonDecrease);
        imageButtonIncrease = findViewById(R.id.imageButtonIncrease);

        checkBoxName = findViewById(R.id.checkBoxName);
        checkBoxAge = findViewById(R.id.checkBoxAge);
        checkBoxSex = findViewById(R.id.checkBoxSex);
        checkBoxStudentNumber = findViewById(R.id.checkBoxStudentNumber);
        checkBoxDepartment = findViewById(R.id.checkBoxDepartment);

        buttonCreate = findViewById(R.id.buttonCreate);

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(CreateActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.profile_image_set)), PICK_IMAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    viewName.setBackgroundResource(R.color.knu_purple);
                } else {
                    viewName.setBackgroundResource(R.color.knu_red);
                }
            }
        });

        editTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    viewDescription.setBackgroundResource(R.color.knu_purple);
                } else {
                    viewDescription.setBackgroundResource(R.color.knu_red);
                }
            }
        });

        imageButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionMaxCount = Integer.parseInt(textViewMaxCount.getText().toString());
                if(optionMaxCount>2){//최소 인원수 2
                    optionMaxCount-=1;
                    textViewMaxCount.setText(String.valueOf(optionMaxCount));
                }
            }
        });

        imageButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionMaxCount = Integer.parseInt(textViewMaxCount.getText().toString());
                if(optionMaxCount<20){//최대 인원수 20
                    optionMaxCount+=1;
                    textViewMaxCount.setText(String.valueOf(optionMaxCount));
                }
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCreate.setEnabled(false);
                attemptCreate();
            }
        });

        ConstraintLayout background = findViewById(R.id.background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextName.clearFocus();
                editTextDescription.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextDescription.getWindowToken(), 0);

            }
        });
    }

    private void attemptCreate() {
        // Reset errors.
        editTextName.setError(null);
        editTextDescription.setError(null);

        // Store values at the time of the login attempt.
        roomName = editTextName.getText().toString();
        roomDescription = editTextDescription.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(roomName)) {
            Toast.makeText(getApplicationContext(), getString(R.string.create_fail_empty_name), Toast.LENGTH_LONG).show();
            focusView = editTextName;
            cancel = true;
        } else if (TextUtils.isEmpty(roomDescription)) {
            Toast.makeText(getApplicationContext(), getString(R.string.create_fail_empty_description), Toast.LENGTH_LONG).show();
            focusView = editTextDescription;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            buttonCreate.setEnabled(true);
        } else {

            optionMaxCount = Integer.parseInt(textViewMaxCount.getText().toString());

            optionName = checkBoxName.isChecked() ? 1 : 0;
            optionAge = checkBoxAge.isChecked() ? 1 : 0;
            optionSex = checkBoxSex.isChecked() ? 1 : 0;
            optionStudentNumber = checkBoxStudentNumber.isChecked() ? 1 : 0;
            optionDepartment = checkBoxDepartment.isChecked() ? 1 : 0;


            mDatabaseReference.child("chatRoomsMemberInfo").push().child(studentNumber).setValue(new ChatMember(studentNumber, POSITION_MASTER), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    roomKey = databaseReference.getParent().getKey();
                    mDatabaseReference.child("chatRooms").child(roomKey).setValue(new ChatRoom(roomKey, roomName, roomDescription, optionMaxCount, optionName, optionAge, optionSex, optionStudentNumber, optionDepartment), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            mDatabaseReference.child("users").child(studentNumber).child("myChatRooms").child(roomKey).setValue(CHATROOM_STATUS_ACTIVE, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if(imageUri!=null){
                                        uploadImage();
                                    }else{
                                        finish();
                                        buttonCreate.setEnabled(true);
                                    }
                                }
                            });
                        }
                    });

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.profile_image_set)), PICK_IMAGE);
                } else {
                    new android.support.v7.app.AlertDialog.Builder(this).setTitle(getString(R.string.user_permission_failed))
                            .setMessage(getString(R.string.user_permission_failed_message)).setCancelable(false)
                            .setPositiveButton(getString(R.string.user_permission_allow), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (ActivityCompat.checkSelfPermission(CreateActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(CreateActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.user_permission_deny), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_IMAGE):
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    Glide.with(getApplicationContext()).load(imageUri).into(imageViewProfile);

                } else {
                    Toast.makeText(this, getString(R.string.user_profile_image_null), Toast.LENGTH_LONG).show();
                }
                break;

        }

    }

    public void uploadImage(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_regiseter_profile_room, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this).setView(dialogView).setCancelable(false);

        dialog = builder.create();

        textViewProgress = dialogView.findViewById(R.id.textViewProgress);
        dialog.show();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mStorageReference.child("chatRooms/" + roomKey + "/profileImage").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        finish();
                        buttonCreate.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {//undo
                        mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).removeValue();
                        mDatabaseReference.child("chatRooms").child(roomKey).removeValue();
                        mDatabaseReference.child("users").child(studentNumber).child("myChatRooms").child(roomKey).removeValue();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_LONG).show();
                        buttonCreate.setEnabled(true);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        textViewProgress.setText((int)progress + "%");
                    }
                });
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this).setTitle(getString(R.string.return_to_main)).setMessage(getString(R.string.return_to_main_message)).setCancelable(false)
                .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton(getString(R.string.no), null).show();
    }
}