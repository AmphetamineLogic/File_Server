package server;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    final static String rootPath = System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;
    final static File mapFile = new File(rootPath + "files.map");
    private ConcurrentHashMap<String, Integer> fileMap;

    protected Storage() {
        if (mapFile.exists()) {
            loadMap();
        } else {
            fileMap = new ConcurrentHashMap<>();
        }
    }

    protected void writeSomeShit(String string, Integer integer) {
        fileMap.put(string, integer);
    }

    protected byte[] readFile(String name) {
        File file = new File(rootPath + name);
        byte[] fileAsBytes = new byte[0];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            fileAsBytes = bufferedInputStream.readAllBytes();
            return fileAsBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected byte[] readFile(Integer id) {
        byte[] fileAsBytes = new byte[0];
        Iterator<Map.Entry<String, Integer>> iterator = fileMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> currentEntry = iterator.next();
            String currentName = currentEntry.getKey();
            Integer currentId = currentEntry.getValue();
            if (currentId == id) {
                File file = new File(rootPath + currentName);
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    fileAsBytes = bufferedInputStream.readAllBytes();
                    return fileAsBytes;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected boolean deleteFile(String name) {
        if (fileMap.contains(name)) {
            File file = new File(rootPath + name);
            file.delete();
            fileMap.remove(name);
            return true;
        }
        return false;
    }

    protected boolean deleteFile(Integer id) {
        Iterator<Map.Entry<String, Integer>> iterator = fileMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> currentEntry = iterator.next();
            String currentName = currentEntry.getKey();
            Integer currentId = currentEntry.getValue();
            if (currentId == id) {
                File file = new File(rootPath + currentName);
                file.delete();
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    private void loadMap() {
        try {
            FileInputStream fileInputStream = new FileInputStream(mapFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
            fileMap = (ConcurrentHashMap<String, Integer>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void saveMap() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mapFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(fileMap);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
