package com.expect.admin.service;

import com.expect.admin.data.dao.*;
import com.expect.admin.data.dataobject.*;
import com.expect.admin.exception.BaseAppException;
import com.expect.admin.service.vo.AttachmentVo;
import com.expect.admin.service.vo.LcrzbVo;
import com.expect.admin.service.vo.TravelingVo;
import com.expect.admin.service.vo.UserVo;
import com.expect.admin.utils.DateUtil;
import com.expect.admin.utils.StringUtil;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TravelingService {

    /**
     * 撤销状态
     */
    private static final String REVOCATION_CONDITION = "revocation";

    //相关出差表的流程停用
    private static final String DEACTIVATION_CONDITION = "deactivation";

    @Autowired
    private LcrzbService lcrzbService;
    @Autowired
    private UserService userService;
    @Autowired
    private LcService lcService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private TravelingRepository travelingRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LcbRepository lcbRepository;
    @Autowired
    private LcrzbRepository lcrzbRepository;
    @Autowired
    private LcjdgxbRepository lcjdgxbRepository;
    @Autowired
    private LcjdbRepository lcjdbRepository;
    @Autowired
    private RoleJdgxbGxbRepository roleJdgxbGxbRepository;

    @Transactional
    public String save(TravelingVo travelingVo, String[] attachmentId){
        Traveling traveling = new Traveling(travelingVo);
        UserVo userVo = userService.getLoginUser();
        User user = userRepository.findOne(userVo.getId());
        traveling.setNccr(user);

        //处理出差申请和附件的对应关系
        if(attachmentId != null && attachmentId.length > 0) {
            List<Attachment> attachmentList = attachmentRepository.findByIdIn(attachmentId);
            if(attachmentList != null && attachmentList.size() > 0)
                traveling.setAttachments(new HashSet<>(attachmentList));
        }else {
            traveling.setAttachments(new HashSet<Attachment>());
        }

        traveling = travelingRepository.save(traveling);
        return traveling.getId();
    }

    /**
     * 新合同的保存
     * @param travelingVo
     * @param bczl  保存种类（提交：“tj”， 保存 ： “bc”）
     * @param attachmentId
     */
    @Transactional
    public boolean  newTravelingSave(TravelingVo travelingVo, String bczl, String[] attachmentId) {

//        UserVo userVo = userService.getLoginUser();
//        String ccfl = getCcfl(userVo);
//        travelingVo.setCcfl(ccfl);

        String lcbs = getLcbs();
        if(lcbs.equals("")){
            return false;
        }
        String startCondition = lcService.getStartCondition(lcbs);
        String condition = startCondition;
        if(StringUtil.equals(bczl, "tj")) {
            condition = lcService.getNextCondition(lcbs, startCondition);
            travelingVo.setSqsj(DateUtil.format(new Date(), DateUtil.fullFormat));
        }
        travelingVo.setLcbs(lcbs);
        travelingVo.setCcshzt(condition);
        String id = save(travelingVo, attachmentId);
        travelingVo.setId(id);

        if(StringUtil.equals(bczl, "tj")) {
            addXzLcrz(id, "cc", lcService.getStartCondition(lcbs));//如果是新增就增加一条日志记录
        }
        return true;
    }

    //出差申请时，根据申请人的角色寻找到相匹配的流程
    public String getLcbs(){
        UserVo userVo = userService.getLoginUser();
        User user = userRepository.findOne(userVo.getId());
        Set<Role> roles = user.getRoles();
        String lcbs = "";
        for(Role role : roles){
            Set<Function> functions = roleService.getFunctionsByRoleId(role.getId());
            for(Function function : functions){
                if(function.getName().equals("出差申请")){
                    Lcb lcb = lcbRepository.findByLclbInAndLcfqRoleAndZt("cc", role.getId(), "Y");
                    if(lcb != null)
                        lcbs = lcb.getId();
                    break;
                }
            }
            if(lcbs.length() == 0 || lcbs == null)
                break;
        }
        return lcbs;
    }

    //根据申请人所在部门来确定出差申请的类别
//    public String getCcfl(UserVo userVo){
//        String department = userVo.getDepartmentName();
//        if(department.indexOf("集团") != -1) return "1";
//        if(department.indexOf("东山公交") != -1) return "2";
//        return "3";
//    }

    /**
     * 新增时绑定流程日志
     * @param id
     * @param ccfl
     * @param ccshzt
     */
    @Transactional
    public void addXzLcrz(String id, String ccfl, String ccshzt) {
        LcrzbVo lcrzbVo = new LcrzbVo("新增", "");
        String lcrzId = lcrzbService.save(lcrzbVo, id, ccfl, ccshzt);
        bindTravelingWithLcrz(id, lcrzId);
    }

    public void bindTravelingWithLcrz(String travelingId, String lcrzId) {
        Traveling traveling = travelingRepository.findOne(travelingId);
        Lcrzb lcrz = lcrzbRepository.findOne(lcrzId);
        traveling.getLcrzSet().add(lcrz);
        travelingRepository.save(traveling);
    }



    /**
     * @param userId
     * @param condition
     * @param lx
     * @return
     */
    @Cacheable(cacheName = "TRAVELING_CACHE")
    public List<TravelingVo> getTravelingByUserIdAndCondition(String userId, String condition, String lx) {
        List<Traveling> travelingList = null;

        if(StringUtil.isBlank(condition)) return new ArrayList<>();
        if(StringUtil.equals(lx, "wtj")){//未提交
            travelingList = travelingRepository.findSqjlWtjList(userId, condition);
        }
        if(StringUtil.equals(lx, "dsp") ){//待审批
            travelingList = travelingRepository.findByCcshztOrderBySqsjDesc(condition);
        }
        if(StringUtil.equals(lx, "ysp")){ //已审批（已审批就是根据个人取出的所以不需要再进行过滤）
            return getCcspYspList(userId, condition);
        }
        if(StringUtil.equals(lx, "yth") ){//包括已撤回以及流程停用相关的出差表
//            travelingList = travelingRepository.findByNccr_idAndCcshztOrderBySqsjDesc(userId, REVOCATION_CONDITION);
//            travelingList.addAll(travelingRepository.findByNccr_idAndCcshztOrderBySqsjDesc(userId, DEACTIVATION_CONDITION));
            travelingList = getYthTravelingList(userId, REVOCATION_CONDITION);
            travelingList.addAll(getYthTravelingList(userId, DEACTIVATION_CONDITION));
        }
        List<TravelingVo> travelingVos = new ArrayList<>();
        if(travelingList == null) return travelingVos;
        for(Traveling traveling : travelingList){
            TravelingVo travelingVo = new TravelingVo(traveling);
            travelingVos.add(travelingVo);
        }
        return travelingVos;
    }


    private List<Traveling> getYthTravelingList(String userId, String condition){
        List<Traveling> travelingList = new ArrayList<>();
        User user = userRepository.getOne(userId);
        Set<Role> roleSet = user.getRoles();
        HashSet<String> roleIdSet = new HashSet<>();
        for(Role role : roleSet)
            roleIdSet.add(role.getId());
        List<RoleJdgxbGxb> roleJdgxbGxblist = roleJdgxbGxbRepository.findByWjzlAndRoleIdIn("cc", roleIdSet);
        if(roleJdgxbGxblist == null)
            return travelingList;
        for(RoleJdgxbGxb roleJdgxbGxb : roleJdgxbGxblist){
            String lcjd = roleJdgxbGxb.getJdId();

            Lcjdgxb lcjdgxb = lcjdgxbRepository.findByJsjd(lcjd);
            String preLcjd = lcjdgxb.getKsjd();
            String lcId = lcjdgxb.getLcbs();
            List<Traveling> allLcbTraveling = travelingRepository.findByLcbsAndCcshztOrderBySqsjDesc(lcId, condition);
            if(preLcjd.equals("S")){
                travelingList.addAll(allLcbTraveling);
                continue;
            }
            for(Traveling traveling : allLcbTraveling){
                Set<Lcrzb> lcrz = traveling.getLcrzSet();
                for(Lcrzb lcrzb : lcrz){
                    if(StringUtil.equals(lcrzb.getDyjd(), preLcjd)){
                        travelingList.add(traveling);
                        break;
                    }
                }

            }
        }
        return travelingList;


    }

    /**
     * 通过condition（审核状态=condition的说明是当前用户待审批的出差表，将这种出差表从已审批的出差列表中去除）
     * @param userId
     * @return
     */
    public List<TravelingVo> getCcspYspList(String userId, String condition) {
        List<TravelingVo> travelingVoList = new ArrayList<>();
        List<Traveling> travelingList = travelingRepository.findYspTraveling(userId, condition);
        Map<String, String> lcjdbMap = getAllLcjdMapping();
        if(travelingList != null && !travelingList.isEmpty()){
            for (Traveling traveling : travelingList) {
                if(condition != null && traveling.getCcshzt().equals(condition)) continue; //过滤掉待审批状态的出差表
                Set<Lcrzb> lcrzbList = traveling.getLcrzSet();

                TravelingVo travelingVo = new TravelingVo(traveling);
                setSpjg(userId, lcrzbList, travelingVo);
                convertCcshzt(lcjdbMap, travelingVo);
                travelingVoList.add(travelingVo);
            }
        }
//		sortContractVoListBySqsjDesc(contractVoList);
        return travelingVoList;
    }

    /**
     * 设置某人已审批出差表的审批意见（某人对出差表的最后一次的审批结果）
     * @param userId
     * @param lcrzbList
     * @param travelingVo
     */
    private void setSpjg(String userId, Set<Lcrzb> lcrzbList, TravelingVo travelingVo) {
        List<Lcrzb> lcrzbListOfUser = new ArrayList<>();
        if(lcrzbList != null && !lcrzbList.isEmpty()){
            for (Lcrzb lcrzb : lcrzbList) {
                if(StringUtil.equals(lcrzb.getUser().getId(), userId))
                    lcrzbListOfUser.add(lcrzb);
            }
        }
        if(!lcrzbListOfUser.isEmpty()){
            Collections.sort(lcrzbListOfUser, new Comparator<Lcrzb>() {
                @Override
                public int compare(Lcrzb c1, Lcrzb c2) {
                    if(c1.getClsj() == null) return -1;
                    if(c2.getClsj() == null) return 1;
                    long dif = DateUtil.getDiffSeconds(c1.getClsj(), c2.getClsj());
                    return (dif > 0) ? -1 :
                            ((dif < 0) ? 1 : 0);
                }
            });
            Lcrzb lastLcrz = lcrzbListOfUser.get(0);
            travelingVo.setSpyj(lastLcrz.getCljg());
        }else travelingVo.setSpyj("");
    }

    /**
     * 审核状态由代码转换为对应汉字
     * @param lcjdbMap
     * @param travelingVo
     */
    private void convertCcshzt(Map<String, String> lcjdbMap, TravelingVo travelingVo) {
        if(!StringUtil.isBlank(travelingVo.getCcshzt())) {
            if (lcjdbMap.get(travelingVo.getCcshzt()).equals("审核完成")) {
                String id = travelingVo.getId();
                LcrzbVo lcrzbVo= lcrzbService.getLastLcrzByClnrid(id);
                if(lcrzbVo !=null){
                    if(lcrzbVo.getCljg().equals("通过")){
                        travelingVo.setCcshzt("通过");
                    }
                    else if(lcrzbVo.getCljg().equals("不通过")){
                        travelingVo.setCcshzt("终止");
                    }
                }
            }
            else {
                travelingVo.setCcshzt(lcjdbMap.get(travelingVo.getCcshzt()));
            }
        }
        else travelingVo.setCcshzt("");
    }

    /**
     * 获取所有节点id和名字的map
     * @return
     */
    public Map<String, String> getAllLcjdMapping() {
        Map<String, String> resultMap = new HashMap<String, String>();
        List<Lcjdb> lcjdbs = lcjdbRepository.findAll();
        for (Lcjdb lcjdb:lcjdbs){
            resultMap.put(lcjdb.getId(),lcjdb.getName());
        }
        resultMap.put("Y", "审核完成");
        resultMap.put(REVOCATION_CONDITION, "已撤销");
        resultMap.put(DEACTIVATION_CONDITION,"流程停用");
        return resultMap;
    }

    /**
     * 申请记录界面未审批出差申请表
     * 1.没有完成 状态不是Y
     * 2.没有撤销 状态不是revocation
     * @param userId
     * @return
     */
    @Cacheable(cacheName = "CONTRACT_CACHE")
    public List<TravelingVo> getSqjlWspList(String userId) {
        List<TravelingVo> travelingVoList = new ArrayList<>();

 //       List<Traveling> wspList = travelingRepository.findSqjlWspList(userId, condition);//过滤掉状态是Y的出差表
        List<Traveling> wspList = travelingRepository.findByNccr_idOrderBySqsjDesc(userId);
        if(wspList != null && wspList.size() > 0)
            for (Traveling traveling : wspList) {
                if(StringUtil.equals("Y", traveling.getCcshzt())) continue;//过滤掉状态是Y的出差表
                if(StringUtil.equals(REVOCATION_CONDITION, traveling.getCcshzt())) continue;//过滤掉已撤销的出差表
                if(StringUtil.equals(DEACTIVATION_CONDITION, traveling.getCcshzt())) continue;//过滤掉已停用的出差表
                if(isWtjTraveling(traveling)) continue; //过滤掉未提交的出差表
                TravelingVo travelingVo = new TravelingVo(traveling);
                travelingVo.setCcshzt("待审批");
                travelingVoList.add(travelingVo);
            }
        return travelingVoList;
    }

    //判断该条出差表状态是否是未提交状态
    public boolean isWtjTraveling(Traveling traveling){
        String condition = traveling.getCcshzt();
        String lcbs = traveling.getLcbs();
        String wtjCondition = lcjdgxbRepository.findByLcbsAndKsjd(lcbs, "S").getJsjd();
        if(condition.equals(wtjCondition) || condition.equals("S"))
            return true;
        else
            return false;
    }



    /**
     * 申请记录界面已审批出差申请
     * @param userId
     * @return
     */
    @Cacheable(cacheName = "CONTRACT_CACHE")
    public List<TravelingVo> getSqjlYspList(String userId){
        List<TravelingVo> travelingVoList = new ArrayList<TravelingVo>();
        List<Traveling> yspList = travelingRepository.findByNccr_idAndCcshztOrderBySqsjDesc(userId, "Y");
        if(yspList != null){
            for (Traveling traveling : yspList) {
                String id = traveling.getId();
                TravelingVo travelingVo = new TravelingVo(traveling);
                List<LcrzbVo> lcrzbVoList = LcrzbService.convert(traveling.getLcrzSet());
                String cljg = lcrzbVoList.get(lcrzbVoList.size()-1).getCljg();
                if(cljg.equals("不通过")){
                    travelingVo.setCcshzt("终止");
                }
                else{
                    travelingVo.setCcshzt("通过");
                }
                travelingVoList.add(travelingVo);
            }
        }
        return travelingVoList;
    }

    /**
     * 1.根据id找到相关出差表，并将数据写入到对用Vo中
     * 2.获取相关的流程日志
     * 3.设置个流程日志相关联的出差表状态的名称
     * 4.如果出差表的状态不是已撤销（REVOCATION_CONDITION）就加载出差表的附件信息，否则就不加载
     * @param travelingId
     * @return
     */
    public TravelingVo getTravelingById(String travelingId){
        Traveling traveling = travelingRepository.findOne(travelingId);
        if(traveling == null) throw new BaseAppException("id为"+travelingId+"的出差表没有找到");
        TravelingVo travelingVo = new TravelingVo(traveling);
        List<LcrzbVo> lcrzbVoList = LcrzbService.convert(traveling.getLcrzSet());
        Map<String, String> lcjdbMap = getAllLcjdMapping();
        for(LcrzbVo lcrzbVo : lcrzbVoList){
            if(!StringUtil.isBlank(lcrzbVo.getLcjd())){
                String lcjdName = lcjdbMap.get(lcrzbVo.getLcjd());
                lcrzbVo.setLcjd(lcjdName);
            }
        }
        travelingVo.setLcrzList(lcrzbVoList);

        //如果出差表的状态不是已撤销就显示出差表的附件信息，否则就不显示附件信息
        if(!StringUtil.equals(traveling.getCcshzt(), REVOCATION_CONDITION)){
            List<AttachmentVo> attachmentVoList = getTravelingAttachment(traveling);
            travelingVo.setAttachmentList(attachmentVoList);
        }
        return travelingVo;
    }

    /**
     * 获取出差表的相关附件信息
     * @param traveling
     * @return
     */
    private List<AttachmentVo> getTravelingAttachment(Traveling traveling){
        Set<Attachment> attachmentList = traveling.getAttachments();
        List<AttachmentVo> attachmentVoList = new ArrayList<>();
        if(attachmentList != null && !attachmentList.isEmpty())
            for (Attachment attachment : attachmentList) {
                AttachmentVo attachementVo = new AttachmentVo();
                BeanUtils.copyProperties(attachment, attachementVo);
                attachmentVoList.add(attachementVo);
            }
        return attachmentVoList;
    }

    @Transactional
    public void updateTraveling(TravelingVo travelingVo, String[] attachmentId) {
        if(travelingVo == null || StringUtil.isBlank(travelingVo.getId()))
            throw new BaseAppException("要更新的出差表ID为空，无法更新");
        Traveling traveling = travelingRepository.findOne(travelingVo.getId());
        if(traveling == null) throw new BaseAppException("要更新的出差表不存在！");
        BeanUtils.copyProperties(travelingVo, traveling);
        //更新出差表的附件列表
        if(attachmentId != null && attachmentId.length > 0) {
            List<Attachment> attachmentList = attachmentRepository.findByIdIn(attachmentId);
            if(attachmentList != null && attachmentList.size() > 0)
                traveling.setAttachments(new HashSet<>(attachmentList));
        }
        travelingRepository.save(traveling);
    }

    /**
     * 从数据库中删除合同，用于未提交以保存的出差表草稿
     * @param id
     */
    @Transactional
    public void delete(String id) {
        Traveling traveling = travelingRepository.findById(id);
        if(traveling == null) return;
        travelingRepository.delete(traveling);
    }

    /**
     * 撤销出差表
     * 出差申请人有权利撤销出差表 撤销后的出差表只能查看基本属性，不能查看和下载已上传的附件
     * 1.在数据库中查找相应id的出差表（未找到抛出异常，指示出差表没有找到）
     * 2.在流程日志表中插入一条出差表撤销的记录
     * 3.修改出差表的状态为已撤销，并保存
     * @param travelingId 要撤销的出差表的id
     * @param reason （1.提交人发现有误  2.其他（理由自填））
     */
    @Transactional
    @TriggersRemove(cacheName = { "TRAVELING_CACHE" }, removeAll = true)
    public void revocationTraveling(String travelingId, String reason) {
        if(StringUtil.isBlank(travelingId)) throw new BaseAppException("要撤销的出差表id为空");
        Traveling traveling = travelingRepository.getOne(travelingId);
        if(traveling == null) throw new BaseAppException("没有找到要撤销的出差表！");

        //插入撤销出差表的记录
        String lcrzId = lcrzbService.save(new LcrzbVo("撤销", reason), travelingId, "", REVOCATION_CONDITION);
        bindTravelingWithLcrz(travelingId, lcrzId);
        //记录与出差表绑定
        traveling.setCcshzt(REVOCATION_CONDITION);
        //保存出差表
        travelingRepository.save(traveling);
    }

    /**
     * 出差表审批
     * @param cljg
     * @param message
     * @param clnrid
     * @param clnrfl
     */
    @Transactional
    @TriggersRemove(cacheName = { "TRAVELING_CACHE" }, removeAll = true)
    public void saveTravelingLcrz(String cljg, String message, String clnrid, String clnrfl) {
        UserVo userVo = userService.getLoginUser();
        String roleName = userVo.getRoleName();
        TravelingVo travelingVo = getTravelingById(clnrid);
        String nextCondition;//合同的下一个状态，根据是否被退回确定
        String curCondition = travelingVo.getCcshzt();
        //添加流程日志
        String lcrzId="";
        //修改出差表状态
        if(StringUtil.equals(cljg, "不通过")){
            nextCondition = "Y";
            lcrzId = lcrzbService.save(new LcrzbVo(cljg, message), clnrid, clnrfl, curCondition);

        } else{
            nextCondition = lcService.getNextCondition(travelingVo.getLcbs(), curCondition);
            lcrzId = lcrzbService.save(new LcrzbVo(cljg, message), clnrid, clnrfl, curCondition);
        }

        bindTravelingWithLcrz(clnrid, lcrzId);

        if(!StringUtil.isBlank(nextCondition)){
            travelingVo.setCcshzt(nextCondition);
//			contractVo.setSfth(sfth);
            updateTraveling(travelingVo, null);
        }
    }

    public void closeTraveling(String lcid){
        List<Traveling> travelingList = travelingRepository.findAllByLcbs(lcid);
        String startCondition = lcService.getStartCondition(lcid);
        for(Traveling traveling : travelingList){
            String condition = traveling.getCcshzt();
            if(condition.equals("Y") || condition.equals("revocation") || condition.equals("deactivation"))
                continue;
            String newCondition;
            if(condition.equals(startCondition) || condition.equals("S")){
                newCondition = "S";
            }else {
                newCondition = "deactivation";

                //插入停用出差表的记录
                String lcrzId = lcrzbService.save(new LcrzbVo("流程停用"), traveling.getId(), "", DEACTIVATION_CONDITION);
                bindTravelingWithLcrz(traveling.getId(), lcrzId);
            }
            traveling.setCcshzt(newCondition);
            travelingRepository.save(traveling);
        }
    }


}
