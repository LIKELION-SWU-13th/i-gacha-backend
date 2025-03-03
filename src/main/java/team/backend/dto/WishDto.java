package team.backend.dto;

public class WishDto {
    private String title;
    private String imageUrl;

    public WishDto(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

