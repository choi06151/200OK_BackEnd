package com._OK._OK.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/amazon/user")
@Tag(name="User", description = "User Api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping

    @Operation(summary = "유저 생성", description = "유저를 생성합니다.<br> id는 자동생성됩니다. id, day 필드는 Body에 포함하지 마세요.")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto createUser = userService.createUser(userDto);
        return new ResponseEntity<>(createUser, HttpStatus.CREATED);
    }
    @GetMapping("/userInfo/{id}")
    @Operation(summary = "플레이어 정보 조회",description = "플레이어의 정보를 조회합니다.<br>id는 유저 고유id를 의미한다.")
    public ResponseEntity<UserDto> Userinfo(@PathVariable("id") Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
