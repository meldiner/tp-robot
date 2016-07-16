package meldiner.tprobot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    Button startButton, sendButton, clearButton, stopButton;
    TextView textView;
    EditText editText;

    SerialPortWrapper serialPort = new SerialPortWrapper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        setUiEnabled(true);
        serialPort.init();
    }

    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);
    }

    public void onClickStart(View view) {
        serialPort.start();
    }

    public void onClickSend(View view) {
        String string = editText.getText().toString();
        serialPort.send(string);
        tvAppend(textView, "\nData Sent : " + string + "\n");
    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView,"\nSerial Connection Closed! \n");

    }

    public void onClickClear(View view) {
        textView.setText(" ");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

}
