package com.imooc.controller;


import com.imooc.domain.ResultBean;
import com.imooc.service.ResultBeanService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class StatApp {


    @Autowired
    ResultBeanService resultBeanService;

    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public ModelAndView map() {

        return new ModelAndView("map");
    }


    @RequestMapping(value = "/map_stat", method = RequestMethod.GET)
    public ModelAndView map_stat() {

        ModelAndView view = new ModelAndView("mapstat");
        List<ResultBean> results = resultBeanService.query();

        JSONArray jsonArray = JSONArray.fromObject(results);

        System.out.println(jsonArray);


        // 如何把我们从后台查询到的数据以json的方式返回给前台页面
        view.addObject("data_json", jsonArray);

        return view;
    }

}
