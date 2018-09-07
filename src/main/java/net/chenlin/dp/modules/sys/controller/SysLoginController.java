package net.chenlin.dp.modules.sys.controller;

import net.chenlin.dp.common.annotation.SysLog;
import net.chenlin.dp.common.utils.MD5Utils;
import net.chenlin.dp.common.utils.ShiroUtils;
import net.chenlin.dp.modules.sys.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 用户controller
 * @author zcl<yczclcn@163.com>
 */
@Controller
public class SysLoginController extends AbstractController {

	@Autowired
	private SysUserService sysUserService;

	/**
	 * 跳转登录页面
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String toLogin() {
		if (ShiroUtils.isLogin() || ShiroUtils.getUserEntity() != null) {
			return redirect("/");
		}
		return html("/login");
	}
	
	/**
	 * 登录
	 */
	@SysLog("登录")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(Model model) {
		String username = getParam("username").trim();
		String password = getParam("password").trim();
		try {
			UsernamePasswordToken token = new UsernamePasswordToken(username, MD5Utils.encrypt(username, password));
			ShiroUtils.getSubject().login(token);
			SecurityUtils.getSubject().getSession().setAttribute("sessionFlag", true);
			return redirect("/");
		} catch (UnknownAccountException | IncorrectCredentialsException | LockedAccountException e) {
			model.addAttribute("errorMsg", e.getMessage());
		} catch (AuthenticationException e) {
			model.addAttribute("errorMsg", "登录服务异常");
		}
		return html("/login");
	}

	/**
	 * 跳转后台控制台
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return html("/index");
	}
	
	/**
	 * 退出
	 */
	@SysLog("退出系统")
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout() {
		ShiroUtils.logout();
		return html("/login");
	}
	
}
