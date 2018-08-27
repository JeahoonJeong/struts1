package com.boardTest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.MyUtil;
import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class BoardAction extends DispatchAction{

	//execute 메소드는 무조건 실행된다.
	
	public ActionForward created(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		String mode = req.getParameter("mode");
		
		if(mode==null){//추가
			req.setAttribute("mode", "save");
		}else{//수정
			//수정화면
			CommonDAO dao = CommonDAOImpl.getInstance();
			
			int num = Integer.parseInt(req.getParameter("num"));
			String pageNum = req.getParameter("pageNum");
			
			String searchValue = req.getParameter("searchValue");
			String searchKey = req.getParameter("searchKey");
			
			BoardForm dto = (BoardForm)dao.getReadData("boardTest.readData", num);
			
			if(dto==null){
				return mapping.findForward("list");
			}
			
			
			req.setAttribute("dto", dto);
			req.setAttribute("mode", "updateOK");
			req.setAttribute("pageNum", pageNum);
			
		}
		
		return mapping.findForward("created");
	}
	
	public ActionForward created_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		BoardForm f = (BoardForm)form;
		
		String mode = req.getParameter("mode");
		
		if(mode=="save"){ //입력
			
			int maxNum = dao.getIntValue("boardTest.maxNum");
			
			f.setNum(maxNum+1);
			f.setIpAddr(req.getRemoteAddr());
			
			dao.insertData("boardTest.insertData", f);
		}else{// 수정(updateOK)
			
			String pageNum = req.getParameter("pageNum");
			
			dao.updateData("boardTest.updateData", f);
			
			//원래페이지로 나가기
			
			HttpSession session = req.getSession();
			session.setAttribute("pageNum", pageNum);//session 말고 다른방법은 없나?
			
		}
		
		dao=null;//왜?
		
		return mapping.findForward("created_ok");
	}
	
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		String cp = req.getContextPath();
		MyUtil myUtil = new MyUtil();
		
		int numPerPage = 5;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = req.getParameter("pageNum");
		int currentPage = 1;
				
		//세션에서 pageNum 받기
		
		HttpSession session = req.getSession();
		
		if(pageNum==null){
			pageNum = (String)session.getAttribute("pageNum");//created에서 넘어온 pageNum
		}
		session.removeAttribute("pageNum");
		
		if(pageNum!=null){
			currentPage = Integer.parseInt(pageNum);
		}
		
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		if(searchKey==null){
			searchKey = "subject";
			searchValue = "";
		}
		
		if(req.getMethod().equalsIgnoreCase("GET")){
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("searchKey", searchKey);
		hMap.put("searchValue", searchValue);
		
		totalDataCount = dao.getIntValue("boardTest.dataCount", hMap);
		
		if(totalDataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		}
		
		if(currentPage>totalPage){
			currentPage=totalPage;
		}
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		List<Object> lists = dao.getListData("boardTest.listData", hMap);
		
		String param = "";
		String urlArticle = "";
		String urlList = "";
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue="+searchValue;
		}
		
		urlList = cp+"/boardTest.do?method=list"+param;
		urlArticle = cp+"/boardTest.do?method=article&pageNum="+currentPage+param;
		
		req.setAttribute("lists", lists);
		req.setAttribute("urlArticle", urlArticle);
		req.setAttribute("urlList", urlList);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("totalDataCount", totalDataCount);
		
		
		return mapping.findForward("list");
	}
	
	public ActionForward article(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		if(searchKey==null){
			searchKey = "subject";
			searchValue = "";
		}
		
		if(req.getMethod().equalsIgnoreCase("GET")){
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}
		
		dao.updateData("boardTest.hitCountUpdate", num);
		
		BoardForm dto = (BoardForm)dao.getReadData("boardTest.readData",num);
		
		if(dto==null){
			return mapping.findForward("list");
		}
		
		int lineSu = dto.getContent().split("\n").length;
		
		dto.setContent(dto.getContent().replaceAll("\r\n", "<br/>"));
		
		//이전글 다음글
		String preUrl = "";
		String nextUrl = "";
		
		String cp = req.getContextPath();
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("searchKey", searchKey);
		hMap.put("searchValue", searchValue);
		hMap.put("num", num);
		
		String preSubject = "";
		BoardForm preDTO = (BoardForm)dao.getReadData("boardTest.preReadData", hMap);
		
		if(preDTO!=null){
			preUrl = cp+"/boardTest.do?method=article&pageNum="+pageNum;
			preUrl += "&num="+preDTO.getNum();
			preSubject = preDTO.getSubject();
		}
		
		String nextSubject = "";
		BoardForm nextDTO = (BoardForm)dao.getReadData("boardTest.nextReadData", hMap);
		
		if(nextDTO!=null){
			nextUrl = cp+"/boardTest.do?method=article&pageNum="+pageNum;
			nextUrl += "&num="+nextDTO.getNum();
			nextSubject = nextDTO.getSubject();
		}
		
		String urlList = cp + "/boardTest.do?method=list&pageNum="+pageNum;
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			urlList += "&searchKey="+searchKey;
			urlList += "&searchValue="+searchValue;
		
			if(!preUrl.equals("")){
				preUrl += "&searchKey="+searchKey+"&searchValue="+searchValue;
			}
			
			if(!nextUrl.equals("")){
				nextUrl += "&searchKey="+searchKey+"&searchValue="+searchValue;
			}
			
		}
		
		//수정과 삭제에서 사용할 변수
		String paramArticle = "num="+num+"&pageNum="+pageNum;
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			paramArticle += "&searchKey="+searchKey;
			paramArticle += "&searchValue="+searchValue;
		}
		
		
		req.setAttribute("dto", dto);
		req.setAttribute("preSubject", preSubject);
		req.setAttribute("preUrl", preUrl);
		req.setAttribute("nextSubject", nextSubject);
		req.setAttribute("nextUrl", nextUrl);
		req.setAttribute("lineSu", lineSu);
		req.setAttribute("paramArticle", paramArticle);
		req.setAttribute("urlList", urlList);
		
		
		return mapping.findForward("article");
	}
	
	public ActionForward deleted(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		
		dao.deleteData("boardTest.deleteData", num);
		
		HttpSession session = req.getSession();
		
		session.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("deleted");
	}
	

	
	
}

























