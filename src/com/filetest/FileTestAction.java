package com.filetest;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.FileManager;
import com.util.MyUtil;
import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class FileTestAction extends DispatchAction{
	
	
	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		return mapping.findForward("write");
		
	}
	
	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		HttpSession session = req.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		
		String savePath = root + File.separator +"pds"+ File.separator+"saveFile";
		
		FileTestForm f = (FileTestForm)form;
		
		//파일 업로드
		String newFileName = FileManager.doFileUpload(f.getUpload(), savePath);
		
		//DB에 저장
		if(newFileName!=null){
			int maxNum = dao.getIntValue("fileTest.maxNum");
			f.setNum(maxNum+1);
			f.setSaveFileName(newFileName);
			f.setOriginalFileName(f.getUpload().getFileName());
			//f.getUpload().getFileSize() //파일크기
			
			dao.insertData("fileTest.insertData", f);
		}
		
		return mapping.findForward("write_ok");
		
	}
	
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		MyUtil myUtil = new MyUtil();
		
		String cp = req.getContextPath();
		
		int numPerPage = 5;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = req.getParameter("pageNum");
		
		int currentPage = 1;
		if(pageNum!=null && !pageNum.equals("")){
			currentPage = Integer.parseInt(pageNum);
		}
		
		totalDataCount = dao.getIntValue("fileTest.dataCount");
		
		if(totalDataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		}
		
		if(currentPage>totalPage){
			currentPage = totalPage;
		}
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		List<Object> lists = dao.getListData("fileTest.listData", hMap);
		
		Iterator<Object> it = lists.iterator();
		int listNum,n=0;
		String str;
		while(it.hasNext()){
			
			FileTestForm dto = (FileTestForm)it.next();
			
			listNum = totalDataCount - (start+n-1);
			dto.setListNum(listNum);
			n++;
			
			//파일 다운로드 경로
			str = cp + "/fileTest.do?method=download&num="+dto.getNum();
			dto.setUrlFile(str);
				
		}
		
		String urlList = cp + "/fileTest.do?method=list";
		
		req.setAttribute("lists", lists);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("totalDataCount", totalDataCount);
		req.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		
		return mapping.findForward("list");
		
	}
	
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		int num = Integer.parseInt(req.getParameter("num"));
		String saveFileName = req.getParameter("saveFileName");
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		HttpSession session = req.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		
		String path = root + File.separator +"pds"+ File.separator+"saveFile";
		
		FileManager.doFileDelete(saveFileName, path);
		
		dao.deleteData("fileTest.deleteData", num);
		
		
		return mapping.findForward("delete_ok");
		
	}
	
	public ActionForward download(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		//String originalFileName = req.getParameter("originalFileName");
		//String saveFileName = req.getParameter("saveFileName");
		
		//FileTestForm f = (FileTestForm)form;
		//String originalFileName = f.getOriginalFileName();
		//String saveFileName = f.getSaveFileName();
		
		int num = Integer.parseInt(req.getParameter("num"));
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		FileTestForm dto = (FileTestForm)dao.getReadData("fileTest.selectData", num);
		
		HttpSession session = req.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		
		String path = root + File.separator +"pds"+ File.separator+"saveFile";
		
		FileManager.doFileDownload(dto.getSaveFileName(), dto.getOriginalFileName(), path, resp);
		
		return mapping.findForward(null);
		
	}

}


















