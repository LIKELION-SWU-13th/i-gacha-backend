package team.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.backend.apiPayload.ApiResponse;
import team.backend.converter.WishConverter;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.dto.WishDTO.WishResponseDTO;
import team.backend.service.WishCommandService;
import team.backend.service.WishQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gift/{userId}/event")
public class WishRestController {
    private final WishCommandService wishCommandService;
    private final WishQueryService wishQueryService;

    //선물 조회
    @GetMapping("/{eventId}")
    public ApiResponse<WishResponseDTO.GetWishTotalListRsDTO> getWishList(
            @PathVariable(name="userId") Long userId,
            @PathVariable(name="eventId") Long eventId,
            @RequestParam(name = "page") Integer page){
        wishQueryService.getWishList(eventId, page);
        return null;
    }

    //선물 생성
    @PostMapping("/{eventId}/create")
    public ApiResponse<WishResponseDTO.CreateWishRsDTO> createWish(
            @PathVariable(name="userId") Long userId,
            @PathVariable(name="eventId") Long eventId,
            @RequestBody @Valid WishRequestDTO.CreateRqDTO request
            ){
        Wish wish = wishCommandService.joinEvent(userId, eventId, request);
        return ApiResponse.onSuccess(WishConverter.createResult(wish));

    }

    //선물 수정
    @PatchMapping("/{eventId}/update/{wishId}")
    public ApiResponse<WishResponseDTO.CreateWishRsDTO> updateWish(
            @PathVariable(name="userId") Long userId,
            @PathVariable(name="eventId") Long eventId,
            @PathVariable(name="wishId") Long wishId,
            @RequestBody @Valid WishRequestDTO.CreateRqDTO request
    ){
        Wish wish = wishCommandService.updateWish(userId, eventId, wishId, request);
        return ApiResponse.onSuccess(WishConverter.createResult(wish));

    }


    //선물 삭제
    @DeleteMapping("/{eventId}/delete/{wishId}")
    public ApiResponse<WishResponseDTO.DeleteResultRsDTO> deleteWish(@PathVariable(name="userId") Long userId,
                                        @PathVariable(name="eventId") Long eventId,
                                        @PathVariable(name="wishId") Long wishId){
        wishCommandService.delSerWish(userId, eventId, wishId);
        return ApiResponse.onSuccess(WishConverter.deleteResult());
    }

    //선물 뽑기 메인

    //선물 뽑기
}
