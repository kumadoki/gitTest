package com.custom.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.custom.vo.FileAttachVO;

@Repository
public class fileAttachDAOImpl implements fileAttachDAO {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	Connection conn = null;
	PreparedStatement pstmt = null;
    ResultSet rs = null;
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521/orcl";
	Boolean connect = false;
	@Override
	public int insert(FileAttachVO fileAttachVO) {
		logger.info("fileListDAOImpl - insert::::");
		int updateCount = 0;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "scott", "tiger"); //자신의 아이디와 비밀번호
			connect = true;

			String query = "INSERT INTO file_attach(uuid, uploadpath, filename, filetype, fno)" +
							"VALUES (?, ?, ?, ?, file_attach_seq.NEXTVAL)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, fileAttachVO.getUuid());
			pstmt.setString(2, fileAttachVO.getUploadPath());
			pstmt.setString(3, fileAttachVO.getFileName());
			pstmt.setString(4, fileAttachVO.getFileType());
			updateCount = pstmt.executeUpdate();
			logger.info("updateCount : {}",updateCount);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
				if (pstmt != null){
					pstmt.close();
				}
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("finally error");
				e.printStackTrace();
			}
		}
		return updateCount;
	}
	@Override
	public int delete(String uuid) {
		logger.info("fileListDAOImpl - delete::::");
		int deleteCount =0;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "scott", "tiger"); //자신의 아이디와 비밀번호
			connect = true;
			String query = "DELETE FROM file_attach WHERE uuid = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, uuid);
			deleteCount = pstmt.executeUpdate();
		} catch (Exception e) {
			connect = false;
			e.printStackTrace();
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
				if (pstmt != null){
					pstmt.close();
				}
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("finally error");
				e.printStackTrace();
			}
		}
		return deleteCount;
	}
	@Override
	public List<FileAttachVO> fileList() {
		logger.info("fileListDAOImpl - fileList::::");
		List<FileAttachVO> list = new ArrayList<FileAttachVO>();
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "scott", "tiger"); //자신의 아이디와 비밀번호
			connect = true;

			String query = "SELECT * FROM file_attach";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			while(rs.next()){
				FileAttachVO fileAttachVO = new FileAttachVO();
				fileAttachVO.setFileName(rs.getString("FILENAME"));
				fileAttachVO.setFileType(rs.getString("FILETYPE"));
				fileAttachVO.setFno(rs.getInt("FNO"));
				fileAttachVO.setUploadPath(rs.getString("UPLOADPATH"));
				fileAttachVO.setUuid(rs.getString("UUID"));
				list.add(fileAttachVO);
			}
		} catch (Exception e) {
			connect = false;
			e.printStackTrace();
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
				if (pstmt != null){
					pstmt.close();
				}
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("finally error");
				e.printStackTrace();
			}
		}
		return list;
	}
	@Override
	public String fileExistCheck(String uuid) {
		logger.info("fileListDAOImpl - fileExistCheck::::");
		String fileName = "";
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "scott", "tiger"); //자신의 아이디와 비밀번호
			connect = true;

			String query = "SELECT filename FROM file_attach where uuid = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, uuid);
			rs = pstmt.executeQuery();
			while(rs.next()){
				fileName = rs.getString("FILENAME");
			}
		} catch (Exception e) {
			connect = false;
			e.printStackTrace();
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
				if (pstmt != null){
					pstmt.close();
				}
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("finally error");
				e.printStackTrace();
			}
		}

		return fileName;
	}
}
