package com.yuqf.fengmomusic.utils;

import android.text.TextUtils;
import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtils {

    public static String convertToPinYin(String inputStr) {
        if (TextUtils.isEmpty(inputStr) || inputStr == null) {
            return "";
        }

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] inputCharArr = inputStr.toCharArray();
        String output = "";
        for (int i = 0; i < inputStr.length(); i++) {
            String char2Str = Character.toString(inputCharArr[i]);
            if (char2Str.matches("[\\u4E00-\\u9FA5]")) {
                //汉字
                String[] temp = new String[0];
                try {
                    temp = PinyinHelper.toHanyuPinyinStringArray(inputCharArr[i], format);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                Log.d("PinYinConvert", char2Str + "======" + temp[0] + "\n");
                if (temp != null && temp.length > 0)
                    output += temp[0];
            } else if (char2Str.matches("[A-Za-z0-9]")) {
                //字母或者数字
                output += char2Str;
            }
        }
        return output;
    }
}
