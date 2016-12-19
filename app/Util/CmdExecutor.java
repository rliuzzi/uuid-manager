package Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by rominaliuzzi on 16/12/2016.
 */
public class CmdExecutor {

    public static final String[] ADD_DEVICES_ZONES = {
            "/bin/sh",
            "-c",
            "cd $HOME/git/App-iOS-Controller/fastlane; fastlane add_devices deviceFile:$HOME/git/iOS-uuid/zone-uuids.txt"

    };

    public static void executeCommand(String[] command){
        try {
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

