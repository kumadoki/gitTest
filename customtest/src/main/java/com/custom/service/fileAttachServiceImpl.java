package com.custom.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.custom.dao.fileAttachDAO;
import com.custom.vo.FileAttachVO;

@Service
public class fileAttachServiceImpl implements fileAttachService {
	@Autowired
	private fileAttachDAO fileattachDAO;

	@Override
	public int insert(FileAttachVO fileAttachVO) {
		return fileattachDAO.insert(fileAttachVO);
	}

	@Override
	public int delete(String uuid) {
		return fileattachDAO.delete(uuid);
	}

	@Override
	public List<FileAttachVO> fileList() {
		return fileattachDAO.fileList();
	}

	@Override
	public String fileExistCheck(String uuid) {
		// TODO Auto-generated method stub
		return fileattachDAO.fileExistCheck(uuid);
	}
}
