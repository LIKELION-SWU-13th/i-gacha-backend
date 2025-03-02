package team.backend.dto.WishDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class WishResponseDTO {
    //선물 조회
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWishTotalListRsDTO{
        List<GetWishTotalRsDTO> wishList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWishTotalRsDTO{
        Long id;
        String name;
        String link;
        String imageUrl;
    }

    //선물 생성, 수정
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWishRsDTO{
        Long id;
        String name;
        String linK;
        String imageUrl;
    }

    //선물 삭제
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResultRsDTO{
        String text;
    }

    //선물 뽑기 메인

    //선물 뽑기
}
