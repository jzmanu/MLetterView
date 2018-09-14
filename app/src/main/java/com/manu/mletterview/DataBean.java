package com.manu.mletterview;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Powered by jzman.
 * Created on 2018/8/21 0021.
 */
public class DataBean {

    private String nameChinese;
    private String namePinyin;
    private String nameHeader;

    public String getNameChinese() {
        return nameChinese;
    }

    public void setNameChinese(String nameChinese) {
        this.nameChinese = nameChinese;
        this.namePinyin = getChineseToPinyin(nameChinese);
        if (isLetter(this.namePinyin.substring(0, 1))) {
            this.nameHeader = this.namePinyin.substring(0, 1);
        }else{
            this.nameHeader = "~";
        }
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public String getNameHeader() {
        return nameHeader;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "nameChinese='" + nameChinese + '\'' +
                ", namePinyin='" + namePinyin + '\'' +
                ", nameHeader='" + nameHeader + '\'' +
                '}';
    }

    /**
     * 获取汉字拼音
     *
     * @param chinese
     * @return
     */
    public static String getChineseToPinyin(String chinese) {
        StringBuilder builder = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] charArray = chinese.toCharArray();
        for (char aCharArray : charArray) {
            if (Character.isSpaceChar(aCharArray)) {
                continue;
            }
            try {
                String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(aCharArray, format);
                if (pinyinArr != null) {
                    builder.append(pinyinArr[0]);
                } else {
                    builder.append(aCharArray);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
                builder.append(aCharArray);
            }
        }
        return builder.toString();
    }

    /**
     * 判断字符串是否是字母
     * @param str
     * @return
     */
    public static boolean isLetter(String str) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(str);
        return m.matches();
    }
}
