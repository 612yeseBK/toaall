package com.expect.admin.web;

import com.expect.admin.config.Settings;
import com.expect.admin.exception.BaseAppException;
import com.expect.admin.service.*;
import com.expect.admin.service.vo.*;
import com.expect.admin.service.vo.component.FileResultVo;
import com.expect.admin.service.vo.component.ResultVo;
import com.expect.admin.utils.JsonResult;
import com.expect.admin.utils.MyResponseBuilder;
import com.expect.admin.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/traveling")
public class TravelingController {
    private final Logger log = LoggerFactory.getLogger(TravelingController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private Settings settings;
    @Autowired
    private TravelingService travelingService;
    @Autowired
    private RoleJdgxbGxbService roleJdgxbGxbService;
    @Autowired
    private LcService lcService;
    @Autowired
    private RoleService roleService;

    private final String viewName = "admin/traveling/";

    /**
     * 出差申请
     * @return
     */
    @RequestMapping(value = "/addTraveling", method = RequestMethod.GET)
    public ModelAndView addTraveling(){
        TravelingVo travelingVo = new TravelingVo();
        UserVo userVo = userService.getLoginUser();
        travelingVo.setUserName(userVo.getFullName());
        ModelAndView mv = new ModelAndView(viewName + "t_apply");
        mv.addObject("travelingVo", travelingVo);
        return mv;
    }

    /**
     * 申请记录
     */
    @RequestMapping(value = "/sqjl", method = RequestMethod.GET)
    public ModelAndView sqjl(@RequestParam(name = "lx", required = false)String lx){
        if(StringUtil.isBlank(lx)) lx = "wtj";
        ModelAndView modelAndView = new ModelAndView(viewName + "t_apply_record");
        UserVo userVo = userService.getLoginUser();
        List<RoleJdgxbGxbVo> condition = roleJdgxbGxbService.getCondition("cc");
        List<TravelingVo> travelingVoList = new ArrayList<>();
        if(condition.size() == 0){
            travelingVoList = travelingService.getTravelingByUserIdAndCondition(userVo.getId(), "S", lx);
        }else{
            for (RoleJdgxbGxbVo roleJdgxbGxbVo:condition){
                List<TravelingVo> travelingVos = travelingService.getTravelingByUserIdAndCondition(userVo.getId(), roleJdgxbGxbVo.getJdId(), lx);
                travelingVoList.addAll(travelingVos);
            }
        }
        modelAndView.addObject("travelingVoList", travelingVoList);
        return modelAndView;
    }

    /**
     * 申请记录详情(可编辑)
     */
    @RequestMapping("/sqjlxqE")
    public ModelAndView sqjlxqE(@RequestParam(name = "id", required = true)String travelingId) {
        ModelAndView modelAndView = new ModelAndView(viewName + "t_apply_recordDetail");
        TravelingVo travelingVo = travelingService.getTravelingById(travelingId);
        modelAndView.addObject("travelingVo", travelingVo);
        return modelAndView;
    }

    /**
     * 申请记录详情(不可编辑)
     */
    @RequestMapping("/sqjlxqNE")
    public ModelAndView sqjlxqNE(@RequestParam(name = "id", required = true)String travelingId) {
        ModelAndView modelAndView = new ModelAndView(viewName + "t_apply_recordDetail_ne");
        TravelingVo travelingVo = travelingService.getTravelingById(travelingId);
        modelAndView.addObject("travelingVo", travelingVo);
        return modelAndView;
    }

    /**
     * 出差审批列表页面
     */
    @GetMapping(value = "/ccsp")
    public ModelAndView ccsp(@RequestParam(name = "lx", required = false)String lx) {
        UserVo userVo = userService.getLoginUser();
        if(StringUtil.isBlank(lx)) lx = "dsp";
        ModelAndView modelAndView = new ModelAndView(viewName + "t_approve");
        List<RoleJdgxbGxbVo> condition = roleJdgxbGxbService.getCondition("cc");
        if(condition == null) return modelAndView;
        List<TravelingVo> travelingVos = new ArrayList<>();
        String roleName ="";
        for (RoleJdgxbGxbVo roleJdgxbGxbVo:condition){
            List<TravelingVo> travelingVoList = travelingService.getTravelingByUserIdAndCondition(userVo.getId(),
                    roleJdgxbGxbVo.getJdId(), lx);
            travelingVos.addAll(travelingVoList);
            RoleVo roleVo = roleService.getRoleById(roleJdgxbGxbVo.getRoleId());
            roleName =roleName+" "+roleVo.getName();

        }
   //    modelAndView.addObject("xsth", sfxsTab(roleName, "yth"));
        modelAndView.addObject("roleName", roleName);

        modelAndView.addObject("travelingVoList", travelingVos);
        return modelAndView;
    }

    /**
     * 出差表审批查看详情
     * @return
     */
    @GetMapping(value = "/ccspckxq")
    public ModelAndView ccspckxq(@RequestParam(name = "id", required = true)String travelingId){
        ModelAndView modelAndView = new ModelAndView(viewName + "t_approveDetail");
        TravelingVo travelingVo = travelingService.getTravelingById(travelingId);
        UserVo userVo = userService.getLoginUser();
        modelAndView.addObject("travelingVo", travelingVo);
        modelAndView.addObject("userVo",userVo);
        return modelAndView;
    }

    /**
     * 保存、提交出差申请表
     * @param travelingVo
     * @param bczl
     * @param attachmentId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/saveTraveling", method = RequestMethod.POST)
    public void saveTraveling(TravelingVo travelingVo,
                            @RequestParam(name = "bczl", required = true)   String bczl,
                            @RequestParam(name = "fileId" ,required = false) String[] attachmentId,
                            HttpServletResponse response) throws IOException {
        if(!travelingCheck(travelingVo, response)) return;
        String message = StringUtil.equals(bczl, "tj") ? "出差申请提交" : "出差申请保存";
        Boolean result = false;
        try{
            result = travelingService.newTravelingSave(travelingVo, bczl, attachmentId);

        }catch(Exception e) {
            log.error("保存出差报错", e);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, message + "失败！").build());
        }
        if(result)
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, message + "成功！").build());
        else
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "相关流程没有启用，请联系相关系统维护人员！").build());
    }

    private boolean  travelingCheck(TravelingVo travelingVo, HttpServletResponse response) throws IOException {
        if(travelingVo == null) {
            log.error("试图保存空的出差表");
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "申请失败，出差申请内容为空！").build());
            return false;
        }
        return true;
    }

    @GetMapping(value = "/sqjlTab")
    public void sqjlTab(@RequestParam(name = "lx", required = false)String lx,
                        @RequestParam(name = "bz", required = false)String bz,
                        HttpServletResponse response) throws IOException{
        if(StringUtil.isBlank(lx)) lx = "wtj";
        List<TravelingVo> travelingVoList = new ArrayList<TravelingVo>();
        try{
            UserVo userVo = userService.getLoginUser();
            List<RoleJdgxbGxbVo> condition = roleJdgxbGxbService.getCondition("cc");

            //申请记录的待审批
            if(StringUtil.equals(bz, "sq") && StringUtil.equals(lx, "dsp")){
                List<TravelingVo> travelingVos = travelingService.getSqjlWspList(userVo.getId());
                travelingVoList.addAll(travelingVos);
                MyResponseBuilder.writeJsonResponse(response,
                        JsonResult.useDefault(true, "获取申请记录成功", travelingVoList).build());
                return;
            }

            //申请记录的已审批
            if(StringUtil.equals(bz, "sq") && StringUtil.equals(lx, "ysp")){
                travelingVoList = travelingService.getSqjlYspList(userVo.getId());
                MyResponseBuilder.writeJsonResponse(response,
                        JsonResult.useDefault(true, "获取申请记录成功", travelingVoList).build());
                return;
            } else{
                if(condition.size() == 0){
                    if(StringUtil.equals(bz, "sp") && StringUtil.equals(lx, "ysp"))
                        travelingVoList = travelingService.getCcspYspList(userVo.getId(),"ysp");
                    if(StringUtil.equals(bz, "sq") && StringUtil.equals(lx, "wtj"))
                        travelingVoList = travelingService.getTravelingByUserIdAndCondition(userVo.getId(), "S", lx);
                    if(StringUtil.equals(lx, "yth") ){//已撤回
                        travelingVoList = travelingService.getTravelingByUserIdAndCondition(userVo.getId(),"yth", lx);

                    }

                }else{
                    for(RoleJdgxbGxbVo roleJdgxbGxbVo : condition){
                        List<TravelingVo> travelingVos = travelingService.getTravelingByUserIdAndCondition(userVo.getId(),
                                roleJdgxbGxbVo.getJdId(), lx);
                        travelingVoList.addAll(travelingVos);
                    }
                }

            }

        }catch(Exception e){
            log.error("获取申请记录错误" + lx, e);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "获取申请记录出错").build());
        }
        MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "获取申请记录成功", travelingVoList).build());
    }

    /**
     * 以保存出差表的提交
     * @throws IOException
     *
     */
    @PostMapping("/submitWtj")
    public void submitWtj(String id,
                          @RequestParam(name = "fileId" ,required = false) String[] attachmentId,
                          HttpServletResponse response) throws IOException{

        try{
            TravelingVo travelingVo = travelingService.getTravelingById(id);
            if(travelingVo == null){
                MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "未找到该出差表").build());
                return;
            }
            String condition =  travelingVo.getCcshzt();
            if(condition.equals("S")){
                String lcbs = travelingService.getLcbs();
                if(lcbs.equals(""))
                    MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "相关流程没有启用，请联系相关系统维护人员！").build());
                travelingVo.setLcbs(lcbs);
                condition = lcService.getNextCondition(lcbs, condition);

            }
            String nextCondition = lcService.getNextCondition(travelingVo.getLcbs(), condition);
            travelingVo.setCcshzt(nextCondition);



            travelingService.updateTraveling(travelingVo, attachmentId);
            travelingService.addXzLcrz(travelingVo.getId(), "cc", condition);
        }catch(Exception e) {
            log.error("以保存出差表提交时报错", e);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "出差表提交出错，请重试").build());
        }
        MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "出差表提交完成").build());
    }

    /**
     * 以保存未提交的出差表的删除
     * @param id
     * @param response
     * @throws IOException
     */
    @PostMapping("/deleteWjt")
    public void deleteWjt(String id, HttpServletResponse response) throws IOException {
        try{
            if(StringUtil.isBlank(id)){
                MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "出差表删除失败， 要删除的出差表id为空").build());
                return;
            }
            travelingService.delete(id);
        }catch(Exception e) {
            log.error("以保存出差表提交时报错", e);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "出差表删除失败").build());
        }
        MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "出差表删除完成").build());
    }

    /**
     * 撤销出差表（只有出差申请人可以撤销）
     * @param response
     * @param travelingId 要撤销的出差表的id
     * @param revocationReason 撤销出差表理由（最多为100个字）
     * @throws IOException
     */
    @PostMapping("/revocationTraveling")
    public void revocationTraveling(HttpServletResponse response,
                                   @RequestParam(name = "id", required = true)String travelingId,
                                   @RequestParam(name = "revocationReason", required = true)String revocationReason) throws IOException {
        try{
            if(revocationReason.length() > 100){
                MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "撤销理由过长，最多100个字").build());
            }
            else travelingService.revocationTraveling(travelingId, revocationReason);
        }catch(BaseAppException be) {
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, be.getMessage()).build());
            log.error("撤销出差表是失败，出差表id为" + travelingId, be);
        }catch(Exception e) {
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "撤销出差表失败").build());
            log.error("撤销出差表是失败，出差表id为" + travelingId, e);
        }
        MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "撤销出差表成功").build());
    }

    /**
     * 流程审批
     * @param response
     * @param cljg
     * @param message
     * @param clnrid
     * @throws IOException
     */
    @RequestMapping(value = "/addLcrz", method = RequestMethod.POST)
    public void addLcrz(HttpServletResponse response,
                        @RequestParam(name = "cljg", required = true)String cljg,
                        @RequestParam(name = "yj", required = false) String message,
                        @RequestParam(name = "id", required = true)  String clnrid) throws IOException{
        //插入流程日志
        //判断审核是否通过 如果通过更新文件状态到下一个状态
        //如果不通过更新文件状态到退回状态，修改流程日志表中的审批记录以后不再显示退回状态之后的审批记录
        if(message == null) message  ="";
        try{
            travelingService.saveTravelingLcrz(cljg, message, clnrid, "ht");
        }catch(Exception e) {
            log.error("出差审核失败", e);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "出差表审核失败！").build());
        }
        MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "出差表审核成功！").build());

    }

    /**
     * 打印出差表单
     * @param travelingId
     * @return
     */
    @RequestMapping("/printTg")
    public ModelAndView printTg(@RequestParam(name = "id", required = true)String travelingId) {
        UserVo userVo = userService.getLoginUser();
        ModelAndView modelAndView = new ModelAndView(viewName + "t_apply_print");
        TravelingVo travelingVo = travelingService.getTravelingById(travelingId);
        travelingVo.setUserName(userVo.getFullName());
        //去除流程日志中第一个申请日志
        List<LcrzbVo> lcrzbVoList = travelingVo.getLcrzList();
        travelingVo.setLcrzList(lcrzbVoList.subList(1, lcrzbVoList.size()));
        modelAndView.addObject("travelingVo", travelingVo);
        return modelAndView;
    }


//    /**
//     * 打印表单
//     * @param travelingId
//     * @param response
//     * @throws IOException
//     */
//    @PostMapping("/print")
//    public void print(@RequestParam(name = "id", required = true) String travelingId,
//                     @RequestParam(name = "dyrq", required=true) String dyrq,
//                     HttpServletResponse response) throws IOException{
//        try{
//            TravelingVo travelingVo = travelingService.getTravelingById(travelingId);
//            if(travelingVo == null){
//                MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "未找到该出差表").build());
//                return;
//            }
//
//            travelingVo.setDyrq(dyrq);
//            travelingService.updateTraveling(travelingVo, null);
//        }
//        catch(Exception e){
//            this.log.error("打印出差表报错");
//            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "打印出差表报错").build());
//        }
//        MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "打印出差表成功").build());
//
//    }

    /**
     * 会议附件上传
     * @return
     */
    @RequestMapping(value = "/uploadTravelingAttachment", method = RequestMethod.POST)
    @ResponseBody
    public ResultVo upload(MultipartFile files, HttpServletRequest request) {
        String path = settings.getAttachmentPath();
        FileResultVo frv = attachmentService.save(files, path);
        return frv;
    }

    /**
     * @param attachmentId
     * @param travelingId
     * @param reponse
     * @return
     */
    @GetMapping("/travelingAttachmentDownload")
    public String travelingAttachmentDownload(
            @RequestParam(name = "attachmentId", required = true)String attachmentId,
            @RequestParam(name = "travelingId", required = true)String travelingId,
            HttpServletResponse reponse) {
        TravelingVo travelingVo = travelingService.getTravelingById(travelingId);
        if(travelingVo != null){
            return "forward:/admin/attachment/download?id=" + attachmentId;
        }
        return "forward:/admin/attachment/downloadAttachmentAsPdf?id=" + attachmentId;
    }
}
