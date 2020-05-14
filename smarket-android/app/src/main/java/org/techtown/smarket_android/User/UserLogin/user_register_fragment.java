package org.techtown.smarket_android.User.UserLogin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.android.volley.VolleyLog.TAG;


public class user_register_fragment extends Fragment {
    ViewGroup viewGroup;

    public static user_register_fragment newInstance() {
        return new user_register_fragment();
    }

    private EditText register_id;
    private EditText register_pw;
    private EditText register_name;
    private EditText register_nickname;
    private Spinner register_phoneNumber_spinner;
    private ArrayList<String> phoneNumber_list;
    private ArrayAdapter<String> phoneNumberAdapter;
    private EditText register_phoneNumber1;
    private EditText register_phoneNumber2;

    private AlertDialog dialog;
    private boolean validate_id = false;
    private boolean validate_nickname = false;
    Button validate_id_btn;
    Button validate_nickname_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_register_main, container, false);

        register_id = viewGroup.findViewById(R.id.register_id_et); // 사용자 아이디
        register_id.setFilters(new InputFilter[] {filterEng});

        register_nickname = viewGroup.findViewById(R.id.register_nick_et); // 사용자 닉네임
        register_pw = viewGroup.findViewById(R.id.register_pw_et); // 사용자 비번호

        register_name = viewGroup.findViewById(R.id.register_name_et); // 사용자 이름
        register_name.setFilters(new InputFilter[] {filterKor});

        register_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    validate_id();
                }else{
                    return;
                }
            }
        });

        register_nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    validate_nick();
                else
                    return;
            }
        });
        phoneNumber_list = new ArrayList<>();
        phoneNumber_list.add("010");
        phoneNumber_list.add("011");

        phoneNumberAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, phoneNumber_list);

        register_phoneNumber_spinner = (Spinner) viewGroup.findViewById(R.id.register_phoneNumber_spinner);
        register_phoneNumber_spinner.setAdapter(phoneNumberAdapter);
        register_phoneNumber1 = viewGroup.findViewById(R.id.register_phoneNumber1_et);
        register_phoneNumber2 = viewGroup.findViewById(R.id.register_phoneNumber2_et);

        Button register_btn = viewGroup.findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        return viewGroup;
    }

    private void validate_id() {
        String url = getString(R.string.authEndpoint) + "/checkid";
        String key = "user_id";
        String user_id = register_id.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 있는 아이디 입니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        register_id.setEnabled(false);
                        validate_id = true;
                        register_id.setTextColor(getResources().getColor(R.color.colorgray));

                        return;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 없는 아이디 입니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        };

        user_validate validateRequest = new user_validate(url, key, user_id, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response != null){
                    try{
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(res);
                        JsonObject data = null;
                        String comment = null;

                        // ID는 6자리 이상입니다.
                        if(!element.getAsJsonObject().get("data").isJsonNull()){
                            data = element.getAsJsonObject().get("data").getAsJsonObject();
                            JsonArray errors = data.getAsJsonArray("errors");
                            JsonObject error_object = errors.get(0).getAsJsonObject();
                            String msg = error_object.get("msg").getAsString();
                            Log.d(TAG, "onErrorResponse: " + msg);
                        }// 이미 존재하는 ID 입니다.
                        else {
                            comment = element.getAsJsonObject().get("comment").getAsString();
                            Log.d(TAG, "onErrorResponse: "+ comment);
                        }



                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }

    private void validate_nick() {
        String url = getString(R.string.authEndpoint) + "/checknickname";
        String key = "nickname";
        String user_nickname = register_nickname.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 있는 닉네임 입니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        register_nickname.setEnabled(false);
                        validate_nickname = true;
                        register_nickname.setTextColor(getResources().getColor(R.color.colorgray));

                        return;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 없는 닉네임 입니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        };

        user_validate validateRequest = new user_validate(url, key, user_nickname, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "errorListener", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }

    private void register() {
        Context context = getContext();
        String userID = register_id.getText().toString();
        String userPW = register_pw.getText().toString();
        String userName = register_name.getText().toString();
        String userNick = register_nickname.getText().toString();
        String register_phonNumber1 = register_phoneNumber1.getText().toString();
        String register_phonNumber2 = register_phoneNumber2.getText().toString();
        String userPhoneNumber = register_phoneNumber_spinner.getSelectedItem().toString() + register_phonNumber1 + register_phonNumber2;

        if (!validate_id && !validate_nickname) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
            return;
        }

        if (userID.equals("") || userPW.equals("") || userName.equals("") || userNick.equals("") || register_phonNumber1.equals("") || register_phonNumber2.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("사용자 정보를 입력해주세요.")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원등록에 성공했습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.main_layout, user_login_fragment.newInstance()).commit();
                                    }
                                })
                                .create();
                        dialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원 등록에 실패했습니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ;
                }
            }
        };

        user_register_request registerRequest = new user_register_request(context, userID, userPW, userName, userNick, userPhoneNumber, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(registerRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected InputFilter filterEng = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";

            }
            return null;
        }

    }; // EditText 영문만 허용


    public InputFilter filterKor = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    }; // EditText 영문만 허용
}
