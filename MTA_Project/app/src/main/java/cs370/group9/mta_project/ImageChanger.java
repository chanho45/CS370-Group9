package cs370.group9.mta_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

public class ImageChanger {
    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color)
    {
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 0);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap sourceBitmap, float degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(sourceBitmap,
                0, 0,
                sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                matrix, true);
    }

    public static Bitmap combineBitmap(Bitmap bit1, Bitmap bit2){
        int width = bit1.getWidth() > bit2.getWidth()?
                bit1.getWidth(): bit2.getWidth();
        int height = bit1.getHeight() > bit2.getHeight()?
                bit1.getHeight(): bit2.getHeight();

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bit1,
                (width - bit1.getWidth()) / 2,
                (height - bit1.getHeight()) / 2,
                null);
        canvas.drawBitmap(bit2,
                (width - bit2.getWidth()) / 2,
                (height - bit2.getHeight()) / 2,
                null);

        return result;
    }
}
