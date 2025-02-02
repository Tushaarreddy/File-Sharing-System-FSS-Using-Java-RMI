import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServer extends Remote {

    void upload(String fileName, byte[] fileData) throws RemoteException;

    byte[] download(String fileName) throws RemoteException;

    String listDirectory(String directoryPath) throws RemoteException;

    boolean createDirectory(String directoryPath) throws RemoteException;

    boolean removeDirectory(String directoryPath) throws RemoteException;

    boolean removeFile(String filePath) throws RemoteException;

    void shutdown() throws RemoteException;
}
