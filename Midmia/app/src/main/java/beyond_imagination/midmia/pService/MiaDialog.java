package beyond_imagination.midmia.pService;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import beyond_imagination.midmia.pMain.Child;
import beyond_imagination.midmia.R;
import beyond_imagination.midmia.pService.Server;

/**
 * Created by laggu on 2017-07-17.
 */

public class MiaDialog extends Dialog {
    ImageView imageView;
    TextView textView;
    Button noButton, yesButton;
    Child child;
    boolean lost;

    public MiaDialog(@NonNull Context context, Child child) {
        super(context);
        this.child = child;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mia);

        imageView = (ImageView) findViewById(R.id.imageView_miaDialog);
        textView = (TextView)findViewById(R.id.textView_miaDialog);
        noButton = (Button) findViewById(R.id.noButton_miaDialog);
        yesButton = (Button) findViewById(R.id.yesButton_miaDialog);

        //imageView.setImageBitmap(child.getPhoto());

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Server server = new Server();
                    server.uploadData(2.0,1.0);
                }
                catch (Exception e){

                }

                lost = true;
                dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(Child child) {
        this.child = child;
        this.show();
    }

    public boolean isLost(){
        return lost;
    }
}
