package com.minibox.minideveloper.DrawLua;

import android.os.*;
import android.graphics.*;
import java.util.*;
import android.content.*;

import com.minibox.minideveloper.DrawActivity;
import com.minibox.minideveloper.R;

import java.io.*;

public class Draw1 {

    private static String zip="A";//A150A0M（150个0M方块）
    private static String nxl="B";//换行
    private static String preasc[]={"","0","1"};//3×88=264（512×512的251色图最多字符数<43万）
    private static String asc="23456789CDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz{|}~[]^_`:;<=>?@ !#$%&()*+,-./";

    private static int bix[],colors[],ids[],w,h,dh,dxh;//像素图数组，使用的颜色列表，对应id列表，宽高
    private static int bicx[],thx[],counts[];//效果图数组，像素图id序号，id使用次数统计
    private static String csx[];//id字符次序
    private static int allc=0;//使用的id数

    private static Context c;

    private static String luaids,luadata;//["A"]=1006,  像素图
    public static void get(Context c,int[] bix,int[] colors,int[] ids,int w,int h,int dh,int dxh){
        Draw1.bix=bix;
        Draw1.colors=colors;
        Draw1.ids=ids;
        Draw1.w=w;
        Draw1.h=h;
        Draw1.bicx=new int[bix.length];
        Draw1.thx=new int[bix.length];
        Draw1.counts=new int[colors.length];
        Draw1.csx=new String[colors.length];
        Draw1.allc=0;
        Draw1.c=c;
        Draw1.dh=dh;
        Draw1.dxh=dxh;
        for(int i=0;i<counts.length;i++)
            counts[i]=i;
        luaids="";
        luadata="";
        count=0;
        hdl.postDelayed(rnb,1);
    }

    private static int count;
    private static Handler hdl=new Handler();
    private static Runnable rnb=new Runnable(){
        @Override
        public void run(){
            if(count<h){
                int j;
                for(int i=0;i<w;i++){
                    j=count*w+i;
                    thx[j]=getNearth(bix[j]);
                    counts[thx[j]]+=1000;
                    if(counts[thx[j]]/1000==1) allc++;
                    if(thx[j]!=0) bicx[j]=0xff000000+colors[thx[j]];
                }
                count++;
                DrawActivity.m("正在获取："+count+"/"+h);
                hdl.postDelayed(this,1);
            }else if(count==h){
                DrawActivity.img.setImageBitmap(Bitmap.createBitmap(bicx,w,h,Bitmap.Config.ARGB_8888));
                count++;
                DrawActivity.m("获取完毕，正在分析…请稍等");
                hdl.postDelayed(this,1);
            }else if(count==h+1){
                Arrays.sort(counts);
                char[] ascx=asc.toCharArray();
                for(int i=0;i<allc;i++){
                    csx[counts[counts.length-1-i]%1000]=preasc[i/ascx.length]+ascx[i%ascx.length];
                    luaids+="[\""+preasc[i/ascx.length]+ascx[i%ascx.length]+"\"]="+ids[counts[counts.length-1-i]%1000]+",";
                }
                count=h*3;
                hdl.postDelayed(this,1);
            }else if(count<h*4){
                int j,k,l;
                k=(count-h*3+1)*w;
                j=(count-h*3)*w;
                while(j<k){
                    l=1;
                    while(j+l<k&&thx[j+l]==thx[j]) l++;
                    if((csx[thx[j]].length()==1&&l>4)||(csx[thx[j]].length()==2&&l>2))
                        //luadata+=zip+l+zip+csx[thx[j]];
                        Main.addStringToPath(DrawActivity.datapath,zip+l+zip+csx[thx[j]]);
                    else for(int i=0;i<l;i++)
                        //luadata+=csx[thx[j]];
                        Main.addStringToPath(DrawActivity.datapath,csx[thx[j]]);
                    j+=l;
                }
                //luadata+=nxl;
                Main.addStringToPath(DrawActivity.datapath,nxl);
                count++;
                DrawActivity.m("正在压缩："+(count-h*3)+"/"+h);
                hdl.postDelayed(this,1);
            }else{
                try{
                    FileInputStream fips =new FileInputStream(DrawActivity.datapath);
                    byte [] buffer = new byte[fips.available()];
                    fips.read(buffer);
                    fips.close();
                    luadata=new String(buffer,"UTF-8");}
                catch(Exception e){}
                getlua(luaids,luadata,dh,dxh);
                DrawActivity.m("生成完毕，点击复制\n(如果复制不完整，请切换讯飞输入法)");
                DrawActivity.ising=0;
                DrawActivity.txtgo.setEnabled(true);
                DrawActivity.txtinit.setText("生成脚本");
                hdl.removeCallbacks(this);
            }
        }
    };

    private static int getNearth(int color){//获取最接近的颜色序号
        if(color>>>24==0) return 0;
        color=color<<8>>>8;
        int more=0x1000000,th=0,m;
        for(int i=1;i<colors.length;i++){
            m=(int)(Math.pow((color<<8>>>24)-(colors[i]<<8>>>24),2)+Math.pow((color<<16>>>24)-(colors[i]<<16>>>24),2)+Math.pow((color<<24>>>24)-(colors[i]<<24>>>24),2));
            if(m<more){
                more=m;
                th=i;
            }
        }
        return th;
    }
    //获取脚本与拆分脚本，设置到界面
    private static void getlua(String ids,String data,int d,int dx){
        String lua="";
        if(d==0){
            switch(dx){
                case 0:lua=Main.rawToString(c,R.raw.lua_y0);break;
                case 1:lua=Main.rawToString(c,R.raw.lua_y1);break;
                case 2:lua=Main.rawToString(c,R.raw.lua_y2);break;
                case 3:lua=Main.rawToString(c,R.raw.lua_y3);break;
                default:break;
            }
        }else{
            switch(dx){
                case 0:lua=Main.rawToString(c, R.raw.lua_xz0);break;
                case 1:lua=Main.rawToString(c,R.raw.lua_xz1);break;
                case 2:lua=Main.rawToString(c,R.raw.lua_xz2);break;
                case 3:lua=Main.rawToString(c,R.raw.lua_xz3);break;
                default:break;
            }
        }
        lua=lua.replace("【0】",""+DrawActivity.s0123[0])
                .replace("【1】",""+DrawActivity.s0123[1])
                .replace("【2】",""+(DrawActivity.s0123[1]+DrawActivity.s0123[2]))
                .replace("【3】",""+(DrawActivity.s0123[1]+DrawActivity.s0123[2]+DrawActivity.s0123[3]));
        DrawActivity.sall="local A=\""+data+"\"\nlocal B={"+ids+"}\nlocal w,h="+w+","+h+"\n"+lua;
        Main.saveStringinFile(DrawActivity.sall,DrawActivity.savepath);
    }

}
