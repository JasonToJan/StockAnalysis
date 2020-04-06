package jason.jan.stockanalysis.utils;

import android.content.Context;

import java.io.InputStream;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2020/4/5 8:59
 */
public class AssetsUtils {

    private static final String TAG = "AssetsUtils";

    public static String readAssetsTxt(Context context, String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName + ".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (Throwable e) {
            // Should never happen!
            LogUtils.e(TAG, " ERROR##" + e.getMessage());
        }
        return "读取错误，请检查文件名";
    }
}
