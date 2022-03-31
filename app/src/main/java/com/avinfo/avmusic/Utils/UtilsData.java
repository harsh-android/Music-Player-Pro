package com.avinfo.avmusic.Utils;

import com.avinfo.avmusic.SongData.Song;

import java.util.ArrayList;
import java.util.Collections;

public class UtilsData {


    public static String FB_Banner_AD = "YOUR_PLACEMENT_ID";
    public static String FB_Interstitial_AD = "YOUR_PLACEMENT_ID";
    public static String FB_Native_AD = "YOUR_PLACEMENT_ID";

//    public static String FB_Banner_AD = "2411013382288319_2411015805621410";
//    public static String FB_Interstitial_AD = "2411013382288319_2411017472287910";
//    public static String FB_Native_AD = "2411013382288319_2412715282118129";

    private static ArrayList<Song> songs = UtilsSong.getSongs();

    public static ArrayList<String> getArtists() {

        ArrayList<String> artists = new ArrayList<String>();

        for (Song song : songs) {
            String artist = song.getArtist();

            if ((artist != null) && (!artists.contains(artist)))
                artists.add(artist);
        }

        // Making them alphabetically sorted
        Collections.sort(artists);

        return artists;
    }

    public static ArrayList<String> getAlbums() {

        ArrayList<String> albums = new ArrayList<String>();

        for (Song song : songs) {
            String album = song.getAlbum();

            if ((album != null) && (!albums.contains(album)))
                albums.add(album);
        }

        // Making them alphabetically sorted
        Collections.sort(albums);

        return albums;
    }

    public static ArrayList<String> getGenres() {

        ArrayList<String> genres = new ArrayList<String>();

        for (Song song : songs) {
            String genre = song.getGenre();

            if ((genre != null) && (!genres.contains(genre)))
                genres.add(genre);
        }

        Collections.sort(genres);

        return genres;
    }

    public static ArrayList<String> getYears() {

        ArrayList<String> years = new ArrayList<String>();

        for (Song song : songs) {
            String year = Integer.toString(song.getYear());

            if ((Integer.parseInt(year) > 0) && (!years.contains(year)))
                years.add(year);
        }

        // Making them alphabetically sorted
        Collections.sort(years);

        return years;
    }
}
