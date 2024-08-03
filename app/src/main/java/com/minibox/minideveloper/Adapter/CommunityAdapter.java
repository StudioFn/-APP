package com.minibox.minideveloper.Adapter;

import static android.content.Context.MODE_PRIVATE;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.minibox.minideveloper.Activity.RoutineActivity;
import com.minibox.minideveloper.View.GridLayoutManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Activity.MdArticleDetails;
import com.minibox.minideveloper.Entity.CommentEntity;
import com.minibox.minideveloper.LoginActivity;
import com.minibox.minideveloper.MyDetailsPage;
import com.minibox.minideveloper.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.minibox.minideveloper.BaseClass.Api_Config;

public class CommunityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<CommentEntity> data;

    public void setData(List<CommentEntity> data) {
        this.data = data;
    }

    public CommunityAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        CommentEntity commentEntity = data.get(position);
        return commentEntity.getPostNewOld();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.community_recycler_item, parent, false));
        } else {
            return new ViewHolderTow(LayoutInflater.from(mContext).inflate(R.layout.community_item_second, parent, false));
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holders, int position) {
        int types = getItemViewType(position);
        //技术贴布局
        if (types == 0) {
            mViewHolder hd = (mViewHolder) holders;
            CommentEntity commentEntity = data.get(position);
            String Ncontent = commentEntity.getPostContent();
            String mContent = Ncontent.replaceAll("</?[^>]+>", "");//去除html标签
            String mNtent = mContent.replaceAll("\\n", "");//去除换行符
            String mMarkdown = mNtent.replaceAll("#|\\*\\*|`|_|\\[|\\]|\\(|\\)|\\{|\\}|\\#|\\+|-|\\*|\\.|!", "");//去除Markdown语法符号
            String url = commentEntity.getPostUserImg();
            DecimalFormat c = new DecimalFormat("0.#");//保留p位小数
            float Com_count = Integer.parseInt(commentEntity.getPostSubscribe());//评论数
            float fie_count = Integer.parseInt(commentEntity.getPostLook());//阅读数
            float lik_count = Integer.parseInt(commentEntity.getPostLike());//点赞数

            String comment = "";
            if (Com_count >= 9999) {
                comment = c.format(Com_count / 1000) + "w";
            } else {
                comment = String.valueOf(Integer.valueOf((int) Com_count));
            }
            //【阅读】数据处理
            String fire;
            if (fie_count >= 9999) {
                fire = c.format(fie_count / 1000) + "w";
            } else {
                fire = String.valueOf(Integer.valueOf((int) fie_count));
            }
            //【点赞】数据处理
            final String[] like = {""};
            if (lik_count >= 9999) {
                like[0] = c.format(lik_count / 1000) + "w";
            } else {
                like[0] = String.valueOf(Integer.valueOf((int) lik_count));
            }

            //【时间】通过时间戳计算距离发帖天数
            hd.publish_time.setText(getTimeAgo(commentEntity.getPostTime()));

            Glide.with(mContext).load(url).into(hd.shapeableImageView);//加载头像
            hd.author_name.setText(commentEntity.getPostUserName());//获得作者昵称
            hd.title.setText(commentEntity.getPostTitle());//获得文章标题
            hd.content.setText(mMarkdown);//显示处理后的文章内容
            hd.comment.setText(comment);//获得评论数
            hd.fire.setText(fire);//获得阅读数
            hd.type.setText("#" + commentEntity.getPostType());//获取帖子类型
            hd.like.setText(like[0]);//获得点赞数

            /***************************注册点击事件********************************/
            hd.itemClick.setOnClickListener(v -> {
                String id = commentEntity.getId();
                String img = commentEntity.getPostUserImg();
                String name = commentEntity.getPostUserName();
                String UID = commentEntity.getPostUserId();
                String title = commentEntity.getPostTitle();
                String time = commentEntity.getPostTime();
                String content = commentEntity.getPostContent();

                Intent i = new Intent(mContext, MdArticleDetails.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("uid", UID);
                bundle.putString("img", img);
                bundle.putString("name", name);
                bundle.putString("title", title);
                bundle.putString("time", time);
                bundle.putString("content", content);
                bundle.putString("transitionName", "heard_share");
                i.putExtras(bundle);
                mContext.startActivity(i);


            });
            /***************************判断是否有图片********************************/
            Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
            Matcher matcher = pattern.matcher(Ncontent);
            if (matcher.find()) {
                String getStr = matcher.group(1);
                hd.imageView.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(getStr).into(hd.imageView);
                Log.e("检查图片链接", "onBindViewHolder: " + getStr);
            } else {
                hd.imageView.setVisibility(View.GONE);
            }

            if (commentEntity.getLike()) {
                hd.img_like.setBackgroundResource(R.drawable.ic_like_red);
            } else {
                hd.img_like.setBackgroundResource(R.drawable.black_like);
            }
            final AtomicLong[] lastTime = {new AtomicLong()};
            int INTERVAL_TIME = 1500;
            final int[] countLike = {Integer.parseInt(commentEntity.getPostLike())};
            /**************************点赞********************************/
            hd.img_like.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                //获取用户ID
                SharedPreferences sh = mContext.getSharedPreferences("QQ_User", MODE_PRIVATE);
                String userId = sh.getString("id", "");
                String articleId = commentEntity.getId();

                if (userId.length() > 0) {//判断是否已经登录
                    if (currentTime - lastTime[0].get() > INTERVAL_TIME) {//防止频繁点击

                        if (commentEntity.getLike()) {
                            hd.img_like.setBackgroundResource(R.drawable.black_like);
                            if (countLike[0] > 0) {
                                hd.like.setText(--countLike[0] + "");
                            }

                            /*改变点赞数，删除点赞表的对应数据*/
                            String data = "UPDATE `dev_posts` SET `post_like` = `post_like`-1 WHERE `dev_posts`.`ID` =" + commentEntity.getId()
                                    + ";DELETE FROM `dev_liked` WHERE `userId`=" + userId + " AND `articleId`=" + articleId;
                            Api_Config.HTTP_AES_API(data, API_URL + "dev_app/dev_more_sql/");
                        } else {
                            hd.img_like.setBackgroundResource(R.drawable.ic_like_red);
                            hd.like.setText(++countLike[0] + "");

                            /*改变点赞数，在点赞表插入数据*/
                            String data = "UPDATE `dev_posts` SET `post_like` = `post_like`+1 WHERE `dev_posts`.`ID` =" + commentEntity.getId()
                                    + ";\nINSERT INTO `dev_liked` (`userId`, `articleId`) VALUES (" + userId + "," + articleId + ")";
                            Api_Config.HTTP_AES_API(data, API_URL + "dev_app/dev_more_sql/");

                        }
                        commentEntity.setLike(!commentEntity.getLike());
                        lastTime[0].set(currentTime);
                    } else {
                        Toast.makeText(mContext, "点击太频繁啦", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "请登录", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                }


            });
            /***************************跳转到作者详情页********************************/
            hd.shapeableImageView.setOnClickListener(v -> {
                String uid = commentEntity.getPostUserId();
                Intent i = new Intent(mContext, MyDetailsPage.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                i.putExtras(bundle);
                mContext.startActivity(i);
            });

        } else if (types == 1) {
            //日常贴布局
            ViewHolderTow hd = (ViewHolderTow) holders;
            CommentEntity commentEntity = data.get(position);
            String Ncontent = commentEntity.getPostContent();
            String mContent = Ncontent.replaceAll("</?[^>]+>", "");//去除html标签
            String mNtent = mContent.replaceAll("\\n", "");//去除换行符
            String mMarkdown = mNtent.replaceAll("#|\\*\\*|`|_|\\[|\\]|\\(|\\)|\\{|\\}|\\#|\\+|-|\\*|\\.|!", "");//去除Markdown语法符号
            String url = commentEntity.getPostUserImg();
            DecimalFormat c = new DecimalFormat("0.#");//保留p位小数
            float Com_count = Integer.parseInt(commentEntity.getPostSubscribe());//评论数
            float fie_count = Integer.parseInt(commentEntity.getPostLook());//阅读数
            float lik_count = Integer.parseInt(commentEntity.getPostLike());//点赞数

            String comment = "";
            if (Com_count >= 9999) {
                comment = c.format(Com_count / 1000) + "w";
            } else {
                comment = String.valueOf(Integer.valueOf((int) Com_count));
            }
            //【阅读】数据处理
            String fire;
            if (fie_count >= 9999) {
                fire = c.format(fie_count / 1000) + "w";
            } else {
                fire = String.valueOf(Integer.valueOf((int) fie_count));
            }
            //【点赞】数据处理
            final String[] like = {""};
            if (lik_count >= 9999) {
                like[0] = c.format(lik_count / 1000) + "w";
            } else {
                like[0] = String.valueOf(Integer.valueOf((int) lik_count));
            }

            //【时间】通过时间戳计算距离发帖天数
            hd.publish_time.setText(getTimeAgo(commentEntity.getPostTime()));

            Glide.with(mContext).load(url).into(hd.shapeableImageView);//加载头像
            hd.author_name.setText(commentEntity.getPostUserName());//获得作者昵称
            hd.title.setText(commentEntity.getPostTitle());//获得文章标题
            hd.content.setText(mMarkdown);//显示处理后的文章内容
            hd.comment.setText(comment);//获得评论数
            hd.fire.setText(fire);//获得阅读数
            hd.type.setText("#" + commentEntity.getPostType());//获取帖子类型
            hd.like.setText(like[0]);//获得点赞数

            //判断用户是否上传有图片
            if (commentEntity.getPostImages().length() > 0) {
                //配置RecycleView的布局
                GridLayoutManager manager = new GridLayoutManager(mContext, 3);
                hd.recyclerView.setLayoutManager(manager);
                String[] images = commentEntity.getPostImages().split(",");
                CommunityItem communityItemAdapter = new CommunityItem(mContext, images);
                hd.recyclerView.setAdapter(communityItemAdapter);
            }


            /***************************注册点击事件********************************/
            hd.itemClick.setOnClickListener(v -> {
                String id = commentEntity.getId();
                String img = commentEntity.getPostUserImg();
                String name = commentEntity.getPostUserName();
                String UID = commentEntity.getPostUserId();
                String title = commentEntity.getPostTitle();
                String time = commentEntity.getPostTime();
                String content = commentEntity.getPostContent();
                String images = commentEntity.getPostImages();

                Intent i = new Intent(mContext, RoutineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("uid", UID);
                bundle.putString("img", img);
                bundle.putString("name", name);
                bundle.putString("title", title);
                bundle.putString("time", time);
                bundle.putString("content", content);
                bundle.putString("images", images);
                bundle.putString("transitionName", "heard_share");
                i.putExtras(bundle);
                mContext.startActivity(i);


            });

            if (commentEntity.getLike()) {
                hd.img_like.setBackgroundResource(R.drawable.ic_like_red);
            } else {
                hd.img_like.setBackgroundResource(R.drawable.black_like);
            }
            final AtomicLong[] lastTime = {new AtomicLong()};
            int INTERVAL_TIME = 1500;
            final int[] countLike = {Integer.parseInt(commentEntity.getPostLike())};
            /**************************点赞********************************/
            hd.img_like.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                //获取用户ID
                SharedPreferences sh = mContext.getSharedPreferences("QQ_User", MODE_PRIVATE);
                String userId = sh.getString("id", "");
                String articleId = commentEntity.getId();

                if (userId.length() > 0) {//判断是否已经登录
                    if (currentTime - lastTime[0].get() > INTERVAL_TIME) {//防止频繁点击

                        if (commentEntity.getLike()) {
                            hd.img_like.setBackgroundResource(R.drawable.black_like);
                            if (countLike[0] > 0) {
                                hd.like.setText(--countLike[0] + "");
                            }

                            /*改变点赞数，删除点赞表的对应数据*/
                            String data = "UPDATE `dev_posts` SET `post_like` = `post_like`-1 WHERE `dev_posts`.`ID` =" + commentEntity.getId()
                                    + ";DELETE FROM `dev_liked` WHERE `userId`=" + userId + " AND `articleId`=" + articleId;
                            Api_Config.HTTP_AES_API(data, API_URL + "dev_app/dev_more_sql/");
                        } else {
                            hd.img_like.setBackgroundResource(R.drawable.ic_like_red);
                            hd.like.setText(++countLike[0] + "");

                            /*改变点赞数，在点赞表插入数据*/
                            String data = "UPDATE `dev_posts` SET `post_like` = `post_like`+1 WHERE `dev_posts`.`ID` =" + commentEntity.getId()
                                    + ";\nINSERT INTO `dev_liked` (`userId`, `articleId`) VALUES (" + userId + "," + articleId + ")";
                            Api_Config.HTTP_AES_API(data, API_URL + "dev_app/dev_more_sql/");

                        }
                        commentEntity.setLike(!commentEntity.getLike());
                        lastTime[0].set(currentTime);
                    } else {
                        Toast.makeText(mContext, "点击太频繁啦", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "请登录", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                }


            });
            /***************************跳转到作者详情页********************************/
            hd.shapeableImageView.setOnClickListener(v -> {
                String uid = commentEntity.getPostUserId();
                Intent i = new Intent(mContext, MyDetailsPage.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                i.putExtras(bundle);
                mContext.startActivity(i);
            });
        }

    }

    public static String getTimeAgo(String postTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(postTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            long timeInMillis = calendar.getTimeInMillis();
            long currentTimeTimeInMillis = System.currentTimeMillis();
            long diff = currentTimeTimeInMillis - timeInMillis;

            long hours = diff / (60 * 60 * 1000);
            long days = hours / 24;
            long week = days / 7;
            long month = days / 30;
            if (hours == 0) {
                return "刚刚";
            } else if (hours < 24) {
                return hours + "小时前";
            } else if (hours >= 24 && hours < 48) {
                return "昨天";
            } else if (hours > 47 && hours <= 174) {
                return days + "天前";
            } else if (days > 7 && week != 0 && week < 5) {
                return week + "周前";
            } else if (week > 5 && month != 0 && month <= 11) {
                return month + "月前";
            } else {
                return postTime;
            }
        } catch (ParseException ignored) {
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    //获取第二个布局属性
    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView shapeableImageView;
        private final TextView author_name;
        private final TextView content;
        private final TextView publish_time;
        private final TextView comment;
        private final TextView fire;
        private final TextView like;
        private final TextView title;
        private final TextView type;
        private final LinearLayout itemClick;
        private final ImageButton img_like;
        private final ShapeableImageView imageView;

        public mViewHolder(View view) {
            super(view);
            shapeableImageView = view.findViewById(R.id.head_portrait);//头像
            author_name = view.findViewById(R.id.author_name);//作者昵称
            title = view.findViewById(R.id.article_title_recycler);//标题
            content = view.findViewById(R.id.article_content_item);//文章内容
            publish_time = view.findViewById(R.id.date_time);//发布时间
            comment = view.findViewById(R.id.comment_count);//评论数
            itemClick = view.findViewById(R.id.list_item_community);//Item全局
            img_like = view.findViewById(R.id.img_like);//点赞按钮
            type = view.findViewById(R.id.article_type);//帖子类型
            fire = view.findViewById(R.id.look_count);//阅读数
            like = view.findViewById(R.id.like_count);//点赞数
            imageView = view.findViewById(R.id.community_item_image);//图片

        }
    }

    //获取第二个布局属性
    public static class ViewHolderTow extends RecyclerView.ViewHolder {
        private final ShapeableImageView shapeableImageView;
        private final TextView author_name;
        private final TextView content;
        private final TextView publish_time;
        private final TextView comment;
        private final TextView fire;
        private final TextView like;
        private final TextView title;
        private final TextView type;
        private final LinearLayout itemClick;
        private final ImageButton img_like;
        private final RecyclerView recyclerView;

        public ViewHolderTow(View view) {
            super(view);
            shapeableImageView = view.findViewById(R.id.head_portrait_second);//头像
            author_name = view.findViewById(R.id.author_name_second);//作者昵称
            title = view.findViewById(R.id.article_title_recycler_second);//标题
            content = view.findViewById(R.id.article_content_item_second);//文章内容
            publish_time = view.findViewById(R.id.date_time_second);//发布时间
            comment = view.findViewById(R.id.comment_count_second);//评论数
            itemClick = view.findViewById(R.id.list_item_community_second);//Item全局
            img_like = view.findViewById(R.id.img_like_second);//点赞按钮
            type = view.findViewById(R.id.article_type_second);//帖子类型
            fire = view.findViewById(R.id.look_count_second);//阅读数
            like = view.findViewById(R.id.like_count_second);//点赞数
            recyclerView = view.findViewById(R.id.community_recycler_second);//列表

        }
    }

}
