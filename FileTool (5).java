package filetool;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileTool {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No command provided. Type 'help' to see available commands.");
            return;
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {

                case "help":
                    help();
                    break;

                case "list":
                    if (args.length < 2) {
                        System.out.println("Usage: list <directory_path>");
                    } else {
                        list(args[1]);
                    }
                    break;

                case "copy":
                    if (args.length < 3) {
                        System.out.println("Usage: copy <source> <destination>");
                    } else {
                        copyFile(args[1], args[2]);
                    }
                    break;

                case "move":
                    if (args.length < 3) {
                        System.out.println("Usage: move <source> <destination>");
                    } else {
                        moveFile(args[1], args[2]);
                    }
                    break;

                case "delete":
                    if (args.length < 2) {
                        System.out.println("Usage: delete <path>");
                    } else {
                        deleteFileOrDirectory(args[1]);
                    }
                    break;

                case "rename":
                    if (args.length < 3) {
                        System.out.println("Usage: rename <file> <new_name>");
                    } else {
                        renameFile(args[1], args[2]);
                    }
                    break;

                default:
                    System.out.println("Unknown command. Type 'help' to see available commands.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //---------------help method---------------//
    /// Displays all supported commands and how to use them
    private static void help() {
        System.out.println("Supported Commands:");
        System.out.println("help                         Show this help message");
        System.out.println("list [directory_path]        List all files in the given directory");
        System.out.println("copy [source] [destination]  Copy file from source to destination");
        System.out.println("move [source] [destination]  Move file from source to destination");
        System.out.println("delete [path]                Delete a file or directory");
        System.out.println("rename [path] [newname]      Rename a file");
    }

    //---------------list method---------------//
    public static void list(String PathString) {

    File directory = new File(PathString);

    // Check if the path exists
    if (!directory.exists()) {
        System.out.println("Directory does not exist.");
        return;
    }

    // Ensure the path refers to a directory, not a file
    if (!directory.isDirectory()) {
        System.out.println("The path is not a directory.");
        return;
    }

    // Verify read permission for the directory
    if (!directory.canRead()) {
        System.out.println("Permission denied: cannot read this directory.");
        return;
    }

    // Display directory contents
    System.out.println("Contents of: " + PathString);
    String[] files = directory.list();

    // Handle empty directory case
    if (files == null || files.length == 0) {
        System.out.println("Directory is empty.");
        return;
    }

    // Print each file or subdirectory name
    for (String f : files) {
        System.out.println(f);
    }
   }

 
//---------------copy method---------------//
public static void copyFile(String sourcePath, String destinationPath) {

    try {
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);

        // Check if the source file exists
        if (!Files.exists(source)) {
            System.out.println("Error: Source file does not exist.");
            return;
        }

        // Ensure the source is a file, not a directory
        if (Files.isDirectory(source)) {
            System.out.println("Error: Source path is a directory, not a file.");
            return;
        } 
        
        // Check read permission for source
        if (!Files.isReadable(source)) {
            System.out.println("Error: Permission denied to read source file");
            return;
        }

        // If destination is a directory, keep the original file name
        if (Files.isDirectory(destination)) {
            destination = destination.resolve(source.getFileName());
        }

        // Copy the file
        Files.copy(source, destination);
        System.out.println("File copied successfully to: " + destination);

    } catch (IOException e) {
        System.out.println("Error copying file: " + e.getMessage());
    }
}
   
//--------------- move method ---------------//
public static void moveFile(String sourcePath, String destinationPath) {

    // Validate input paths
    if (sourcePath == null || sourcePath.trim().isEmpty()) {
        System.err.println("Error: Source path is empty.");
        return;
    }
    if (destinationPath == null || destinationPath.trim().isEmpty()) {
        System.err.println("Error: Destination path is empty.");
        return;
    }

    try {
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);

        // Ensure source exists and is a file
        if (!Files.exists(source) || Files.isDirectory(source)) {
            System.err.println("Error: Invalid source file.");
            return;
        }

        // Check read permission for source file
        if (!Files.isReadable(source)) {
            System.err.println("Error: Cannot read source file.");
            return;
        }

        // Prevent moving into a file when destination exists
        if (Files.exists(destination) && !Files.isDirectory(destination)) {
            System.err.println("Error: Destination is a file, expected a directory.");
            return;
        }

        // Create parent directory if needed
        Path destParent = destination.getParent();
        if (destParent != null && !Files.exists(destParent)) {
            Files.createDirectories(destParent);
        }

        // Perform move using original filename if destination is directory
        if (Files.isDirectory(destination)) {
            destination = destination.resolve(source.getFileName());
        }

        Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File moved successfully.");

    } catch (AccessDeniedException e) {
        System.err.println("Error: Permission denied while moving file.");
    } catch (IOException e) {
        System.err.println("Error moving file: " + e.getMessage());
    } catch (InvalidPathException e) {
        System.err.println("Error: Invalid path format.");
    }
}

   
//---------------delete method---------------// 
public static void deleteFileOrDirectory(String pathString) { 
    File file = new File(pathString); 

    // Check existence 
    if (!file.exists()) { 
        System.out.println("Error: File or directory not found: " + pathString);
        return; 
    } 

    // Check read permission 
    if (!file.canRead()) { 
        System.out.println("Error: No read permission for: " + pathString);
        return; 
    } 

    // Check write/delete permission 
    if (!file.canWrite()) { 
        System.out.println("Error: No write permission for: " + pathString); 
        return; 
    } 

    // Prevent deleting non-empty directory 
    if (file.isDirectory()) { 
        File[] files = file.listFiles(); 
        if (files != null && files.length > 0) { 
            System.out.println("Error: Cannot delete non-empty directory: " + pathString); 
            return; 
        } 
    } 

    // Try deletion 
    try { 
        if (file.delete()) { 
            if (file.isDirectory()) { 
                System.out.println("Directory deleted successfully: " + pathString); 
            } else { 
                System.out.println("File deleted successfully: " + pathString); 
            } 
        } else { 
            System.out.println("Error: Failed to delete: " + pathString); 
        } 

    } catch (SecurityException se) { 
        System.out.println("Permission denied: " + se.getMessage()); 

    } catch (Exception e) { 
        System.out.println("Error deleting file: " + e.getMessage()); 
    } 
}
 
//--------------- rename method ---------------//
public static void renameFile(String filePath, String newName) {

    Path file = Paths.get(filePath);

    // Check if the file exists
    if (!Files.exists(file)) {
        System.out.println("File not found: " + filePath);
        return;
    }

    // Ensure the path refers to a file and not a directory
    if (Files.isDirectory(file)) {
        System.out.println("The specified path refers to a directory, not a file.");
        return;
    }

    // Validate that the new file name is not null or empty
    if (newName == null || newName.trim().isEmpty()) {
        System.out.println("The new file name must not be empty.");
        return;
    }

    // Validate file name characters (security & OS safety)
    // Allowed: letters, numbers, dot (.), underscore (_), dash (-)
    if (!newName.matches("^[a-zA-Z0-9._-]+$")) {
        System.out.println(
            "Invalid file name. Only letters, numbers, dots (.), underscores (_), and dashes (-) are allowed."
        );
        return;
    }

    // Prevent path traversal by disallowing directory separators
    if (newName.contains("/") || newName.contains("\\")) {
        System.out.println("The new file name must not contain path separators.");
        return;
    }

    // Retrieve the parent directory
    Path parentDirectory = file.getParent();
    if (parentDirectory == null) {
        System.out.println("Unable to determine the parent directory.");
        return;
    }

    // Check write permission on the directory
    if (!Files.isWritable(parentDirectory)) {
        System.out.println("Permission denied: insufficient write access to the directory.");
        return;
    }

    // Resolve the new file path within the same directory
    Path newPath = parentDirectory.resolve(newName);

    // Check if a directory with the new name already exists
    if (Files.exists(newPath) && Files.isDirectory(newPath)) {
        System.out.println("A directory with the specified new name already exists.");
        return;
    }

    // Check if a file with the new name already exists
    if (Files.exists(newPath) && Files.isRegularFile(newPath)) {
        System.out.println("A file with the new name already exists.");
        return;
    }

    // Handle case where the new name is identical to the current name
    if (file.equals(newPath)) {
        System.out.println("The new file name is identical to the current name.");
        return;
    }

    try {
        // Rename the file by moving it to the new path
        Files.move(file, newPath);
        System.out.println("File renamed successfully to: " + newPath.getFileName());

    } catch (AccessDeniedException e) {
        System.out.println("Permission denied while attempting to rename the file.");
    } catch (IOException e) {
        System.out.println("File renaming failed due to an I/O error: " + e.getMessage());
    }
}
}