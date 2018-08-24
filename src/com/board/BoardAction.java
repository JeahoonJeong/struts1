package com.board;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.DBCPConn;
import com.util.MyUtil;

//dispatchaction 메소드단위로 action을 나눈다.
public class BoardAction extends DispatchAction{
	
	
	public ActionForward write(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return mappding.findForward("created");
	}
	
	public ActionForward write_ok(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		Connection conn = DBCPConn.getConnection();
		BoardDAO dao = new BoardDAO(conn);
		
		BoardForm f = (BoardForm)form;
		
		f.setNum(dao.getMaxNum()+1);
		f.setIpAddr(req.getRemoteAddr());
		
		dao.insertData(f);
			
		return mappding.findForward("save");
	}
	
	public ActionForward list(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		Connection conn = DBCPConn.getConnection();
		BoardDAO dao = new BoardDAO(conn);
		
		String cp = req.getContextPath();
		
		MyUtil myUtil = new MyUtil();
		
		int numPerPage = 3;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = req.getParameter("pageNum");
		
		int currentPage = 1;
		if(pageNum != null){
			currentPage = Integer.parseInt(pageNum);
		}
		
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		if(searchKey == null){
			searchKey = "subject";
			searchValue = "";
		}
		
		if(req.getMethod().equalsIgnoreCase("GET")){
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}
		
		totalDataCount = dao.getDataCount(searchKey, searchValue);
		
		if(totalDataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		}
		
		if(currentPage > totalPage){
			currentPage = totalPage;
		}
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		List<BoardForm> lists = dao.getLists(start, end, searchKey, searchValue);
		
		String param = "";
		String urlArticle = "";
		String urlList = "";
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue="+searchValue;
		}
		
		urlList = cp +"/board.do?method=list"+param;
		urlArticle = cp +"/board.do?method=article&pageNum="+currentPage;
		urlArticle += param;
		
		req.setAttribute("lists", lists);
		req.setAttribute("urlArticle", urlArticle);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("totalDataCount", totalDataCount);
		
		return mappding.findForward("list");
	}
	
	
	public ActionForward article(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		Connection conn = DBCPConn.getConnection();
		BoardDAO dao = new BoardDAO(conn);
		
		BoardForm dto =  dao.getReadData(Integer.parseInt(req.getParameter("num")));
		
		String cp = req.getContextPath();
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		String param = "";
		
		if(searchValue != null){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue="+searchValue;
		}
		
		String url;
		url = cp +"/board.do?method=list&pageNum="+req.getParameter("pageNum");
		url += param;
		
		String updateUrl;
		updateUrl = cp +"/board.do?method=update&pageNum="+req.getParameter("pageNum");
		updateUrl += param;
		
		String deleteUrl;
		deleteUrl = cp +"/board.do?method=delete_ok&pageNum="+req.getParameter("pageNum");
		deleteUrl += param;
		
		req.setAttribute("updateUrl", updateUrl);
		req.setAttribute("deleteUrl", deleteUrl);
		req.setAttribute("url", url);
		req.setAttribute("pageNum", req.getParameter("pageNum"));
		req.setAttribute("dto", dto);
		
		return mappding.findForward("article");
	}
	
	
	public ActionForward delete_ok(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		Connection conn = DBCPConn.getConnection();
		BoardDAO dao = new BoardDAO(conn);
		
		dao.deleteData(Integer.parseInt(req.getParameter("num")));
		
		String cp = req.getContextPath();
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		String param = "";
		
		if(searchValue != null){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue="+searchValue;
		}
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/board.do?method=list&pageNum="+req.getParameter("pageNum")+param);
		
		return af;
	}
	
	public ActionForward update(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		Connection conn = DBCPConn.getConnection();
		BoardDAO dao = new BoardDAO(conn);
		BoardForm dto = dao.getReadData(Integer.parseInt(req.getParameter("num")));
		
		
		String cp = req.getContextPath();
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		String param = "";
		
		if(searchValue != null){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue="+searchValue;
		}
		
		String url;
		url = cp +"/board.do?method=article&pageNum="+req.getParameter("pageNum");
		url += param;
		
		String okUrl;
		okUrl = cp +"/board.do?method=update_ok&pageNum="+req.getParameter("pageNum");
		okUrl += param;
		
		req.setAttribute("okUrl", okUrl);
		req.setAttribute("url", url);
		req.setAttribute("dto", dto);
		
		return mappding.findForward("update");
	}
	
	
	public ActionForward update_ok(ActionMapping mappding, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		Connection conn = DBCPConn.getConnection();
		BoardDAO dao = new BoardDAO(conn);
		
		BoardForm f = (BoardForm)form;
		dao.updateData(f);
		
		String cp = req.getContextPath();
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		
		String param = "";
		
		if(searchValue != null){
			searchValue = URLEncoder.encode(searchValue, "UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue="+searchValue;
		}
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/board.do?method=list&pageNum="+req.getParameter("pageNum")+param);
		
		return af;
	}
	
}


























