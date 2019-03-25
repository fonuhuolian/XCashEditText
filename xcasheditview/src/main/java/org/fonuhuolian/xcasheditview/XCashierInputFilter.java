package org.fonuhuolian.xcasheditview;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义过滤器
 * 限制为现金格式
 */
public class XCashierInputFilter implements InputFilter {

    // 正则表达·匹配模式
    private Pattern mPattern;

    // 输入的最大金额
    private double MAX_VALUE;
    // 小数点后的位数
    private static final int POINTER_LENGTH = 2;

    private static final String POINTER = ".";

    private static final String ZERO = "0";

    private XCashInputListener editListener;

    public XCashierInputFilter() {
        mPattern = Pattern.compile("([0-9]|\\.)*");
        this.MAX_VALUE = Double.MAX_VALUE;
    }

    public XCashierInputFilter(double MAX_VALUE, XCashInputListener editListener) {
        mPattern = Pattern.compile("([0-9]|\\.)*");
        this.MAX_VALUE = MAX_VALUE;
        this.editListener = editListener;
    }

    /**
     * 只要用户 调用 setText(); dstart = 0
     *
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end    新输入的字符串终点下标，一般为source长度-1
     * @param dest   输入之前文本框内容
     * @param dstart 要替换或者添加的起始位置，即光标所在的位置
     * @param dend   原内容终点坐标，若为选择一串字符串进行更改，则为选中字符串 最后一个字符在dest中的位置。
     * @return 替换光标所在位置的CharSequence
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        // 新输入的字符串
        String sourceText = source.toString();
        // 输入前的字符串
        String destText = dest.toString();


        // sourceText为空
        // 情况1：用户执行了删除操作
        // 情况2：setText("")
        if (TextUtils.isEmpty(sourceText)) {

            // 删除操作(手动操作)
            if (dend > dstart) {

                // 删除最后一个数据
                if (dend == 1) {

                    if (editListener != null)
                        editListener.onInputCorrectCash(0);

                } else {

                    // 没清空需要格式化double 并 验证金额
                    double sumText = Double.parseDouble(destText.substring(0, dstart));

                    if (sumText > MAX_VALUE && editListener != null) {
                        editListener.onInputOverflow(sumText);
                    }

                    if (sumText <= MAX_VALUE && editListener != null) {
                        editListener.onInputCorrectCash(sumText);
                    }
                }

            } else {

                // dend == dstart setText("");
                // setText("");

                if (editListener != null)
                    editListener.onInputCorrectCash(0);
            }


            // 返回""
            return "";

        } else {

            Matcher matcher = mPattern.matcher(source);

            // 不符合正则表达式(不执行写入)
            if (!matcher.matches()) {

                // 情况1: setText("0..0");执行了清空
                // 情况2: 第一个字符输入错误 如 +
                if (dstart == 0 && editListener != null)
                    editListener.onInputCorrectCash(0);

                return "";

            } else {

                // 符合正则表达式的情况

                if (destText.contains(POINTER)) {

                    // 原数据包含.的情况下，手动输入 只能输入数字 不可能 dstart = 0
                    // 原数据包含.的情况  setText() dstart = 0

                    if (dstart == 0) {

                        if (isCashText(sourceText)) {

                            double v = Double.parseDouble(sourceText);

                            if (v > MAX_VALUE && editListener != null) {
                                editListener.onInputOverflow(v);
                            }

                            if (v <= MAX_VALUE && editListener != null) {
                                editListener.onInputCorrectCash(v);
                            }

                            return sourceText;

                        } else {

                            if (editListener != null)
                                editListener.onInputCorrectCash(0);

                            return "";
                        }

                    } else {

                        // 新输入的字符串包含点
                        if (sourceText.equals(POINTER)) {

                            // 只能输入一个小数点
                            return "";

                        } else {

                            // 验证小数点精度，保证小数点后只能输入两位
                            int pointIndex = destText.indexOf(POINTER);

                            if (dend - pointIndex > POINTER_LENGTH) {
                                return "";
                            }


                            // 验证新增输入-总金额的大小并回调
                            double sumText = Double.parseDouble(destText + sourceText);

                            if (sumText > MAX_VALUE && editListener != null) {
                                editListener.onInputOverflow(sumText);
                            }

                            if (sumText <= MAX_VALUE && editListener != null) {
                                editListener.onInputCorrectCash(sumText);
                            }

                            return sourceText;
                        }

                    }


                } else {

                    // 原数据不包含.的情况下，只能输入小数点和数字，但首位不能输入小数点
                    // 手动输入 和 setText() dstart = 0 (两种情况)

                    if (dstart == 0) {

                        // 只要新输入的不对 就是清空

                        // 判断 sourceText > 1 必定setText()
                        if (sourceText.length() > 1) {

                            if (isCashText(sourceText)) {


                                double v = Double.parseDouble(sourceText);

                                if (v > MAX_VALUE && editListener != null) {
                                    editListener.onInputOverflow(v);
                                }

                                if (v <= MAX_VALUE && editListener != null) {
                                    editListener.onInputCorrectCash(v);
                                }


                                return sourceText;

                            } else {

                                if (editListener != null)
                                    editListener.onInputCorrectCash(0);

                                return "";
                            }

                        } else {

                            // 手动输入首位 或者 setText("1");长度为1

                            if (sourceText.equals(POINTER)) {

                                if (editListener != null)
                                    editListener.onInputCorrectCash(0);

                                return "";
                            }


                            // 不符合正则表达式
                            if (!matcher.matches()) {

                                if (editListener != null)
                                    editListener.onInputCorrectCash(0);

                                return "";

                            } else {

                                // 由上方 方法拦截 此时输入为纯数字
                                double v = Double.parseDouble(sourceText);

                                if (v > MAX_VALUE && editListener != null) {
                                    editListener.onInputOverflow(v);
                                }

                                if (v <= MAX_VALUE && editListener != null) {
                                    editListener.onInputCorrectCash(v);
                                }

                                return sourceText;
                            }


                        }

                    } else {

                        // 手动输入的情况 且 不是首位输入 (原数据 无.)


                        // 没有输入小数点的情况下，首位是0只能输入小数点
                        if (destText.equals(ZERO) && !sourceText.equals(POINTER)) {
                            return "";
                        }

                        // 验证新增输入-总金额的大小并回调
                        double sumText = Double.parseDouble(destText + sourceText);

                        if (sumText > MAX_VALUE && editListener != null) {
                            editListener.onInputOverflow(sumText);
                        }

                        if (sumText <= MAX_VALUE && editListener != null) {
                            editListener.onInputCorrectCash(sumText);
                        }

                        return sourceText;

                    }


                }
            }

        }


    }

    /**
     * 验证是否是现金
     */
    private boolean isCashText(String value) {

        if (TextUtils.isEmpty(value))
            return false;

        Matcher matcher = Pattern.compile("([0-9]|\\.)*").matcher(value);
        // 不符合正则表达式
        if (!matcher.matches())
            return false;

        // normalText 由数字和点组成

        try {

            double v = Double.parseDouble(value);

            // 正确的double类型 如果包含点
            if (value.contains(".")) {

                int pointIndex = value.indexOf(".");

                // .10  00.00 1.555
                if (pointIndex == 0 || (value.startsWith("0") && pointIndex > 1) || (pointIndex + 3 < value.length()))
                    return false;

            } else {

                // 01
                if (value.startsWith("0"))
                    return false;
            }

        } catch (NumberFormatException e) {

            // 0...01
            return false;
        }

        return true;

    }
}
