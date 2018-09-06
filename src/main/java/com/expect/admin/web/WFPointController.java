package com.expect.admin.web;

import com.expect.admin.data.dataobject.WorkFlow;
import com.expect.admin.data.pojo.Addwf;
import com.expect.admin.data.pojo.Addwfp;
import com.expect.admin.data.pojo.UpdwfpU;
import com.expect.admin.service.WFPointService;
import com.expect.admin.service.WorkFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
@Controller
@RequestMapping("/wfp")
public class WFPointController {
    private final Logger log = LoggerFactory.getLogger(WFPointController.class);
    @Autowired
    WorkFlowService workFlowService;
    @Autowired
    WFPointService wfPointService;


    /**
     * 为各个流程点更新人员,新增也在这儿
     * @param upds
     */
    @RequestMapping("/updateUsers")
    public void updateUsers(List<UpdwfpU> upds){
        wfPointService.updatewfp(upds);
    }



}
