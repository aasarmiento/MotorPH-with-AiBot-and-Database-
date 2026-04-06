package util;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumericLimitFilter extends DocumentFilter {
    private final int maxLength;

    public NumericLimitFilter(int maxLength) {
        this.maxLength = maxLength;
    }



@Override
public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
        throws BadLocationException {
    
    if (!text.matches("\\d*")) {
        Toolkit.getDefaultToolkit().beep();
        return;
    }

    int currentLength = fb.getDocument().getLength();
    if ((currentLength - length + text.length()) <= maxLength) {
        super.replace(fb, offset, length, text, attrs);
    } else {
        Toolkit.getDefaultToolkit().beep();
    }
}
}