package com.expect.admin.web;

import com.expect.admin.data.dao.LcjdgxbRepository;
import com.expect.admin.data.dataobject.Lcb;
import com.expect.admin.data.dataobject.Lcjdgxb;
import com.expect.admin.service.HytzService;
import com.expect.admin.service.MeetingService;
import com.expect.admin.service.ProcessRecordService;
import com.expect.admin.service.vo.HytzVo;
import com.expect.admin.service.vo.LcVo;
import com.expect.admin.service.vo.MeetingVo;
import com.expect.admin.service.vo.ProcessVo;
import com.expect.admin.service.vo.component.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qifeng on 17/12/8.
 */
@Controller
@RequestMapping(value = "admin/processRecord")
public class ProcessRecordController {
    private final String viewName = "admin/system/processRecord/";

    @Autowired
    ProcessRecordService processRecordService;


    @RequestMapping(value = "/Record", method = RequestMethod.GET)
    public ModelAndView processRecord(){
        ModelAndView modelAndView = new ModelAndView(viewName + "processRecord1");
        List<Lcb> lcbList = processRecordService.getAllLcb();
        modelAndView.addObject("lcbList", lcbList);
        return modelAndView;
    }

    @GetMapping(value = "/seeLcb")
    public ModelAndView seeLcb(@RequestParam(name = "id", required = true) String lcbId){
        ModelAndView modelAndView = new ModelAndView(viewName + "processXq");
        LcVo lcVo = processRecordService.getLcb(lcbId);
        modelAndView.addObject("lcVo", lcVo);
        return modelAndView;
    }

//    @RequestMapping(value = "seeUsers")
//    @ResponseBody
//    public ResultVo seeUsers(String roleId){
//        ResultVo resultVo = new ResultVo();
//        ArrayList<String> userList = processRecordService.getUsers(roleId);
//        if(userList != null && userList.size() > 0){
//            resultVo.setResult(true);
//            resultVo.setObj(userList);
//        }
//        return resultVo;
//    }

    @RequestMapping(value = "closeLcb")
    @ResponseBody
    public ResultVo closeLcb(String lcid){
        ResultVo resultVo =  processRecordService.closeLcb(lcid);
        return resultVo;
    }

    @RequestMapping(value = "openLcb")
    @ResponseBody
    public ResultVo openLcb(String lcid){
        ResultVo resultVo =  processRecordService.openLcb(lcid);
        return resultVo;
    }


    @RequestMapping(value = "getProcessVoList")
    @ResponseBody
    public ResultVo getProcessVoList(){
        ResultVo resultVo = new ResultVo();
        List<ProcessVo> processVoList = processRecordService.getContractProcess();
        for (int i=0;i<processVoList.size();i++){
            processVoList.get(i).setProcessId(String.valueOf(i+1));
        }
        if (processVoList !=null && processVoList.size() >0){
            resultVo.setResult(true);
            resultVo.setObj(processVoList);
        }
        return resultVo;
    }

    @RequestMapping(value ="deleteProcess")
    @ResponseBody
    public ResultVo deleteProcess(String processId){
        ResultVo resultVo = processRecordService.deleteProcess(processId);
        return resultVo;
    }

}
