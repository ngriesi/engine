package engine.general.save;

import java.io.*;
import java.nio.charset.Charset;

public class FileSaves {

    /**
     * Method saves a string in a file
     *
     * @param content of the file
     * @param location of the file: "dir/dir/"
     * @param name of the file
     */
    public static void saveString(String content, String location, String name) {
        saveString(content,location+name);
    }

    public static void createFolders(String path) {
        File temp = new File(path + "temp.sf");
        if(!temp.getParentFile().exists()) {
            temp.getParentFile().mkdirs();
        }
    }

    /**
     * saves a string in a file, default location: jar folder
     *
     * @param content of the file
     * @param name of the file
     */
    public static void saveString(String content,String name) {
        File saveFile = new File(name+".sf");
        if(!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        if(saveFile.exists()) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile))) {
                bw.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                saveFile.createNewFile();
                try(BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile))) {
                    bw.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * loads a saved string from a file with the given name at the give location
     * relative to the jars directory
     *
     * @param location of the file relative to the jar: "dir/dir/"
     * @param name of the file (no extension)
     * @return content of the file or an empty string if an error occurs
     */
    public static String loadString(String location,String name,String extension) {
        return loadString(location + name,extension);
    }

    public static String loadSaveFile(String location,String name) {
        return loadString(location + name,".sf");
    }

    /**
     * loads a file from the jar directory
     *
     * @param name of the file
     * @return content of the file or an empty string if an error occurs
     */
    public static String loadString(String name, String extention) {
        File saveFile = new File(name+extention);
        if(saveFile.exists()) {
            StringBuilder result = new StringBuilder();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(saveFile), Charset.forName("ISO-8859-15")))) {

                String line;
                while((line = br.readLine()) != null){
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        } else {
            return "";
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
     * @param name of the folder
     * @return string list of the directories content
     */
    public static String[] listFolderContent(String name) {
        File file = new File(name);
        return file.list();
    }

    /**
     * deletes a file with a specific name in the jars folder
     *
     * @param name of the file
     */
    public static void deleteFile(String name) {

        File f = new File(name+".sf");
        f.delete();
    }

    /**
     * deletes a file
     *
     * @param location of the file
     * @param name of the file
     */
    public static void deleteSaveGame(String location,String name) {
        deleteFile(location + name);
    }

}
