package com.expect.admin.factory.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.expect.admin.data.dao.UserRepository;
import com.expect.admin.data.dataobject.Attachment;
import com.expect.admin.data.dataobject.User;
import com.expect.admin.service.UserService;
import com.expect.admin.service.convertor.UserConvertor;
import com.expect.admin.service.vo.AttachmentVo;
import com.expect.admin.service.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expect.admin.factory.WordXmlFactory;
import com.expect.admin.service.ContractService;
import com.expect.admin.service.vo.ContractVo;
import com.expect.admin.service.vo.LcrzbVo;
import com.expect.admin.utils.StringUtil;
import com.expect.admin.utils.WordXmlUtil;

import freemarker.template.TemplateException;
import sun.misc.BASE64Encoder;

@Service("jthtFactory")
public class JthtFactory implements WordXmlFactory {

	private final String TEMPLATE_NAME = "jtht3.ftl";
	@Autowired
	private ContractService contractService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;


	
	@Override
	public byte[] create(String wjid) throws IOException, TemplateException {
		ContractVo contractVo = contractService.getContractById(wjid);
		if(contractVo == null) return null;
		List<LcrzbVo> lcrzbVoList = contractVo.getLcrzList(); 
		Map<String,Object> dataMap = new HashMap<>();
		
		setJbxx(contractVo, dataMap);//设置基本信息
		setLcxx(lcrzbVoList, dataMap);//设置流程信息
		
		byte[] content =  WordXmlUtil.create(dataMap, TEMPLATE_NAME) ;
		return content;
	}

	/**
	 * 设置显示流程信息
	 * @param lcrzbVoList
	 * @param dataMap
	 */
	private void setLcxx(List<LcrzbVo> lcrzbVoList, Map<String, Object> dataMap) {
		if(lcrzbVoList == null || lcrzbVoList.isEmpty()) return;

		List<Map<String, Object>> approveList = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < lcrzbVoList.size(); i++){
			if(i == 0){
				String imagePath = getImageStrByLcrzbVo(lcrzbVoList.get(i));
				if (!imagePath.equals("签名附件没有上传")){
					dataMap.put("nhtrImage",imagePath);
				}
				continue;
			}
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
			approveList.add(map);
		}
		dataMap.put("approveList", approveList);
	}

	/**
	 * 设置基本信息
	 * @param contractVo
	 * @param dataMap
	 */
	private void setJbxx(ContractVo contractVo, Map<String, Object> dataMap) {
		dataMap.put("sbd", contractVo.getSbd());
		dataMap.put("htbt", contractVo.getHtbt());
		dataMap.put("bh", contractVo.getBh());
		dataMap.put("htnr", contractVo.getHtnr());
		dataMap.put("nhtr", contractVo.getUserName());
		dataMap.put("nqdrq", contractVo.getNqdrq());
		dataMap.put("qx", contractVo.getQx());
	}

	@Override
	public String getFileName(String wjid) {
		ContractVo contractVo = contractService.getContractById(wjid);
		return contractVo.getHtbt() + contractVo.getBh()+"号.doc";
	}

	private String getImageStrByLcrzbVo(LcrzbVo lcrzbVo) {
		User user =lcrzbVo.getUser();
		return getImageStrByUser(user);

	}
//	private String getImageStrByContractVo(ContractVo contractVo){
//		User user =contractVo.getNhtr();
//		return getImageStrByUser(user);
//	}
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
