package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/capsule")
@Tag(name = "스노우볼", description = "스노우볼 관리 API")
public class SnowballController {
    private final UserService userService;
    private final SnowballService snowballService;

    @Operation(summary = "스노우볼 생성", description = "나의 스노우볼 페이지를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "나의 스노우볼 페이지 생성 성공",
                    content = @Content(schema = @Schema(implementation = SnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> makeSnowball(@RequestHeader("Authorization") String token){
        User user = userService.findByAccessToken(token);
        Snowball snowball = snowballService.makeSnowball(user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.response(new SnowballResponse(snowball)));
    }

    @Operation(summary = "스노우볼 가져오기", description = "나의 스노우볼을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "나의 스노우볼 조회 성공",
                    content = @Content(schema = @Schema(implementation = SnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/{id}")
    public ResponseEntity<?> getSnowball(@PathVariable("id") Long userId){
        Snowball snowball = snowballService.getSnowball(user.getEmail(), snowballName);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.response(new SnowballResponse(snowball)));
    }

    @Operation(summary = "스노우볼 이름 수정", description = "나의 스노우볼 이름을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "나의 스노우볼 이름 수정 성공",
                    content = @Content(schema = @Schema(implementation = SnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/changeSnowballName")
    public ResponseEntity<?> changeSnowballName(@RequestHeader("Authorization") String token,
                                                @RequestParam("name") String snowballName){
        User user = userService.findByAccessToken(token);
        Snowball snowball = snowballService.changeSnowballName(user.getEmail(), snowballName);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.response(new SnowballResponse(snowball)));
    }
}
