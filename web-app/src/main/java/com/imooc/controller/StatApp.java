package com.imooc.controller;


import com.imooc.domain.ResultBean;
import com.imooc.service.ResultBeanService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class StatApp {


    @Autowired
    ResultBeanService resultBeanService;

    @RequestMapping(value = "/get_data.js", method = RequestMethod.GET)
    public ModelAndView get_data() {
        return new ModelAndView("get_data");
    }

    @RequestMapping(value = "/map_sent", method = RequestMethod.GET)
    public ModelAndView map() {

        ModelAndView view = new ModelAndView("map_sent");

        List<ResultBean> results = resultBeanService.query(2);

        JSONArray jsonArray = JSONArray.fromObject(results);
        view.addObject("data_points", jsonArray);

        return view;
    }

    @RequestMapping(value = "/maps", method = RequestMethod.GET)
    public ModelAndView maps() {

        ModelAndView view = new ModelAndView("maps");

        List<ResultBean> results = resultBeanService.query(1);

        JSONArray jsonArray = JSONArray.fromObject(results);

        //System.out.println(jsonArray);

        view.addObject("data_json", jsonArray);

        return view;
    }


    @RequestMapping(value = "/map_stat", method = RequestMethod.GET)
    public ModelAndView map_stat() {

        ModelAndView view = new ModelAndView("mapstat");
        List<ResultBean> results = resultBeanService.query(0);

        JSONArray jsonArray = JSONArray.fromObject(results);

        //System.out.println(jsonArray);


        // 如何把我们从后台查询到的数据以json的方式返回给前台页面
        view.addObject("data_json", jsonArray);

        return view;
    }

}
