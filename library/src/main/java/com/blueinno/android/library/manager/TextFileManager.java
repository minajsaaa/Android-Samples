package com.blueinno.android.library.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextFileManager {

    private static final String FILE_NAME = "unist_log";
    private Context mContext;

    //  =========================================================================================

    public TextFileManager(Context context) {
        mContext = context;
    }

    //  =========================================================================================

    public void save(String text) {
        try{
            String path = Environment.getExternalStorageDirectory() + File.separator + "unist";

            File file = new File(path);

            if(!file.exists())
                file.mkdirs();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String time = dateFormat.format(new Date());

            File savefile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "unist" + File.separator + time + ".txt");

            savefile.createNewFile();

            FileOutputStream fos = new FileOutputStream(savefile);
            fos.write(text.getBytes());
            fos.close();

            Toast.makeText(mContext, "Save Success", Toast.LENGTH_SHORT).show();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

}

