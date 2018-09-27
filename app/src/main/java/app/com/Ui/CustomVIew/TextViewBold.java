package app.com.Ui.CustomVIew;

import android.content.Context;
import android.util.AttributeSet;


public class TextViewBold extends android.support.v7.widget.AppCompatTextView {

    public TextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewBold(Context context) {
        super(context);
        init();
    }

    public void init() {
        /*Typeface tf = Typeface.createFromAsset(getContext().getAssets(), AppConstant.TYPE_ROBOTO_CONDENSE_BOLD);
        setTypeface(tf ,1);*/
    }


}
