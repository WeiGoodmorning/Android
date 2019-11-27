package com.example.ggmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ContentResolver mContentResolver;
    private ListView mPlaylist;
    private MediaCursorAdapter mediaCursorAdapter;
    private Cursor mCursor;

    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final String SELECTION = MediaStore.Audio.Media.IS_MUSIC + " = ?" + " AND " +
            MediaStore.Audio.Media.MIME_TYPE + " LIKE ? ";
    private final String[] SELECTION_ARGS = {
            Integer.toString(1),
            "audio/mpeg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlaylist = findViewById(R.id.lv_list);
        mContentResolver = getContentResolver();
        mediaCursorAdapter = new MediaCursorAdapter(MainActivity.this);
        mPlaylist.setAdapter(mediaCursorAdapter);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(
            MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else {
                requestPermissions(PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        }else {
            initPlaylist();
        }
    }

    public void initPlaylist(){
        mCursor = mContentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                SELECTION,
                SELECTION_ARGS,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        mediaCursorAdapter.swapCursor(mCursor);
        mediaCursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults){
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initPlaylist();
                }
                break;
            default:
                break;
        }
    }
}
