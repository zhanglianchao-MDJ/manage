package net.chenlin.dp.common.support.interceptor;

import net.chenlin.dp.common.annotation.RestAnon;
import net.chenlin.dp.common.constant.RestApiConstant;
import net.chenlin.dp.common.utils.JSONUtils;
import net.chenlin.dp.common.utils.SpringContextUtils;
import net.chenlin.dp.common.utils.TokenUtils;
import net.chenlin.dp.common.utils.WebUtils;
import net.chenlin.dp.modules.sys.entity.SysUserEntity;
import net.chenlin.dp.modules.sys.entity.SysUserTokenEntity;
import net.chenlin.dp.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * rest api拦截器
 * @author zcl<yczclcn@163.com>
 */
@DependsOn("springContextUtils")
public class RestApiInterceptor extends HandlerInterceptorAdapter {

    private SysUserService userService = (SysUserService) SpringContextUtils.getBean("sysUserService");

    /**
     * 拦截
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 静态资源请求拦截
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        // 有RestAnon注解的方法不拦截
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(RestAnon.class)) {
                return true;
            }
        }
        return checkToken(request, response);
    }

    /**
     * token校验
     * @param request
     * @param response
     * @return
     */
    private boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        // 登录 或 有效状态校验 请求直接通过
        String requestPath = request.getServletPath();
        if (RestApiConstant.AUTH_REQUEST.equals(requestPath) || RestApiConstant.AUTH_CHECK.equals(requestPath)) {
            return true;
        }
        // 校验请求是否包含验证信息
        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            WebUtils.write(response, JSONUtils.beanToJson(RestApiConstant.TokenErrorEnum.TOKEN_NOT_FOUND.getResp()));
            return false;
        }
        // token校验
        SysUserTokenEntity sysUserTokenEntity = userService.getUserTokenByToken(token);
        if (sysUserTokenEntity == null) {
            WebUtils.write(response, JSONUtils.beanToJson(RestApiConstant.TokenErrorEnum.TOKEN_INVALID.getResp()));
            return false;
        }
        // token过期
        if (TokenUtils.isExpired(sysUserTokenEntity.getGmtExpire())) {
            WebUtils.write(response, JSONUtils.beanToJson(RestApiConstant.TokenErrorEnum.TOKEN_EXPIRED.getResp()));
            return false;
        }
        // 用户校验
        SysUserEntity sysUserEntity = userService.getUserByIdForToken(sysUserTokenEntity.getUserId());
        if (sysUserEntity.getStatus() == 0) {
            WebUtils.write(response, JSONUtils.beanToJson(RestApiConstant.TokenErrorEnum.USER_DISABLE.getResp()));
            return false;
        }
        return true;
    }

    /**
     * 获取token
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        // 请求头token
        String token = request.getHeader(RestApiConstant.AUTH_TOKEN);
        if (StringUtils.isBlank(token)) {
            // 请求参数token
            return request.getParameter(RestApiConstant.AUTH_TOKEN);
        }
        return token;
    }

}
