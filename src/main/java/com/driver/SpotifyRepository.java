package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap = new HashMap<>();
    public HashMap<Album, List<Song>> albumSongMap = new HashMap<>();
    public HashMap<Playlist, List<Song>> playlistSongMap = new HashMap<>();
    public HashMap<Playlist, List<User>> playlistListenerMap=new HashMap<>();
    public HashMap<User, Playlist> creatorPlaylistMap = new HashMap<>();
    public HashMap<User, List<Playlist>> userPlaylistMap = new HashMap<>();
    public HashMap<Song, List<User>> songLikeMap = new HashMap<>();

    public List<User> users = new ArrayList<>();
    public List<Song> songs = new ArrayList<>();
    public List<Playlist> playlists = new ArrayList<>();
    public List<Album> albums = new ArrayList<>();
    public List<Artist> artists = new ArrayList<>();

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        userPlaylistMap.put(user, new ArrayList<>());
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist, new ArrayList<>());
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        albums.add(album);
        for(Artist artist : artists){
            String name = artist.getName();
            if(name.equals(artistName)){
                List<Album> albumList = artistAlbumMap.getOrDefault(artistName, new ArrayList<>());
                albumList.add(album);
                artistAlbumMap.put(artist, albumList);
                albumSongMap.put(album, new ArrayList<>());
                return album;
            }
        }
        Artist artist = new Artist(artistName);
        artists.add(artist);
        List<Album> albumList = artistAlbumMap.getOrDefault(artistName, new ArrayList<>());
        albumList.add(album);
        artistAlbumMap.put(artist, albumList);
        albumSongMap.put(album, new ArrayList<>());
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title, length);
        songs.add(song);

        Album currAlbum = null;
        for (Album album : albums){
            if (album.getTitle().equals(albumName)){
                currAlbum=album;
                break;
            }
        }
        if (currAlbum==null){
            throw new Exception("Album does not exist");
        }
        List<Song> songList = albumSongMap.getOrDefault(currAlbum, new ArrayList<>());
        songList.add(song);
        albumSongMap.put(currAlbum, songList);

        songLikeMap.put(song, new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for (User user1 : users){
            if(user1.getMobile().equals(mobile)){
                user = user1;
                break;
            }
        }
        if (user == null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, new ArrayList<>());

        for (Song song : songs){
            if (song.getLength()==length){
                playlistSongMap.get(playlist).add(song);
            }
        }
        userPlaylistMap.get(user).add(playlist);
        creatorPlaylistMap.put(user, playlist);

        List<User> userList = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist, userList);
        userList.add(user);
        playlistListenerMap.put(playlist, userList);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for (User user1 : users){
            if (user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, new ArrayList<>());
        for (String songTitle : songTitles){
            for (Song song : songs){
                if(song.getTitle().equals(songTitle)){
                    playlistSongMap.get(playlist).add(song);
                }
            }
        }
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.get(user).add(playlist);

        playlistListenerMap.put(playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(user);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User currUser = null;
        for (User user : users){
            if (user.getMobile().equals(mobile)){
                currUser=user;
            }
        }
        if (currUser==null){
            throw new Exception("User does not exist");
        }

        Playlist playlist = null;
        for (Playlist playlist1 : playlists){
            if (playlist1.getTitle().equals(playlistTitle)){
                playlist=playlist1;
                break;
            }
        }
        if (playlist==null){
            throw new Exception("Playlist does not exist");
        }
        if (creatorPlaylistMap.containsKey(currUser)){
            return playlist;
        }
        userPlaylistMap.get(currUser).add(playlist);
        if (playlistListenerMap.containsKey(playlist)){
            if (!playlistListenerMap.get(playlist).contains(currUser)){
                playlistListenerMap.get(playlist).add(currUser);
            }
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User currUser = null;
        for (User user : users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
            }
        }
        if (currUser == null){
            throw new Exception("User does not exist");
        }
        Song currSong = null;
        for (Song song : songs){
            if (song.getTitle().equals(songTitle)){
                currSong=song;
                break;
            }
        }
        if (currSong==null){
            throw new Exception("Song does not exist");
        }
        if(songLikeMap.get(currSong).contains(currUser)){
            return currSong;
        }
        songLikeMap.get(currSong).add(currUser);
        currSong.setLikes(currSong.getLikes()+1);

        Album currAlbum = null;
        for (Album album : albumSongMap.keySet()){
            if(albumSongMap.get(album).contains(currSong)){
                currAlbum=album;
            }
        }
        Artist currArtist = null;
        for (Artist artist: artistAlbumMap.keySet()){
            if (artistAlbumMap.get(artist).contains(currAlbum)){
                currArtist = artist;
            }
        }
        if (currArtist==null){
            throw new Exception("");
        }
        currArtist.setLikes(currArtist.getLikes()+1);
        return currSong;
    }

    public String mostPopularArtist() {
        Artist currArtist = null;
        int maxLikes = 0;

        for (Artist artist : artists){
            if (artist.getLikes()>=maxLikes){
                currArtist = artist;
                maxLikes = artist.getLikes();
            }
        }
        if (currArtist==null){
            return "";
        }
        return currArtist.getName();
    }

    public String mostPopularSong() {
        Song currSong = null;
        int maxLikes = 0;

        for (Song song : songs){
            if (song.getLikes()>=maxLikes){
                currSong = song;
                maxLikes = song.getLikes();
            }
        }
        if (currSong==null){
            return "";
        }
        return currSong.getTitle();
    }
}