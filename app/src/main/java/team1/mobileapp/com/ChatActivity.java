package team1.mobileapp.com;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    private Uri imageUri;

    private Bundle bundle;


    private String mStudentNumber;
    private String roomKey;
    String randomNickName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bundle = getIntent().getExtras();
        mStudentNumber = bundle.getString("my_key");
        roomKey = bundle.getString("room_key");
        mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).child(mStudentNumber).child("nickName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    final Dialog dialog = new Dialog(ChatActivity.this);
                    dialog.setContentView(R.layout.dialog_register_profile_user);
                    ImageView imageViewProfile = dialog.findViewById(R.id.imageViewProfile);
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
                            mDatabaseReference.child("chatRoomsMemberInfo").child(roomKey).child(mStudentNumber).child("nickName").setValue(randomNickName);
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
