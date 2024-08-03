package com.minibox.minideveloper;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.os.*;
import android.content.*;
import android.content.ClipboardManager;
import android.net.*;
import android.widget.*;
import java.io.*;
import android.view.View.*;
import android.view.*;
import android.graphics.*;
import android.provider.*;
import android.widget.SeekBar.*;
import java.text.*;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.DrawLua.Draw1;
import com.minibox.minideveloper.DrawLua.Main;

import es.dmoral.toasty.Toasty;

public class DrawActivity extends BaseActivity {

    //视图与控件
    private TextView txtcopy,txtsend;
    public static TextView txt,txtinit;
    public static LinearLayout txtgo;//生成脚本
    public static ShapeableImageView img;
    private ImageButton exit;//退出
    private SeekBar skb;

    //全局预置
    private int[] colors_251,colors_17,ids_251,ids_17;//颜色列表，id列表
    public static String sall,savepath,datapath;//脚本，拆分脚本，保存路径
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    //操作设定
    private int mx,my,nx,ny,pro;//原尺寸，当前尺寸，进度
    public static int ch,dh,dxh;//颜色，方向，拆分
    public static int ising;//正在生成
    private Bitmap bi,bid;//原图，效果图
    private boolean isshow=false,issenior=false;
    public static int s0123[]=new int[4];//单块，时停

    private int soundid=2,wrongid=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );//设置状态栏字体颜色
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_activity);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //分配视图与控件
        initView();
        ch=0;dh=0;dxh=0;ising=0;//颜色，方向，拆分,正在生成

        //初始化监听
        initListner();
        //初始化数据
        colors_251=Main.rawToIntx(this,R.raw.zsi_rgb,16);
        ids_251=Main.rawToIntx(this,R.raw.zsi_id,10);
        colors_17=Main.rawToIntx(this,R.raw.lt_rgb,16);
        ids_17=Main.rawToIntx(this,R.raw.lt_id,10);

        datapath=getApplicationContext().getExternalFilesDir("").getPath()+"/data";
        //设置监听
        initListner2();
    }
    //初始化视图与控件
    private void initView(){
        txtinit=findViewById(R.id.text_init);//生成状态反馈
        txtgo=findViewById(R.id.go_lua);//生成脚本
        txtcopy=findViewById(R.id.copy);//复制
        txtsend=findViewById(R.id.send);//分享
        txt=findViewById(R.id.condition);//生成状态
        img=findViewById(R.id.draw_a);//图片视图
        skb=findViewById(R.id.seekbar);//拖动条
        exit=findViewById(R.id.draw_exit);//退出

    }
    //初始化监听2
    private void initListner2(){
        txtgo.setOnClickListener(new OnClickListener(){//生成
            @Override
            public void onClick(View p1){
                if(check(DrawActivity.this)!=0) return;
                if(bid==null||ising==1){
                    Toasty.error(DrawActivity.this,"您还未选择图片！！",Toasty.LENGTH_LONG).show(); return;}
                if(dh==0&&ny>256){m("竖直方向高度不得超过256，请调整尺寸");return;}
                ising=1;
                if(issenior){
                    for(int i=0;i<4;i++){
                        String s="";
                        if(s==null||s.length()<1) s0123[i]=2;
                        else{
                            if(s.length()>5) s=s.substring(0,5);
                            s0123[i]=Integer.parseInt(s);
                            if(s0123[i]>512) s0123[i]=512;
                            else if(s0123[i]<2) s0123[i]=2;
                        }

                    }
                }else{
                    if(dh==0) s0123=new int[]{40,20,20,20};
                    else s0123=new int[]{40,40,20,20};
                }
                savepath=getApplicationContext().getExternalFilesDir("").getPath()+"/luaPixel"+".txt";

                txtinit.setText("正在生成…");
                int[] ix=new int[nx*ny];
                bid.getPixels(ix,0,nx,0,0,nx,ny);
                new File(datapath).delete();
                Draw1.get(DrawActivity.this,ix,ch==0?colors_251:colors_17,ch==0?ids_251:ids_17,nx,ny,dh,dxh);
            }
        });
        txtcopy.setOnClickListener(new OnClickListener(){//复制
            @Override
            public void onClick(View p1){
                if (sall == null||sall.length()<1||ising == 1){
                    Toasty.error(DrawActivity.this,"您未生成脚本！！",Toasty.LENGTH_LONG).show();
                    return;
                }//是否生成已脚本
                ClipboardManager cm = (ClipboardManager)DrawActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(sall);
                m("已复制");
            }
        });
        //发送
        txtsend.setOnClickListener(p1 -> {
            if(ising==1||savepath==null||savepath.length()<1||!new File(savepath).exists()||new File(savepath).isDirectory()){
                Toasty.error(DrawActivity.this,"您未生成脚本！！",Toasty.LENGTH_LONG).show();return;
            }
            try{
                //避免安卓10以上分享失败，file:// 协议不再支持共享，需要用content:// 协议
                Uri uri = FileProvider.getUriForFile(DrawActivity.this, DrawActivity.this.getApplicationContext().getPackageName() +".fileProvider", new File(savepath));
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                shareIntent.setDataAndType(uri,"*/*");
                startActivity(Intent.createChooser(shareIntent, "分享到："));
            }catch(Exception e){
                Toasty.normal(DrawActivity.this,e+"",Toasty.LENGTH_SHORT).show();
            }
        });}

    //初始化监听
    private void initListner(){

        exit.setOnClickListener(new OnClickListener() {//退出应用
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img.setOnClickListener(new OnClickListener(){//选择图片
            @Override
            public void onClick(View p1){
                if(check(DrawActivity.this)!=0) return;
                if(ising==1){}
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,0);
            }
        });
        skb.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){//调整尺寸
            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3){
                if(bi==null||ising==1) return;
                nx=(int)(mx*p2*1.0/100);
                ny=(int)(my*p2*1.0/100);
                if(nx<1) nx=1;
                if(ny<1) ny=1;
                bid=Bitmap.createScaledBitmap(bi,nx,ny,true);
                img.setImageBitmap(bid);
                t();
                if (ny <= 256){
                    txt.setText("当前尺寸,宽度:"+nx+" x "+"高度:"+ny+"\n"+"可以生成脚本了");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar p1){}
            @Override
            public void onStopTrackingTouch(SeekBar p1){}
        });
    }
    //尺寸与提示
    private void t(){ txt.setText("当前尺寸,宽度:"+nx+" x "+"高度:"+ny); }
    public static void m(String s){txt.setText(s);}
    //返回选择图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            try{
                Uri uri=data.getData();
                if(uri==null||uri.toString().length()<1) return;
                bi=MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if(bi==null) return;
                mx=bi.getWidth();
                my=bi.getHeight();
                if(mx>=my&&mx>512){
                    my=(int)(my*512.0/mx);
                    mx=512;
                    bi=Bitmap.createScaledBitmap(bi,mx,my,true);
                }else if(my>=mx&&my>512){
                    mx=(int)(mx*512.0/my);
                    my=512;
                    bi=Bitmap.createScaledBitmap(bi,mx,my,true);
                }
                nx=mx;ny=my;
                bid=Bitmap.createScaledBitmap(bi,nx,ny,true);
                img.setImageBitmap(bid);
                skb.setProgress(100);
                pro=100;
                txt.setText("调整尺寸，点击生成");
                if (my>256){
                    txt.setText("调整尺寸，点击生成\n您现在图片高度大于256，请调整");
                }
            }
            catch (IOException e){m(e.toString());}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isshow){
                isshow=false;
                return true;
            }if(ising==1) return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private int check(Context c)
    {
        return 0;
    }
}

