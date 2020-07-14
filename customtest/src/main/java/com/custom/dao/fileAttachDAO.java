package com.custom.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.custom.vo.FileAttachVO;

@Repository("fileAttachDAO")
public interface fileAttachDAO {

	public int insert(FileAttachVO fileAttachVO);

	public int delete(String uuid);

	public List<FileAttachVO> fileList();

	public String fileExistCheck(String uuid);


}
