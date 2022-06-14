package com.example.activitytest.Activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.activitytest.R;
import com.example.activitytest.Util.BaseActivity;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/** 计算器 */
public class CalActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CalActivity";

    /* 未匹配的右括号的数量 */
    private int openParenthesis = 0;

    /* 为true时连续计算最后一次运算，为false则是第一次计算等于，按下等号置为true，按下其他置为false */
    private boolean equalClicked = false;
    /* 上个算术表达式的最后一个算术子项
    举例：上个计算结束的算术表达式为"1+2+3+4=10"，则lastExpression = "+4" */
    private String lastExpression;

    private final static int IS_NUMBER = 0; // 数字
    private final static int IS_OPERAND = 1; // +-*/
    private final static int IS_OPEN_PARENTHESIS = 2; // 左括号
    private final static int IS_CLOSE_PARENTHESIS = 3; // 右括号
    private final static int IS_DOT = 4; // 小数点
    private final static int IS_PERCENT = 5; // %

    private Button buttonNumber0;
    private Button buttonNumber1;
    private Button buttonNumber2;
    private Button buttonNumber3;
    private Button buttonNumber4;
    private Button buttonNumber5;
    private Button buttonNumber6;
    private Button buttonNumber7;
    private Button buttonNumber8;
    private Button buttonNumber9;

    private Button buttonClear;
    private Button buttonParentheses;
    private Button buttonPercent;
    private Button buttonDivision;
    private Button buttonMultiplication;
    private Button buttonSubtraction;
    private Button buttonAddition;
    private Button buttonEqual;
    private Button buttonDot;
    private Button buttonDel;

    private TextView textViewInputNumbers;
    private TextView textViewLogcat;

    private ScriptEngine scriptEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_layout);

        /* Toolbar 实例化 */
        newInstance_Toolbar("简易计算");

        /* ScriptEngine用于计算结果 */
        scriptEngine = new ScriptEngineManager().getEngineByName("rhino");

        initializeViewVariables();
        setOnClickListeners();
    }

    private void initializeViewVariables() {
        buttonNumber0 = findViewById(R.id.button_zero);
        buttonNumber1 = findViewById(R.id.button_one);
        buttonNumber2 = findViewById(R.id.button_two);
        buttonNumber3 = findViewById(R.id.button_three);
        buttonNumber4 = findViewById(R.id.button_four);
        buttonNumber5 = findViewById(R.id.button_five);
        buttonNumber6 = findViewById(R.id.button_six);
        buttonNumber7 = findViewById(R.id.button_seven);
        buttonNumber8 = findViewById(R.id.button_eight);
        buttonNumber9 = findViewById(R.id.button_nine);

        buttonClear = findViewById(R.id.button_clear);
        buttonParentheses = findViewById(R.id.button_parentheses);
        buttonPercent = findViewById(R.id.button_percent);
        buttonDivision = findViewById(R.id.button_division);
        buttonMultiplication = findViewById(R.id.button_multiplication);
        buttonSubtraction = findViewById(R.id.button_subtraction);
        buttonAddition = findViewById(R.id.button_addition);
        buttonEqual = findViewById(R.id.button_equal);
        buttonDot = findViewById(R.id.button_dot);
        buttonDel = findViewById(R.id.button_delete);

        textViewInputNumbers = findViewById(R.id.textView_input_numbers);
        textViewLogcat = findViewById(R.id.textView_logcat);
    }

    private void setOnClickListeners() {
        buttonNumber0.setOnClickListener(this);
        buttonNumber1.setOnClickListener(this);
        buttonNumber2.setOnClickListener(this);
        buttonNumber3.setOnClickListener(this);
        buttonNumber4.setOnClickListener(this);
        buttonNumber5.setOnClickListener(this);
        buttonNumber6.setOnClickListener(this);
        buttonNumber7.setOnClickListener(this);
        buttonNumber8.setOnClickListener(this);
        buttonNumber9.setOnClickListener(this);

        buttonClear.setOnClickListener(this);
        buttonParentheses.setOnClickListener(this);
        buttonPercent.setOnClickListener(this);
        buttonDivision.setOnClickListener(this);
        buttonMultiplication.setOnClickListener(this);
        buttonSubtraction.setOnClickListener(this);
        buttonAddition.setOnClickListener(this);
        buttonEqual.setOnClickListener(this);
        buttonDot.setOnClickListener(this);
        buttonDel.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        String sTmp = textViewInputNumbers.getText().toString();
        switch (view.getId())
        {
            case R.id.button_zero:
                addNumber("0");
                equalClicked = false;
                break;
            case R.id.button_one:
                addNumber("1");
                equalClicked = false;
                break;
            case R.id.button_two:
                addNumber("2");
                equalClicked = false;
                break;
            case R.id.button_three:
                addNumber("3");
                equalClicked = false;
                break;
            case R.id.button_four:
                addNumber("4");
                equalClicked = false;
                break;
            case R.id.button_five:
                addNumber("5");
                equalClicked = false;
                break;
            case R.id.button_six:
                addNumber("6");
                equalClicked = false;
                break;
            case R.id.button_seven:
                addNumber("7");
                equalClicked = false;
                break;
            case R.id.button_eight:
                addNumber("8");
                equalClicked = false;
                break;
            case R.id.button_nine:
                addNumber("9");
                equalClicked = false;
                break;
            case R.id.button_addition:
                addOperand("+");
                equalClicked = false;
                break;
            case R.id.button_subtraction:
                addOperand("-");
                equalClicked = false;
                break;
            case R.id.button_multiplication:
                addOperand("x");
                equalClicked = false;
                break;
            case R.id.button_division:
                addOperand("\u00F7");
                equalClicked = false;
                break;
            case R.id.button_percent:
                addOperand("%");
                equalClicked = false;
                break;
            case R.id.button_dot:
                addDot();
                equalClicked = false;
                break;
            case R.id.button_parentheses: // 括号
                addParenthesis();
                equalClicked = false;
                break;
            case R.id.button_clear:
                textViewInputNumbers.setText("");
                openParenthesis = 0;
                equalClicked = false;
                break;
            case R.id.button_equal:
                if (!sTmp.equals("")) {
                    calculate(sTmp);
                }
                break;
            case R.id.button_delete:
                if (!sTmp.equals("")){
                    addDel();
                }
                break;
        }
    }

    /** 输入DEL */
    private void addDel() {
        String sTmp = textViewInputNumbers.getText().toString(); // 已有内容
        int sLen = sTmp.length();
        String lastInput = sTmp.substring(sLen - 1);

        textViewInputNumbers.setText(sTmp.substring(0, sTmp.length()-1)); // 删除末尾字符

        switch (defineLastCharacter(lastInput)) {
            case IS_OPEN_PARENTHESIS: // 删的是左括号
                openParenthesis--;
                break;
            case IS_CLOSE_PARENTHESIS: // 删的是右括号
                openParenthesis++;
                break;
        }
    }

    /** 输入小数点 */
    private void addDot() {
        String sTmp = textViewInputNumbers.getText().toString(); // 已有内容
        Log.e(TAG, "addOperand sTmp: " + sTmp);
        int sLen = sTmp.length();

        if (sLen == 0) { // 文本框内无内容
            textViewInputNumbers.setText("0.");
        }
        else {
            String lastInput = sTmp.substring(sLen - 1);
            switch (defineLastCharacter(lastInput)) {
                case IS_NUMBER:
                    if (DotUsed(sTmp)) {
                        showToast("已输入小数点");
                    }
                    else {
                        sTmp += ".";
                    }
                    break;
                case IS_OPERAND: // 运算符
                case IS_OPEN_PARENTHESIS: // 左括号
                    sTmp = sTmp + "0.";
                    break;
                case IS_CLOSE_PARENTHESIS:
                    sTmp = sTmp + "x0.";
                    break;
                case IS_PERCENT:
                    showToast("百分号%后不可直接跟小数点");
                    break;
            }
            textViewInputNumbers.setText(sTmp);
        }
    }

    /** 输入括号 */
    private void addParenthesis() {
        String sTmp = textViewInputNumbers.getText().toString(); // 已有内容
        int sLen = sTmp.length();

        if (sLen == 0) {
            sTmp += "(";
            openParenthesis++;
        }
        else { // sLen > 0 有已输入内容
            String lastInput = sTmp.substring(sLen -1);
            int flag = defineLastCharacter(lastInput); // 尾字符类型

            if (openParenthesis > 0) { // 有左括号未匹配
                switch (flag)
                {
                    case IS_NUMBER:
                    case IS_PERCENT:
                    case IS_CLOSE_PARENTHESIS:
                        sTmp += ")";
                        openParenthesis--;
                        break;
                    case IS_OPERAND:
                    case IS_OPEN_PARENTHESIS:
                        sTmp += "(";
                        openParenthesis++;
                        break;
                    case IS_DOT:
                        sTmp = sTmp.substring(0, sLen-1);
                        sTmp += ")";
                        openParenthesis--;
                        break;
                }
            }
            else { // openParenthesis == 0 没有未匹配括号
                if (flag == IS_OPERAND) { // 符号
                    sTmp += "(";
                }
                else {
                    sTmp += "x(";
                }
                openParenthesis++;
            }
        }
        textViewInputNumbers.setText(sTmp);
    }

    /** 输入符号 */
    private void addOperand(String operand) {
        String sTmp = textViewInputNumbers.getText().toString(); // 已有内容
        Log.e(TAG, "addOperand sTmp: " + sTmp);

        if (!sTmp.isEmpty()) { // 除本次输入外文本框中仍有其他输入内容
            int sLen = sTmp.length(); // 内容长度
            // 文本框内容的最后一位
            String lastInput = sTmp.substring(sLen -1);
            Log.e(TAG, "addOperand lastInput: " + lastInput);

            switch (defineLastCharacter(lastInput)) {
                case IS_NUMBER: // 数字
                case IS_CLOSE_PARENTHESIS: // 右括号
                    sTmp += operand;
                    break;
                case IS_OPERAND: // +-*/
                    if (lastInput.equals("-")) { // 最后一位是 -
                        if ((sLen > 1)) { // 算式长度大于2
                            int flag = defineLastCharacter(sTmp.charAt(sLen-2) + "");
                            if(flag == IS_NUMBER || flag == IS_PERCENT) { // -当减号用，可以替换
                                sTmp = sTmp.substring(0, sLen -1) + operand;
                            }
                            else { // 当负号用，-变为0
                                sTmp = sTmp.substring(0, sLen -1) + "0" + operand;
                            }
                        }
                    }
                    else { // 最后一位是+、*、/
                        if (operand.equals("-")) { // 输入符号是 - 则视为负数，如：2+输入- 变为 2+(-
                            sTmp = sTmp + "(" + operand;
                            openParenthesis++;

                        }
                        else { // 输入符号是 +*/ 则替换原有符号，如：3*输入+ 变为 3+
                            sTmp = sTmp.substring(0, sLen -1) + operand;
                        }
                    }
                    Log.e(TAG, "addOperand sTmp change: " + sTmp);
                    break;
                case IS_OPEN_PARENTHESIS: // 左括号
                    if (operand.equals("-")) {
                        sTmp += operand;
                    }
                    else {
                        showToast("左括号后不可直接跟运算符");
                    }
                    break;
                case IS_DOT: // 小数点
                    sTmp = sTmp.substring(0, sLen -1) + operand;
                    break;
                case IS_PERCENT: // 文本框内容的最后一位是 %
                    if (!operand.equals("%")) { // 本次输入不是%
                        sTmp += operand;
                    }
                    else {
                        showToast("%后不应继续跟%");
                    }
                    break;
            }
        }
        else { // 输入框为空
            if (operand.equals("-")) {
                sTmp = operand;
            }
            else if (operand.equals("%")) {
                sTmp = "0";
            }
            else {
                sTmp = "0" + operand;
            }
        }
        textViewInputNumbers.setText(sTmp);
    }

    /** 输入数字 */
    private void addNumber(String number) {
        String sTmp = textViewInputNumbers.getText().toString(); // 已有内容
        Log.e(TAG, "addNumber 已有内容: " + sTmp);
        int sLen = sTmp.length();

        if (sLen > 0) {
            String lastInput = sTmp.substring(sLen - 1);
            int flag = defineLastCharacter(lastInput); // 最后一个字符的类型

            switch (flag) {
                case IS_NUMBER:
                    if (lastInput.equals("0")) { // 末尾字符是0
                        if (sLen == 1) {
                            sTmp = number; // 更改为本次输入的数字
                        }
                        else if (!DotUsed(sTmp)) { // 最后一个运算数没有使用小数点，是整数
                            String penultimate = sTmp.substring(sLen - 2, sLen - 1);
                            int flagPen = defineLastCharacter(penultimate);
                            if (flagPen == IS_NUMBER ||flagPen == IS_DOT){
                                sTmp += number;
                            }
                            else {
                                showToast("整数不应以0开头");
                            }
                        }
                        else { // 最后一个运算数用了小数点，是小数
                            sTmp += number;
                        }
                    }
                    else { // 尾字符为 数字 或 符号 或 左括号 或 小数点
                        sTmp += number;
                    }
                    break;
                case IS_OPERAND:
                case IS_OPEN_PARENTHESIS:
                case IS_DOT:
                    sTmp += number;
                    break;
                case IS_CLOSE_PARENTHESIS:
                case IS_PERCENT:
                    // 尾字符为右括号 或 百分号
                    sTmp = sTmp + "x" + number;
                    break;
            }
        }
        else { // 文本框为空
            sTmp += number;
        }
        textViewInputNumbers.setText(sTmp);
    }


    /** 计算结果 */
    private void calculate(String input) {
        String result;
        // String temp = input;
        try {
            if (equalClicked) {
                // temp = input + lastExpression;
                input += lastExpression;
                Log.e(TAG, "calculate: equalClicked is true");
            }
            else {
                saveLastExpression(input);
                Log.e(TAG, "calculate: equalClicked is false");
            }

            Log.e(TAG, "计算结果前： " + input);
            result = scriptEngine.eval(
                    // temp.replaceAll("%", "/100")
                    input.replace("%", "/100")
                        .replaceAll("x", "*")
                        .replaceAll("[^\\x00-\\x7F]", "/"))
                        .toString();
            equalClicked = true;
            Log.e(TAG, "计算结果后： " + result);

        }
        catch (Exception e) {
            showToast("格式错误，请检查输入的表达式！");
            return;
        }

        if (result.equals("Infinity")) {
            showToast("不允许零做除数");
            textViewInputNumbers.setText(input);
        }
        else if (result.contains(".")) { // 结果包含小数点
            // 由于eval计算结果即使是整数也会以 整数.0 的形式返回，因此需要将末尾.0抹去
            result = result.replaceAll("\\.?0*$", "");
            /* \\.?0*$ 正则表达式，\\将.转义为字符，即 .?0*$
            * .? 意为：.出现一次或者零次
            * 0*$ 意为：以任意0结尾
            * 举两个例子，也就是：1.000（.出现一次，并以任意0结尾） 和 1.1000(.不出现，仅以任意0结尾)
            * 都会被抹去末尾0
            * 但实际上 scriptEngine.eval 会自动优化上面两种情况，只会出现 1.0 这种情况 */
            Log.e(TAG, "calculate result 1 : " + result);
            textViewInputNumbers.setText(result);
            result = input + "\n=" + result;
            Log.e(TAG, "calculate result 2 : " + result);
            textViewLogcat.setText(result);
        }
    }

    /** 保存最后一次运算表达式
     * 作用是，如 首次输入 7+8，则记录 lastExpression = "+8"
     * 下次直接点击 等于号时 直接在上次计算结果 15 上 +8
     * 即得到 23 */
    private void saveLastExpression(@NonNull String input) {
        int sLen = input.length();
        String lastOfExpression = input.substring(sLen - 1);

        if (sLen > 1) {
            if (lastOfExpression.equals(")")) { // 表达式以)结尾
                lastExpression = ")";
                int numberOfCloseParenthesis = 1;

                /* 从倒数第二个字符开始检索有几个右括号未匹配 */
                for (int i = sLen - 2; i >= 0; i--) {
                    String last = input.charAt(i) + "";
                    if (numberOfCloseParenthesis > 0) { // 尚有右括号未匹配
                        if (last.equals(")")) { // 第i个字符是右括号
                            numberOfCloseParenthesis++;
                        }
                        else if (last.equals("(")) { // 第i个字符是左括号
                            numberOfCloseParenthesis--;
                        }
                        lastExpression = String.format("%s%s", last, lastExpression);
                        // 将last加入最后一次运算表达式
                    }
                    else if (defineLastCharacter(last) == IS_OPERAND) {
                        // 没有未匹配的括号 且 当前i字符是运算符
                        lastExpression = last + lastExpression;
                        break;
                    }
                    else {
                        lastExpression = "";
                    }
                }
            } else if (defineLastCharacter(lastOfExpression + "") == IS_NUMBER) {
                // 表达式以数字结尾
                lastExpression = lastOfExpression;
                for (int i = sLen - 2; i >= 0; i--) {
                    String last = input.charAt(i) + "";
                    int flag = defineLastCharacter(last);
                    if (flag == IS_NUMBER || flag == IS_DOT) {
                        lastExpression = String.format("%s%s", last, lastExpression);
                    }
                    else if (flag == IS_OPERAND) {
                        // 当前i字符是运算符
                        lastExpression = last + lastExpression;
                        break;
                    }
                    if (i == 0) {
                        lastExpression = "";
                    }
                }
            }
            Log.e(TAG, "saveLastExpression sLen > 1 lastExpression: " + lastExpression);
        }
        else {
            lastExpression = "";
            Log.e(TAG, "saveLastExpression sLen <= 1 lastExpression: " + lastExpression);
        }
    }

    /** 判断 lastCharacter 的类型 */
    private int defineLastCharacter(String lastCharacter) {
        try {
            Integer.parseInt(lastCharacter);
            return IS_NUMBER;
        } catch (NumberFormatException ignored) {
            Log.e(TAG, "defineLastCharacter Integer.parseInt error");
        }

        if ((lastCharacter.equals("+") || lastCharacter.equals("-") || lastCharacter.equals("x")
                || lastCharacter.equals("\u00F7")))
            return IS_OPERAND;

        if (lastCharacter.equals("("))
            return IS_OPEN_PARENTHESIS;

        if (lastCharacter.equals(")"))
            return IS_CLOSE_PARENTHESIS;

        if (lastCharacter.equals("."))
            return IS_DOT;

        if (lastCharacter.equals("%"))
            return IS_PERCENT;

        return -1;
    }

    /** 查看最后一个运算数是否有使用小数点 */
    private boolean DotUsed(String sTmp){
        for(int i = sTmp.length()-1; i >= 0; i--) {
            char c = sTmp.charAt(i);
            if(c >= '0' && c <= '9') { // 等于数字
                Log.e(TAG, "DotUsed sTmp.charAt: " + c);
            }
            else if (c == '.') { // 有小数点
                return true;
            }
            else { // 如果是+-*/%()说明遍历完了当前运算数
                break;
            }
        }
        return false;
    }

}