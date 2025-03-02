package team.backend.dto.WishDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class WishRequestDTO {
    //선물 생성, 수정
    @Getter
    public static class CreateRqDTO{
        @NotNull
        @NotBlank
        String link;
    }
}
