package team.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import team.backend.apiPayload.ApiResponse;
import team.backend.converter.WishConverter;
import team.backend.domain.Event;
import team.backend.domain.Wish;
import team.backend.dto.WishDTO.WishRequestDTO;
import team.backend.dto.WishDTO.WishResponseDTO;
import team.backend.service.WishCommandService;
import team.backend.service.WishQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gift")
public class WishRestController {
    private final WishCommandService wishCommandService;
    private final WishQueryService wishQueryService;

    //선물 조회
    @GetMapping("/{userId}/event/{eventId}")
    public ApiResponse<WishResponseDTO.GetWishTotalListRsDTO> getWishList(
            @PathVariable(name="userId") Long userId,
            @PathVariable(name="eventId") Long eventId,
            @RequestParam(name = "page") Integer page){
        wishCommandService.checkEvent(userId, eventId);
        Page<Wish> wishPage = wishQueryService.getWishList(eventId, page);
        return ApiResponse.onSuccess(WishConverter.wishViewListDTO(wishPage));
    }

    //선물 생성
    @PostMapping("/{userId}/event/{eventId}/create")
    public ApiResponse<WishResponseDTO.CreateWishRsDTO> createWish(
            @PathVariable(name="userId") Long userId,
            @PathVariable(name="eventId") Long eventId,
            @RequestBody @Valid WishRequestDTO.CreateRqDTO request
            ){
        Wish wish = wishCommandService.joinEvent(userId, eventId, request);
        return ApiResponse.onSuccess(WishConverter.createResult(wish));

    }

    //선물 수정
    @PatchMapping("/{userId}/event/{eventId}/update/{wishId}")
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
    @DeleteMapping("/{userId}/event/{eventId}/delete/{wishId}")
    public ApiResponse<WishResponseDTO.DeleteResultRsDTO> deleteWish(@PathVariable(name="userId") Long userId,
                                        @PathVariable(name="eventId") Long eventId,
                                        @PathVariable(name="wishId") Long wishId){
        wishCommandService.delSerWish(userId, eventId, wishId);
        return ApiResponse.onSuccess(WishConverter.deleteResult());
    }

    //선물 뽑기 메인
    @GetMapping("/{userId}/event/{eventId}/drawing/main")
    public ApiResponse<WishResponseDTO.DrawingResultRsDTO> drawWish(
            @PathVariable(name="userId") Long userId,
            @PathVariable(name="eventId") Long eventId
    ){
        wishCommandService.checkEvent(userId, eventId);
        Event event = wishQueryService.getEventName(eventId);
        return ApiResponse.onSuccess(WishConverter.drawingResult(event));
    }

    //선물 뽑기
    @GetMapping("/event/{eventId}/drawing")
    public ApiResponse<WishResponseDTO.CreateWishRsDTO> drawRandomWish(
            @PathVariable(name="eventId") Long eventId
    ){
        Wish wish = wishQueryService.drawRandomWish(eventId);
        return ApiResponse.onSuccess(WishConverter.createResult(wish));
    }
}
