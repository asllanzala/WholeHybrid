
package com.honeywell.wholesale.ui;

import com.honeywell.wholesale.framework.application.WholesaleApplication;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtils {


    private static final long MAX_FILE_SIZE = 50000;

    public static String loadTextResource(Context context, int resource) throws IOException {
        if (context == null) {
            return null;
        }

        InputStream inputStream = context.getResources().openRawResource(resource);
        return loadFile(inputStream);
    }

    public static String loadTextFile(Context context, String filename) throws IOException {
        if (context == null) {
            return null;
        }

        InputStream inputStream = context.openFileInput(filename);
        return loadFile(inputStream);
    }

    /**
     * Read content from the files in the Android asset folder.
     */
    public static String loadAssetFile(Context context, String filename) throws IOException {
        InputStreamReader inputStream = null;
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            inputStream = new InputStreamReader(context.getResources().getAssets().open(filename));
            BufferedReader bufReader = new BufferedReader(inputStream);
            int n;
            while ((n = bufReader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return writer.toString();
    }

    public static void saveTextFile(Context context, String filename, String value)
            throws IOException {
        FileOutputStream outputStream = null;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(value.getBytes());
            outputStream.close();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static byte[] readContentIntoByteArray(File file) {

        FileInputStream fileInputStream;
        byte[] bFile = new byte[(int) file.length()];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bFile;
    }

    private static String loadFile(InputStream inputStream) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return writer.toString();
    }

    /**
     * Get the string data by from Json file.
     *
     * @param jsonFileName json file name
     */
    public static String getStringByAssetFileName(String jsonFileName) throws IOException {
        try {
            return IOUtils.loadAssetFile(WholesaleApplication.getAppContext(), jsonFileName);
        } catch (Exception e) {
            throw new IOException("Demo Request returned null payload: " + e.getMessage());
        }
    }

    public static void writeDataToFile(String outputString, String fileName, String directoryName,
            boolean isAppendToFile) {

        if (isExternalStorageWritable()) {
            File directory = getDocumentsStorageDir(directoryName);
            File logFile = new File(directory, fileName);

            if (logFile.length() > MAX_FILE_SIZE) {
                logFile.delete();
            }

            try {
                Writer writer = new BufferedWriter(new FileWriter(logFile, isAppendToFile));
                writer.write(outputString);
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static File getDocumentsStorageDir(String directoryName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), directoryName);
        if (file.mkdirs()) {
            Log.d(IOUtils.class.getSimpleName(), "Directory created");
        }
        return file;
    }

}
