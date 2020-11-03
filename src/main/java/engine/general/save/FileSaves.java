package engine.general.save;

import java.io.*;
import java.nio.charset.Charset;



@SuppressWarnings("unused")
public class FileSaves {

    /**
     * Method saves a string in a file
     *
     * @param content of the file
     * @param location of the file: "dir/dir/"
     * @param name of the file
     */
    public static void saveString(String content, String location, String name) {
        saveString(content,location+name+".sf");
    }

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
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
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
     * @param name of the file (no extension)
     * @return content of the file or an empty string if an error occurs
     */
    public static FileWriteReadResult loadString(String location,String name,String extension) {
        return loadString(location + name,extension);
    }

    public static FileWriteReadResult loadSaveFile(String location,String name) {
        return loadString(location + name,".sf");
    }

    /**
     * loads a file from the jar directory
     *
     * @param name of the file
     * @return content of the file or an empty string if an error occurs
     */
    @SuppressWarnings("WeakerAccess")
    public static FileWriteReadResult loadString(String name, String extension) {
        File saveFile = new File(name+extension);
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
     * @return string list of the directories content
     */
    public static String[] listFolderContent(String location,String name) {
        return  listFolderContent(location + name);
    }

    /**
     * lists the content of a folder in the jars directory
     *
     * @param path of the folder
     * @return string list of the directories content
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
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public static FileWriteReadResult deleteFile(String name) {

        File f = new File(name+".sf");
        if(f.delete()) {
            return new FileWriteReadResult(FileWriteReadResult.ResultKind.SUCCESS,"File deleted successfully");
        }

        return new FileWriteReadResult(FileWriteReadResult.ResultKind.FAILED_TO_DELETE_FILE,"could not delete this file");
    }

    /**
     * deletes a file
     *
     * @param location of the file
     * @param name of the file
     */
    public static void deleteSaveFile(String location,String name) {
        deleteFile(location + name);
    }


}
