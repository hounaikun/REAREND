package com.kun.IO181;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-21 15:30
 **/
public class DirList {
    public static void main(String[] args) {
        File path = new File("D:\\");
        String[] list;
        if (args.length == 0) {
            // 列出path下所有的目录
            list = path.list();
        } else {
            //在path下，list(new FilenameFilter())调用accept() 筛选目录，
            //源码：filter.accept(this, names[i]) ,filter 就是 new FilenameFilter(),this是path,names 是list()
            /*
             * list()方法会为此目录对象下的每个文件名调用 accept(),来判断该文件是否包含在内;
             * 判断结果由 accept()返回的布尔值表示。
             * */
            list = path.list(new FilenameFilter() {
                private Pattern pattern = Pattern.compile(args[0]);

                @Override
                public boolean accept(File dir, String name) {
                    /*
                     * compile(String regex) **将给定的正则表达式编译为模式。
                     * matcher(CharSequence input) **创建一个匹配器，匹配给定的输入与此模式。
                     * matches() **尝试将整个区域与模式进行匹配。
                     * */
                    return pattern.matcher(name).matches();
                }
            });
            Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
            for (String dir : list) {
                System.out.println(dir);
            }
        }
        Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
        for (String dir : list) {
            System.out.println(dir);
        }
    }
}
