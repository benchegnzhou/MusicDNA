package com.sdsmdg.harjot.MusicDNA;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditLocalSongFragment extends Fragment {

    EditText titleText, artistText, albumText;
    ImageView songImage, backImage;
    Button saveButton;

    Context ctx;

    boolean isTitleNotNull = false;
    boolean isArtistNotNull = false;
    boolean isAlbumNotNull = false;

    MP3File mp3File;

    onEditSongSaveListener mCallback;
    newCoverListener mCallback2;

    public interface onEditSongSaveListener {
        public void onEditSongSave(boolean wasSaveSuccessful);
    }

    public interface newCoverListener {
        public void getNewBitmap();
    }

    public EditLocalSongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
        try {
            mCallback = (onEditSongSaveListener) context;
            mCallback2 = (newCoverListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_local_song, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleText = (EditText) view.findViewById(R.id.edit_song_title);
//        titleText.setText(HomeActivity.editSong.getTitle());
        artistText = (EditText) view.findViewById(R.id.edit_song_artist);
//        artistText.setText(HomeActivity.editSong.getArtist());
        albumText = (EditText) view.findViewById(R.id.edit_song_album);
//        albumText.setText(HomeActivity.editSong.getAlbum());

        songImage = (ImageView) view.findViewById(R.id.edit_song_image);
        backImage = (ImageView) view.findViewById(R.id.back_image);

        Bitmap bmp = null;
        try {
            bmp = getBitmap(HomeActivity.editSong.getPath());
        } catch (Exception e) {

        }

        if (bmp != null) {
            songImage.setImageBitmap(bmp);
            backImage.setImageBitmap(bmp);
        } else {
            songImage.setImageResource(R.drawable.ic_default);
            backImage.setImageResource(R.drawable.ic_default);
        }

        songImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback2.getNewBitmap();
            }
        });

        saveButton = (Button) view.findViewById(R.id.edit_song_save_button);
        saveButton.setBackgroundColor(HomeActivity.themeColor);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!titleText.getText().toString().trim().equals("")) {
                    HomeActivity.editSong.setTitle(titleText.getText().toString().trim());
                    isTitleNotNull = true;
                } else {
                    titleText.setError("Enter a valid Title");
                    isTitleNotNull = false;
                }
                if (!artistText.getText().toString().trim().equals("")) {
                    HomeActivity.editSong.setTitle(artistText.getText().toString().trim());
                    isArtistNotNull = true;
                } else {
                    artistText.setError("Enter a valid Artist name");
                    isArtistNotNull = false;
                }
                if (!albumText.getText().toString().trim().equals("")) {
                    HomeActivity.editSong.setTitle(albumText.getText().toString().trim());
                    isAlbumNotNull = true;
                } else {
                    albumText.setError("Enter a valid Album name");
                    isAlbumNotNull = false;
                }
                if (isTitleNotNull && isArtistNotNull && isAlbumNotNull) {

                    Tag tag = mp3File.getTag();
                    ID3v1Tag id3v1Tag = mp3File.getID3v1Tag();
                    AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
                    ID3v24Tag id3v24Tag = mp3File.getID3v2TagAsv24();

//                    id3v2Tag.setTitle(titleText.getText().toString());
//                    id3v2Tag.setArtist(artistText.getText().toString());
//                    id3v2Tag.setAlbum(albumText.getText().toString());

                    boolean error = false;

                    try {
                        if (tag != null) {
                            tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        if (id3v1Tag != null) {
                            id3v1Tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            id3v1Tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            id3v1Tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        if (id3v2Tag != null) {
                            id3v2Tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            id3v2Tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            id3v2Tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        if (id3v24Tag != null) {
                            id3v24Tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            id3v24Tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            id3v24Tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        mp3File.commit();
                    } catch (CannotWriteException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    if (!error) {
                        Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
                        HomeActivity.editSong.setTitle(titleText.getText().toString());
                        HomeActivity.editSong.setArtist(artistText.getText().toString());
                        HomeActivity.editSong.setAlbum(albumText.getText().toString());
                    }

                    mCallback.onEditSongSave(!error);

                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        mp3File = null;

        try {
            File f = new File(HomeActivity.editSong.getPath());
            mp3File = (MP3File) AudioFileIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        }

        if (mp3File == null) {
            Toast.makeText(ctx, "Error in loading the file", Toast.LENGTH_SHORT).show();
            mCallback.onEditSongSave(false);
        }
        if (!mp3File.hasID3v2Tag()) {
            Toast.makeText(ctx, "No Tags Found", Toast.LENGTH_SHORT).show();
            mCallback.onEditSongSave(false);
        }

        if (mp3File != null && mp3File.hasID3v2Tag()) {
            Tag tag = mp3File.getTag();
//            titleText.setText(tag.getFirst(FieldKey.TITLE));
//            artistText.setText(tag.getFirst(FieldKey.ARTIST));
//            albumText.setText(tag.getFirst(FieldKey.ALBUM));
            titleText.setText(HomeActivity.editSong.getTitle());
            artistText.setText(HomeActivity.editSong.getArtist());
            albumText.setText(HomeActivity.editSong.getAlbum());

        }
    }

    public Bitmap getBitmap(String url) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url);
        Bitmap bitmap = null;

        byte[] data = mmr.getEmbeddedPicture();

        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }

    public void updateCoverArt(Bitmap bmp) {
        if (bmp != null) {
            songImage.setImageBitmap(bmp);
            backImage.setImageBitmap(bmp);
        }
    }

}
