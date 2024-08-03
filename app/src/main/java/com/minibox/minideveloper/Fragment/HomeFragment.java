package com.minibox.minideveloper.Fragment;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.minibox.minideveloper.Activity.DevActivity;
import com.minibox.minideveloper.Activity.MusicActivity;
import com.minibox.minideveloper.Activity.WxSetup;
import com.minibox.minideveloper.Adapter.HomeNewsAdapter;
import com.minibox.minideveloper.BaseClass.BaseFragment;
import com.minibox.minideveloper.BaseClass.ScrollLinearLayoutManager;
import com.minibox.minideveloper.Entity.DayArticle;
import com.minibox.minideveloper.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends BaseFragment {
    private RelativeLayout mRe;
    private RelativeLayout music;
    private RelativeLayout wx_sport;
    private RelativeLayout develop;
    private TextView mas;
    private LinearLayout linearLayout;
    private ShapeableImageView Home_Hot_Banner;
    private ViewPager v_banner;
    private RecyclerView recyclerView;
    private HomeNewsAdapter homeNewsAdapter;
    private final List<String> IMGUR = new ArrayList<>();
    private List<DayArticle.DataDTO.TopStoriesDTO> Data = new ArrayList<>();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private final Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {

        }

        @Override
        public void flush() {

        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void close() throws SecurityException {
            homeNewsAdapter.setData(Data);
            homeNewsAdapter.notifyDataSetChanged();
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mRe = v.findViewById(R.id.deltaRelative);
        music = v.findViewById(R.id.music_id);
        mas = v.findViewById(R.id.bing_content);
        linearLayout = v.findViewById(R.id.animateToStart);
        Home_Hot_Banner = v.findViewById(R.id.Home_Banner);
        wx_sport = v.findViewById(R.id.wx_sports);
        develop = v.findViewById(R.id.blog);
        v_banner = v.findViewById(R.id.fragment_viewpager);
        recyclerView = v.findViewById(R.id.fragment_recyclerview);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRe.setPadding(0, getStatusBarHeight(), 0, 0);
        setListener();

        IMGUR.add("https://tse4-mm.cn.bing.net/th/id/OIP-C.rPWTtEif0_eNiPZXRRc8agHaEK?pid=ImgDet&rs=1");
        IMGUR.add("https://pic3.zhimg.com/v2-6cd53a13ce6ac9c1de3110fd3a1a23ca_r.jpg");
        mPagerAdapter.notifyDataSetChanged();
        v_banner.setOffscreenPageLimit(2);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "token=HRkpVppZHRHR9X90");
        Request request = new Request.Builder()
                .url("https://v2.alapi.cn/api/zhihu")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    DayArticle.DataDTO.TopStoriesDTO topStoriesDTO = new DayArticle.DataDTO.TopStoriesDTO();
                    topStoriesDTO.setTitle("哎呀，出现了点错误");
                    topStoriesDTO.setUrl("https://yc-hequan.com/blogsphere");
                    topStoriesDTO.setImage("https://pic3.zhimg.com/v2-c6ae9c3aff36b9b221258f6a90577902_r.jpg");
                    topStoriesDTO.setHint("远赤");
                    Data.add(topStoriesDTO);
                    handler.close();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String data = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    try {
                        DayArticle data_str = new Gson().fromJson(data, DayArticle.class);
                        DayArticle.DataDTO dats = data_str.getData();
                        List<DayArticle.DataDTO.TopStoriesDTO> TopNews = dats.getTopStories();
                        if (TopNews != null) {
                            Data = TopNews;
                            handler.close();
                        } else {
                            DayArticle.DataDTO.TopStoriesDTO topStoriesDTO = new DayArticle.DataDTO.TopStoriesDTO();
                            topStoriesDTO.setTitle("哎呀，出现了点错误");
                            topStoriesDTO.setUrl("https://yc-hequan.com/blogsphere");
                            topStoriesDTO.setImage("https://pic3.zhimg.com/v2-c6ae9c3aff36b9b221258f6a90577902_r.jpg");
                            topStoriesDTO.setHint("远赤");
                            Data.add(topStoriesDTO);
                            handler.close();
                        }
                    } catch (Exception ignored) {
                    }

                });
                Log.i("知乎日报", data);

            }
        });

    }


    //事件操作
    public void setListener() {
        //跳转到网易云音乐搜索
        music.setOnClickListener(v -> toActivity(MusicActivity.class));
        //跳转到步数修改
        wx_sport.setOnClickListener(v -> toActivity(WxSetup.class));
        //跳转到官网
        develop.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("url", "http://yc-hequan.com");
            toActivityWithBundle(DevActivity.class, bundle);
        });
        //渐显动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(700);
        alphaAnimation.setFillAfter(true);
        linearLayout.startAnimation(alphaAnimation);
        String url = getSharedUser("Qimg");//QQ头像链接
        Glide.with(getContext()).load(url).into(Home_Hot_Banner);//加载头像
        //轮播图
        v_banner.setAdapter(mPagerAdapter);
        v_banner.setPageMargin(50);
        v_banner.setCurrentItem(Integer.MAX_VALUE / 2);
        //每日一言
        String content = getShared("content");
        mas.setText(content);
        //RecyclerView设置布局管理器
        ScrollLinearLayoutManager Manager = new ScrollLinearLayoutManager(getActivity());
        Manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(Manager);
        homeNewsAdapter = new HomeNewsAdapter(getContext());/*获取上下文*/
        recyclerView.setAdapter(homeNewsAdapter);/*设置适配器*/

    }

    private final PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.viewpager_item, container, false);
            ImageView img = view.findViewById(R.id.cover);

            //加载数据
            int v_position = position % IMGUR.size();//取余，实现伪循环
            Glide.with(getContext()).load(IMGUR.get(v_position)).into(img);//加载图片

            if (img.getParent() instanceof ViewGroup) {
                ((ViewGroup) img.getParent()).removeView(img);
            }
            container.addView(img);

            return img;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        String url = getSharedUser("Qimg");//QQ头像链接
        try {
            Glide.with(getContext()).load(url).into(Home_Hot_Banner);//加载头像
        } catch (Exception ignored) {
            Glide.with(getContext())
                    .load(API_URL + "imgApi/public/uploads/20230528/1685252374.jpg")
                    .into(Home_Hot_Banner);
        }//加载头像
    }

    private int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        @SuppressLint("InternalInsetResource") int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
