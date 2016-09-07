package com.yuqf.fengmomusic.media;

import android.graphics.Bitmap;

public class Music {

    public int id;
    public String name;
    public String artist;
    private String artistId;
    private String album;
    private int albumId;
    private int rating;
    private int duration;
    private int playedPosition;
    private Bitmap cover;
    private boolean isLocal;
    private int bufferingProgress;
    private String coverUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPlayedPosition() {
        return playedPosition;
    }

    public void setPlayedPosition(int playedPosition) {
        this.playedPosition = playedPosition;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public int getBufferingProgress() {
        return bufferingProgress;
    }

    public void setBufferingProgress(int bufferingProgress) {
        this.bufferingProgress = bufferingProgress;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
