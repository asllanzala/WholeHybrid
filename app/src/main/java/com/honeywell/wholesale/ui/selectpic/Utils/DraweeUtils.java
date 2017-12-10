package com.honeywell.wholesale.ui.selectpic.Utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by zhujunyu on 16/6/14.
 */
public class DraweeUtils {
    private static String TAG = "";

    public static void showThunb(Uri uri, SimpleDraweeView draweeView) {

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).setResizeOptions(new ResizeOptions(200, 200)).build();

        DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setAutoPlayAnimations(true)
                .setOldController(draweeView.getController()).setControllerListener(new BaseControllerListener<ImageInfo>()).build();

        draweeView.setController(controller);


    }
}
