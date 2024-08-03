package com.minibox.minideveloper.DrawLua;
import android.content.*;
import android.net.*;
import java.io.*;
import android.app.*;
import android.view.*;
import android.view.inputmethod.*;
import android.os.*;
import android.graphics.*;

public class Main
{
    //SD卡路径
    final public static String SDpath=Environment.getExternalStorageDirectory().getPath();
    //加群
    public static boolean joinQ(Context c) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D14HJWf94xblnhR0DlkjXe33MXCTgytkQ"));
        try {
            c.startActivity(intent);
            return true;
        } catch (Exception e) {return false;}
    }//raw文件转String
    public static String rawToString(Context c,int id){
        try{
            InputStream fips =c.getResources().openRawResource(id);
            byte [] buffer = new byte[fips.available()];
            fips.read(buffer);
            fips.close();
            return new String(buffer,"UTF-8");}
        catch(Exception e){return e.toString();}
    }//隐藏输入法
    public static void hideInputMethod(Activity activity){
        View a = activity.getCurrentFocus();
        if(a != null){
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//raw转intx
    public static int[] rawToIntx(Context c,int id,int base){
        String s=rawToString(c,id);
        int a=s.indexOf("─");
        int b=s.lastIndexOf("─");
        return markStringToIntx(s.substring(a+1,b),"\n",base);
    }//获取标记String中int数组
    public static int[] markStringToIntx(String markString,String mark,int base){
        int count=indexOfCount(markString,mark)-1;
        if(count<1){return null;}
        else{
            int[] intx=new int[count];
            int start=0,end=0;
            for(int i=0;i<count;i++){
                start=markString.indexOf(mark,end);
                end=markString.indexOf(mark,start+mark.length());
                intx[i]=Integer.parseInt(markString.substring(start+mark.length(),end),base);
            }
            return intx;}
    }//获取标记字符串中字符串数组
    public static String[] getmarkStringx(String markString,String mark){
        int count=indexOfCount(markString,mark)-1;
        if(count<1){return null;}
        else{
            String[] stringx=new String[count];
            int start=0,end=0;
            for(int i=0;i<count;i++)
            {
                start=markString.indexOf(mark,end);
                end=markString.indexOf(mark,start+mark.length());
                stringx[i]=markString.substring(start+mark.length(),end);
            }
            return stringx;}
    }//根据字符串数组生成标记字符串
    public static String getmarkString(String[] stringx,String mark){
        String markString="";
        for(int i=0;i<stringx.length;i++){
            markString=markString+stringx[i]+mark;
        }
        return mark+markString;
    }//String在String中出现次数
    public static int indexOfCount(String all,String in){
        int count=0;
        int loc=0;
        while(all.indexOf(in,loc)!=-1){
            count++;
            loc=all.indexOf(in,loc)+in.length();
        }
        return count;
    }//文件转字符串UTF-8
    public static String FiletoString(File file){
        String string="";
        if(file.exists())
            try{
                FileInputStream fips = new FileInputStream(file);
                int length = fips.available();
                byte [] buffer = new byte[length];
                fips.read(buffer);
                string = new String(buffer, "UTF-8");
                fips.close();}
            catch(Exception e){}
        return string;
    }//向路径文件添加字符串
    public static void addStringToPath(String path,String str){
        try{
            FileWriter fw=new FileWriter(path,true);
            fw.write(str);
            fw.close();
        }catch(Exception e){}
    }//保存字符串到文件
    public static boolean saveStringinFile(String string,String path){
        try{
            ByteArrayInputStream bips = new ByteArrayInputStream(string.getBytes());
            OutputStream ops = new FileOutputStream(new File(path));
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = bips.read(buffer, 0, 8192)) != -1)
            { ops.write(buffer, 0, bytesRead); }
            ops.close();
            bips.close();
            return true;}
        catch(Exception e){return false;}
    }//文件转byte数组
    public static byte[] Filetobytex(File file){
        try{
            FileInputStream fips=new FileInputStream(file);
            byte[] btx=new byte[fips.available()];
            fips.read(btx);
            fips.close();
            return btx;}
        catch(Exception e){return new byte[]{};}
    }//btx是否包含btx
    public static int btxIndexOf(byte[] all,byte[] in,int start,int add){
        boolean isin;
        for(int i=start;i<all.length-in.length+1;i+=add){
            isin=true;
            for(int j=0;j<in.length;j++)
                if(all[i+j]!=in[j]){isin=false;break;}
            if(isin) return i;
        }
        return -1;
    }//裁剪btx
    public static byte[] subbtx(byte[] all,int a,int b){
        byte[] bx=new byte[b-a+1];
        for(int i=0;i<bx.length;i++)
            bx[i]=all[a+i];
        return bx;
    }//根据bitmap保存图片文件
    public static void saveFilebyBitmap(Bitmap bitmap,String path){
        try{
            File file = new File(path);
            if(!file.exists()){file.createNewFile();}
            FileOutputStream fops = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fops);
            fops.flush();
            fops.close();
        }catch(Exception e){}
    }
}
