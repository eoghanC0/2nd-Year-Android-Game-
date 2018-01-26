package uk.ac.qub.eeecs.gage.engine.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.SoundPool;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.R;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.gage.engine.audio.Sound;

/**
 * Input support across standard file stores
 *
 * @version 1.0
 */
public class FileIO {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Context for the file IO
     */
    private Context mContext;

    /**
     * Asset manager that will be used
     */
    private AssetManager mAssetManager;

    /**
     * Location of the external storage
     */
    private String mExternalStoragePath;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new File IO service
     *
     * @param context Context to which this File IO will use
     */
    public FileIO(Context context) {
        mContext = context;
        mAssetManager = context.getAssets();
        mExternalStoragePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Asset IO //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Open an input stream to the named asset stored in the APK file.
     *
     * @param assetName Name of the asset to open for reading
     * @return InputStream that can be used to read the asset
     * @throws IOException if the asset cannot be opened
     */
    public String readAsset(String assetName) throws IOException {
        InputStream in = mAssetManager.open(assetName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        in.close();
        return out.toString();
    }

    /**
     * Load the specified bitmap using the specified format from the APK file.
     *
     * @param fileName Name of the bitmap to be loaded
     * @param format   Bitmap format to be used when loading the bitmap
     * @throws IOException if the asset cannot be opened or read.
     */
    public Bitmap loadBitmap(String fileName, Bitmap.Config format)
            throws IOException {

        Options options = new Options();
        options.inPreferredConfig = format;
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = mAssetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null) {
                String message = mContext.getApplicationContext()
                        .getResources().getString(R.string.WARNING_TAG)
                        + "Could not load bitmap [" + fileName + "]";
                throw new IOException(message);
            }
        } catch (IOException e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load bitmap [" + fileName + "]";
            throw new IOException(message);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return bitmap;
    }

    /**
     * Load in the specified music file.
     *
     * @param filename Name of the music asset to be loaded
     * @return Loaded Music instance, or null if the effect could not be loaded
     */
    public Music loadMusic(String filename) throws IOException {
        try {
            AssetFileDescriptor assetDescriptor = mAssetManager.openFd(filename);
            return new Music(assetDescriptor);
        } catch (IOException e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load music [" + filename + "]";
            throw new IOException(message);
        }
    }

    /**
     * Load in the specified sound effect file.
     *
     * @param filename Name of the sound asset to be loaded
     * @return Loaded Sound instance, or null if clip could not be loaded
     */
    public Sound loadSound(String filename, SoundPool soundPool)
            throws IOException {
        try {
            AssetFileDescriptor assetDescriptor = mAssetManager.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 0);
            return new Sound(soundPool, soundId);
        } catch (IOException e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load sound [" + filename + "]";
            throw new IOException(message);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Device Storage IO //
    // /////////////////////////////////////////////////////////////////////////
    /**
     * @return a string (i.e. the data stored in the file)
     */
    public String readFile(String fileName) throws IOException {
        File file = new File(mContext.getFilesDir().getPath() + File.separator + fileName);
        if (file != null) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }
            bufferedReader.close();
            fileReader.close();
            Log.i("READ:", "Read Successful");
            return stringBuilder.toString();
        }
        Log.e("Error", "File not found");
        return "";
    }

    /**
     * @param fileName filepath given to the file to be written
     * @param data the data to be written to the file
     */
    public void writeFile(String fileName, String data) throws IOException {
        File file = new File(mContext.getFilesDir().getPath() + File.separator + fileName);
        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter outWriter = new OutputStreamWriter(out);
        outWriter.append(data);
        outWriter.close();
        out.close();
        Log.i("SAVE:", "Save Successful");
    }

    /*
    Get all of the files stored in external storage
     */
    public ArrayList<File> getFiles() {
        ArrayList<File> files = new ArrayList<>();
        File dir = new File(mContext.getFilesDir().getPath() + File.separator);
        File[] items = dir.listFiles();
        if (items != null) {
            for (File f : items) {
                if (f.isFile()) files.add(f);
            }
        }
        return files;
    }

    /*
    Get the names of all files stored in external storage
     */
    public ArrayList<String> getFileList() {
        ArrayList<String> files = new ArrayList<>();
        for (File f : getFiles()) {
            files.add(f.getName());
        }
        return files;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Preferences IO //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the shared preferences for the app.
     *
     * @return Shared preferences instance
     */
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}