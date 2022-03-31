package com.avinfo.avmusic.SongData;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a single audio file on the Android system.
 * <p>
 * It's a simple data container, filled with setters/getters.
 * <p>
 * Only mandatory fields are:
 * - id (which is a unique Android identified for a media file
 * anywhere on the system)
 * - filePath (full path for the file on the filesystem).
 */
public class Song implements Cloneable, Parcelable {

    private long id;
    private String filePath;
    private String title = "";
    private String artist = "";
    private String album = "";

    // optional metadata
    private int year = -1;
    private String genre = "";
    private int track_no = -1;
    private long duration_ms = -1;
    private String albumid = "";

    /**
     * Creates a new Song, with specified `songID` and `filePath`.
     *
     * @note It's a unique Android identifier for a media file
     * anywhere on the system.
     */
    public Song(long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    /**
     * Identifier for the song on the Android system.
     * (so we can locate the file anywhere)
     */
    public long getId() {
        return id;
    }

    /**
     * Full path for the music file within the filesystem.
     */
    public String getFilePath() {
        return filePath;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public int getTrackNumber() {
        return track_no;
    }

    public void setTrackNumber(int track_no) {
        this.track_no = track_no;
    }

    /**
     * Returns the duration of the song, in miliseconds.
     */
    public long getDuration() {
        return duration_ms;
    }

    /**
     * Sets the duration of the song, in miliseconds.
     */
    public void setDuration(long duration_ms) {
        this.duration_ms = duration_ms;
    }

    public long getDurationSeconds() {
        return getDuration() / 1000;
    }

    public long getDurationMinutes() {
        return getDurationSeconds() / 60;
    }

    @Override
    public Song clone() throws CloneNotSupportedException {
        return (Song) super.clone();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

