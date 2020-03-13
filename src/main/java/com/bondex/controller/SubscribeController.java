package com.bondex.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.Common;
import com.bondex.entity.Subscribe;
import com.bondex.entity.page.Datagrid;
import com.bondex.util.GsonUtil;
import com.bondex.util.RandomUtil;

/**
 * 
 * @author bondex_public
 * 订阅
 */
@RestController
@RequestMapping("subscribe")
public class SubscribeController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	public static Map<String, List<Subscribe>> subscribeMap = new HashMap<>();

	@RequestMapping("add")
	public String add(Subscribe subscribe, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		if (subscribe.getSrMawb() == null || subscribe.getSrMawb().trim().equals("")) {
			return "<b style='color:red;'>主单号为必填项！</b>";
		}
		if (subscribe.getSrMawb().trim().length() < 11) {
			return "<b style='color:red;'>主单号长度不足11位！</b>";
		}
		String uuid = RandomUtil.UUID32();
		String opid = (String) session.getAttribute(Common.Session_thisOpid);
		String mawbs[] = subscribe.getSrMawb().split("\n");
		StringBuffer buffer = null;
		for (String mawb : mawbs) {
			mawb = mawb.trim();
			String tmp = "";
			if (!mawb.equals("")) {
				tmp = Pattern.compile("[^0-9]").matcher(mawb).replaceAll("");// 提取数字
				String head = tmp.substring(0, 3);
				String body = tmp.substring(3, 11);
				mawb = head + "-" + body;
			}
			// 查询是否已经订阅
			List<Subscribe> subscribes = jdbcTemplate.query("select * from subscribe where sr_mawb = ? and sr_state= '0'", new Object[] { mawb }, new BeanPropertyRowMapper<Subscribe>(Subscribe.class));
			for (Subscribe subscribe2 : subscribes) {
				if (subscribe2.getSrOpid().equals(opid)) {
					if (buffer == null) {
						buffer = new StringBuffer();
					}
					buffer.append("<b style='color:red;'>" + subscribe2.getSrMawb() + "</b>	已由<b style='color:red;'>	" + subscribe2.getSrEmail() + "</b>  订阅中<br/>");
				}
			}
			if (buffer == null) {
				int id = jdbcTemplate.update("INSERT INTO subscribe(sr_user,sr_mawb,sr_hawb,sr_email,sr_opid,batch_id) VALUES(?,?,?,?,?,?) ", new Object[] { subscribe.getSrUser(), mawb, subscribe.getSrHawb(), subscribe.getSrEmail(), opid, uuid });
				if (id < 0) {
					return "false";
				}
			}

		}
		if (buffer != null) {
			return buffer.toString();
		}
		return "true";
	}

	@RequestMapping("all")
	public String all(String page, String rows, HttpServletRequest request) {
		HttpSession session = request.getSession();
		List<Subscribe> subscribes = jdbcTemplate.query("select * from subscribe where sr_opid = ?   limit " + (Integer.valueOf(page) - 1) * Integer.valueOf(rows) + "," + rows + "", new Object[] { session.getAttribute("thisOpid") }, new BeanPropertyRowMapper<Subscribe>(Subscribe.class));
		Long total = jdbcTemplate.queryForObject("select count(1) from subscribe where sr_opid = ? ", new Object[] { session.getAttribute("thisOpid") }, long.class);
		Datagrid<Subscribe> datagrid = new Datagrid<>();
		datagrid.setTotal(total);
		datagrid.setRows(subscribes);
		return GsonUtil.GsonString(datagrid);
	}

	@RequestMapping("redirect/{mawbs:.+}")
	public ModelAndView emailRedirect(@PathVariable String mawbs, ModelAndView andView) {
		List<Subscribe> jsonResults = GsonUtil.GsonToList(mawbs, Subscribe.class);
		subscribeMap.put(jsonResults.get(0).getSrOpid(), jsonResults);
		andView.setViewName("redirect:/login");
		return andView;
	}

	@RequestMapping("test/{id}/{name}")
	public void redis(@PathVariable String id, @PathVariable String name) {
		System.out.println(id);
		System.out.println(name);
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		ValueOperations<String, String> stringOperations = stringRedisTemplate.opsForValue();
		operations.set("test", "1111111", 200, TimeUnit.SECONDS);
		System.out.println("redisTemplate" + operations.get("test"));
		stringOperations.set("test2", "222222", 200, TimeUnit.SECONDS);
		System.out.println("stringRedisTemplate" + operations.get("test"));
	}

}
