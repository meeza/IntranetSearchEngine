/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import static controllers.ServerController.installation_directory_path;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author developers7
 */
public class FtpFileDownloader extends Thread {

    private final String ftp_address;
    private final FTPClient ftpClient;
    private final ArrayList<String> all_files_on_ftp;

    public FtpFileDownloader(String ftp_addr, ArrayList<String> all_files) {
        this.ftp_address = ftp_addr;
        ftpClient = new FTPClient();
        all_files_on_ftp = all_files;
    }

    @Override
    public void run() {
        super.run();
        for (String file_name : all_files_on_ftp) {
            try {
                ftpClient.connect(ftp_address);
                FileOutputStream fos = new FileOutputStream(installation_directory_path + "\\downloads\\" + file_name);
                boolean download = ftpClient.retrieveFile(file_name, fos);
                if (download) {
                    System.out.println("file downliaded: "+file_name);
                    //
                }
            } catch (IOException ex) {
                System.out.println("can not download the file:  "+file_name);
                Logger.getLogger(FtpFileDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Indexer.all_files_downloaded_status = true;
    }

}
