package com.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
	
	private Connection conn;
	
	public BoardDAO(Connection conn){
		this.conn = conn;
	}
	
	
	public int getMaxNum(){
		
		int maxNum = 0;
		
		PreparedStatement pstmt = null;
		String sql= "";
		ResultSet rs = null;
		
		try {
			
			sql = "select nvl(max(NUM),0) from board";
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				
				maxNum = rs.getInt(1);
				
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			// TODO: handle exception
		}
		
		return maxNum;
	}
	
	
	public int insertData(BoardForm dto){
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql = "";
		
		try {
			
			sql = "insert into board (num, name, pwd, email, subject, content, ipaddr, hitcount, created)"
					+ " values(?,?,?,?,?,?,?,0,sysdate)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getPwd());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getSubject());
			pstmt.setString(6, dto.getContent());
			pstmt.setString(7, dto.getIpAddr());
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
			
		} catch (Exception e) {
			try {
				pstmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			System.out.println(e.toString());
			// TODO: handle exception
		}
		return result;
	} 
	
	//��ü������
	public List<BoardForm> getLists(int start, int end, String searchKey,String searchValue){
			
		List<BoardForm> lists = new ArrayList<BoardForm>();
		
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
	
		try {
			
			//searchKey="subject"&searchValue like "%suzi%"
			
			searchValue = "%" + searchValue +"%";
			
			sql = "select * from(select rownum rnum, data.* from(";
			sql += "select num, name, subject, hitcount, to_char(created,'YYYY-MM-DD') created "
					+ "from board "
					+ "where "+searchKey+" like ? "
					+ "order by num desc) data)"
					+ "where rnum>=? and rnum<=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				
				BoardForm dto = new BoardForm();
				
				dto.setNum(rs.getInt("num"));
				dto.setName(rs.getString("name"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitcount"));
				dto.setCreated(rs.getString("created"));
				
				lists.add(dto);
				
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			// TODO: handle exception
		}
		
		return lists;
	}
	
	//��ü �������� ����
	public int getDataCount(String searchKey, String searchValue){
		
		int DataCount = 0;
		
		PreparedStatement pstmt = null;
		String sql= "";
		ResultSet rs = null;
		
		try {
			
			searchValue = "%"+ searchValue+ "%";
			
			sql = "select nvl(count(*),0) from board where "+searchKey+" like ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, searchValue);
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				
				DataCount = rs.getInt(1);// �÷����� �����ü� �����Ƿ�.
				
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			// TODO: handle exception
		}
		
		return DataCount;
	}
	
	//�Ѱ��� ������ �б�
	
	public BoardForm getReadData(int num){
		BoardForm dto = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			
			sql = "select num,name,pwd,email,subject,"
					+ "content,ipaddr,hitcount,created "
					+ "from board where num =?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				
				dto = new BoardForm();
				
				dto.setNum(rs.getInt("num"));
				dto.setName(rs.getString("name"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitcount"));
				dto.setEmail(rs.getString("email"));
				dto.setContent(rs.getString("content"));
				dto.setIpAddr(rs.getString("ipaddr"));
				dto.setPwd(rs.getString("pwd"));
				dto.setCreated(rs.getString("created"));
				
				
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dto;
	}
	
	
	//��ȸ�� ����
	
	public int updateHitCount(int num){
		
		int result = 0;
		
		PreparedStatement pstmt = null;
		
		String sql ="";
		
		try {
			
			sql = "update board set hitcount=hitcount+1 where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		return result;
		
	}
	
	// ����
	
	public int updateData(BoardForm dto){
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql = "";
		
		try {
			
			sql = "update board set name =?, pwd=?, email=?, subject=?, content=? where num =?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getPwd());
			pstmt.setString(3, dto.getEmail());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			pstmt.setInt(6, dto.getNum());
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	// ����
	
	public int deleteData(int num){
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql ="";
		
		try {
			
			sql = "delete from board where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		
		return result;
		
	}
	
	
	
	
	
	
}













































