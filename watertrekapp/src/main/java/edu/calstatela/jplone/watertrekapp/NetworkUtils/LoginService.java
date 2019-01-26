package edu.calstatela.jplone.watertrekapp.NetworkUtils;

public class LoginService {
    public static void checkLoginStatus(NetworkTask.NetworkCallback callback){
        String url = "https://watertrek.jpl.nasa.gov/hydrology";
        NetworkTask nt = new NetworkTask(callback, 0);
        nt.execute(url);
    }
}
