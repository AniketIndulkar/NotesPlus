package com.notes.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import java.util.Stack;

/**
 * Created by Aniket on 27-03-2018.
 */

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Context mContext;
    private Stack<Character> undoStack = new Stack<>();
    private boolean isBold = true;

    public CustomEditText(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void unDoText() {
        String text = getText().toString();
        if (text.length() > 0) {
            undoStack.push(text.charAt(text.length() - 1));
            text = text.substring(0, text.length() - 1);
            setText(text);
            setSelection(text.length());
        } else {
            Toast.makeText(mContext, "No text To unDO", Toast.LENGTH_SHORT).show();
        }
    }


    public void reDo() {
        String text = getText().toString();
        StringBuilder stringBuilder = new StringBuilder(text);
        if (!undoStack.empty()) {
            stringBuilder.append(undoStack.pop());
            setText(stringBuilder.toString());
            setSelection(stringBuilder.toString().length());
        } else {
            Toast.makeText(mContext, "Nothing to redo", Toast.LENGTH_SHORT).show();
        }
    }

}
