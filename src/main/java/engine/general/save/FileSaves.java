package engine.general.save;

import java.io.*;
import java.nio.charset.Charset;


/**
 * class contains only static methods for the following operations:
 *
 * saving strings to files at a specific location (automatically creating
 * the necessary folders)
 *
 * creating a specified folder chain
 *
 * loading a string from a file
 *
 * deleting a file
 *
 * All of the actions return a FileWriteReadResult as a feedback whether the
 * operation was successful or not. This object als contains either more specific
 * information about the failure or the result of the operation e.g. the loaded string
 *
 * PARAMETER FORMAT:
 *
 *      path:   contains out of the complete path (in case of files with extension),
 *              relative to the jar file:
 *              dir/dir/dir/filename.extension
 *
 *      name:   contains the full name of the folder or file (in case of files with extension),
 *              filename.extension
 *
 *      location:   contains the location of a file or directory, always ends with /:
 *                  dir/dir/dir/
 *
 * @see FileWriteReadResult
 */
@SuppressWarnings("unused")
public class FileSaves {

    /**
     * Method saves a string in a file
     *
     * @param content of the file
     * @param location of the file: "dir/dir/"
     * @param name of the file with extension
     * @return FileWriteReadResult containing information about the saving operation
     */
    public static FileWriteReadResult saveString(String content, String location, String name) {
        return saveString(content,location+name);
    }

    /**
     * creates Folders up to the last name mentioned in the path
     *
     * @param path of the folders: "dir/dir/"
     * @return true if the folder exists now
     */
    public static boolean createFolders(String path) {
        File temp = new File(path + "temp.sf");
        if(!temp.getParentFile().exists()) {
            return temp.getParentFile().mkdirs();
        } else {
            return true;
        }
    }

    /**
     * creates the parent folders for a file
     *
     * @param file of which the parent folders should be created
     * @return if true the folders existed already or were created
     */
    private static boolean createFolders(File file) {
        if(!file.getParentFile().exists()) {
            return file.getParentFile().mkdirs();
        } else {
            return true;
        }
    }

    /**
     * saves a string in a file, default location: jar folder
     *
     * @param content of the file
     * @param path of the file with name and extension
     * @return FileWriteReadResultObject containing information about the operation
     */
    @SuppressWarnings({"WeakerAccess"})
    public static FileWriteReadResult saveString(String content, String path) {
        File saveFile = new File(path);
        if(!createFolders(saveFile)) {
            return new FileWriteReadResult(FileWriteReadResult.ResultKind.CANT_CREATE_FOLDERS,"One of the parent folders could not be created");
        }

        if(saveFile.exists()) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile))) {
                bw.write(content);
            } catch (IOException e) {
                return new FileWriteReadResult(FileWriteReadResult.ResultKind.FAILED_TO_WRITE_DATA,e.getMessage());
            }
        } else {
            try {
                //noinspection ResultOfMethodCallIgnored
                saveFile.createNewFile();

                try(BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile))) {
                    bw.write(content);
                } catch (IOException e) {
                    return new FileWriteReadResult(FileWriteReadResult.ResultKind.FAILED_TO_WRITE_DATA,e.getMessage());
                }
            } catch (IOException e) {
                return new FileWriteReadResult(FileWriteReadResult.ResultKind.CANT_CREATE_FILE,e.getMessage());

            }
        }

        return new FileWriteReadResult(FileWriteReadResult.ResultKind.SUCCESS,"String saved successfully");
    }

    /**
     * loads a saved string from a file with the given name at the give location
     * relative to the jars directory
     *
     * @param location of the file relative to the jar: "dir/dir/"
     * @param name of the file with extension
     * @return FileWriteReadResult object containing either a failure reason or the loaded string in
     *          the description
     * @see FileWriteReadResult
     */
    public static FileWriteReadResult loadFile(String location,String name) {
        return loadString(location + name);
    }

    /**
     * loads a file from the jar directory
     *
     * @param path of the file
     * @return FileWriteReadResult object containing either a failure reason or the loaded string in
     *         the description
     */
    @SuppressWarnings("WeakerAccess")
    public static FileWriteReadResult loadString(String path) {
        File saveFile = new File(path);
        if(saveFile.exists()) {
            StringBuilder result = new StringBuilder();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(saveFile), Charset.forName("ISO-8859-15")))) {

                String line;
                while((line = br.readLine()) != null){
                    result.append(line);
                }
            } catch (IOException e) {
                new FileWriteReadResult(FileWriteReadResult.ResultKind.CANT_READ_FILE,e.getMessage());
            }
            return new FileWriteReadResult(FileWriteReadResult.ResultKind.SUCCESS,result.toString());
        } else {
            return new FileWriteReadResult(FileWriteReadResult.ResultKind.FILE_DOES_NOT_EXIST,"cant find file at this location");
        }
    }

    /**
     * lists content of a folder
     *
     * @param location of the folder
     * @param name of the folder
     * @return string array of the directories content
     */
    public static String[] listFolderContent(String location,String name) {
        return  listFolderContent(location + name);
    }

    /**
     * lists the content of a folder in the jars directory
     *
     * @param path of the folder
     * @return string array of the directories content
     */
    @SuppressWarnings("WeakerAccess")
    public static String[] listFolderContent(String path) {
        File file = new File(path);
        return file.list();
    }

    /**
     * deletes a file with a specific name in the jars folder
     *
     * @param name of the file
     * @return FileWriteReadResult containing the result of the operation
     */
    @SuppressWarnings({"WeakerAccess"})
    public static FileWriteReadResult deleteFile(String name) {

        File f = new File(name);
        if(f.delete()) {
            return new FileWriteReadResult(FileWriteReadResult.ResultKind.SUCCESS,"File deleted successfully");
        }

        return new FileWriteReadResult(FileWriteReadResult.ResultKind.FAILED_TO_DELETE_FILE,"could not delete this file");
    }

    /**
     * deletes a file
     *
     * @param location of the file
     * @param name of the file with extension
     * @return FileWriteReadResult containing the result of the operation
     */
    public static FileWriteReadResult deleteSaveFile(String location,String name) {
        return deleteFile(location + name);
    }


}
