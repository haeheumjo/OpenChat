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
import android.widget.Button;
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

import team1.mobileapp.com.model.ChatMember;
import team1.mobileapp.com.model.ChatRoom;

/**
 * Created by Haeheum Jo on 2018-03-05.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final int CHATROOM_STATUS_ACTIVE = 2;
    private final int CHATROOM_STATUS_ALERT = 1;
    private final int CHATROOM_STATUS_BANNED = 0;

    private final int POSITION_MEMBER = 2;

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

                    if(page==1){
                        ChatRoom chatRoom = mChatRooms.get(getAdapterPosition());
                        mDatabaseReference.child("users").child(mStudentNumber).child("myChatRooms").child(chatRoom.getKey()).setValue(CHATROOM_STATUS_ACTIVE);
                        directChatActivity(v, chatRoom);
                    }else{
                        final ChatRoom theChatRoom = chatRooms.get(getAdapterPosition());

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_observe_profile_room);
                        TextView textViewName = dialog.findViewById(R.id.textViewName);
                        final TextView textViewMemberCount = dialog.findViewById(R.id.textViewMemberCount);
                        TextView textViewDescription = dialog.findViewById(R.id.textViewDescription);
                        TextView textViewRoomInfo = dialog.findViewById(R.id.textViewRoomInfo);
                        final Button buttonEnter = dialog.findViewById(R.id.buttonEnter);
                        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                        textViewName.setText(chatRooms.get(getAdapterPosition()).getName());
                        textViewDescription.setText(chatRooms.get(getAdapterPosition()).getDescription());

                        mDatabaseReference.child("chatRoomsMemberInfo").child(theChatRoom.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int count = (int)dataSnapshot.getChildrenCount();
                                int maxCount = theChatRoom.getOptionMaxCount();
                                textViewMemberCount.setText("(" + count + "/" + maxCount +")");
                                if(!(count<=maxCount)){
                                    buttonEnter.setEnabled(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if(theChatRoom.getOptionName()==0&&theChatRoom.getOptionAge()==0&&theChatRoom.getOptionSex()==0&&theChatRoom.getOptionStudentNumber()==0&&theChatRoom.getOptionDepartment()==0){
                            textViewRoomInfo.setText(R.string.room_info_anonymous);
                        }else{
                            boolean isFirst = true;
                            String theRoomInfo = "이 채팅방은 ";

                            if(theChatRoom.getOptionName()==1){
                                theRoomInfo = theRoomInfo.concat("이름");
                                isFirst = false;
                            }

                            if(theChatRoom.getOptionAge()==1){
                                if(isFirst){
                                    theRoomInfo = theRoomInfo.concat("나이");
                                }else{
                                    theRoomInfo = theRoomInfo.concat(", 나이");
                                }
                                isFirst = false;
                            }

                            if(theChatRoom.getOptionSex()==1){
                                if(isFirst){
                                    theRoomInfo = theRoomInfo.concat("성별");
                                    theRoomInfo = theRoomInfo.concat(", 성별");
                                }
                                isFirst = false;
                            }

                            if(theChatRoom.getOptionStudentNumber()==1){
                                if(isFirst){
                                    theRoomInfo = theRoomInfo.concat("학번");
                                }else{
                                    theRoomInfo = theRoomInfo.concat(", 학번");
                                }
                                isFirst = false;
                            }

                            if(theChatRoom.getOptionDepartment()==1){
                                if(isFirst){
                                    theRoomInfo = theRoomInfo.concat("학과");
                                }else{
                                    theRoomInfo = theRoomInfo.concat(", 학과");
                                }
                            }
                            theRoomInfo = theRoomInfo.concat(" 정보를 상호 공개하는 채팅방입니다.");
                            textViewRoomInfo.setText(theRoomInfo);
                        }



                        buttonEnter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDatabaseReference.child("chatRoomsMemberInfo").child(theChatRoom.getKey()).child(mStudentNumber).setValue(new ChatMember(mStudentNumber,POSITION_MEMBER));
                                mDatabaseReference.child("users").child(mStudentNumber).child("myChatRooms").child(theChatRoom.getKey()).setValue(CHATROOM_STATUS_ALERT);
                                directChatActivity(v, theChatRoom);
                                dialog.dismiss();
                            }
                        });

                        buttonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }

                }
            });
            imageViewProfile = v.findViewById(R.id.imageViewProfile);
            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_observe_image_room);
                    final ImageView imageViewProfileObserve = dialog.findViewById(R.id.imageViewProfileObserve);
                    imageViewProfileObserve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    if(page==1){
                        mStorageReference.child("chatRooms/"+ mChatRooms.get(getAdapterPosition()).getKey() + "/profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                GlideApp.with(context).load(uri).fitCenter().into(imageViewProfileObserve);
                            }
                        });
                    }else{
                        mStorageReference.child("chatRooms/"+ chatRooms.get(getAdapterPosition()).getKey() + "/profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                GlideApp.with(context).load(uri).fitCenter().into(imageViewProfileObserve);
                            }
                        });
                    }

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



        if(page==1){
            mStorageReference.child("chatRooms/"+ mChatRooms.get(position).getKey() + "/profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(holder.context).load(uri).fitCenter().placeholder(R.drawable.img_default_profile_room).into(holder.imageViewProfile);
                }
            });

            holder.textViewName.setText(mChatRooms.get(position).getName());
            holder.textViewDescription.setText(mChatRooms.get(position).getDescription());

            mDatabaseReference.child("users").child(mStudentNumber).child("myChatRooms").child(mChatRooms.get(position).getKey()).addValueEventListener(new ValueEventListener() {
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
            mStorageReference.child("chatRooms/"+ chatRooms.get(position).getKey() + "/profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(holder.context).load(uri).fitCenter().placeholder(R.drawable.img_default_profile_room).into(holder.imageViewProfile);
                }
            });

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

    public void directChatActivity(View view,ChatRoom chatRoom){
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        String roomInfo = Utils.getGsonParser().toJson(chatRoom);
        intent.putExtra("room_info", roomInfo);
        view.getContext().startActivity(intent);
    }

}
