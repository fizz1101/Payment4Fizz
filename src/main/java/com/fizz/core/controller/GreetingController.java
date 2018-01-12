package com.fizz.core.controller;

import com.fizz.common.controller.BasicController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Created by ly on 2017/7/10.
 */
@Controller
public class GreetingController extends BasicController{

	@RequestMapping({
			"/",
			"index"
	})
	public String index()
	{
		return "payment.html";
	}

}
