package industries.zk.friday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech myTTS;
    private SpeechRecognizer mySpeechRegonizer;

    TextView mytext;
    BottomAppBar bottomAppBar;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        fab = (FloatingActionButton) findViewById(R.id.floatActionBtn);
        mytext = (TextView) findViewById(R.id.textView);

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra( RecognizerIntent.EXTRA_MAX_RESULTS,1);
                mySpeechRegonizer.startListening(intent);
            }
        });
        setSupportActionBar(bottomAppBar);

        initializeTextToSpeech();
        initializeSpeechRecognizer();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)){
            mySpeechRegonizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRegonizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    mytext.setText("Listening....");
                    fab.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                }

                @Override
                public void onBeginningOfSpeech() {
                    mytext.setText("Listening....");
                    fab.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    mytext.setText("Buffer Recieved");
                }

                @Override
                public void onEndOfSpeech() {
                    mytext.setText("Tap On Mic");
                    fab.setImageResource(R.drawable.ic_mic_none_black_24dp);
                }

                @Override
                public void onError(int error) {
                    mytext.setText("Error Occured");
                }

                @Override
                public void onResults(Bundle bundle) {

                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    mytext.setText("Evetn");
                }
            });
        }
    }

    private void processResult(String command) {
        command = command.toLowerCase();

        if (command.indexOf("open") != -1) {
            if (command.indexOf("browser") != -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com"));
                startActivity(intent);
                speak("Opening Browser...");
            }
            if (command.indexOf("instagram") != -1){
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                startActivity(intent);
            }
            if (command.indexOf("whatsapp") != -1){
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                startActivity(intent);
            }

        }
        else if (command.indexOf("what") != -1) {
            if (command.indexOf("name") != -1) {
                speak("My Name is Friday");
            }
            if (command.indexOf("time") != -1) {
                Date now = new Date();
                String time = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_TIME);
                speak("The Time Now is " + time);
            }
            if (command.indexOf("date") != -1) {
                Date now = new Date();
                String date = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_YEAR);
                speak("The Date Today is " + date);
            }
        } else if (command.indexOf("about") != -1) {
            if (command.indexOf("you") != -1 || command.indexOf("your") != -1 || command.indexOf("yourself") != -1) {
                speak("I am Voice Assistant ,You can Call me Friday, and, i am developed by ZK ");
            }
        } else if (command.indexOf("search") != -1){
            String query = command.substring(6,command.length());
            String squery = null;
            try {
                squery = URLEncoder.encode(query,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q="+squery));
            startActivity(intent);
            speak("Taking You To Google...");
        }
    }

    private void initializeTextToSpeech() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size() == 0){
                    Toast.makeText(MainActivity.this,"There is no text to speech engine on this system",Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    myTTS.setLanguage(Locale.UK);
                    speak("Hello I'm Ready");
                }
            }
        });
    }

    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else{
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.appBar_about){
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

}
