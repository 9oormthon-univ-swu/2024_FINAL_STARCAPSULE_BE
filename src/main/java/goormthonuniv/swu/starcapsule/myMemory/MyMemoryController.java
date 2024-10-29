package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/my_memory")
@Tag(name = "내 추억", description = "내 추억 기록 API")
public class MyMemoryController {

    private final UserService userService;
    private final MyMemoryService myMemoryService;

    public MyMemoryController(UserService userService, MyMemoryService myMemoryService) {
        this.userService = userService;
        this.myMemoryService = myMemoryService;
    }

    @Operation(summary = "내 추억 기록", description = "나의 추억을 기록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "내 추억 작성 성공",
                    content = @Content(schema = @Schema(implementation = MyMemory.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 오늘의 기록이 존재합니다.",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/write")
    public ResponseEntity<?> createMemory(@RequestHeader("Authorization") String token,
                                          @RequestParam("title") String title,
                                          @RequestParam("answer") String answer,
                                          @RequestParam("shapeName") String shapeName,
                                          @RequestPart(value = "image", required = false) MultipartFile image) throws IOException{

        User user = userService.findByAccessToken(token);

        if (myMemoryService.existsMemoryForUser(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.response("이미 오늘의 기록이 존재합니다."));
        }

        myMemoryService.createMemory(title, answer, shapeName, user.getEmail(), image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.response("기록이 성공적으로 저장되었습니다."));
    }

    @Operation(summary = "내 추억 상세 조회", description = "나의 추억을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 추억 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyMemoryDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 추억을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{memory_id}")
    public ResponseEntity<?> getMemory(@PathVariable("memory_id") Long memoryId,
                                       @RequestHeader("Authorization") String token) {
        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        MyMemory memory = myMemoryService.getMemoryById(memoryId);

        if (memory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.response("해당 추억을 찾을 수 없습니다."));
        }

        MyMemoryDto memoryDto = MyMemoryDto.fromEntity(memory);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.response(memoryDto));
    }

}
