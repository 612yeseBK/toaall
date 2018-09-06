package com.expect.admin.data.pojo.TransPerson;


import com.expect.admin.data.dataobject.TransPerRecord;
import com.expect.admin.utils.DateUtil;

import java.util.*;

/**
 * description:
 * Created by gaoyw on 2018/5/28.
 */
public class Front_TransRec {
    private String id;
    private String clsj;//处理时间
    private String userName;
    private String message;//处理意见
    private String cljg;//处理结果
    private String clnrid;//处理内容的id（所处理的东西的id）
    private String dyjd;//对应的流程节点

    public static List<Front_TransRec> convert(Set<TransPerRecord> trs){
        List<Front_TransRec> front_transRecs = new ArrayList<>();
        Front_TransRec front_transRec = new Front_TransRec();
        for (TransPerRecord transPerRecord : trs){
            front_transRec.setId(transPerRecord.getId());
            front_transRec.setCljg(transPerRecord.getCljg());
            front_transRec.setClsj(transPerRecord.getClsj());
            front_transRec.setMessage(transPerRecord.getMessage());
            front_transRec.setUserName(transPerRecord.getUser().getUsername());
            front_transRecs.add(front_transRec);
            front_transRec.setDyjd(transPerRecord.getWfPoint().getName());
            front_transRec = new Front_TransRec();
        }
        Collections.sort(front_transRecs, new Comparator<Front_TransRec>() {
            @Override
            public int compare(Front_TransRec o1, Front_TransRec o2) {
                String o1Date = DateUtil.format(DateUtil.parse(o1.getClsj(),DateUtil.fullFormat),DateUtil.longFormat);
                String o2Date = DateUtil.format(DateUtil.parse(o2.getClsj(),DateUtil.fullFormat),DateUtil.longFormat);
                return o1Date.compareTo(o2Date);
            }
        });
        return front_transRecs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClsj() {
        return clsj;
    }

    public void setClsj(String clsj) {
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
