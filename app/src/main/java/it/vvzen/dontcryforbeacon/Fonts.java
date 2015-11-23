package it.vvzen.dontcryforbeacon;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by valerioviperino on 21/11/15.
 * Singleton pattern for fonts
 */
public class Fonts {

    private static Typeface EBGaramondRegular;
    private static Typeface EBGaramondSC08Regular;

    public static Typeface getEBGaramond12Regular(Context context){

        if(EBGaramondRegular == null){
            EBGaramondRegular = Typeface.createFromAsset(context.getAssets(), "fonts/EBGaramond12-Regular.ttf");
        }
        return EBGaramondRegular;
    }

    public static Typeface getEBGaramondSC08Regular(Context context){

        if(EBGaramondSC08Regular == null){
            EBGaramondSC08Regular = Typeface.createFromAsset(context.getAssets(), "fonts/EBGaramondSC08-Regular.ttf");
        }
        return EBGaramondSC08Regular;
    }

}
