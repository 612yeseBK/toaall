package com.expect.admin.factory.impl;

import com.expect.admin.data.dao.UserRepository;
import com.expect.admin.data.dataobject.User;
import com.expect.admin.factory.WordXmlFactory;
import com.expect.admin.service.LcrzbService;
import com.expect.admin.service.TravelingService;
import com.expect.admin.service.UserService;
import com.expect.admin.service.vo.AttachmentVo;
import com.expect.admin.service.vo.LcrzbVo;
import com.expect.admin.service.vo.TravelingVo;
import com.expect.admin.utils.WordXmlUtil;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("jtrsFactory")
public class JtrsFactory implements WordXmlFactory {
    @Autowired
    TravelingService travelingService;
    @Autowired
    LcrzbService lcrzbService;
    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;

    public byte[] create(String wjid) throws IOException, TemplateException {
        Map<String, Object> dataMap = new HashMap();
        TravelingVo travelingVo = travelingService.getTravelingById(wjid);
        if(travelingVo == null) return null;
        List<LcrzbVo> lcrzbVoList = travelingVo.getLcrzList();

        setJbxx(travelingVo, dataMap);
        setLcxx(lcrzbVoList, dataMap);

        byte[] content = WordXmlUtil.create(dataMap, "jtcc.ftl");
        return content;
    }

    public String getFileName(String wjid) {
        TravelingVo travelingVo = this.travelingService.getTravelingById(wjid);
        return travelingVo.getName() + "出差申请表.doc";
    }

    public void setLcxx(List<LcrzbVo> lcrzbVoList, Map<String, Object> dataMap){
        if(lcrzbVoList == null || lcrzbVoList.isEmpty()) return;

        List<Map<String, Object>> approveList = new ArrayList<Map<String, Object>>();
        for(int i = 1; i < lcrzbVoList.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            LcrzbVo lcrzbVo = lcrzbVoList.get(i);
            map.put("lcjd", lcrzbVo.getLcjd());
            map.put("cljg", lcrzbVo.getCljg());
            map.put("message", lcrzbVo.getMessage());
            String clsj = lcrzbVo.getClsj().split(" ")[0];
            map.put("clsj", clsj);
            User user = lcrzbVo.getUser();
            String imagePath = getImageStrByUser(user);
            if (!imagePath.equals("签名附件没有上传")){
                map.put("imagePath",imagePath);
            }
            map.get("imagePath");
            approveList.add(map);
        }
        dataMap.put("approveList", approveList);
    }

    /**
     * 设置基本信
     * @param travelingVo
     * @param dataMap
     */
    private void setJbxx(TravelingVo travelingVo, Map<String, Object> dataMap){
        dataMap.put("name", travelingVo.getName());
        dataMap.put("sex", travelingVo.getSex());
        dataMap.put("birth", travelingVo.getBirth());
        dataMap.put("staffId", travelingVo.getStaffId());
        dataMap.put("position", travelingVo.getPosition());
        dataMap.put("workUnit", travelingVo.getWorkUnit());
        dataMap.put("travelingTime", travelingVo.getTravelingTime());
        dataMap.put("reasonNumber", travelingVo.getReasonNumber());
    }

    public String getImageStrByUser(User user){
        List<AttachmentVo> attachmentVos = userService.getQmAttachmentByUser(user);
        String imgFile="";
        int size=attachmentVos.size();
        if (attachmentVos !=null && attachmentVos.size()>0){
            imgFile=attachmentVos.get(size-1).getPath()+"/"+attachmentVos.get(size-1).getId();
        }
        if(imgFile==""){
            return "签名附件没有上传";
        }
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//将图片路径用Base64编码
    }



}
