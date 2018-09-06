package com.expect.admin.web;

import com.expect.admin.data.dataobject.Lcb;
import com.expect.admin.data.dataobject.Lcjdb;
import com.expect.admin.service.LcService;
import com.expect.admin.service.ProcessConfigurationService;
import com.expect.admin.service.RoleService;
import com.expect.admin.service.UserService;
import com.expect.admin.service.vo.LcVo;
import com.expect.admin.service.vo.ProcessDataVo;
import com.expect.admin.service.vo.RoleVo;
import com.expect.admin.service.vo.UserVo;
import com.expect.admin.service.vo.component.ResultVo;

import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

/**
 * Created by qifeng on 17/11/14.
 */
@Controller
@RequestMapping(value = "admin/processConfiguration")
public class ProcessConfigutationController {
    private final Logger log = LoggerFactory.getLogger(ProcessConfigutationController.class);
    private final String viewName = "admin/system/processConfiguration/";
    @Autowired
    private ProcessConfigurationService processConfigurationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LcService lcService;

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public ModelAndView processConfiguration(){
        ModelAndView mv = new ModelAndView(viewName+"configuration");
        return mv;
    }
    @RequestMapping(value = "save")
    @ResponseBody
    public ResultVo save(LcVo lcVo,String processData){
        JSONArray jsonArray = JSONArray.fromObject(processData);
        List<ProcessDataVo> list = (List<ProcessDataVo>)JSONArray.toCollection(jsonArray, ProcessDataVo.class);
        ResultVo resultVo = new ResultVo();
        try{
             resultVo = processConfigurationService.save(lcVo, list);
        }catch(Exception e){
            log.error("保存流程信息报错", e);
        }
        return resultVo;
    }

    @RequestMapping(value = "getRoles")
    @ResponseBody
    public ResultVo getRoles(String lclb){
        ResultVo resultVo = new ResultVo();
        if (lclb == null) {
            return  resultVo;
        }

        List<RoleVo> roles = roleService.getRolesByLclb(lclb);
        resultVo.setObj(roles);
        resultVo.setResult(true);
        return resultVo;
    }

    @RequestMapping(value = "getLclx")
    @ResponseBody
    public ResultVo getLclx(){
        ResultVo resultVo = new ResultVo();

        return resultVo;
    }

}
