package com.custom.service;

import java.util.List;

import com.custom.vo.FileAttachVO;

public interface fileAttachService {
	public int insert(FileAttachVO fileAttachVO);

	public int delete(String uuid);

	public List<FileAttachVO> fileList();

	public String fileExistCheck(String uuid);

}
