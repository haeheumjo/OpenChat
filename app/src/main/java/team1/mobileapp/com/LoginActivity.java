package team1.mobileapp.com;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    EditText editTextId;
    EditText editTextPw;
    View progressBarLogin;

    Bundle bundle = new Bundle();

    private String name;
    private String studentNumber;
    private int gender;
    private int age;
    private int birthYear;
    private String department;

    private KnuLoginTask KnuLoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextId = findViewById(R.id.editTextId);
        editTextId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    editTextPw.requestFocus();
                    return true;
                }
                return false;
            }
        });
        editTextPw = findViewById(R.id.editTextPw);
        editTextPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        progressBarLogin = findViewById(R.id.progressBarLogin);

        Button buttonVerify = findViewById(R.id.buttonVerify);
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("USER_ACCOUNT", MODE_PRIVATE);
        String mKey = sharedPref.getString("MY_KEY", "");
        if (!mKey.equals("")) {
            AutoLogin(mKey);
        }
    }

    private void attemptLogin() {
        // Reset errors.
        editTextId.setError(null);
        editTextPw.setError(null);

        // Store values at the time of the login attempt.
        String mID = editTextId.getText().toString();
        String mPW = editTextPw.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mID)) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_fail_empty_id), Toast.LENGTH_LONG).show();
            focusView = editTextId;
            cancel = true;
        } else if (TextUtils.isEmpty(mPW)) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_fail_empty_pw), Toast.LENGTH_LONG).show();
            focusView = editTextPw;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            KnuLoginTask = new KnuLoginTask(mID, mPW);
            KnuLoginTask.execute((Void) null);
        }
    }

    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        editTextId.setVisibility(show ? INVISIBLE : VISIBLE);
        editTextId.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                editTextId.setVisibility(show ? INVISIBLE : VISIBLE);
            }
        });

        editTextPw.setVisibility(show ? INVISIBLE : VISIBLE);
        editTextPw.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                editTextPw.setVisibility(show ? INVISIBLE : VISIBLE);
            }
        });

        progressBarLogin.setVisibility(show ? VISIBLE : INVISIBLE);
        progressBarLogin.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBarLogin.setVisibility(show ? VISIBLE : INVISIBLE);
            }
        });
    }

    public class KnuLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mID;
        private final String mPW;

        KnuLoginTask(String mID, String mPW) {
            this.mID = mID;
            this.mPW = mPW;
        }

        @Override
        protected Integer doInBackground(Void... params) {


            try {
                Map<String, String> data = new HashMap<>();
                data.put("menuParam", "901");
                data.put("user.usr_id", mID);
                data.put("user.passwd", mPW);
                data.put("user.user_div", "20");


                Connection.Response response = Jsoup.connect("http://my.knu.kr/stpo/comm/support/loginPortal/login.action")
                        .data(data)
                        .method(Connection.Method.POST)
                        .ignoreHttpErrors(true)
                        .execute();


                Map<String, String> loginCookie = response.cookies();
                System.out.println("cookie : " + loginCookie);

                Document document = Jsoup.connect("http://my.knu.kr/stpo/stpo/stud/infoMngt/basisMngt/list.action")
                        .cookies(loginCookie)
                        .get();


                String documentText = document.text();
                System.out.println(documentText);
                if (documentText.contains("LOGIN ID와 Password는 통합정보시스템과 동일합니다.")) {//로그인 실패 0
                    return 0;
                } else if (documentText.contains("비밀번호는 안전성을 위하여 3개월에 한번씩 변경하여야 합니다.")) {//비밀번호 변경 요구 1
                    return 1;
                } else {//로그인 성공 2
                    Element stu_nbr = document.getElementById("stu_nbr");
                    studentNumber = stu_nbr.attr("value");
                    Element kor_nm = document.getElementById("kor_nm");
                    name = kor_nm.attr("value");
                    Element descern_nbr1 = document.getElementById("descern_nbr1");
                    String social_front = descern_nbr1.attr("value");
                    Element descern_nbr2 = document.getElementById("descern_nbr2");
                    String social_back = descern_nbr2.attr("value");
                    department = document.getElementById("basisMngtTablet").select("td").get(6).text();
                    birthYear = Integer.parseInt(social_front.substring(0, 2));
                    gender = (Character.getNumericValue(social_back.charAt(0))) % 2 == 0 ? 2 : 1;

                    bundle.putString("my_name", name);
                    bundle.putInt("my_gender", gender);
                    bundle.putInt("my_birthYear", birthYear);
                    bundle.putString("my_key", studentNumber);
                    bundle.putString("my_department", department);

                    System.out.println("bundle : " + bundle);

                    return 2;

                }
            } catch (IOException e) {
                Log.e("error", e.toString());
                return 0;//로그인 실패(에러) 0
            }
        }


        @Override
        protected void onPostExecute(final Integer result) {
            if (result == 0) {//로그인 실패
                showProgress(false);
                Toast.makeText(getApplicationContext(), getString(R.string.login_fail), Toast.LENGTH_LONG).show();
                editTextPw.requestFocus();
            } else if (result == 1) {//비밀번호 변경
                showProgress(false);
                Toast.makeText(getApplicationContext(), getString(R.string.login_require_change_pw), Toast.LENGTH_LONG).show();
            } else {//로그인 성공
                mDatabaseReference.child("users").child(studentNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {//신규
                            mDatabaseReference.child("users").child(studentNumber).setValue(new User(name,gender,birthYear,department,studentNumber));
                        }
                        SharedPreferences sharedPref = getSharedPreferences("USER_ACCOUNT", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("MY_KEY", studentNumber);
                        editor.apply();

                        showProgress(false);
                        directMainActivity();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

        @Override
        protected void onCancelled() {
            KnuLoginTask = null;
            showProgress(false);
        }
    }

    private void AutoLogin(final String mKey) {
        showProgress(true);
        mDatabaseReference.child("users").child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    name = dataSnapshot.child("name").getValue().toString();
                    gender = Integer.parseInt(dataSnapshot.child("gender").getValue().toString());
                    birthYear = Integer.parseInt(dataSnapshot.child("birthYear").getValue().toString());
                    department = dataSnapshot.child("department").getValue().toString();

                    bundle.putString("my_name", name);
                    bundle.putInt("my_gender", gender);
                    bundle.putInt("my_birthYear", birthYear);
                    bundle.putString("my_key", mKey);
                    bundle.putString("my_department", department);

                    directMainActivity();
                } else {
                    SharedPreferences sharedPref = getSharedPreferences("USER_ACCOUNT", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.apply();
                    showProgress(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void directSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void directMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
