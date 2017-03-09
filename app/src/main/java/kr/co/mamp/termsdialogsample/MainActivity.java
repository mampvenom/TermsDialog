package kr.co.mamp.termsdialogsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kr.co.mamp.termsdialog.MampTermsDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MampTermsDialogFragment.Builder()
                        .setUrl("https://mampcorp.appspot.com/term.html?pk=com.mamp.quicklaunch.free")
                        .create().show(getSupportFragmentManager());
            }
        });
    }
}
