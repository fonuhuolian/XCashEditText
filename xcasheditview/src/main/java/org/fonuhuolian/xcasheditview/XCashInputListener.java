package org.fonuhuolian.xcasheditview;

/**
 * 超过最大值的监听
 */
public interface XCashInputListener {
    /**
     * 输入的金额(正确时才回调)
     */
    void onInputCorrectCash(double cashValue);

    /**
     * 超出可输入的最大值
     */
    void onInputOverflow(double cashValue);
}
