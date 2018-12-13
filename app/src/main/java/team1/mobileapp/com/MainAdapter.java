package team1.mobileapp.com;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import team1.mobileapp.com.model.ChatRoom;

/**
 * Created by Haeheum Jo on 2018-03-05.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    public final int CHATROOM_STATUS_ACTIVE = 2;
    public final int CHATROOM_STATUS_ALERT = 1;
    public final int CHATROOM_STATUS_BANNED = 0;


    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    private Bundle bundle;
    private String mStudentNumber;
    private int page;

    public static ArrayList<ChatRoom> mChatRooms;
    public static ArrayList<ChatRoom> chatRooms;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ConstraintLayout background;
        private ImageView imageViewProfile;
        private TextView textViewName;
        private TextView textViewDescription;
        private View viewAlert;

        public ViewHolder(View v) {
            super(v);
            context = v.getContext();

            background = v.findViewById(R.id.background);
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String roomKey = mChatRooms.get(getAdapterPosition()).getKey();
                    directChatActivity(v, roomKey);
                }
            });
            imageViewProfile = v.findViewById(R.id.imageViewProfile);
            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_observe);
                    final ImageView imageViewProfileObserve = dialog.findViewById(R.id.imageViewProfileObserve);
                    imageViewProfileObserve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    mStorageReference.child("chatRooms/"+ mChatRooms.get(getAdapterPosition()).getKey() + "/profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            GlideApp.with(context).load(uri).fitCenter().into(imageViewProfileObserve);
                        }
                    });
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            });

            textViewName = v.findViewById(R.id.textViewName);
            textViewDescription = v.findViewById(R.id.textViewDescription);
            viewAlert = v.findViewById(R.id.viewAlert);
        }
    }

    public MainAdapter(Bundle bundle, String mStudentNumber, ArrayList<ChatRoom> chatRooms, int page) {
        this.bundle = bundle;
        this.mStudentNumber = mStudentNumber;

        if(page==1){
            mChatRooms=chatRooms;
        }else{
            this.chatRooms=chatRooms;
        }
        this.page = page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        final ViewHolder vh = new ViewHolder(v);



        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        mStorageReference.child("chatRooms/"+ mChatRooms.get(position).getKey() + "/profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(holder.context).load(uri).fitCenter().placeholder(R.drawable.img_default_profile_room).into(holder.imageViewProfile);
            }
        });


        if(page==1){
            holder.textViewName.setText(mChatRooms.get(position).getName());
            holder.textViewDescription.setText(mChatRooms.get(position).getDescription());

            mDatabaseReference.child("users").child(mStudentNumber).child("myChatRooms").child(mChatRooms.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(Integer.parseInt(dataSnapshot.getValue().toString())==CHATROOM_STATUS_ALERT){
                        holder.viewAlert.setVisibility(View.VISIBLE);
                    }else{//노말상태
                        holder.viewAlert.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            holder.textViewName.setText(chatRooms.get(position).getName());
            holder.textViewDescription.setText(chatRooms.get(position).getDescription());
            holder.viewAlert.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (page==1) {
            return mChatRooms.size();
        }else{
            return chatRooms.size();
        }

    }

    public void directChatActivity(View view,String roomKey){
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("room_key", roomKey);
        view.getContext().startActivity(intent);
    }

}
