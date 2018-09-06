package com.expect.admin.service.vo;

import com.expect.admin.data.dataobject.TransPerRecord;
import com.expect.admin.data.pojo.TransPerson.Front_TransRec;
import com.expect.admin.service.convertor.UserConvertor;
import com.expect.admin.utils.DateUtil;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public class TransPerRecordVo {
    private String id;
    private UserVo userVo;//处理人
    private Date clsj;//处理时间
    private String message;//处理意见
    private String cljg;//处理结果
    private String clnrid;//处理内容的id（所处理的东西的id）
    private String dyjd;//对应的流程节点

    public TransPerRecordVo(){}

    /**
     * 将do转成vo
     * @param transPerRecord
     * @return
     */
    public static TransPerRecordVo convert(TransPerRecord transPerRecord){
        TransPerRecordVo transPerRecordVo = new TransPerRecordVo();
        BeanUtils.copyProperties(transPerRecord,transPerRecordVo);
        return transPerRecordVo;
    }


    public static List<TransPerRecordVo> convert(Set<TransPerRecord> trs){
        List<TransPerRecordVo> transPerRecordVos = new ArrayList<>();
        TransPerRecordVo transPerRecordVo = new TransPerRecordVo();
        for (TransPerRecord transPerRecord : trs){
            transPerRecordVo.setId(transPerRecord.getId());
            transPerRecordVo.setCljg(transPerRecord.getCljg());
            transPerRecordVo.setClsj(DateUtil.parse(transPerRecord.getClsj(),DateUtil.fullFormat));
            transPerRecordVo.setMessage(transPerRecord.getMessage());
            transPerRecordVo.setUserVo(UserConvertor.convert(transPerRecord.getUser()));
            transPerRecordVos.add(transPerRecordVo);
            transPerRecordVo.setDyjd(transPerRecord.getWfPoint().getName());
            transPerRecordVo = new TransPerRecordVo();
        }
        Collections.sort(transPerRecordVos, new Comparator<TransPerRecordVo>() {
            @Override
            public int compare(TransPerRecordVo o1, TransPerRecordVo o2) {
                String o1Date = DateUtil.format(o1.getClsj(),DateUtil.longFormat);
                String o2Date = DateUtil.format(o2.getClsj(),DateUtil.longFormat);
                return o1Date.compareTo(o2Date);
            }
        });
        return transPerRecordVos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserVo getUserVo() {
        return userVo;
    }

    public void setUserVo(UserVo userVo) {
        this.userVo = userVo;
    }

    public Date getClsj() {
        return clsj;
    }

    public void setClsj(Date clsj) {
        this.clsj = clsj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCljg() {
        return cljg;
    }

    public void setCljg(String cljg) {
        this.cljg = cljg;
    }

    public String getClnrid() {
        return clnrid;
    }

    public void setClnrid(String clnrid) {
        this.clnrid = clnrid;
    }

    public String getDyjd() {
        return dyjd;
    }
    public void setDyjd(String dyjd) {
        this.dyjd = dyjd;
    }
}
