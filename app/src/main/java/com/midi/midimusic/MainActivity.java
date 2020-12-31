package com.midi.midimusic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.midi.midilib.MidiFile;
import com.midi.midilib.MidiTrack;
import com.midi.midilib.event.NoteOff;
import com.midi.midilib.event.NoteOn;
import com.midi.midilib.event.meta.Tempo;
import com.midi.midilib.event.meta.TimeSignature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
        System.out.println("---++++");
        main();
//            }
//        });
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
    }


    public void main() {
        System.out.println("---++++");
        // 1. Create some MidiTracks
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();

        // 2. Add events to the tracks
        // 2a. Track 0 is typically the tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo t = new Tempo();
        t.setBpm(228);

        tempoTrack.insertEvent(ts);
//        tempoTrack.insertEvent(t);

        // 2b. Track 1 will have some notes in it
//        for (int i = 0; i < 80; i++) {
        int channel = 0, pitch = 1, velocity = 100;
        NoteOn on = new NoteOn(480, channel, pitch, velocity);
            NoteOff off = new NoteOff( 480 + 120, 2, pitch, 0);
//
        noteTrack.insertEvent(on);
//        noteTrack.insertEvent(on);
        noteTrack.insertEvent(off);
//
//            // There is also a utility function for notes that you should use
//            // instead of the above.
//            noteTrack.insertNote(channel, pitch + 2, velocity, 1 * 480, 120);
//        }

        // It's best not to manually insert EndOfTrack events; MidiTrack will
        // call closeTrack() on itself before writing itself to a file

        // 3. Create a MidiFile with the tracks we created
        ArrayList<com.midi.midilib.MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
//        tracks.add(noteTrack);
        tracks.add(noteTrack);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/test.mid");
//        System.out.println(file.getParent());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[1024];
            while (inputStream.read(data) > 0) {
                System.out.println(MidiUtil.bytesToHex(data));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
        // 4. Write the MIDI data to a file
        try {
            midi.writeToFile(file);
        } catch (IOException e) {
            System.err.println(e + "---");
        }
    }


}