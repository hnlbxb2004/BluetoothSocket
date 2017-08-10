package com.xloong.bluetoothsocketdemo;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.jaiky.imagespickers.ImageConfig;
import com.jaiky.imagespickers.ImageSelector;
import com.jaiky.imagespickers.ImageSelectorActivity;

import java.util.List;

/**
 * Created by xubingbing on 2017/8/10.
 */

public abstract class BaseActivity  extends Activity{



    public void chooseImage(){
        ImageConfig imageConfig
                = new ImageConfig.Builder(new GlideLoader())
                .steepToolBarColor(getResources().getColor(R.color.blue))
                .titleBgColor(getResources().getColor(R.color.blue))
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                .titleTextColor(getResources().getColor(R.color.white))
                // 开启单选   （默认为多选）
                .singleSelect()
                // 开启拍照功能 （默认关闭）
                .showCamera()
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
                .filePath("/temp/picture")
                .build();


        ImageSelector.open(this, imageConfig);   // 开启图片选择器
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // 获取选中的图片路径列表 Get Images Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            onChooseImage(pathList.get(0));
            for (String path : pathList) {
                Log.i("ImagePath", path);
            }
        }
    }

    public abstract void onChooseImage(String path);

}
