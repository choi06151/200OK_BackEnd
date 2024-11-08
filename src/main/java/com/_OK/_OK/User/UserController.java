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
    @Operation(summary = "유저 생성", description = "유저를 생성합니다.<br> id는 자동생성됩니다. name,water,food만 보내주세요 나머진 자동생성")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto createUser = userService.createUser(userDto);
        return new ResponseEntity<>(createUser, HttpStatus.CREATED);
    }
    @GetMapping("/userInfo/{id}")
    @Operation(summary = "플레이어 정보 조회",description = "플레이어의 정보를 조회합니다.<br>id는 유저 고유id를 의미한다.")
    public ResponseEntity<UserDto> userinfo(@PathVariable("id") Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    //물 개수 수정
    @PatchMapping("/userInfo/editWater/{id}/{delta}")
    @Operation(summary = "물 섭취",description = "물 개수의 변화량을 주면 user의 water가 수정된다. <br> -1와 같이 정수로 줘야한다.<br>수정된 user를 반환한다.<br>가능하면 -1씩 여러번 호출하는게 좋음")
    public ResponseEntity<UserDto> editWater(
            @PathVariable("id") Long userId,
            @PathVariable("delta") int dWater){

        UserDto userDto = userService.editWater(userId,dWater);
        return new ResponseEntity<>(userDto,HttpStatus.OK);

    }
    //음식 개수 수정
    @PatchMapping("/userInfo/editFood/{id}/{delta}")
    @Operation(summary = "음식 섭취",description = "음식 개수의 변화량을 주면 user의 food가 수정된다. <br> -1와 같이 정수로 줘야한다.<br>수정된 user를 반환한다.<br>가능하면 -1씩 여러번 호출하는게 좋음")
    public ResponseEntity<UserDto> editFood(
            @PathVariable("id") Long userId,
            @PathVariable("delta") int dFood){

        UserDto userDto = userService.editFood(userId,dFood);
        return new ResponseEntity<>(userDto,HttpStatus.OK);

    }
}
