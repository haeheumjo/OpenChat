package team1.mobileapp.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainFragment extends Fragment {
    private static final String ARG_PAGE = "page";

    Bundle bundle = new Bundle();
    
    private int page;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance(int page, Bundle bundle) {
        MainFragment fragment = new MainFragment();
        bundle.putInt(ARG_PAGE, page);
        bundle.putAll(bundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerViewMain = view.findViewById(R.id.recyclerViewMain);
        Button buttonBottom = view.findViewById(R.id.buttonBottom);

        page = getArguments().getInt(ARG_PAGE);
        if(page==1){//내채팅방목록
            buttonBottom.setText("오픈채팅 만들기");
            buttonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    directCreateActivity(bundle);
                }
            });
        }else{//채팅방둘러보기
            buttonBottom.setText("채팅방 검색하기");
            buttonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    directSearchActivity(bundle);
                }
            });
        }
        return view;
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
