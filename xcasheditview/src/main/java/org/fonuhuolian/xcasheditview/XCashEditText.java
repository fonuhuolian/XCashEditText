package org.fonuhuolian.xcasheditview;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 现金输入的EditText (支付宝模式)
 * ①支持设置可输入的最大值
 * ②不允许操作光标，永远只在末尾
 * ③只验证输入的格式是否正确 是否符合现金的格式
 */
public class XCashEditText extends android.support.v7.widget.AppCompatEditText {

    public XCashEditText(Context context) {
        super(context);
        init();
    }

    public XCashEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XCashEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        noClick();
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // 验证xml文件  android:text="" 属性
        Editable text = getText();

        if (text != null) {
            String normalText = text.toString();

            Matcher matcher = Pattern.compile("([0-9]|\\.)*").matcher(normalText);
            // 不符合正则表达式
            if (!matcher.matches()) {
                this.setText("");
            } else {

                // normalText 由数字和点组成

                try {

                    double v = Double.parseDouble(normalText);

                    // 正确的double类型 如果包含点
                    if (normalText.contains(".")) {

                        int pointIndex = normalText.indexOf(".");

                        // .10  00.00 1.555
                        if (pointIndex == 0 || (normalText.startsWith("0") && pointIndex > 1) || (pointIndex + 3 < normalText.length())) {
                            this.setText("");
                        }

                    } else {

                        // 01
                        if (normalText.startsWith("0")) {
                            this.setText("");
                        }
                    }

                } catch (NumberFormatException e) {

                    // 0...01
                    this.setText("");
                }

            }

        }

        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new XCashierInputFilter();
        this.setFilters(inputFilter);
    }

    /**
     * 禁用双击以及长按弹出 复制、全选菜单
     */
    private void noClick() {

        this.setLongClickable(false);
        this.setTextIsSelectable(false);

        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        //保证光标始终在最后面
        if (selStart == selEnd) {//防止不能多选
            try {
                setSelection(getText().length());
            } catch (NullPointerException e) {
                Log.e("XCashEditText", "getText() = null");
            }
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return true;
    }


    /**
     * 设置输入的监听(最大值默认为 Double.MAX_VALUE )
     */
    public void setCashInputListener(XCashInputListener listener) {
        setMaxCashAndInputListener(Double.MAX_VALUE, listener);
    }

    /**
     * 设置最大金额以及输入的监听
     */
    public void setMaxCashAndInputListener(double max, XCashInputListener listener) {

        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new XCashierInputFilter(max, listener);
        this.setFilters(inputFilter);
    }


}
