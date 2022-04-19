package com.example.a05proxymodel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 关于文件操作的工具类
 */

public class FileUtils {

    private static final int BUFF_SIZE = 1024 * 1024;

    private static final int SIZE_TYPE_B = 0;
    private static final int SIZE_TYPE_KB = 1;
    private static final int SIZE_TYPE_MB = 2;
    private static final int SIZE_TYPE_GB = 3;
    private static final int SIZE_TYPE_TB = 4;
    private static final int SIZE_TYPE_PB = 5;
    private static final int SIZE_TYPE_EB = 6;

    private static final long SIZE_PER_B = 1;
    private static final long SIZE_PER_KB = SIZE_PER_B * 1024;
    private static final long SIZE_PER_MB = SIZE_PER_KB * 1024;
    private static final long SIZE_PER_GB = SIZE_PER_MB * 1024;
    private static final long SIZE_PER_TB = SIZE_PER_GB * 1024;
    private static final long SIZE_PER_PB = SIZE_PER_TB * 1024;
    private static final long SIZE_PER_EB = SIZE_PER_PB * 1024;

    private static final String[] SIZE_TYPE_DES = {"B", "KB", "MB", "GB", "TB", "PB", "EB"};
    private static final String SD_CACHE_ROOT_FILE_NAME = "com.mxlive";//外部存储卡的缓存文件根目录
    private static final String DEVICE_ID_FILE_NAME = ".mi_dev_id.txt";//设备唯一id
    private static final String DEVICE_IMEI_FILE_NAME = ".mi_dev_imei.txt";//设备唯一IMEI
    private static final String APP_CHANNEL_FILE_NAME = ".mi_channel_";//渠道号文件前缀


    /**
     * 检查是否安装SD卡
     *
     * @return boolean SD卡是否安装.
     */
    public static boolean isStorageAvailable() {
        String storageState = Environment.getExternalStorageState();
        return storageState.equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 将内容写入文件
     *
     * @param filePath eg:/mnt/sdcard/demo.txt
     * @param content  内容
     * @param isAppend 是否追加
     */
    public static void writeFile(String filePath, String content, boolean isAppend) {
        try {
            FileOutputStream fout = new FileOutputStream(filePath, isAppend);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用程序缓存文件夹下的指定目录
     *
     * @param context 上下文环境
     * @param dir     文件夹路径
     * @return
     */
    public static String getLocalPath(Context context, String dir) {
        if (context == null || dir == null || "".equals(dir.trim())) {
            return null;
        }
        File file = new File(context.getCacheDir().getAbsolutePath() + "/" + dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 在应用文件夹路径下创建文件并写入数据 . 文件保存在 /data/data/PACKAGE_NAME/files目录下
     *
     * @param context  上下文环境
     * @param fileName 文件夹名称
     * @param content  需要写入内容
     */
    public static boolean writeToLocal(Context context, String fileName, String content) {
        boolean flag = false;
        if (context == null || fileName == null || "".equals(fileName.trim()) || content == null) {
            return flag;
        }
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (outputStream == null) {
                return flag;
            }
            byte[] bytes = content.getBytes();
            if (bytes == null) {
                return flag;
            }
            outputStream.write(bytes);
            outputStream.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return flag;
        }
    }

    /**
     * 读取应用文件夹路径下指定文件的内容 . 文件目录在 /data/data/PACKAGE_NAME/files目录下
     *
     * @param context  上下文环境
     * @param fileName 文件名称
     * @return String 读取的内容
     */
    public static String readFromLocal(Context context, String fileName) {
        if (context == null || fileName == null || "".equals(fileName)) {
            return null;
        }
        String content = null;
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            if (inputStream == null) {
                return content;
            }
            content = readInStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return content;
        }
    }

    /**
     * 读取应用文件夹路径下指定文件的内容 . 文件目录在 /data/data/PACKAGE_NAME/files目录下
     *
     * @param fileDir 文件路径
     * @return String 读取的内容
     */
    public static String readFromFile(String fileDir) {
        if (fileDir == null || "".equals(fileDir.trim())) {
            return null;
        }
        String content = null;
        try {
            File file = new File(fileDir);
            if (file == null || !file.exists()) {
                return content;
            }
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream == null) {
                return content;
            }
            content = readInStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return content;
        }
    }

    /**
     * 判断文件路劲是否存在
     *
     * @param dir 文件路径字符串
     * @return boolean 路径是否存在
     */
    public static boolean isDirExists(String dir) {
        if (dir == null || "".equals(dir.trim())) {
            return false;
        }
        File file = new File(dir);
        return file.exists();
    }

    /**
     * 根据文件输入流获得文件内容
     *
     * @param inputStream 文件输入流
     * @return String 读取的内容
     */
    public static String readInStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建目录路径
     *
     * @param newPath 指定文件路径
     * @return boolean 路径是否创建成功
     */
    public static boolean createPath(String newPath) {
        if (newPath == null || "".equals(newPath)) {
            return false;
        }
        File newFile = new File(newPath);
        return newFile.exists() || newFile.mkdirs();
    }

    /**
     * 向指定文件中写入内容
     *
     * @param buffer  输入内容的字节数组
     * @param fileDir 需要写入文件的路径
     */
    public static boolean writeToFile(byte[] buffer, String fileDir) {
        if (buffer == null || fileDir == null || "".equals(fileDir.trim())) {
            return false;
        }
        boolean flag = false;
        File file = new File(fileDir);
        FileOutputStream outputStream = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return flag;
        }
    }

    /**
     * 向手机写文件
     *
     * @param buffer    字节数组
     * @param fileName  文件名
     * @param parentDir 父路径
     * @return boolean 文件是否写入成功
     */
    public static boolean writeToFile(byte[] buffer, String parentDir, String fileName) {
        if (!isStorageAvailable() || parentDir == null || "".equals(parentDir.trim()) || fileName == null
                || "".equals(fileName.trim())) {
            return false;
        }
        return writeToFile(buffer, new File(parentDir, fileName).getAbsolutePath());
    }

    /**
     * 根据File文件对象获取文件名
     *
     * @param file 需要获取文件名的File对象
     * @return String(nullable) 获取得的文件名称
     */
    public static String getFullName(File file) {
        if (file == null) {
            return null;
        }
        return getFullName(file.getAbsolutePath());
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath 需要获取文件名的文件的绝对路径
     * @return String(nullable) 获取得的文件名称
     */
    public static String getFullName(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        }
        int index = filePath.lastIndexOf(File.separator);
        String substring = index == -1 ? filePath : filePath.substring(index + 1);
        return substring;
    }

    /**
     * 根据File文件对象获取文件名但不包含扩展名
     *
     * @param file 需要获取文件名的文件的绝对路径
     * @return String(nullable) 获取得的文件名称
     */
    public static String getNameIgnoreSuffix(File file) {
        if (file == null) {
            return null;
        }
        return getNameIgnoreSuffix(file.getAbsolutePath());
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath 需要获取文件名的文件的绝对路径
     * @return String(nullable) 获取得的文件名称
     */
    public static String getNameIgnoreSuffix(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return null;
        }
        String fullName = getFullName(filePath);
        if (fullName == null) {
            return null;
        }
        int index = fullName.lastIndexOf('.');
        return index == -1 ? fullName : fullName.substring(0, index);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 需要获取文件拓展名的文件名
     * @return String(nullable) 文件拓展名
     */
    public static String getFileSuffix(String fileName) {
        if (fileName == null || "".equals(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf('.');
        return index == -1 ? null : fileName.substring(index + 1);
    }

    /**
     * 获取文件扩展名
     *
     * @param file 需要获取文件拓展名的File类型文件
     * @return String(nullable) 文件拓展名
     */
    public static String getFileSuffix(File file) {
        if (file == null) {
            return null;
        }
        return getFileSuffix(file.getName());
    }

    /**
     * 获取文件大小
     *
     * @param filePath 需要获得文件大小信息的文件路径
     * @return long 文件大小值
     */
    public static long getFileSize(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return 0;
        }
        return getFileSize(new File(filePath));
    }

    /**
     * 根据所给long值获取文件大小描述
     *
     * @param file    需要获取大小的文件
     * @param pattern 数字显示模板
     * @return Sting 文件大小值描述语
     */
    public static String getFileSize(File file, String pattern) {
        long size = (file == null || !file.exists()) ? 0 : getFileSize(file);
        return long2Size(size, pattern);
    }

    public static String long2Size(long size, String pattern) {
        if (pattern == null) {
            pattern = "0.0";
        }
        DecimalFormat format = new DecimalFormat(pattern);
        if (size <= 0) {
            return format.format(0) + "B";
        }
        if (size < SIZE_PER_KB) {
            return format.format(size * 1.0f / SIZE_PER_B) + "B";
        }
        if (size < SIZE_PER_MB) {
            return format.format(size * 1.0f / SIZE_PER_KB) + "KB";
        }
        if (size < SIZE_PER_GB) {
            return format.format(size * 1.0f / SIZE_PER_MB) + "MB";
        }
        if (size < SIZE_PER_TB) {
            return format.format(size * 1.0f / SIZE_PER_GB) + "GB";
        }
        if (size < SIZE_PER_PB) {
            return format.format(size * 1.0f / SIZE_PER_TB) + "TB";
        }
        if (size < SIZE_PER_EB) {
            return format.format(size * 1.0f / SIZE_PER_PB) + "PB";
        }
        return format.format(size / SIZE_PER_TB) + "EB";
    }

    public static String long2Size(long size) {
        return long2Size(size, null);
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param file     需要获得文件大小的文件
     * @param pattern  数字显示模板
     * @param sizeType 需要返回的结果类型 B.KB.MB.GB.TB ...
     * @return
     */
    public static String getFileSize(File file, String pattern, int sizeType) {
        DecimalFormat format = new DecimalFormat(pattern);
        if (file == null || pattern == null || "".equals(pattern.trim())) {
            return format.format(0) + SIZE_TYPE_DES[Math.min(sizeType, SIZE_TYPE_DES.length - 1)];
        }
        return format.format((double) getFileSize(file) / Math.pow(1024, Math.min(sizeType, SIZE_TYPE_DES.length - 1)))
                + SIZE_TYPE_DES[Math.min(sizeType, SIZE_TYPE_DES.length - 1)];
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return base64;
    }

    /**
     * 获取文件大小
     *
     * @param file 需要获取大小的文件
     * @return long 文件大小的long值
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.isFile()) {
            return file.length();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                return 0;
            }
            for (File child : files) {
                size += getFileSize(child);
            }
        }
        return size;
    }

    /**
     * 检查文件在根目录是否存在
     *
     * @param name 指定文件夹的名称
     * @return boolean 文件是否存在的结果
     */
    public static boolean checkFileExists(String name) {
        if (name == null || "".equals(name)) {
            return false;
        }
        File file = new File(Environment.getExternalStorageDirectory(), name);
        return file.exists();
    }

    /**
     * 检查路径是否存在
     *
     * @param path 需要进行判断的路径
     * @return boolean 路径是否存在的结果
     */
    public static boolean isPathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return (- 1.说明没有安装SD卡)
     */
    public static long getFreeDiskSpace() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return -1;
        }
        long freeSpace = 0;
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            freeSpace = availableBlocks * blockSize / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return (freeSpace);
        }
    }

    /**
     * 获得SD卡的剩余空间
     */
    public static long getSDFreeSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long availSize = availableBlocks * blockSize;
        return availSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 检查是否安装外置的SD卡
     *
     * @return boolean 是否外置SD卡的结果
     */
    public static boolean isExternalStorageAvailable() {
        Map<String, String> env = System.getenv();
        return env.containsKey("SECONDARY_STORAGE");
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param file 需要进行删除操作的File文件
     * @return boolean 是否成功进行删除操作
     */
    public static void deleteFile(File file) {
        if (file == null || !file.canWrite() || !file.exists()) {
            return;
        }
        try {
            if (file.isFile()) {
                safetyDelete(file); // 如果file是文件类型而不是目录类型 则直接进行删除操作
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    safetyDelete(file); // 如果该file是目录类型 但是并不存在子文件 则直接进行删除该路径
                    return;
                }
                for (File child : files) {
                    deleteFile(child); // 逐个删除子文件
                }
                safetyDelete(file); // 删除完子文件则删除根目录
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除单个文件或路径
     *
     * @param file 需要删除的File类型文件
     */
    private static void safetyDelete(File file) {
        File deleteFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
        file.renameTo(deleteFile);
        deleteFile.delete();
    }

    /**
     * 文件重命名
     *
     * @param parentPath 父文件夹路径
     * @param oldName    旧的文件名
     * @param newName    新的文件名
     * @return boolean 文件名是否重命名成功
     */
    public static boolean renameFile(String parentPath, String oldName, String newName) {
        if (parentPath == null || "".equals(parentPath.trim()) || oldName == null || "".equals(oldName.trim())
                || newName == null || "".equals(newName.trim())) {
            return false;
        }
        return renameFile(new File(parentPath, oldName), newName);
    }

    /**
     * 文件重命名
     *
     * @param renameFile 需要进行重命名的文件
     * @param newName    新的文件名
     * @return boolean 文件名是否重命名成功
     */
    public static boolean renameFile(File renameFile, String newName) {
        if (renameFile == null || newName == null || "".equals(newName.trim())) {
            return false;
        }
        return renameFile.renameTo(new File(renameFile.getParent() + "/" + newName));
    }

    /**
     * 获取目录子文件个数
     *
     * @param rootFile        需要统计数量的文件
     * @param ignoreFile      是否忽略统计文件数量
     * @param ignoreDirectory 是否忽略统计文件夹数量
     */
    public static int getChildCount(File rootFile, boolean ignoreFile, boolean ignoreDirectory) {
        if (rootFile == null) {
            return 0;
        }
        File[] files = rootFile.listFiles();
        if (files == null || !rootFile.isDirectory()) {
            return 0;
        }
        int count = 0;
        for (File file : files) {
            if (file.isFile() && !ignoreFile) {
                count++;
            }
            if (file.isDirectory() && !ignoreDirectory) {
                count++;
            }
            count += getChildCount(file, ignoreFile, ignoreDirectory);
        }
        return count;
    }

    /**
     * 拷贝文件从一个文件夹到另一个文件夹
     *
     * @param from 源文件
     * @param to   目标文件
     * @return File 返回目标文件对象
     */
    public static File fileChannelCopy(File from, File to) {
        if (from == null || to == null || !from.exists() || !to.getParentFile().exists() || !from.isFile()
                || !to.isFile()) {
            return null;
        }
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        FileChannel outChannel = null;
        FileChannel inChannel = null;
        try {
            inputStream = new FileInputStream(from);
            outputStream = new FileOutputStream(to);
            inChannel = inputStream.getChannel(); // 得到对应的文件通道
            outChannel = outputStream.getChannel(); // 得到对应的文件通道
            inChannel.transferTo(0, inChannel.size(), outChannel); // 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                inChannel.close();
                outChannel.close();
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return to;
    }

    /**
     * 打开指定文件
     *
     * @param context 上下文环境
     * @param file    需要打开的文件
     */
    public static void openFile(Context context, File file, String type) {
        try {
            if (file == null || !file.isFile()) {
                return;
            }
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            if (TextUtils.isEmpty(type) || "application/octet-stream".equals(type)) {
                type = getFileMimeType(file.getName());
            }
            if ("*/*".equals(type)) {
                type = "application/octet-stream";
            }
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取file的MimeType
     * <p>
     * 需要确定类型的file文件
     *
     * @return String 文件的MimeType
     */
    public static String getFileMimeType(String fileName) {

        String type = "";
        String suffix = getFileSuffix(fileName).toLowerCase();

        /* 依扩展名的类型决定MimeType */
        if ("apk".equals(suffix)) {
            type = "application/vnd.android.package-archive";
        } else if ("3gp".equals(suffix) || "mp4".equals(suffix) || "avi".equals(suffix) || "mov".equals(suffix)
                || "3pg".equals(suffix) || "flv".equals(suffix) || "rmvb".equals(suffix) || "mkv".equals(suffix)
                || "f4v".equals(suffix)) {
            type = "video/*";
        } else if ("bmp".equals(suffix) || "jpg".equals(suffix) || "tiff".equals(suffix) || "gif".equals(suffix)
                || "jpeg".equals(suffix) || "tga".equals(suffix) || "svg".equals(suffix) || "png".equals(suffix)) {
            type = "image/*";
        } else if ("m4a".equals(suffix) || "mp3".equals(suffix) || "aac".equals(suffix) || "flac".equals(suffix)
                || "ape".equals(suffix) || "mid".equals(suffix) || "xmf".equals(suffix) || "wav".equals(suffix)
                || "wma".equals(suffix) || "wav".equals(suffix) || "ogg ".equals(suffix)) {
            type = "audio/*";
        } else if ("rar".equals(suffix)) {
            type = "application/x-rar-compressed";
        } else if ("zip".equals(suffix)) {
            type = "application/zip";
        } else if ("tar".equals(suffix)) {
            type = "application/x-tar";
        } else if ("exe".equals(suffix)) {
            type = "application/octet-stream";
        } else if ("gz".equals(suffix)) {
            type = "application/x-gzip";
        } else if ("css".equals(suffix) || "323".equals(suffix) || "htm".equals(suffix) || "html".equals(suffix)
                || "stm".equals(suffix) || "uls".equals(suffix) || "txt".equals(suffix) || "vcf".equals(suffix)) {
            type = "text/*";
        } else {
            type = "*/*";
        }
        return type;
    }

    /**
     * 解压缩一个文件
     *
     * @param originalFile 压缩文件
     * @param folderPath   解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile(File originalFile, String folderPath) throws IOException {
        if (originalFile == null || folderPath == null || "".equals(folderPath.trim())) {
            return;
        }
        ZipFile zipFile = new ZipFile(originalFile);
        for (Enumeration<?> entries = zipFile.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            InputStream inputStream = zipFile.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte[] buffer = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            inputStream.close();
            out.close();
        }
    }

    /**
     * 通过Uri找到File
     *
     * @param context context
     * @param uri     uri
     * @return File
     */
    public static File uri2File(Activity context, Uri uri) {
        File file;
        String[] project = {MediaStore.Images.Media.DATA};
        Cursor actualImageCursor = context.getContentResolver().query(uri, project, null, null, null);
        if (actualImageCursor != null) {
            int actual_image_column_index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualImageCursor.moveToFirst();
            String img_path = actualImageCursor.getString(actual_image_column_index);
            file = new File(img_path);
        } else {
            file = new File(uri.getPath());
        }
        if (actualImageCursor != null) {
            actualImageCursor.close();
        }
        return file;
    }

    // 获取sd卡路径
    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从存储卡获取设备ID
     */
    public static String getDeviceIDFromSD(Context context) {
        if (!isFileHavePermission(context))
            return null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), SD_CACHE_ROOT_FILE_NAME);
            if (!dir.exists()) {
                return null;
            }
            File deviceIdFile = new File(dir, DEVICE_ID_FILE_NAME);
            if (!deviceIdFile.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(deviceIdFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存设备ID到存储卡
     *
     * @param deviceId
     */
    public static void saveDeviceIDToSD(Context context,String deviceId) {
        if (!isFileHavePermission(context))
            return;
        File dir = new File(Environment.getExternalStorageDirectory(), SD_CACHE_ROOT_FILE_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File targetFile = new File(dir, DEVICE_ID_FILE_NAME);
        if (!targetFile.exists()) {
            try {
                OutputStreamWriter osw;
                osw = new OutputStreamWriter(new FileOutputStream(targetFile));
                try {
                    osw.write(deviceId);
                    osw.flush();
                    osw.close();
                    Log.d("FileUtils", "saveDeviceIDToSD成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从存储卡获取设备IMEI
     */
    public static String getDeviceIMEIFromSD(Context context) {
        if (!isFileHavePermission(context))
            return null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), SD_CACHE_ROOT_FILE_NAME);
            if (!dir.exists()) {
                return null;
            }
            File deviceIdFile = new File(dir, DEVICE_IMEI_FILE_NAME);
            if (!deviceIdFile.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(deviceIdFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存设备IMEI到存储卡
     *
     * @param imei
     */
    public static void saveDeviceIMEIToSD(Context context, String imei) {
        if (!isFileHavePermission(context))
            return;
        File dir = new File(Environment.getExternalStorageDirectory(), SD_CACHE_ROOT_FILE_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File targetFile = new File(dir, DEVICE_IMEI_FILE_NAME);
        if (!targetFile.exists()) {
            try {
                OutputStreamWriter osw;
                osw = new OutputStreamWriter(new FileOutputStream(targetFile));
                try {
                    osw.write(imei);
                    osw.flush();
                    osw.close();
                    Log.d("FileUtils", "saveDeviceIMEIToSD成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从存储卡获取APP的渠道号
     */
    public static String getAPPChannelFromSD(Context context,String packageName) {
        if (!isFileHavePermission(context))
            return null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), SD_CACHE_ROOT_FILE_NAME);
            if (!dir.exists()) {
                return null;
            }
            packageName = packageName.replace(".", "");
            File deviceIdFile = new File(dir, APP_CHANNEL_FILE_NAME + packageName + ".txt");
            if (!deviceIdFile.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(deviceIdFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存APP的渠道号到存储卡
     *
     * @param appChannel
     */
    public static void saveAPPChannelToSD(Context context,String appChannel, String packageName) {
        if (!isFileHavePermission(context))
            return;
        File dir = new File(Environment.getExternalStorageDirectory(), SD_CACHE_ROOT_FILE_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        packageName = packageName.replace(".", "");
        File targetFile = new File(dir, APP_CHANNEL_FILE_NAME + packageName + ".txt");
        if (!targetFile.exists()) {
            try {
                OutputStreamWriter osw;
                osw = new OutputStreamWriter(new FileOutputStream(targetFile));
                try {
                    osw.write(appChannel);
                    osw.flush();
                    osw.close();
                    Log.d("FileUtils", "saveAPPChannelToSD成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否有文件权限
     *
     * @param context
     * @return
     */
    public static boolean isFileHavePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (android.content.pm.PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            } else {
                return false;
            }
        }
        return true;

    }


}