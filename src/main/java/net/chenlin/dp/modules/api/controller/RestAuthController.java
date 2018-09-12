package net.chenlin.dp.modules.api.controller;

import net.chenlin.dp.common.annotation.RestAnon;
import net.chenlin.dp.common.constant.RestApiConstant;
import net.chenlin.dp.common.entity.R;
import net.chenlin.dp.common.utils.MD5Utils;
import net.chenlin.dp.common.utils.TokenUtils;
import net.chenlin.dp.modules.sys.controller.AbstractController;
import net.chenlin.dp.modules.sys.entity.SysUserEntity;
import net.chenlin.dp.modules.sys.entity.SysUserTokenEntity;
import net.chenlin.dp.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * rest授权controller
 * @author zcl<yczclcn@163.com>
 */
@RestController
public class RestAuthController extends AbstractController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录授权校验
     * @return
     */
    @RequestMapping(RestApiConstant.AUTH_REQUEST)
    public R auth() {
        String username = getParam("username").trim();
        String password = getParam("password").trim();
        // 用户名为空
        if (StringUtils.isBlank(username)) {
            return RestApiConstant.TokenErrorEnum.USER_USERNAME_NULL.getResp();
        }
        // 密码为空
        if (StringUtils.isBlank(password)) {
            return RestApiConstant.TokenErrorEnum.USER_PASSWORD_NULL.getResp();
        }
        // 用户名不存在
        SysUserEntity sysUserEntity = sysUserService.getByUserName(username);
        if (sysUserEntity == null) {
            return RestApiConstant.TokenErrorEnum.USER_USERNAME_INVALID.getResp();
        }
        // 密码错误
        String checkPassword = MD5Utils.encrypt(username, password);
        if (!sysUserEntity.getPassword().equals(checkPassword)) {
            return RestApiConstant.TokenErrorEnum.USER_PASSWORD_INVALID.getResp();
        }
        // 用户被锁定
        if (sysUserEntity.getStatus() == 0) {
            return RestApiConstant.TokenErrorEnum.USER_DISABLE.getResp();
        }
        // 保存或者更新token
        String token = TokenUtils.generateValue();
        int count = sysUserService.saveOrUpdateToken(sysUserEntity.getUserId(), token);
        if (count > 0) {
            R success = RestApiConstant.TokenErrorEnum.TOKEN_ENABLE.getResp();
            success.put(RestApiConstant.AUTH_TOKEN, token);
            return success;
        }
        return RestApiConstant.TokenErrorEnum.USER_AUTH_ERROR.getResp();
    }

    /**
     * 异步校验token，用于接口异步校验登录状态
     * @return
     */
    @RequestMapping(RestApiConstant.AUTH_CHECK)
    public R authStatus() {
        String token = getParam(RestApiConstant.AUTH_TOKEN);
        // token为空
        if (StringUtils.isBlank(token)) {
            return RestApiConstant.TokenErrorEnum.TOKEN_NOT_FOUND.getResp();
        }
        SysUserTokenEntity sysUserTokenEntity = sysUserService.getUserTokenByToken(token);
        // 无效的token：token不存在
        if (sysUserTokenEntity == null) {
            return RestApiConstant.TokenErrorEnum.TOKEN_INVALID.getResp();
        }
        // 无效token：用户不存在
        SysUserEntity sysUserEntity = sysUserService.getUserByIdForToken(sysUserTokenEntity.getUserId());
        if (sysUserEntity == null) {
            return RestApiConstant.TokenErrorEnum.TOKEN_INVALID.getResp();
        }
        // token过期
        if (TokenUtils.isExpired(sysUserTokenEntity.getGmtExpire())) {
            return RestApiConstant.TokenErrorEnum.TOKEN_EXPIRED.getResp();
        }
        // 用户是否禁用
        if (sysUserEntity.getStatus() == 0) {
            return RestApiConstant.TokenErrorEnum.USER_DISABLE.getResp();
        }
        return RestApiConstant.TokenErrorEnum.TOKEN_ENABLE.getResp();
    }

    /**
     * 验证拦截
     * @return
     */
    @RequestMapping("/rest/testAuth")
    public String test() {
        return "auth token";
    }

    /**
     * 匿名调用：@RestAnon
     * @return
     */
    @RequestMapping("/rest/testAnon")
    @RestAnon
    public String testAnon() {
        return "rest anon";
    }

}
