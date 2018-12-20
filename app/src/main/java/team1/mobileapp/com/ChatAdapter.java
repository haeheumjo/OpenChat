package team1.mobileapp.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import team1.mobileapp.com.model.ChatMessage;
import team1.mobileapp.com.model.ChatRoom;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final String NOTICE = "1";

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    private ChatRoom chatRoom;
    private static List<ChatMessage> chatMessages;

    private String mStudentNumber;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        TextView textViewNotice;

        ConstraintLayout chatMessageI;
        TextView textViewMessageI;
        TextView textViewTimeI;

        TextView textViewNickNameOther;
        ConstraintLayout chatMessageOther;
        ImageView imageViewProfileOther;
        TextView textViewMessageOther;
        TextView textViewTimeOther;

        public ViewHolder(View v) {
            super(v);
            context = v.getContext();

            textViewNotice = v.findViewById(R.id.textViewNotice);

            textViewNickNameOther = v.findViewById(R.id.textViewNickNameOther);
            chatMessageI = v.findViewById(R.id.chatMessageI);
            textViewMessageI = v.findViewById(R.id.textViewMessageI);
            textViewTimeI = v.findViewById(R.id.textViewTimeI);

            imageViewProfileOther = v.findViewById(R.id.imageViewProfileOther);
            chatMessageOther = v.findViewById(R.id.chatMessageOther);
            textViewMessageOther = v.findViewById(R.id.textViewMessageOther);
            textViewTimeOther = v.findViewById(R.id.textViewTimeOther);
        }
    }

    public ChatAdapter(List<ChatMessage> chatMessages, String mStudentNumber, ChatRoom chatRoom) {
        this.chatMessages = chatMessages;
        this.mStudentNumber = mStudentNumber;
        this.chatRoom = chatRoom;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        ViewHolder vh = new ViewHolder(v);

        vh.imageViewProfileOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //directInfoActivity(view); todo
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String sender = chatMessages.get(position).getSender();
        String content = chatMessages.get(position).getContent();

        if (sender.equals("1")) {//입장
            holder.textViewNotice.setVisibility(View.VISIBLE);
            holder.chatMessageI.setVisibility(View.GONE);
            holder.textViewNickNameOther.setVisibility(View.GONE);
            holder.imageViewProfileOther.setVisibility(View.GONE);
            holder.chatMessageOther.setVisibility(View.GONE);

            holder.textViewNotice.setText(content + "님이 입장하셨습니다.");
        } else if(sender.equals("2")){//퇴장
            holder.textViewNotice.setVisibility(View.VISIBLE);
            holder.chatMessageI.setVisibility(View.GONE);
            holder.textViewNickNameOther.setVisibility(View.GONE);
            holder.imageViewProfileOther.setVisibility(View.GONE);
            holder.chatMessageOther.setVisibility(View.GONE);

            holder.textViewNotice.setText(content + "님이 퇴장하셨습니다.");
        } else if (sender.equals(mStudentNumber)) {//내 메세지

            holder.textViewNotice.setVisibility(View.GONE);
            holder.chatMessageI.setVisibility(View.VISIBLE);
            holder.textViewNickNameOther.setVisibility(View.GONE);
            holder.imageViewProfileOther.setVisibility(View.GONE);
            holder.chatMessageOther.setVisibility(View.GONE);

            long curTimeMillis = Long.valueOf(chatMessages.get(position).getTime());
            String now = convertTime(curTimeMillis);

            holder.textViewMessageI.setText(content);
            holder.textViewTimeI.setText(now);

        } else {//남의 메세지
            holder.textViewNotice.setVisibility(View.GONE);
            holder.chatMessageI.setVisibility(View.GONE);
            holder.textViewNickNameOther.setVisibility(View.VISIBLE);
            holder.imageViewProfileOther.setVisibility(View.VISIBLE);
            holder.chatMessageOther.setVisibility(View.VISIBLE);

            long curTimeMillis = Long.valueOf(chatMessages.get(position).getTime());
            String now = convertTime(curTimeMillis);

            mDatabaseReference.child("chatRoomsMemberInfo").child(chatRoom.getKey()).child(mStudentNumber).child("nickName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.textViewNickNameOther.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.textViewMessageOther.setText(content);
            holder.textViewTimeOther.setText(now);


            mStorageReference.child("chatRooms/" + chatRoom.getKey() + "/userProfileImage/" + mStudentNumber).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(holder.context).load(uri).into(holder.imageViewProfileOther);
                }

            });

        }
    }

    public String convertTime(long curTimeMillis) {
        Date date = new Date(curTimeMillis);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.KOREA);
        return dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
}
