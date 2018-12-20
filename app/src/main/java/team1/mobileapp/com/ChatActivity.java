package team1.mobileapp.com;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import team1.mobileapp.com.model.ChatMessage;
import team1.mobileapp.com.model.ChatRoom;

public class ChatActivity extends AppCompatActivity {

    private final int PERMISSION_READ_STORAGE = 1;
    private final int PICK_IMAGE = 1;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    private Uri imageUri;

    private Bundle bundle;


    ImageView imageViewProfile;
    Dialog dialog;
    TextView textViewProgress;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    RecyclerView recyclerViewChat;
    private RecyclerView.Adapter adapterChat;
    private RecyclerView.LayoutManager layoutManagerChat;
    private static List<ChatMessage> chatMessages= new ArrayList<>();
    private ChatMessage chatMessage;

    private ChildEventListener childEventListener;

    EditText editTextSend;
    Button buttonSend;


    String mStudentNumber;
    ChatRoom chatRoom;
    String roomKey;
    String randomNickName;
    boolean insert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bundle = getIntent().getExtras();
        mStudentNumber = bundle.getString("my_key");
        String roomInfo = bundle.getString("room_info");
        chatRoom = Utils.getGsonParser().fromJson(roomInfo, ChatRoom.class);
        roomKey = chatRoom.getKey();

        mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).child(mStudentNumber).child("nickName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    dialog = new Dialog(ChatActivity.this);
                    dialog.setContentView(R.layout.dialog_register_profile_user);
                    imageViewProfile = dialog.findViewById(R.id.imageViewProfile);
                    final TextView textViewNickName = dialog.findViewById(R.id.textViewNickName);
                    ImageButton imageButtonRefresh = dialog.findViewById(R.id.imageButtonRefresh);
                    Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);

                    final String[] randomNickNameFirst = getResources().getStringArray(R.array.name_first);
                    final String[] randomNickNameSecond = getResources().getStringArray(R.array.name_second);
                    randomNickName = randomNickNameFirst[new Random().nextInt(randomNickNameFirst.length)] + randomNickNameSecond[new Random().nextInt(randomNickNameSecond.length)];

                    textViewNickName.setText(randomNickName);
                    imageViewProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
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
                    imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            randomNickName = randomNickNameFirst[new Random().nextInt(randomNickNameFirst.length)] + randomNickNameSecond[new Random().nextInt(randomNickNameSecond.length)];
                            textViewNickName.setText(randomNickName);
                        }
                    });

                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).child(mStudentNumber).child("nickName").setValue(randomNickName, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    dialog.dismiss();
                                    if(imageUri!=null){
                                        uploadImage();
                                    }else{
                                        dialog.dismiss();
                                        mDatabaseReference.child("chat").child(roomKey).push().setValue(new ChatMessage(randomNickName, "1",mStudentNumber));
                                    }

                                }
                            });

                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

       getSupportActionBar().setTitle(chatRoom.getName());
       toolbar.setSubtitle(chatRoom.getDescription());

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setHasFixedSize(true);
        layoutManagerChat = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManagerChat);
        adapterChat = new ChatAdapter(chatMessages, mStudentNumber, chatRoom);
        recyclerViewChat.setAdapter(adapterChat);



        editTextSend = findViewById(R.id.editTextSend);
        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setEnabled(false);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatMessage = dataSnapshot.getValue(ChatMessage.class);
                if(chatMessage.getTime().equals(mStudentNumber)){
                    if(chatMessage.getSender().equals("1")){
                        insert = true;
                    }else{
                        insert = false;
                        chatMessages.clear();
                    }

                }
                if(insert){
                    chatMessages.add(chatMessage);
                    adapterChat.notifyItemInserted(chatMessages.size()-1);
                    recyclerViewChat.smoothScrollToPosition(chatMessages.size());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child("chat").child(roomKey).addChildEventListener(childEventListener);

        editTextSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    buttonSend.setBackgroundColor(getResources().getColor(R.color.white));
                    buttonSend.setTextColor(getResources().getColor(R.color.grey));
                    buttonSend.setEnabled(false);
                } else {
                    buttonSend.setBackgroundColor(getResources().getColor(R.color.knu_red));
                    buttonSend.setTextColor(getResources().getColor(R.color.white));
                    buttonSend.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextSend.getText().toString();
                String time = String.valueOf(System.currentTimeMillis());
                chatMessage = new ChatMessage(message, mStudentNumber, time);
                mDatabaseReference.child("chat").child(roomKey).push().setValue(chatMessage);
                editTextSend.setText("");

                mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                            mDatabaseReference.child("users").child(postSnapShot.getKey()).child("myChatRooms").child(roomKey).setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

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
                                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
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

    public void uploadImage() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_regiseter_profile_room, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this).setView(dialogView).setCancelable(false);

        dialog = builder.create();

        textViewProgress = dialogView.findViewById(R.id.textViewProgress);
        dialog.show();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mStorageReference.child("chatRooms/" + roomKey + "/userProfileImage/" + mStudentNumber).putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDatabaseReference.child("chat").child(roomKey).push().setValue(new ChatMessage(randomNickName, "1",mStudentNumber));
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).child(mStudentNumber).child("nickName").removeValue();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.upload_fail), Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        textViewProgress.setText((int) progress + "%");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            chatMessages.clear();
            mDatabaseReference.child("chat").child(roomKey).removeEventListener(childEventListener);
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatMessages.clear();
        mDatabaseReference.child("users").child(mStudentNumber).child("myChatRooms").child(roomKey).setValue(2);
        mDatabaseReference.child("chat").child(roomKey).removeEventListener(childEventListener);
    }
}
