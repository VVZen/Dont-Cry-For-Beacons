package it.vvzen.dontcryforbeacon;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class State1BActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state1b);

        context = State1BActivity.this;

        // Assigning ui vars to xml
        TextView tv_greeterState1b = (TextView) this.findViewById(R.id.tv_greeter_state1b);
        TextView tv_state1bMessage = (TextView) this.findViewById(R.id.tv_state1b_message);

        // Setting typeface
        tv_greeterState1b.setTypeface(Fonts.getEBGaramondSC08Regular(context));
        tv_state1bMessage.setTypeface(Fonts.getEBGaramond12Regular(context));
    }
}
