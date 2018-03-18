package beyond_imagination.midmia.pBackground;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import beyond_imagination.midmia.R;

/**
 * Created by cru65 on 2017-10-15.
 */

public class DangerAreaDialog extends Dialog {
    /*** Variable ***/
    Button button;

    /*** Function ***/
    public DangerAreaDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_danger);

        button = (Button) findViewById(R.id.btn_dialog_danger);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
