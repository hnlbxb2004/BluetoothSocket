package com.xloong.library.bluesocket.message;

import android.os.Environment;
import android.os.Parcel;

import com.xloong.library.bluesocket.utils.TypeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by xubingbing on 2017/8/10.
 */

public class ImageMessage implements IMessage<File> {

    private File mContent;
    private String mExtend;
    private long mLength;
    private byte mType = TYPE_BYTE;

    public ImageMessage() {

    }

    @Override
    public byte getType() {
        return mType;
    }

    @Override
    public void setType(byte type) {
        mType = type;
    }

    @Override
    public long getLength() {
        return mLength;
    }

    @Override
    public void setLength(long length) {
        mLength = length;
    }

    @Override
    public File getContent() {
        return mContent;
    }

    @Override
    public void setExtend(String extend) {
        mExtend = extend;
    }


    @Override
    public void setContent(File content, String extend) {
        mContent = content;
        mExtend = extend;
        try {
            FileInputStream fio = new FileInputStream(mContent);
            mLength = fio.available();
            fio.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void parseContent(InputStream inputStream) throws IOException {
        mContent = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), mExtend);

        if (!mContent.getParentFile().exists()) {
            mContent.getParentFile().mkdirs();
        }
        if (mContent.exists()) {
            mContent.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(mContent);


        long tempLength = 0;
        byte[] buffer = null;
        while (tempLength < mLength) {
            if (mLength - tempLength < (1024 * 64)) {
                buffer = new byte[(int) (mLength - tempLength)];
            } else {
                if (buffer == null)
                    buffer = new byte[1024 * 64];
            }
            int readLength = inputStream.read(buffer, 0, buffer.length);
            fos.write(buffer, 0, readLength);
            tempLength += readLength;
        }

        fos.close();
    }


    @Override
    public void writeContent(OutputStream outputStream) throws IOException {
        outputStream.write(creatHeader());
        outputStream.write(0xA);
        outputStream.write(0xD);

        FileInputStream fio = new FileInputStream(mContent);


        byte[] buffer = new byte[64 * 1024];
        int length = 0;
        int temp = 0;
        while ((length = fio.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, length);
            outputStream.flush();
            temp += length;
        }

//        Log.d("writeContent:" ," 写完:" +temp);
    }

    @Override
    public byte[] creatHeader() {
        byte[] extend = mExtend.getBytes();
        byte[] length = TypeUtils.longToBytes(getLength());
        byte[] header = new byte[10 + extend.length];
        header[0] = HEADER;                                         //魔数
        header[1] = getType();                                     //类型
        System.arraycopy(length, 0, header, 2, length.length);     //长度
        System.arraycopy(extend, 0, header, 10, extend.length);     //扩展信息
        return header;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mContent.getAbsolutePath());
        dest.writeString(this.mExtend);
        dest.writeLong(this.mLength);
        dest.writeByte(this.mType);
    }

    protected ImageMessage(Parcel in) {
        this.mContent = new File(in.readString());
        this.mExtend = in.readString();
        this.mLength = in.readLong();
        this.mType = in.readByte();
    }

    public static final Creator<ImageMessage> CREATOR = new Creator<ImageMessage>() {
        @Override
        public ImageMessage createFromParcel(Parcel source) {
            return new ImageMessage(source);
        }

        @Override
        public ImageMessage[] newArray(int size) {
            return new ImageMessage[size];
        }
    };

    @Override
    public String toString() {
        return "ImageMessage{" +
                "mContent=" + mContent +
                ", mExtend='" + mExtend + '\'' +
                ", mLength=" + mLength +
                ", mType=" + mType +
                '}';
    }
}
