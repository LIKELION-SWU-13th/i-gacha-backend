package team.backend.Controller;

import org.springframework.web.bind.annotation.*;
import team.backend.dto.WishDto;
import team.backend.service.WishService;

@RestController
@RequestMapping("/wish")
public class WishController {
    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping("/fetch")
    public WishDto fetchWish(@RequestParam String url) {
        return wishService.fetchWishData(url);
    }
}
