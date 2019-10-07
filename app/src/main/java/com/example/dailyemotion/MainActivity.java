package com.example.dailyemotion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button btn_chooseback, btn_share, btn_chooseerweima;
    private LinearLayout Hllayout, Sllayout;
    private ImageView Himg_erweima,  Simg_erweima;
    private static final String TAG = "MainActivity";
    private ImageButton Himg,Simg;
    private AlertDialog dialog;
    private EditText et_shopname, et_address, et_phone, et_mood;
    private TextView Htx_year, Htx_week, Htx_lunar, Htx_shopname, Htx_phone, Htx_address, Htx_mood;
    private TextView Stx_year, Stx_week, Stx_lunar, Stx_shopname, Stx_phone, Stx_address, Stx_mood;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置访问内存权限
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1
        );
        //相机相关权限
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
         new Thread(new Runnable() {
             @Override
             public void run() {
                 OkHttpUtils
                         .get()
                         .url("http://hpuiot.com/dailyemotion/versioncheck.txt")
                         .build()
                         .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "Dailyversion.txt") {
                             @Override
                             public void onError(okhttp3.Call call, Exception e, int id) {
                                 Log.e(TAG, "onError: "+e );
                             }
                             @Override
                             public void onResponse(File response, int id) {
                                 Log.e(TAG, "onResponse: "+response);
                                 String versionCode = null;
                                 try {
                                     //获取软件版本号，对应AndroidManifest.xml下android:versionCode
                                     versionCode = MainActivity.this.getPackageManager().
                                             getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
                                 } catch (PackageManager.NameNotFoundException e) {
                                     e.printStackTrace();
                                 }
                                 float version = 0;
                                 version = Float.parseFloat(versionCode);
                                 Log.e(TAG, "getVersion: " + version);
                                 File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Dailyversion.txt");
                                 FileReader fileReader;
                                 if (!file.exists()) {
                                     return;
                                 } else {
                                     try {
                                         fileReader = new FileReader(file);
                                         char buffer[] = new char[(int) file.length()];
                                         fileReader.read(buffer);
                                         String str = new String(buffer);
                                         fileReader.close();
                                         float code = Float.parseFloat(str);
                                         if (version < code) {
                                             showUpdataDialog(code);
                                         }
                                         Log.e(TAG, "code: " + code);
                                     } catch (FileNotFoundException e) {
                                         e.printStackTrace();
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }
                         });
             }
         }).start();

//控件初始化
        Hllayout = (LinearLayout) findViewById(R.id.Hlayout);
        Sllayout = (LinearLayout) findViewById(R.id.SLayout);
        Simg = (ImageButton) findViewById(R.id.Simg);
        Simg_erweima = (ImageView) findViewById(R.id.Simg_erweima);
        Himg = (ImageButton) findViewById(R.id.Himg);
        Himg_erweima = (ImageView) findViewById(R.id.Himg_erweima);
        btn_chooseback = (Button) findViewById(R.id.btn_chooseback);
        btn_chooseerweima = (Button) findViewById(R.id.btn_erweima);
        btn_share = (Button) findViewById(R.id.btn_share);
        et_mood = (EditText) findViewById(R.id.ed_mood);
        et_phone = (EditText) findViewById(R.id.ed_phone);
        et_address = (EditText) findViewById(R.id.ed_address);
        et_shopname = (EditText) findViewById(R.id.ed_shopname);
        Stx_year = (TextView) findViewById(R.id.Stx_year);
        Stx_week = (TextView) findViewById(R.id.Stx_week);
        Stx_lunar = (TextView) findViewById(R.id.Stx_lunar);
        Stx_shopname = (TextView) findViewById(R.id.Stx_shopname);
        Stx_phone = (TextView) findViewById(R.id.Stx_phone);
        Stx_address = (TextView) findViewById(R.id.Stx_address);
        Stx_mood = (TextView) findViewById(R.id.Stx_mood);
        Htx_year = (TextView) findViewById(R.id.Htx_year);
        Htx_week = (TextView) findViewById(R.id.Htx_week);
        Htx_lunar = (TextView) findViewById(R.id.Htx_lunar);
        Htx_phone = (TextView) findViewById(R.id.Htx_phone);
        Htx_shopname = (TextView) findViewById(R.id.Htx_shopname);
        Htx_address = (TextView) findViewById(R.id.Htx_address);
        Htx_mood=(TextView)findViewById(R.id.Htx_mood);
        //起始隐藏s竖屏
        Sllayout.setVisibility(View.INVISIBLE);
        //显示分享后保存的数据
        SharedPreferences sp = getSharedPreferences("set", MODE_PRIVATE);
        String Shopname = sp.getString("shopname", "店铺名称");
        String Phone = sp.getString("telenumber", "联系方式");
        String Address = sp.getString("address", "店铺地址");
        String Mood = sp.getString("mood", "心情短语");
        Htx_mood.setText(Mood);
        Htx_address.setText(Address);
        Htx_phone.setText(Phone);
        Htx_shopname.setText(Shopname);
        Stx_mood.setText(Mood);
        Stx_address.setText(Address);
        Stx_phone.setText(Phone);
        Stx_shopname.setText(Shopname);
        et_shopname.setText(Shopname);
        et_phone.setText(Phone);
        et_address.setText(Address);
        et_mood.setText(Mood);
        //将保存的二维码进行显示
        File appDir = new File(Environment.getExternalStorageDirectory(), "dailyimage");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "qrcode.jpg";
        File file = new File(appDir, fileName);
        if (file != null && file.exists() && file.isFile()) {
            Bitmap bmp=null;
            Uri imageUri = Uri.fromFile(file);
            try {
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                Himg_erweima.setImageBitmap(bmp);
                Simg_erweima.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //获取并设置当前时间
        Calendar calendar=Calendar.getInstance();
        String year =String.valueOf( calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String day =String.valueOf( calendar.get(Calendar.DAY_OF_MONTH));
        String  mweek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mweek)){
            mweek ="日";
        }else if("2".equals(mweek)){
            mweek ="一";
        }else if("3".equals(mweek)){
            mweek ="二";
        }else if("4".equals(mweek)){
            mweek ="三";
        }else if("5".equals(mweek)){
            mweek ="四";
        }else if("6".equals(mweek)){
            mweek ="五";
        }else if("7".equals(mweek)){
            mweek ="六";
        }
        Lunar lunar=new Lunar(calendar);
        String lunarStr = "";
        lunarStr =lunar.toString();
        Htx_year.setText(year+"年"+month+"月"+day+"日");
        Htx_week.setText("星期"+mweek);
        Htx_lunar.setText(lunarStr);
        Stx_year.setText(year+"年"+month+"月"+day+"日");
        Stx_week.setText("星期"+mweek);
        Stx_lunar.setText(lunarStr);

//设置各输入框实时输入事件
        //输入店铺名称监听
        et_shopname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                Stx_shopname.setText(s);
                Htx_shopname.setText(s);
            }
        });
        //输入联系电话监听
        et_phone.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                Stx_phone.setText(s);
                Htx_phone.setText(s);
            }
        });
        //输入店铺地址监听
        et_address.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Stx_address.setText(s);
                Htx_address.setText(s);
            }
        });
        //输入输入心情监听
        et_mood.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Stx_mood.setText(s);
                Htx_mood.setText(s);
            }
        });
        et_mood.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                }
                return false;
            }
        });
        Himg_erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });
        Simg_erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });
        Himg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        Simg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        //选择背景监听
        btn_chooseback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        //选择二维码按钮监听
        btn_chooseerweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });
        //分享图片按钮监听
        btn_share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
//                deleteFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/dailyimage/picture.jpg");
                save();
            }
        });
        ///软件更新设置

    }

    //软件更新
    protected void showUpdataDialog(Float code) {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("检测到新版本");
        builer.setMessage("V "+code+"\n 每日心情更新");
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        // builer.setPositiveButton("确定", (dialog, which) -> downLoadApk());
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downLoadApk();
            }
        });
        //当点取消按钮时不做任何举动
        //  builer.setNegativeButton("取消", (dialogInterface, i) -> {});
        builer.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = builer.create();
        dialog.show();

    }

    protected void downLoadApk() {
        //进度条
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCancelable(false);
        pd.setProgressNumberFormat("");
        pd.show();

        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer("http://hpuiot.com/dailyemotion/dailyemotion.apk", pd);
                    //安装APK
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Log.e(TAG, "run: " + e);
                }
            }
        }.start();
    }
    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "DailyEmotion.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    protected void installApk(File file) {

        try { //处理安装时，出现解析失败的问题
            String[] command = {"chmod", "777", file.getPath()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(FileProvider.getUriForFile(MainActivity.this, "hpu.com.DailyEmotion", file), "application/vnd.android.package-archive");
        startActivity(intent);

    }
    //相册回调函数
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (data != null) {
                Uri uri = data.getData();
                Bitmap bip = null;
                try {
                    bip = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    int width = bip.getWidth();
                    int height = bip.getHeight();
                    if (width >= height) {
                        Himg.setImageBitmap(bip);
                        Himg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Hllayout.setVisibility(View.VISIBLE);
                        Sllayout.setVisibility(View.INVISIBLE);
                    } else {
                        Simg.setImageBitmap(bip);
                        Simg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Hllayout.setVisibility(View.INVISIBLE);
                        Sllayout.setVisibility(View.VISIBLE);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 1) {
            if (data != null) {
                Uri uri = data.getData();
                Bitmap bip = null;
                try {
                    bip = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    Simg_erweima.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Himg_erweima.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Simg_erweima.setImageBitmap(bip);
                    Himg_erweima.setImageBitmap(bip);
                    File appDir = new File(Environment.getExternalStorageDirectory(), "dailyimage");
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName = "qrcode.jpg";
                    File file = new File(appDir, fileName);
                    FileOutputStream fos = new FileOutputStream(file,false);
                    bip.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public boolean deleteFile(String filePath) {
//        File file = new File(filePath);
//        if (file.isFile() && file.exists()) {
//           return file.delete();
//        }else {
//            return false;
//        }
//    }


    //截图的图片需要先保存再分享
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMddHHmmss");
        Date nowtimemills=new Date(System.currentTimeMillis());
        String now=simpleDateFormat.format(nowtimemills);
        File appDir = new File(Environment.getExternalStorageDirectory(), "dailyimage");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName =now+".jpg";
        File file = new File(appDir, fileName);
        Bitmap bitmap = null;
        View view=null;
        if (Hllayout.getVisibility()==View.VISIBLE ) {
            view=Hllayout;
        }
        else{
            view=Sllayout;
        }
        //截图
        bitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        view.draw(canvas);
        try {
            FileOutputStream fos = new FileOutputStream(file,false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         appDir = new File(Environment.getExternalStorageDirectory(), "dailyimage");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        if (file != null && file.exists() && file.isFile()) {
            Uri imageUri = Uri.fromFile(file);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "picture"));
            buffer();
        } else {
            Toast.makeText(MainActivity.this, "请先选择背景图片", Toast.LENGTH_SHORT).show();
        }
    }

    public void buffer() {
        String shopname;
        String telenumber;
        String address;
        String mood ;
        SharedPreferences sp = getSharedPreferences("set", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
         mood=et_mood.getText().toString();
         telenumber=et_phone.getText().toString();
         shopname=et_shopname.getText().toString();
         address=et_address.getText().toString();
        editor.putString("shopname", shopname);
        editor.putString("telenumber",  telenumber);
        editor.putString("mood", mood);
        editor.putString("address", address);
        editor.commit();
    }

}




