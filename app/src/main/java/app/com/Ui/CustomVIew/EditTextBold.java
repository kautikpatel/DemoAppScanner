package app.com.Ui.CustomVIew;

import android.content.Context;
import android.util.AttributeSet;

public class EditTextBold extends android.support.v7.widget.AppCompatEditText {

    public EditTextBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EditTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextBold(Context context) {
        super(context);
        init();
    }

    public void init() {
        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), AppConstant.TYPE_ROBOTO_CONDENSE);
        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Animated.ttf");
        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Hungama_Android_Tv_Fonts.ttf");
        //setTypeface(tf ,1);
    }


}
