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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/capsule")
@Tag(name = "스노우볼", description = "스노우볼 관리 API")
public class SnowballController {
    private final UserService userService;
    private final SnowballService snowballService;

    @PostMapping
    @Operation(summary = "스노우볼 생성", description = "나의 스노우볼 페이지를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "나의 스노우볼 페이지 생성 성공",
                    content = @Content(schema = @Schema(implementation = SnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    public ResponseEntity<?> makeSnowball(@RequestHeader("Authorization") String token) {
        User user = userService.findByAccessToken(token);
        Snowball snowball = snowballService.makeSnowball(user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.response(new SnowballDto(snowball, user)));
    }

    @Operation(summary = "스노우볼 페이지네이션 데이터 조회", description = "스노우볼의 페이지네이션 관련 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스노우볼 페이지네이션 데이터 조회 성공",
                    content = @Content(schema = @Schema(implementation = PaginationSnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{id}/pagination")
    public ResponseEntity<?> getSnowballPagination(
            @PathVariable("id") String id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam("page") Integer page) {

        Snowball snowball;

        // Authorization 헤더가 있는 경우: 개인 스노우볼 조회
        if (token != null) {
            User user = userService.findByAccessToken(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponse.response("인증 실패"));
            }

            snowball = snowballService.getMySnowball(user.getEmail());

            if (snowball == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.response("스노우볼을 찾을 수 없습니다."));
            }

        } else {
            // Authorization 헤더가 없고 ID가 있는 경우: 공개된 스노우볼 조회
            if (id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.response("스노우볼 ID가 필요합니다."));
            }

            snowball = snowballService.getSnowball(id);

            if (snowball == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.response("스노우볼을 찾을 수 없습니다."));
            }
        }

        // 공통 응답 데이터 구성
        LocalDateTime serverTime = LocalDateTime.now();
        String isoServerTime = serverTime.format(DateTimeFormatter.ISO_DATE_TIME);

        PaginationSnowballResponse response = new PaginationSnowballResponse(page, isoServerTime, snowball);
        return ResponseEntity.ok(BaseResponse.response(response));
    }

    @Operation(summary = "스노우볼 기본 데이터 조회", description = "스노우볼의 기본 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스노우볼 기본 데이터 조회 성공",
                    content = @Content(schema = @Schema(implementation = BasicSnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{id}/info")
    public ResponseEntity<?> getSnowballBasicInfo(
            @PathVariable("id") String id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Snowball snowball;

        if (token != null) {
            User user = userService.findByAccessToken(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponse.response("인증 실패"));
            }

            snowball = snowballService.getMySnowball(user.getEmail());

            if (snowball == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.response("스노우볼을 찾을 수 없습니다."));
            }

        } else {
            if (id == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.response("스노우볼 ID가 필요합니다."));
            }

            try{
                snowball = snowballService.getSnowball(id);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.response("스노우볼을 찾을 수 없습니다."));
            }
        }

        // 기본 데이터 구성
        BasicSnowballResponse response = BasicSnowballResponse.from(snowball);
        return ResponseEntity.ok(BaseResponse.response(response));
    }

    @Operation(summary = "스노우볼 이름 수정", description = "나의 스노우볼 이름을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "나의 스노우볼 이름 수정 성공",
                    content = @Content(schema = @Schema(implementation = SnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/changeSnowballName")
    public ResponseEntity<?> changeSnowballName(@RequestHeader("Authorization") String token,
                                                @RequestParam("name") String snowballName){
        User user = userService.findByAccessToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("토큰이 유효하지 않거나 누락되었습니다."));
        }

        Snowball snowball = snowballService.changeSnowballName(user.getEmail(), snowballName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.response(new SnowballResponse(snowball)));
    }


}
