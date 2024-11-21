package com._OK._OK.Ranking;


import com._OK._OK.User.User;
import com._OK._OK.User.UserDto;
import com._OK._OK.User.UserMapper;
import com._OK._OK.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/amazon/rank")
@Tag(name="Rank", description = "Rank Api") //스웨거 태그 어노테이션임
public class RankingController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "랭킹 가져오기", description = "랭킹을 가져옵니다. 유저DTO 리스트를 순위별로 반환합니다.")
    public ResponseEntity<List<UserDto>> getRank(){
        List<User> userList = userService.getSortedUsers();
        List<UserDto> userDtoList = userList.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtoList);
    }

}
