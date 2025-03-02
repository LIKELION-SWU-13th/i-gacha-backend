package team.backend.converter;

import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.dto.WishDTO.WishResponseDTO;

import java.util.List;

public class WishConverter {
    //위시 생성 응답
    public static WishResponseDTO.CreateWishRsDTO createResult(Wish wish){
        return WishResponseDTO.CreateWishRsDTO.builder()
                .id(wish.getId())
                .name(wish.getName())
                .linK(wish.getLink())
                .imageUrl(wish.getImageUrl())
                .build();
    }

    //위시 생성
    public static Wish toWish(WishRequestDTO.CreateRqDTO request){
        return Wish.builder()
                .name(request.getLink())//임의값입니다.. 크롤링한 결과들 들어가야됨
                .link(request.getLink())
                .imageUrl(request.getLink())
                .build();
    }

    //삭제 성공 응답
    public static WishResponseDTO.DeleteResultRsDTO deleteResult(){
        return WishResponseDTO.DeleteResultRsDTO.builder()
                .text("삭제 성공!")
                .build();
    }

    //위시 전체 조회
    public static WishResponseDTO.GetWishTotalRsDTO wishViewDTO(Wish wish){
    }

    public static WishResponseDTO.GetWishTotalListRsDTO wishViewList(List<Wish> wishList){

    }
}
