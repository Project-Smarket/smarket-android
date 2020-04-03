package org.techtown.smarket_android.User;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.techtown.smarket_android.R;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class user_register_fragment extends Fragment {
    ViewGroup viewGroup;

    public static user_register_fragment newInstance() {
        return new user_register_fragment();
    }

    EditText register_id;
    EditText register_pw;
    EditText register_name;
    EditText register_nickname;
    Spinner register_phoneNumber_spinner;
    ArrayList<String> phoneNumber_list;
    ArrayAdapter<String> phoneNumberAdapter;
    EditText register_phoneNumber1;
    EditText register_phoneNumber2;

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



        validate_id_btn = viewGroup.findViewById(R.id.validate_id_btn);
        validate_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_id();
            }
        });

        validate_nickname_btn = viewGroup.findViewById(R.id.validate_nick_btn);
        validate_nickname_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_nick();
            }
        });

        phoneNumber_list = new ArrayList<>();
        phoneNumber_list.add("010");
        phoneNumber_list.add("011");
        phoneNumber_list.add("012");
        phoneNumber_list.add("013");
        phoneNumber_list.add("014");
        phoneNumber_list.add("015");

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
        String userID = register_id.getText().toString();

        if (validate_id) {
            return;
        }
        if (userID.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("아이디는 빈 칸 일 수 없습니다.")
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

        user_register_validate_id validateRequest = new user_register_validate_id(userID, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "errorListener", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }

    private void validate_nick() {
        String userID = register_nickname.getText().toString();

        if (validate_nickname) {
            return;
        }
        if (userID.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("닉네임은 빈 칸 일 수 없습니다.")
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

        user_register_validate_id validateRequest = new user_register_validate_id(userID, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "errorListener", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }

    private void register() {
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
                                .setPositiveButton("확인", null)
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
        user_register_request registerRequest = new user_register_request(userID, userPW, userName, userNick, userPhoneNumber, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(registerRequest);
    }

    /*private void register2() {
        String userID = register_id.getText().toString();
        String userPW = register_pw.getText().toString();
        String userName = register_name.getText().toString();
        String userNick = register_nick.getText().toString();
        String userPhoneNumber = register_phoneNumber_spinner.getSelectedItem().toString() + register_phoneNumber1.getText().toString() + register_phoneNumber2.getText().toString();

        String user = userID + "\n" + userPW + "\n" + userName + "\n" + userNick + "\n" + userPhoneNumber;
        Toast.makeText(getContext(), user, Toast.LENGTH_LONG).show();
    }*/

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
