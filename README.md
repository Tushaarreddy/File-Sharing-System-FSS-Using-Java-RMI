# File-Sharing-System-FSS-Using-Java-RMI

# File Sharing System (FSS)
## Overview
The File Sharing System (FSS) is a client-server application implemented using Java RMI, enabling file uploads, downloads, directory listing, and management via a command-line interface. It comprises a File Server and multiple Clients, allowing concurrent connections from multiple clients to the server and supports resuming interrupted file transfers.

## System Requirements
Java (Version 8 or above)

## Environment Variable Setup: 
Ensure that the environment variable PA2_SERVER is set with the format <hostname>:<port> (In My case it listed as follows: localhost:5011)

```bash
export PA2_SERVER = localhost:5011
```

## Location of Server's Storage folder
/tmp/client1/ , /tmp/client2/ , /tmp/client3/, /tmp/client4/

## Instructions for Running the System
### 1. Starting the Server
Navigate to the directory containing the JAR file (PA2.jar).

Command to convert class files in jar file:

jar cvfm PA2.jar manifest.txt *.class

Run the server using the following command:


```bash
java -cp PA2.jar Server start 5011

```


The server will start and will use its current directory as its root storage folder for file uploads and directory operations.

### 2. Running the Client
To perform file operations, open a new terminal session and use the following steps:

```bash
export PA2_SERVER=localhost:<port_number>
#example
export PA2_SERVER=localhost:5011
```

- ** Upload a File **
Command to upload a file to the server:

```bash
java -cp PA2.jar Client upload <local_file_path> <Server_Path>
#example 
java -cp PA2.jar Client upload /Users/tushaarreddy/Downloads/MainFile.pdf /tmp/client1/MainFile.pdf
```
If the file exists on the server, it will be replaced.

- ** Download a File **
Command to download a file from the server:

```bash
java -cp PA2.jar Client download <server_file_path> <local_download_path>
#example
java -cp PA2.jar Client download /tmp/client1/MainFile.pdf copy_of_MainFile.pdf 
```

If the file does not exist on the server, an error message will be displayed.

- ** List Directory Contents **
Command to list contents of a directory on the server:

```bash
java -cp PA2.jar Client dir <server_directory_path>
#example
java -cp PA2.jar Client dir /tmp/
```

Lists files and directories within the specified server directory. 

- ** Create Directory **
Command to create a directory on the server:

```bash
java -cp PA2.jar Client mkdir <server_directory_path>
#example
java -cp PA2.jar Client mkdir /tmp/client1/
```

Creates a new directory at the specified path. 

- ** Remove Directory **
Command to remove an empty directory from the server:

```bash
java -cp PA2.jar Client rmdir <server_directory_path>
#example
java -cp PA2.jar Client rmdir /tmp/client1/
```

Deletes an empty directory. If it contains files, it will not be deleted.

- ** Remove File **
Command to delete a file from the server:

```bash
java -cp PA2.jar Client rm <server_file_path>
#example
java -cp PA2.jar Client rm /tmp/client1/MainFile.pdf
```

Deletes the specified file. If it doesn’t exist, an error message will be displayed.

- ** Resume Upload/Download **

If a file transfer is interrupted, the client can resume by re-issuing the upload/download command for the same file path. The program will print progress and skip any already-transferred data.

- ** Shutdown Server **
Command to shut down the server from a client:

```bash
java -cp PA1.jar Client shutdown
```

- ** Directory Path where the file stores in the server: **

/tmp/
```bash
#example
/tmp/client1/MainFile.pdf
```

- ** Example command to run the jar file: **

start server first in terminal
server side: 
```bash 
java -cp PA2.jar Server start 5011
```

Client1 side:
```bash
export PA2_SERVER=localhost:5011
java -cp PA2.jar Client upload /Users/tushaarreddy/Downloads/MainFile.pdf /tmp/client1/MainFile.pdf
```

File upload complete

Client2 side:
```bash
export PA2_SERVER=localhost:5011
java -cp PA2.jar Client download /tmp/client2/MainFile.pdf copy_of_MainFile.pdf
```

- ** Location of File on Server: **

/tmp/client2/MainFile.pdf Download Location: MainFile.pdf
Downloading file - 100% complete%

```bash 
java -cp PA2.jar Client shutdown
```

Server/Client Closed
