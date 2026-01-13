package com.example.complog.controller.background;

import com.example.complog.response.AjaxResult;
import com.example.complog.response.PageResult;
import com.example.complog.service.UserService;
import com.example.complog.vo.UserAddVo;
import com.example.complog.vo.UserListVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public AjaxResult<Boolean> add(@RequestBody UserAddVo userAddVo) {
        return AjaxResult.success(userService.add(userAddVo));
    }

    @GetMapping
    public AjaxResult<PageResult<UserListVo>> list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) Long deptId) {
        return AjaxResult.success(userService.list(pageNum, pageSize, name, deptId));
    }
}
