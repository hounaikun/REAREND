package com.kun.spring_security_jsp.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-29 17:19
 **/
public class PinyinUtil {
    public static void main(String[] args) {
        System.out.println(PinyinUtil.getPinYin("王利于aaa"));
    }
    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return String
     */
    private static String getPinYin(String src) {
        char[] t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for(int i = 0; i < t0; ++i) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
                    t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串t4后
                    t4 = t4 + Character.toString(t1[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination var8) {
            var8.printStackTrace();
        }
        return t4;
    }
}
