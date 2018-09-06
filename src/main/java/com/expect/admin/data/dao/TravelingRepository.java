package com.expect.admin.data.dao;


import com.expect.admin.data.dataobject.Traveling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelingRepository extends JpaRepository<Traveling, String> {
    public Traveling findById(String id);

    @Query("select t from Traveling t where t.nccr.id = ?1 and t.ccshzt = ?2 or t.ccshzt = 'S' order by t.sqsj desc")
    public List<Traveling> findSqjlWtjList(String userId, String condition);

    public List<Traveling> findByNccr_idAndCcshztOrderBySqsjDesc(String userId, String condition);

    public List<Traveling> findByCcshztOrderBySqsjDesc(String condition);

    public List<Traveling> findByNccr_idOrderBySqsjDesc(String userId);

    public List<Traveling> findAllByLcbs(String lcid);

    List<Traveling> findByLcbsAndCcshztOrderBySqsjDesc(String lcbs, String conditon);

    @Query("select t from Traveling t where t.nccr.id = ?1 and t.ccshzt <> 'Y' order by t.sqsj desc")
    public List<Traveling> findSqjlWspList(String userId, String condition);

    /**
     * 某用户已审批的记录
     * @param userId
     * @return
     */
    @Query("select distinct t from Traveling t, Lcrzb l where t.id = l.clnrid and l.user.id = ?1 and t.ccshzt <> ?2 and t.ccshzt <> 'deactivation' and t.ccshzt <> 'revocation' order by t.sqsj desc")
    List<Traveling> findYspTraveling(String userId, String condition);


}
