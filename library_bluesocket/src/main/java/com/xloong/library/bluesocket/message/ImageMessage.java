package com.xloong.library.bluesocket.message;

import android.os.Environment;
import android.os.Parcel;

import com.xloong.library.bluesocket.utils.TypeUtils;

import java.io.File;
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
        return null;
    }

    @Override
    public void setExtend(String extend) {
        mExtend = extend;
    }



    @Override
    public void setContent(File content, String extend) {
        mContent = content;
        mExtend = extend;
        mLength = mContent.length();
    }

    @Override
    public void parseContent(InputStream inputStream) throws IOException {
        mContent = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), mExtend);

        if (mContent.exists()) {
            mContent.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(mContent);
        long tempLength = 0;

        byte[] buffer = null;
        while (tempLength < mLength) {
            if (mLength - tempLength < 1024 * 1024 * 2) {
                buffer = new byte[(int) (mLength - tempLength)];
            } else {
                if (buffer == null)
                    buffer = new byte[1024 * 1024 * 2];
            }
            inputStream.read(buffer);
            fos.write(buffer);
            fos.flush();
            tempLength += buffer.length;
        }
        fos.close();
    }


    @Override
    public void writeContent(OutputStream outputStream) throws IOException {
        outputStream.write(creatHeader());
        outputStream.write(0xA);
        outputStream.write(0xD);

        mContent = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), mExtend);

        if (mContent.exists()) {
            mContent.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(mContent);


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
}
