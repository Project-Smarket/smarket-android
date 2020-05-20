//package org.techtown.smarket_android.searchItemList.Pager.youtube;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.Volley;
//import com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerSupportFragment;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.techtown.smarket_android.R;
//import org.techtown.smarket_android.searchItemList.Request.danawaRequest;
//
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.android.volley.VolleyLog.TAG;
//
//public class youtubeFragment extends Fragment {
//
//    private static final String API_KEY = YouTubeAPI.getApiKey();
//    private List<String> videoId = new ArrayList<>();
//    private String txt;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.search_detail_video_fragment_layout, container, false);
//
//        getBundle();
//
//        try {
//            getJson();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return view;
//    }
//
//    private void getBundle() {
//        if (getArguments() != null) {
//            txt = getArguments().getString("txt");
//        }
//    }
//
//    private void getJson() throws UnsupportedEncodingException {
//        danawaRequest danawaRequest = new danawaRequest(txt, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray data = jsonObject.getJSONArray("data");
//                    int length = data.length();
//
//                    for (int i = 1; i < length; i++) {
//                            String s = data.getJSONObject(i).getJSONObject("id").getString("videoId");
//                            videoId.add(s);
//                    }
//
//                    youtubeMethod();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
//            }
//        });
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        queue.add(danawaRequest);
//    }
//
//    public void youtubeMethod(){
//        YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
//
//        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.youtubeLayout, youTubePlayerSupportFragment).commit();
//        // android 기능이 androidx 로 바뀌면서 생기는 오류, error가 발생하는데 사용하는데 지장은 없다
//        // 관련글 : https://stackoverflow.com/questions/52577000/youtube-player-support-fragment-no-longer-working-on-android-studio-3-2-android
//
//        youTubePlayerSupportFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                if (!b) {
//                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
//                    youTubePlayer.loadVideos(videoId);
//                    youTubePlayer.play();
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                Toast.makeText(getActivity(), youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
//                Log.d(TAG, "onInitializationFailure: " + youTubeInitializationResult.toString());
//            }
//        });
//    }
//}
