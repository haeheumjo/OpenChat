package team1.mobileapp.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import team1.mobileapp.com.model.ChatRoom;


public class MainFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    private static final String ARG_PAGE = "page";

    private RecyclerView recyclerViewMain;
    private TextView emptyView;
    private RecyclerView.Adapter adapterMain;
    private RecyclerView.LayoutManager layoutManagerMain;

    private static ArrayList<ChatRoom> mChatRooms = new ArrayList<>();
    private static ArrayList<ChatRoom> chatRooms = new ArrayList<>();
    private ChatRoom chatRoom;
    private static ArrayList<String> mChatRoomsKeys = new ArrayList<>();

    Bundle bundle;
    private String mKey;
    private int page;

    ValueEventListener valueEventListener;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance(int page, Bundle bundle) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putAll(bundle);
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        page = getArguments().getInt(ARG_PAGE);
        mKey = getArguments().getString("my_key");
        bundle = getArguments();

        recyclerViewMain = rootView.findViewById(R.id.recyclerViewMain);
        emptyView = rootView.findViewById(R.id.emptyView);

        if (chatRooms.isEmpty()) {
            recyclerViewMain.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewMain.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        recyclerViewMain.setHasFixedSize(true);
        layoutManagerMain = new LinearLayoutManager(getActivity());
        recyclerViewMain.setLayoutManager(layoutManagerMain);

        Button buttonBottom = rootView.findViewById(R.id.buttonBottom);

        if (page == 1) {//내채팅방목록

            adapterMain = new MainAdapter(bundle, mKey, mChatRooms, page);
            recyclerViewMain.setAdapter(adapterMain);
            recyclerViewMain.setItemAnimator(new DefaultItemAnimator());

            mDatabaseReference.child("users").child(mKey).child("myChatRooms").orderByValue().startAt(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mChatRooms.clear();
                    if (dataSnapshot.exists()) {
                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            mDatabaseReference.child("chatRooms").child(postSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        chatRoom = dataSnapshot.getValue(ChatRoom.class);
                                        mChatRooms.add(chatRoom);
                                        adapterMain.notifyDataSetChanged();
                                        if (mChatRooms.isEmpty()) {
                                            recyclerViewMain.setVisibility(View.GONE);
                                            emptyView.setVisibility(View.VISIBLE);
                                        } else {
                                            recyclerViewMain.setVisibility(View.VISIBLE);
                                            emptyView.setVisibility(View.GONE);
                                        }
                                    }else{
                                        mDatabaseReference.child("users").child(mKey).child("myChatRooms").child(postSnapshot.getKey()).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        recyclerViewMain.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            buttonBottom.setText(getString(R.string.open_chat_create));
            buttonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    directCreateActivity(getArguments());
                }
            });
        } else {//채팅방둘러보기
            adapterMain = new MainAdapter(bundle, mKey, chatRooms, page);
            recyclerViewMain.setAdapter(adapterMain);
            recyclerViewMain.setItemAnimator(new DefaultItemAnimator());

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        chatRooms.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            boolean isMyRoom = false;
                            for (String mChatRoomKey : mChatRoomsKeys) {
                                if (postSnapshot.child("key").getValue().equals(mChatRoomKey)) {
                                    isMyRoom = true;
                                    break;
                                }
                            }
                            if (!isMyRoom) {
                                chatRoom = postSnapshot.getValue(ChatRoom.class);
                                chatRooms.add(chatRoom);
                                adapterMain.notifyItemInserted(chatRooms.size()-1);
                            }
                        }
                        if (chatRooms.isEmpty()) {
                            recyclerViewMain.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewMain.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }

                    } else {
                        recyclerViewMain.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mDatabaseReference.child("users").child(mKey).child("myChatRooms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mChatRoomsKeys.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        mChatRoomsKeys.add(postSnapshot.getKey());
                    }
                    mDatabaseReference.child("chatRooms").addListenerForSingleValueEvent(valueEventListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mDatabaseReference.child("chatRooms").addValueEventListener(valueEventListener);

            buttonBottom.setText(getString(R.string.open_chat_search));
            buttonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    directSearchActivity(getArguments());
                }
            });
        }

        return rootView;
    }


    private void directCreateActivity(Bundle bundle) {
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void directSearchActivity(Bundle bundle) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
