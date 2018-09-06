package com.expect.admin.factory.impl;

import com.expect.admin.data.dao.UserRepository;
import com.expect.admin.data.dataobject.TransPerRecord;
import com.expect.admin.data.dataobject.TransferPersonnel;
import com.expect.admin.data.dataobject.User;
import com.expect.admin.data.pojo.TransPerson.Front_TransRec;
import com.expect.admin.data.pojo.TransPerson.TransPrint;
import com.expect.admin.factory.WordXmlFactory;
import com.expect.admin.service.*;
import com.expect.admin.service.vo.*;
import com.expect.admin.utils.DateUtil;
import com.expect.admin.utils.StringUtil;
import com.expect.admin.utils.WordXmlUtil;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by gaoyw on 2018/6/15.
 */
@Service("TransPerFactory")
public class TransPerFactory{
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TransferPersonnelService tpService;


    public byte[] create(String wjid,String birthday) throws IOException, TemplateException {
        Map<String, Object> dataMap = new HashMap<>();
        TransPrint transPrint = tpService.getTransPrintFromtpId(wjid);
        getDataMap(transPrint, dataMap);
        dataMap.put("birthday",birthday);

        byte[] content = WordXmlUtil.create(dataMap, "transper.ftl");
        return content;
    }

    public String getFileName(String wjid) {
        TransferPersonnel transferPersonnel = tpService.findById(wjid);
        return transferPersonnel.getApplyTime() + ".doc";
    }

    public void getDataMap(TransPrint transPrint, Map<String,Object> dataMap){
        TransferPersonnel tp = tpService.findById(transPrint.getId());
        List<TransPerRecordVo> transPerRecordVos = TransPerRecordVo.convert(tp.getTransPerRecords());
        String records="<w:br/>";
        int i = 0;
        for (TransPerRecordVo tprVo:transPerRecordVos){
            if (!tprVo.getCljg().equals("保存")) {
                //保存的记录不需要展示
                records = records + (i == 0 ? "申请人: " : "审核人: ") + tprVo.getUserVo().getUsername()+"("+ tprVo.getUserVo().getFullName()+")"  + " "
                        + " 处理时间：" + DateUtil.format(tprVo.getClsj(),DateUtil.zbFormat) + "<w:br/>";
                if (!tprVo.getCljg().equals("新增")) {
                    //新增记录不需要有处理意见
                    records = records + "处理意见：" + (StringUtil.isEmpty(tprVo.getMessage()) ? "通过" : tprVo.getMessage())
                            + "<w:br/>";
                }
                records = records +"签名："
                        + "<w:pict><w:binData w:name=\"wordml://02000002" + i + ".jpg\" xml:space=\"preserve\">"
                        + getImageStrByUserId(tprVo.getUserVo().getId())
                        + "</w:binData>"
                        + "<v:shape id=\"_x0000_i1025" + i + "\" type=\"#_x0000_t75\" style=\"width:68.4pt;height:35.35pt\">"
                        + "<v:imagedata src=\"wordml://02000002" + i + ".jpg\" o:title=\"ceshi23\"/></v:shape></w:pict>" + "<w:br/><w:br/>";
                i++;
            }
        }
//        String imagePath=getImageStrByUserId(tp.getApplicant().getId());
        dataMap.put("name", transPrint.getName());
        dataMap.put("sex", transPrint.getSex());
//        dataMap.put("birthday", "19950101");
        dataMap.put("staffNum", transPrint.getStaffNum());
        dataMap.put("job", transPrint.getUserJob());
        dataMap.put("company", transPrint.getCompany());
        dataMap.put("timePeriod", transPrint.getTimePeriod());
        dataMap.put("toDepartment",transPrint.getToDepartment());
        dataMap.put("jobKind",transPrint.getJobKind());
        dataMap.put("reason", transPrint.getReason());
        dataMap.put("number",transPrint.getNumber());
        dataMap.put("records",records);
//        if (!imagePath.equals("签名附件没有上传")){
//            dataMap.put("image",imagePath);
//        }

    }


    public String getImageStrByUserId(String id){
        User user = userRepository.findOne(id);
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
