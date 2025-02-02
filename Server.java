import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.Arrays;

public class Server extends UnicastRemoteObject implements FileServer {

    protected Server() throws RemoteException {
        super();
    }


    @Override
    public void upload(String fileName, byte[] fileData) throws RemoteException {
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs(); // Ensure the parent directory exists

            try (FileOutputStream fos = new FileOutputStream(file)) {
                int bufferSize = 1024; // Buffer size for writing
                long totalBytes = fileData.length;
                long bytesWritten = 0;

                for (int i = 0; i < fileData.length; i += bufferSize) {
                    int length = Math.min(bufferSize, fileData.length - i);
                    fos.write(fileData, i, length);
                    bytesWritten += length;

                    // Calculate and display progress
                    int progress = (int) ((double) bytesWritten / totalBytes * 100);
                    System.out.print("\rUploading: " + progress + "%");
                }
            }

            System.out.println("\nFile uploaded successfully: " + fileName);
        } catch (IOException e) {
            throw new RemoteException("File upload failed", e);
        }
    }


    @Override
    public byte[] download(String fileName) throws RemoteException {
        try {
            File file = new File(fileName);
            if (!file.exists()) throw new RemoteException("File not found");

            long totalBytes = file.length();
            byte[] fileData = new byte[(int) totalBytes];
            long bytesRead = 0;

            try (FileInputStream fis = new FileInputStream(file)) {
                int bufferSize = 1024; // Buffer size for reading
                byte[] buffer = new byte[bufferSize];
                int read;

                while ((read = fis.read(buffer)) != -1) {
                    System.arraycopy(buffer, 0, fileData, (int) bytesRead, read);
                    bytesRead += read;

                    // Calculate and display progress
                    int progress = (int) ((double) bytesRead / totalBytes * 100);
                    System.out.print("\rDownloading: " + progress + "%");
                }
            }

            System.out.println("\nFile downloaded successfully: " + fileName);
            return fileData;
        } catch (IOException e) {
            throw new RemoteException("File download failed", e);
        }
    }


    @Override
    public String listDirectory(String directoryPath) throws RemoteException {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) return "Invalid directory path";

        return Arrays.toString(dir.listFiles());
    }

    @Override
    public boolean createDirectory(String directoryPath) throws RemoteException {
        File dir = new File(directoryPath);
        return dir.mkdir();
    }

    @Override
    public boolean removeDirectory(String directoryPath) throws RemoteException {
        File dir = new File(directoryPath);
        return dir.exists() && dir.isDirectory() && dir.delete();
    }

    @Override
    public boolean removeFile(String filePath) throws RemoteException {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.delete();
    }

    @Override
    public void shutdown() throws RemoteException {
        try {
            System.out.println("Server shutting down...");
            UnicastRemoteObject.unexportObject(this, true);  // Properly unexport the object
            System.exit(0);  // Shuts down the server
        } catch (Exception e) {
            System.out.println("Error during shutdown: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length < 3 || !args[0].equalsIgnoreCase("Server") || !args[1].equalsIgnoreCase("start")) {
                System.err.println("Usage: java -jar PA2.jar Server start <port>");
                return;
            }
    
            int port = Integer.parseInt(args[2]); // Parse the port number
    
            // Create the server object
            Server server = new Server();
    
            // Create the RMI registry on the specified port
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("FileServer", server);
    
            System.out.println("Server started on port: " + port);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }  
    
}
