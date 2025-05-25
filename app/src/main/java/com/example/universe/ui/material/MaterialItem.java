package com.example.universe.ui.material;

public class MaterialItem {

    private String title;
    private String description;
    private String type;       // Örn: "ders_notlari", "pdf", "link"
    private String fileUrl;    // Eklenen alan: dosya bağlantısı veya link

    // Boş constructor (Firebase için gerekir)
    public MaterialItem() {
    }

    // Constructor
    public MaterialItem(String title, String description, String type, String fileUrl) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.fileUrl = fileUrl;
    }

    // Getter ve Setter metotları
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
