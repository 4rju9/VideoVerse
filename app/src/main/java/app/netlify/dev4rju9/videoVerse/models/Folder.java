package app.netlify.dev4rju9.videoVerse.models;

public class Folder {

    String id, folderName;

    public Folder(String id, String folderName) {
        this.id = id;
        this.folderName = folderName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

}
