package com.ai.deepbrain.dexterai;

/**
 * Created by Debabrata on 6/11/2017.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import android.widget.Toast;

public class ApiaiActivity extends AppCompatActivity implements AIListener {

    private ImageButton listenButton;
    private TextView resultTextView;
    private AIService aiService;
    TextToSpeech t1;
    Button b1;

    public interface AIListener {
        void onResult(AIResponse result); // here process response
        void onError(AIError error); // here process error
        void onAudioLevel(float level); // callback for sound level visualization
        void onListeningStarted(); // indicate start listening here
        void onListeningCanceled(); // indicate stop listening here
        void onListeningFinished(); // indicate stop listening here
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apiai);
        listenButton = (ImageButton) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        b1=(Button)findViewById(R.id.b1);
        final AIConfiguration config = new AIConfiguration("2bf53fdfbd8a4a41b8ab90ab450b2073",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);


    }

    public void listenButtonOnClick(final View view) {
        aiService.startListening();
    }

    public void onResult(final AIResponse response) {
        final Result result = response.getResult();
        final String toSpeak;
        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
        resultTextView.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + parameterString+
                "\nResponse: " + result.getFulfillment().getSpeech());
        ;
        toSpeak = result.getFulfillment().getSpeech();
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }



    @Override
    public void onError(final AIError error) {
        resultTextView.setText(error.toString());
    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}
}
