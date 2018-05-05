package horizon.taglib.security;

import horizon.taglib.enums.ResultMessage;
import horizon.taglib.model.User;
import horizon.taglib.service.UserService;
import horizon.taglib.service.impl.UserServiceImpl;
import horizon.taglib.vo.ResultVO;
import horizon.taglib.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

// 自定义身份认证验证组件
@Component
class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        List<Object> list = userService.login(name, password);
        ResultMessage re = (ResultMessage) list.get(0);
        if (re == ResultMessage.SUCCESS) {
            Authentication auth = new UsernamePasswordAuthenticationToken(name, password, Collections.emptyList());
            return auth;
        } else if (re == ResultMessage.NOT_EXIST) {
            throw new BadCredentialsException("用户不存在");
        } else {
            throw new BadCredentialsException("密码错误");
        }
//        if (name.equals("admin") && password.equals("123456")) {
//
//            // 生成令牌
//            Authentication auth = new UsernamePasswordAuthenticationToken(name, password, Collections.emptyList());
//            return auth;
//        }else {
//            throw new BadCredentialsException("密码错误~");
//        }
    }

    // 是否可以提供输入类型的认证服务
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}