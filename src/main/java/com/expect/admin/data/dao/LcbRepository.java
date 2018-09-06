package com.expect.admin.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expect.admin.data.dataobject.Lcb;

import java.util.List;

public interface LcbRepository extends JpaRepository<Lcb, String> {

    public Lcb findById(String id);

    public List<Lcb> findAllByLclbAndLcfqRoleAndZt(String lclb, String lcfqRole, String zt);

    public Lcb findByLclbInAndLcfqRoleAndZt(String lclb, String lcfqRole, String zt);

    public List<Lcb> findAllByLclbAndZt(String lclb, String zt);


}
