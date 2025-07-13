package com.github.soil.util;

import java.io.File;

/**
 * @author tanruidong
 * @since 2024/06/28 22:35
 */
public class ReverseTest {
    public static void main(String[] args) {
        String targetPath = "D:\\404\\漫画\\逆转\\44-46";
        String filePath = "D:\\404\\漫画\\逆转\\46";

        File fileFolder = new File(filePath);
        System.out.println(fileFolder.getName());
        String sort = fileFolder.getName();
        File[] files = fileFolder.listFiles();
        for (File file : files) {
            String fileName = file.getName().substring(0, file.getName().indexOf("."));
            int index = Integer.parseInt(fileName);
            String newName = String.format("%04d_%04d.jpg", Integer.parseInt(sort), index);
            file.renameTo(new File(targetPath +"\\" + newName));
        }
        System.out.println("文件数量：" + files.length);
    }

//    public static void main(String[] args) {
//        String targetPath = "D:\\404\\漫画\\逆转\\44-46";
//
//        File fileFolder = new File(targetPath);
//        File[] files = fileFolder.listFiles();
//        for (File file : files) {
//            String fileName = file.getName();
//            file.renameTo(new File(targetPath +"\\" + fileName +".jpg"));
//        }
//        System.out.println("文件数量：" + files.length);
//    }
}
