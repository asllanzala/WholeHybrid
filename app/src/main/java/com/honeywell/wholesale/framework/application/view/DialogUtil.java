package com.honeywell.wholesale.framework.application.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.honeywell.wholesale.R;

/**
 * Created by e887272 on 9/1/16.
 */

public class DialogUtil {


    public static void showDialog(Context context, String title, String content, String positiveBtnText, String negativeBtnText,
                                  DialogInterface.OnClickListener positiveBtnClickListener, DialogInterface.OnClickListener negativeBtnClickListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setMessage(content); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton(positiveBtnText, positiveBtnClickListener);
        builder.setNegativeButton(negativeBtnText, negativeBtnClickListener);

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


    public static void showDialog(Context context, String title, String content, String positiveBtnText,
                                  DialogInterface.OnClickListener positiveBtnClickListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setMessage(content); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton(positiveBtnText, positiveBtnClickListener);

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


}
