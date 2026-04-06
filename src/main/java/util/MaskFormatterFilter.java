package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class MaskFormatterFilter extends DocumentFilter {
    private final String mask;

    public MaskFormatterFilter(String mask) {
        this.mask = mask;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;
        super.insertString(fb, offset, applyMask(fb.getDocument().getText(0, fb.getDocument().getLength()) + string), attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) return;
        
        String digits = text.replaceAll("[^\\d]", "");
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        StringBuilder newContent = new StringBuilder(currentText);
        newContent.replace(offset, offset + length, digits);
        
        String formatted = applyMask(newContent.toString().replaceAll("[^\\d]", ""));
        
        fb.remove(0, fb.getDocument().getLength());
        fb.insertString(0, formatted, attrs);
    }

    private String applyMask(String source) {
        StringBuilder result = new StringBuilder();
        int sourceIdx = 0;
        for (int i = 0; i < mask.length() && sourceIdx < source.length(); i++) {
            char m = mask.charAt(i);
            if (m == '#') {
                if (sourceIdx < source.length()) {
                    result.append(source.charAt(sourceIdx++));
                }
            } else {
                result.append(m);
            }
        }
        return result.toString();
    }
}