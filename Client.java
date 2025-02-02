import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;

public class Client {

    private static FileServer fileServer;
    private static boolean isConnected = false; // Flag to track connection status

    public static void main(String[] args) throws RemoteException, IOException {
        String serverEnvVar = System.getenv("PA2_SERVER");

        if (serverEnvVar != null) {
            try {
                String[] serverInfo = serverEnvVar.split(":");

                if (serverInfo.length == 2) {
                    String host = serverInfo[0];
                    int port = Integer.parseInt(serverInfo[1]);

                    // Connect to RMI registry
                    Registry registry = LocateRegistry.getRegistry(host, port);
                    fileServer = (FileServer) registry.lookup("FileServer");

                    if (!isConnected) {
                        //System.out.println("Connected to server: " + host + ":" + port);
                        isConnected = true;
                    }

                    ClientOperations(args);
                } else {
                    System.out.println("ERROR: Invalid server format in PA2_SERVER environment variable.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: Unable to connect to the server. " + e.getMessage());
            }
        } else {
            System.out.println("ERROR: PA2_SERVER environment variable is not set.");
        }
    }

    private static void ClientOperations(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No command provided. Usage: upload/download/dir/mkdir/rmdir/rm/shutdown");
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "upload":
                if (args.length == 3)
                    Upload(args[1], args[2]);
                else
                    System.out.println("Usage: upload <localFilePath> <serverDestination>");
                break;

            case "download":
                if (args.length == 3)
                    Download(args[1], args[2]);
                else
                    System.out.println("Usage: download <serverFilePath> <localDestination>");
                break;

            case "dir":
                if (args.length == 2)
                    ListDirectory(args[1]);
                else
                    System.out.println("Usage: dir <directoryPath>");
                break;

            case "mkdir":
                if (args.length == 2)
                    CreateDirectory(args[1]);
                else
                    System.out.println("Usage: mkdir <directoryPath>");
                break;

            case "rmdir":
                if (args.length == 2)
                    RemoveDirectory(args[1]);
                else
                    System.out.println("Usage: rmdir <directoryPath>");
                break;

            case "rm":
                if (args.length == 2)
                    RemoveFile(args[1]);
                else
                    System.out.println("Usage: rm <filePath>");
                break;

            case "shutdown":
                ShutDown();
                break;

            default:
                System.out.println("Invalid command");
                break;
        }
    }

    private static void Upload(String filePath, String destination) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }

        try {
            fileServer.upload(destination, fileData);
            System.out.println("File uploaded: " + filePath);
        } catch (RemoteException e) {
            System.out.println("Error during file upload: " + e.getMessage());
        }
    }

    private static void Download(String serverPath, String clientPath) throws IOException {
        System.out.println("Server file path: " + serverPath + ", Client download path: " + clientPath);
        
        try {
            // Check if the file exists on the server
            byte[] fileData = fileServer.download(serverPath);
            if (fileData != null && fileData.length > 0) {
                // File exists, proceed to download
                File file = new File(clientPath);
                if (file.exists()) {
                    
                    // Logic for resuming download or appending
                    long bytesAlreadyDownloaded = file.length();
                    try (FileOutputStream fos = new FileOutputStream(file, true)) {
                        fos.write(fileData, (int) bytesAlreadyDownloaded, fileData.length - (int) bytesAlreadyDownloaded);
                        //System.out.println("Download resumed. File downloaded: " + clientPath);
                    }
                } else {
                    // New download
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(fileData);
                        System.out.println("File downloaded: " + clientPath);
                    }
                }
            } else {
                // File does not exist on the server
                System.out.println("\nFile does not exist on the server.");
            }
        } catch (IOException e) {
            System.out.println("Error during file download: " + e.getMessage());
            throw e;
        }
    }
    

    private static void ListDirectory(String directoryPath) throws RemoteException {
        String directoryListing = fileServer.listDirectory(directoryPath);
        System.out.println("Directory contents:\n" + directoryListing);
    }

    private static void CreateDirectory(String directoryPath) throws RemoteException {
        boolean success = fileServer.createDirectory(directoryPath);
        if (success) {
            System.out.println("Directory created: " + directoryPath);
        } else {
            System.out.println("Directory creation failed.");
        }
    }

    private static void RemoveDirectory(String directoryPath) throws RemoteException {
        boolean success = fileServer.removeDirectory(directoryPath);
        if (success) {
            System.out.println("Directory removed: " + directoryPath);
        } else {
            System.out.println("Directory removal failed.");
        }
    }

    private static void RemoveFile(String filePath) throws RemoteException {
        boolean success = fileServer.removeFile(filePath);
        if (success) {
            System.out.println("File removed: " + filePath);
        } else {
            System.out.println("File removal failed.");
        }
    }

    private static void ShutDown() throws RemoteException {
        try {
            // Call the shutdown method on the server
            fileServer.shutdown();
            System.out.println("Server/Client Closed");
            System.exit(0);  // Stop the client process
            
        } catch (RemoteException e) {
            System.out.println("Server/Client Closed");
        }
    }
    
    
}
