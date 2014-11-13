package com.example.shuttapp;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
//import android.content.Intent;

import java.io.IOException;
import java.util.Formatter;

public class CapturaAudio extends ActionBarActivity {

	private MediaRecorder mGrabacion=null;
	private TextView tDecibel=null;
	private boolean listening;
	
	private void startGrabacion() {
		mGrabacion = new MediaRecorder();
		mGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
		mGrabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mGrabacion.setOutputFile("/dev/null");
        mGrabacion.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
        	mGrabacion.prepare();
        } catch (IOException e) {
            Log.e("pruebaAudio", "prepare() failed");
        }

        mGrabacion.start();
    }
	
	private void stopGrabacion() {
		mGrabacion.stop();
		mGrabacion.release();
		mGrabacion = null;
    }
	
	private void Grabando(boolean inicio) {
        if (inicio) {
            startGrabacion();
        } else {
            stopGrabacion();
        }
    }
	
	public double getAmplitude() {
	    if (mGrabacion != null)
	        return (mGrabacion.getMaxAmplitude());
	    else
	        return 0;
	}
	
	
	private Button bGrabar;
	boolean mStartG = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_captura_audio);
		
		tDecibel=(TextView) findViewById(R.id.tDecibel);
		bGrabar=(Button)findViewById(R.id.bGrabar);
		bGrabar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Grabando(mStartG);
                mStartG = !mStartG;
                
			}
		});
		
		//tDecibel.setText("Cambiando " + amplitude);
	}
	
	 @Override
	    protected void onResume() {
	        listening = true;
	        new Escucha().execute();
	        super.onResume();
	    }
	    
	    @Override
	    protected void onPause() {
	        listening = false;
	        super.onPause();
	    }
	
	
	public class Escucha extends AsyncTask <Void, Double, Void> {
		 
        protected void onPreExecute() {
            startGrabacion();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
 
            while(listening) {
                SystemClock.sleep(300); // Si es menor casi siempre da 0
                Double amplitude = 20 * Math.log10(getAmplitude() / 32768.0);
                publishProgress(amplitude);
                
            }
 
            return null;
        }
 
        @Override
        protected void onProgressUpdate(Double... values) {
            Double value = values[0];
 
            if (value < -80) {
                value = new Double(-80);
            } else if (value > 0) {
                value = new Double(0);
            }
 
            String db = new Formatter().format("%03.1f",value).toString();
            
            tDecibel.setText(db + " dB");
            
            if(db.equalsIgnoreCase("0.0" ) ){
            	
            	Intent alert = new Intent(CapturaAudio.this, Alerta.class);
				startActivity(alert);
            }
 
           
 
        }
 
        @Override
        protected void onPostExecute(Void result) {
            stopGrabacion();
        }
 
}
 
    
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.captura_audio, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
