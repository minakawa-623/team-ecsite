package jp.co.internous.kabuki.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.kabuki.model.session.LoginSession;
import jp.co.internous.kabuki.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.kabuki.model.domain.MstDestination;
import jp.co.internous.kabuki.model.mapper.MstDestinationMapper;
import jp.co.internous.kabuki.model.mapper.TblCartMapper;

@Controller
@RequestMapping("/kabuki/settlement")
public class SettlementController {
	
	@Autowired
	private LoginSession loginSession;
	
	private Gson gson = new Gson();
	
	@Autowired
	private TblPurchaseHistoryMapper tblPurchaseHistoryMapper;
	
	@Autowired
	private MstDestinationMapper mstDestinationMapper;
	
	@Autowired
	private TblCartMapper tblCartMapper;
	
	@RequestMapping("/")
	public String settlement(Model m) { 
		List<MstDestination> destinationList = mstDestinationMapper.findByUserId(loginSession.getUserId());
		m.addAttribute("destinationList", destinationList);
		m.addAttribute("loginSession",loginSession);
		
		return "settlement";
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/complete")
	public boolean complete(@RequestBody String destinationId) {
		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		String id = map.get("destinationId");
		long userId = loginSession.getUserId();
		
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("destinationId", id);
		parameter.put("userId", userId);
		long insertCount = tblPurchaseHistoryMapper.insert(parameter);
		
		long deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = tblCartMapper.deleteByUserId(userId);
		}
		return deleteCount == insertCount;
	}	
}