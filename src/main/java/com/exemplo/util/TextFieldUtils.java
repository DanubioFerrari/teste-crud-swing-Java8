package com.exemplo.util;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.text.NumberFormat;

public class TextFieldUtils {

    public static JTextField createNumericTextField(int maxDigits) {
        JTextField textField = new JTextField();
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(maxDigits));
        return textField;
    }

    public static JFormattedTextField createFormattedNumericField(NumberFormat format, int maxDigits) {
        JFormattedTextField textField = new JFormattedTextField(format);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(maxDigits));
        return textField;
    }

    private static class NumericDocumentFilter extends DocumentFilter {
        private int maxDigits;

        public NumericDocumentFilter(int maxDigits) {
            this.maxDigits = maxDigits;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.matches("\\d*") && fb.getDocument().getLength() + text.length() - length <= maxDigits) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text.matches("\\d*") && fb.getDocument().getLength() + text.length() <= maxDigits) {
                super.insertString(fb, offset, text, attr);
            }
        }
    }
}
